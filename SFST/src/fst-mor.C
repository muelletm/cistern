/*******************************************************************/
/*                                                                 */
/*  FILE     fst-mor.C                                             */
/*  MODULE   fst-mor                                               */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*******************************************************************/

#include "fst.h"

using std::cerr;
using std::cout;

#ifdef READLINE
#include <readline/readline.h>
#include <readline/history.h>
#else

char *readline( const char *prompt ) {
  static char buffer[10000];
  printf("%s", prompt);
  if ((fgets(buffer,9999,stdin)) == NULL)
    return NULL;
  size_t l = strlen(buffer);
  if (buffer[l-1] == '\n')
    buffer[l-1] = 0;
  return buffer;
}
#endif

using namespace SFST;

bool WithBrackets=true;


/*******************************************************************/
/*                                                                 */
/*  usage                                                          */
/*                                                                 */
/*******************************************************************/

void usage()

{
  cerr << "\nUsage: fst-mor [options] file [file [file]]\n\n";
  cerr << "Options:\n";
  cerr << "-n:  print multi-character symbols without enclosing angle brackets\n";
  cerr << "-v:  print version ifnormation\n";
  cerr << "-h:  print this message\n";
  exit(1);
}


/*******************************************************************/
/*                                                                 */
/*  get_flags                                                      */
/*                                                                 */
/*******************************************************************/

void get_flags( int *argc, char **argv )

{
  for( int i=1; i<*argc; i++ ) {
    if (strcmp(argv[i],"-h") == 0) {
      usage();
      argv[i] = NULL;
    }
    else if (strcmp(argv[i],"-v") == 0) {
      printf("fst-mor version %s\n", SFSTVersion);
      exit(0);
    }
    else if (strcmp(argv[i],"-n") == 0) {
      WithBrackets = false;
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

int main( int argc, char **argv )

{
  FILE *file;

  get_flags(&argc, argv);
  if (argc < 2)
    usage();

  if (argc < 2)
    usage();

  if ((file = fopen(argv[1],"rb")) == NULL) {
    fprintf(stderr,"\nError: Cannot open fst file %s\n\n", argv[1]);
    exit(1);
  }
  cout << "reading transducer...\n";
  try {
    Transducer a(file);
    fclose(file);
    cout << "finished.\n";
      
    int analyze=1;
    for(;;) {
      const char *prompt=(analyze)? "analyze> ": "generate> ";
      char *input_string=readline(prompt);
      if (input_string == NULL || strcmp(input_string,"q") == 0)
	break;
#ifdef READLINE      
      add_history(input_string);
#endif
      if (strcmp(input_string,"") == 0)
	analyze = !analyze;
      else if (analyze) {
	if (!a.analyze_string(input_string, stdout, WithBrackets))
	  printf( "no result for %s\n", input_string);
      }
      else {
	if (!a.generate_string(input_string, stdout, WithBrackets))
	  printf( "no result for %s\n", input_string);
      }
#ifdef READLINE      
      free(input_string);
#endif
    } 
  }
  catch(const char* p) {
    cerr << p << "\n";
    return 1;
  }

  return 0;
}
