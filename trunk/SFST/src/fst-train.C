
/*******************************************************************/
/*                                                                 */
/*     File: fst-train.C                                           */
/*   Author: Helmut Schmid                                         */
/*  Purpose: EM training of a transducer                           */
/*  Created: Mon Aug  8 15:11:36 2005                              */
/* Modified: Mon Jan 16 15:38:02 2012 (schmid)                     */
/*                                                                 */
/*******************************************************************/

#include <math.h>

#include "compact.h"

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
/*  print_parameters                                               */
/*                                                                 */
/*******************************************************************/

void print_parameters( vector<double> &arcfreq, vector<double> &finalfreq,
		       FILE *file )
{
  size_t n = finalfreq.size();
  fwrite(&n, sizeof(n), 1, file);
  n = arcfreq.size();
  fwrite(&n, sizeof(n), 1, file);
  for( size_t n=0; n<finalfreq.size(); n++ ) {
    float f = (float)log(finalfreq[n]);
    fwrite(&f, sizeof(f), 1, file);
  }
  for( size_t a=0; a<arcfreq.size(); a++ ) {
    float f = (float)log(arcfreq[a]);
    fwrite(&f, sizeof(f), 1, file);
  }
}


/*******************************************************************/
/*                                                                 */
/*  usage                                                          */
/*                                                                 */
/*******************************************************************/

void usage()

{
  cerr << "\nUsage: fst-train [options] file [file]\n\n";
  cerr << "Options:\n";
  cerr << "-t tfile:  alternative transducer\n";
  cerr << "-b:  input with surface and analysis characters\n";
  cerr << "-d:  disambiguate symbolically (use only the simplest analyses)\n";
  cerr << "-q:  suppress status messages\n";
  cerr << "-v:  print version information\n";
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
    if (strcmp(argv[i],"-q") == 0) {
      Verbose = false;
      argv[i] = NULL;
    }
    else if (strcmp(argv[i],"-d") == 0) {
      Disambiguate = true;
      argv[i] = NULL;
    }
    else if (strcmp(argv[i],"-b") == 0) {
      BothLayers = true;
      argv[i] = NULL;
    }
    else if (strcmp(argv[i],"-h") == 0) {
      usage();
      argv[i] = NULL;
    }
    else if (strcmp(argv[i],"-v") == 0) {
      printf("fst-train version %s\n", SFSTVersion);
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
  FILE *file;
  vector<CompactTransducer*> transducer;

  get_flags(&argc, argv);
  if (argc < 2)
    usage();

  Filenames.push_back(argv[1]);
  try {
    for( size_t i=0; i<Filenames.size(); i++ ) {
      if ((file = fopen(Filenames[i],"rb")) == NULL) {
	fprintf(stderr, "\nError: Cannot open transducer file %s\n\n",
		Filenames[i]);
	exit(1);
      }
      if (Verbose)
	cerr << "reading transducer from file \"" << Filenames[i] <<"\"...\n";
      transducer.push_back(new CompactTransducer(file));
      fclose(file);
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

    vector<vector<double> > arcfreq, finalfreq;
    finalfreq.resize(transducer.size());
    arcfreq.resize(transducer.size());
    for( size_t i=0; i<transducer.size(); i++ ) {
      finalfreq[i].resize(transducer[i]->node_count(), 0.0);
      arcfreq[i].resize(transducer[i]->arc_count(), 0.0);
    }

    char buffer[BUFFER_SIZE];
    int N=0;
    while (fgets(buffer, BUFFER_SIZE, file)) {
      if (Verbose && ++N % 100 == 0)
	fprintf(stderr,"\r%d", N);
      int l=(int)strlen(buffer)-1;
      if (buffer[l] == '\n')
	buffer[l] = '\0';

      for( size_t i=0; i<transducer.size(); i++ ) {
	if (BothLayers) {
	  if (transducer[i]->train2(buffer, arcfreq[i], finalfreq[i] ))
	    break;
	}
	else {
	  if (transducer[i]->train(buffer, arcfreq[i], finalfreq[i] ))
	    break;
	}
      }
    }
    if (Verbose)
      fputc('\n', stderr);
    
    for( size_t i=0; i<transducer.size(); i++ ) {
      char buffer[1000];
      FILE *outfile;
      sprintf(buffer, "%s.prob", Filenames[i]);
      if ((outfile = fopen(buffer,"wb")) == NULL) {
	fprintf(stderr, "\nError: Cannot open probability file %s.prob\n\n",
		Filenames[i]);
	exit(1);
      }
      transducer[i]->estimate_probs( arcfreq[i], finalfreq[i] );
      print_parameters( arcfreq[i], finalfreq[i], outfile );
    }
  }
  catch (const char *p) {
    cerr << p << "\n";
    return 1;
  }
  return 0;
}
