/*******************************************************************/
/*                                                                 */
/*  FILE     fst-print.C                                           */
/*  MODULE   fst-print                                             */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*******************************************************************/

#include "fst.h"

using std::cout;
using std::cerr;

using namespace SFST;


/*******************************************************************/
/*                                                                 */
/*  main                                                           */
/*                                                                 */
/*******************************************************************/

int main( int argc, char **argv )

{
  FILE *file;

  if (argc > 1 && (!strcmp(argv[1],"-h") || 
		   !strcmp(argv[1],"-help") ||
		   !strcmp(argv[1],"-?")))
    {
      fprintf(stderr,"\nUsage: %s [file]\n\n", argv[0]);
      exit(1);
    }

  if (argc == 1)
    file = stdin;
  else if ((file = fopen(argv[1],"rb")) == NULL) {
    fprintf(stderr,"\nError: Cannot open transducer file %s\n\n", argv[1]);
    exit(1);
  }

  try {
    Transducer a(file);
    fclose(file);
    cout << a;
  }
  catch (const char *p) {
    cerr << p << "\n";
    return 1;
  }

  return 0;
}
