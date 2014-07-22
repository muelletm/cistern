%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  File:     inflectionfilter.fst
%  Author:   Helmut Schmid; IMS, Universitaet Stuttgart
%  Content:  definition of the inflectional filter
%  Modified: Fri Jun 17 14:29:02 2005 (schmid)   
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% definition of the symbol classes
#include "symbols.fst"


% The following inflection filter ensures that the base stems
% are combined with the correct inflectional endings

ALPHABET = [#Letter# #EntryType# #WordClass#]

$=1$ = [#InflClass#]:<>

.* $=1$ $=1$ .* [#Cap#]
