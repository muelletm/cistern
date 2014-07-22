/*******************************************************************/
/*                                                                 */
/*  FILE     fst-compact.C                                         */
/*  MODULE   fst-compact                                           */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*******************************************************************/

#include "make-compact.h"

using std::cerr;

bool Switch=false;

using namespace SFST;


/*******************************************************************/
/*                                                                 */
/*  main                                                           */
/*                                                                 */
/*******************************************************************/

int main( int argc, char **argv )

{
  FILE *file, *outfile;

  if (argc == 2) {
    if (strcmp(argv[1],"-h") == 0 ||
	strcmp(argv[1],"-help") == 0 ||
	strcmp(argv[1],"-?") == 0)
      {
	fprintf(stderr,"\nUsage: fst-compact [file [file]]\n\n");
	fprintf(stderr,"Options:\n\t-s create transducer for generation\n\n");
	exit(1);
      }
    else if (strcmp(argv[1],"-v") == 0) {
      printf("fst-compact version %s\n", SFSTVersion);
      exit(0);
    }
  }

  int n = 1;
  if (argc > 1 && strcmp(argv[1],"-s") == 0) {
    Switch = true;
    n++;
  }
  if (argc <= n)
    file = stdin;
  else {
    if ((file = fopen(argv[n],"rb")) == NULL) {
      fprintf(stderr,"Error: Cannot open input file %s\n\n", argv[n]);
      exit(1);
    }
    n++;
  }
  
  if (argc <= n)
    outfile = stdout;
  else {
    if ((outfile = fopen(argv[n],"wb")) == NULL) {
      fprintf(stderr,"Error: Cannot open output file %s\n\n", argv[n]);
      exit(1);
    }
  }

  try {
    Transducer a(file);
    if (Switch)
      MakeCompactTransducer(a.switch_levels()).store(outfile);
    else
      MakeCompactTransducer(a).store(outfile);
  }
  catch (const char *p) {
    cerr << p << "\n";
    return 1;
  }

  return 0;
}
