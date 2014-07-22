/*******************************************************************/
/*                                                                 */
/*  FILE     interface.h                                           */
/*  MODULE   interface                                             */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*******************************************************************/

#ifndef _INTERFACE_H_
#define _INTERFACE_H_

#include "utf8.h"
#include "fst.h"

using std::set;
using std::cerr;

namespace SFST {

  void error( const char *message );
  void error2( const char *message, char *input );

  typedef enum {twol_left,twol_right,twol_both} Twol_Type;
  
  typedef enum {repl_left,repl_right,repl_up,my_repl_down,repl_down} Repl_Type;
  
  typedef struct range_t {
    Character character;
    struct range_t *next;
  } Range;
  
  typedef struct ranges_t {
    Range  *range;
    struct ranges_t *next;
  } Ranges;
  
  
  typedef struct contexts_t {
    Transducer *left, *right;
    struct contexts_t *next;
  } Contexts;


  /*****************  class Interface  *******************************/

  class Interface {

  private:
    struct ltstr {
      bool operator()(const char* s1, const char* s2) const
      { return strcmp(s1, s2) < 0; }
    };
    
    typedef set<char*, ltstr> RVarSet;
    
    typedef map<char*, Transducer*, ltstr> VarMap;
    typedef map<char*, Range*, ltstr> SVarMap;

    Range *copy_values( const Range *r );
    Transducer *empty_transducer();
    Transducer *one_label_transducer( Label l );
    void add_pi_transitions( Transducer *t, Node *node, Alphabet &alph );
    Transducer *pi_machine( Alphabet &alph );
    Transducer *cp( Range *lower_range, Range *upper_range );
    Transducer *anti_cp( Range *lower_range, Range *upper_range );
    Transducer *twol_right_rule( Transducer *lc, Range *lower_range, 
				 Range *upper_range, Transducer *rc);
    Transducer *twol_left_rule( Transducer *lc, Range *lower_range,
				Range *upper_range, Transducer *rc );
    Transducer *restriction_transducer( Transducer *l1, Transducer *l2,
					Character marker );
    Transducer *marker_transducer( Transducer *t, Contexts *c, 
				   Character &marker );
    Transducer *center_transducer( Transducer *t, Transducer *pi, 
				   Transducer *mt );
    Transducer *context_transducer( Transducer *t, Transducer *pi,
				    Transducer *mt, Contexts *c );
    Transducer *constrain_boundary_transducer( Character leftm, Character rm );
    Transducer *extended_left_transducer( Transducer *t, 
					  Character m1, Character m2 );
    Transducer *left_context( Transducer *t, Character m1, Character m2 );
    Transducer *make_optional( Transducer *t, Repl_Type type );
    Transducer *replace_transducer( Transducer *ct, Character lm, 
				    Character rm, Repl_Type type );

    Transducer *result_transducer( Transducer *l1, Transducer *l2,
				   Twol_Type type, Character marker );
    
    VarMap VM;
    SVarMap SVM;
    RVarSet RS;
    RVarSet RSS;

  public:
    bool Verbose;
    bool Alphabet_Defined;
    bool LexiconComments;
    Alphabet TheAlphabet;

  Interface( bool utf8=false, bool verbose=false ) :
    Verbose(verbose), Alphabet_Defined(false), LexiconComments(false)
      {
	TheAlphabet.utf8 = utf8;
      }

    void allow_lexicon_comments() { LexiconComments = true; }

    Transducer *new_transducer( Range*, Range* );
    Transducer *read_words( char *filename );
    Transducer *read_transducer( char *filename );
    Transducer *var_value( char *name );
    Transducer *rvar_value( char *name );
    Range *svar_value( char *name );
    Range *complement_range( Range* );
    Range *rsvar_value( char *name );
    Character character_code( unsigned int uc );
    Character symbol_code( char *s );
    
    bool in_range( unsigned int c, Range *r );
    Transducer *make_transducer( Range *r1, Range *r2 );
    
    Range *add_value( Character, Range*);

    Range *add_var_values( char *name, Range*);
    Range *add_values( unsigned int, unsigned int, Range*);
    Range *append_values( Range *r2, Range *r );
    void add_alphabet( Transducer* );
    
    // These functions delete their argument automata
    
    void def_alphabet( Transducer *a );
    bool def_var( char *name, Transducer *a );
    bool def_rvar( char *name, Transducer *a );
    bool def_svar( char *name, Range *r );
    Transducer *explode( Transducer *a );
    Transducer *catenate( Transducer *a1, Transducer *a2 );
    Transducer *disjunction( Transducer *a1, Transducer *a2 );
    Transducer *conjunction( Transducer *a1, Transducer *a2 );
    Transducer *subtraction( Transducer *a1, Transducer *a2 );
    Transducer *composition( Transducer *a1, Transducer *a2 );
    Transducer *restriction( Transducer *a, Twol_Type type, Contexts *c, int );
    Transducer *replace( Transducer *a, Repl_Type type, bool optional );
    Transducer *replace_in_context( Transducer *a, Repl_Type type, 
				    Contexts *c, bool optional );
    Transducer *negation( Transducer *a );
    Transducer *upper_level( Transducer *a );
    Transducer *lower_level( Transducer *a );
    Transducer *minimise( Transducer *a );
    Transducer *switch_levels( Transducer *a );
    Transducer *repetition( Transducer *a );
    Transducer *repetition2( Transducer *a );
    Transducer *optional( Transducer *a );
    Transducer *make_rule( Transducer *lc, Range *r1, Twol_Type type,
			   Range *r2, Transducer *rc );
    Transducer *freely_insert( Transducer *a, Character lc, Character uc );
    Transducer *make_mapping( Ranges*, Ranges* );
    Ranges *add_range( Range*, Ranges* );
    Contexts *make_context( Transducer *l, Transducer *r );
    Contexts *add_context( Contexts *nc, Contexts *c );
    Transducer *result( Transducer*, bool );
    void write_to_file( Transducer*, char *filename);
  };
}
#endif
