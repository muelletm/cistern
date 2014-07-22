
/*******************************************************************/
/*                                                                 */
/*     File: robust.C                                              */
/*   Author: Helmut Schmid                                         */
/*  Purpose:                                                       */
/*  Created: Wed Aug  3 08:49:16 2005                              */
/* Modified: Wed Feb 29 12:35:00 2012 (schmid)                     */
/*                                                                 */
/*******************************************************************/

#include <assert.h>

#include "compact.h"

using std::vector;
using std::set;
using std::pair;
using std::equal_range;

namespace SFST {

  class label_less {
  public:
    bool operator()(const Label l1, const Label l2) const {
      return l1.upper_char() < l2.upper_char();
    }
  };

  // data structure for a search path

  class Path {
  public:
    unsigned int arc_number; // number of the current transducer arc
    unsigned int position;   // number of processed input symbols
    float errors;            // errors accumulated so far
    int previous;            // back-pointer (for printing)
  
    // contructor
    Path( unsigned int n, unsigned int p, float e, unsigned int pp )
    { arc_number = n; position = p; errors = e; previous = (int)pp; };

    // constructor for the intial path
    Path() { arc_number = 0; position = 0; errors = 0; previous = -1; };

    // check whether a path is the intial path
    bool is_start() const { return previous == -1; };
  };


  class ActivePath {
  public: 
    size_t index;
    vector<Path> &path;

    ActivePath( size_t i, vector<Path> &p ) : index(i), path(p) {}

    bool operator<( const ActivePath &a ) const {
      Path &p1 = path[index];
      Path &p2 = path[a.index];
      if (p1.errors < p2.errors)
	return true;
      if (p1.errors > p2.errors)
	return false;
      if (p1.position > p2.position)
	return true;
      if (p1.position < p2.position)
	return false;
      return (index < a.index);
    }
  };

  // search data structure containing all the search paths

  class Agenda {
  private:
    float min_errors;      // smallest number of errors of a complete analysis
                           // or maximal number of errors allowed
    vector<Path> path;     // set of active and inactive paths (for printing)
    set<ActivePath> active_path;    // set of currently active search paths
    vector<size_t> complete_path;  // set of complete search paths

  public:

    Agenda( float e ) : min_errors(e) {
      // initialization
      path.push_back(Path());     // initial search path
      active_path.insert(ActivePath(0, path));   // one active search path
    };

    // get the highest ranked active search path
    Path &best_active_path() { 
      assert(active_path.size() > 0);
      return path[active_path.begin()->index]; 
    };

    // pop the index of the next active path
    size_t pop_active_path_index() { 
      assert(active_path.size() > 0);
      set<ActivePath>::iterator it = active_path.begin();
      size_t n = it->index;
      active_path.erase(it);
      return n;
    };

    // get the highest ranked complete search path
    Path &first_complete_path() { return path[complete_path[0]]; };

    // check whether the analysis is finished
    bool finished() {
      return (active_path.size() == 0 || // no more active paths
	      best_active_path().errors > min_errors);
    };

    // add a new search path
    void add_path( int s, unsigned int pos, float e, int pp, bool final );

    void add_analysis( int sn, CAnalysis &ana );
    void extract_analyses( vector<CAnalysis> &analyses );

    friend class CompactTransducer;
  };


  // trivial error functions for the beginning

  float mismatch_error( Character c, Character c2) { 
    return 1.0;
  };
  float deletion_error( Character c) { return 1.0; };
  float insertion_error( Character c) { return 1.0; };
  float transpose_error( Character c, Character c2) { return 1.0; };




  /*******************************************************************/
  /*                                                                 */
  /*  Agenda::add_path                                               */
  /*                                                                 */
  /*******************************************************************/

  void Agenda::add_path(int arc, unsigned int pos, float e, int pp, bool final)

