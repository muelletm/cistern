/*******************************************************************/
/*                                                                 */
/*  FILE     fst.h                                                 */
/*  MODULE   fst                                                   */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*  PURPOSE  finite state tools                                    */
/*                                                                 */
/*******************************************************************/

#ifndef _FST_H_
#define _FST_H_

#include "alphabet.h"

typedef enum { Joint, UpperOnly, LowerOnly, Both } OutputType;


/*******************************************************************/
/* include commands                                                */
/*******************************************************************/

#include <string>
#include <vector>
#include <map>
#include <set>

using std::map;
using std::set;
using std::vector;
using std::istream;
using std::ostream;

#include "mem.h"

namespace SFST {

  // data type for table indices
  typedef unsigned Index;
  static const Index undef = (Index)(-1);

  // data type of the generation counter for transducer traversal
  typedef unsigned short VType;  

  extern int Quiet;

  class Node;
  class Arc;
  class Arcs;
  class Transducer;
  class Node2Int;

  class Transition;

  struct hashf {
    size_t operator()(const Node *n) const { return (size_t) n; }
  };
  typedef hash_set<const Node*, hashf> NodeHashSet;

  /*****************  class Arc  *************************************/

  class Arc {

  private:
    Label l;
    Node *target;
    Arc *next;

  public:
    void init( Label ll, Node *node ) { l=ll; target=node; };
    Label label( void ) const { return l; };
    Node *target_node( void ) { return target; };
    const Node *target_node( void ) const { return target; };

    friend class Arcs;
    friend class ArcsIter;
  };


  /*****************  class Arcs  ************************************/

  class Arcs {

  private:
    Arc *first_arcp;
    Arc *first_epsilon_arcp;

  public:
    void init( void ) { first_arcp = first_epsilon_arcp = NULL; };
    Arcs( void ) { init(); };
    Node *target_node( Label l );
    const Node *target_node( Label l ) const;
    void add_arc( Label, Node*, Transducer* );
    int remove_arc( Arc* );
    bool is_empty( void ) const {
      return !(first_arcp || first_epsilon_arcp);
    };
    bool epsilon_transition_exists( void ) const {
      return first_epsilon_arcp != NULL;
    };
    bool non_epsilon_transition_exists( void ) const {
      return first_arcp != NULL; 
    };
    int size( void ) const;

    friend class ArcsIter;
  };


  /*****************  class ArcsIter  ********************************/

  class ArcsIter {

    // ArcsIter iterates over the arcs starting with epsilon arcs

  private:
    Arc *current_arcp;
    Arc *more_arcs;

  public:
    typedef enum {all,non_eps,eps}  IterType;

    ArcsIter( const Arcs *arcs, IterType type=all ) {
      more_arcs = NULL;
      if (type == all) {
	if (arcs->first_epsilon_arcp) {
	  current_arcp = arcs->first_epsilon_arcp;
	  more_arcs = arcs->first_arcp;
	}
	else
	  current_arcp = arcs->first_arcp;
      }
      else if (type == non_eps)
	current_arcp = arcs->first_arcp;
      else
	current_arcp = arcs->first_epsilon_arcp;
    };
  
    void operator++( int ) {
      if (current_arcp) {
	current_arcp = current_arcp->next;
	if (!current_arcp && more_arcs) {
	  current_arcp = more_arcs;
	  more_arcs = NULL;
	}
      }
    };
    operator Arc*( void ) const { return current_arcp; };
  
  };


  /*****************  class Node  ************************************/

  class Node {

  private:
    Arcs   arcsp;
    Node   *forwardp;
    VType  visited;
    bool   final;

