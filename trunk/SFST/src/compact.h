/*******************************************************************/
/*                                                                 */
/*  FILE     compact.h                                             */
/*  MODULE   compact                                               */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*  PURPOSE  finite state tools                                    */
/*                                                                 */
/*******************************************************************/

#ifndef _COMPACT_H_
#define _COMPACT_H_

#include "alphabet.h"

#include <vector>

namespace SFST {

  typedef std::vector<unsigned int> CAnalysis;
    
  class CompactTransducer {
    
  protected:

    // the following data structures are used to store the nodes

    unsigned int number_of_nodes; // number of nodes in the transducer
    char *finalp;  // finalp[i] is 1 if node i is final and 0 otherwise
    unsigned int *first_arc;  // first_arc[i] is the number of the first
    // arc outgoing from node i

    // the following data structures are used to store the transition arcs

    unsigned int number_of_arcs; // total number of arcs in the transducer
    Label *label;              // the label (character pair) of arc i
    unsigned int *target_node; // target node of arc i

    // the following data structures are used to store the stochastic parameters
    float *final_logprob;
    float *arc_logprob;

    // functions needed to read the transducer from a file

    void read_finalp( FILE *file );
    void read_first_arcs( FILE *file );
    void read_target_nodes( FILE *file );
    void read_labels( FILE *file );
    void read_probs( FILE *file );

    // functions needed to analyze data with the transducer

    void analyze( unsigned int n, std::vector<Character> &ch, size_t ipos,
		  CAnalysis&, std::vector<CAnalysis>&);

    // function selecting the simplest morphological analysis

    int compute_score( CAnalysis &ana );
    void disambiguate( std::vector<CAnalysis> &analyses );

    // functions for longest-match analysis of input data

    void longest_match2(unsigned int, char*, int, CAnalysis&, int&, CAnalysis&);

    void convert( CAnalysis &cana, Analysis &ana );
  
  public:
    size_t node_count() { return number_of_nodes; };
    size_t arc_count() { return number_of_arcs; };

    bool both_layers;   // print surface and analysis symbols
    bool simplest_only; // print only the simplest analyses

    Alphabet alphabet;  // data structure which maps symbols to numeric codes
    CompactTransducer(); // dummy constructor
    CompactTransducer( FILE*, FILE *pfile=NULL ); // reads a (stochastic) transducer
    ~CompactTransducer();  // destroys a transducer
  
    // the analysis function returns the set of analyses for the string "s"
    // in the argument "analyses"
    void analyze_string( char *s, std::vector<CAnalysis > &analyses );

    void compute_probs( std::vector<CAnalysis> &analyses, std::vector<double> &prob );
    char *print_analysis( CAnalysis &ana );
  
    // longest-match analysis
    const char *longest_match( char*& );

    // EM training
    bool train2( char *s, std::vector<double> &arcfreq, std::vector<double> &finalfreq );
    bool train( char *s, std::vector<double> &arcfreq, std::vector<double> &finalfreq );
    void estimate_probs( std::vector<double> &arcfreq, std::vector<double> &finalfreq );

    // robust analysis
    float robust_analyze_string( char *string, std::vector<CAnalysis> &analyses,
				 float ErrorsAllowed );
  };
}
#endif
