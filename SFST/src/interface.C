/*******************************************************************/
/*                                                                 */
/*  FILE     interface.C                                           */
/*  MODULE   interface                                             */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*******************************************************************/

#include <fstream>
#include <set>

#include "interface.h"

using std::ifstream;
using std::vector;

namespace SFST {
  
  /*******************************************************************/
  /*                                                                 */
  /*  error                                                          */
  /*                                                                 */
  /*******************************************************************/

  void error( const char *message )

  {
    cerr << "\nError: " << message << "\naborted.\n";
    exit(1);
  }


  /*******************************************************************/
  /*                                                                 */
  /*  error2                                                         */
  /*                                                                 */
  /*******************************************************************/

  void error2( const char *message, char *input )

  {
    cerr << "\nError: " << message << ": " << input << "\naborted.\n";
    exit(1);
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::symbol_code                                         */
  /*                                                                 */
  /*******************************************************************/

  Character Interface::symbol_code( char *symbol )

  {
    int c=TheAlphabet.symbol2code(symbol);
    if (c == EOF)
      c = TheAlphabet.add_symbol( symbol );
    free(symbol);
    return (Character)c;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::character_code                                      */
  /*                                                                 */
  /*******************************************************************/

  Character Interface::character_code( unsigned int uc )

  {
    if (TheAlphabet.utf8)
      return symbol_code(fst_strdup(int2utf8(uc)));

    unsigned char *buffer=(unsigned char*)malloc(2);
    buffer[0] = (unsigned char)uc;
    buffer[1] = 0;

    return symbol_code((char*)buffer);
  }
  
  
  /*******************************************************************/
  /*                                                                 */
  /*  Interface::add_value                                           */
  /*                                                                 */
  /*******************************************************************/

  Range *Interface::add_value( Character c, Range *r )

  {
    Range *result=new Range;
    result->character = c;
    result->next = r;
    return result;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::add_values                                          */
  /*                                                                 */
  /*******************************************************************/

  Range *Interface::add_values( unsigned int c1, unsigned int c2, Range *r )

  {
    for( unsigned int c=c2; c>=c1; c-- )
      r = add_value(character_code(c), r);
    return r;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::append_values                                       */
  /*                                                                 */
  /*******************************************************************/

  Range *Interface::append_values( Range *r2, Range *r )

  {
    if (r2 == NULL)
      return r;
    return add_value(r2->character, append_values(r2->next, r));
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::add_var_values                                      */
  /*                                                                 */
  /*******************************************************************/

  Range *Interface::add_var_values( char *name, Range *r )

  {
    return append_values(svar_value(name), r);
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::in_range                                            */
  /*                                                                 */
  /*******************************************************************/

  bool Interface::in_range( unsigned int c, Range *r )

  {
    while (r) {
      if (r->character == c)
	return true;
      r = r->next;
    }
    return false;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  free_values                                                    */
  /*                                                                 */
  /*******************************************************************/

  static void free_values( Range *r )

  {
    if (r) {
      free_values(r->next);
      delete r;
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  free_values                                                    */
  /*                                                                 */
  /*******************************************************************/

  static void free_values( Ranges *r )

  {
    if (r) {
      free_values(r->next);
      delete r;
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  free_contexts                                                  */
  /*                                                                 */
  /*******************************************************************/

  static void free_contexts( Contexts *c )

  {
    if (c) {
      free_contexts(c->next);
      delete c; 
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::copy_values                                         */
  /*                                                                 */
  /*******************************************************************/

  Range *Interface::copy_values( const Range *r )

  {
    if (r == NULL)
      return NULL;
    return add_value( r->character, copy_values(r->next));
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::complement_range                                    */
  /*                                                                 */
  /*******************************************************************/

  Range *Interface::complement_range( Range *r )

  {
    vector<Character> sym;
    for( Range *p=r; p; p=p->next)
      sym.push_back( p->character );
    free_values( r );

    TheAlphabet.complement(sym);
    if (sym.size() == 0)
      error("Empty character range!");
    

    Range *result=NULL;
    for( size_t i=0; i<sym.size(); i++ ) {
      Range *tmp = new Range;
      tmp->character = sym[i];
      tmp->next = result;
      result = tmp;
    }

    return result;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::make_transducer                                     */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::make_transducer( Range *r1, Range *r2 )

  {
    Transducer *t=new Transducer();
    Node *node=t->new_node();
    node->set_final(1);

    if (r1 == NULL || r2 == NULL) {
      if (!Alphabet_Defined)
	error("The wildcard symbol '.' requires the definition of an alphabet");

      // one of the ranges was '.'
      for(Alphabet::const_iterator it=TheAlphabet.begin(); 
	  it!=TheAlphabet.end(); it++)
	if ((r1 == NULL || in_range(it->lower_char(), r1)) &&
	    (r2 == NULL || in_range(it->upper_char(), r2)))
	  t->root_node()->add_arc( *it, node, t );
    }
    else {
      for (;;) {
	Label l(r1->character, r2->character);
	// TheAlphabet.insert(l);
	t->root_node()->add_arc( l, node, t );
	if (!r1->next && !r2->next)
	  break;
	if (r1->next)
	  r1 = r1->next;
	if (r2->next)
	  r2 = r2->next;
      }
    }

    return t;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::empty_transducer                                    */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::empty_transducer()

  {
    Transducer *t=new Transducer();
    t->root_node()->set_final(1);

    return t;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::one_label_transducer                                */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::one_label_transducer( Label l )

  {
    Transducer *t = new Transducer();
    Node *last = t->new_node();
    t->root_node()->add_arc( l, last, t );
    last->set_final(1);

    return t;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::new_transducer                                      */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::new_transducer( Range *r1, Range *r2 )

  {
    Transducer *t=make_transducer( r1, r2);
    if (r1 != r2)
      free_values(r1);
    free_values(r2);
    return t;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::read_words                                          */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::read_words( char *filename )

  {
    if (Verbose)
      fprintf(stderr,"\nreading words from %s...", filename);
    ifstream is(filename);
    if (!is.is_open()) {
      static char message[1000];
      sprintf(message,"Error: Cannot open file \"%s\"!", filename);
      throw message;
    }
    free( filename );
    Transducer *t = new Transducer(is, &TheAlphabet, Verbose, LexiconComments);
    is.close();
    TheAlphabet.insert_symbols(t->alphabet);
    if (Verbose)
      fprintf(stderr,"finished\n");
    return t;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::read_transducer                                     */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::read_transducer( char *filename )

  {
    if (Verbose)
      fprintf(stderr,"\nreading transducer from %s...", filename);
    FILE *file = fopen(filename,"rb");
    if (file == NULL) {
      static char message[1000];
      sprintf(message,"Error: Cannot open file \"%s\"!",filename);
      throw message;
    }
    Transducer t(file);
    fclose(file);
    if (t.alphabet.utf8 != TheAlphabet.utf8) {
      static char message[1000];
      sprintf(message,"Error: incompatible character encoding in file \"%s\"!",
	      filename);
      throw message;
    }
    free( filename );
    Transducer *nt = &t.copy(false, &TheAlphabet);
    TheAlphabet.insert_symbols(nt->alphabet);
    if (Verbose)
      fprintf(stderr,"finished\n");
    return nt;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::def_alphabet                                        */
  /*                                                                 */
  /*******************************************************************/

  void Interface::def_alphabet( Transducer *t )

  {
    t = explode(t);
    t = minimise(t);
    t->alphabet.clear_char_pairs();
    t->complete_alphabet();
    TheAlphabet.clear_char_pairs();
    TheAlphabet.copy(t->alphabet);
    Alphabet_Defined = 1;
    delete t;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::def_svar  definition of a value range variable      */
  /*                                                                 */
  /*******************************************************************/

  bool Interface::def_svar( char *name, Range *r )

  {
    // delete the old value of the variable
    SVarMap::iterator it=SVM.find(name);
    if (it != SVM.end()) {
      char *n=it->first;
      Range *v=it->second;
      SVM.erase(it);
      delete v;
      free(n);
    }
    SVM[name] = r;
    return r == NULL;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::svar_value                                          */
  /*                                                                 */
  /*******************************************************************/

  Range *Interface::svar_value( char *name )

  {
    SVarMap::iterator it=SVM.find(name);
    if (it == SVM.end())
      error2("undefined variable", name);
    free(name);
    return copy_values(it->second);
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::rsvar_value                                         */
  /*                                                                 */
  /*******************************************************************/

  Range *Interface::rsvar_value( char *name )

  {
    if (RSS.find(name) == RSS.end())
      RSS.insert(fst_strdup(name));
    return add_value(symbol_code(name), NULL);
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::def_var  definition of a transducer variable        */
  /*                                                                 */
  /*******************************************************************/

  bool Interface::def_var( char *name, Transducer *t )

  {
    // delete the old value of the variable
    VarMap::iterator it=VM.find(name);
    if (it != VM.end()) {
      char *n=it->first;
      Transducer *v=it->second;
      VM.erase(it);
      delete v;
      free(n);
    }

    t = explode(t);
    t = minimise(t);

    VM[name] = t;
    return t->is_empty();
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::def_rvar                                            */
  /*  definition of an agreement variable for automata               */
  /*                                                                 */
  /*******************************************************************/

  bool Interface::def_rvar( char *name, Transducer *t )

  {
    if (t->is_cyclic())
      error2("cyclic transducer assigned to", name);
    return def_var( name, t );
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::var_value                                           */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::var_value( char *name )

  {
    VarMap::iterator it=VM.find(name);
    if (it == VM.end())
      error2("undefined variable", name);
    free(name);
    return &(it->second->copy());
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::rvar_value                                          */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::rvar_value( char *name )

  {
    if (RS.find(name) == RS.end())
      RS.insert(fst_strdup(name));
    Range *r=add_value(symbol_code(name), NULL);
    return new_transducer(r,r);
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::explode                                             */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::explode( Transducer *t )

  {
    if (RS.size() == 0 && RSS.size() == 0)
      return t;

    t = minimise(t);

    vector<char*> name;
    for( RVarSet::iterator it=RS.begin(); it!=RS.end(); it++)
      name.push_back(*it);
    RS.clear();

    // replace all agreement variables
    for( size_t i=0; i<name.size(); i++ ) {
      Transducer *nt = NULL;
      Label l((Character)TheAlphabet.symbol2code(name[i]));
      Transducer *vt=var_value(name[i]);

      // enumerate all paths of the transducer
      vector<Transducer*> it;
      vt->enumerate_paths(it);
      delete vt;

      // insert each path
      for( size_t i=0; i<it.size(); i++ ) {

	// insertion
	Transducer *t1 = &t->splice(l, it[i]);
	delete it[i];

	if (nt == NULL)
	  nt = t1;
	else
	  nt = disjunction(nt, t1);
      }
      delete t;
      t = nt;
    }

    name.clear();
    for( RVarSet::iterator it=RSS.begin(); it!=RSS.end(); it++)
      name.push_back(*it);
    RSS.clear();

    // replace all agreement variables
    for( size_t i=0; i<name.size(); i++ ) {
      Transducer *nt = NULL;
      Character c=(Character)TheAlphabet.symbol2code(name[i]);
      Range *r=svar_value(name[i]);

      // insert each character
      while (r != NULL) {

	// insertion
	Transducer *t1 = &t->replace_char(c, r->character);

	if (nt == NULL)
	  nt = t1;
	else
	  nt = disjunction(nt, t1);

	Range *next = r->next;
	delete r;
	r = next;
      }
      delete t;
      t = nt;
    }

    return t;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::catenate                                            */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::catenate( Transducer *t1, Transducer *t2 )

  {
    Transducer *t = &(*t1 + *t2);
    delete t1;
    delete t2;
    return t;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::add_range                                           */
  /*                                                                 */
  /*******************************************************************/

  Ranges *Interface::add_range( Range *r, Ranges *l )

  {
    Ranges *result = new Ranges;
    result->range = r;
    result->next = l;
    return result;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::make_mapping                                        */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::make_mapping( Ranges *list1, Ranges *list2 )

  {
    Ranges *l1=list1;
    Ranges *l2=list2;
    Transducer *t=new Transducer();

    Node *node=t->root_node();
    while (l1 && l2) {
      Node *nn=t->new_node();
      for( Range *r1=l1->range; r1; r1=r1->next )
	for( Range *r2=l2->range; r2; r2=r2->next )
	  node->add_arc( Label(r1->character, r2->character), nn, t );
      node = nn;
      l1 = l1->next;
      l2 = l2->next;
    }
    while (l1) {
      Node *nn=t->new_node();
      for( Range *r1=l1->range; r1; r1=r1->next )
	node->add_arc( Label(r1->character, Label::epsilon), nn, t );
      node = nn;
      l1 = l1->next;
    }
    while (l2) {
      Node *nn=t->new_node();
      for( Range *r2=l2->range; r2; r2=r2->next )
	node->add_arc( Label(Label::epsilon, r2->character), nn, t );
      node = nn;
      l2 = l2->next;
    }
    node->set_final(1);

    free_values(list1);
    free_values(list2);
    return t;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::disjunction                                         */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::disjunction( Transducer *t1, Transducer *t2 )

  {
    Transducer *t = &(*t1 | *t2);
    delete t1;
    delete t2;
    return t;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::conjunction                                         */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::conjunction( Transducer *t1, Transducer *t2 )

  {
    if (RS.size() > 0 || RSS.size() > 0)
      cerr << "\nWarning: agreement operation inside of conjunction!\n";
    Transducer *t = &(*t1 & *t2);
    delete t1;
    delete t2;
    return t;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::subtraction                                         */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::subtraction( Transducer *t1, Transducer *t2 )

  {
    if (RS.size() > 0 || RSS.size() > 0)
      cerr << "\nWarning: agreement operation inside of conjunction!\n";
    Transducer *t = &(*t1 / *t2);
    delete t1;
    delete t2;
    return t;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::composition                                         */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::composition( Transducer *t1, Transducer *t2 )

  {
    if (RS.size() > 0 || RSS.size() > 0)
      cerr << "\nWarning: agreement operation inside of composition!\n";
    Transducer *t = &(*t1 || *t2);
    delete t1;
    delete t2;
    return t;
  }

  /*******************************************************************/
  /*                                                                 */
  /*  Interface::freely_insert                                       */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::freely_insert( Transducer *t, 
					Character lc, Character uc )
  {
    return &t->freely_insert(Label(lc,uc));
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::negation                                            */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::negation( Transducer *t )

  {
    if (RS.size() > 0 || RSS.size() > 0)
      cerr << "\nWarning: agreement operation inside of negation!\n";
    if (!Alphabet_Defined)
      error("Negation requires the definition of an alphabet");
    t->alphabet.clear_char_pairs();
    t->alphabet.copy(TheAlphabet);
    Transducer *nt = &(!*t);
    delete t;
    return nt;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::upper_level                                         */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::upper_level( Transducer *t )

  {
    Transducer *nt = &t->upper_level();
    delete t;
    return nt;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::lower_level                                         */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::lower_level( Transducer *t )

  {
    Transducer *nt = &t->lower_level();
    delete t;
    return nt;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::minimise                                            */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::minimise( Transducer *t )

  {
    t->alphabet.copy(TheAlphabet);
    Transducer *nt = &t->minimise( Verbose );
    delete t;
    return nt;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::switch_levels                                       */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::switch_levels( Transducer *t )

  {
    Transducer *nt = &t->switch_levels();
    delete t;
    return nt;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::repetition                                          */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::repetition( Transducer *t )

  {
    Transducer *nt = &(t->kleene_star());
    delete t;
    return nt;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::repetition2                                         */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::repetition2( Transducer *t )

  {
    Transducer *t1 = &(t->kleene_star());
    Transducer *nt = &(*t + *t1);
    delete t;
    delete t1;
    return nt;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::optional                                            */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::optional( Transducer *t )

  {
    Transducer *nt = &(t->copy());
    nt->root_node()->set_final(1);
    delete t;
    return nt;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::add_pi_transitions                                  */
  /*                                                                 */
  /*******************************************************************/

  void Interface::add_pi_transitions( Transducer *t, Node *node, Alphabet &alph)

  {
    for( Alphabet::const_iterator it=alph.begin(); it!=alph.end(); it++)
      node->add_arc( *it, node, t );
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::pi_machine                                          */
  /*                                                                 */
  /*******************************************************************/

 Transducer *Interface::pi_machine( Alphabet &alph )

  {
    Transducer *t=new Transducer();
    t->root_node()->set_final(1);
    add_pi_transitions( t, t->root_node(), alph );
    return t;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::cp                                                  */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::cp( Range *lower_range, Range *upper_range )

  {
    Transducer *t = make_transducer(lower_range, upper_range);
    for( ArcsIter p(t->root_node()->arcs()); p; p++ ) {
      Arc *arc=p;
      if (TheAlphabet.find(arc->label()) == TheAlphabet.end())
	fprintf(stderr,"Warning: 2-level rule mapping \"%s\" not defined in alphabet!\n",
		TheAlphabet.write_label(arc->label()));
    }
    
    return t;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::anti_cp                                             */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::anti_cp( Range *lower_range, Range *upper_range )

  {
    Transducer *cpt = cp(lower_range, upper_range);
    Transducer *t=new Transducer();
    Node *node=t->new_node();

    node->set_final(1);
    for(Alphabet::const_iterator it=TheAlphabet.begin(); 
	it!=TheAlphabet.end(); it++){
      Label l=*it;
      if (in_range(l.lower_char(), lower_range) &&
	  !cpt->root_node()->target_node(l))
	t->root_node()->add_arc( l, node, t );
    }
    if (in_range(Label::epsilon, lower_range) &&
	!cpt->root_node()->target_node(Label()))
      t->root_node()->add_arc( Label(), node, t );

    delete cpt;
    return t;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::twol_right_rule                                     */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::twol_right_rule( Transducer *lc, Range *lower_range, 
					  Range *upper_range, Transducer *rc )
  {
    // Build the rule transducer
    Transducer *cpt = cp(lower_range, upper_range);
    Transducer *pi=pi_machine(TheAlphabet);

    // First unwanted language

    lc->alphabet.copy(TheAlphabet);
    Transducer *notlc = &(!*lc);
    Transducer *tmp = &(*notlc + *cpt);
    delete notlc;
    Transducer *t1 = &(*tmp + *pi);
    delete tmp;

    // Second unwanted language
    rc->alphabet.copy(TheAlphabet);
    Transducer *notrc = &(!*rc);
    tmp = &(*cpt + *notrc);
    delete cpt;
    delete notrc;
    Transducer *t2 = &(*pi + *tmp);
    delete pi;
    delete tmp;

    tmp = &(*t1|*t2); 
    delete t1; 
    delete t2; 

    tmp->alphabet.copy(TheAlphabet);
    t1 = &(!*tmp); 
    delete tmp; 

    return t1;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::twol_left_rule                                      */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::twol_left_rule( Transducer *lc, Range *lower_range,
					 Range *upper_range, Transducer *rc )
  {
    // check for problematic insertion operations like "$L <> <= a $R"
    // where either $L or $R includes the empty string
    if (in_range(Label::epsilon, lower_range)) {
      if (lc->generates_empty_string())
	error("in two level rule: insertion operation with deletable left context!");
      if (rc->generates_empty_string())
	error("in two level rule: insertion operation with deletable right context!");
      cerr << "\nWarning: two level rule used for insertion operation (might produce unexpected results)\n";
    }

    // Build the rule transducer
    Transducer *t1 = anti_cp(lower_range, upper_range);

    // Add the left context;
    Transducer *t2 = &(*lc + *t1);
    delete t1;

    // Add the right context;
    t1 = &(*t2 + *rc);
    delete t2;

    // Form the complement
    t1->alphabet.copy(TheAlphabet);
    t2 = &(!*t1);
    delete t1;

    return t2;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::make_rule                                           */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::make_rule( Transducer *lc, Range *lower_range, 
				    Twol_Type type, Range *upper_range, 
				    Transducer *rc )
  {
    if (RS.size() > 0 || RSS.size() > 0)
      cerr << "\nWarning: agreement operation inside of replacement rule!\n";

    if (!Alphabet_Defined)
      error("Two level rules require the definition of an alphabet");

    // expand the left and the right contexts to their full length
    Transducer *pi=pi_machine(TheAlphabet);

    if (lc == NULL)
      lc = pi_machine(TheAlphabet);
    else {
      Transducer *tmp = &(*pi + *lc);
      delete lc;
      lc = tmp;
    }
    if (rc == NULL)
      rc = pi_machine(TheAlphabet);
    else {
      Transducer *tmp = &(*rc + *pi);
      delete rc;
      rc = tmp;
    }
    delete pi;

    Transducer *result = NULL;

    switch (type) {
    case twol_left:
      result = twol_left_rule(lc, lower_range, upper_range, rc);
      break;
    case twol_right:
      result = twol_right_rule(lc, lower_range, upper_range, rc);
      break;
    case twol_both:
      {
	Transducer *t1 = twol_left_rule(lc, lower_range, upper_range, rc);
	Transducer *t2 = twol_right_rule(lc, lower_range, upper_range, rc);
	result = &(*t1 & *t2);
	delete t1;
	delete t2;
      }
    }
    delete lc;
    delete rc;
    if (lower_range != upper_range)
      free_values(lower_range);
    free_values(upper_range);

    return minimise(result);
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::make_context                                        */
  /*                                                                 */
  /*******************************************************************/

  Contexts *Interface::make_context( Transducer *l, Transducer *r )

  {
    if (l == NULL)
      l = empty_transducer();
    if (r == NULL)
      r = empty_transducer();

    Contexts *c=new Contexts();
    c->left = l;
    c->right = r;
    c->next = NULL;

    return c;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::add_context                                         */
  /*                                                                 */
  /*******************************************************************/

  Contexts *Interface::add_context( Contexts *nc, Contexts *c )

  {
    nc->next = c;
    return nc;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::restriction_transducer                              */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::restriction_transducer( Transducer *l1, Transducer *l2,
						 Character marker )
  {
    l1->alphabet.copy(TheAlphabet);
    Transducer *t1 = &(*l1 / *l2);

    Transducer *t2 = &t1->replace_char(marker, Label::epsilon);
    delete t1;

    t2->alphabet.copy(TheAlphabet);
    t1 = &(!*t2);
    delete t2;

    return t1;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::marker_transducer                                   */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::marker_transducer( Transducer *t, Contexts *c,
					    Character &marker )
  {
    marker = TheAlphabet.new_marker();
    Transducer *result = one_label_transducer( Label(marker) );

    // build the alphabet with a new marker
    result->alphabet.insert_symbols(t->alphabet);
    while (c) {
      result->alphabet.insert_symbols(c->left->alphabet);
      result->alphabet.insert_symbols(c->right->alphabet);
      c = c->next;
    }

    return result;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::center_transducer                                   */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::center_transducer( Transducer *t, Transducer *pi, 
					    Transducer *mt )
  {
    // create the concatenation   pi + mt + *t + mt + pi
    Transducer *t1=&(*pi + *mt);
    Transducer *t2=&(*t1 + *t);
    delete t1;
    t1 = &(*t2 + *mt);
    delete t2;
    t2 = &(*t1 + *pi);
    delete t1;
    return t2;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::context_transducer                                  */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::context_transducer( Transducer *t, Transducer *pi,
					     Transducer *mt, Contexts *c )
  {
    // pi + left[i] + mt + pi + mt + right[i] + pi
  
    Transducer *t1 = &(*mt + *t);
    Transducer *tmp = &(*t1 + *mt);
    delete t1;
    Transducer *result=NULL;

    while (c) {
      t1 = &(*pi + *c->left);
      Transducer *t2 = &(*t1 + *tmp);
      delete t1;
      t1 = &(*t2 + *c->right);
      delete t2;
      t2 = &(*t1 + *pi);
      delete t1;

      if (result) {
	t1 = &(*result | *t2);
	delete t2;
	result = t1;
      }
      else 
	result = t2;

      c = c->next;
    }
    delete tmp;

    return result;
  }



  /*******************************************************************/
  /*                                                                 */
  /*  Interface::result_transducer                                   */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::result_transducer( Transducer *l1, Transducer *l2,
					    Twol_Type type, Character marker )
  {
    Transducer *result=NULL;
    if (type == twol_right)
      result = restriction_transducer( l1, l2, marker );
    else if (type == twol_left)
      result = restriction_transducer( l2, l1, marker );
    else if (type == twol_both) {
      Transducer *t1 = restriction_transducer( l1, l2, marker );
      Transducer *t2 = restriction_transducer( l2, l1, marker );
      result = &(*t1 & *t2);
      delete t1;
      delete t2;
    }

    return result;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::restriction                                         */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::restriction( Transducer *t, Twol_Type type, 
				      Contexts *c, int direction )
  {
    Character marker;
    Transducer *mt=marker_transducer( t, c, marker );
    Transducer *pi=pi_machine(TheAlphabet);
    Transducer *l1=center_transducer( t, pi, mt );

    Transducer *tmp;
    if (direction == 0)
      tmp = pi;
    else if (direction == 1) {
      // compute  _t || .*
      Transducer *t1 = &t->lower_level();
      tmp = &(*t1 || *pi);
      delete t1;
    }
    else {
      // compute  ^t || .*
      Transducer *t1 = &t->upper_level();
      tmp = &(*pi || *t1);
      delete t1;
    }
    delete t;

    Transducer *l2=context_transducer( tmp, pi, mt, c );
    if (tmp != pi)
      delete tmp;
    delete pi;
    delete mt;

    Transducer *result=result_transducer( l1, l2, type, marker );
    delete l1;
    delete l2;

    free_contexts( c );

    return result;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::constrain_boundary_transducer                       */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::constrain_boundary_transducer( Character leftm, 
							Character rightm )
  {
    // create the transducer  (.|<L>|<R>)*

    Transducer *tmp=pi_machine(TheAlphabet);

    // create the transducer  (.|<L>|<R>)* <L><R> (.|<L>|<R>)*
    Node *root = tmp->root_node();
    Node *node = tmp->new_node();
    Node *last = tmp->new_node();

    root->set_final(0);
    last->set_final(1);

    root->add_arc( Label(leftm), node, tmp);
    node->add_arc( Label(rightm), last, tmp);

    add_pi_transitions( tmp, last, TheAlphabet );

    // create the transducer  !((.|<L>|<R>)* <L><R> (.|<L>|<R>)*)
    tmp->alphabet.copy(TheAlphabet);
    Transducer *result = &(!*tmp);
    delete tmp;

    return result;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::extended_left_transducer                            */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::extended_left_transducer( Transducer *t, 
						   Character m1, Character m2 )
  {
    if (t == NULL) // empty context
      return pi_machine(TheAlphabet);

    // Extended left context transducer

    // <R> >> (<L> >> $T$)
    Transducer *tmp=&t->freely_insert( Label(m1) );
    delete t;
    t = &tmp->freely_insert( Label(m2) );
    delete tmp;

    // .* (<R> >> (<L> >> $T$))
    add_pi_transitions( t, t->root_node(), TheAlphabet );

    // !(.*<L>)
    tmp = one_label_transducer(Label(m1));
    add_pi_transitions( tmp, tmp->root_node(), TheAlphabet );
    tmp->alphabet.copy(TheAlphabet);
    Transducer *t2 = &(!*tmp);
    delete tmp;
    
    // .* (<R> >> (<L> >> $T$)) || !(.*<L>)
    tmp = &(*t || *t2);
    delete t;
    delete t2;

    return tmp;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::left_context                                        */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::left_context( Transducer *t, 
				       Character m1, Character m2 )
  {
    // .* (<R> >> (<L> >> $T$)) || !(.*<L>)
    Transducer *ct = extended_left_transducer(t, m1, m2);

    // <R>* <L> .*
    Transducer *mt = one_label_transducer(Label(m1));
    mt->root_node()->add_arc(Label(m2), mt->root_node(), mt );
    add_pi_transitions(mt, mt->root_node()->target_node(Label(m1)),TheAlphabet);

    ct->alphabet.copy(TheAlphabet);
    Transducer *no_ct = &!*ct;

    mt->alphabet.copy(TheAlphabet);
    Transducer *no_mt = &!*mt;

    Transducer *t1 = &(*no_ct + *mt);
    delete no_ct;
    delete mt;

    Transducer *t2 = &(*ct + *no_mt);
    delete ct;
    delete no_mt;

    Transducer *tmp = &(*t1 | *t2);
    delete t1;
    delete t2;

    tmp->alphabet.copy(TheAlphabet);
    t1 = &!*tmp;
    delete tmp;

    return t1;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::make_optional                                       */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::make_optional( Transducer *t, Repl_Type type )

  {
    Transducer *t1;
    if (type == my_repl_down)
      t1 = &t->upper_level();
    else
      t1 = &t->lower_level();

    Transducer *t2 = &(*t | *t1);

    delete t;
    delete t1;

    return t2;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::replace                                             */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::replace( Transducer *ct, Repl_Type type, 
				  bool optional )
  {
    if (optional)
      ct = make_optional(ct, type);

    // compute the no-center transducer
    Transducer *tmp=NULL;

    if (type == repl_up)
      // _ct
      tmp = &ct->lower_level();
    else if (type == my_repl_down)
      // ^ct
      tmp = &ct->upper_level();
    else
      error("Invalid type of replace operator");

    // .* _ct
    add_pi_transitions( tmp, tmp->root_node(), TheAlphabet );

    // .*  _ct .*
    Transducer *t2 = pi_machine(TheAlphabet);
    Transducer *t3 = &(*tmp + *t2);
    delete tmp;
    delete t2;

    // no_ct = !(.*  _ct .*)
    t3->alphabet.copy(TheAlphabet);
    Transducer *no_ct = &(!*t3);
    delete t3;

    // compute the unconditional replacement transducer

    // no-ct ct
    tmp = &(*no_ct + *ct);
    delete ct;

    // (no-ct ct)*
    t2 = &(tmp->kleene_star());
    delete tmp;

    // (no-ct ct)* no-ct
    tmp = &(*t2 + *no_ct);
    delete t2;
    delete no_ct;

    return tmp;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::replace_transducer                                  */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::replace_transducer( Transducer *ct, Character lm, 
					     Character rm, Repl_Type type )
  {
    // insert boundary markers into the center transducer

    // <L> >> (<R> >> $Center$)
    Transducer *tmp = &ct->freely_insert(Label(lm));
    delete ct;
    ct = &tmp->freely_insert(Label(rm));
    delete tmp;
  
    // add surrounding boundary markers to the center transducer

    // <L> (<L> >> (<R> >> $Center$))
    Transducer *t2 = one_label_transducer( Label(lm) );
    tmp = &(*t2 + *ct);
    delete t2;
    delete ct;
      
    // $CenterB$ = <L> (<L> >> (<R> >> $Center$)) <R>
    t2 = one_label_transducer( Label(rm) );
    ct = &(*tmp + *t2);
    delete tmp;
    delete t2;

    return replace(ct, type, false);
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::replace_in_context                                  */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::replace_in_context( Transducer *t, Repl_Type type, 
					     Contexts *c, bool optional )
  {
    if (optional)
      t = make_optional(t, type);

    // The implementation of the replace operators is based on
    // "The Replace Operator" by Lauri Karttunen

    if (!Alphabet_Defined)
      error("The replace operators require the definition of an alphabet");

    if (!c->left->is_automaton() || !c->right->is_automaton())
      error("The replace operators require automata as context expressions! (Do not include any character mappings x:y between the two parentheses of the operator.)");

    // create the marker symbols
    Character leftm = TheAlphabet.new_marker();
    Character rightm = TheAlphabet.new_marker();

    /////////////////////////////////////////////////////////////
    // Create the insert boundaries transducer (.|<>:<L>|<>:<R>)*
    /////////////////////////////////////////////////////////////
  
    Transducer *ibt=pi_machine(TheAlphabet);
    Node *root=ibt->root_node();
    root->add_arc( Label(Label::epsilon, leftm), root, ibt);
    root->add_arc( Label(Label::epsilon, rightm),root, ibt);

    /////////////////////////////////////////////////////////////
    // Create the remove boundaries transducer (.|<L>:<>|<R>:<>)*
    /////////////////////////////////////////////////////////////

    Transducer *rbt=pi_machine(TheAlphabet);
    root = rbt->root_node();
    root->add_arc( Label(leftm, Label::epsilon), root, rbt);
    root->add_arc( Label(rightm,Label::epsilon), root, rbt);

    // Add the markers to the alphabet
    TheAlphabet.insert(Label(leftm));
    TheAlphabet.insert(Label(rightm));

    /////////////////////////////////////////////////////////////
    // Create the constrain boundaries transducer !(.*<L><R>.*)
    /////////////////////////////////////////////////////////////

    Transducer *cbt=constrain_boundary_transducer(leftm, rightm);

    /////////////////////////////////////////////////////////////
    // Create the extended context transducers
    /////////////////////////////////////////////////////////////

    // left context transducer:  .* (<R> >> (<L> >> $T$)) || !(.*<L>)
    Transducer *lct = left_context(c->left, leftm, rightm);

    // right context transducer:  (<R> >> (<L> >> $T$)) .* || !(<R>.*)
    Transducer *tmp = &c->right->reverse();
    delete c->right;
    Transducer *t2 = left_context(tmp, rightm, leftm);
    Transducer *rct = &t2->reverse();
    delete t2;

    /////////////////////////////////////////////////////////////
    // unconditional replace transducer
    /////////////////////////////////////////////////////////////

    Transducer *rt;
    if (type == repl_up || type == repl_right || 
	type == repl_left || type == repl_down)
      rt = replace_transducer( t, leftm, rightm, repl_up );
    else
      rt = replace_transducer( t, leftm, rightm, my_repl_down );

    /////////////////////////////////////////////////////////////
    // build the conditional replacement transducer
    /////////////////////////////////////////////////////////////

    tmp = &(ibt->copy());
    tmp = &(cbt->copy());
    tmp = &(lct->copy());
    tmp = &(rct->copy());
    tmp = &(rt->copy());
    tmp = &(rbt->copy());

    tmp = ibt;
    tmp = &(*ibt || *cbt);
    delete(ibt);
    delete(cbt);

    if (type == repl_up || type == repl_left) {
      t2 = &(*tmp || *lct);
      delete tmp;
      delete lct;
      tmp = t2;
    }
    if (type == repl_up || type == repl_right) {
      t2 = &(*tmp || *rct);
      delete tmp;
      delete rct;
      tmp = t2;
    }

    t2 = &(*tmp || *rt);
    delete tmp;
    delete rt;
    tmp = t2;
    
    if (type == my_repl_down || type == repl_right || type == repl_down) {
      t2 = &(*tmp || *lct);
      delete tmp;
      delete lct;
      tmp = t2;
    }
    if (type == my_repl_down || type == repl_left || type == repl_down) {
      t2 = &(*tmp || *rct);
      delete tmp;
      delete rct;
      tmp = t2;
    }

    t2 = &(*tmp || *rbt);
    delete tmp;
    delete rbt;

    // Remove the markers from the alphabet
    TheAlphabet.delete_markers();

    free_contexts( c );

    return t2;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::add_alphabet                                        */
  /*                                                                 */
  /*******************************************************************/

  void Interface::add_alphabet( Transducer *t )

  {
    t->alphabet.copy(TheAlphabet);
    t->complete_alphabet();
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::write_to_file                                       */
  /*                                                                 */
  /*******************************************************************/

  void Interface::write_to_file( Transducer *t, char *filename)

  {
    FILE *file;
    if ((file = fopen(filename,"wb")) == NULL) {
      fprintf(stderr,"\nError: Cannot open output file \"%s\"\n\n", filename);
      exit(1);
    }
    free( filename );

    t = explode(t);
    add_alphabet(t);
    t = minimise(t);
    t->store(file);
    fclose(file);
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Interface::result                                              */
  /*                                                                 */
  /*******************************************************************/

  Transducer *Interface::result( Transducer *t, bool switch_flag )

  {
    t = explode(t);

    // delete the variable values
    vector<char*> s;
    for( VarMap::iterator it=VM.begin(); it != VM.end(); it++ ) {
      s.push_back(it->first);
      delete it->second;
      it->second = NULL;
    }
    VM.clear();
    for( size_t i=0; i<s.size(); i++ )
      free(s[i]);
    s.clear();

    if (switch_flag)
      t = switch_levels(t);
    add_alphabet(t);
    t = minimise(t);
    return t;
  }

}
