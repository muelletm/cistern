/*******************************************************************/
/*                                                                 */
/*     File: sgi.h                                                 */
/*   Author: Helmut Schmid                                         */
/*  Purpose:                                                       */
/*  Created: Thu Sep 11 15:58:25 2008                              */
/* Modified: Fri Sep 12 08:17:03 2008 (schmid)                     */
/* Modified: Wed May 26 12:54:00 2010 (hfst)                       */
/*******************************************************************/

#ifndef _SGI_INCLUDED
#define _SGI_INCLUDED

#if HAVE_CONFIG_H
#  include <config.h>
#endif

#if HAVE_BACKWARD_HASH_MAP
#  include <backward/hash_map>
#elif HAVE_EXT_HASH_MAP
#  include <ext/hash_map>
#elif HAVE_HASH_MAP
#  include <hash_map>
#elif SGIext
#  include <ext/hash_map>
#elif SGI__gnu_cxx
#  include <ext/hash_map>
#else
#  warning "unknown hash_map"
#  include <hash_map>
#endif
#if HAVE_BACKWARD_HASH_SET
#  include <backward/hash_set>
#elif HAVE_EXT_HASH_SET
#  include <ext/hash_set>
#elif HAVE_HASH_SET
#  include <hash_set>
#elif SGIext
#  include <ext/hash_set>
#elif SGI__gnu_cxx
#  include <ext/hash_set>
#else
#  warning "missing hash_set"
#  include <hash_set>
#endif

// Hfst addition
namespace SFST 
{
// from <http://gcc.gnu.org/onlinedocs/libstdc++/manual/backwards.html>
#ifdef __GNUC__
#  if __GNUC__ < 3
  using ::hash_map;
  using ::hash_set;
  using ::hash;
#  elif __GNUC__ == 3 && __GNUC_MINOR__ == 0
  using std::hash_map;
  using std::hash_set;
  using std::hash;
#  else 
  using __gnu_cxx::hash_map;
  using __gnu_cxx::hash_set;
  using __gnu_cxx::hash;
#  endif
#else
  using std::hash_map;
  using std::hash_set;
  using std::hash;
#endif
}

#endif
