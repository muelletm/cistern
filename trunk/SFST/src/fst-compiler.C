/* A Bison parser, made by GNU Bison 2.7.  */

/* Bison implementation for Yacc-like parsers in C
   
      Copyright (C) 1984, 1989-1990, 2000-2012 Free Software Foundation, Inc.
   
   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.
   
   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
   
   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.  */

/* As a special exception, you may create a larger work that contains
   part or all of the Bison parser skeleton and distribute that work
   under terms of your choice, so long as that work isn't itself a
   parser generator using the skeleton or a modified version thereof
   as a parser skeleton.  Alternatively, if you modify or redistribute
   the parser skeleton itself, you may (at your option) remove this
   special exception, which will cause the skeleton and the resulting
   Bison output files to be licensed under the GNU General Public
   License without this special exception.
   
   This special exception was added by the Free Software Foundation in
   version 2.2 of Bison.  */

/* C LALR(1) parser skeleton written by Richard Stallman, by
   simplifying the original so-called "semantic" parser.  */

/* All symbols defined below should begin with yy or YY, to avoid
   infringing on user name space.  This should be done even for local
   variables, as they might otherwise be expanded by user macros.
   There are some unavoidable exceptions within include files to
   define necessary library symbols; they are noted "INFRINGES ON
   USER NAME SPACE" below.  */

/* Identify Bison output.  */
#define YYBISON 1

/* Bison version.  */
#define YYBISON_VERSION "2.7"

/* Skeleton name.  */
#define YYSKELETON_NAME "yacc.c"

/* Pure parsers.  */
#define YYPURE 0

/* Push parsers.  */
#define YYPUSH 0

/* Pull parsers.  */
#define YYPULL 1




/* Copy the first part of user declarations.  */
/* Line 371 of yacc.c  */
#line 1 "fst-compiler.yy"

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

/* Line 371 of yacc.c  */
#line 101 "fst-compiler.C"

# ifndef YY_NULL
#  if defined __cplusplus && 201103L <= __cplusplus
#   define YY_NULL nullptr
#  else
#   define YY_NULL 0
#  endif
# endif

/* Enabling verbose error messages.  */
#ifdef YYERROR_VERBOSE
# undef YYERROR_VERBOSE
# define YYERROR_VERBOSE 1
#else
# define YYERROR_VERBOSE 0
#endif

/* In a future release of Bison, this section will be replaced
   by #include "fst-compiler.H".  */
#ifndef YY_YY_FST_COMPILER_H_INCLUDED
# define YY_YY_FST_COMPILER_H_INCLUDED
/* Enabling traces.  */
#ifndef YYDEBUG
# define YYDEBUG 0
#endif
#if YYDEBUG
extern int yydebug;
#endif

/* Tokens.  */
#ifndef YYTOKENTYPE
# define YYTOKENTYPE
   /* Put the tokens into the symbol table, so that GDB and other debuggers
      know about them.  */
   enum yytokentype {
     NEWLINE = 258,
     ALPHA = 259,
     COMPOSE = 260,
     PRINT = 261,
     POS = 262,
     INSERT = 263,
     SWITCH = 264,
     ARROW = 265,
     REPLACE = 266,
     SYMBOL = 267,
     VAR = 268,
     SVAR = 269,
     RVAR = 270,
     RSVAR = 271,
     STRING = 272,
     STRING2 = 273,
     UTF8CHAR = 274,
     CHARACTER = 275,
     SEQ = 276
   };
#endif


#if ! defined YYSTYPE && ! defined YYSTYPE_IS_DECLARED
typedef union YYSTYPE
{
/* Line 387 of yacc.c  */
#line 34 "fst-compiler.yy"

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


/* Line 387 of yacc.c  */
#line 181 "fst-compiler.C"
} YYSTYPE;
# define YYSTYPE_IS_TRIVIAL 1
# define yystype YYSTYPE /* obsolescent; will be withdrawn */
# define YYSTYPE_IS_DECLARED 1
#endif

extern YYSTYPE yylval;

#ifdef YYPARSE_PARAM
#if defined __STDC__ || defined __cplusplus
int yyparse (void *YYPARSE_PARAM);
#else
int yyparse ();
#endif
#else /* ! YYPARSE_PARAM */
#if defined __STDC__ || defined __cplusplus
int yyparse (void);
#else
int yyparse ();
#endif
#endif /* ! YYPARSE_PARAM */

#endif /* !YY_YY_FST_COMPILER_H_INCLUDED  */

/* Copy the second part of user declarations.  */

/* Line 390 of yacc.c  */
#line 209 "fst-compiler.C"

#ifdef short
# undef short
#endif

#ifdef YYTYPE_UINT8
typedef YYTYPE_UINT8 yytype_uint8;
#else
typedef unsigned char yytype_uint8;
#endif

#ifdef YYTYPE_INT8
typedef YYTYPE_INT8 yytype_int8;
#elif (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
typedef signed char yytype_int8;
#else
typedef short int yytype_int8;
#endif

#ifdef YYTYPE_UINT16
typedef YYTYPE_UINT16 yytype_uint16;
#else
typedef unsigned short int yytype_uint16;
#endif

#ifdef YYTYPE_INT16
typedef YYTYPE_INT16 yytype_int16;
#else
typedef short int yytype_int16;
#endif

#ifndef YYSIZE_T
# ifdef __SIZE_TYPE__
#  define YYSIZE_T __SIZE_TYPE__
# elif defined size_t
#  define YYSIZE_T size_t
# elif ! defined YYSIZE_T && (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
#  include <stddef.h> /* INFRINGES ON USER NAME SPACE */
#  define YYSIZE_T size_t
# else
#  define YYSIZE_T unsigned int
# endif
#endif

#define YYSIZE_MAXIMUM ((YYSIZE_T) -1)

#ifndef YY_
# if defined YYENABLE_NLS && YYENABLE_NLS
#  if ENABLE_NLS
#   include <libintl.h> /* INFRINGES ON USER NAME SPACE */
#   define YY_(Msgid) dgettext ("bison-runtime", Msgid)
#  endif
# endif
# ifndef YY_
#  define YY_(Msgid) Msgid
# endif
#endif

/* Suppress unused-variable warnings by "using" E.  */
#if ! defined lint || defined __GNUC__
# define YYUSE(E) ((void) (E))
#else
# define YYUSE(E) /* empty */
#endif

/* Identity function, used to suppress warnings about constant conditions.  */
#ifndef lint
# define YYID(N) (N)
#else
#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static int
YYID (int yyi)
#else
static int
YYID (yyi)
    int yyi;
#endif
{
  return yyi;
}
#endif

#if ! defined yyoverflow || YYERROR_VERBOSE

/* The parser invokes alloca or malloc; define the necessary symbols.  */

# ifdef YYSTACK_USE_ALLOCA
#  if YYSTACK_USE_ALLOCA
#   ifdef __GNUC__
#    define YYSTACK_ALLOC __builtin_alloca
#   elif defined __BUILTIN_VA_ARG_INCR
#    include <alloca.h> /* INFRINGES ON USER NAME SPACE */
#   elif defined _AIX
#    define YYSTACK_ALLOC __alloca
#   elif defined _MSC_VER
#    include <malloc.h> /* INFRINGES ON USER NAME SPACE */
#    define alloca _alloca
#   else
#    define YYSTACK_ALLOC alloca
#    if ! defined _ALLOCA_H && ! defined EXIT_SUCCESS && (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
#     include <stdlib.h> /* INFRINGES ON USER NAME SPACE */
      /* Use EXIT_SUCCESS as a witness for stdlib.h.  */
#     ifndef EXIT_SUCCESS
#      define EXIT_SUCCESS 0
#     endif
#    endif
#   endif
#  endif
# endif

# ifdef YYSTACK_ALLOC
   /* Pacify GCC's `empty if-body' warning.  */
#  define YYSTACK_FREE(Ptr) do { /* empty */; } while (YYID (0))
#  ifndef YYSTACK_ALLOC_MAXIMUM
    /* The OS might guarantee only one guard page at the bottom of the stack,
       and a page size can be as small as 4096 bytes.  So we cannot safely
       invoke alloca (N) if N exceeds 4096.  Use a slightly smaller number
       to allow for a few compiler-allocated temporary stack slots.  */
#   define YYSTACK_ALLOC_MAXIMUM 4032 /* reasonable circa 2006 */
#  endif
# else
#  define YYSTACK_ALLOC YYMALLOC
#  define YYSTACK_FREE YYFREE
#  ifndef YYSTACK_ALLOC_MAXIMUM
#   define YYSTACK_ALLOC_MAXIMUM YYSIZE_MAXIMUM
#  endif
#  if (defined __cplusplus && ! defined EXIT_SUCCESS \
       && ! ((defined YYMALLOC || defined malloc) \
	     && (defined YYFREE || defined free)))
#   include <stdlib.h> /* INFRINGES ON USER NAME SPACE */
#   ifndef EXIT_SUCCESS
#    define EXIT_SUCCESS 0
#   endif
#  endif
#  ifndef YYMALLOC
#   define YYMALLOC malloc
#   if ! defined malloc && ! defined EXIT_SUCCESS && (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
void *malloc (YYSIZE_T); /* INFRINGES ON USER NAME SPACE */
#   endif
#  endif
#  ifndef YYFREE
#   define YYFREE free
#   if ! defined free && ! defined EXIT_SUCCESS && (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
void free (void *); /* INFRINGES ON USER NAME SPACE */
#   endif
#  endif
# endif
#endif /* ! defined yyoverflow || YYERROR_VERBOSE */


#if (! defined yyoverflow \
     && (! defined __cplusplus \
	 || (defined YYSTYPE_IS_TRIVIAL && YYSTYPE_IS_TRIVIAL)))

/* A type that is properly aligned for any stack member.  */
union yyalloc
{
  yytype_int16 yyss_alloc;
  YYSTYPE yyvs_alloc;
};

/* The size of the maximum gap between one aligned stack and the next.  */
# define YYSTACK_GAP_MAXIMUM (sizeof (union yyalloc) - 1)

/* The size of an array large to enough to hold all stacks, each with
   N elements.  */
# define YYSTACK_BYTES(N) \
     ((N) * (sizeof (yytype_int16) + sizeof (YYSTYPE)) \
      + YYSTACK_GAP_MAXIMUM)

# define YYCOPY_NEEDED 1

/* Relocate STACK from its old location to the new one.  The
   local variables YYSIZE and YYSTACKSIZE give the old and new number of
   elements in the stack, and YYPTR gives the new location of the
   stack.  Advance YYPTR to a properly aligned location for the next
   stack.  */
# define YYSTACK_RELOCATE(Stack_alloc, Stack)				\
    do									\
      {									\
	YYSIZE_T yynewbytes;						\
	YYCOPY (&yyptr->Stack_alloc, Stack, yysize);			\
	Stack = &yyptr->Stack_alloc;					\
	yynewbytes = yystacksize * sizeof (*Stack) + YYSTACK_GAP_MAXIMUM; \
	yyptr += yynewbytes / sizeof (*yyptr);				\
      }									\
    while (YYID (0))

#endif

#if defined YYCOPY_NEEDED && YYCOPY_NEEDED
/* Copy COUNT objects from SRC to DST.  The source and destination do
   not overlap.  */
# ifndef YYCOPY
#  if defined __GNUC__ && 1 < __GNUC__
#   define YYCOPY(Dst, Src, Count) \
      __builtin_memcpy (Dst, Src, (Count) * sizeof (*(Src)))
