%option 8Bit batch yylineno noyywrap

/* the "incl" state is used to pick up the name of an include file */
%x incl

%{
/*******************************************************************/
/*                                                                 */
/*  FILE     scanner.ll                                            */
/*  MODULE   scanner                                               */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*******************************************************************/

#include <stdio.h>
#include <string.h>

#include "interface.h"
#include "fst-compiler.h"
#include "scanner.h"

using namespace SFST;

#define MAX_INCLUDE_DEPTH 10
  
int Include_Stack_Ptr = 0;
YY_BUFFER_STATE Include_Stack[MAX_INCLUDE_DEPTH];
char *Name_Stack[MAX_INCLUDE_DEPTH];
int  Lineno_Stack[MAX_INCLUDE_DEPTH];

bool Verbose=true;
char *FileName=NULL;

bool UTF8=false;

static void unmatched( char sym ) {
  fprintf(stderr,"Warning: unmatched symbol \"%c\". You might want to quote it.\n", sym);
}

static char *unquote(char *string, bool del_quote=true) {
  char *s=string, *result=string;
  if (del_quote)
    string++;

  while (*string) {
    if (*string == '\\')
      string++;
    *(s++) = *(string++);
  }

  if (del_quote)
    s--;
  *s = '\0';

  return fst_strdup(result);
}

static void print_lineno() {
  if (!Verbose)
    return;
  fputc('\r',stderr);
  for( int i=0; i<Include_Stack_Ptr; i++ )
    fputs("  ", stderr);
  fprintf(stderr,"%s: %d", FileName, yylineno);
}

extern void yyerror( const char *text );

%}

CC	[\x80-\xbf]
C1	[A-Za-z0-9._/\-]
C2	[A-Za-z0-9._/\-&()+,=?\^|~]
C3	[A-Za-z0-9._/\-&()+,=?\^|~#<>]
C4	[A-Za-z0-9._/\-&()+,=?\^|~$<>]
C5	[\!-;\?-\[\]-\~=]
FN	[A-Za-z0-9._/\-*+]

%%

^[ \t]*\#use[ \t]*revdet2[ \t]*\n { Transducer::hopcroft_minimisation = false;};
^[ \t]*\#use[ \t]*default[ \t]*\n { Transducer::hopcroft_minimisation = true;};

#include           BEGIN(incl);
<incl>[ \t]*       /* eat the whitespace */
<incl>{FN}+        { error2("Missing quotes",yytext); }
<incl>\"{FN}+\"    { /* got the include file name */
                     FILE *file;
                     char *name=fst_strdup(yytext+1);
		     name[strlen(name)-1] = 0;
                     if ( Include_Stack_Ptr >= MAX_INCLUDE_DEPTH ) {
		       fprintf( stderr, "Includes nested too deeply" );
		       exit( 1 );
		     }
		     if (Verbose) fputc('\n', stderr);
		     file = fopen( name, "rt" );
		     if (!file)
                       error2("Can't open include file", name);
                     else {
                       Name_Stack[Include_Stack_Ptr] = FileName;
                       FileName = name;
                       Lineno_Stack[Include_Stack_Ptr] = yylineno;
		       yylineno = 1;
		       Include_Stack[Include_Stack_Ptr++]=YY_CURRENT_BUFFER;
		       yy_switch_to_buffer(yy_create_buffer(yyin, YY_BUF_SIZE));
                       yyin = file;
		       print_lineno();
		       BEGIN(INITIAL);
                     }
                  }
<<EOF>>           {
                     if (Verbose)
		       fputc('\n', stderr);
                     if ( --Include_Stack_Ptr < 0 )
		       yyterminate();
		     else {
                       free(FileName);
                       FileName = Name_Stack[Include_Stack_Ptr];
                       yylineno = Lineno_Stack[Include_Stack_Ptr];
		       yy_delete_buffer( YY_CURRENT_BUFFER );
		       yy_switch_to_buffer(Include_Stack[Include_Stack_Ptr]);
                     }
                  }


^[ \t]*\%.*\r?\n  { print_lineno();  /* ignore comments */ }

\%.*\\[ \t]*\r?\n { print_lineno();  /* ignore comments */ }

\%.*              { /* ignore comments */ }


^[ \t]*ALPHABET[ \t]*= { return ALPHA; }

\|\|              { return COMPOSE; }
"<=>"             { yylval.type = twol_both; return ARROW; }
"=>"              { yylval.type = twol_right;return ARROW; }
"<="              { yylval.type = twol_left; return ARROW; }
"^->"             { yylval.rtype = repl_up;   return REPLACE; }
"_->"             { yylval.rtype = my_repl_down; return REPLACE; }
"/->"             { yylval.rtype = repl_right;return REPLACE; }
"\\->"            { yylval.rtype = repl_left; return REPLACE; }
">>"              { return PRINT; }
"<<"              { return INSERT; }
"__"              { return POS; }
"^_"              { return SWITCH; }

[.,{}\[\]()&!?|*+:=_\^\-] { return yytext[0]; }

\$=({C3}|(\\.))+\$ { yylval.name = fst_strdup(yytext); return RVAR; }

\$({C3}|(\\.))+\$ { yylval.name = fst_strdup(yytext); return VAR; }

#=({C4}|(\\.))+# { yylval.name = fst_strdup(yytext); return RSVAR; }

#({C4}|(\\.))+# { yylval.name = fst_strdup(yytext); return SVAR; }

\<({C5}|\\.)*\>   { yylval.name = unquote(yytext,false); return SYMBOL; }

\"<{FN}+>\" { 
                    yylval.value = fst_strdup(yytext+2);
		    yylval.value[strlen(yylval.value)-2] = 0;
                    return STRING2;
                  }

\"{FN}+\" { 
                    yylval.value = fst_strdup(yytext+1);
		    yylval.value[strlen(yylval.value)-1] = 0;
                    return STRING;
                  }

[ \t]             { /* ignored */ }
\\[ \t]*([ \t]\%.*)?\r?\n { print_lineno(); /* ignored */ }
\r?\n             { print_lineno(); return NEWLINE; }

\\[0-9]+          { long l=atol(yytext+1); 
	    if (l <= 255) { yylval.uchar=(unsigned char)l; return CHARACTER; }
		    yyerror("invalid expression");
                  }

\\.	  { yylval.uchar = yytext[1]; return CHARACTER; }
[\$\#\<]	  { yylval.uchar = yytext[0]; unmatched(yytext[0]); return CHARACTER; }
.	  { yylval.uchar = yytext[0]; return CHARACTER; }


%%
