  
/*******************************************************************/
/*                                                                 */
/*  FILE     hopcroft.C                                            */
/*  MODULE   hopcroft                                              */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*******************************************************************/

#include "fst.h"

// HFST
namespace SFST
{

  
  /*****************  class Minimiser  *****************************/
  
  class Minimiser {
    
    
    /*****************  class Transition  **************************/
    
    class Transition {
      
    public:
      Index source;
      Index next_for_target;
      Index next_for_label;
      Label label;

      Transition( Index s, Label l, Index n ) {
	source = s; 
	label = l; 
	next_for_target = n; 
	next_for_label = undef; 
      }
    };
    
    
    /*****************  class State  *******************************/
    
    class State {
      
    public:
      Index group;             // index of group to which this state belongs
      Index next_in_group;     // index of next state in group
      Index previous_in_group; // index of previous state in group
      Index first_transition;  // index of first transition with this
                               // state as target

      State() { 
	group = next_in_group = previous_in_group = undef;
	first_transition = undef; 
      }
    };

   
    /*****************  class StateGroup  **************************/
    
    class StateGroup {
      
    public:
      Index next;          // index of next source group 
      Index next_in_agenda;
      Index previous_in_agenda;

      Index size;          // number of states in this group
      Index first_state;   // pointer to first state

      Index new_size;    
      Index first_new_state;  // pointer to the set of intersection states

      void init( Index i ) { 
	next_in_agenda = i;
	size = new_size = 0;
	next = first_state = first_new_state = undef;
      }
      bool is_empty() {
	return first_state == undef; 
      }
    };
    
    
    /*****************  class Agenda  *****************************/
    
    class Agenda {
      
      static const Index bucket_count = (Index)(sizeof(Index) * 8);
      // the first "bucket_count" many groups are dummy groups
      // used as the agenda buckets

      vector<StateGroup> &group;

    public:

      Agenda( vector<StateGroup> &g ) : group(g) {
	// allocate some dummy groups for the agenda 
	g.resize(bucket_count);
	for( Index i=0; i<bucket_count; i++ )
	  group[i].next_in_agenda = group[i].previous_in_agenda = i;
      }

      Index pop() {
	for( Index i=0; i<bucket_count; i++ ) {
	  if (group[i].next_in_agenda != i) {
	    Index result = group[i].next_in_agenda;
	    erase( result );
	    return result;
	  }
	}
	return undef;
      }

      void add( Index g, Index size ) {

	// find the bucket
	Index i;
	for( i=0; (size >>= 1); i++ ) ;

	// insert the new group
	Index next = group[i].next_in_agenda;
	group[i].next_in_agenda = g;
	group[g].next_in_agenda = next;
	group[g].previous_in_agenda = i;
	group[next].previous_in_agenda = g;
      }

      void erase( Index g ) {
	// update the pointers
	Index next = group[g].next_in_agenda;
	Index previous = group[g].previous_in_agenda;
	group[previous].next_in_agenda = next;
	group[next].previous_in_agenda = previous;
	
	// unlink the result element
	group[g].previous_in_agenda = group[g].next_in_agenda = g;
      }

      bool contains( Index g ) {
	return (group[g].next_in_agenda != g);
      }

      Index number_of_buckets() { return bucket_count; }
    };


    /***************************************************************/


    Transducer &transducer;    // pointer to original transducer
    size_t number_of_nodes;    // node count in original t.
    size_t number_of_transitions; // transition count in original t.
    vector<Node*> nodearray;   // maps indices to original transducer nodes 

    // CAVEAT: Do not use references to elements of the group vector
    // because they become invalid when the group vector is resized.
    vector<StateGroup> group;
    vector<State>      state;
    vector<Transition> transition;
    Agenda             agenda;

    // data structure for the sets of incoming transitions
    typedef map<Label,Index> Label2TransSet;

    // "first_transition_for_label" maps a label to a list of transitions
    // to (states in) C that are labelled with the respective label
    Label2TransSet first_transition_for_label;