#  else
#   define YYCOPY(Dst, Src, Count)              \
      do                                        \
        {                                       \
          YYSIZE_T yyi;                         \
          for (yyi = 0; yyi < (Count); yyi++)   \
            (Dst)[yyi] = (Src)[yyi];            \
        }                                       \
      while (YYID (0))
#  endif
# endif
#endif /* !YYCOPY_NEEDED */

/* YYFINAL -- State number of the termination state.  */
#define YYFINAL  3
/* YYLAST -- Last index in YYTABLE.  */
#define YYLAST   900

/* YYNTOKENS -- Number of terminals.  */
#define YYNTOKENS  41
/* YYNNTS -- Number of nonterminals.  */
#define YYNNTS  16
/* YYNRULES -- Number of rules.  */
#define YYNRULES  92
/* YYNRULES -- Number of states.  */
#define YYNSTATES  153

/* YYTRANSLATE(YYLEX) -- Bison symbol number corresponding to YYLEX.  */
#define YYUNDEFTOK  2
#define YYMAXUTOK   276

#define YYTRANSLATE(YYX)						\
  ((unsigned int) (YYX) <= YYMAXUTOK ? yytranslate[YYX] : YYUNDEFTOK)

/* YYTRANSLATE[YYLEX] -- Bison symbol number corresponding to YYLEX.  */
static const yytype_uint8 yytranslate[] =
{
       0,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,    25,     2,     2,     2,     2,    23,     2,
      32,    33,    28,    29,    40,    22,    39,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,    36,     2,
       2,    30,     2,    31,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,    37,     2,    38,    26,    27,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,    34,    21,    35,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     1,     2,     3,     4,
       5,     6,     7,     8,     9,    10,    11,    12,    13,    14,
      15,    16,    17,    18,    19,    20,    24
};

#if YYDEBUG
/* YYPRHS[YYN] -- Index of the first RHS symbol of rule number YYN in
   YYRHS.  */
static const yytype_uint16 yyprhs[] =
{
       0,     0,     3,     7,    10,    13,    14,    18,    22,    26,
      30,    34,    37,    41,    46,    51,    55,    60,    65,    71,
      77,    82,    87,    91,    95,   103,   109,   115,   121,   125,
     129,   131,   133,   135,   138,   141,   144,   147,   150,   153,
     156,   159,   163,   167,   171,   175,   177,   179,   182,   183,
     187,   192,   196,   198,   200,   202,   206,   210,   212,   214,
     218,   222,   225,   228,   231,   233,   237,   239,   241,   243,
     245,   247,   249,   251,   253,   255,   257,   259,   261,   263,
     265,   267,   269,   271,   273,   275,   277,   279,   281,   283,
     285,   287,   290
};

/* YYRHS -- A `-1'-separated list of the rules' RHS.  */
static const yytype_int8 yyrhs[] =
{
      42,     0,    -1,    43,    45,    56,    -1,    43,    44,    -1,
      43,     3,    -1,    -1,    13,    30,    45,    -1,    15,    30,
      45,    -1,    14,    30,    52,    -1,    16,    30,    52,    -1,
      45,     6,    17,    -1,     4,    45,    -1,    45,    10,    48,
      -1,    45,    26,    10,    48,    -1,    45,    27,    10,    48,
      -1,    45,    11,    50,    -1,    45,    11,    31,    50,    -1,
      45,    11,    32,    33,    -1,    45,    11,    31,    32,    33,
      -1,    45,    47,    10,    47,    45,    -1,    45,    47,    10,
      47,    -1,    47,    10,    47,    45,    -1,    47,    10,    47,
      -1,    45,     5,    45,    -1,    34,    46,    35,    36,    34,
      46,    35,    -1,    47,    36,    34,    46,    35,    -1,    34,
      46,    35,    36,    47,    -1,    45,     8,    55,    36,    55,
      -1,    45,     8,    55,    -1,    47,    36,    47,    -1,    47,
      -1,    13,    -1,    15,    -1,    45,    28,    -1,    45,    29,
      -1,    45,    31,    -1,    45,    45,    -1,    25,    45,    -1,
       9,    45,    -1,    26,    45,    -1,    27,    45,    -1,    45,
      23,    45,    -1,    45,    22,    45,    -1,    45,    21,    45,
      -1,    32,    45,    33,    -1,    17,    -1,    18,    -1,    47,
      46,    -1,    -1,    37,    52,    38,    -1,    37,    26,    52,
      38,    -1,    37,    16,    38,    -1,    39,    -1,    55,    -1,
      49,    -1,    32,    49,    33,    -1,    51,    40,    49,    -1,
      51,    -1,    51,    -1,    32,    51,    33,    -1,    45,     7,
      45,    -1,     7,    45,    -1,    45,     7,    -1,    53,    52,
      -1,    53,    -1,    54,    22,    54,    -1,    14,    -1,    54,
      -1,    55,    -1,    20,    -1,    19,    -1,    39,    -1,    25,
      -1,    31,    -1,    34,    -1,    35,    -1,    33,    -1,    32,
      -1,    23,    -1,    21,    -1,    28,    -1,    29,    -1,    36,
      -1,    40,    -1,    30,    -1,    27,    -1,    26,    -1,    22,
      -1,    20,    -1,    19,    -1,    12,    -1,     3,    56,    -1,
      -1
};

/* YYRLINE[YYN] -- source line where rule number YYN was defined.  */
static const yytype_uint8 yyrline[] =
{
       0,    74,    74,    77,    78,    79,    82,    83,    84,    85,
      86,    87,    90,    91,    92,    93,    94,    95,    96,    97,
      98,    99,   100,   101,   102,   103,   104,   105,   106,   107,
     108,   109,   110,   111,   112,   113,   114,   115,   116,   117,
     118,   119,   120,   121,   122,   123,   124,   127,   128,   131,
     132,   133,   134,   135,   138,   139,   142,   143,   146,   147,
     150,   151,   152,   155,   156,   159,   160,   161,   162,   165,
     166,   167,   168,   169,   170,   171,   172,   173,   174,   175,
     176,   177,   178,   179,   180,   181,   182,   183,   186,   187,
     188,   192,   193
};
#endif

#if YYDEBUG || YYERROR_VERBOSE || 0
/* YYTNAME[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
   First, the terminals, then, starting at YYNTOKENS, nonterminals.  */
static const char *const yytname[] =
{
  "$end", "error", "$undefined", "NEWLINE", "ALPHA", "COMPOSE", "PRINT",
  "POS", "INSERT", "SWITCH", "ARROW", "REPLACE", "SYMBOL", "VAR", "SVAR",
  "RVAR", "RSVAR", "STRING", "STRING2", "UTF8CHAR", "CHARACTER", "'|'",
  "'-'", "'&'", "SEQ", "'!'", "'^'", "'_'", "'*'", "'+'", "'='", "'?'",
  "'('", "')'", "'{'", "'}'", "':'", "'['", "']'", "'.'", "','", "$accept",
  "ALL", "ASSIGNMENTS", "ASSIGNMENT", "RE", "RANGES", "RANGE", "CONTEXTS2",
  "CONTEXTS", "CONTEXT2", "CONTEXT", "VALUES", "VALUE", "LCHAR", "CODE",
  "NEWLINES", YY_NULL
};
#endif

# ifdef YYPRINT
/* YYTOKNUM[YYLEX-NUM] -- Internal token number corresponding to
   token YYLEX-NUM.  */
static const yytype_uint16 yytoknum[] =
{
       0,   256,   257,   258,   259,   260,   261,   262,   263,   264,
     265,   266,   267,   268,   269,   270,   271,   272,   273,   274,
     275,   124,    45,    38,   276,    33,    94,    95,    42,    43,
      61,    63,    40,    41,   123,   125,    58,    91,    93,    46,
      44
};
# endif

/* YYR1[YYN] -- Symbol number of symbol that rule YYN derives.  */
static const yytype_uint8 yyr1[] =
{
       0,    41,    42,    43,    43,    43,    44,    44,    44,    44,
      44,    44,    45,    45,    45,    45,    45,    45,    45,    45,
      45,    45,    45,    45,    45,    45,    45,    45,    45,    45,
      45,    45,    45,    45,    45,    45,    45,    45,    45,    45,
      45,    45,    45,    45,    45,    45,    45,    46,    46,    47,
      47,    47,    47,    47,    48,    48,    49,    49,    50,    50,
      51,    51,    51,    52,    52,    53,    53,    53,    53,    54,
      54,    54,    54,    54,    54,    54,    54,    54,    54,    54,
      54,    54,    54,    54,    54,    54,    54,    54,    55,    55,
      55,    56,    56
};

/* YYR2[YYN] -- Number of symbols composing right hand side of rule YYN.  */
static const yytype_uint8 yyr2[] =
{
       0,     2,     3,     2,     2,     0,     3,     3,     3,     3,
       3,     2,     3,     4,     4,     3,     4,     4,     5,     5,
       4,     4,     3,     3,     7,     5,     5,     5,     3,     3,
       1,     1,     1,     2,     2,     2,     2,     2,     2,     2,
       2,     3,     3,     3,     3,     1,     1,     2,     0,     3,
       4,     3,     1,     1,     1,     3,     3,     1,     1,     3,
       3,     2,     2,     2,     1,     3,     1,     1,     1,     1,
       1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
       1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
       1,     2,     0
};

/* YYDEFACT[STATE-NAME] -- Default reduction number in state STATE-NUM.
   Performed when YYTABLE doesn't specify something else to do.  Zero
   means the default is an error.  */
static const yytype_uint8 yydefact[] =
{
       5,     0,     0,     1,     4,     0,     0,    90,    31,     0,
      32,     0,    45,    46,    89,    88,     0,     0,     0,     0,
      48,     0,    52,     3,    92,    30,    53,    31,    32,    11,
      38,     0,     0,     0,     0,    37,    39,    40,     0,     0,
      48,    66,     0,    70,    69,    79,    87,    78,    72,    86,
      85,    80,    81,    84,    73,    77,    76,    74,    75,    82,
      71,    83,     0,    64,    67,    68,    92,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,    33,    34,    35,
      36,    30,     2,     0,     0,     6,    86,     8,     7,     9,
      44,     0,    47,    51,     0,    49,    63,     0,    91,    23,
      10,    28,     0,     0,     0,    12,    54,    57,     0,     0,
      15,    58,    43,    42,    41,     0,     0,     0,    22,    48,
      29,     0,    50,    70,    69,    65,     0,    61,     0,     0,
      62,     0,     0,    16,    17,     0,    13,    14,    20,    21,
       0,    48,    26,    27,    55,    60,    56,    18,    59,    19,
      25,     0,    24
};

/* YYDEFGOTO[NTERM-NUM].  */
static const yytype_int8 yydefgoto[] =
{
      -1,     1,     2,    23,    80,    39,    25,   105,   106,   110,
     107,    62,    63,    64,    26,    82
};

/* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
   STATE-NUM.  */
