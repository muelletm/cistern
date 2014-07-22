/*******************************************************************/
/*                                                                 */
/*  FILE     lowmem.C                                              */
/*  MODULE   lowmem                                                */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*  PURPOSE  Code needed for analysing data                        */
/*                                                                 */
/*******************************************************************/

#include <stdio.h>

#include "lowmem.h"

using std::vector;

namespace SFST {

  const int BUFFER_SIZE=1000;


  /*******************************************************************/
  /*                                                                 */
  /*  LowMemTransducer::analyze                                      */
  /*                                                                 */
  /*******************************************************************/

  void LowMemTransducer::analyze( const LMNode &node, 
				  vector<Character> &input, size_t ipos,
				  Analysis &ca, vector<Analysis> &analyses )
  {
    if (node.finalp && ipos == input.size())
      // store the new analysis
      analyses.push_back(ca);

    // follow the transitions
    for( int i=0; i<node.number_of_arcs; i++ ) {
      ca.push_back(node.arc[i].label);
      LMNode target(node.arc[i].tnodepos, lmafile);

      if (node.arc[i].label.upper_char() == Label::epsilon)
	analyze(target, input, ipos, ca, analyses);
      else if (ipos < input.size() &&
	       node.arc[i].label.upper_char() == (Character)input[ipos])
	analyze(target, input, ipos+1, ca, analyses);

      ca.pop_back();
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  LowMemTransducer::analyze_string                               */
  /*                                                                 */
  /*******************************************************************/

  void LowMemTransducer::analyze_string(char *string, vector<Analysis> &analyses)

  {
    vector<Character> input;
    alphabet.string2symseq( string, input );

    Analysis ca;
    analyses.clear();
    analyze(*rootnode, input, 0, ca, analyses);

    if (simplest_only)
      alphabet.disambiguate( analyses );
  }



  /*******************************************************************/
  /*                                                                 */
  /*  LowMemTransducer::LowMemTransducer                             */
  /*                                                                 */
  /*******************************************************************/

  LowMemTransducer::LowMemTransducer( FILE *file )

  {
    simplest_only = false;
    lmafile = file;
    if (fgetc(file) != 'l')
      throw "Error: wrong file format (not a lowmem transducer)\n";
    alphabet.read(file);

    rootnode = new LMNode(ftell(file), lmafile);
  }

}
