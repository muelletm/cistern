/*******************************************************************/
/*                                                                 */
/*  FILE     fst-match.C                                           */
/*  MODULE   fst-match                                             */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*******************************************************************/


#include "compact.h"

using std::cerr;
using std::vector;

using namespace SFST;

const int BUFFER_SIZE=100000;

bool Verbose=true;


/*******************************************************************/
/*                                                                 */
/*  usage                                                          */
/*                                                                 */
/*******************************************************************/

void usage()

{
  cerr << "\nUsage: fst-infl [options] file [file [file]]\n\n";
  cerr << "Options:\n";
  cerr << "-h:  print this message\n";
  cerr << "-v:  print version information\n";
  cerr << "-q:  suppress status messages\n";
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
      Verbose = false;
      argv[i] = NULL;
    }
    else if (strcmp(argv[i],"-h") == 0) {
      usage();
      argv[i] = NULL;
    }
    else if (strcmp(argv[i],"-v") == 0) {
      printf("fst-parse version %s\n", SFSTVersion);
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
  if (Verbose)
    cerr << "reading transducer...\n";
  try {
    CompactTransducer a(file);
    fclose(file);
    if (Verbose)
      cerr << "finished.\n";
    
    if (argc <= 2)
      file = stdin;
    else {
      if ((file = fopen(argv[2],"rt")) == NULL) {
	fprintf(stderr,"\nError: Cannot open input file %s\n\n",argv[2]);
	exit(1);
      }
    }
    
    if (argc <= 3)
      outfile = stdout;
    else {
      if ((outfile = fopen(argv[3],"wt")) == NULL) {
	fprintf(stderr,"\nError: Cannot open output file %s\n\n",argv[3]);
	exit(1);
      }
    }
    
    char buffer[BUFFER_SIZE];
    int N=0;
    vector<char*> analyses;
    while (fgets(buffer, BUFFER_SIZE, file)) {
      if (Verbose && ++N % 100 == 0)
	fprintf(stderr,"\r%d", N);
      int l=(int)strlen(buffer)-1;
      if (buffer[l] == '\n')
	buffer[l] = '\0';
      char *s=buffer;
      while (*s)
	fputs(a.longest_match(s), outfile);
      fputc('\n', outfile);
    }
  }
  catch (const char *p) {
    cerr << p << "\n";
    return 1;
  }
  if (Verbose)
    fputc('\n', stderr);

  return 0;
}
