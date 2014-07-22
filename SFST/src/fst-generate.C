/*******************************************************************/
/*                                                                 */
/*  FILE     fst-generate.C                                        */
/*  MODULE   fst-generate                                          */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*******************************************************************/

#include <errno.h>

#include "fst.h"

using std::cerr;

using namespace SFST;

bool Upper=false;
bool Lower=false;
int  MaxAnalyses=-1;

OutputType Output = Joint;


/*******************************************************************/
/*                                                                 */
/*  usage                                                          */
/*                                                                 */
/*******************************************************************/

void usage()

{
  fprintf(stderr,"Usage: fst-generate [Options] file\n");
  fprintf(stderr,"\nOptions:\n");
  fprintf(stderr,"\t-n x: print up to x many analyses\n");
  fprintf(stderr,"\t-u: print upper layer only\n");
  fprintf(stderr,"\t-l: print lower layer only\n");
  fprintf(stderr,"\t-b: print upper and lower layer separately\n");
  fprintf(stderr,"\t-v: print version information\n");
  fprintf(stderr,"\t-h: print usage information\n\n");
}


/*******************************************************************/
/*                                                                 */
/*  get_flags                                                      */
/*                                                                 */
/*******************************************************************/

void get_flags( int *argc, char **argv )

{
  for( int i=1; i<*argc; i++ ) {
    if (strcmp(argv[i],"-h") == 0 ||
	     strcmp(argv[i],"-help") == 0 ||
	     strcmp(argv[i],"-?") == 0)
      {
	usage();
	exit(0);
      }
    else if (strcmp(argv[i],"-v") == 0) {
      printf("fst-generate version %s\n", SFSTVersion);
      exit(0);
    }
    else if (strcmp(argv[i],"-u") == 0) {
      Output = UpperOnly;
      argv[i] = NULL;
    }
    else if (strcmp(argv[i],"-l") == 0) {
      Output = LowerOnly;
      argv[i] = NULL;
    }
    else if (strcmp(argv[i],"-b") == 0) {
      Output = Both;
      argv[i] = NULL;
    }
    else if (i < *argc-1) {
      if (strcmp(argv[i],"-n") == 0) {
        errno = 0;
        if ((MaxAnalyses = atoi(argv[i+1])), errno) {
          fprintf(stderr,"Error: invalid argument of option -n: %s\n", 
		  argv[i+1]);
          exit(1);
        }
	argv[i] = NULL;
	argv[++i] = NULL;
      }
    }
  }

  // remove flags from the argument list
  int k;
  for( int i=k=1; i<*argc; i++)
    if (argv[i] != NULL)
      argv[k++] = argv[i];
  *argc = k;
  if (k > 2) {
    fprintf(stderr,"Error: too many arguments\n");
    exit(1);
  }
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

  if (argc == 1)
    file = stdin;
  else if ((file = fopen(argv[1],"rb")) == NULL) {
    fprintf(stderr,"\nError: Cannot open transducer file %s\n\n", argv[1]);
    exit(1);
  }

  try {
    Transducer a(file);
    fclose(file);
    a.generate(stdout, MaxAnalyses, Output);
  }
  catch (const char *p) {
    cerr << p << "\n";
    return 1;
  }

  return 0;
}
