%**************************************************************************
%  File:     phon.fst
%  Author:   Helmut Schmid; IMS, University of Stuttgart
%  Content:  morphophonological rules
%  Modified: Mon Nov  6 09:23:31 2006 (schmid)   
%**************************************************************************

#include "symbols.fst"


%%% adjective rules %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% easy+er -> easier
% easy+est -> easiest
% late+er -> later
% red -> redder

ALPHABET = [#Letter# #EntryType# #WordClass# #Cap#] \
	y:i e:<> <ADJ>:<dup>

$C$ = [<ADJ><ADV>]

$T$ = ([hwxaioue] $C$) e <=> <> (r | st) &\
	[#Cons#] y <=> i ($C$ e(r|st)) &\
	([#Cons#][#vowel#][#cons#]) <ADJ> <=> <dup> (e(r|st))

ALPHABET = [#Letter# #EntryType# #WordClass# #Cap#]

#=1# = #cons#
$X$ = [#=1#] <>:<ADJ> <dup>:[#=1#]
$Rule1$ = $T$ || (.* $X$)* .*


%%% noun rules %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% story -> stories
% wish -> wishes  (also verbs)

ALPHABET = [#Letter# #EntryType# #WordClass# #Cap#] 

$T$ = {y}:{ie} ^-> ([#cons#] __ [<N><V>] s)

$Rule2$ = $T$ || <>:e ^-> (([szx]|[cs]h) __ [<N><V>] s)


%%% verb rules %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% delete -> deleting

ALPHABET = [#Letter# #EntryType# #WordClass# #Cap#] e:<> y:i

$Rule3$ =  e <=> <> (<V> (ing|ed)) &\
	[#cons#] y <=> i  (<V> ed)


%%% capitalisation %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

ALPHABET = [#Letter#] [#WordClass#]:<>

$T$ = .* \
	([#EntryType#]:<> ([#LETTER#]:[#letter#] | [#letter#]) .*)* \
	[#Cap#]

$Capitalisation$ = $T$ || (\
	([#LETTER#]:[#letter#] | [#letter#]) .* <LC>:<> |\
	([#letter#]:[#LETTER#] | [#LETTER#]) .* <UC>:<> |\
	.* <FC>:<>)

$Rule1$ || $Rule2$ || $Rule3$ || $Capitalisation$
