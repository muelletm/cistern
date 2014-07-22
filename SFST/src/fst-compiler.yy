%{
/*******************************************************************/
/*                                                                 */
/*  FILE     fst-compiler.yy                                       */
/*  MODULE   fst-compiler                                          */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*******************************************************************/

#include <stdio.h>

#include "make-compact.h"
#include "scanner.h"
#include "interface.h"

using std::cerr;
using namespace SFST;

extern int  yylineno;
extern char *yytext;

void yyerror( const char *text );
void warn( const char *text );
void warn2( const char *text, const char *text2);
int yylex( void );
int yyparse( void );

static int Switch=0;
Interface interface;
Transducer *Result;
%}

%union {
  int        number;
  SFST::Twol_Type  type;
  SFST::Repl_Type  rtype;
  char       *name;
  char       *value;
  unsigned char uchar;
  unsigned int  longchar;
  SFST::Character  character;
  SFST::Transducer  *expression;
  SFST::Range      *range;
  SFST::Ranges     *ranges;
  SFST::Contexts   *contexts;
}

%token <number> NEWLINE ALPHA COMPOSE PRINT POS INSERT SWITCH
%token <type>   ARROW
%token <rtype>  REPLACE
%token <name>   SYMBOL VAR SVAR RVAR RSVAR
%token <value>  STRING STRING2 UTF8CHAR
%token <uchar>  CHARACTER

%type  <longchar>   LCHAR
%type  <character>  CODE
%type  <expression> RE
%type  <range>      RANGE VALUE VALUES
%type  <ranges>     RANGES
%type  <contexts>   CONTEXT CONTEXT2 CONTEXTS CONTEXTS2

%left PRINT INSERT
%left ARROW REPLACE
%left COMPOSE
%left '|'
%left '-'
%left '&'
%left SEQ
%left '!' '^' '_'
%left '*' '+'
%%

ALL:        ASSIGNMENTS RE NEWLINES { Result=interface.result($2, Switch); }
          ;

ASSIGNMENTS: ASSIGNMENTS ASSIGNMENT {}
          | ASSIGNMENTS NEWLINE     {}
          | /* nothing */           {}
          ;

ASSIGNMENT: VAR '=' RE              { if (interface.def_var($1,$3)) warn2("assignment of empty transducer to",$1); }
          | RVAR '=' RE             { if (interface.def_rvar($1,$3)) warn2("assignment of empty transducer to",$1); }
          | SVAR '=' VALUES         { if (interface.def_svar($1,$3)) warn2("assignment of empty symbol range to",$1); }
          | RSVAR '=' VALUES        { if (interface.def_svar($1,$3)) warn2("assignment of empty symbol range to",$1); }
          | RE PRINT STRING         { interface.write_to_file($1, $3); }
          | ALPHA RE                { interface.def_alphabet($2); }
          ;

