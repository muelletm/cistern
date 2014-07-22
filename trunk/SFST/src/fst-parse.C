/*******************************************************************/
/*                                                                 */
/*  FILE     fst-parse.C                                           */
/*  MODULE   fst-parse                                             */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*******************************************************************/

#include "fst.h"

using std::cerr;
using std::vector;

using namespace SFST; 

#define BUFFER_SIZE 10000

bool Debug=false;
bool Verbose=false;

vector<char*> TFileNames;


/*******************************************************************/
/*                                                                 */
/*  usage                                                          */
/*                                                                 */
/*******************************************************************/

void usage()

{
  cerr << "\nUsage: fst-parse [options] transducer [infile [outfile]]\n\n";
  cerr << "Options:\n";
  cerr << "-t t:  compose transducer t\n";
  cerr << "-h:  print this message\n";
  cerr << "-q:  suppress status messages\n";
  cerr << "-v:  print version information\n";
  cerr << "-d:  print debugging output\n";
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
    else if (strcmp(argv[i],"-d") == 0) {
      Debug = true;
      argv[i] = NULL;
    }
    else if (i < (*argc)-1 && strcmp(argv[i],"-t") == 0) {
      TFileNames.push_back(argv[i+1]);
      argv[i++] = NULL;
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
  FILE *file, *outfile;
    
  get_flags(&argc, argv);
  if (argc < 2)
    usage();

  TFileNames.push_back(argv[1]);
  vector<Transducer*> a;
  try {
    for( size_t i=0; i<TFileNames.size(); i++ ) {
      if ((file = fopen(TFileNames[i],"rb")) == NULL) {
	fprintf(stderr,"\nError: Cannot open transducer file \"%s\"\n\n", 
		TFileNames[i]);
	exit(1);
      }
      if (Verbose)
	fprintf(stderr,"reading transducer %s ...", TFileNames[i]);
      a.push_back(new Transducer(file));
      fclose(file);
      if (Verbose)
	fputs("finished.\n",stderr);
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

	  Transducer *t = new Transducer(buffer, &a.back()->alphabet, false);
	  for( int i=(int)a.size()-1; i>=0; i-- ) {
	    if (Debug) {
	      cerr << "\n";
	      cerr << *t;
	    }
	    Transducer *t2 = &(*a[i] || *t);
	    delete t;
	    t = t2;
	  }
	  Transducer *t2 = &t->lower_level();
	  delete t;
	  t = &t2->minimise();
	  delete t2;
	  if (Debug) {
	    cerr << "result:\n";
	    cerr << *t;
	  }
	  t->alphabet.copy(a[0]->alphabet);
	  if (!t->print_strings( outfile ))
	    fprintf(outfile, "no analysis for \"%s\"\n", buffer);
	  delete t;
      }
  }
  catch (const char *p) {
      cerr << p << "\n";
      return 1;
  }

  return 0;
}
