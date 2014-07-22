%**************************************************************************
%  File:     map1.fst  
%  Author:   Helmut Schmid; IMS, University of Stuttgart
%  Content:  deletes tags in the analysis string
%  Modified: Thu Jun 23 10:22:28 2005 (schmid)   
%**************************************************************************

% definition of the symbol classes
#include "symbols.fst"


% delete unwanted symbols in the analysis

ALPHABET = [#Letter# #WordClass#] \
	<>:[#StemType# #Origin-cl# #InflClass#]

<>:<NoDef>? <>:<Stem> .* |\
<>:<Suffix> <>:[#Complex#] <>:[#WordClass#] .* <SUFF>:<> |\
<>:<Prefix> .* <>:[#WordClass#] .* <PREF>:<>
