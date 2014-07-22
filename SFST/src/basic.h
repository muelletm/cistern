
/*******************************************************************/
/*                                                                 */
/*  FILE     basic.h                                               */
/*  MODULE   basic                                                 */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*  PURPOSE                                                        */
/*                                                                 */
/*******************************************************************/

#ifndef _BASIC_H_
#define _BASIC_H_

#include <stdio.h>

namespace SFST {

  extern bool Switch_Bytes;

  char* fst_strdup(const char* pString);
  int read_string( char *buffer, int size, FILE *file );
  size_t read_num( void *p, size_t size, FILE *file );

}
#endif
