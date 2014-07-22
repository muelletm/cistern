
/*******************************************************************/
/*                                                                 */
/*  FILE     determinise.C                                         */
/*  MODULE   determinise                                           */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*******************************************************************/


#include "fst.h"

using std::vector;
using std::pair;
using std::set;

namespace SFST {

  /*****************  class NodeSet  *********************************/

  class NodeSet {
    // This class is used to store a set of nodes.
    // Whenever a new node is added, all nodes accessible
    // through epsilon transitions are added as well.

  private:
    set<Node*> ht;
  
  public:
    typedef set<Node*>::iterator iterator;
    NodeSet() {};
    void add( Node* );
    bool insert(Node *node) { 
      pair<iterator, bool> result = ht.insert(node);
      return result.second;
    };
    iterator begin() const { return ht.begin(); }
    iterator end() const { return ht.end(); }
    size_t size() const { return ht.size(); }
    void clear() { ht.clear(); }
  };


  /*****************  class NodeArray  *******************************/

  class NodeArray {

  private:
    size_t sizev;
    bool final;
    Node **node;

  public:
    NodeArray( NodeSet& );
    ~NodeArray() { delete[] node; };
    size_t size() const { return sizev; }
    bool is_final() const { return final; };
    Node* &operator[]( size_t i ) const { return node[i]; }
  };


  /*****************  class DTransition  *****************************/

  class DTransition {
  public:
    Label label;
    NodeArray *nodes;
    DTransition(Label l, NodeArray *na) { label = l; nodes = na; };
  };


  /*****************  class NodeMapping  ****************************/

  class NodeMapping {
    // This class is used to map a node set from one transducer
    // to a single node in another transducer

  private:
    struct hashf {
      size_t operator()(const NodeArray *na) const { 
	size_t key=na->size() ^ na->is_final();
	for( size_t i=0; i<na->size(); i++)
	  key = (key<<1) ^ (size_t)(*na)[i];
	return key;
      }
    };
    struct equalf {
      int operator()(const NodeArray *na1, const NodeArray *na2) const {
	if (na1->size() != na2->size() || na1->is_final() != na2->is_final())
	  return 0;
	for( size_t i=0; i<na1->size(); i++)
	  if ((*na1)[i] != (*na2)[i])
	    return 0;
	return 1;
      }
    };
    typedef hash_map<NodeArray*, Node*, hashf, equalf> NodeMap;
    NodeMap hm;
  
  public:
    typedef NodeMap::iterator iterator;
    ~NodeMapping();
    iterator begin() { return hm.begin(); };
    iterator end() { return hm.end(); };
    iterator find( NodeArray *na) { return hm.find( na ); };
    Node* &operator[]( NodeArray *na ) { return hm.operator[](na); };
  
  };


  /*****************  class Label2NodeSet  ****************************/

  class Label2NodeSet {
    // This class is used to map a label to a node set

  private:
    typedef map<const Label, NodeSet> LabelMap;
    LabelMap lm;
  
  public:
    Label2NodeSet(): lm() {};
    typedef LabelMap::iterator iterator;
    iterator begin() { return lm.begin(); };
    iterator end() { return lm.end(); };
    size_t   size() { return lm.size(); };
    iterator find( Label l) { return lm.find( l ); };
    NodeSet &operator[]( const Label l ) { return lm.operator[]( l ); };
  
  };

  static void determinise_node( NodeArray&, Node*, Transducer*, NodeMapping& );



  /*******************************************************************/
  /*                                                                 */
  /*  NodeSet::add                                                   */
  /*                                                                 */
  /*******************************************************************/

  void NodeSet::add( Node *node )

  {
    pair<iterator, bool> result = ht.insert(node);
    if (result.second) {
      // new node, add nodes reachable with epsilon transitions
      for( ArcsIter p(node->arcs(),ArcsIter::eps); p; p++ ) {
	Arc *arc=p;
	if (!arc->label().is_epsilon())
	  break;
	add(arc->target_node());
      }
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  NodeArray::NodeArray                                           */
  /*                                                                 */
  /*******************************************************************/

  NodeArray::NodeArray( NodeSet &ns )

  {
    sizev = 0;
    NodeSet::iterator it;

    final = false;
    node = new Node*[ns.size()];
    for( it=ns.begin(); it!=ns.end(); it++ ) {
      Node *nn = *it;
      if (nn->arcs()->non_epsilon_transition_exists())
	node[sizev++] = nn;
      if (nn->is_final())
	final = true;
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  NodeMapping::~NodeMapping                                      */
  /*                                                                 */
  /*******************************************************************/

  NodeMapping::~NodeMapping()

  {
    // if we delete NodeArrays without removing them from NodeMapping,
    // the system will crash when NodeMapping is deleted.
    for( iterator it=hm.begin(); it!=hm.end(); ) {
      NodeArray *na=it->first;
      iterator old = it++;
      hm.erase(old);
      delete na;
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  compute_transitions                                            */
  /*                                                                 */
  /*******************************************************************/

  static void compute_transitions( NodeArray &na, vector<DTransition> &t )

  {
    Label2NodeSet lmap;
    
    // for all nodes in the current set
    for( size_t i=0; i<na.size(); i++) {
      Node *n = na[i];    // old node
    
      // For each non-epsilon transition, add the target node
      // to the respective node set.
      for( ArcsIter p(n->arcs(),ArcsIter::non_eps); p; p++ ) {
	Arc *arc=p;
	lmap[arc->label()].add(arc->target_node());
      }
    }
  
    t.reserve(lmap.size());
    for( Label2NodeSet::iterator it=lmap.begin(); it!=lmap.end(); it++ )
      t.push_back(DTransition(it->first, new NodeArray( it->second )));
  }


  /*******************************************************************/
  /*                                                                 */
  /*  determinise_node                                               */
  /*                                                                 */
  /*******************************************************************/

  static void determinise_node( NodeArray &na, Node *node, Transducer *a, 
				NodeMapping &map )
  {
    node->set_final(na.is_final());

    vector<DTransition> t;
    compute_transitions( na, t );

    for( size_t i=0; i<t.size(); i++ ) {
      NodeMapping::iterator it=map.find(t[i].nodes);
      if (it == map.end()) {
	// new node set
	Node *target_node = a->new_node();
	map[t[i].nodes] = target_node;
	node->add_arc( t[i].label, target_node, a );
	determinise_node( *t[i].nodes, target_node, a, map );
      }
      else {
	delete t[i].nodes;
	node->add_arc( t[i].label, it->second, a );
      }
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::determinise                                        */
  /*                                                                 */
  /*******************************************************************/

  Transducer &Transducer::determinise( bool copy_alphabet )

  {
    if (deterministic)
      return copy();

    Transducer *a = new Transducer();
    if (copy_alphabet)
      a->alphabet.copy(alphabet);

    // creation of the initial node set consisting of all nodes
    // reachable from the start node via epsilon transitions.
    NodeArray *na;
    {
      NodeSet ns;
      ns.add(root_node());
      na = new NodeArray(ns);
    }

    // map the node set to the new root node
    NodeMapping map;
    map[na] = a->root_node();

    // determinise the transducer recursively
    determinise_node( *na, a->root_node(), a, map );
    a->deterministic = 1;
    return *a;
  }
}