  public:
    Index index;
    Node( void ) { init(); };
    void init( void );
    bool is_final( void ) const { return final; };
    void set_final( bool flag ) { final = flag; };
    void set_forward( Node *node ) { forwardp = node; };
    const Node *target_node( Label l ) const { return arcs()->target_node(l); };
    Node *target_node( Label l ) { return arcs()->target_node(l); };
    void add_arc( Label l, Node *n, Transducer *a ) { arcs()->add_arc(l, n, a); };
    Arcs *arcs( void ) { return &arcsp; };
    const Arcs *arcs( void ) const { return &arcsp; };
    Node *forward( void ) { return forwardp; };
    void clear_visited( NodeHashSet &nodeset );
    bool was_visited( VType vmark ) {
      if (visited == vmark)
	return true;
      visited = vmark;
      return false;
    };
    bool check_visited( VType vm ) // leaves the visited flag unchanged
    { return (visited==vm); };
  };


  /*****************  class PairMapping  ****************************/

  class PairMapping {
    // This class is used to map a node pair from two transducers
    // to a single node in another transducer

    typedef std::pair<Node*, Node*> NodePair;

  private:
    struct hashf {
      size_t operator()(const NodePair p) const { 
	return (size_t)p.first ^ (size_t)p.second;
      }
    };
    struct equalf {
      int operator()(const NodePair p1, const NodePair p2) const {
	return (p1.first==p2.first && p1.second == p2.second);
      }
    };
    typedef hash_map<NodePair, Node*, hashf, equalf> PairMap;
    PairMap pm;
  
  public:
    typedef PairMap::iterator iterator;
    iterator begin( void ) { return pm.begin(); };
    iterator end( void ) { return pm.end(); };
    iterator find( Node *n1, Node *n2 )
    { return pm.find( NodePair(n1,n2) ); };
    Node* &operator[]( NodePair p ) { return pm.operator[](p); };
  
  };


  /*****************  class Transducer  *******************************/

  class Transducer {

  private:
    VType vmark;
    Node root;
    Mem mem;

    size_t node_count;
    size_t transition_count;

    typedef set<Label, Label::label_cmp> LabelSet;
    typedef hash_map<Character, char*> SymbolMap;

    void incr_vmark( void ) {
      if (++vmark == 0) {
	NodeHashSet nodes;
	root.clear_visited( nodes );
	fprintf(stderr,"clearing flags\n");
	vmark = 1;
      }
    };
    void reverse_node( Node *old_node, Transducer *new_node );
    Label recode_label( Label, bool lswitch, bool recode, Alphabet& );
    Node *copy_nodes( Node *n, Transducer *a, 
		      bool lswitch=false, bool recode=false );
    void rec_cat_nodes( Node*, Node* );
    void negate_nodes( Node*, Node* );
    bool compare_nodes( Node *node, Node *node2, Transducer &a2 );
    void map_nodes( Node *node, Node *node2, Transducer *a, Level level );
    void freely_insert_at_node( Node *node, Label l );
    int print_strings_node(Node *node, char *buffer, int pos, FILE *file, bool);
    bool infinitely_ambiguous_node( Node* );
    bool is_cyclic_node( Node*, NodeHashSet &visited );
    bool is_automaton_node( Node* );
    void store_symbols( Node*, SymbolMap&, LabelSet& );

    void splice_nodes(Node*, Node*, Label sl, Transducer*, Transducer*);
    void splice_arc( Node*, Node*, Node*, Transducer* );
    void enumerate_paths_node( Node*, vector<Label>&, NodeHashSet&, 
			       vector<Transducer*>& );
    void replace_char2( Node*, Node*, Character, Character, Transducer* );
    Node *create_node( vector<Node*>&, char*, size_t line );
    void read_transducer_binary( FILE* );
    void read_transducer_text( FILE* );

    void build_TT( Node *node, vector<Transition> &transtab );
    size_t size_node( Node *node );

    void index_nodes( Node*, vector<Node*>* );

  public:
    static bool hopcroft_minimisation;

    bool deterministic;
    bool minimised;
    bool indexed;

    Alphabet alphabet; // The set of all labels, i.e. character pairs

  Transducer( bool empty=false ) : root(), mem() { 
      vmark = 0; 
      deterministic = minimised = empty; 
      indexed = false;
      node_count = transition_count = 0;
    };
    
    Transducer( Transducer&, vector<size_t>&, size_t );