#define YYPACT_NINF -109
static const yytype_int16 yypact[] =
{
    -109,    11,   183,  -109,  -109,   727,   727,  -109,   -21,   -18,
     -17,    -6,  -109,  -109,  -109,  -109,   727,   727,   727,   727,
     193,   780,  -109,  -109,   151,    -4,  -109,  -109,  -109,   315,
     315,   727,   809,   727,   809,   756,   756,   756,   249,   -10,
     193,  -109,   -20,  -109,  -109,  -109,  -109,  -109,  -109,   838,
    -109,  -109,  -109,  -109,  -109,  -109,  -109,  -109,  -109,  -109,
    -109,  -109,    12,   809,     5,  -109,    25,   727,    17,    36,
     462,   375,   727,   727,   727,   667,   698,  -109,  -109,  -109,
     636,     9,  -109,   193,    42,   315,  -109,  -109,   315,  -109,
    -109,    13,  -109,  -109,    15,  -109,  -109,   860,  -109,   549,
    -109,    21,   727,   491,   282,  -109,  -109,     7,   520,   404,
    -109,  -109,   578,   607,   636,   462,   462,   193,   727,   193,
    -109,   743,  -109,  -109,  -109,  -109,    36,   315,   216,    26,
     727,   491,   433,  -109,  -109,    27,  -109,  -109,   727,   346,
      29,   193,  -109,  -109,  -109,   315,  -109,  -109,  -109,   346,
    -109,    31,  -109
};

/* YYPGOTO[NTERM-NUM].  */
static const yytype_int8 yypgoto[] =
{
    -109,  -109,  -109,  -109,    -2,   -39,     6,  -108,   -98,   -41,
     -69,   -12,  -109,   -22,   -11,    16
};

/* YYTABLE[YYPACT[STATE-NUM]].  What to do in state STATE-NUM.  If
   positive, shift that token.  If negative, reduce the rule which
   number is the opposite.  If YYTABLE_NINF, syntax error.  */
#define YYTABLE_NINF -1
static const yytype_uint8 yytable[] =
{
      24,    92,   111,    29,    30,   129,    83,   136,   137,    31,
      65,     3,    32,    33,    35,    36,    37,    38,    93,   117,
      87,    65,    89,    65,    34,    91,    40,    97,    66,    85,
      81,    88,    84,   146,   100,    81,    81,    94,    65,   111,
     135,    81,    81,    81,    81,    84,    40,   131,     7,   121,
      95,    96,    65,   122,     7,    14,    15,   126,   101,   144,
     148,    14,    15,   135,   150,    99,   152,   133,   104,   104,
     112,   113,   114,    36,    37,   125,   119,     0,     0,    21,
     140,    22,    98,     0,     0,     0,    81,     0,     0,   118,
     120,    81,     0,     0,    81,     0,     0,     0,     0,     0,
     127,   128,   151,     0,     0,    81,   104,   128,     0,     0,
      81,     0,     0,   104,   104,   143,   139,     0,    81,    81,
      81,     0,     0,   138,     0,    40,     0,   142,   145,   104,
     128,     0,     0,    81,    81,     0,   149,     0,     0,     0,
       0,     0,     0,     0,     0,    81,     0,    40,     0,     0,
       0,    81,     0,     0,    66,    81,    67,    68,     0,    69,
       6,    70,    71,     7,    27,     0,    28,     0,    12,    13,
      14,    15,    72,    73,    74,     0,    16,    75,    76,    77,
      78,     0,    79,    19,     0,    20,     4,     5,    21,     0,
      22,     0,     6,     0,     0,     7,     8,     9,    10,    11,
      12,    13,    14,    15,     0,     7,     0,     0,    16,    17,
      18,     0,    14,    15,     0,    19,     0,    20,     0,     0,
      21,    67,    22,   130,    69,     6,    70,    71,     7,    27,
      21,    28,    22,    12,    13,    14,    15,    72,    73,    74,
       0,    16,    75,    76,    77,    78,     0,    79,    19,    90,
      20,     0,     0,    21,    67,    22,     0,    69,     6,    70,
      71,     7,    27,     0,    28,     0,    12,    13,    14,    15,
      72,    73,    74,     0,    16,    75,    76,    77,    78,     0,
      79,    19,    90,    20,     0,     0,    21,    67,    22,   130,
      69,     6,    70,    71,     7,    27,     0,    28,     0,    12,
      13,    14,    15,    72,    73,    74,     0,    16,    75,    76,
      77,    78,     0,    79,    19,     0,    20,     0,     0,    21,
      67,    22,     0,    69,     6,    70,    71,     7,    27,     0,
      28,     0,    12,    13,    14,    15,    72,    73,    74,     0,
      16,    75,    76,    77,    78,     0,    79,    19,     0,    20,
       0,    67,    21,     0,    22,     6,     0,     0,     7,    27,
       0,    28,     0,    12,    13,    14,    15,    72,    73,    74,
       0,    16,    75,    76,    77,    78,     0,    79,    19,     0,
      20,     0,   102,    21,     6,    22,     0,     7,    27,     0,
      28,     0,    12,    13,    14,    15,     0,     0,     0,     0,
      16,    17,    18,     0,     0,     0,   108,   109,     0,    20,
       0,   102,    21,     6,    22,     0,     7,    27,     0,    28,
       0,    12,    13,    14,    15,     0,     0,     0,     0,    16,
      17,    18,     0,     0,     0,     0,    19,   134,    20,     0,
     102,    21,     6,    22,     0,     7,    27,     0,    28,     0,
      12,    13,    14,    15,     0,     0,     0,     0,    16,    17,
      18,     0,     0,     0,     0,    19,   147,    20,     0,   102,
      21,     6,    22,     0,     7,    27,     0,    28,     0,    12,
      13,    14,    15,     0,     0,     0,     0,    16,    17,    18,
       0,     0,     0,     0,   103,     0,    20,     0,   102,    21,
       6,    22,     0,     7,    27,     0,    28,     0,    12,    13,
      14,    15,     0,     0,     0,     0,    16,    17,    18,     0,
       0,     0,     0,    19,     0,    20,     0,   102,    21,     6,
      22,     0,     7,    27,     0,    28,     0,    12,    13,    14,
      15,     0,     0,     0,     0,    16,    17,    18,     0,     0,
       0,     0,   132,     0,    20,     0,     0,    21,     6,    22,
       0,     7,    27,     0,    28,     0,    12,    13,    14,    15,
      72,    73,    74,     0,    16,    75,    76,    77,    78,     0,
      79,    19,     0,    20,     0,     0,    21,     6,    22,     0,
       7,    27,     0,    28,     0,    12,    13,    14,    15,     0,
      73,    74,     0,    16,    75,    76,    77,    78,     0,    79,
      19,     0,    20,     0,     0,    21,     6,    22,     0,     7,
      27,     0,    28,     0,    12,    13,    14,    15,     0,     0,
      74,     0,    16,    75,    76,    77,    78,     0,    79,    19,
       0,    20,     0,     0,    21,     6,    22,     0,     7,    27,
       0,    28,     0,    12,    13,    14,    15,     0,     0,     0,
       0,    16,    75,    76,    77,    78,     0,    79,    19,     0,
      20,     0,     0,    21,     0,    22,     6,   115,     0,     7,
      27,     0,    28,     0,    12,    13,    14,    15,     0,     0,
       0,     0,    16,    17,    18,     0,     0,     0,     0,    19,
       0,    20,     0,     0,    21,     0,    22,     6,   116,     0,
       7,    27,     0,    28,     0,    12,    13,    14,    15,     0,
       0,     0,     0,    16,    17,    18,     0,     0,     0,     0,
      19,     0,    20,     0,     0,    21,     6,    22,     0,     7,
      27,     0,    28,     0,    12,    13,    14,    15,     0,     0,
       0,     0,    16,    17,    18,     7,     0,     0,     0,    19,
       0,    20,    14,    15,    21,     6,    22,     0,     7,    27,
       0,    28,     0,    12,    13,    14,    15,   141,     0,     0,
      21,     0,    22,     0,    77,    78,     0,    79,    19,     0,
      20,     0,     7,    21,    41,    22,    42,     0,     0,    43,
      44,    45,    46,    47,     0,    48,    49,    50,    51,    52,
      53,    54,    55,    56,    57,    58,    59,     0,     0,    60,
      61,     7,     0,    41,     0,     0,     0,     0,    43,    44,
      45,    46,    47,     0,    48,    86,    50,    51,    52,    53,
      54,    55,    56,    57,    58,    59,     0,     0,    60,    61,
       7,     0,    41,     0,     0,     0,     0,    43,    44,     0,
       0,     0,     0,     0,     0,     0,    51,    52,    53,    54,
      55,    56,    57,    58,    59,     0,     0,    60,    61,   123,
     124,    45,    46,    47,     0,    48,    86,    50,    51,    52,
      53,    54,    55,    56,    57,    58,    59,     0,     0,    60,
      61
};

#define yypact_value_is_default(Yystate) \
  (!!((Yystate) == (-109)))

#define yytable_value_is_error(Yytable_value) \
  YYID (0)

