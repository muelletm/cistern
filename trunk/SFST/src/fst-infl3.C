/*******************************************************************/
/*                                                                 */
/*  FILE     fst-infl3.C                                           */
/*  MODULE   fst-infl3                                             */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*******************************************************************/

#include "lowmem.h"

using std::cerr;
using std::vector;

using namespace SFST;

const int BUFFER_SIZE=1000;

bool Verbose=true;
bool BothLayers=false;
bool Disambiguate=false;
vector<char*> Filenames;


/*******************************************************************/
/*                                                                 */
/*  usage                                                          */
/*                                                                 */
/*******************************************************************/

void usage()

{
  cerr << "\nUsage: fst-infl3 [options] tfile [file [file]]\n\n";
  cerr << "Options:\n";
  cerr << "-t tfile:  alternative transducer\n";
  cerr << "-d:  only print the simplest analyses\n";
  cerr << "-b:  print analysis and surface characters\n";
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
    if (strcmp(argv[i],"-d") == 0) {
      Disambiguate = true;
      argv[i] = NULL;
    }
    else if (strcmp(argv[i],"-b") == 0) {
      BothLayers = true;
      argv[i] = NULL;
    }
    else if (strcmp(argv[i],"-q") == 0) {
      Verbose = false;
      argv[i] = NULL;
    }
    else if (strcmp(argv[i],"-h") == 0) {
      usage();
      argv[i] = NULL;
    }
    else if (strcmp(argv[i],"-v") == 0) {
      printf("fst-infl3 version %s\n", SFSTVersion);
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
  vector<LowMemTransducer*> transducer;

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
      transducer.push_back(new LowMemTransducer(file));
      transducer[i]->simplest_only = Disambiguate;
      if (Verbose)
	cerr << "finished.\n";
    }

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
    vector<Analysis> analyses;
    while (fgets(buffer, BUFFER_SIZE, file)) {
      if (Verbose && ++N % 100 == 0)
	fprintf(stderr,"\r%d", N);
      int l=(int)strlen(buffer)-1;
      if (buffer[l] == '\n')
	buffer[l] = '\0';
      fprintf(outfile, "> %s\n", buffer);
      for( size_t i=0; i<transducer.size(); i++ ) {
	transducer[i]->analyze_string(buffer, analyses);
	if (analyses.size() > 0) {
	  Alphabet &a=transducer[i]->alphabet;
	  for( size_t k=0; k<analyses.size(); k++ ) {
	    fputs(a.print_analysis(analyses[k], BothLayers), outfile);
	    fputc('\n', outfile);
	  }
	  break;
	}
      }

      if (analyses.size() == 0)
	fprintf( outfile, "no result for %s\n", buffer);
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