    // convertion of a string to an transducer
    Transducer( char *s, const Alphabet *a=NULL, bool extended=false );
    // reads a word list from a file and stores it in the transducer
    Transducer( istream&, const Alphabet *a=NULL, bool verbose=false, 
		bool lexcomments=false );
    // reads a transducer from a binary or text file
    Transducer( FILE*, bool binary=true );
    // turns a sequence of labels into a transducer
    Transducer( vector<Label>& );

    // HFST additions...
    Transducer &expand( set<char*> &s );
    Node *expand_nodes( Node *node, Transducer *a, set<char*> &s );
    void expand_node( Node *origin, Label &l, Node *target, Transducer *a, set<char*> &s );
    void copy_nodes( Node *search_node, Transducer *copy_tr,
		     Node *start_node,
		     map<int, Node*> &mapper );
    Transducer &remove_epsilons();
    // ...HFST additions end

    Node *root_node( void ) { return &root; };  // returns the root node
    const Node *root_node( void ) const { return &root; };  // returns the root node
    Node *new_node( void );                // memory alocation for a new node
    Arc *new_arc( Label l, Node *target ); // memory alocation for a new arc
    void add_string( char *s, bool extended=false, Alphabet *a=NULL );
    void complete_alphabet( void );
    void minimise_alphabet( void );
    std::pair<size_t,size_t> nodeindexing( vector<Node*> *nodearray=NULL );
 
    int print_strings( FILE*, bool with_brackets=true ); //enumerate all strings

    bool analyze_string( char *s, FILE *file, bool with_brackets=true );
    bool generate_string( char *s, FILE *file, bool with_brackets=true );
    void generate( FILE *file, int max=-1, OutputType ot=Joint );

    void clear( void );      // clears the transducer. The resulting transducer
    // is like one created with Transducer()
    // copy duplicates an transducer
    // if called with a non-zero argument, upper and lower level are switched
    Transducer &copy( bool lswitch=false, const Alphabet *al=NULL );
    Transducer &switch_levels( void ) { return copy( true ); };
    Transducer &splice( Label l, Transducer *a);
    Transducer &freely_insert( Label l );
    Transducer &replace_char( Character c, Character nc );
    Transducer &level( Level );
    Transducer &lower_level( void )   // creates an transducer for the "lower" language
      { return level(lower); };
    Transducer &upper_level( void )   // creates an transducer for the "upper" language
      { return level(upper); };
    Transducer &determinise( bool copy_alphabet=true ); // creates a deterministic transducer
    Transducer &rev_det_minimise( bool verbose ); 
    Transducer &hopcroft_minimise( bool verbose );
    Transducer &minimise( bool verbose=true ) {
      if (hopcroft_minimisation)
	return hopcroft_minimise( verbose );
      return rev_det_minimise( verbose );
    }
    void store( FILE* );       // stores the transducer in binary format
    void store_lowmem( FILE* );
    void read( FILE* );        // reads an transducer in binary format
    bool enumerate_paths( vector<Transducer*>& );

    size_t size();

    void build_transtab( vector<Transition> &transtab );

    Transducer &reverse( bool copy_alphabet=true );  // reverse language
    Transducer &operator|( Transducer& );   // union, disjunction
    Transducer &operator+( Transducer& );   // concatenation
    Transducer &operator/( Transducer& );   // subtraction
    Transducer &operator&( Transducer& );   // intersection, conjunction
    Transducer &operator||( Transducer& );  // composition
    Transducer &operator!( void );          // complement, negation
    Transducer &kleene_star( void );
    bool operator==( Transducer& );         // minimises its arguments first

    bool is_cyclic( void );
    bool is_automaton( void );
    bool is_infinitely_ambiguous( void );
    bool is_empty( void );	      // For efficiency reasons, these functions
    bool generates_empty_string( void );// are better called after minimisation
  
    friend class EdgeCount;
    friend class MakeCompactTransducer;
    friend class Minimiser;
    friend ostream &operator<<(ostream&, Transducer&);
  };
}
#endif
