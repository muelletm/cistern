/*******************************************************************/
/*                                                                 */
/*  FILE     make-compact.h                                        */
/*  MODULE   make-compact                                          */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*******************************************************************/

#ifndef _MAKE_COMPACT_H_
#define _MAKE_COMPACT_H_

#include "fst.h"
#include "compact.h"

namespace SFST {

  class MakeCompactTransducer : CompactTransducer {
    
  private:
    void count_arcs(Node *node, VType vmark);
    void store_arcs(Node *node, VType vmark);
    void store_finalp( FILE *file );
    void store_first_arcs( FILE *file );
    void store_target_nodes( FILE *file );
    void store_labels( FILE *file );

  public:
    MakeCompactTransducer( Transducer &a, Level sort=upper );

    void sort( Level );
    void store( FILE *file );
  };

}
#endif