    Index first_source_group;   // linked list of source groups
   
  public:
    Minimiser( Transducer &t );
    Transducer &result();

  private:
    // transform the transducer to the representation needed for minimisation
    void add_transition( Index s, Label l, Index t );
    void link_state_in( Index &first_state, Index s );
    void add_state( Index g, Index s );
    void link_state_out( Index &first_state, Index s );
    void remove_state( Index g, Index s );
    void move_state_to_new( Index g, Index s );
    void merge_state_lists( Index g );

    void compute_source_states( Index g );
    void process_source_groups( Label l );
    void split( Index g, Label l );

    Index first_group() { return agenda.number_of_buckets(); }

    Transducer &build_transducer();

#ifndef NDEBUG
    void print_groups() {
      fputs("--------------\n", stderr);
      for( size_t g=first_group(); g<group.size(); g++ ) {
     	fprintf(stderr,"group %lu: ", (unsigned long)g-first_group());
	if (group[g].first_state != undef) {
	  Index s = group[g].first_state;
	  do {
	    fprintf(stderr,"%lu ", (unsigned long)s);
	    s = state[s].next_in_group;
	  } while (s != group[g].first_state);
	}
	if (group[g].first_new_state != undef) {
	  fputs("| ", stderr);
	  Index s = group[g].first_new_state;
	  do {
	    fprintf(stderr,"%lu ", (unsigned long)s);
	    s = state[s].next_in_group;
	  } while (s != group[g].first_new_state);
	}
	fputc('\n', stderr);
      }
    }
#endif
  };


  /*******************************************************************/
  /*                                                                 */
  /*  Transducer::hopcroft_minimise                                  */
  /*                                                                 */
  /*******************************************************************/

  Transducer &Transducer::hopcroft_minimise( bool verbose )

  {
    if (minimised)
      return copy();

    Transducer *a1 = &reverse( false );
    Transducer *a2 = &a1->reverse( false );
    delete a1;
    a1 = &a2->determinise( false );
    delete a2;

    Transducer *result = &Minimiser( *a1 ).result();
    delete a1;

    result->minimised = true;
    result->alphabet.copy(alphabet);
    result->minimise_alphabet();

    return *result;
  }
  
  
  /*******************************************************************/
  /*                                                                 */
  /*  Minimiser::Minimiser                                           */
  /*                                                                 */
  /*******************************************************************/
  
  Minimiser::Minimiser( Transducer &t )
    : transducer(t), agenda(group)

  {
    std::pair<size_t, size_t> NC_TC = t.nodeindexing( &nodearray );
    number_of_nodes = NC_TC.first;
    number_of_transitions = NC_TC.second;

    state.resize(number_of_nodes);
    transition.reserve(number_of_transitions);

    group.reserve(number_of_nodes+first_group());

    // one group for final and non-final transducers resp.
    Index final = (Index)group.size();
    group.push_back( StateGroup() );
    group.back().init( final );

    Index nonfinal = (Index)group.size();
    group.push_back( StateGroup() );
    group.back().init(nonfinal);

    // build the transition table
    for( Index sourceID=0; sourceID<(Index)nodearray.size(); sourceID++ ) {
      Node *node = nodearray[sourceID];

      if (node->is_final())
	add_state( final, sourceID );
      else
	add_state( nonfinal, sourceID );
      
      for( ArcsIter p(node->arcs()); p; p++ ) {
	Arc *arc=p;
	add_transition( sourceID, arc->label(), arc->target_node()->index );
      }
    }
  }

  
  /*******************************************************************/
  /*                                                                 */
  /*  Minimiser::link_state_in                                       */
  /*                                                                 */
  /*******************************************************************/
  
  void Minimiser::link_state_in( Index &first_state, Index s )

