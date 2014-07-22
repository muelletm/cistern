%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  File:         smor.fst
%  Author:       Helmut Schmid; IMS, Universitaet Stuttgart
%  Date:         April 2003
%  Content:      main file of the German morphology   
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

$PHON$ = "<phon.a>"

#include "map.fst"

% read the morphemes

$LEX$ = "lexicon"


% add repetitive prefixes

$LEX$ = $LEX$ |\
	<Pref_Stems>(ur<PREF>)+<ADJ,NN><nativ> |\
	<Pref_Stems>(vor<PREF>)+<ADJ,NN><nativ>

% delete certain symbols on the upper and lower level

$LEX$ = $MAP1$ || $LEX$ || $MAP2$

% define a transducer for numeric morphemes
#include "NUM.fst"

% add the numeric stems to the other morphems
$LEX$ = $LEX$ | $Num_Stems$


%**************************************************************************
% extraction of sublexica
%**************************************************************************

ALPHABET = [\!-\~¡-ÿ] <FB><SS><n><~n><e><d><Ge-Nom><UL> \
% category features
<ABK><ADJ><ADV><CARD><DIGCARD><NE><NN><PRO><V><ORD><OTHER> \
% stem type features
<base><deriv><kompos> \
% Herkunft features
<nativ><frei><gebunden><kurz><lang><fremd><klassisch> \
% inflection classes
<Abk_ADJ><Abk_ADV><Abk_ART><Abk_DPRO><Abk_KONJ><Abk_NE-Low><Abk_NE> \
<Abk_NN-Low><Abk_NN><Abk_PREP><Abk_VPPAST><Abk_VPPRES><Adj$><Adj$e><Adj+(e)> \
<Adj+><Adj&><Adj+Lang><Adj+e><Adj-el/er><Adj0><Adj0-Up><AdjComp><AdjSup> \
<AdjNN><AdjNNSuff><AdjPos><AdjPosAttr><AdjPosPred><AdjPosSup><AdjSup><Adj~+e>\
<Adv><Card><Ord><DigOrd><Circp><FamName_0><FamName_s><Name-Pl_0><Name-Pl_x> \
<Intj><IntjUp><Konj-Inf><Konj-Kon><Konj-Sub><Konj-Vgl><N?/Pl_0><N?/Pl_x> \
<NFem-Deriv><NFem-a/en><NFem-in><NFem-is/en><NFem-is/iden><NFem-s/$sse> \
<NFem-s/sse><NFem-s/ssen><NFem/Pl><NFem/Sg><NFem_0_$><NFem_0_$e><NFem_0_e> \
<NFem_0_en><NFem_0_n><NFem_0_s><NFem_0_x><NGeo+er/in><NGeo-Fem_0> \
<NGeo-Invar><NGeo-Masc_0><NGeo-Masc_s><NGeo-Neut+Loc><NGeo-Neut_0> \
<NGeo-Neut_s><NGeo-Pl_0><NMasc-Adj><NMasc-ns><NMasc-s/$sse><NMasc-s/Sg><NMasc-s/sse> \
<NMasc-s0/sse><NMasc-us/en><NMasc-us/i><NMasc/Pl><NMasc/Sg_0><NMasc/Sg_es> \
<NMasc/Sg_s><NMasc_0_x><NMasc_en_en=in><NMasc_en_en><NMasc_es_$e> \
<NMasc_es_$er><NMasc_es_e><NMasc_es_en><NMasc_n_n=$in><NMasc_n_n=in> \
<NMasc_n_n><NMasc_s_$><NMasc_s_$x><NMasc_s_0=in><NMasc_s_0><NMasc_s_e=in> \
<NMasc_s_e><NMasc_s_en=in><NMasc_s_en><NMasc_s_n><NMasc_s_s><NMasc_s_x> \
<NNeut-0/ien><NNeut-Dimin><NNeut-Herz><NNeut-a/ata><NNeut-a/en><NNeut-on/a> \
<NNeut-s/$sser><NNeut-s/sse><NNeut-um/a><NNeut-um/en><NNeut/Pl><NNeut/Sg_0> \
<NNeut/Sg_en><NNeut/Sg_es><NNeut/Sg_s><NNeut_0_x><NNeut_es_$e><NNeut_es_$er> \
<NNeut_es_e><NNeut_es_en><NNeut_es_er><NNeut_s_$><NNeut_s_0><NNeut_s_e><NNeut_s_en> \
<NNeut_s_n><NNeut_s_s><NNeut_s_x><Name-Fem_0><Name-Fem_s><Name-Masc_0> \
<Name-Masc_s><Name-Neut_s><Name-Neut_0><Name-Neut+Loc><Name-Invar> \
<NSNeut_es_e><NSFem_0_n><NSFem_0_en><NSMasc_es_e><NSMasc_es_$e> \
<NSMasc-s/$sse> \
<NGeo-$er-NMasc_s_0><NGeo-$er-Adj0-Up><NGeo-$isch-Adj+><NGeo-0-Name-Fem_0>\
<NGeo-0-Name-Masc_s><NGeo-0-Name-Neut_s><NGeo-a-Name-Fem_s> \
<NGeo-a-Name-Neut_s><NGeo-aner-NMasc_s_0><NGeo-aner-Adj0-Up> \
<NGeo-anisch-Adj+><NGeo-e-NMasc_n_n><NGeo-e-Name-Fem_0><NGeo-e-Name-Neut_s> \
<NGeo-ei-Name-Fem_0><NGeo-en-Name-Neut_s><NGeo-0-NMasc_s_0><NGeo-0-Adj0-Up> \
<NGeo-er-NMasc_s_0><NGeo-er-Adj0-Up><NGeo-erisch-Adj+><NGeo-ese-NMasc_n_n> \
<NGeo-esisch-Adj+><NGeo-ianer-NMasc_s_0> \
<NGeo-ianisch-Adj+><NGeo-ien-Name-Neut_s><NGeo-ier-NMasc_s_0><NGeo-isch-Adj+>\
<NGeo-istan-Name-Neut_s><NGeo-land-Name-Neut_s><NGeo-ner-NMasc_s_0> \
<NGeo-ner-Adj0-Up><NGeo-nisch-Adj+> \
<Postp-Akk><Postp-Dat><Postp-Gen><Pref/Adj> \
<Pref/Adv><Pref/N><Pref/ProAdv><Pref/Sep><Pref/V><Prep-Akk><Prep-Dat> \
<Prep-Gen><Prep/Art-m><Prep/Art-n><Prep/Art-r><Prep/Art-s><ProAdv><PInd-Invar><Ptkl-Adj> \
<Ptkl-Ant><Ptkl-Neg><Ptkl-Zu><VAImpPl><VAImpSg><VAPastKonj2><VAPres1/3PlInd> \
<VAPres1SgInd><VAPres2PlInd><VAPres2SgInd><VAPres3SgInd><VAPresKonjPl> \
<VAPresKonjSg><VInf+PPres><VInf><VMPast><VMPastKonj><VMPresPl><VMPresSg> \
<VPPast><VPPres><VPastIndReg><VPastIndStr><VPastKonjStr><VPresKonj> \
<VPresPlInd><VVPP-en><VVPP-t><VVPastIndReg><VVPastIndStr><VVPastKonjReg> \
<VVPastKonjStr><VVPastStr><VVPres1+Imp><VVPres1><VVPres2+Imp0><VVPres2+Imp> \
<VVPres2><VVPres2t><VVPres><VVPresPl><VVPresSg><VVReg-el/er><VVReg><WAdv>

