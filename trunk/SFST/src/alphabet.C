
/*******************************************************************/
/*                                                                 */
/*  FILE     alphabet.C                                            */
/*  MODULE   alphabet                                              */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*  PURPOSE  basic FST functions                                   */
/*                                                                 */
/*******************************************************************/

#include <climits>
#include <cstring>

#include "utf8.h"
#include "alphabet.h"

#include <map>
using std::map;

namespace SFST {

  using std::vector;
  using std::ostream;

  const int BUFFER_SIZE=100000;

  char EpsilonString[]="<>";



  /*******************************************************************/
  /*                                                                 */
  /*  Alphabet::add                                                  */
  /*                                                                 */
  /*******************************************************************/

  void Alphabet::add( const char *symbol, Character c )

  {
    char *s = fst_strdup(symbol);
    cm[c] = s;
    sm[s] = c;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Alphabet::Alphabet                                             */
  /*                                                                 */
  /*******************************************************************/

  Alphabet::Alphabet()

  { 
    utf8 = false;
    add(EpsilonString, Label::epsilon);
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Alphabet::clear                                                */
  /*                                                                 */
  /*******************************************************************/

  void Alphabet::clear()

  {
    char **s=new char*[cm.size()];
    ls.clear();
    sm.clear();

    size_t i, n=0;
    for( CharMap::iterator it=cm.begin(); it!=cm.end(); it++ )
      s[n++] = it->second;
    cm.clear();

    for( i=0; i<n; i++ )
      free(s[i]);
    delete[] s;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Alphabet::print                                                */
  /*                                                                 */
  /*******************************************************************/

  void Alphabet::print(void)

  {
    for( CharMap::iterator it=cm.begin(); it!=cm.end(); it++ )
      fprintf(stderr, "%i\t%s\n", it->first, it->second);
    return;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Alphabet::new_marker                                           */
  /*                                                                 */
  /*******************************************************************/

  Character Alphabet::new_marker()

  {
    // find some unused character code
    for(Character i=1; i!=0; i++)
      if (cm.find(i) == cm.end()) {
	// create a unique identifier string
	char symbol[100];
	sprintf(symbol,">%ld<",(long)i);
	add(symbol, i);
	return i;
      }
  
    throw "Error: too many symbols in transducer definition";
  }


  /*******************************************************************/
  /*                                                                 */
  /*  is_marker_symbol                                               */
  /*                                                                 */
  /*******************************************************************/

  static bool is_marker_symbol( const char *s )

  {
    // recogize strings matching the expression ">[0-9]+<"
    if (s != NULL && *s == '>') {
      do { s++; } while (*s >= '0' && *s <= '9');
      if (*s=='<' && *(s+1) == 0 && *(s-1) != '>')
	return true;
    }
    return false;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Alphabet::delete_markers                                       */
  /*                                                                 */
  /*******************************************************************/

  void Alphabet::delete_markers()

  {
    vector<char*> sym;
    vector<Character> code;
    vector<Label> label;

    for( CharMap::const_iterator it=cm.begin(); it!=cm.end(); it++ ) {
      Character c=it->first;
      char *s=it->second;
      if (!is_marker_symbol(s)) {
	sym.push_back(fst_strdup(s));
	code.push_back(c);
      }
    }
    
    for( LabelSet::const_iterator it=begin(); it!=end(); it++ ) {
      Label l=*it;
      if (!is_marker_symbol(code2symbol(l.upper_char())) &&
	  !is_marker_symbol(code2symbol(l.lower_char())))
	label.push_back(l);
    }

    clear();

    for( size_t i=0; i<sym.size(); i++ ) {
      add_symbol(sym[i], code[i]);
      free(sym[i]);
    }
    for( size_t i=0; i<label.size(); i++ )
      insert( label[i] );
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Alphabet::add_symbol                                           */
  /*                                                                 */
  /*******************************************************************/

  Character Alphabet::add_symbol(const char *symbol)

  {
    if (sm.find(symbol) != sm.end())
      return sm[symbol];

    // assign the symbol to some unused character
    for(Character i=1; i!=0; i++)
      if (cm.find(i) == cm.end()) {
	add(symbol, i);
	return i;
      }
  
    throw "Error: too many symbols in transducer definition";
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Alphabet::add_symbol                                           */
  /*                                                                 */
  /*******************************************************************/

  void Alphabet::add_symbol( const char *symbol, Character c )

  {
    // check whether the symbol was previously defined
    int sc=symbol2code(symbol);
    if (sc != EOF) {
      if ((Character)sc == c)
	return;

      if (strlen(symbol) < 60) {
	static char message[100];
	sprintf(message, "Error: reinserting symbol '%s' in alphabet with incompatible character value %u %u", symbol, (unsigned)sc, (unsigned)c);
	throw message;
      }
      else
	throw "reinserting symbol in alphabet with incompatible character value";
    }

    // check whether the character is already in use
    const char *s=code2symbol(c);
    if (s == NULL)
      add(symbol, c);
    else {
      if (strcmp(s, symbol) != 0) {
	static char message[100];
	if (strlen(symbol) < 70)
	  sprintf(message,"Error: defining symbol %s as character %d (previously defined as %s)", symbol, (unsigned)c, s);
	else
	  sprintf(message,"Error: defining a (very long) symbol with previously used character");
	throw message;
      }
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Alphabet::write_char                                           */
  /*                                                                 */
  /*******************************************************************/

  void Alphabet::write_char( Character c, char *buffer, int *pos, 
			     bool with_brackets) const
  {
    const char *s = code2symbol(c);

    // quote colons
    if (strcmp(s,":") == 0) {
      buffer[(*pos)++] = '\\';
      buffer[(*pos)++] = s[0];
    }
    else if (s) {
      int i = 0;
      int l=(int)strlen(s)-1;
      if (!with_brackets && s[i] == '<' && s[l] == '>') { i++; l--; }
      while (i <= l)
	buffer[(*pos)++] = s[i++];
    }
    else {
      unsigned int uc = c;
      if (uc>=32 && uc<256)
	buffer[(*pos)++] = (char)c;
      else {
	sprintf(buffer+(*pos),"\\%u", uc);
	*pos += (int)strlen(buffer+(*pos));
      }
    }
    buffer[*pos] = '\0';
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Alphabet::write_char                                           */
  /*                                                                 */
  /*******************************************************************/

  const char *Alphabet::write_char( Character c, bool with_brackets ) const

  {
    static char buffer[1000];
    int n=0;

    write_char( c, buffer, &n, with_brackets );
    return buffer;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Alphabet::write_label                                          */
  /*                                                                 */
  /*******************************************************************/

  void Alphabet::write_label( Label l, char *buffer, int *pos, 
			      bool with_brackets ) const
  {
    Character lc=l.lower_char();
    Character uc=l.upper_char();
    write_char( lc, buffer, pos, with_brackets );
    if (lc != uc) {
      buffer[(*pos)++] = ':';
      write_char( uc, buffer, pos, with_brackets );
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Alphabet::write_label                                          */
  /*                                                                 */
  /*******************************************************************/

  const char *Alphabet::write_label( Label l, bool with_brackets  ) const

  {
    static char buffer[1000];
    int n=0;
    write_label( l, buffer, &n, with_brackets );
    return buffer;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Alphabet::insert_symbols                                       */
  /*                                                                 */
  /*******************************************************************/

  void Alphabet::insert_symbols( const Alphabet &a )

  {
    for( CharMap::const_iterator it=a.cm.begin(); it!=a.cm.end(); it++ )
      add_symbol(it->second, it->first);
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Alphabet::complement                                           */
  /*                                                                 */
  /*******************************************************************/

  void Alphabet::complement( vector<Character> &sym )

  {
    vector<Character> result;
    for( CharMap::const_iterator it=cm.begin(); it!=cm.end(); it++ ) {
      Character c = it->first;
      if (c != Label::epsilon) {
	size_t i;
	for( i=0; i<sym.size(); i++ )
	  if (sym[i] == c)
	    break;
	if (i == sym.size())
	  result.push_back(c);
      }
    }
    sym.swap(result);
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Alphabet::copy                                                 */
  /*                                                                 */
  /*******************************************************************/

  void Alphabet::copy( const Alphabet &a )

  {
    utf8 = a.utf8;
    sm.resize(a.sm.size());
    cm.resize(a.sm.size());
    insert_symbols( a );
    for( LabelSet::const_iterator it=a.begin(); it!=a.end(); it++ )
      ls.insert( *it );
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Alphabet::compose                                              */
  /*                                                                 */
  /*******************************************************************/

  void Alphabet::compose( const Alphabet &la, const Alphabet &ua )

  {
    // insert the symbols
    insert_symbols(la);
    insert_symbols(ua);
    utf8 = la.utf8;

    map<Character, set<Character> > cs;

    // create a table for a quick lookup of the target characters
    for( iterator it=ua.begin(); it!=ua.end(); it++ ) {
      Character lc=it->lower_char();
      if (lc == Label::epsilon)
	insert(*it);
      else
	cs[lc].insert(it->upper_char());
    }

    for( iterator it=la.begin(); it!=la.end(); it++ ) {
      Character uc=it->upper_char();
      if (uc == Label::epsilon)
	insert(*it);
      else {
	if (cs.find(uc) != cs.end()) {
	  set<Character> s=cs[uc];
	  Character lc=it->lower_char();
	  for( set<Character>::iterator it=s.begin(); it!=s.end(); it++)
	    insert(Label(lc, *it));
	}
      }
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  operator<<(Alphabet)                                           */
  /*                                                                 */
  /*******************************************************************/

  ostream &operator<<( ostream &s, const Alphabet &a )

  {
    for( Alphabet::CharMap::const_iterator it=a.cm.begin(); it!=a.cm.end(); it++ )
      s << it->first << " -> " << it->second << "\n";
    for( Alphabet::iterator it=a.begin(); it!=a.end(); it++ )
      s << a.write_label(*it) << " ";
    s << "\n";
    return s;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Alphabet::next_mcsym                                           */
  /*                                                                 */
  /*  recognizes multi-character symbols which are enclosed with     */
  /*  angle brackets <...>. If the argument flag insert is false,    */
  /*  the multi-character symbol must already be in the lexicon in   */
  /*  order to be recognized.                                        */
  /*                                                                 */
  /*******************************************************************/

  int Alphabet::next_mcsym( char* &string, bool insert )

  {
    char *start=string;

    if (*start == '<')
      // symbol might start here
      for( char *end=start+1; *end; end++ )
	if (*end == '>') {
	  // matching pair of angle brackets found
	  // mark the end of the substring with \0
	  char lastc = *(++end);
	  *end = 0;

	  int c;
	  if (insert)
	    c = add_symbol( start );
	  else
	    c = symbol2code(start);
	  // restore the original string
	  *end = lastc;

	  if (c != EOF) {
	    // symbol found
	    // return its code
	    string = end;
	    return (Character)c;
	  }
	  else
	    // not a complex character
	    break;
	}
    return EOF;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Alphabet::next_code                                            */
  /*                                                                 */
  /*******************************************************************/

  int Alphabet::next_code( char* &string, bool extended, bool insert )

  {
    if (*string == 0)
      return EOF; // finished

    int c = next_mcsym(string, insert);
    if (c != EOF)
      return c;

    if (extended && *string == '\\')
      string++; // remove quotation

    if (utf8) {
      unsigned int c = utf8toint( &string );
      if (c == 0) {
	fprintf(stderr, "Error in UTF-8 encoding at: <%s>\n", string);
	return EOF; // error encountered in utf8 character
      }
      return (int)add_symbol(int2utf8(c));
    }
    else {
      char buffer[2];
      buffer[0] = *string;
      buffer[1] = 0;
      string++;
      return (int)add_symbol(buffer);
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Alphabet::next_label                                           */
  /*                                                                 */
  /*******************************************************************/

  Label Alphabet::next_label( char* &string, bool extended )

  {
    // read first character
    int c = next_code( string, extended );
    if (c == EOF)
      return Label(); // end of string reached

    Character lc=(Character)c;
    if (!extended || *string != ':') { // single character?
      if (lc == Label::epsilon)
	return next_label(string, extended); // ignore epsilon
      return Label(lc);
    }

    // read second character
    string++; // jump over ':'
    c = next_code( string, extended );
    if (c == EOF) {
      static char buffer[1000];
      sprintf(buffer,"Error: incomplete symbol in input file: %s", string);
      throw buffer;
    }

    Label l(lc, (Character)c);
    if (l.is_epsilon())
      return next_label(string, extended); // ignore epsilon transitions
    return l;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Alphabet::string2symseq                                        */
  /*                                                                 */
  /*******************************************************************/

  void Alphabet::string2symseq( char *s, vector<Character> &ch )

  {
    int c;
    while ((c = next_code(s, false, false)) != EOF)
      ch.push_back((Character)c);
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Alphabet::string2labelseq                                      */
  /*                                                                 */
  /*******************************************************************/

  void Alphabet::string2labelseq( char *s, vector<Label> &labels )

  {
    Label l;
    while ((l = next_label(s)) != Label::epsilon)
      labels.push_back(l);
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Alphabet::store                                                */
  /*                                                                 */
  /*******************************************************************/

  void Alphabet::store( FILE *file ) const

  {
    char c=(utf8)? (char)1: (char)0;
    fputc(c, file);

    // write the symbol mapping
    Character n=(Character)cm.size();
    fwrite(&n, sizeof(n), 1, file);
    for( CharMap::const_iterator it=cm.begin(); it!=cm.end(); it++ ) {
      Character c=it->first;
      char *s=it->second;
      fwrite(&c, sizeof(c), 1, file);
      fwrite(s, sizeof(char), strlen(s)+1, file);
    }

    // write the character pairs
    n = (Character)size();
    fwrite(&n, sizeof(n), 1, file);
    for( LabelSet::const_iterator p=ls.begin(); p!=ls.end(); p++ ) {
      Character c=p->lower_char();
      fwrite(&c, sizeof(c), 1, file);
      c = p->upper_char();
      fwrite(&c, sizeof(c), 1, file);
    }

    if (ferror(file))
      throw "Error encountered while writing alphabet to file\n";
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Alphabet::read                                                 */
  /*                                                                 */
  /*******************************************************************/

  void Alphabet::read( FILE *file )

  {
    utf8 = (fgetc(file) != 0);

    // read the symbol mapping
    Character n=0;
    read_num(&n, sizeof(n), file);
    for( unsigned i=0; i<n; i++) {
      char buffer[BUFFER_SIZE];
      Character c;
      read_num(&c, sizeof(c), file);
      if (!read_string(buffer, BUFFER_SIZE, file) || 
	  feof(file) || ferror(file))
	throw "Error1 occurred while reading alphabet!\n";
      add_symbol(buffer, c);
    }

    // read the character pairs
    read_num(&n, sizeof(n), file);
    if (ferror(file))
      throw "Error2 occurred while reading alphabet!\n";
    for( unsigned i=0; i<n; i++) {
      Character lc, uc;
      read_num(&lc, sizeof(lc), file);
      read_num(&uc, sizeof(uc), file);
      insert(Label(lc, uc));
    }
    if (ferror(file))
      throw "Error3 occurred while reading alphabet!\n";
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Alphabet::compute_score                                        */
  /*                                                                 */
  /*******************************************************************/

  int Alphabet::compute_score( Analysis &ana )

  {
    // check whether the morpheme boundaries are explicitly marked
    // with <X> tags
    int score=0;
    for( size_t i=0; i<ana.size(); i++ ) {

      // get next symbol
      const char *sym=write_char(ana[i].lower_char());

      if (strcmp(sym,"<X>") == 0)
	score--;
    }
    if (score <  0)
      return score;

    // No explicit morpheme boundary markers have been found.
    // Count the number of part-of-speech and PREF tags.
    for( size_t i=0; i<ana.size(); i++ ) {

      // get next symbol
      const char *sym=write_char(ana[i].lower_char());

      // Is it not a multi-character symbol
      if (sym[0] != '<' || sym[1] == 0)
	continue;

      // Is it a POS tag starting with "+" like <+NN>?
      if (sym[1] == '+') {
	const char *t=sym+2;
	for( ; *t >= 'A' && *t <= 'Z'; t++) ;
	if (t > sym+2 && *t == '>')
	  return score;
      }

      // Is it a potential POS tag (i.e. all uppercase)?
      const char *t = sym+1;
      for( ; *t >= 'A' && *t <= 'Z'; t++) ;
      if (t == sym+1 || *t != '>')
	continue;

      // uppercase symbol found
      if (strcmp(sym,"<SUFF>") == 0 ||
	  strcmp(sym,"<OLDORTH>") == 0 ||
	  strcmp(sym,"<NEWORTH>") == 0)
	continue; // not what we are looking for

      // disprefer nouns with prefixes
      if (strcmp(sym,"<PREF>") == 0)
	score-=2;

      if (strcmp(sym,"<V>") == 0 || strcmp(sym,"<ADJ>") == 0) {
	bool is_verb=(strcmp(sym,"<V>")==0);
	// get the next non-empty symbol
	Character c=Label::epsilon;
	size_t k;
	for( k=i+1; k<ana.size(); k++ )
	  if ((c = ana[k].lower_char()) != Label::epsilon)
	    break;
	// Is it a participle
	if (c != Label::epsilon) {
	  sym = write_char(c);
	  if (strcmp(sym,"<OLDORTH>") == 0 || 
	      strcmp(sym,"<NEWORTH>") == 0 || 
	      strcmp(sym,"<SUFF>") == 0) {
	    for( k++; k<ana.size(); k++ )
	      if ((c = ana[k].lower_char()) != Label::epsilon)
		break;
	    if (c != Label::epsilon)
	      sym = write_char(c);
	  }
	  if (is_verb &&
	      (strcmp(sym,"<PPres>") == 0 || strcmp(sym,"<PPast>") == 0))
	    continue; // don't consider participles as complex
	  if (!is_verb &&
	      (strcmp(sym,"<Sup>") == 0 || strcmp(sym,"<Comp>") == 0))
	    continue;
	}
      }
      score--;
    }
    return score;
  }



  /*******************************************************************/
  /*                                                                 */
  /*  Alphabet::disambiguate                                         */
  /*                                                                 */
  /*******************************************************************/

  void Alphabet::disambiguate( vector<Analysis> &analyses )

  {
    // compute the scores
    int bestscore=INT_MIN;
    vector<int> score;

    for( size_t i=0; i<analyses.size(); i++ ) {
      score.push_back(compute_score(analyses[i]));
      if (bestscore < score[i])
	bestscore = score[i];
    }

    // delete suboptimal analyses
    size_t k=0;
    for( size_t i=0; i<analyses.size(); i++ )
      if (score[i] == bestscore)
	analyses[k++] = analyses[i];
    analyses.resize(k);
  }



  /*******************************************************************/
  /*                                                                 */
  /*  Alphabet::print_analysis                                       */
  /*                                                                 */
  /*******************************************************************/

  char *Alphabet::print_analysis( Analysis &ana, bool both_layers )

  {
    vector<char> ch;

    // for each transition
    for( size_t i=0; i<ana.size(); i++ ) {

      // get the transition label
      Label l=ana[i];
      const char *s;

      // either print the analysis symbol or the whole label
      if (both_layers)
	s = write_label(l);
      else if (l.lower_char() != Label::epsilon)
	s = write_char(l.lower_char());
      else
	continue;

      // copy the characters to the character array
      while (*s)
	ch.push_back(*(s++));
    }
    ch.push_back(0); // terminate the string

    static char *result=NULL;
    if (result != NULL)
      delete[] result;
    result = new char[ch.size()];
    for( size_t i=0; i<ch.size(); i++ )
      result[i] = ch[i];
  
    return result;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  Alphabet::operator==                                           */
  /*                                                                 */
  /*******************************************************************/

  bool Alphabet::operator==(const Alphabet &alpha) const

  {
    for ( SymbolMap::const_iterator it = this->sm.begin(); it != this->sm.end(); it++ )
      {
	SymbolMap::const_iterator alpha_it = alpha.sm.find(it->first);
	if ( alpha_it == alpha.sm.end() )
	  return false;
	if ( alpha_it->second == it->second )
	  return false;
      }
    for ( SymbolMap::const_iterator alpha_it = alpha.sm.begin(); alpha_it != alpha.sm.end(); alpha_it++ )
      {
	SymbolMap::const_iterator it = this->sm.find(alpha_it->first);
	if ( it == this->sm.end() )
	  return false;
	if ( it->second == alpha_it->second )
	  return false;
      }
    return true;
  }

}