  {
    if (first_state == undef) {
      first_state = s;
      state[s].next_in_group = state[s].previous_in_group = s;
    }
    else {
      Index n = state[first_state].next_in_group;
      state[first_state].next_in_group = s;
      state[s].next_in_group = n;
      state[n].previous_in_group = s;
      state[s].previous_in_group = first_state;
    }
  }

  
  /*******************************************************************/
  /*                                                                 */
  /*  Minimiser::add_state                                           */
  /*                                                                 */
  /*******************************************************************/
  
  void Minimiser::add_state( Index g, Index s )

  {
    group[g].size++;
    state[s].group = g;
    link_state_in( group[g].first_state, s );
  }

  
  /*******************************************************************/
  /*                                                                 */
  /*  Minimiser::link_state_out                                      */
  /*                                                                 */
  /*******************************************************************/
  
  void Minimiser::link_state_out( Index &first_state, Index s )

  {
    State &S = state[s];
    // only state in group ?
    if (S.next_in_group == s)
      first_state = undef;
    else {
      Index p = S.previous_in_group;
      Index n = S.next_in_group;
      state[p].next_in_group = n;
      state[n].previous_in_group = p;
      if (first_state == s)
	first_state = n;
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Minimiser::remove_state                                        */
  /*                                                                 */
  /*******************************************************************/
  
  void Minimiser::remove_state( Index g, Index s )

  {
    group[g].size--;
    link_state_out( group[g].first_state, s );
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Minimiser::move_state_to_new                                   */
  /*                                                                 */
  /*******************************************************************/
  
  void Minimiser::move_state_to_new( Index g, Index s )

  {
    group[g].size--;
    group[g].new_size++;

    link_state_out( group[g].first_state, s );
    link_state_in( group[g].first_new_state, s );
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Minimiser::merge_state_lists                                   */
  /*                                                                 */
  /*******************************************************************/
  
  void Minimiser::merge_state_lists( Index g )

  {
    Index first1 = group[g].first_state;
    if (first1 == undef)
      group[g].first_state = group[g].first_new_state;
    else {
      Index first2 = group[g].first_new_state;
      Index next1 =  state[first1].next_in_group;
      Index next2 =  state[first2].next_in_group;
      state[first1].next_in_group = next2;
      state[first2].next_in_group = next1;
      state[next1].previous_in_group = first2;
      state[next2].previous_in_group = first1;
    }
    group[g].first_new_state = undef;
    group[g].size += group[g].new_size;
    group[g].new_size = 0;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Minimiser::add_transition                                      */
  /*                                                                 */
  /*******************************************************************/
  
  void Minimiser::add_transition( Index s, Label l, Index t )

  {
    Transition T( s, l, state[t].first_transition );
    state[t].first_transition = (Index)transition.size();
    transition.push_back(T);
  }
  
  
  /*******************************************************************/
  /*                                                                 */
  /*  Minimiser::result                                              */
  /*                                                                 */
  /*******************************************************************/

  Transducer &Minimiser::result()

  {
    if (number_of_nodes == 1)
      return transducer.copy();  // no need for a minimisation

    Index final = first_group();
    Index nonfinal = final + 1;
    if (group[final].is_empty())
      // no final transducers
      return *new Transducer( true ); // return an empty transducer

    if (group[nonfinal].is_empty()) {
      // no non-final transducers
      group.pop_back();
      agenda.add(final, group[final].size);
    }
    else {
      agenda.add(final, group[final].size);
      agenda.add(nonfinal, group[nonfinal].size);
    }
    
    Index g;
    while ((g = agenda.pop()) != undef) {
	    
      compute_source_states( g );

      // for all labels appearing on incoming transitions
      for( Label2TransSet::iterator it=first_transition_for_label.begin();
	   it!=first_transition_for_label.end(); it++ )
	{
	  process_source_groups( it->first );
	}
      if (group.size() - first_group() == number_of_nodes)
	break;
    }
    Transducer &t = build_transducer();

    return t;
  }

  
  /*******************************************************************/
  /*                                                                 */
  /*  Minimiser::compute_source_states                               */
  /*                                                                 */
  /*******************************************************************/
  
  void Minimiser::compute_source_states( Index g )

  {
    first_transition_for_label.clear();

    // for all states S in C
    Index first = group[g].first_state;
    Index s = first;
    do {
      State &S = state[s];
      // for all transitions T into S
      for( Index t=S.first_transition; t!=undef; 
	   t=transition[t].next_for_target )
	{
	  Transition &T = transition[t];
	  T.next_for_label = undef;
	  // add the transition to the list of
	  // incoming transitions with the same label
	  Label2TransSet::iterator it=first_transition_for_label.find(T.label);
	  if (it == first_transition_for_label.end())
	    // add a new mapping
	    first_transition_for_label[T.label] = t;
	  else {
	    // prepend the new element to the list
	    T.next_for_label = it->second;
	    it->second = t;
	  }
	}
      s = S.next_in_group;
    }
    while (s != first);
  }
  
  
  /*******************************************************************/
  /*                                                                 */
  /*  Minimiser::process_source_groups                               */
  /*                                                                 */
  /*******************************************************************/
  
  void Minimiser::process_source_groups( Label l )

  {
    first_source_group = undef;

    // for all incoming transitions with label l
    for( Index t = first_transition_for_label[l]; t != undef; 
	 t = transition[t].next_for_label )
      {
	// get the transition, source state, and source state group
	Transition &T = transition[t];
	State &S = state[T.source];
	Index g = S.group;

	// If new, add this group to the list of source groups
	if (group[g].first_new_state == undef) {
	  group[g].next = first_source_group;
	  first_source_group = S.group;
	}
	
	move_state_to_new(g, T.source );
      }

    // for all source groups
    for( Index g = first_source_group; g != undef; g = group[g].next ) {
      if (group[g].size > 0)
	split( g, l );
      else
	merge_state_lists( g );
    }
    return;
  }

  
  
  /*******************************************************************/
  /*                                                                 */
  /*  Minimiser::split                                               */
  /*                                                                 */
  /*******************************************************************/
  
  void Minimiser::split( Index g, Label l )

  {
    // create a new group
    Index newg = (Index)group.size();
    group.push_back( StateGroup() );
    StateGroup &NewG = group.back();
    NewG.init( newg );
    NewG.first_state = group[g].first_new_state;
    NewG.size = group[g].new_size;
    group[g].first_new_state = undef;
    group[g].new_size = 0;
    Index s = NewG.first_state;

    do {
      state[s].group = newg;
      s = state[s].next_in_group;
    }
    while (s != NewG.first_state);

    // update the agenda

    if (agenda.contains( g )) {
      // G was on the agenda
      agenda.erase(g);
      agenda.add(g, group[g].size);
      agenda.add(newg, group[newg].size);
    }
    // Otherwise, put the smaller subgroup on the agenda
    else if (group[g].size < group[newg].size)
      agenda.add(g, group[g].size);
    else
      agenda.add(newg, group[newg].size);

    return;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Minimiser::build_transducer                                    */
  /*                                                                 */
  /*******************************************************************/
  
  Transducer &Minimiser::build_transducer()

  {
    Transducer *t = new Transducer( true );
    t->alphabet.copy(transducer.alphabet);
    

    // create the nodes of the new transducer
    vector<Node*> node(group.size(), NULL);

    // define the root node
    node[state[0].group] = t->root_node();

    for( size_t i=first_group(); i<node.size(); i++ )
      if (node[i] == NULL)
	node[i] = t->new_node();

    // Add the transitions
    for( size_t g=first_group(); g<group.size(); g++ ) {
      Node *old_node = nodearray[group[g].first_state];
      Node *new_node = node[g];
      new_node->set_final( old_node->is_final() );

      for( ArcsIter p(old_node->arcs()); p; p++ ) {
	Arc *arc=p;
	// Compute the ID of the target state
	Index ts = (Index)arc->target_node()->index;
	// Get the node for the corresponding state group
	Node *target = node[state[ts].group];
	// Insert the transition
	new_node->add_arc( arc->label(), target, t );
      }
    }

    return *t;
  }

}
