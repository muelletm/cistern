/*******************************************************************/
/*                                                                 */
/*  FILE     fst-infl.C                                            */
/*  MODULE   fst-infl                                              */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*******************************************************************/

#include "fst.h"

using std::cerr;
using std::vector;

using namespace SFST;

const int BUFFER_SIZE=1000;

bool Verbose=true;
bool WithBrackets=true;
vector<char*> Filenames;


/*******************************************************************/
/*                                                                 */
/*  usage                                                          */
/*                                                                 */
/*******************************************************************/

void usage()

{
  cerr << "\nUsage: fst-infl [options] tfile [file [file]]\n\n";
  cerr << "Options:\n";
  cerr << "-t tfile:  alternative transducer\n";
  cerr << "-n:  Print multi-character symbols without angle brackets\n";
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
    else if (strcmp(argv[i],"-n") == 0) {
      WithBrackets = false;
      argv[i] = NULL;
    }
    else if (strcmp(argv[i],"-h") == 0) {
      usage();
      argv[i] = NULL;
    }
    else if (strcmp(argv[i],"-v") == 0) {
      printf("fst-infl version %s\n", SFSTVersion);
      exit(0);
    }
    else if (i < *argc-1) {
      if (strcmp(argv[i],"-t") == 0) {
	Filenames.push_back(argv[i+1]);
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
}


/*******************************************************************/
/*                                                                 */
/*  main                                                           */
/*                                                                 */
/*******************************************************************/

int main( int argc, char **argv )

{
  FILE *file, *outfile;
  vector<Transducer*> transducer;

  get_flags(&argc, argv);
  if (argc < 2)
    usage();

  Filenames.push_back(argv[1]);
  try {
    for( size_t i=0; i<Filenames.size(); i++ ) {
      if ((file = fopen(Filenames[i],"rb")) == NULL) {
	fprintf( stderr, "\nError: Cannot open transducer file %s\n\n",
		 Filenames[i]);
	exit(1);
      }
      if (Verbose)
	cerr << "reading transducer from file \"" << Filenames[i] <<"\"...\n";
      transducer.push_back(new Transducer(file));
      fclose(file);
      if (Verbose)
	cerr << "finished.\n";
    }

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
    while (fgets(buffer, BUFFER_SIZE, file)) {
      int l=(int)strlen(buffer)-1;
      if (buffer[l] == '\n')
	buffer[l] = '\0';
      fprintf(outfile, "> %s\n", buffer);
      size_t i;
      for( i=0; i<transducer.size(); i++ )
	if (transducer[i]->analyze_string(buffer, outfile, WithBrackets))
	  break;
      if (i == transducer.size())
	fprintf( outfile, "no result for %s\n", buffer);
    }
  }
  catch (const char *p) {
    cerr << p << "\n";
    return 1;
  }

  return 0;
}
