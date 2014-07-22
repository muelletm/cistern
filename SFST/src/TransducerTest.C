#include "Transducer.h"

#include <iostream>
using std::cerr;

int main( int argc, char **argv )

{
  FILE *file;

  file = fopen(argv[1],"rb");  // open the input file
  if (file == NULL)  exit(1);
  try {
    Transducer transducer(file); // (1)  read the transducer
      
    char buffer[1000];
    while (fgets(buffer, 1000, stdin)) {  // (2) next input line
      // delete newline character
      int l=strlen(buffer)-1;
      if (buffer[l] == '\n')
	buffer[l] = '\0';
      printf("> %s\n", buffer);  // print the input line

      vector<vector<char> > analyses;
      transducer.analyze(buffer, analyses);  // (3) analyse the input

      if (analyses.size() == 0)
	printf( "no result for %s\n", buffer);
      else
	for( size_t i=0; i<analyses.size(); i++ ) {
	  for( size_t k=0; k<analyses[i].size(); k++ )
	    fputc(analyses[i][k], stdout);
	  fputc('\n', stdout);
	}
    }
  }
  catch (const char *p) {
    cerr << p << "\n";
    return 1;
  }

  return 0;
}
