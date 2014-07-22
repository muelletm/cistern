/*******************************************************************/
/*                                                                 */
/*  FILE     fst-infl2.C                                           */
/*  MODULE   fst-infl2                                             */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*******************************************************************/

#include <errno.h>

#include "compact.h"

using std::vector;
using std::cerr;

using namespace SFST;

const int BUFFER_SIZE=1000;

bool Verbose=true;
bool Disambiguate=false;
bool BothLayers=false;
bool PrintProbs=false;
vector<char*> Filenames;
double Threshold=1.0;
float MaxError=0.0;


/*******************************************************************/
/*                                                                 */
/*  usage                                                          */
/*                                                                 */
/*******************************************************************/

void usage()

{
  cerr << "\nUsage: fst-infl2 [options] tfile [file [file]]\n\n";
  cerr << "Options:\n";
  cerr << "-d:  disambiguate symbolically (print the simplest analyses)\n";
  cerr << "-e n: robust matching (up to n errors allowed)\n";
  cerr << "-% f: disambiguate statistically (print best analyses up to a total probability of f %).\n";
  cerr << "-b:  print analysis and surface characters\n";
  cerr << "-p:  print probabilities\n";
  cerr << "-t tfile:  alternative transducer\n";
  cerr << "-c:  read transducer with an opposite-endian encoding (Sparc <=> Pentium)\n";
  cerr << "-q:  suppress status messages\n";
  cerr << "-v:  print version information\n";
  cerr << "-h:  print this message\n";
  cerr << "\nThe names of the probability files required by option -p and -% are are\nobtained by adding .prob to the transducer file names.\n\n";
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
    else if (strcmp(argv[i],"-p") == 0) {
      PrintProbs = true;
      argv[i] = NULL;
    }
    else if (strcmp(argv[i],"-c") == 0) {
      Switch_Bytes = true;
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
      printf("fst-infl2 version %s\n", SFSTVersion);
      exit(0);
    }
    else if (i < *argc-1) {
      if (strcmp(argv[i],"-t") == 0) {
	Filenames.push_back(argv[i+1]);
	argv[i] = NULL;
	argv[++i] = NULL;
      }
      else if (strcmp(argv[i],"-%") == 0) {
        errno = 0;
        if ((Threshold = atof(argv[i+1])), errno) {
          fprintf(stderr,"Invalid argument of option -%%: %s\n", argv[i+1]);
          exit(1);
        }
	if (Threshold <= 0.0 || Threshold > 100.0) {
          fprintf(stderr,"Argument of option -%% is out of range: %s\n", 
		  argv[i+1]);
          exit(1);
	}
	Threshold *= 0.01;
	argv[i] = NULL;
	argv[++i] = NULL;
      }
      else if (strcmp(argv[i],"-e") == 0) {
        errno = 0;
        if ((MaxError = (float)atof(argv[i+1])), errno) {
          fprintf(stderr,"Invalid argument of option -e: %s\n", argv[i+1]);
          exit(1);
        }
	if (MaxError <= 0.0) {
          fprintf(stderr,"Argument of option -e is out of range: %s\n", 
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
}


/*******************************************************************/
/*                                                                 */
/*  main                                                           */
/*                                                                 */
/*******************************************************************/

int main( int argc, char **argv )

{
  FILE *file, *outfile;
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

      if (Threshold != 1.0 || PrintProbs) {
	FILE *pfile;
	char buffer[1000];
	sprintf(buffer, "%s.prob", Filenames[i]);
	if ((pfile = fopen(buffer,"rb")) == NULL) {
	  fprintf(stderr, "\nError: Cannot open probability file %s.prob\n\n",
		  Filenames[i]);
	  exit(1);
	}
	transducer.push_back(new CompactTransducer(file, pfile));
	fclose(pfile);
      }
      else
	transducer.push_back(new CompactTransducer(file));
      transducer[i]->both_layers = BothLayers;
      transducer[i]->simplest_only = Disambiguate;
      fclose(file);
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
    vector<CAnalysis> analyses;
    while (fgets(buffer, BUFFER_SIZE, file)) {
      if (Verbose && ++N % 100 == 0)
	fprintf(stderr,"\r%d", N);
      int l=(int)strlen(buffer)-1;
      if (buffer[l] == '\n')
	buffer[l] = '\0';
      fprintf(outfile, "> %s\n", buffer);

      for( size_t i=0; i<transducer.size(); i++ ) {
	transducer[i]->analyze_string(buffer, analyses);

	if (analyses.size() == 0 && MaxError > 0.0) {
	  float f=transducer[i]->robust_analyze_string(buffer, analyses, 
						       MaxError);
	  if (analyses.size() > 0)
	    fprintf(outfile,"analyses with error score %f:\n", f);
	}

	if (analyses.size() > 0) {
	  if (Threshold != 1.0 || PrintProbs) {
	    vector<double> prob;
	    transducer[i]->compute_probs( analyses, prob );
	    double sum=0.0;
	    for( size_t k=0; k<analyses.size(); k++ ) {
	      fputs(transducer[i]->print_analysis(analyses[k]), outfile);
	      if (PrintProbs)
		fprintf(outfile,"\t%f", prob[k]);
	      fputc('\n', outfile);
	      sum += prob[k];
	      if (sum > 0.0 && sum >= Threshold &&
		  (k==analyses.size() || prob[k] > prob[k+1]))
		break;
	    }
	  }
	  else
	    for( size_t k=0; k<analyses.size(); k++ ) {
	      fputs(transducer[i]->print_analysis(analyses[k]), outfile);
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