$ANY$ = .*

% symbols appearing before the morpheme class symbol
$I$ = [<Initial><NoHy><ge><no-ge><NoPref>]

% Herkunft features
$HK$ = [<nativ><frei><gebunden><kurz><lang><fremd><klassisch>]

$NoDef2NULL$ = ($ANY$ | $HK$ | $I$ | <NoDef>:<> |\
	[<Base_Stems><Deriv_Stems><Kompos_Stems><Pref_Stems><Suff_Stems>])*

% base derivation and compound stems (without derivation suffixes)
$I$ = ($I$ | <NoDef>)*
$BDKStems$ = $LEX$ || ($I$ [<Base_Stems><Deriv_Stems><Kompos_Stems>] $ANY$)
$BaseStems$ = $BDKStems$ || ($I$ <Base_Stems> $ANY$)

% prefix morphems
$PrefStems$ = $LEX$ || ($I$ <Pref_Stems> $ANY$)
$VPrefStems$ = $PrefStems$ || ($I$ <Pref_Stems> $ANY$ <V> $ANY$)

% derivation suffixes which combine with simplex stems
$SimplexSuffStems$   = $LEX$ || ($I$ <Suff_Stems><simplex>:<> $ANY$)

% derivation suffixes which combine with suffixed stems
$SuffDerivSuffStems$ = $LEX$ || ($I$ <Suff_Stems><suffderiv>:<> $ANY$)

