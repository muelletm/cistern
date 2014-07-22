%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  File:     compoundfilter.fst
%  Author:   Helmut Schmid; IMS, University of Stuttgart
%  Content:  enforcement of compounding constraints
%  Modified: Fri Jun 17 14:14:08 2005 (schmid)   
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


#include "symbols.fst"


% Compounding filter
% Compounds are restricted to nouns and adjectives

$org$ = [#Origin#]:<>

% symbols occurring in non-compounds
$T$ = [#Letter# #EntryType#] | [#WordClass#]:<> | $org$

% expression matching non-compounds
$TS$ = $T$*

% expression matching compounds
$TC$ = ($T$ | <comp>:<>)*


($TS$ [<ADV><CARD><V><OTHER>] |\
 $TC$ [<ADJ><N>]) \
<base>:<> $org$ [#InflClass#]