static const yytype_int16 yycheck[] =
{
       2,    40,    71,     5,     6,   103,    10,   115,   116,    30,
      21,     0,    30,    30,    16,    17,    18,    19,    38,    10,
      32,    32,    34,    34,    30,    35,    20,    22,     3,    31,
      24,    33,    36,   131,    17,    29,    30,    49,    49,   108,
     109,    35,    36,    37,    38,    36,    40,    40,    12,    36,
      38,    63,    63,    38,    12,    19,    20,    36,    69,    33,
      33,    19,    20,   132,    35,    67,    35,   108,    70,    71,
      72,    73,    74,    75,    76,    97,    34,    -1,    -1,    37,
     119,    39,    66,    -1,    -1,    -1,    80,    -1,    -1,    83,
      84,    85,    -1,    -1,    88,    -1,    -1,    -1,    -1,    -1,
     102,   103,   141,    -1,    -1,    99,   108,   109,    -1,    -1,
     104,    -1,    -1,   115,   116,   126,   118,    -1,   112,   113,
     114,    -1,    -1,   117,    -1,   119,    -1,   121,   130,   131,
     132,    -1,    -1,   127,   128,    -1,   138,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,   139,    -1,   141,    -1,    -1,
      -1,   145,    -1,    -1,     3,   149,     5,     6,    -1,     8,
       9,    10,    11,    12,    13,    -1,    15,    -1,    17,    18,
      19,    20,    21,    22,    23,    -1,    25,    26,    27,    28,
      29,    -1,    31,    32,    -1,    34,     3,     4,    37,    -1,
      39,    -1,     9,    -1,    -1,    12,    13,    14,    15,    16,
      17,    18,    19,    20,    -1,    12,    -1,    -1,    25,    26,
      27,    -1,    19,    20,    -1,    32,    -1,    34,    -1,    -1,
      37,     5,    39,     7,     8,     9,    10,    11,    12,    13,
      37,    15,    39,    17,    18,    19,    20,    21,    22,    23,
      -1,    25,    26,    27,    28,    29,    -1,    31,    32,    33,
      34,    -1,    -1,    37,     5,    39,    -1,     8,     9,    10,
      11,    12,    13,    -1,    15,    -1,    17,    18,    19,    20,
      21,    22,    23,    -1,    25,    26,    27,    28,    29,    -1,
      31,    32,    33,    34,    -1,    -1,    37,     5,    39,     7,
       8,     9,    10,    11,    12,    13,    -1,    15,    -1,    17,
      18,    19,    20,    21,    22,    23,    -1,    25,    26,    27,
      28,    29,    -1,    31,    32,    -1,    34,    -1,    -1,    37,
       5,    39,    -1,     8,     9,    10,    11,    12,    13,    -1,
      15,    -1,    17,    18,    19,    20,    21,    22,    23,    -1,
      25,    26,    27,    28,    29,    -1,    31,    32,    -1,    34,
      -1,     5,    37,    -1,    39,     9,    -1,    -1,    12,    13,
      -1,    15,    -1,    17,    18,    19,    20,    21,    22,    23,
      -1,    25,    26,    27,    28,    29,    -1,    31,    32,    -1,
      34,    -1,     7,    37,     9,    39,    -1,    12,    13,    -1,
      15,    -1,    17,    18,    19,    20,    -1,    -1,    -1,    -1,
      25,    26,    27,    -1,    -1,    -1,    31,    32,    -1,    34,
      -1,     7,    37,     9,    39,    -1,    12,    13,    -1,    15,
      -1,    17,    18,    19,    20,    -1,    -1,    -1,    -1,    25,
      26,    27,    -1,    -1,    -1,    -1,    32,    33,    34,    -1,
       7,    37,     9,    39,    -1,    12,    13,    -1,    15,    -1,
      17,    18,    19,    20,    -1,    -1,    -1,    -1,    25,    26,
      27,    -1,    -1,    -1,    -1,    32,    33,    34,    -1,     7,
      37,     9,    39,    -1,    12,    13,    -1,    15,    -1,    17,
      18,    19,    20,    -1,    -1,    -1,    -1,    25,    26,    27,
      -1,    -1,    -1,    -1,    32,    -1,    34,    -1,     7,    37,
       9,    39,    -1,    12,    13,    -1,    15,    -1,    17,    18,
      19,    20,    -1,    -1,    -1,    -1,    25,    26,    27,    -1,
      -1,    -1,    -1,    32,    -1,    34,    -1,     7,    37,     9,
      39,    -1,    12,    13,    -1,    15,    -1,    17,    18,    19,
      20,    -1,    -1,    -1,    -1,    25,    26,    27,    -1,    -1,
      -1,    -1,    32,    -1,    34,    -1,    -1,    37,     9,    39,
      -1,    12,    13,    -1,    15,    -1,    17,    18,    19,    20,
      21,    22,    23,    -1,    25,    26,    27,    28,    29,    -1,
      31,    32,    -1,    34,    -1,    -1,    37,     9,    39,    -1,
      12,    13,    -1,    15,    -1,    17,    18,    19,    20,    -1,
      22,    23,    -1,    25,    26,    27,    28,    29,    -1,    31,
      32,    -1,    34,    -1,    -1,    37,     9,    39,    -1,    12,
      13,    -1,    15,    -1,    17,    18,    19,    20,    -1,    -1,
      23,    -1,    25,    26,    27,    28,    29,    -1,    31,    32,
      -1,    34,    -1,    -1,    37,     9,    39,    -1,    12,    13,
      -1,    15,    -1,    17,    18,    19,    20,    -1,    -1,    -1,
      -1,    25,    26,    27,    28,    29,    -1,    31,    32,    -1,
      34,    -1,    -1,    37,    -1,    39,     9,    10,    -1,    12,
      13,    -1,    15,    -1,    17,    18,    19,    20,    -1,    -1,
      -1,    -1,    25,    26,    27,    -1,    -1,    -1,    -1,    32,
      -1,    34,    -1,    -1,    37,    -1,    39,     9,    10,    -1,
      12,    13,    -1,    15,    -1,    17,    18,    19,    20,    -1,
      -1,    -1,    -1,    25,    26,    27,    -1,    -1,    -1,    -1,
      32,    -1,    34,    -1,    -1,    37,     9,    39,    -1,    12,
      13,    -1,    15,    -1,    17,    18,    19,    20,    -1,    -1,
      -1,    -1,    25,    26,    27,    12,    -1,    -1,    -1,    32,
      -1,    34,    19,    20,    37,     9,    39,    -1,    12,    13,
      -1,    15,    -1,    17,    18,    19,    20,    34,    -1,    -1,
      37,    -1,    39,    -1,    28,    29,    -1,    31,    32,    -1,
      34,    -1,    12,    37,    14,    39,    16,    -1,    -1,    19,
      20,    21,    22,    23,    -1,    25,    26,    27,    28,    29,
      30,    31,    32,    33,    34,    35,    36,    -1,    -1,    39,
      40,    12,    -1,    14,    -1,    -1,    -1,    -1,    19,    20,
      21,    22,    23,    -1,    25,    26,    27,    28,    29,    30,
      31,    32,    33,    34,    35,    36,    -1,    -1,    39,    40,
      12,    -1,    14,    -1,    -1,    -1,    -1,    19,    20,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    28,    29,    30,    31,
      32,    33,    34,    35,    36,    -1,    -1,    39,    40,    19,
      20,    21,    22,    23,    -1,    25,    26,    27,    28,    29,
      30,    31,    32,    33,    34,    35,    36,    -1,    -1,    39,
      40
};

/* YYSTOS[STATE-NUM] -- The (internal number of the) accessing
   symbol of state STATE-NUM.  */
static const yytype_uint8 yystos[] =
{
       0,    42,    43,     0,     3,     4,     9,    12,    13,    14,
      15,    16,    17,    18,    19,    20,    25,    26,    27,    32,
      34,    37,    39,    44,    45,    47,    55,    13,    15,    45,
      45,    30,    30,    30,    30,    45,    45,    45,    45,    46,
      47,    14,    16,    19,    20,    21,    22,    23,    25,    26,
      27,    28,    29,    30,    31,    32,    33,    34,    35,    36,
      39,    40,    52,    53,    54,    55,     3,     5,     6,     8,
      10,    11,    21,    22,    23,    26,    27,    28,    29,    31,
      45,    47,    56,    10,    36,    45,    26,    52,    45,    52,
      33,    35,    46,    38,    52,    38,    52,    22,    56,    45,
      17,    55,     7,    32,    45,    48,    49,    51,    31,    32,
      50,    51,    45,    45,    45,    10,    10,    10,    47,    34,
      47,    36,    38,    19,    20,    54,    36,    45,    45,    49,
       7,    40,    32,    50,    33,    51,    48,    48,    47,    45,
      46,    34,    47,    55,    33,    45,    49,    33,    33,    45,
      35,    46,    35
};

#define yyerrok		(yyerrstatus = 0)
#define yyclearin	(yychar = YYEMPTY)
#define YYEMPTY		(-2)
#define YYEOF		0

#define YYACCEPT	goto yyacceptlab
#define YYABORT		goto yyabortlab
#define YYERROR		goto yyerrorlab


/* Like YYERROR except do call yyerror.  This remains here temporarily
   to ease the transition to the new meaning of YYERROR, for GCC.
   Once GCC version 2 has supplanted version 1, this can go.  However,
   YYFAIL appears to be in use.  Nevertheless, it is formally deprecated
   in Bison 2.4.2's NEWS entry, where a plan to phase it out is
   discussed.  */

#define YYFAIL		goto yyerrlab
#if defined YYFAIL
  /* This is here to suppress warnings from the GCC cpp's
     -Wunused-macros.  Normally we don't worry about that warning, but
     some users do, and we want to make it easy for users to remove
     YYFAIL uses, which will produce warnings from Bison 2.5.  */
#endif

#define YYRECOVERING()  (!!yyerrstatus)

#define YYBACKUP(Token, Value)                                  \
do                                                              \
  if (yychar == YYEMPTY)                                        \
    {                                                           \
      yychar = (Token);                                         \
      yylval = (Value);                                         \
      YYPOPSTACK (yylen);                                       \
      yystate = *yyssp;                                         \
      goto yybackup;                                            \
    }                                                           \
  else                                                          \
    {                                                           \
      yyerror (YY_("syntax error: cannot back up")); \
      YYERROR;							\
    }								\
while (YYID (0))

/* Error token number */
#define YYTERROR	1
#define YYERRCODE	256


/* This macro is provided for backward compatibility. */
#ifndef YY_LOCATION_PRINT
# define YY_LOCATION_PRINT(File, Loc) ((void) 0)
#endif


/* YYLEX -- calling `yylex' with the right arguments.  */
#ifdef YYLEX_PARAM
# define YYLEX yylex (YYLEX_PARAM)
#else
# define YYLEX yylex ()
#endif

/* Enable debugging if requested.  */
#if YYDEBUG

# ifndef YYFPRINTF
#  include <stdio.h> /* INFRINGES ON USER NAME SPACE */
#  define YYFPRINTF fprintf
# endif

# define YYDPRINTF(Args)			\
do {						\
  if (yydebug)					\
    YYFPRINTF Args;				\
} while (YYID (0))

# define YY_SYMBOL_PRINT(Title, Type, Value, Location)			  \
do {									  \
  if (yydebug)								  \
    {									  \
      YYFPRINTF (stderr, "%s ", Title);					  \
      yy_symbol_print (stderr,						  \
		  Type, Value); \
      YYFPRINTF (stderr, "\n");						  \
    }									  \
} while (YYID (0))


/*--------------------------------.
| Print this symbol on YYOUTPUT.  |
`--------------------------------*/

/*ARGSUSED*/
#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static void
yy_symbol_value_print (FILE *yyoutput, int yytype, YYSTYPE const * const yyvaluep)
#else
static void
yy_symbol_value_print (yyoutput, yytype, yyvaluep)
    FILE *yyoutput;
    int yytype;
    YYSTYPE const * const yyvaluep;
#endif
{
  FILE *yyo = yyoutput;
  YYUSE (yyo);
  if (!yyvaluep)
    return;
# ifdef YYPRINT
  if (yytype < YYNTOKENS)
    YYPRINT (yyoutput, yytoknum[yytype], *yyvaluep);
# else
  YYUSE (yyoutput);
# endif
  switch (yytype)
    {
      default:
        break;
    }
}


/*--------------------------------.
| Print this symbol on YYOUTPUT.  |
`--------------------------------*/

#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static void
yy_symbol_print (FILE *yyoutput, int yytype, YYSTYPE const * const yyvaluep)
#else
static void
yy_symbol_print (yyoutput, yytype, yyvaluep)
    FILE *yyoutput;
    int yytype;
    YYSTYPE const * const yyvaluep;
#endif
{
  if (yytype < YYNTOKENS)
    YYFPRINTF (yyoutput, "token %s (", yytname[yytype]);
  else
    YYFPRINTF (yyoutput, "nterm %s (", yytname[yytype]);

  yy_symbol_value_print (yyoutput, yytype, yyvaluep);
  YYFPRINTF (yyoutput, ")");
}

/*------------------------------------------------------------------.
| yy_stack_print -- Print the state stack from its BOTTOM up to its |
| TOP (included).                                                   |
`------------------------------------------------------------------*/

#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static void
yy_stack_print (yytype_int16 *yybottom, yytype_int16 *yytop)
#else
static void
yy_stack_print (yybottom, yytop)
    yytype_int16 *yybottom;
    yytype_int16 *yytop;
#endif
{
  YYFPRINTF (stderr, "Stack now");
  for (; yybottom <= yytop; yybottom++)
    {
      int yybot = *yybottom;
      YYFPRINTF (stderr, " %d", yybot);
    }
  YYFPRINTF (stderr, "\n");
}

# define YY_STACK_PRINT(Bottom, Top)				\
do {								\
  if (yydebug)							\
    yy_stack_print ((Bottom), (Top));				\
} while (YYID (0))


/*------------------------------------------------.
| Report that the YYRULE is going to be reduced.  |
`------------------------------------------------*/

#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static void
yy_reduce_print (YYSTYPE *yyvsp, int yyrule)
#else
static void
yy_reduce_print (yyvsp, yyrule)
    YYSTYPE *yyvsp;
    int yyrule;
