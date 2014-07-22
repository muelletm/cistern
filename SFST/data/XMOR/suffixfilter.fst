%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  File:     suffixfilter.fst
%  Author:   Helmut Schmid; IMS, University of Stuttgart
%  Content:  enforcement of derivational constraints
%  Modified: Fri Jun 17 16:31:36 2005 (schmid)   
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


#include "symbols.fst"


% Definition of an expression which matches either a simplex word form
% or the features of the last morpheme of a complex word form

% expression matching prefixes
$C1$ = [#Letter# #BDKStem# <Prefix> <Suffix>]

% expression matching the last morpheme and its agreement features
$C2$ = [#Letter# #AgrFeat#]

$Tail$ = $C1$* $C2$* [#InflClass#]?


%%% Feature Checking %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% The agreement features are deleted

$=1$ =	[#WordClass#]:<>
$=2$ =	[#StemType#]:<>
$=3$ =	[#Origin#]:<>

$T$ = $=1$ $=2$ $=3$ <Suffix> $=1$ $=2$ $=3$

ALPHABET = [#Letter# #BDKStem#]

$Filter$ = .* (.* $T$)* $Tail$


%%% Phonological Rules %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% morpho-phonological rules accompanying derivational processes could
% be added here

ALPHABET = [#AllSym#] e:<>

% write+able => writable
$PhonRules$ = e <=> <> (<Suffix> a)


%%% Resulting Transducer %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

$Filter$ || $PhonRules$
