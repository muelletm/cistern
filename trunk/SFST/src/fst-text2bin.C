/*******************************************************************/
/*                                                                 */
/*  FILE     fst-text2bin.C                                        */
/*  MODULE   fst-text2bin                                          */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*******************************************************************/

#include "fst.h"

using std::cerr;

using namespace SFST;


/*******************************************************************/
/*                                                                 */
/*  main                                                           */
/*                                                                 */
/*******************************************************************/

int main( int argc, char **argv )

{
  FILE *file;

  if (argc < 3 || (argc > 1 && (strcmp(argv[1],"-h") == 0 || 
				strcmp(argv[1],"-help") == 0 ||
				strcmp(argv[1],"-?") == 0)))
    {
      fprintf(stderr,"\nUsage: %s file file\n\n", argv[0]);
      fprintf(stderr,"converts a transducer from text format into the standard binary format");
      exit(1);
    }

  if ((file = fopen(argv[1],"rt")) == NULL) {
    fprintf(stderr,"\nError: Cannot open input file %s\n\n", argv[1]);
    exit(1);
  }

  try {
    Transducer a(file, false); // read a transducer in text format
    fclose(file);
    if ((file = fopen(argv[2],"wb")) == NULL) {
      fprintf(stderr,"\nError: Cannot open output file %s\n\n", argv[2]);
      exit(1);
    }
    a.store(file);
    fclose(file);
  }
  catch (const char *p) {
    cerr << p << "\n";
    return 1;
  }

  return 0;
}
