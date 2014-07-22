// This is just a simple program which shows
// how the transducer library is used.

#include "compact.h"

int main( int argc, char **argv )

{
  FILE *file;

  file = fopen(argv[1],"rb");  // open the input file
  if (file == NULL)  exit(1);
  try {
    CompactTransducer ca(file); // read the transducer
      
    char buffer[1000];
    std::vector<CAnalysis> analyses;
    while (fgets(buffer, 1000, stdin)) {  // next input line
      int l=strlen(buffer)-1;  // delete the newline character
      if (buffer[l] == '\n')
	buffer[l] = '\0';
      printf("> %s\n", buffer);  // print the input line

      ca.analyze_string(buffer, analyses);  // analyse the input

      if (analyses.size() == 0)
	printf( "no result for %s\n", buffer);  // analysis has failed
      else  // print all analyses
	for( size_t i=0; i<analyses.size(); i++ ) {
	  fputs(ca.print_analysis(analyses[i]), stdout);
	  fputc('\n', stdout);
	}
    }
  }
  catch (const char *p) {   // deal with exceptions
    std::cerr << p << "\n";
    return 1;
  }

  return 0;
}