#endif
{
  int yynrhs = yyr2[yyrule];
  int yyi;
  unsigned long int yylno = yyrline[yyrule];
  YYFPRINTF (stderr, "Reducing stack by rule %d (line %lu):\n",
	     yyrule - 1, yylno);
  /* The symbols being reduced.  */
  for (yyi = 0; yyi < yynrhs; yyi++)
    {
      YYFPRINTF (stderr, "   $%d = ", yyi + 1);
      yy_symbol_print (stderr, yyrhs[yyprhs[yyrule] + yyi],
		       &(yyvsp[(yyi + 1) - (yynrhs)])
		       		       );
      YYFPRINTF (stderr, "\n");
    }
}

# define YY_REDUCE_PRINT(Rule)		\
do {					\
  if (yydebug)				\
    yy_reduce_print (yyvsp, Rule); \
} while (YYID (0))

/* Nonzero means print parse trace.  It is left uninitialized so that
   multiple parsers can coexist.  */
int yydebug;
#else /* !YYDEBUG */
# define YYDPRINTF(Args)
# define YY_SYMBOL_PRINT(Title, Type, Value, Location)
# define YY_STACK_PRINT(Bottom, Top)
# define YY_REDUCE_PRINT(Rule)
#endif /* !YYDEBUG */


/* YYINITDEPTH -- initial size of the parser's stacks.  */
#ifndef	YYINITDEPTH
# define YYINITDEPTH 200
#endif

/* YYMAXDEPTH -- maximum size the stacks can grow to (effective only
   if the built-in stack extension method is used).

   Do not make this value too large; the results are undefined if
   YYSTACK_ALLOC_MAXIMUM < YYSTACK_BYTES (YYMAXDEPTH)
   evaluated with infinite-precision integer arithmetic.  */

#ifndef YYMAXDEPTH
# define YYMAXDEPTH 10000
#endif


#if YYERROR_VERBOSE

# ifndef yystrlen
#  if defined __GLIBC__ && defined _STRING_H
#   define yystrlen strlen
#  else
/* Return the length of YYSTR.  */
#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static YYSIZE_T
yystrlen (const char *yystr)
#else
static YYSIZE_T
yystrlen (yystr)
    const char *yystr;
#endif
{
  YYSIZE_T yylen;
  for (yylen = 0; yystr[yylen]; yylen++)
    continue;
  return yylen;
}
#  endif
# endif

# ifndef yystpcpy
#  if defined __GLIBC__ && defined _STRING_H && defined _GNU_SOURCE
#   define yystpcpy stpcpy
#  else
/* Copy YYSRC to YYDEST, returning the address of the terminating '\0' in
   YYDEST.  */
#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static char *
yystpcpy (char *yydest, const char *yysrc)
#else
static char *
yystpcpy (yydest, yysrc)
    char *yydest;
    const char *yysrc;
#endif
{
  char *yyd = yydest;
  const char *yys = yysrc;

  while ((*yyd++ = *yys++) != '\0')
    continue;

  return yyd - 1;
}
#  endif
# endif

# ifndef yytnamerr
/* Copy to YYRES the contents of YYSTR after stripping away unnecessary
   quotes and backslashes, so that it's suitable for yyerror.  The
   heuristic is that double-quoting is unnecessary unless the string
   contains an apostrophe, a comma, or backslash (other than
   backslash-backslash).  YYSTR is taken from yytname.  If YYRES is
   null, do not copy; instead, return the length of what the result
   would have been.  */
static YYSIZE_T
yytnamerr (char *yyres, const char *yystr)
{
  if (*yystr == '"')
    {
      YYSIZE_T yyn = 0;
      char const *yyp = yystr;

      for (;;)
	switch (*++yyp)
	  {
	  case '\'':
	  case ',':
	    goto do_not_strip_quotes;

	  case '\\':
	    if (*++yyp != '\\')
	      goto do_not_strip_quotes;
	    /* Fall through.  */
	  default:
	    if (yyres)
	      yyres[yyn] = *yyp;
	    yyn++;
	    break;

	  case '"':
	    if (yyres)
	      yyres[yyn] = '\0';
	    return yyn;
	  }
    do_not_strip_quotes: ;
    }

  if (! yyres)
    return yystrlen (yystr);

  return yystpcpy (yyres, yystr) - yyres;
}
# endif

/* Copy into *YYMSG, which is of size *YYMSG_ALLOC, an error message
   about the unexpected token YYTOKEN for the state stack whose top is
   YYSSP.

   Return 0 if *YYMSG was successfully written.  Return 1 if *YYMSG is
   not large enough to hold the message.  In that case, also set
   *YYMSG_ALLOC to the required number of bytes.  Return 2 if the
   required number of bytes is too large to store.  */
static int
yysyntax_error (YYSIZE_T *yymsg_alloc, char **yymsg,
                yytype_int16 *yyssp, int yytoken)
{
  YYSIZE_T yysize0 = yytnamerr (YY_NULL, yytname[yytoken]);
  YYSIZE_T yysize = yysize0;
  enum { YYERROR_VERBOSE_ARGS_MAXIMUM = 5 };
  /* Internationalized format string. */
  const char *yyformat = YY_NULL;
  /* Arguments of yyformat. */
  char const *yyarg[YYERROR_VERBOSE_ARGS_MAXIMUM];
  /* Number of reported tokens (one for the "unexpected", one per
     "expected"). */
  int yycount = 0;

  /* There are many possibilities here to consider:
     - Assume YYFAIL is not used.  It's too flawed to consider.  See
       <http://lists.gnu.org/archive/html/bison-patches/2009-12/msg00024.html>
       for details.  YYERROR is fine as it does not invoke this
       function.
     - If this state is a consistent state with a default action, then
       the only way this function was invoked is if the default action
       is an error action.  In that case, don't check for expected
       tokens because there are none.
     - The only way there can be no lookahead present (in yychar) is if
       this state is a consistent state with a default action.  Thus,
       detecting the absence of a lookahead is sufficient to determine
       that there is no unexpected or expected token to report.  In that
       case, just report a simple "syntax error".
     - Don't assume there isn't a lookahead just because this state is a
       consistent state with a default action.  There might have been a
       previous inconsistent state, consistent state with a non-default
       action, or user semantic action that manipulated yychar.
     - Of course, the expected token list depends on states to have
       correct lookahead information, and it depends on the parser not
       to perform extra reductions after fetching a lookahead from the
       scanner and before detecting a syntax error.  Thus, state merging
       (from LALR or IELR) and default reductions corrupt the expected
       token list.  However, the list is correct for canonical LR with
       one exception: it will still contain any token that will not be
       accepted due to an error action in a later state.
  */
  if (yytoken != YYEMPTY)
    {
      int yyn = yypact[*yyssp];
      yyarg[yycount++] = yytname[yytoken];
      if (!yypact_value_is_default (yyn))
        {
          /* Start YYX at -YYN if negative to avoid negative indexes in
             YYCHECK.  In other words, skip the first -YYN actions for
             this state because they are default actions.  */
          int yyxbegin = yyn < 0 ? -yyn : 0;
          /* Stay within bounds of both yycheck and yytname.  */
          int yychecklim = YYLAST - yyn + 1;
          int yyxend = yychecklim < YYNTOKENS ? yychecklim : YYNTOKENS;
          int yyx;

          for (yyx = yyxbegin; yyx < yyxend; ++yyx)
            if (yycheck[yyx + yyn] == yyx && yyx != YYTERROR
                && !yytable_value_is_error (yytable[yyx + yyn]))
              {
                if (yycount == YYERROR_VERBOSE_ARGS_MAXIMUM)
                  {
                    yycount = 1;
                    yysize = yysize0;
                    break;
                  }
                yyarg[yycount++] = yytname[yyx];
                {
                  YYSIZE_T yysize1 = yysize + yytnamerr (YY_NULL, yytname[yyx]);
                  if (! (yysize <= yysize1
                         && yysize1 <= YYSTACK_ALLOC_MAXIMUM))
                    return 2;
                  yysize = yysize1;
                }
              }
        }
    }

  switch (yycount)
    {
# define YYCASE_(N, S)                      \
      case N:                               \
        yyformat = S;                       \
      break
      YYCASE_(0, YY_("syntax error"));
      YYCASE_(1, YY_("syntax error, unexpected %s"));
      YYCASE_(2, YY_("syntax error, unexpected %s, expecting %s"));
      YYCASE_(3, YY_("syntax error, unexpected %s, expecting %s or %s"));
      YYCASE_(4, YY_("syntax error, unexpected %s, expecting %s or %s or %s"));
      YYCASE_(5, YY_("syntax error, unexpected %s, expecting %s or %s or %s or %s"));
# undef YYCASE_
    }

  {
    YYSIZE_T yysize1 = yysize + yystrlen (yyformat);
    if (! (yysize <= yysize1 && yysize1 <= YYSTACK_ALLOC_MAXIMUM))
      return 2;
    yysize = yysize1;
  }

  if (*yymsg_alloc < yysize)
    {
      *yymsg_alloc = 2 * yysize;
      if (! (yysize <= *yymsg_alloc
             && *yymsg_alloc <= YYSTACK_ALLOC_MAXIMUM))
        *yymsg_alloc = YYSTACK_ALLOC_MAXIMUM;
      return 1;
    }

  /* Avoid sprintf, as that infringes on the user's name space.
     Don't have undefined behavior even if the translation
     produced a string with the wrong number of "%s"s.  */
  {
    char *yyp = *yymsg;
    int yyi = 0;
    while ((*yyp = *yyformat) != '\0')
      if (*yyp == '%' && yyformat[1] == 's' && yyi < yycount)
        {
          yyp += yytnamerr (yyp, yyarg[yyi++]);
          yyformat += 2;
        }
      else
        {
          yyp++;
          yyformat++;
        }
  }
  return 0;
}
#endif /* YYERROR_VERBOSE */

/*-----------------------------------------------.
| Release the memory associated to this symbol.  |
`-----------------------------------------------*/

/*ARGSUSED*/
#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static void
yydestruct (const char *yymsg, int yytype, YYSTYPE *yyvaluep)
#else
static void
yydestruct (yymsg, yytype, yyvaluep)
    const char *yymsg;
    int yytype;
    YYSTYPE *yyvaluep;
#endif
{
  YYUSE (yyvaluep);

  if (!yymsg)
    yymsg = "Deleting";
  YY_SYMBOL_PRINT (yymsg, yytype, yyvaluep, yylocationp);

  switch (yytype)
    {

      default:
        break;
    }
}




/* The lookahead symbol.  */
int yychar;


#ifndef YY_IGNORE_MAYBE_UNINITIALIZED_BEGIN
# define YY_IGNORE_MAYBE_UNINITIALIZED_BEGIN
# define YY_IGNORE_MAYBE_UNINITIALIZED_END
#endif
#ifndef YY_INITIAL_VALUE
# define YY_INITIAL_VALUE(Value) /* Nothing. */
#endif

/* The semantic value of the lookahead symbol.  */
YYSTYPE yylval YY_INITIAL_VALUE(yyval_default);

/* Number of syntax errors so far.  */
int yynerrs;


/*----------.
| yyparse.  |
`----------*/

#ifdef YYPARSE_PARAM
#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
int
yyparse (void *YYPARSE_PARAM)
#else
int
yyparse (YYPARSE_PARAM)
    void *YYPARSE_PARAM;
