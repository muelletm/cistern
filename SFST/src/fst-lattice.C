/*******************************************************************/
/*                                                                 */
/*  FILE     fst-lattice.C                                         */
/*  MODULE   fst-lattice                                           */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*******************************************************************/

#include "fst.h"

using std::cerr;
using std::cout;

using namespace SFST;

namespace SFST 
{

const int BUFFER_SIZE=1000;

int Quiet=0;
int AnalysisOnly=0;

}


/*******************************************************************/
/*                                                                 */
/*  usage                                                          */
/*                                                                 */
/*******************************************************************/

void usage()

{
  cerr << "\nUsage: fst-lattice [options] file [file [file]]\n\n";
  cerr << "Options:\n";
  cerr << "-h:  print this message\n";
  cerr << "-v:  print version information\n";
  cerr << "-a:  print analysis characters only\n";
  cerr << "-q:  suppress status messages\n";
  cerr << "\nThis program composes each input line from the second argument file\nwith the transducer read from the first argument file and prints the resulting transducer.\n";
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
    if (strcmp(argv[i],"-q") == 0) {
      Quiet = 1;
      argv[i] = NULL;
    }
    else if (strcmp(argv[i],"-a") == 0) {
      AnalysisOnly = 1;;
      argv[i] = NULL;
    }
    else if (strcmp(argv[i],"-h") == 0) {
      usage();
      argv[i] = NULL;
    }
    else if (strcmp(argv[i],"-v") == 0) {
      printf("fst-lattice version %s\n", SFSTVersion);
      exit(0);
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
  FILE *file, *outfile;

  get_flags(&argc, argv);
  if (argc < 2)
    usage();

  if ((file = fopen(argv[1],"rb")) == NULL) {
    fprintf(stderr,"\nError: Cannot open transducer file %s\n\n", argv[1]);
    exit(1);
  }
  if (!Quiet)
    cerr << "reading transducer...\n";
  try {
    Transducer a(file);
    fclose(file);
    if (!Quiet)
      cerr << "finished.\n";

    if (argc <= 2)
      file = stdin;
    else {
      if ((file = fopen(argv[2],"rt")) == NULL) {
	fprintf(stderr,"Error: Cannot open input file %s\n\n", argv[2]);
	exit(1);
      }
    }
      
    if (argc <= 3)
      outfile = stdout;
    else {
      if ((outfile = fopen(argv[3],"wt")) == NULL) {
	fprintf(stderr,"Error: Cannot open output file %s\n\n", argv[3]);
	exit(1);
      }
    }

    char buffer[BUFFER_SIZE];
    for( long n=0; fgets(buffer, BUFFER_SIZE, file); n++ ) {
      if (!Quiet)
	fprintf(stderr,"\r%ld",n);
      int l=(int)strlen(buffer)-1;
      if (buffer[l] == '\n')
	buffer[l] = '\0';

      Transducer *a1 = new Transducer(buffer, &a.alphabet);
      Transducer *a2 = &(a || *a1);
      delete a1;
      if (AnalysisOnly) {
	a1 = &(a2->lower_level());
	delete a2;
	a2 = a1;
      }
      a1 = &(a2->minimise());
      delete a2;
      cout << "> " << buffer << "\n";
      cout << *a1;
      cout << "\n";
      delete a1;
    }
  }
  catch (const char *p) {
    cerr << p << "\n";
    return 1;
  }

  return 0;
}
