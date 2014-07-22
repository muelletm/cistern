%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  File:     prefixfilter.fst
%  Author:   Helmut Schmid; IMS, University of Stuttgart
%  Content:  enforcement of derivational constraints
%  Modified: Wed Jun 22 17:04:56 2005 (schmid)   
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


#include "symbols.fst"


% Check agreement of the word class and origin features
% Delete the agreement features of the prefix

ALPHABET = [#Letter#] <Suffix>

#=wc# = #WordClass#
#=orig# = #Origin#

<Prefix> .* \
[#=wc#]:<> [#=orig#]:<> [#BDKStem#] .* [#=wc#] [#StemType#] [#=orig#] \
[#InflClass#]?