#endif
#else /* ! YYPARSE_PARAM */
#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
int
yyparse (void)
#else
int
yyparse ()

#endif
#endif
{
    int yystate;
    /* Number of tokens to shift before error messages enabled.  */
    int yyerrstatus;

    /* The stacks and their tools:
       `yyss': related to states.
       `yyvs': related to semantic values.

       Refer to the stacks through separate pointers, to allow yyoverflow
       to reallocate them elsewhere.  */

    /* The state stack.  */
    yytype_int16 yyssa[YYINITDEPTH];
    yytype_int16 *yyss;
    yytype_int16 *yyssp;

    /* The semantic value stack.  */
    YYSTYPE yyvsa[YYINITDEPTH];
    YYSTYPE *yyvs;
    YYSTYPE *yyvsp;

    YYSIZE_T yystacksize;

  int yyn;
  int yyresult;
  /* Lookahead token as an internal (translated) token number.  */
  int yytoken = 0;
  /* The variables used to return semantic value and location from the
     action routines.  */
  YYSTYPE yyval;

#if YYERROR_VERBOSE
  /* Buffer for error messages, and its allocated size.  */
  char yymsgbuf[128];
  char *yymsg = yymsgbuf;
  YYSIZE_T yymsg_alloc = sizeof yymsgbuf;
#endif

#define YYPOPSTACK(N)   (yyvsp -= (N), yyssp -= (N))

  /* The number of symbols on the RHS of the reduced rule.
     Keep to zero when no symbol should be popped.  */
  int yylen = 0;

  yyssp = yyss = yyssa;
  yyvsp = yyvs = yyvsa;
  yystacksize = YYINITDEPTH;

  YYDPRINTF ((stderr, "Starting parse\n"));

  yystate = 0;
  yyerrstatus = 0;
  yynerrs = 0;
  yychar = YYEMPTY; /* Cause a token to be read.  */
  goto yysetstate;

/*------------------------------------------------------------.
| yynewstate -- Push a new state, which is found in yystate.  |
`------------------------------------------------------------*/
 yynewstate:
  /* In all cases, when you get here, the value and location stacks
     have just been pushed.  So pushing a state here evens the stacks.  */
  yyssp++;

 yysetstate:
  *yyssp = yystate;

  if (yyss + yystacksize - 1 <= yyssp)
    {
      /* Get the current used size of the three stacks, in elements.  */
      YYSIZE_T yysize = yyssp - yyss + 1;

#ifdef yyoverflow
      {
	/* Give user a chance to reallocate the stack.  Use copies of
	   these so that the &'s don't force the real ones into
	   memory.  */
	YYSTYPE *yyvs1 = yyvs;
	yytype_int16 *yyss1 = yyss;

	/* Each stack pointer address is followed by the size of the
	   data in use in that stack, in bytes.  This used to be a
	   conditional around just the two extra args, but that might
	   be undefined if yyoverflow is a macro.  */
	yyoverflow (YY_("memory exhausted"),
		    &yyss1, yysize * sizeof (*yyssp),
		    &yyvs1, yysize * sizeof (*yyvsp),
		    &yystacksize);

	yyss = yyss1;
	yyvs = yyvs1;
      }
#else /* no yyoverflow */
# ifndef YYSTACK_RELOCATE
      goto yyexhaustedlab;
# else
      /* Extend the stack our own way.  */
      if (YYMAXDEPTH <= yystacksize)
	goto yyexhaustedlab;
      yystacksize *= 2;
      if (YYMAXDEPTH < yystacksize)
	yystacksize = YYMAXDEPTH;

      {
	yytype_int16 *yyss1 = yyss;
	union yyalloc *yyptr =
	  (union yyalloc *) YYSTACK_ALLOC (YYSTACK_BYTES (yystacksize));
	if (! yyptr)
	  goto yyexhaustedlab;
	YYSTACK_RELOCATE (yyss_alloc, yyss);
	YYSTACK_RELOCATE (yyvs_alloc, yyvs);
#  undef YYSTACK_RELOCATE
	if (yyss1 != yyssa)
	  YYSTACK_FREE (yyss1);
      }
# endif
#endif /* no yyoverflow */

      yyssp = yyss + yysize - 1;
      yyvsp = yyvs + yysize - 1;

      YYDPRINTF ((stderr, "Stack size increased to %lu\n",
		  (unsigned long int) yystacksize));

      if (yyss + yystacksize - 1 <= yyssp)
	YYABORT;
    }

  YYDPRINTF ((stderr, "Entering state %d\n", yystate));

  if (yystate == YYFINAL)
    YYACCEPT;

  goto yybackup;

/*-----------.
| yybackup.  |
`-----------*/
yybackup:

  /* Do appropriate processing given the current state.  Read a
     lookahead token if we need one and don't already have one.  */

  /* First try to decide what to do without reference to lookahead token.  */
  yyn = yypact[yystate];
  if (yypact_value_is_default (yyn))
    goto yydefault;

  /* Not known => get a lookahead token if don't already have one.  */

  /* YYCHAR is either YYEMPTY or YYEOF or a valid lookahead symbol.  */
  if (yychar == YYEMPTY)
    {
      YYDPRINTF ((stderr, "Reading a token: "));
      yychar = YYLEX;
    }

  if (yychar <= YYEOF)
    {
      yychar = yytoken = YYEOF;
      YYDPRINTF ((stderr, "Now at end of input.\n"));
    }
  else
    {
      yytoken = YYTRANSLATE (yychar);
      YY_SYMBOL_PRINT ("Next token is", yytoken, &yylval, &yylloc);
    }

  /* If the proper action on seeing token YYTOKEN is to reduce or to
     detect an error, take that action.  */
  yyn += yytoken;
  if (yyn < 0 || YYLAST < yyn || yycheck[yyn] != yytoken)
    goto yydefault;
  yyn = yytable[yyn];
  if (yyn <= 0)
    {
      if (yytable_value_is_error (yyn))
        goto yyerrlab;
      yyn = -yyn;
      goto yyreduce;
    }

  /* Count tokens shifted since error; after three, turn off error
     status.  */
  if (yyerrstatus)
    yyerrstatus--;

  /* Shift the lookahead token.  */
  YY_SYMBOL_PRINT ("Shifting", yytoken, &yylval, &yylloc);

  /* Discard the shifted token.  */
  yychar = YYEMPTY;

  yystate = yyn;
  YY_IGNORE_MAYBE_UNINITIALIZED_BEGIN
  *++yyvsp = yylval;
  YY_IGNORE_MAYBE_UNINITIALIZED_END

  goto yynewstate;


/*-----------------------------------------------------------.
| yydefault -- do the default action for the current state.  |
`-----------------------------------------------------------*/
yydefault:
  yyn = yydefact[yystate];
  if (yyn == 0)
    goto yyerrlab;
  goto yyreduce;


/*-----------------------------.
| yyreduce -- Do a reduction.  |
`-----------------------------*/
yyreduce:
  /* yyn is the number of a rule to reduce with.  */
  yylen = yyr2[yyn];

  /* If YYLEN is nonzero, implement the default value of the action:
     `$$ = $1'.

     Otherwise, the following line sets YYVAL to garbage.
     This behavior is undocumented and Bison
     users should not rely upon it.  Assigning to YYVAL
     unconditionally makes the parser a bit smaller, and it avoids a
     GCC warning that YYVAL may be used uninitialized.  */
  yyval = yyvsp[1-yylen];


  YY_REDUCE_PRINT (yyn);
  switch (yyn)
    {
        case 2:
/* Line 1792 of yacc.c  */
#line 74 "fst-compiler.yy"
    { Result=interface.result((yyvsp[(2) - (3)].expression), Switch); }
    break;

  case 3:
/* Line 1792 of yacc.c  */
#line 77 "fst-compiler.yy"
    {}
    break;

  case 4:
/* Line 1792 of yacc.c  */
#line 78 "fst-compiler.yy"
    {}
    break;

  case 5:
/* Line 1792 of yacc.c  */
#line 79 "fst-compiler.yy"
    {}
    break;

  case 6:
/* Line 1792 of yacc.c  */
#line 82 "fst-compiler.yy"
    { if (interface.def_var((yyvsp[(1) - (3)].name),(yyvsp[(3) - (3)].expression))) warn2("assignment of empty transducer to",(yyvsp[(1) - (3)].name)); }
    break;

  case 7:
/* Line 1792 of yacc.c  */
#line 83 "fst-compiler.yy"
    { if (interface.def_rvar((yyvsp[(1) - (3)].name),(yyvsp[(3) - (3)].expression))) warn2("assignment of empty transducer to",(yyvsp[(1) - (3)].name)); }
    break;

  case 8:
/* Line 1792 of yacc.c  */
#line 84 "fst-compiler.yy"
    { if (interface.def_svar((yyvsp[(1) - (3)].name),(yyvsp[(3) - (3)].range))) warn2("assignment of empty symbol range to",(yyvsp[(1) - (3)].name)); }
    break;

  case 9:
/* Line 1792 of yacc.c  */
#line 85 "fst-compiler.yy"
    { if (interface.def_svar((yyvsp[(1) - (3)].name),(yyvsp[(3) - (3)].range))) warn2("assignment of empty symbol range to",(yyvsp[(1) - (3)].name)); }
    break;

  case 10:
/* Line 1792 of yacc.c  */
#line 86 "fst-compiler.yy"
    { interface.write_to_file((yyvsp[(1) - (3)].expression), (yyvsp[(3) - (3)].value)); }
    break;

  case 11:
/* Line 1792 of yacc.c  */
#line 87 "fst-compiler.yy"
    { interface.def_alphabet((yyvsp[(2) - (2)].expression)); }
    break;

  case 12:
/* Line 1792 of yacc.c  */
#line 90 "fst-compiler.yy"
    { (yyval.expression) = interface.restriction((yyvsp[(1) - (3)].expression),(yyvsp[(2) - (3)].type),(yyvsp[(3) - (3)].contexts),0); }
    break;

  case 13:
/* Line 1792 of yacc.c  */
#line 91 "fst-compiler.yy"
    { (yyval.expression) = interface.restriction((yyvsp[(1) - (4)].expression),(yyvsp[(3) - (4)].type),(yyvsp[(4) - (4)].contexts),1); }
    break;

  case 14:
/* Line 1792 of yacc.c  */
#line 92 "fst-compiler.yy"
    { (yyval.expression) = interface.restriction((yyvsp[(1) - (4)].expression),(yyvsp[(3) - (4)].type),(yyvsp[(4) - (4)].contexts),-1); }
    break;

  case 15:
/* Line 1792 of yacc.c  */
#line 93 "fst-compiler.yy"
    { (yyval.expression) = interface.replace_in_context(interface.minimise(interface.explode((yyvsp[(1) - (3)].expression))),(yyvsp[(2) - (3)].rtype),(yyvsp[(3) - (3)].contexts),false); }
    break;

  case 16:
/* Line 1792 of yacc.c  */
#line 94 "fst-compiler.yy"
    { (yyval.expression) = interface.replace_in_context(interface.minimise(interface.explode((yyvsp[(1) - (4)].expression))),(yyvsp[(2) - (4)].rtype),(yyvsp[(4) - (4)].contexts),true);}
    break;

  case 17:
/* Line 1792 of yacc.c  */
#line 95 "fst-compiler.yy"
    { (yyval.expression) = interface.replace(interface.minimise(interface.explode((yyvsp[(1) - (4)].expression))), (yyvsp[(2) - (4)].rtype), false); }
    break;

  case 18:
/* Line 1792 of yacc.c  */
#line 96 "fst-compiler.yy"
    { (yyval.expression) = interface.replace(interface.minimise(interface.explode((yyvsp[(1) - (5)].expression))), (yyvsp[(2) - (5)].rtype), true); }
    break;

  case 19:
/* Line 1792 of yacc.c  */
#line 97 "fst-compiler.yy"
    { (yyval.expression) = interface.make_rule((yyvsp[(1) - (5)].expression),(yyvsp[(2) - (5)].range),(yyvsp[(3) - (5)].type),(yyvsp[(4) - (5)].range),(yyvsp[(5) - (5)].expression)); }
    break;

  case 20:
/* Line 1792 of yacc.c  */
#line 98 "fst-compiler.yy"
    { (yyval.expression) = interface.make_rule((yyvsp[(1) - (4)].expression),(yyvsp[(2) - (4)].range),(yyvsp[(3) - (4)].type),(yyvsp[(4) - (4)].range),NULL); }
    break;

  case 21:
/* Line 1792 of yacc.c  */
#line 99 "fst-compiler.yy"
    { (yyval.expression) = interface.make_rule(NULL,(yyvsp[(1) - (4)].range),(yyvsp[(2) - (4)].type),(yyvsp[(3) - (4)].range),(yyvsp[(4) - (4)].expression)); }
    break;

  case 22:
/* Line 1792 of yacc.c  */
#line 100 "fst-compiler.yy"
    { (yyval.expression) = interface.make_rule(NULL,(yyvsp[(1) - (3)].range),(yyvsp[(2) - (3)].type),(yyvsp[(3) - (3)].range),NULL); }
    break;

  case 23:
/* Line 1792 of yacc.c  */
#line 101 "fst-compiler.yy"
    { (yyval.expression) = interface.composition((yyvsp[(1) - (3)].expression), (yyvsp[(3) - (3)].expression)); }
    break;

  case 24:
/* Line 1792 of yacc.c  */
#line 102 "fst-compiler.yy"
    { (yyval.expression) = interface.make_mapping((yyvsp[(2) - (7)].ranges),(yyvsp[(6) - (7)].ranges)); }
    break;

  case 25:
/* Line 1792 of yacc.c  */
#line 103 "fst-compiler.yy"
    { (yyval.expression) = interface.make_mapping(interface.add_range((yyvsp[(1) - (5)].range),NULL),(yyvsp[(4) - (5)].ranges)); }
    break;

  case 26:
/* Line 1792 of yacc.c  */
#line 104 "fst-compiler.yy"
    { (yyval.expression) = interface.make_mapping((yyvsp[(2) - (5)].ranges),interface.add_range((yyvsp[(5) - (5)].range),NULL)); }
    break;

  case 27:
/* Line 1792 of yacc.c  */
#line 105 "fst-compiler.yy"
    { (yyval.expression) = interface.freely_insert((yyvsp[(1) - (5)].expression), (yyvsp[(3) - (5)].character), (yyvsp[(5) - (5)].character)); }
    break;

  case 28:
/* Line 1792 of yacc.c  */
#line 106 "fst-compiler.yy"
    { (yyval.expression) = interface.freely_insert((yyvsp[(1) - (3)].expression), (yyvsp[(3) - (3)].character), (yyvsp[(3) - (3)].character)); }
    break;

  case 29:
/* Line 1792 of yacc.c  */
#line 107 "fst-compiler.yy"
    { (yyval.expression) = interface.new_transducer((yyvsp[(1) - (3)].range),(yyvsp[(3) - (3)].range)); }
    break;

  case 30:
/* Line 1792 of yacc.c  */
#line 108 "fst-compiler.yy"
    { (yyval.expression) = interface.new_transducer((yyvsp[(1) - (1)].range),(yyvsp[(1) - (1)].range)); }
    break;

  case 31:
/* Line 1792 of yacc.c  */
#line 109 "fst-compiler.yy"
    { (yyval.expression) = interface.var_value((yyvsp[(1) - (1)].name)); }
    break;

  case 32:
/* Line 1792 of yacc.c  */
#line 110 "fst-compiler.yy"
    { (yyval.expression) = interface.rvar_value((yyvsp[(1) - (1)].name)); }
    break;

  case 33:
/* Line 1792 of yacc.c  */
#line 111 "fst-compiler.yy"
    { (yyval.expression) = interface.repetition((yyvsp[(1) - (2)].expression)); }
    break;

  case 34:
/* Line 1792 of yacc.c  */
#line 112 "fst-compiler.yy"
    { (yyval.expression) = interface.repetition2((yyvsp[(1) - (2)].expression)); }
    break;

  case 35:
/* Line 1792 of yacc.c  */
#line 113 "fst-compiler.yy"
    { (yyval.expression) = interface.optional((yyvsp[(1) - (2)].expression)); }
    break;

  case 36:
/* Line 1792 of yacc.c  */
#line 114 "fst-compiler.yy"
    { (yyval.expression) = interface.catenate((yyvsp[(1) - (2)].expression), (yyvsp[(2) - (2)].expression)); }
    break;

  case 37:
/* Line 1792 of yacc.c  */
#line 115 "fst-compiler.yy"
    { (yyval.expression) = interface.negation((yyvsp[(2) - (2)].expression)); }
    break;

  case 38:
/* Line 1792 of yacc.c  */
#line 116 "fst-compiler.yy"
    { (yyval.expression) = interface.switch_levels((yyvsp[(2) - (2)].expression)); }
    break;

  case 39:
/* Line 1792 of yacc.c  */
#line 117 "fst-compiler.yy"
    { (yyval.expression) = interface.upper_level((yyvsp[(2) - (2)].expression)); }
    break;

  case 40:
/* Line 1792 of yacc.c  */
#line 118 "fst-compiler.yy"
    { (yyval.expression) = interface.lower_level((yyvsp[(2) - (2)].expression)); }
    break;

  case 41:
/* Line 1792 of yacc.c  */
#line 119 "fst-compiler.yy"
    { (yyval.expression) = interface.conjunction((yyvsp[(1) - (3)].expression), (yyvsp[(3) - (3)].expression)); }
    break;

  case 42:
/* Line 1792 of yacc.c  */
#line 120 "fst-compiler.yy"
    { (yyval.expression) = interface.subtraction((yyvsp[(1) - (3)].expression), (yyvsp[(3) - (3)].expression)); }
    break;

  case 43:
/* Line 1792 of yacc.c  */
#line 121 "fst-compiler.yy"
    { (yyval.expression) = interface.disjunction((yyvsp[(1) - (3)].expression), (yyvsp[(3) - (3)].expression)); }
    break;

  case 44:
/* Line 1792 of yacc.c  */
#line 122 "fst-compiler.yy"
    { (yyval.expression) = (yyvsp[(2) - (3)].expression); }
    break;

  case 45:
/* Line 1792 of yacc.c  */
#line 123 "fst-compiler.yy"
    { (yyval.expression) = interface.read_words((yyvsp[(1) - (1)].value)); }
    break;

  case 46:
/* Line 1792 of yacc.c  */
#line 124 "fst-compiler.yy"
    { (yyval.expression) = interface.read_transducer((yyvsp[(1) - (1)].value)); }
    break;

  case 47:
/* Line 1792 of yacc.c  */
#line 127 "fst-compiler.yy"
    { (yyval.ranges) = interface.add_range((yyvsp[(1) - (2)].range),(yyvsp[(2) - (2)].ranges)); }
    break;

  case 48:
/* Line 1792 of yacc.c  */
#line 128 "fst-compiler.yy"
    { (yyval.ranges) = NULL; }
    break;

  case 49:
/* Line 1792 of yacc.c  */
#line 131 "fst-compiler.yy"
    { (yyval.range)=(yyvsp[(2) - (3)].range); }
    break;

  case 50:
/* Line 1792 of yacc.c  */
#line 132 "fst-compiler.yy"
    { (yyval.range)=interface.complement_range((yyvsp[(3) - (4)].range)); }
    break;

  case 51:
/* Line 1792 of yacc.c  */
#line 133 "fst-compiler.yy"
    { (yyval.range)=interface.rsvar_value((yyvsp[(2) - (3)].name)); }
    break;

  case 52:
/* Line 1792 of yacc.c  */
#line 134 "fst-compiler.yy"
    { (yyval.range)=NULL; }
    break;

  case 53:
/* Line 1792 of yacc.c  */
#line 135 "fst-compiler.yy"
    { (yyval.range)=interface.add_value((yyvsp[(1) - (1)].character),NULL); }
    break;

  case 54:
/* Line 1792 of yacc.c  */
#line 138 "fst-compiler.yy"
    { (yyval.contexts) = (yyvsp[(1) - (1)].contexts); }
    break;

  case 55:
/* Line 1792 of yacc.c  */
#line 139 "fst-compiler.yy"
    { (yyval.contexts) = (yyvsp[(2) - (3)].contexts); }
    break;

  case 56:
/* Line 1792 of yacc.c  */
#line 142 "fst-compiler.yy"
    { (yyval.contexts) = interface.add_context((yyvsp[(1) - (3)].contexts),(yyvsp[(3) - (3)].contexts)); }
    break;

  case 57:
/* Line 1792 of yacc.c  */
#line 143 "fst-compiler.yy"
    { (yyval.contexts) = (yyvsp[(1) - (1)].contexts); }
    break;

  case 58:
/* Line 1792 of yacc.c  */
#line 146 "fst-compiler.yy"
    { (yyval.contexts) = (yyvsp[(1) - (1)].contexts); }
    break;

  case 59:
/* Line 1792 of yacc.c  */
#line 147 "fst-compiler.yy"
    { (yyval.contexts) = (yyvsp[(2) - (3)].contexts); }
    break;

  case 60:
/* Line 1792 of yacc.c  */
#line 150 "fst-compiler.yy"
    { (yyval.contexts) = interface.make_context((yyvsp[(1) - (3)].expression), (yyvsp[(3) - (3)].expression)); }
    break;

  case 61:
/* Line 1792 of yacc.c  */
#line 151 "fst-compiler.yy"
    { (yyval.contexts) = interface.make_context(NULL, (yyvsp[(2) - (2)].expression)); }
    break;

  case 62:
/* Line 1792 of yacc.c  */
#line 152 "fst-compiler.yy"
    { (yyval.contexts) = interface.make_context((yyvsp[(1) - (2)].expression), NULL); }
    break;

  case 63:
/* Line 1792 of yacc.c  */
#line 155 "fst-compiler.yy"
    { (yyval.range)=interface.append_values((yyvsp[(1) - (2)].range),(yyvsp[(2) - (2)].range)); }
    break;

  case 64:
/* Line 1792 of yacc.c  */
#line 156 "fst-compiler.yy"
    { (yyval.range) = (yyvsp[(1) - (1)].range); }
    break;

  case 65:
/* Line 1792 of yacc.c  */
#line 159 "fst-compiler.yy"
    { (yyval.range)=interface.add_values((yyvsp[(1) - (3)].longchar),(yyvsp[(3) - (3)].longchar),NULL); }
    break;

  case 66:
/* Line 1792 of yacc.c  */
#line 160 "fst-compiler.yy"
    { (yyval.range)=interface.svar_value((yyvsp[(1) - (1)].name)); }
    break;

  case 67:
/* Line 1792 of yacc.c  */
#line 161 "fst-compiler.yy"
    { (yyval.range)=interface.add_value(interface.character_code((yyvsp[(1) - (1)].longchar)),NULL); }
    break;

  case 68:
/* Line 1792 of yacc.c  */
#line 162 "fst-compiler.yy"
    { (yyval.range)=interface.add_value((yyvsp[(1) - (1)].character),NULL); }
    break;

  case 69:
/* Line 1792 of yacc.c  */
#line 165 "fst-compiler.yy"
    { (yyval.longchar)=(yyvsp[(1) - (1)].uchar); }
    break;

  case 70:
/* Line 1792 of yacc.c  */
#line 166 "fst-compiler.yy"
    { (yyval.longchar)=utf8toint((yyvsp[(1) - (1)].value)); free((yyvsp[(1) - (1)].value)); }
    break;

  case 71:
/* Line 1792 of yacc.c  */
#line 167 "fst-compiler.yy"
    { (yyval.longchar)='.'; }
    break;

  case 72:
/* Line 1792 of yacc.c  */
#line 168 "fst-compiler.yy"
    { (yyval.longchar)='!'; }
    break;

  case 73:
/* Line 1792 of yacc.c  */
#line 169 "fst-compiler.yy"
    { (yyval.longchar)='?'; }
    break;

  case 74:
/* Line 1792 of yacc.c  */
#line 170 "fst-compiler.yy"
    { (yyval.longchar)='{'; }
    break;

  case 75:
/* Line 1792 of yacc.c  */
#line 171 "fst-compiler.yy"
    { (yyval.longchar)='}'; }
    break;

  case 76:
/* Line 1792 of yacc.c  */
#line 172 "fst-compiler.yy"
    { (yyval.longchar)=')'; }
    break;

  case 77:
/* Line 1792 of yacc.c  */
#line 173 "fst-compiler.yy"
    { (yyval.longchar)='('; }
    break;

  case 78:
/* Line 1792 of yacc.c  */
#line 174 "fst-compiler.yy"
    { (yyval.longchar)='&'; }
    break;

  case 79:
/* Line 1792 of yacc.c  */
#line 175 "fst-compiler.yy"
    { (yyval.longchar)='|'; }
    break;

  case 80:
/* Line 1792 of yacc.c  */
#line 176 "fst-compiler.yy"
    { (yyval.longchar)='*'; }
    break;

  case 81:
/* Line 1792 of yacc.c  */
#line 177 "fst-compiler.yy"
    { (yyval.longchar)='+'; }
    break;

  case 82:
/* Line 1792 of yacc.c  */
#line 178 "fst-compiler.yy"
    { (yyval.longchar)=':'; }
    break;

  case 83:
/* Line 1792 of yacc.c  */
#line 179 "fst-compiler.yy"
    { (yyval.longchar)=','; }
    break;

  case 84:
/* Line 1792 of yacc.c  */
#line 180 "fst-compiler.yy"
    { (yyval.longchar)='='; }
    break;

  case 85:
/* Line 1792 of yacc.c  */
#line 181 "fst-compiler.yy"
    { (yyval.longchar)='_'; }
    break;

  case 86:
/* Line 1792 of yacc.c  */
#line 182 "fst-compiler.yy"
    { (yyval.longchar)='^'; }
    break;

  case 87:
/* Line 1792 of yacc.c  */
#line 183 "fst-compiler.yy"
    { (yyval.longchar)='-'; }
    break;

  case 88:
/* Line 1792 of yacc.c  */
#line 186 "fst-compiler.yy"
    { (yyval.character)=interface.character_code((yyvsp[(1) - (1)].uchar)); }
    break;

  case 89:
/* Line 1792 of yacc.c  */
#line 187 "fst-compiler.yy"
    { (yyval.character)=interface.symbol_code((yyvsp[(1) - (1)].value)); }
    break;

  case 90:
/* Line 1792 of yacc.c  */
#line 188 "fst-compiler.yy"
    { (yyval.character)=interface.symbol_code((yyvsp[(1) - (1)].name)); }
    break;

  case 91:
/* Line 1792 of yacc.c  */
#line 192 "fst-compiler.yy"
    {}
    break;

  case 92:
/* Line 1792 of yacc.c  */
#line 193 "fst-compiler.yy"
    {}
    break;


/* Line 1792 of yacc.c  */
#line 2234 "fst-compiler.C"
      default: break;
    }
  /* User semantic actions sometimes alter yychar, and that requires
     that yytoken be updated with the new translation.  We take the
     approach of translating immediately before every use of yytoken.
     One alternative is translating here after every semantic action,
     but that translation would be missed if the semantic action invokes
     YYABORT, YYACCEPT, or YYERROR immediately after altering yychar or
     if it invokes YYBACKUP.  In the case of YYABORT or YYACCEPT, an
     incorrect destructor might then be invoked immediately.  In the
     case of YYERROR or YYBACKUP, subsequent parser actions might lead
     to an incorrect destructor call or verbose syntax error message
     before the lookahead is translated.  */
  YY_SYMBOL_PRINT ("-> $$ =", yyr1[yyn], &yyval, &yyloc);

  YYPOPSTACK (yylen);
  yylen = 0;
  YY_STACK_PRINT (yyss, yyssp);

  *++yyvsp = yyval;

  /* Now `shift' the result of the reduction.  Determine what state
     that goes to, based on the state we popped back to and the rule
     number reduced by.  */

  yyn = yyr1[yyn];

  yystate = yypgoto[yyn - YYNTOKENS] + *yyssp;
  if (0 <= yystate && yystate <= YYLAST && yycheck[yystate] == *yyssp)
    yystate = yytable[yystate];
  else
    yystate = yydefgoto[yyn - YYNTOKENS];

  goto yynewstate;


/*------------------------------------.
| yyerrlab -- here on detecting error |
`------------------------------------*/
yyerrlab:
  /* Make sure we have latest lookahead translation.  See comments at
     user semantic actions for why this is necessary.  */
  yytoken = yychar == YYEMPTY ? YYEMPTY : YYTRANSLATE (yychar);

  /* If not already recovering from an error, report this error.  */
  if (!yyerrstatus)
    {
      ++yynerrs;
#if ! YYERROR_VERBOSE
      yyerror (YY_("syntax error"));
#else
# define YYSYNTAX_ERROR yysyntax_error (&yymsg_alloc, &yymsg, \
                                        yyssp, yytoken)
      {
        char const *yymsgp = YY_("syntax error");
        int yysyntax_error_status;
        yysyntax_error_status = YYSYNTAX_ERROR;
        if (yysyntax_error_status == 0)
          yymsgp = yymsg;
        else if (yysyntax_error_status == 1)
          {
            if (yymsg != yymsgbuf)
              YYSTACK_FREE (yymsg);
            yymsg = (char *) YYSTACK_ALLOC (yymsg_alloc);
            if (!yymsg)
              {
                yymsg = yymsgbuf;
                yymsg_alloc = sizeof yymsgbuf;
                yysyntax_error_status = 2;
              }
            else
              {
                yysyntax_error_status = YYSYNTAX_ERROR;
                yymsgp = yymsg;
              }
          }
        yyerror (yymsgp);
        if (yysyntax_error_status == 2)
          goto yyexhaustedlab;
      }
# undef YYSYNTAX_ERROR
#endif
    }



  if (yyerrstatus == 3)
    {
      /* If just tried and failed to reuse lookahead token after an
	 error, discard it.  */

      if (yychar <= YYEOF)
	{
	  /* Return failure if at end of input.  */
	  if (yychar == YYEOF)
	    YYABORT;
	}
      else
	{
	  yydestruct ("Error: discarding",
		      yytoken, &yylval);
	  yychar = YYEMPTY;
	}
    }

  /* Else will try to reuse lookahead token after shifting the error
     token.  */
  goto yyerrlab1;


