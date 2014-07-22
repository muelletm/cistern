
/*******************************************************************/
/*                                                                 */
/*     File: utf8.C                                                */
/*   Author: Helmut Schmid                                         */
/*  Purpose:                                                       */
/*  Created: Mon Sep  5 17:49:16 2005                              */
/* Modified: Wed Sep 29 15:08:34 2010 (schmid)                     */
/*                                                                 */
/*******************************************************************/

#include "string.h"
#include "utf8.h"

namespace SFST {

  const unsigned char get3LSbits=7;
  const unsigned char get4LSbits=15;
  const unsigned char get5LSbits=31;
  const unsigned char get6LSbits=63;

  const unsigned char set1MSbits=128;
  const unsigned char set2MSbits=192;
  const unsigned char set3MSbits=224;
  const unsigned char set4MSbits=240;



  /*******************************************************************/
  /*                                                                 */
  /*  int2utf8                                                       */
  /*                                                                 */
  /*******************************************************************/

  char *int2utf8( unsigned int sym )

  {
    static unsigned char ch[5];

    if (sym < 128) {
      // 1-byte UTF8 symbol, 7 bits
      ch[0] = (unsigned char)sym;
      ch[1] = 0;
    }
  
    else if (sym < 2048) {
      // 2-byte UTF8 symbol, 5+6 bits
      ch[0] = (unsigned char)((sym >> 6) | set2MSbits);
      ch[1] = (unsigned char)((sym & get6LSbits) | set1MSbits);
      ch[2] = 0;
    }
  
    else if (sym < 65536) {
      // 3-byte UTF8 symbol, 4+6+6 bits
      ch[0] = (unsigned char)((sym >> 12) | set3MSbits);
      ch[1] = (unsigned char)(((sym >> 6) & get6LSbits) | set1MSbits);
      ch[2] = (unsigned char)((sym & get6LSbits) | set1MSbits);
      ch[3] = 0;
    }
  
    else if (sym < 2097152) {
      // 4-byte UTF8 symbol, 3+6+6+6 bits
      ch[0] = (unsigned char)((sym >> 18) | set4MSbits);
      ch[1] = (unsigned char)(((sym >> 12) & get6LSbits) | set1MSbits);
      ch[2] = (unsigned char)(((sym >> 6) & get6LSbits) | set1MSbits);
      ch[3] = (unsigned char)((sym & get6LSbits) | set1MSbits);
      ch[4] = 0;
    }
  
    else
      return NULL;

    return (char*)ch;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  utf8toint                                                      */
  /*                                                                 */
  /*******************************************************************/

  unsigned int utf8toint( char **s )

  {
    int bytes_to_come;
    unsigned int result=0;
    unsigned char c=(unsigned char)**s;

    if (c >= (unsigned char)set4MSbits) { // 1111xxxx
      bytes_to_come = 3;
      result = (result << 3) | (c & get3LSbits);
    }
      
    else if (c >= (unsigned char) set3MSbits) { // 1110xxxx
      // start of a three-byte symbol
      bytes_to_come = 2;
      result = (result << 4) | (c & get4LSbits);
    }
      
    else if (c >= (unsigned char) set2MSbits) { // 1100xxxx
      // start of a two-byte symbol
      bytes_to_come = 1;
      result = (result << 5) | (c & get5LSbits);
    }
      
    else if (c < (unsigned char) set1MSbits) { // 0100xxxx
      // one-byte symbol
      bytes_to_come = 0;
      result = c;
    }

    else
      return 0; // error

    while (bytes_to_come > 0) {
      bytes_to_come--;
      (*s)++;
      c = (unsigned char)**s;
      if (c < (unsigned char) set2MSbits &&
	  c >= (unsigned char) set1MSbits)    // 1000xxxx
	{
	  result = (result << 6) | (c & get6LSbits);
	}
      else
	return 0;
    }

    (*s)++;
    return result;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  utf8toint                                                      */
  /*                                                                 */
  /*******************************************************************/

  unsigned int utf8toint( char *s )

  {
    unsigned int result = utf8toint( &s );
    if (*s == 0) // all bytes converted?
      return result;
    return 0;
  }

}