RE:         RE ARROW CONTEXTS2      { $$ = interface.restriction($1,$2,$3,0); }
	  | RE '^' ARROW CONTEXTS2  { $$ = interface.restriction($1,$3,$4,1); }
	  | RE '_' ARROW CONTEXTS2  { $$ = interface.restriction($1,$3,$4,-1); }
          | RE REPLACE CONTEXT2     { $$ = interface.replace_in_context(interface.minimise(interface.explode($1)),$2,$3,false); }
          | RE REPLACE '?' CONTEXT2 { $$ = interface.replace_in_context(interface.minimise(interface.explode($1)),$2,$4,true);}
          | RE REPLACE '(' ')'      { $$ = interface.replace(interface.minimise(interface.explode($1)), $2, false); }
          | RE REPLACE '?' '(' ')'  { $$ = interface.replace(interface.minimise(interface.explode($1)), $2, true); }
          | RE RANGE ARROW RANGE RE { $$ = interface.make_rule($1,$2,$3,$4,$5); }
          | RE RANGE ARROW RANGE    { $$ = interface.make_rule($1,$2,$3,$4,NULL); }
          | RANGE ARROW RANGE RE    { $$ = interface.make_rule(NULL,$1,$2,$3,$4); }
          | RANGE ARROW RANGE       { $$ = interface.make_rule(NULL,$1,$2,$3,NULL); }
          | RE COMPOSE RE    { $$ = interface.composition($1, $3); }
          | '{' RANGES '}' ':' '{' RANGES '}' { $$ = interface.make_mapping($2,$6); }
          | RANGE ':' '{' RANGES '}' { $$ = interface.make_mapping(interface.add_range($1,NULL),$4); }
          | '{' RANGES '}' ':' RANGE { $$ = interface.make_mapping($2,interface.add_range($5,NULL)); }
          | RE INSERT CODE ':' CODE  { $$ = interface.freely_insert($1, $3, $5); }
          | RE INSERT CODE           { $$ = interface.freely_insert($1, $3, $3); }
          | RANGE ':' RANGE  { $$ = interface.new_transducer($1,$3); }
          | RANGE            { $$ = interface.new_transducer($1,$1); }
          | VAR              { $$ = interface.var_value($1); }
          | RVAR             { $$ = interface.rvar_value($1); }
          | RE '*'           { $$ = interface.repetition($1); }
          | RE '+'           { $$ = interface.repetition2($1); }
          | RE '?'           { $$ = interface.optional($1); }
          | RE RE %prec SEQ  { $$ = interface.catenate($1, $2); }
          | '!' RE           { $$ = interface.negation($2); }
          | SWITCH RE        { $$ = interface.switch_levels($2); }
          | '^' RE           { $$ = interface.upper_level($2); }
          | '_' RE           { $$ = interface.lower_level($2); }
          | RE '&' RE        { $$ = interface.conjunction($1, $3); }
          | RE '-' RE        { $$ = interface.subtraction($1, $3); }
          | RE '|' RE        { $$ = interface.disjunction($1, $3); }
          | '(' RE ')'       { $$ = $2; }
          | STRING           { $$ = interface.read_words($1); }
          | STRING2          { $$ = interface.read_transducer($1); }
          ;

RANGES:     RANGE RANGES     { $$ = interface.add_range($1,$2); }
          |                  { $$ = NULL; }
          ;

RANGE:      '[' VALUES ']'   { $$=$2; }
          | '[' '^' VALUES ']' { $$=interface.complement_range($3); }
          | '[' RSVAR ']'    { $$=interface.rsvar_value($2); }
          | '.'              { $$=NULL; }
          | CODE             { $$=interface.add_value($1,NULL); }
          ;

CONTEXTS2:  CONTEXTS               { $$ = $1; }
          | '(' CONTEXTS ')'       { $$ = $2; }
          ;

CONTEXTS:   CONTEXT ',' CONTEXTS   { $$ = interface.add_context($1,$3); }
          | CONTEXT                { $$ = $1; }
          ;

CONTEXT2:   CONTEXT                { $$ = $1; }
          | '(' CONTEXT ')'        { $$ = $2; }
          ;

CONTEXT :   RE POS RE              { $$ = interface.make_context($1, $3); }
          |    POS RE              { $$ = interface.make_context(NULL, $2); }
          | RE POS                 { $$ = interface.make_context($1, NULL); }
          ;

VALUES:     VALUE VALUES           { $$=interface.append_values($1,$2); }
          | VALUE                  { $$ = $1; }
          ;

VALUE:      LCHAR '-' LCHAR	   { $$=interface.add_values($1,$3,NULL); }
          | SVAR                   { $$=interface.svar_value($1); }
          | LCHAR  	           { $$=interface.add_value(interface.character_code($1),NULL); }
          | CODE		   { $$=interface.add_value($1,NULL); }
          ;

LCHAR:      CHARACTER	{ $$=$1; }
          | UTF8CHAR	{ $$=utf8toint($1); free($1); }
	  | '.'		{ $$='.'; }
          | '!'		{ $$='!'; }
          | '?'		{ $$='?'; }
          | '{'		{ $$='{'; }
          | '}'		{ $$='}'; }
          | ')'		{ $$=')'; }
          | '('		{ $$='('; }
          | '&'		{ $$='&'; }
          | '|'		{ $$='|'; }
          | '*'		{ $$='*'; }
          | '+'		{ $$='+'; }
          | ':'		{ $$=':'; }
          | ','		{ $$=','; }
          | '='		{ $$='='; }
          | '_'		{ $$='_'; }
          | '^'		{ $$='^'; }
          | '-'		{ $$='-'; }
          ;

CODE:       CHARACTER	{ $$=interface.character_code($1); }
          | UTF8CHAR	{ $$=interface.symbol_code($1); }
          | SYMBOL	{ $$=interface.symbol_code($1); }
          ;