/*---------------------------------------------------.
| yyerrorlab -- error raised explicitly by YYERROR.  |
`---------------------------------------------------*/
yyerrorlab:

  /* Pacify compilers like GCC when the user code never invokes
     YYERROR and the label yyerrorlab therefore never appears in user
     code.  */
  if (/*CONSTCOND*/ 0)
     goto yyerrorlab;

  /* Do not reclaim the symbols of the rule which action triggered
     this YYERROR.  */
  YYPOPSTACK (yylen);
  yylen = 0;
  YY_STACK_PRINT (yyss, yyssp);
  yystate = *yyssp;
  goto yyerrlab1;


/*-------------------------------------------------------------.
| yyerrlab1 -- common code for both syntax error and YYERROR.  |
`-------------------------------------------------------------*/
yyerrlab1:
  yyerrstatus = 3;	/* Each real token shifted decrements this.  */

  for (;;)
    {
      yyn = yypact[yystate];
      if (!yypact_value_is_default (yyn))
	{
	  yyn += YYTERROR;
	  if (0 <= yyn && yyn <= YYLAST && yycheck[yyn] == YYTERROR)
	    {
	      yyn = yytable[yyn];
	      if (0 < yyn)
		break;
	    }
	}

      /* Pop the current state because it cannot handle the error token.  */
      if (yyssp == yyss)
	YYABORT;


      yydestruct ("Error: popping",
		  yystos[yystate], yyvsp);
      YYPOPSTACK (1);
      yystate = *yyssp;
      YY_STACK_PRINT (yyss, yyssp);
    }

  YY_IGNORE_MAYBE_UNINITIALIZED_BEGIN
  *++yyvsp = yylval;
  YY_IGNORE_MAYBE_UNINITIALIZED_END


  /* Shift the error token.  */
  YY_SYMBOL_PRINT ("Shifting", yystos[yyn], yyvsp, yylsp);

  yystate = yyn;
  goto yynewstate;


