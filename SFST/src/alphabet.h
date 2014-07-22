/*******************************************************************/
/*                                                                 */
/*  FILE     alphabet.h                                            */
/*  MODULE   alphabet                                              */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*  PURPOSE  finite state tools                                    */
/*                                                                 */
/*******************************************************************/

#ifndef _ALPHABET_H_
#define _ALPHABET_H_

#include <stdio.h>

#include "basic.h"

#include <set>
using std::set;

#include <vector>
using std::vector;

#include <iostream>
using std::ostream;

#include <cstring>

#include "sgi.h"

#define SFSTVersion "1.4.6f"

namespace SFST {

#ifndef CODE_DATA_TYPE
  typedef unsigned short Character;  // data type of the symbol codes
#else
  typedef unsigned CODE_DATA_TYPE Character;
#endif

  // data type used to indicate whether some action is to be performed
  // on the analysis level (lower) or the surface level (upper)
  typedef enum {upper, lower} Level;


  /*****************  class Label  ***********************************/

  class Label {

  private:
    // data structure where the two symbols are stored
    struct {
      Character lower;
      Character upper;
    } label;

  public:
    static const Character epsilon=0; // code of the empty symbol

    // new label with two identical symbols
    Label( Character c=epsilon ) { label.lower = label.upper = c; };

    // new label with two different symbols
    Label( Character c1, Character c2 )
      { label.lower = c1; label.upper = c2; };

    // returns the indicated symbol of the label
    Character get_char( Level l ) const
    { return ((l==upper)? label.upper: label.lower); };

    // returns the "upper" symbol of the label (i.e. the surface symbol)
    Character upper_char() const {  return label.upper; };

    // returns the "lower" symbol of the label (i.e. the analysis symbol)
    Character lower_char() const {  return label.lower; };

    // replaces symbols in a label
    Label replace_char( Character c, Character nc ) const {
      Label l = *this;
      if (l.label.lower == c)
	l.label.lower = nc;
      if (l.label.upper == c)
	l.label.upper = nc;
      return l;
    };

    // operators checking the equality of labels
    int operator==( Label l ) const
    { return (label.lower==l.label.lower && label.upper==l.label.upper); };
    int operator!=( Label l ) const
    { return !(l == *this); };

    // comparison operator needed for sorting labels in compact.C
    int operator<( Label l ) const { 
      if (upper_char() < l.upper_char())
	return true;
      if (upper_char() > l.upper_char())
	return false;
      if (lower_char() < l.lower_char())
	return true;
      return false;
    };
    int operator>( Label l ) const { 
      if (upper_char() > l.upper_char())
	return true;
      if (upper_char() < l.upper_char())
	return false;
      if (lower_char() > l.lower_char())
	return true;
      return false;
    };

    // check whether the label is epsilon (i.e. both symbols are epsilon)
    // transitions with epsilon labels are epsilon transitions
    int is_epsilon() const
    { return (label.upper == epsilon && label.lower == epsilon); };

    // check whether the "upper" symbol is epsilon
    int upper_is_epsilon() const
    { return (label.upper == epsilon); };

    // check whether the "lower" symbol is epsilon
    int lower_is_epsilon() const
    { return (label.lower == epsilon); };

    // hash function needed to store labels in a hash table
    struct label_hash {
      size_t operator() ( const Label l ) const {
	return (size_t)l.lower_char() ^ 
	  ((size_t)l.upper_char() << 16) ^
	  ((size_t)l.upper_char() >> 16);
      }
    };

    // comparison function needed to store labels in a map table
    struct label_cmp {
      bool operator() ( const Label l1, const Label l2 ) const {
	return (l1.lower_char() < l2.lower_char() ||
		(l1.lower_char() == l2.lower_char() && 
		 l1.upper_char() < l2.upper_char()));
      }
    };

    // comparison operator needed to store labels in a hash table
    struct label_eq {
      bool operator() ( const Label l1, const Label l2 ) const {
	return (l1.lower_char() == l2.lower_char() &&
		l1.upper_char() == l2.upper_char());
      }
    };
  };

  typedef vector<Label> Analysis;


  /*****************  class Alphabet  *******************************/

  class Alphabet {

