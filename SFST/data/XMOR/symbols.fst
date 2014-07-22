%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  File:     symbols.fst
%  Author:   Helmut Schmid; IMS, Universitaet Stuttgart
%  Content:  definition of symbol classes
%  Modified: Fri Mar 24 10:10:07 2006 (schmid)   
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% All symbols used by the morphology should be defined here


%%% Single Character Symbols %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% lower case consonants
#cons# = bcdfghjklmnpqrstvwxyz

% upper case consonants
#CONS# = BCDFGHJKLMNPQRSTVWXYZ

% all consonants
#Cons# = #cons# #CONS#


% lower case vowels
#vowel# = aeiou

% upper case vowels
#VOWEL# = AEIOU

% all vowels
#Vowel# = #vowel# #VOWEL#


% lower case letters
#letter# = #cons# #vowel#

% upper case letters
#LETTER# = #CONS# #VOWEL#

% all letters
#Letter# = #Cons# #Vowel#


%%% Lexicon Entry Markers %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% affix type features
#Affix# = <Prefix><Suffix>

% stem type features (internally used)
#BDKStem# = <BaseStem><DerivStem><CompStem>

% all stem types including the general stem feature <Stem>
% used in the lexicon
#EntryType# = <Stem> #BDKStem# #Affix#


%%% Agreement Features %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% word class features
#WordClass# = <ADJ><ADV><CARD><N><V>

% stem type feature
#StemType# = <base><deriv><comp>

% classic origin features
#classic# = <free><bound><short><long>

% all origin features
#Origin# = <native><foreign> #classic#

% origin features including the internally used feature <classic>
% which represents the disjunction stored in #classic#
#Origin-cl# = #Origin# <classic>

% complexity features
#Complex# = <simplex><prefderiv><suffderiv>

% inflection class features
#InflClass# = <AdjReg><AdvReg><NounReg><NounSg><NounPl><VerbReg>

% all agreement features
#AgrFeat# = #WordClass# #StemType# #Origin-cl#

% all agreement features + inflection class features
#AgrFeatInfl# = #AgrFeat# #InflClass#


%%% Analysis Features %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% number feature
#Number# = <sg><pl>

% gender feature
#Gender# = <masc><fem><neut>

% case feature
#Case# = <nom><gen><dat><acc>

% Person Feature
#Person# = <1><2><3>

% degree feature
#Degree# = <positive><comparative><superlative>

% verbal features
#VerbFeat# = <pres><past><part>

% affix markers
#AFF# = <PREF><SUFF>

% Morphosyntactic Features
#MorphSyn# = #Number# #Gender# #Case# #Person#\
	     #Degree# #VerbFeat# #AFF#


%%% Trigger Features %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% capitalisation feature: lower case, capitalized or fixed
#Cap# = <LC><Cap><Fix>

% Features used to mark the boundaries of morphemes and inflection
#Boundary# = <MB><IB>

% all triggers
% <NoDef> marks lexicon entries without default stems
#Trigger# = #Cap# #Boundary# <NoDef>


%%% General Symbol Classes %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

#Tag# = #EntryType# #AgrFeatInfl# #MorphSyn# #Trigger#

#AllSym# = #Letter# #Tag#

