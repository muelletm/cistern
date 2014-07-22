/*******************************************************************/
/*                                                                 */
/*  FILE     compact.C                                             */
/*  MODULE   compact                                               */
/*  PROGRAM  SFST                                                  */
/*  AUTHOR   Helmut Schmid, IMS, University of Stuttgart           */
/*                                                                 */
/*  PURPOSE  Code needed for analysing data                        */
/*                                                                 */
/*******************************************************************/

#include <stdio.h>
#include <math.h>

#include <limits.h>

#include "compact.h"

namespace SFST {

  using std::equal_range;
  using std::vector;
  using std::pair;

  class label_less {
  public:
    bool operator()(const Label l1, const Label l2) const {
      return l1.upper_char() < l2.upper_char();
    }
  };

  const int BUFFER_SIZE=1000;


  /*******************************************************************/
  /*                                                                 */
  /*  CompactTransducer::convert                                     */
  /*                                                                 */
  /*******************************************************************/

  void CompactTransducer::convert( CAnalysis &cana, Analysis &ana )

  {
    ana.resize(cana.size());
    for( size_t i=0; i<cana.size(); i++ )
      ana[i] = label[cana[i]];
  }


  /*******************************************************************/
  /*                                                                 */
  /*  CompactTransducer::analyze                                     */
  /*                                                                 */
  /*******************************************************************/

