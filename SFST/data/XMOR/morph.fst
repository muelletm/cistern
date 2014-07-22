%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  File:     morph.fst
%  Author:   Helmut Schmid; IMS, Universitaet Stuttgart
%  Content:  main file of the morphology   
%  Modified: Thu Jun 23 10:23:15 2005 (schmid)   
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% definition of the symbol classes
#include "symbols.fst"


% read the morphemes
$LEX$ = "lexicon"


% apply the transducers which deletes unwanted symbols in the analysis
% string (map1) and the surface string (map2) of the lexicon entries

$LEX$ = "<map1.a>" || $LEX$ || "<map2.a>"

% creation of sublexica for the different types of entries

% stems
$BDKStem$ = $LEX$ || <NoDef>:<>? [#BDKStem#] [#AllSym#]*

% prefixes
$Prefix$ = $LEX$ || <Prefix> [#AllSym#]*

% suffixes combining with simplex stems
$SimplexSuffix$ = $LEX$ || <Suffix><simplex>:<> [#AllSym#]*

% suffixes combining with suffix derivation stems
$SuffDerivSuffix$ = $LEX$ || <Suffix><suffderiv>:<> [#AllSym#]*

% suffixes combining with prefix derivation stems
$PrefDerivSuffix$ = $LEX$ || <Suffix><prefderiv>:<> [#AllSym#]*


% generation of default derivational and compounding stems

#include "defaultstems.fst"


%%% Derivation and Composition %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% sequence of derivational suffixes to be added to simplex stems
$SimplexSuffixes$ = ($SimplexSuffix$ $SuffDerivSuffix$*)?

% sequence of derivational suffixes to be added to prefixed stems
$PrefDerivSuffixes$ = ($PrefDerivSuffix$ $SuffDerivSuffix$*)?

% suffix derivation with a simplex base
$SuffixFilter$ = "<suffixfilter.a>"
$S0$ = $BDKStem$ $SimplexSuffixes$ || $SuffixFilter$

% prefix derivation
$P1$ = $Prefix$ $S0$ || "<prefixfilter.a>"

% suffix derivation with a "prefderiv" base
$S1$ = $P1$ $PrefDerivSuffixes$ || $SuffixFilter$

% combination of the different derivations
$Morph$ = $S0$ | $S1$


%%% Compounding %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% $Morph$ = $Morph$* $Morph$ || "<compoundfilter.a>"
$Morph$ = $Morph$ || "<compoundfilter.a>"


%%% Inflection %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

$Morph$ = $Morph$ "<inflection.a>" || "<inflectionfilter.a>"


%%% Morpho-Phonological Rules %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

$Morph$ || "<phon.a>"
