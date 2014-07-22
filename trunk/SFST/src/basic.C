
/*******************************************************************/
/*                                                                 */
/*  FILE     basic.C                                               */
/*  MODULE   basic                                                 */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*  PURPOSE                                                        */
/*                                                                 */
/*******************************************************************/

#include <stdlib.h>
#include <string.h>

#include "basic.h"

namespace SFST {

  bool Switch_Bytes=false;


  /*******************************************************************/
  /*                                                                 */
  /*  fst_strdup                                                     */
  /*                                                                 */
  /*******************************************************************/

  char* fst_strdup(const char* pString)

  {
    char* pStringCopy = (char*)malloc(strlen(pString) + 1);
    if (pStringCopy == NULL) {
      fprintf(stderr, "\nError: out of memory (malloc failed)\naborted.\n");
      exit(1);
    }
    strcpy(pStringCopy, pString);
    return pStringCopy;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  read_string                                                    */
  /*                                                                 */
  /*******************************************************************/

  int read_string( char *buffer, int size, FILE *file )

  {
    for( int i=0; i<size; i++ ) {
      int c=fgetc(file);
      if (c == EOF || c == 0) {
	buffer[i] = 0;
	return (c==0);
      }
      buffer[i] = (char)c;
    }
    buffer[size-1] = 0;
    return 0;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  read_num                                                       */
  /*                                                                 */
  /*******************************************************************/

  size_t read_num( void *p, size_t n, FILE *file )

  {
    char *pp=(char*)p;
    size_t result=fread( pp, 1, n, file );
    if (Switch_Bytes) {
      size_t e=n/2;
      for( size_t i=0; i<e; i++ ) {
	char tmp=pp[i];
	pp[i] = pp[--n];
	pp[n] = tmp;
      }
    }
    return result;
  }
}
