/*******************************************************************/
/*                                                                 */
/*  FILE     make-compact.C                                        */
/*  MODULE   make-compact                                          */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*  PURPOSE  Code needed for generating compact automata           */
/*                                                                 */
/*******************************************************************/

#include <math.h>

#include "make-compact.h"

namespace SFST {

  class ARC {
  public:
    int cv;
    Label label;
    unsigned int target_node;
  
    bool operator< ( const ARC a ) const {
      return cv < a.cv;
    };
  };

  typedef map<Label, size_t, Label::label_cmp> LabelNumber;


  /*******************************************************************/
  /*                                                                 */
  /*  MakeCompactTransducer::sort                                    */
  /*                                                                 */
  /*******************************************************************/

  void MakeCompactTransducer::sort( Level level )

  {
    for( unsigned int n=0; n<number_of_nodes; n++) {
      unsigned int from=first_arc[n]; 
      unsigned int to=first_arc[n+1]; 
      int l=to-from; 

      // copy the arcs to a temporary table
      ARC *arc=new ARC[l];
      for( unsigned int i=from; i<to; i++) {
	arc[i-from].cv = (int)label[i].get_char(level);
	// make sure that epsilon arcs are stored at the beginning
	// even if epsilon is not 0
	if (arc[i-from].cv == (int)Label::epsilon)
	  arc[i-from].cv = -1;
	arc[i-from].label = label[i];
	arc[i-from].target_node = target_node[i];
      }

      // sort the table
      std::sort( arc, arc+l );

      // copy the arcs back to the original table
      for( unsigned int i=from; i<to; i++) {
	label[i] = arc[i-from].label;
	target_node[i] = arc[i-from].target_node;
      }

      delete[] arc;
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  MakeCompactTransducer::count_arcs                              */
  /*                                                                 */
  /*******************************************************************/

  void MakeCompactTransducer::count_arcs( Node *node, VType vmark )
  {
    if (!node->was_visited( vmark )) {
      unsigned n = (unsigned)node->index;
      finalp[n] = node->is_final();
      first_arc[n] = 0;
      Arcs *arcs=node->arcs();
      for( ArcsIter p(arcs); p; p++ ) {
	Arc *arc=p;
	first_arc[n]++;
	count_arcs(arc->target_node(), vmark);
      }
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  MakeCompactTransducer::store_arcs                              */
  /*                                                                 */
  /*******************************************************************/

  void MakeCompactTransducer::store_arcs( Node *node, VType vmark )
  {
    if (!node->was_visited( vmark )) {
      unsigned int n=first_arc[node->index];
      Arcs *arcs=node->arcs();
      for( ArcsIter p(arcs); p; p++ ) {
        Arc *arc=p;
	label[n] = arc->label();
	target_node[n++] = (unsigned)arc->target_node()->index;
	store_arcs(arc->target_node(), vmark);
      }
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  MakeCompactTransducer::MakeCompactTransducer                   */
  /*                                                                 */
  /*******************************************************************/

  MakeCompactTransducer::MakeCompactTransducer( Transducer &a, Level l )

  {
    if (a.is_infinitely_ambiguous()) {
      std::cerr << "Error: resulting transducer contains an infinite loop!\n";
      exit(1);
    }

    number_of_nodes = (unsigned)a.nodeindexing().first;
    alphabet.copy(a.alphabet);

    // memory allocation
    finalp = new char[number_of_nodes];
    first_arc = new unsigned int[number_of_nodes+1];

    // count the number of outgoing arcs for each node
    // and store them in first_arc[]
    a.incr_vmark();
    count_arcs( a.root_node(), a.vmark );
    for( int n=number_of_nodes; n>0; n-- )
      first_arc[n] = first_arc[n-1];
    first_arc[0] = 0;
    for( unsigned int n=0; n<number_of_nodes; n++ )
      first_arc[n+1] += first_arc[n];
    number_of_arcs = first_arc[number_of_nodes];

    // memory allocation
    label = new Label[number_of_arcs];
    target_node = new unsigned int[number_of_arcs];

    // store the arcs
    a.incr_vmark();
    store_arcs( a.root_node(), a.vmark );

    // sort the arcs
    sort( l );
  }


  /*******************************************************************/
  /*                                                                 */
  /*  MakeCompactTransducer::store_finalp                            */
  /*                                                                 */
  /*******************************************************************/

  void MakeCompactTransducer::store_finalp( FILE *file )

  {
    int k=0;
    unsigned char n=0;
  
    for( size_t i=0; i<number_of_nodes; i++ ) {
      n = (unsigned char)(n << 1);
      if (finalp[i])
	n |= 1;
      if (++k == 8) {
	fputc(n, file);
	n = 0;
	k = 0;
      }
    }
    if (k > 0) {
      n = (unsigned char)(n << (8-k));
      fputc(n, file);
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  MakeCompactTransducer::store_first_arcs                        */
  /*                                                                 */
  /*  The data is encoded with the minimal number of bits needed.    */
  /*                                                                 */
  /*******************************************************************/

  void MakeCompactTransducer::store_first_arcs( FILE *file )

  {
    int k=0;
    unsigned int n=0;
    // compute number of bits required for storing each item
    int bits=(int)ceil(log(number_of_arcs+1)/log(2));

    for( size_t i=0; i<=number_of_nodes; i++ ) {
      unsigned int m=first_arc[i];
      m <<= (sizeof(n)*8) - bits;
      m >>= k;
      n = n | m;
      k += bits;
      if (k >= (int)sizeof(n)*8) {
	fwrite(&n, sizeof(n), 1, file);
	k -= (int)sizeof(n) * 8;
	n = first_arc[i];
	if (k == 0)
	  n = 0;
	else
	  n = first_arc[i] << (sizeof(n) * 8 - k);
      }
    }
    if (k > 0)
      fwrite(&n, sizeof(n), 1, file);
  }


  /*******************************************************************/
  /*                                                                 */
  /*  MakeCompactTransducer::store_target_nodes                      */
  /*                                                                 */
  /*******************************************************************/

  void MakeCompactTransducer::store_target_nodes( FILE *file )

  {
    int k=0;
    unsigned int n=0;
    int bits=(int)ceil(log(number_of_nodes)/log(2));

    for( size_t i=0; i<number_of_arcs; i++ ) {
      unsigned int m=target_node[i];
      m <<= (sizeof(n)*8) - bits;
      m >>= k;
      n = n | m;
      k += bits;
      if (k >= (int)sizeof(n)*8) {
	fwrite(&n, sizeof(n), 1, file);
	k -= (int)sizeof(n)*8;
	if (k == 0)
	  n = 0;
	else
	  n = target_node[i] << (sizeof(n) * 8 - k);
      }
    }
    if (k > 0)
      fwrite(&n, sizeof(n), 1, file);
  }


  /*******************************************************************/
  /*                                                                 */
  /*  MakeCompactTransducer::store_labels                            */
  /*                                                                 */
  /*******************************************************************/

  void MakeCompactTransducer::store_labels( FILE *file )

  {
    size_t N=0;
    LabelNumber LNum;
    for( Alphabet::const_iterator it=alphabet.begin();
	 it != alphabet.end(); it++ )
      {
	Label l=*it;
	LNum[l] = N++;
      }

    int k=0;
    unsigned int n=0;
    int bits=(int)ceil(log((double)alphabet.size())/log(2));

    for( size_t i=0; i<number_of_arcs; i++ ) {
      unsigned int l = (unsigned)LNum[label[i]];
      unsigned int m=l;
      m <<= (sizeof(n)*8) - bits;
      m >>= k;
      n = n | m;
      k += bits;
      if (k >= (int)sizeof(n)*8) {
	fwrite(&n, sizeof(n), 1, file);
	k -= (int)sizeof(n)*8;
	if (k == 0)
	  n = 0;
	else
	  n = l << (sizeof(n) * 8 - k);
      }
    }
    if (k > 0)
      fwrite(&n, sizeof(n), 1, file);
  }


  /*******************************************************************/
  /*                                                                 */
  /*  MakeCompactTransducer::store                                   */
  /*                                                                 */
  /*******************************************************************/

  void MakeCompactTransducer::store( FILE *file )

  {
    fputc('c',file);
    alphabet.store(file);
    fwrite(&number_of_nodes, sizeof(number_of_nodes), 1, file);
    fwrite(&number_of_arcs, sizeof(number_of_arcs), 1, file);
    store_finalp(file);
    store_first_arcs(file);
    store_labels(file);
    store_target_nodes(file);
    if (ferror(file))
      throw "Error encountered while writing transducer to file\n";
  }

}
