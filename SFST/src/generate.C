/*******************************************************************/
/*                                                                 */
/*  FILE     generate.C                                            */
/*  MODULE   generate                                              */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*  PURPOSE  prints automata                                       */
/*                                                                 */
/*******************************************************************/

#include "fst.h"

using std::vector;

namespace SFST {

  /********************  Gen  ***************************************/
  
  class Gen {
  public:
    Node *node;
    Index previous;
    Label label;

    Gen( Node *n, Label l=Label::epsilon , Index p=undef ) 
      : node(n), previous(p), label(l) {}

    void print( vector<Gen> &paths, FILE *file, Alphabet &alphabet, 
		OutputType ot )
    {
      if (previous != undef) {
	paths[previous].print( paths, file, alphabet, ot );
	if (ot == Joint)
	  fputs(alphabet.write_label(label), file);
	else if (ot == UpperOnly) {
	  if (label.upper_char() != Label::epsilon)
	    fputs(alphabet.write_char(label.upper_char()), file);
	}
	else if (ot == LowerOnly) {
	  if (label.lower_char() != Label::epsilon)
	    fputs(alphabet.write_char(label.lower_char()), file);
	}
      }
    }
  };


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::generate                                           */
  /*                                                                 */
  /*******************************************************************/

  void Transducer::generate( FILE *file, int max, OutputType ot )

  {
    vector<Gen> paths;
    paths.push_back(Gen(root_node()));
 
    int n=0;
    for( size_t i=0; i<paths.size(); i++ ) {
      // fprintf(stderr,">>> %lu\n", i);
      Gen &gen = paths[i];
      Node *node = gen.node;
      if (node->is_final()) {
	if (ot == Both) {
	  gen.print( paths, file, alphabet, UpperOnly );
	  fputc('\t',file);
	  gen.print( paths, file, alphabet, LowerOnly );
	}
	else
	  gen.print( paths, file, alphabet, ot );
	fputc('\n',file);
	if (++n == max)
	  return;
      }

      for( ArcsIter p(node->arcs()); p; p++ ) {
	Arc *arc=p;
	paths.push_back( Gen( arc->target_node(), arc->label(), (Index)i ) );
      }
    }
  }
}
