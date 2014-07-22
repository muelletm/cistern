/*******************************************************************/
/*                                                                 */
/*  FILE     mem.h                                                 */
/*  MODULE   mem                                                   */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*  PURPOSE  memory management functions                           */
/*                                                                 */
/*******************************************************************/

#ifndef _MEM_H_
#define _MEM_H_

#include <stdlib.h>
#include <assert.h>

namespace SFST {

#define MEMBUFFER_SIZE 100000


  /*****************  class Mem  *************************************/

  class Mem {

  private:

    struct MemBuffer {
      char buffer[MEMBUFFER_SIZE];
      struct MemBuffer *next;
    };

    MemBuffer *first_buffer;
    long pos;
    void add_buffer() {
      MemBuffer *mb=(MemBuffer*)malloc(sizeof(MemBuffer));
      if (mb == NULL)
	throw "Allocation of memory failed in Mem::add_buffer!";
      mb->next = first_buffer;
      first_buffer = mb;
      pos = 0;
    }

  public:
    Mem() { first_buffer = NULL; add_buffer(); }
    ~Mem() { clear(); }

    void clear() {
      while (first_buffer) {
	MemBuffer *next = first_buffer->next;
	free(first_buffer);
	first_buffer = next;
      }
      pos = 0;
    }

    void *alloc( size_t n ) {
      void *result;
    
      /* do memory alignment to multiples of 4 */
      if (n % 4)
	n += 4 - (n % 4);
    
      if (first_buffer == NULL || pos+n > MEMBUFFER_SIZE)
	add_buffer();
      if (pos+n > MEMBUFFER_SIZE)
	throw "Allocation of memory block larger than MEMBUFFER_SIZE attempted!";
    
      result = (void*)(first_buffer->buffer + pos);
      pos += n;
      return result;
    }

    //class MemError {};

  };

}
#endif