/*-------------------------------------.
| yyacceptlab -- YYACCEPT comes here.  |
`-------------------------------------*/
yyacceptlab:
  yyresult = 0;
  goto yyreturn;

/*-----------------------------------.
| yyabortlab -- YYABORT comes here.  |
`-----------------------------------*/
yyabortlab:
  yyresult = 1;
  goto yyreturn;

#if !defined yyoverflow || YYERROR_VERBOSE
/*-------------------------------------------------.
| yyexhaustedlab -- memory exhaustion comes here.  |
`-------------------------------------------------*/
yyexhaustedlab:
  yyerror (YY_("memory exhausted"));
  yyresult = 2;
  /* Fall through.  */
#endif

yyreturn:
  if (yychar != YYEMPTY)
    {
      /* Make sure we have latest lookahead translation.  See comments at
         user semantic actions for why this is necessary.  */
      yytoken = YYTRANSLATE (yychar);
      yydestruct ("Cleanup: discarding lookahead",
                  yytoken, &yylval);
    }
  /* Do not reclaim the symbols of the rule which action triggered
     this YYABORT or YYACCEPT.  */
  YYPOPSTACK (yylen);
  YY_STACK_PRINT (yyss, yyssp);
  while (yyssp != yyss)
    {
      yydestruct ("Cleanup: popping",
		  yystos[*yyssp], yyvsp);
      YYPOPSTACK (1);
    }
#ifndef yyoverflow
  if (yyss != yyssa)
    YYSTACK_FREE (yyss);
#endif
#if YYERROR_VERBOSE
  if (yymsg != yymsgbuf)
    YYSTACK_FREE (yymsg);
#endif
  /* Make sure YYID is used.  */
  return YYID (yyresult);
}


/* Line 2055 of yacc.c  */
#line 196 "fst-compiler.yy"


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