  void CompactTransducer::analyze(unsigned int n, vector<Character> &input,
				  size_t ipos, CAnalysis &ca, 
				  vector<CAnalysis> &analyses )
  {
    // "n" is the number of the current transducer node/state
    // "input" is the sequence of input symbols
    // "ipos" is the input position currently analysed
    // "ca" stores the incomplete analysis string
    // "analyses" stores the analyses found so far

    if (analyses.size() > 10000)
      return; // limit the maximal number of analyses

    // Is the input string fully analyzed and the current node a final node?
    if (finalp[n] && ipos == input.size())
      // store the new analysis
      analyses.push_back(ca);

    // follow the epsilon transitions
    // first_arc[n] is the number of the first outgoing transition of node n
    // first_arc[n+1]-1 is the number of the last outgoing transition of node n
    // first_arc[n+1] is the number of the first outgoing transition of node n+1
    unsigned int i;
    for( i=first_arc[n]; 
	 i<first_arc[n+1] && label[i].upper_char() == Label::epsilon; 
	 i++)
      {
	ca.push_back(i);
	analyze(target_node[i], input, ipos, ca, analyses);
	ca.pop_back();
      }

    // follow the non-epsilon transitions

    // scan the next input symbol
    if (ipos < input.size()) {
      // find the set of arcs with matching upper character in the sorted list
      pair<Label*,Label*>range = 
	equal_range(label+i, label+first_arc[n+1], Label(input[ipos]), 
		    label_less());
      unsigned int to = (unsigned int)(range.second - label);

      // follow the non-epsilon transitions
      for( i=(unsigned)(range.first-label); i<to; i++) {
	ca.push_back(i);
	analyze(target_node[i], input, ipos+1, ca, analyses);
	ca.pop_back();
      }
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  CompactTransducer::analyze_string                              */
  /*                                                                 */
  /*******************************************************************/

  void CompactTransducer::analyze_string( char *s, vector<CAnalysis> &analyses )

  {
    // "s" input string to be analyzed
    // "analyses" is the data structure in which the results are stored
    // and returned

    vector<Character> input;

    alphabet.string2symseq( s, input );

    analyses.clear();
    CAnalysis ca; // data structure where the current incomplete analysis
    // is stored
    analyze(0, input, 0, ca, analyses); // start the analysis

    if (analyses.size() > 10000)
      fprintf(stderr,"Warning: Only the first 10000 analyses considered for \"%s\"!\n", s);
  
    if (simplest_only && analyses.size() > 1)
      disambiguate( analyses ); // select the simplest analyses
  }



  /*******************************************************************/
  /*                                                                 */
  /*  CompactTransducer::~CompactTransducer                          */
  /*                                                                 */
  /*******************************************************************/

  CompactTransducer::~CompactTransducer()

  {
    delete[] finalp;
    delete[] first_arc;
    delete[] label;
    delete[] target_node;
    delete[] final_logprob;
    delete[] arc_logprob;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  CompactTransducer::CompactTransducer                           */
  /*                                                                 */
  /*******************************************************************/

  CompactTransducer::CompactTransducer()

  {
    both_layers = false;
    simplest_only = false;
    number_of_nodes = 0;
    number_of_arcs = 0;
    finalp = NULL;
    first_arc = NULL;
    label = NULL;
    target_node = NULL;
    arc_logprob = final_logprob = (float*)NULL;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  CompactTransducer::read_finalp                                 */
  /*                                                                 */
  /*******************************************************************/

  void CompactTransducer::read_finalp( FILE *file )

  {
    int k=0;
    unsigned char n=0;
    for( size_t i=0; i<number_of_nodes; i++ ) {
      if (k == 0) {
	n = (unsigned char)fgetc(file);
	k = 8;
      }
      k--;
      if (n & (1 << k))
	finalp[i] = 1;
      else
	finalp[i] = 0;
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  CompactTransducer::read_first_arcs                             */
  /*                                                                 */
  /*******************************************************************/

  void CompactTransducer::read_first_arcs( FILE *file )

  {
    int k=0;
    unsigned int n=0;
    int bits=(int)ceil(log(number_of_arcs+1)/log(2));

    for( size_t i=0; i<=number_of_nodes; i++ ) {
      first_arc[i] = n >> (sizeof(n)*8 - bits);
      n <<= bits;
      k -= bits;
      if (k < 0) {
	read_num(&n,sizeof(n),file);
	first_arc[i] |= n >> (sizeof(n)*8 + k);
	n <<= -k;
	k += (int)sizeof(n) * 8;
      }
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  CompactTransducer::read_target_nodes                           */
  /*                                                                 */
  /*******************************************************************/

  void CompactTransducer::read_target_nodes( FILE *file )

  {
    int k=0;
    unsigned int n=0;
    int bits=(int)ceil(log(number_of_nodes)/log(2));

    for( size_t i=0; i<number_of_arcs; i++ ) {
      target_node[i] = n >> (sizeof(n)*8 - bits);
      n <<= bits;
      k -= bits;
      if (k < 0) {
	read_num(&n,sizeof(n),file);
	target_node[i] |= n >> (sizeof(n)*8 + k);
	n <<= -k;
	k += (int)sizeof(n) * 8;
      }
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  CompactTransducer::read_labels                                 */
  /*                                                                 */
  /*******************************************************************/

  void CompactTransducer::read_labels( FILE *file )

  {
    size_t N=0;
    vector<Label> Num2Label(alphabet.size());
    for( Alphabet::const_iterator it=alphabet.begin();
	 it != alphabet.end(); it++ )
      {
	Label l=*it;
	Num2Label[N++] = l;
      }

    int k=0;
    unsigned int n=0;
    int bits=(int)ceil(log((double)alphabet.size())/log(2));

    for( size_t i=0; i<number_of_arcs; i++ ) {
      unsigned int l = n >> (sizeof(n)*8 - bits);
      n <<= bits;
      k -= bits;
      if (k < 0) {
	read_num(&n,sizeof(n),file);
	l |= n >> (sizeof(n)*8 + k);
	n <<= -k;
	k += (int)sizeof(n) * 8;
      }
      label[i] = Num2Label[l];
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  CompactTransducer::read_probs                                  */
  /*                                                                 */
  /*******************************************************************/

  void CompactTransducer::read_probs( FILE *file )

  {
    size_t n,m;
    fread(&n, sizeof(n), 1, file);
    if (fread(&m, sizeof(n), 1, file) != 1 ||
	n != node_count() || m != arc_count())
      {
	fprintf(stderr,"Error: incompatible probability file!\n");
	exit(1);
      }
    final_logprob = new float[n];
    arc_logprob = new float[m];
    fread(final_logprob, sizeof(float), n, file);
    if (fread(arc_logprob, sizeof(float), n, file) != n) {
      fprintf(stderr,"Error: in probability file!\n");
      exit(1);
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  CompactTransducer::CompactTransducer                           */
  /*                                                                 */
  /*******************************************************************/

  CompactTransducer::CompactTransducer( FILE *file, FILE *pfile )

  {
    both_layers = false;
    simplest_only = false;

    if (fgetc(file) != 'c')
      throw "Error: wrong file format (not a compact transducer)\n";

    alphabet.read(file);

    read_num(&number_of_nodes,sizeof(number_of_nodes),file);
    read_num(&number_of_arcs,sizeof(number_of_arcs),file);

    if (!ferror(file)) {
      // memory allocation
      finalp = new char[number_of_nodes];
      first_arc = new unsigned[number_of_nodes+1];
      label = new Label[number_of_arcs];
      target_node = new unsigned[number_of_arcs];
    
      // reading the data
      read_finalp(file);
      read_first_arcs(file);
      read_labels(file);
      read_target_nodes(file);
    }

    if (pfile == NULL)
      arc_logprob = final_logprob = (float*)NULL;
    else
      read_probs(pfile);
  }


  /*******************************************************************/
  /*                                                                 */
  /*  CompactTransducer::longest_match2                              */
  /*                                                                 */
  /*******************************************************************/

  void CompactTransducer::longest_match2(unsigned int n, char *string, int l, 
					 CAnalysis &ca, int &bl, CAnalysis &ba)
  {
    // n: transducer state
    // string: rest string
    // l: length of current analysis
    // bl: length of the currently longest match
    // ca: current analysis
    // ba: best analysis

    if (finalp[n] && l > bl) {
      // store the new analysis
      bl = l;
      ba = ca; // copy the arc vector
    }

    // follow the epsilon transitions
    unsigned int i;
    for( i=first_arc[n]; 
	 i<first_arc[n+1] && label[i].upper_char() == Label::epsilon; 
	 i++)
      {
	ca.push_back(i);
	longest_match2(target_node[i], string, l, ca, bl, ba);
	ca.pop_back();
      }

    // follow the non-epsilon transitions
    char *end=string;
    int c=alphabet.next_code(end, false, false);
    l += (int)(end - string);
    if (c != EOF) {
      // find the set of arcs with matching upper character in the sort list
      pair<Label*,Label*>range = 
	equal_range(label+i, label+first_arc[n+1], Label((Character)c), 
		    label_less());
      unsigned int to = (unsigned int)(range.second - label);
      for( i=(unsigned)(range.first-label); i<to; i++) {
	ca.push_back(i);
	longest_match2(target_node[i], end, l, ca, bl, ba);
	ca.pop_back();
      }
    }
  }


  /*******************************************************************/
  /*                                                                 */
  /*  CompactTransducer::print_analysis                              */
  /*                                                                 */
  /*******************************************************************/

  char *CompactTransducer::print_analysis( CAnalysis &cana )

  {
    Analysis ana;
    convert(cana, ana);
    return alphabet.print_analysis( ana, both_layers );
  }


  /*******************************************************************/
  /*                                                                 */
  /*  CompactTransducer::longest_match                               */
  /*                                                                 */
  /*******************************************************************/

  const char *CompactTransducer::longest_match( char* &string )

  {
    vector<char> analysis;
    CAnalysis ca, ba;
    int l=0;
    longest_match2(0, string, 0, ca, l, ba);

    // no match? return the next character
    if (ba.size() == 0) {
      int c=alphabet.next_code(string, false, false);
      return alphabet.code2symbol((Character)c);
    }

    string += l;
    return print_analysis( ba );
  }


  /*******************************************************************/
  /*                                                                 */
  /*  CompactTransducer::disambiguate                                */
  /*                                                                 */
  /*******************************************************************/

  void CompactTransducer::disambiguate( vector<CAnalysis> &analyses )

  {
    // compute the scores
    int bestscore=INT_MIN;
    vector<int> score;
    Analysis ana;

    for( size_t i=0; i<analyses.size(); i++ ) {
      convert(analyses[i], ana);
      score.push_back(alphabet.compute_score(ana));
      if (bestscore < score[i])
	bestscore = score[i];
    }

    // delete suboptimal analyses
    size_t k=0;
    for( size_t i=0; i<analyses.size(); i++ )
      if (score[i] == bestscore)
	analyses[k++] = analyses[i];
    analyses.resize(k);
  }


  /*******************************************************************/
  /*                                                                 */
  /*  CompactTransducer::train2                                      */
  /*                                                                 */
  /*******************************************************************/

  bool CompactTransducer::train2( char *s, vector<double> &arcfreq, 
				  vector<double> &finalfreq )
  {
    vector<CAnalysis> analyses;
    vector<Label> input;
    alphabet.string2labelseq( s, input );

    CAnalysis ca; // data structure where the analysis is stored
    unsigned int n=0;
    bool failure=false;
    for( size_t i=0; i<input.size(); i++ ) {
      failure = true;
      for( unsigned int k=first_arc[n]; k<first_arc[n+1]; k++) {
	if (label[k] == input[i]) {
	  ca.push_back(k);
	  n = target_node[k];
	  failure = false;
	  break;
	}
      }
      if (failure)
	break;
    }
    if (failure || !finalp[n]) {
      fprintf(stderr,"Warning: The following input is not covered:\n%s\n", s);
      return false;
    }

    for( size_t k=0; k<ca.size(); k++ )
      arcfreq[ca[k]]++;
    finalfreq[target_node[ca.back()]]++;

    return true;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  CompactTransducer::train                                       */
  /*                                                                 */
  /*******************************************************************/

  bool CompactTransducer::train( char *s, vector<double> &arcfreq, 
				 vector<double> &finalfreq )
  {
    vector<CAnalysis> analyses;
    vector<Character> input;
    alphabet.string2symseq( s, input );

    CAnalysis ca; // data structure where the current incomplete analysis
    // is stored
    analyze(0, input, 0, ca, analyses); // start the analysis

    if (analyses.size() > 10000)
      return true; // ignore inputs with more than 10000 analyses
    else if (analyses.size() == 0)
      return false;
  
    if (simplest_only && analyses.size() > 1)
      disambiguate( analyses ); // select the simplest analyses

    if (analyses.size() > 0) {
      double incr = 1.0 / (double)analyses.size();
      CAnalysis arcs;

      for( size_t i=0; i<analyses.size(); i++ ) {
	CAnalysis &arcs=analyses[i];
	for( size_t k=0; k<arcs.size(); k++ )
	  arcfreq[arcs[k]] += incr;
	finalfreq[target_node[arcs.back()]] += incr;
      }
    }
    return true;
  }


  /*******************************************************************/
  /*                                                                 */
  /*  CompactTransducer::estimate_probs                              */
  /*                                                                 */
  /*******************************************************************/

  void CompactTransducer::estimate_probs( vector<double> &arcfreq, 
					  vector<double> &finalfreq )
  {
    // turn frequencies into probabilities
    for( size_t n=0; n<finalfreq.size(); n++ ) {
      double sum = finalfreq[n];
      for( size_t a=first_arc[n]; a<first_arc[n+1]; a++ )
	sum += arcfreq[a];
      if (sum == 0.0)
	sum = 1.0;
      finalfreq[n] = finalfreq[n] / sum;
      for( size_t a=first_arc[n]; a<first_arc[n+1]; a++ )
	arcfreq[a] = arcfreq[a] / sum;
    }
  }



  /*******************************************************************/
  /*                                                                 */
  /*  CompactTransducer::compute_probs                               */
  /*                                                                 */
  /*******************************************************************/

  void CompactTransducer::compute_probs( vector<CAnalysis> &analyses, 
					 vector<double> &prob )
  {
    prob.resize(analyses.size());
    double sum=0.0;
    for( size_t i=0; i<analyses.size(); i++ ) {
      CAnalysis &a=analyses[i];

      // compute the probability
      double logprob=0.0;
      for( size_t k=0; k<a.size(); k++ )
	logprob += arc_logprob[a[k]];
      logprob += final_logprob[target_node[a.back()]];
      prob[i] = exp(logprob);
      sum += prob[i];
    }

    // sort the analyses
    vector<CAnalysis> oldanalyses(analyses);
    vector<double> oldprob(prob);
    for( size_t i=0; i<analyses.size(); i++ ) {
      prob[i] = -1.0;
      size_t n=0;
      for( size_t k=0; k<oldanalyses.size(); k++ )
	if (prob[i] < oldprob[k]) {
	  prob[i] = oldprob[k];
	  n = k;
	}
      analyses[i] = oldanalyses[n];
      oldprob[n] = -1.0;
      prob[i] /= sum; // normalization
    }
  }
}
