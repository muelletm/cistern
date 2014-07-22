/*******************************************************************/
/*                                                                 */
/*  FILE     fst-compare.C                                         */
/*  MODULE   fst-compare                                           */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*******************************************************************/

#include "fst.h"

using std::cout;
using std::cerr;

using namespace SFST;


/*******************************************************************/
/*                                                                 */
/*  main                                                           */
/*                                                                 */
/*******************************************************************/

int main( int argc, char **argv )

{
  FILE *file1, *file2;

  if (argc < 3) {
    fprintf(stderr,"\nUsage: %s file file\n\n", argv[0]);
    exit(1);
  }

  if ((file1 = fopen(argv[1],"rb")) == NULL) {
    fprintf(stderr,"\nError: Cannot open transducer file %s\n\n", argv[1]);
    exit(1);
  }
  if ((file2 = fopen(argv[2],"rb")) == NULL) {
    fprintf(stderr,"\nError: Cannot open transducer file %s\n\n", argv[2]);
    exit(1);
  }

  try {
    Transducer a1(file1);
    fclose(file1);
    Transducer a2(file2);
    fclose(file2);
    Transducer *p = &a2.copy(false, &a1.alphabet);
    if (a1 == *p)
      cout << "Transducers are equivalent\n";
    else
      cout << "Transducers are different\n";
  }
  catch (const char *p) {
    cerr << p << "\n";
    return 1;
  }

  return 0;
}
