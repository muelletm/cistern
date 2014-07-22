/*******************************************************************/
/*                                                                 */
/*  FILE     fst-lowmem.C                                          */
/*  MODULE   fst-lowmem                                            */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*******************************************************************/

#include "fst.h"

using std::cerr;

using namespace SFST; 

bool Switch=false;


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
	fprintf(stderr,"\nUsage: fst-lowmem [file [file]]\n\n");
	fprintf(stderr,"Options:\n\t-s create transducer for generation\n\n");
	exit(0);
      }
    else if (strcmp(argv[1],"-v") == 0) {
      printf("fst-lowmem version %s\n", SFSTVersion);
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
      a.switch_levels().store_lowmem(outfile);
    else
      a.store_lowmem(outfile);
  }
  catch (const char *p) {
    cerr << p << "\n";
    return 1;
  }

  return 0;
}