NEWLINES:   NEWLINE NEWLINES     {}
          | /* nothing */        {}
          ;

%%

extern FILE  *yyin;
static int Compact=0;
static int LowMem=0;

/*******************************************************************/
/*                                                                 */
/*  yyerror                                                        */
/*                                                                 */
/*******************************************************************/

void yyerror( const char *text )

{
  cerr << "\n" << FileName << ":" << yylineno << ": " << text << " at: ";
  cerr << yytext << "\naborted.\n";
  exit(1);
}


/*******************************************************************/
/*                                                                 */
/*  warn                                                           */
/*                                                                 */
/*******************************************************************/

void warn( const char *text )

{
  if (Verbose)
    cerr << "\n";
  cerr << FileName << ":" << yylineno << ": warning: " << text << "!\n";
}


/*******************************************************************/
/*                                                                 */
/*  warn2                                                          */
/*                                                                 */
/*******************************************************************/

void warn2( const char *text, const char *text2)

{
  if (Verbose)
    cerr << "\n";
  cerr << FileName << ":" << yylineno << ": warning: " << text << ": ";
  cerr << text2 << "\n";
}


/*******************************************************************/
/*                                                                 */
/*  get_flags                                                      */
/*                                                                 */
/*******************************************************************/

void get_flags( int *argc, char **argv )

{
  for( int i=1; i<*argc; i++ ) {
    if (strcmp(argv[i],"-c") == 0) {
      Compact = 1;
      argv[i] = NULL;
    }
    else if (strcmp(argv[i],"-l") == 0) {
      LowMem = 1;
      argv[i] = NULL;
    }
    else if (strcmp(argv[i],"-q") == 0) {
      Verbose = 0;
      argv[i] = NULL;
    }
    else if (strcmp(argv[i],"-v") == 0) {
      printf("fst-compiler version %s\n", SFSTVersion);
      exit(0);
    }
    else if (strcmp(argv[i],"-s") == 0) {
      Switch = 1;
      argv[i] = NULL;
    }
    else if (strcmp(argv[i],"-lc") == 0) {
      interface.allow_lexicon_comments();
      argv[i] = NULL;
    }
  }
  // remove flags from the argument list
  int k;
  for( int i=k=1; i<*argc; i++)
    if (argv[i] != NULL)
      argv[k++] = argv[i];
  *argc = k;
}


/*******************************************************************/
/*                                                                 */
/*  main                                                           */
/*                                                                 */
/*******************************************************************/

int main( int argc, char *argv[] )

{
  FILE *file;

  get_flags(&argc, argv);
  if (argc < 3) {
    fprintf(stderr,"\nUsage: %s [options] infile outfile\n", argv[0]);
    fprintf(stderr,"\nOPTIONS:\n");
    fprintf(stderr,"-c\tStore the transducer in fst-infl2 format.\n");
    fprintf(stderr,"-l\tStore the transducer in fst-infl3 format.\n");
    fprintf(stderr,"-s\tSwitch the upper and lower levels producing a transducer for generation rather than recognition.\n");
    fprintf(stderr,"-lc\tallow comments starting with '%%' in the lexicon files.\n");
    fprintf(stderr,"-v\tprint version information\n");
    fprintf(stderr,"-q\tquiet mode\n\n");
    exit(1);
  }
  if ((file = fopen(argv[1],"rt")) == NULL) {
    fprintf(stderr,"\nError: Cannot open source file \"%s\"\n\n", argv[1]);
    exit(1);
  }
  FileName = argv[1];
  Result = NULL;
  interface.TheAlphabet.utf8 = UTF8;
  interface.Verbose = Verbose;
  yyin = file;
  try {
    yyparse();
    Result->alphabet.utf8 = UTF8;
    if (Verbose)
      cerr << "\n";
    if (Result->is_empty()) 
      warn("result transducer is empty"); 
    if ((file = fopen(argv[2],"wb")) == NULL) {
	fprintf(stderr,"\nError: Cannot open output file %s\n\n", argv[2]);
	exit(1);
    }
    if (Compact) {
      MakeCompactTransducer ca(*Result);
      delete Result;
      ca.store(file);
    }
    else if (LowMem)
      Result->store_lowmem(file);
    else
      Result->store(file);
    fclose(file);
  }
  catch(const char* p) {
      cerr << "\n" << p << "\n\n";
      exit(1);
  }
}