    // string comparison operators needed to stored strings in a hash table
    struct eqstr {
      bool operator()(const char* s1, const char* s2) const {
	return strcmp(s1, s2) == 0;
      }
    };

    // data structure storing labels without repetitions (i.e. as a set)
    typedef set<Label, Label::label_cmp> LabelSet;

    // hash table used to map the symbols to their codes
    typedef hash_map<const char*, Character, hash<const char*>,eqstr> SymbolMap;

  public: // HFST addition
    // hash table used to map the codes back to the symbols
    typedef hash_map<Character, char*> CharMap;

    // HFST addition
    bool operator==(const Alphabet &alpha) const;

  private:
    SymbolMap sm; // maps symbols to codes
    CharMap  cm; // maps codes to symbols
    LabelSet ls; // set of labels known to the alphabet

    // add a new symbol with symbol code c
    void add( const char *symbol, Character c );

  public:
    bool utf8;

    // iterators over the set of known labels
    typedef LabelSet::iterator iterator;
    typedef LabelSet::const_iterator const_iterator;
    Alphabet();
    ~Alphabet() { clear(); };
    const_iterator begin() const { return ls.begin(); };
    const_iterator end() const { return ls.end(); };
    size_t size() const { return ls.size(); };

    // HFST additions
    CharMap get_char_map(void) { return cm; };
    void print(void);

    void clear();
    void clear_char_pairs() { ls.clear(); };

    // lookup a label in the alphabet
    iterator find( Label l ) { return ls.find(l); };

    // insert a label in the alphabet
    void insert( Label l ) { if (!l.is_epsilon()) ls.insert(l); };

    // insert the known symbols from another alphabet
    void insert_symbols( const Alphabet& );

    // insert the labels and known symbols from another alphabet
    void copy( const Alphabet& );

    // create the alphabet of a transducer obtained by a composition operation
    void compose( const Alphabet &la, const Alphabet &ua );

    // add a symbol to the alphabet and return its code
    Character add_symbol(const char *symbol);

    // add a symbol to the alphabet with a given code
    void add_symbol(const char *symbol, Character c );

    // create a new marker symbol and return its code
    Character new_marker( void );
    void delete_markers();

    // compute the complement of a symbol set
    void complement( vector<Character> &sym );
  
    // return the code of the argument symbol
    int symbol2code( const char *s ) const { 
      SymbolMap::const_iterator p = sm.find(s);
      if (p != sm.end()) return p->second;
      return EOF;
    };

    // return the symbol for the given symbol code
    const char *code2symbol( Character c ) const {
      CharMap::const_iterator p=cm.find(c);
      if (p == cm.end())
	return NULL;
      else
	return p->second;
    };

    // write the symbol for the given symbol code into a string
    void write_char( Character c, char *buffer, int *pos,
		     bool with_brackets=true ) const;

    // write the symbol pair of a given label into a string
    void write_label( Label l, char *buffer, int *pos,
		      bool with_brackets=true ) const;

    // write the symbol for the given symbol code into a buffer and return
    // a pointer to it
    // the flag "with_brackets" indicates whether the angle brackets
    // surrounding multi-character symbols are to be printed or not
    const char *write_char( Character c, bool with_brackets=true ) const;

    // write the symbol pair of a given label into a string
    // and return a pointer to it
    const char *write_label( Label l, bool with_brackets=true ) const;

    // scan the next multi-character symbol in the argument string
    int next_mcsym( char*&, bool insert=true );

    // scan the next symbol in the argument string
    int next_code( char*&, bool extended=true, bool insert=true );

    // convert a character string into a symbol or label sequence
    void string2symseq( char*, vector<Character>& );
    void string2labelseq( char*, vector<Label>& );

    // scan the next label in the argument string
    Label next_label( char*&, bool extended=true );

    // store the alphabet in the argument file (in binary form)
    void store( FILE* ) const;

    // read the alphabet from the argument file
    void read( FILE* );

    // disambiguation and printing of analyses
    int compute_score( Analysis &ana );
    void disambiguate( vector<Analysis> &analyses );
    char *print_analysis( Analysis &ana, bool both_layers );

    friend ostream &operator<<(ostream&, const Alphabet&);
  };

  // write the alphabet to the output stream (in readable form)
  ostream &operator<<(ostream&, const Alphabet&);
}

#endif