  {
    // check whether the number of allowed errors is exceeded
    if (e > min_errors)
      return;
  
    // store the new search path
    size_t sn=path.size();              // index of the new search path
    path.push_back(Path(arc, pos, e, pp)); // add the new path

    // sorted insertion of the new active path (reversed order)
    active_path.insert(ActivePath(sn,path));

    if (final) {
      // Is the new analysis better than the previous ones?
      if (complete_path.size() > 0 && first_complete_path().errors > e) {
	complete_path.clear(); // delete all the previous analyses
	min_errors = e;
      }
      complete_path.push_back(sn);
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Agenda::add_analysis                                           */
  /*                                                                 */
  /*******************************************************************/
  
  void Agenda::add_analysis( int sn, CAnalysis &ana )

  {
    Path &cs=path[sn];
    if (!cs.is_start()) {
      add_analysis( cs.previous, ana );
      ana.push_back(cs.arc_number);
    }
  }
  

  /*******************************************************************/
  /*                                                                 */
  /*  Agenda::extract_analyses                                       */
  /*                                                                 */
  /*******************************************************************/
  
  void Agenda::extract_analyses( vector<CAnalysis> &analyses )

  {
    analyses.resize(complete_path.size());
    for( size_t i=0; i<complete_path.size(); i++ )
      add_analysis((int)complete_path[i], analyses[i]);
  }


  /*******************************************************************/
  /*                                                                 */
  /*  CompactTransducer::robust_analyze_string                       */
  /*                                                                 */
  /*******************************************************************/
  
  float CompactTransducer::robust_analyze_string( char *string, 
						  vector<CAnalysis> &analyses,
						  float ErrorsAllowed )
  {
    analyses.clear();
  
    // convert the input string to a sequence of symbols
    vector<Character> input;
    alphabet.string2symseq( string, input );

    // initialize the agenda
    Agenda agenda( ErrorsAllowed );

    // start the analysis
    while (!agenda.finished()) {

      // get the highest ranked search path
      unsigned int sn=(unsigned)agenda.pop_active_path_index();
      const Path cs=agenda.path[sn];

      unsigned int state=cs.is_start()? 0: target_node[cs.arc_number];


      // no more errors allowed
      if (cs.errors == agenda.min_errors) {
	unsigned int i;
	// epsilon transitions
	for( i=first_arc[state]; 
	     i<first_arc[state+1] && label[i].upper_char() == Label::epsilon; 
	     i++)
	  {
	    bool f = (cs.position==input.size() && finalp[target_node[i]]);
	    agenda.add_path(i, cs.position, cs.errors, sn, f);
	  }
	
	// non-epsilon transitions
	// scan the next input symbol
	if (cs.position < input.size()) {
	  // find the set of arcs with matching upper character
	  pair<Label*,Label*> range = 
	    equal_range(label+i, label+first_arc[state+1], 
			Label(input[cs.position]), label_less());
	  unsigned int from = (unsigned int)(range.first - label);
	  unsigned int to   = (unsigned int)(range.second - label);
	  
	  // follow the non-epsilon transitions
	  for( i=from; i<to; i++) {
	    bool f = (cs.position+1==input.size() && finalp[target_node[i]]);
	    agenda.add_path( i, cs.position+1, cs.errors, sn, f);
	  }
	}
      }

      // more errors allowed
      else {

	// for all transitions from the current state
	for( unsigned int i=first_arc[state]; i<first_arc[state+1]; i++ ) {
	  Label l = label[i];               // label of the transition
	  Character tc = l.upper_char();    // surface symbol
	  
	  if (cs.position == input.size()) {
	    if (tc == Label::epsilon)  // epsilon transition
	      agenda.add_path(i, cs.position, cs.errors, sn, 
			      finalp[target_node[i]]);
	    
	    else  // insertion of symbol
	      agenda.add_path(i, cs.position, cs.errors + insertion_error(tc),
			      sn, finalp[target_node[i]]);
	  }
	  
	  else {
	    Character ic = input[cs.position];
	    
	    if (tc == Label::epsilon) // epsilon transition
	      agenda.add_path(i, cs.position, cs.errors, sn, false);
	    else if (tc == ic) { // matching symbols
	      bool f=(cs.position+1==input.size() && finalp[target_node[i]]);
	      agenda.add_path(i, cs.position+1, cs.errors, sn, f);
	    }
	    
	    else {
	      // symbol mismatch
	      bool f=(cs.position+1==input.size() && finalp[target_node[i]]);
	      agenda.add_path(i, cs.position+1, cs.errors+mismatch_error(tc,ic),
			      sn, f);
	      
	      // deletion of symbol
	      f = (cs.position+1==input.size() && 
		   finalp[target_node[cs.arc_number]]);
	      agenda.add_path(cs.arc_number, cs.position+1,
			      cs.errors+deletion_error(ic), cs.previous, f);
	      
	      // insertion of symbol
	      f = (cs.position==input.size() && finalp[target_node[i]]);
	      agenda.add_path(i, cs.position, cs.errors + insertion_error(tc),
			      sn, f);
	    }
	  }
	}
      }
    }

    if (agenda.complete_path.size() == 0)
      return 0.0;
    agenda.extract_analyses( analyses );
    return agenda.first_complete_path().errors;
  }
}
