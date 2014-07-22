%**************************************************************************
%  File:     map2.fst  
%  Author:   Helmut Schmid; IMS, University of Stuttgart
%  Content:  deletes tags on the surface string
%  Modified: Fri Jun 17 14:34:43 2005 (schmid)   
%**************************************************************************

% definition of the symbol classes
#include "symbols.fst"


% delete unwanted symbols on the "surface"
% and map the feature <Stems> to the more specific features
% <BaseStem> <DerivStem> and <CompStem>

ALPHABET = [#Letter# #WordClass# #StemType# #Origin# #Complex# #InflClass#] \
	<classic>:[#classic#]

[#Affix#] .* |\
<NoDef>? (<Stem>:<BaseStem>  .* <base>  |\
	  <Stem>:<DerivStem> .* <deriv> |\
	  <Stem>:<CompStem>  .* <comp>) .*
