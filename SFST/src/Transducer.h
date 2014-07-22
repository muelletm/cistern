
/*******************************************************************/
/*                                                                 */
/*     File: Transducer.h                                          */
/*   Author: Helmut Schmid                                         */
/*                                                                 */
/*******************************************************************/

#include <stdio.h>
#include <vector>

namespace SFST {

  class Transition {
  public:
    char lower;
    char upper;
    int  target;

    Transition( char l, char u, size_t t ) { lower = l; upper = u; target = t; };
  };

  class State {
  public:
    bool final;
    vector<Transition> transition;

    State() { final = false; };
  };

  class Transducer {

  private:
    vector<State> state;

    void analyze1( int sn, const char *s, vector<char> &ana, 
		   vector<vector<char> > &analyses )
    {
      if (*s == 0 && state[sn].final)
	analyses.push_back( ana );
    
      vector<Transition> &t=state[sn].transition;
      for( size_t i=0; i<t.size(); i++ ) {
	if (t[i].upper == 0) {
	  ana.push_back(t[i].lower);
	  analyze1( t[i].target, s, ana, analyses);
	  ana.pop_back();
	}
	else if (t[i].upper == *s) {
	  ana.push_back(t[i].lower);
	  analyze1( t[i].target, s+1, ana, analyses);
	  ana.pop_back();
	}
      }
    }

  public:
    Transducer( FILE *file ) {
      char buffer[1000];
      for( unsigned int line=0; (fgets(buffer, 1000, file)); line++ ) {
	int s, t;
	char u, l;
	if (sscanf( buffer, "final: %d", &s) == 1) {
	  if (s >= (int)state.size())
	    state.resize( s+1 );
	  state[s].final = true;
	}
	else if (sscanf( buffer, "%d %c:<> %d", &s, &l, &t) == 3) {
	  if (s >= (int)state.size())
	    state.resize( s+1 );
	  state[s].transition.push_back(Transition(l, 0, t));
	}
	else if (sscanf( buffer, "%d <>:%c %d", &s, &u, &t) == 3) {
	  if (s >= (int)state.size())
	    state.resize( s+1 );
	  state[s].transition.push_back(Transition(0, u, t));
	}
	else if (sscanf( buffer, "%d %c:%c %d", &s, &l, &u, &t) == 4) {
	  if (s >= (int)state.size())
	    state.resize( s+1 );
	  state[s].transition.push_back(Transition(l, u, t));
	}
	else if (sscanf( buffer, "%d %c %d", &s, &l, &t) == 3) {
	  if (s >= (int)state.size())
	    state.resize( s+1 );
	  state[s].transition.push_back(Transition(l, l, t));
	}
	else {
	  fprintf(stderr,"Error: in line %u of transducer file at: %s\n",
		  line, buffer);
	  exit(1);
	}
      }
    }

    void analyze( const char *s, vector<vector<char> > &analyses ) {
      vector<char> ana;
      analyze1( 0, s, ana, analyses );
    }
  };

}