% derivation suffixes which combine with prefixed stems
$PrefDerivSuffStems$ = $LEX$ || ($I$ <Suff_Stems><prefderiv>:<> $ANY$)

% derivation suffixes which combine with a number and a simplex stem
$QuantSuffStems$ = $LEX$ || (<QUANT>:<> $I$ <Suff_Stems><simplex>:<> $ANY$)


#include "deko.fst"
#include "flexion.fst"
#include "defaults.fst"


%**************************************************************************
% derivation and composition
%**************************************************************************

% derivation suffixes to be added to simplex stems
$Suffs1$ = ($SimplexSuffStems$ $SuffDerivSuffStems$*)?

% derivation suffixes to be added to prefixed stems
$Suffs2$ = ($PrefDerivSuffStems$ $SuffDerivSuffStems$*)?

% suffixes for "Dreifarbigkeit"
$QSuffs$ = $QuantSuffStems$ $SuffDerivSuffStems$*


% dreistündig, 3stündig, 3-stündig, Mehrfarbigkeit
$Sx$ = $Quant$ ($BDKStems$ $QSuffs$ || $SUFFFILTER$)

$S0$ = $BDKStems$ $Suffs1$ || $SUFFFILTER$

$P1$ = $PrefStems$ $S0$ || $PREFFILTER$

$S1$ = $P1$ $Suffs2$ || $SUFFFILTER$

$TMP$ = $S0$ | $S1$ | $Sx$

$TMP$ = $TMP$+ || $KOMPOSFILTER$


%**************************************************************************
% add inflection and filter out incorrect combinations of stems and infl.
%**************************************************************************

$ANY$ = [\!-\~¡-ÿ <FB><SS><n><~n><e><d><Ge-Nom><UL> <NoHy><ge><no-ge><CB><VADJ> \
	<Base_Stems><Deriv_Stems><Kompos_Stems><Pref_Stems><Suff_Stems>]*

$BASE$ = $TMP$ $FLEXION$ || $ANY$ $FLEXFILTER$ || $INFIXFILTER$


#include "FIX.fst"
#include "PRO.fst"

$BASE$ = $Fix_Stems$ | $Pro_Stems$ | $BASE$

$BASE$ = $BASE$ || $UPLOW$


%**************************************************************************
%  application of phonological rules
%**************************************************************************

$BASE$ = <>:<WB> $BASE$ <>:<WB> || $PHON$


%**************************************************************************
%  whole word in upper case
%**************************************************************************

$TMP$ = ([a-zà-þ]:[A-ZÀ-Þ] | [\!-`\{-ß] | {ß}:{SS})*
$TMP$ = <NoHy>:<>? $TMP$ [a-zà-þ]:[A-ZÀ-Þ] $TMP$
$UC$ = <UC>:<> ($BASE$ || $TMP$)


%**************************************************************************
%  capitalisation
%**************************************************************************

$TMP$ = <NoHy>:<>? [a-zà-þ]:[A-ZÀ-Þ] [\!-\~¡-ÿ]*
$CAP$ = <CAP>:<> ($BASE$ || $TMP$)


%**************************************************************************
%  hyphenated words
%**************************************************************************

$Pref$ = \{:<> [\!-\,\.-ÿ]+ \}:<> \-
$TMP$ =  <NoHy>:<>? (\- [A-ZÀ-Þ]:[a-zà-þ])? [\!-\~¡-ÿ]*

$BASE$ = \
(\-? $Pref$* \
$BASE$ || $TMP$ \
) | $Pref$+ <+TRUNC>:<>

$CAP$ | $UC$ | \
$BASE$
