%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  File:         deko.fst
%  Author:       Helmut Schmid; IMS, University of Stuttgart
%  Date:         July 2003
%  Content:      enforcement of derivation and composition constraints
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% expression matching either a simplex word form
% or the features of the last morphem

$C1$ = [\!-\~¡-ÿ<n><e><d><~n><Ge-Nom><UL><SS><FB><ge><no-ge> \
	<Initial><NoHy><NoPref><NoDef><Pref_Stems>]

$B$  = [<Base_Stems><Deriv_Stems><Kompos_Stems>]

$C2$ = [\!-\~¡-ÿ<n><e><d><~n><Ge-Nom><UL><SS><FB><Suff_Stems>]

$C3$ = [\!-\~¡-ÿ<n><e><d><~n><ge><Ge-Nom><UL><SS><FB> \
<ABK><ADJ><ADV><CARD><DIGCARD><NE><NN><PRO><V><ORD><OTHER> \
<base><deriv><kompos> \
<nativ><frei><gebunden><kurz><lang><fremd><klassisch> \
<NSNeut_es_e><NSFem_0_n><NSFem_0_en><NSMasc_es_e><NSMasc_es_$e> \
<NSMasc-s/$sse>]

$FLEX$ = [<Abk_ADJ><Abk_ADV><Abk_ART><Abk_DPRO><Abk_KONJ><Abk_NE-Low><Abk_NE>\
<Abk_NN-Low><Abk_NN><Abk_PREP><Abk_VPPAST><Abk_VPPRES><Adj$><Adj$e><Adj+(e)> \
<Adj+><Adj&><Adj+Lang><Adj+e><Adj-el/er><Adj0><Adj0-Up><AdjComp><AdjNN> \
<AdjNNSuff><AdjPos><AdjPosAttr><AdjPosPred><AdjPosSup><AdjSup><Adj~+e> \
<Adv><Card><Ord><DigOrd><Circp><FamName_0><FamName_s><Name-Pl_0><Name-Pl_x> \
<Intj><IntjUp><Konj-Inf><Konj-Kon><Konj-Sub><Konj-Vgl><N?/Pl_0><N?/Pl_x> \
<NFem-Deriv><NFem-a/en><NFem-in><NFem-is/en><NFem-is/iden><NFem-s/$sse> \
<NFem-s/sse><NFem-s/ssen><NFem/Pl><NFem/Sg><NFem_0_$><NFem_0_$e><NFem_0_e> \
<NFem_0_en><NFem_0_n><NFem_0_s><NFem_0_x><NGeo+er/in><NGeo-Fem_0> \
<NGeo-Invar><NGeo-Masc_0><NGeo-Masc_s><NGeo-Neut+Loc><NGeo-Neut_0> \
<NGeo-Neut_s><NGeo-Pl_0><NMasc-ns><NMasc-Adj><NMasc-s/$sse><NMasc-s/Sg><NMasc-s/sse> \
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
<Postp-Akk><Postp-Dat><Postp-Gen><Pref/Adj> \
<Pref/Adv><Pref/N><Pref/ProAdv><Pref/Sep><Pref/V><Prep-Akk><Prep-Dat> \
<Prep-Gen><Prep/Art-m><Prep/Art-n><Prep/Art-r><Prep/Art-s><ProAdv><Ptkl-Adj> \
<Ptkl-Ant><Ptkl-Neg><Ptkl-Zu><VAImpPl><VAImpSg><VAPastKonj2><VAPres1/3PlInd> \
<VAPres1SgInd><VAPres2PlInd><VAPres2SgInd><VAPres3SgInd><VAPresKonjPl> \
<VAPresKonjSg><VInf+PPres><VInf><VMPast><VMPastKonj><VMPresPl><VMPresSg> \
<VPPast><VPPres><VPastIndReg><VPastIndStr><VPastKonjStr><VPresKonj> \
<VPresPlInd><VVPP-en><VVPP-t><VVPastIndReg><VVPastIndStr><VVPastKonjReg> \
<VVPastKonjStr><VVPastStr><VVPres1+Imp><VVPres1><VVPres2+Imp0><VVPres2+Imp> \
<VVPres2><VVPres2t><VVPres><VVPresPl><VVPresSg><VVReg-el/er><VVReg><WAdv>]

$TAIL$ = ($C1$* $B$ $C2$*)? $C3$* $FLEX$?


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Herkunft Filter
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

ALPHABET = [\!-\~¡-ÿ<n><e><d><~n><Ge-Nom><UL><SS><FB>] \
% stem types
<Base_Stems><Deriv_Stems><Kompos_Stems><Pref_Stems><Suff_Stems> \
% category features
<ABK><ADJ><ADV><CARD><DIGCARD><NE><NN><ORD><PRO><V><OTHER> \
% stem type features
<base><deriv><kompos> \
% other features
<ge><no-ge><Initial><NoHy><NoPref><NoDef>

$ANY$ = .*

$FILTER$ = (\
	<nativ>:<>   <Suff_Stems> <nativ>:<> |\
	<frei>:<>    <Suff_Stems> <frei>:<> |\
	<gebunden>:<><Suff_Stems> <gebunden>:<> |\
	<kurz>:<>    <Suff_Stems> <kurz>:<> |\
	<lang>:<>    <Suff_Stems> <lang>:<> |\
	<fremd>:<>   <Suff_Stems> <fremd>:<> |\
	<NSFem_0_en>:<>	<Suff_Stems>	<NSFem_0_en>:<> |\
	<NSFem_0_n>:<>	<Suff_Stems>	<NSFem_0_n>:<> |\
	<NSMasc-s/$sse>:<>	<Suff_Stems>	<NSMasc-s/$sse>:<> |\
	<NSMasc_es_$e>:<>	<Suff_Stems>	<NSMasc_es_$e>:<> |\
	<NSMasc_es_e>:<>	<Suff_Stems>	<NSMasc_es_e>:<> |\
	<NSNeut_es_e>:<>	<Suff_Stems>	<NSNeut_es_e>:<> |\
	<NGeo-$er-NMasc_s_0>:<>	<Suff_Stems>	<NGeo-$er-NMasc_s_0>:<> |\
	<NGeo-$er-Adj0-Up>:<>	<Suff_Stems>	<NGeo-$er-Adj0-Up>:<> |\
	<NGeo-$isch-Adj+>:<>	<Suff_Stems>	<NGeo-$isch-Adj+>:<> |\
	<NGeo-0-Name-Fem_0>:<>	<Suff_Stems>	<NGeo-0-Name-Fem_0>:<> |\
	<NGeo-0-Name-Masc_s>:<>	<Suff_Stems>	<NGeo-0-Name-Masc_s>:<> |\
	<NGeo-0-Name-Neut_s>:<>	<Suff_Stems>	<NGeo-0-Name-Neut_s>:<> |\
	<NGeo-a-Name-Fem_s>:<>	<Suff_Stems>	<NGeo-a-Name-Fem_s>:<> |\
	<NGeo-a-Name-Neut_s>:<>	<Suff_Stems>	<NGeo-a-Name-Neut_s>:<> |\
	<NGeo-aner-NMasc_s_0>:<><Suff_Stems>	<NGeo-aner-NMasc_s_0>:<> |\
	<NGeo-aner-Adj0-Up>:<>	<Suff_Stems>	<NGeo-aner-Adj0-Up>:<> |\
	<NGeo-anisch-Adj+>:<>	<Suff_Stems>	<NGeo-anisch-Adj+>:<> |\
	<NGeo-e-NMasc_n_n>:<>	<Suff_Stems>	<NGeo-e-NMasc_n_n>:<> |\
	<NGeo-e-Name-Fem_0>:<>	<Suff_Stems>	<NGeo-e-Name-Fem_0>:<> |\
	<NGeo-e-Name-Neut_s>:<>	<Suff_Stems>	<NGeo-e-Name-Neut_s>:<> |\
	<NGeo-ei-Name-Fem_0>:<>	<Suff_Stems>	<NGeo-ei-Name-Fem_0>:<> |\
	<NGeo-en-Name-Neut_s>:<><Suff_Stems>	<NGeo-en-Name-Neut_s>:<> |\
	<NGeo-er-NMasc_s_0>:<>	<Suff_Stems>	<NGeo-er-NMasc_s_0>:<> |\
	<NGeo-er-Adj0-Up>:<>	<Suff_Stems>	<NGeo-er-Adj0-Up>:<> |\
	<NGeo-0-NMasc_s_0>:<>	<Suff_Stems>	<NGeo-0-NMasc_s_0>:<> |\
	<NGeo-0-Adj0-Up>:<>	<Suff_Stems>	<NGeo-0-Adj0-Up>:<> |\
	<NGeo-erisch-Adj+>:<>	<Suff_Stems>	<NGeo-erisch-Adj+>:<> |\
	<NGeo-ese-NMasc_n_n>:<>	<Suff_Stems>	<NGeo-ese-NMasc_n_n>:<> |\
	<NGeo-esisch-Adj+>:<>	<Suff_Stems>	<NGeo-esisch-Adj+>:<> |\
	<NGeo-ianer-NMasc_s_0>:<><Suff_Stems>	<NGeo-ianer-NMasc_s_0>:<> |\
	<NGeo-ianisch-Adj+>:<>	<Suff_Stems>	<NGeo-ianisch-Adj+>:<> |\
	<NGeo-ien-Name-Neut_s>:<><Suff_Stems>	<NGeo-ien-Name-Neut_s>:<> |\
	<NGeo-ier-NMasc_s_0>:<>	<Suff_Stems>	<NGeo-ier-NMasc_s_0>:<> |\
	<NGeo-isch-Adj+>:<>	<Suff_Stems>	<NGeo-isch-Adj+>:<> |\
	<NGeo-istan-Name-Neut_s>:<><Suff_Stems>	<NGeo-istan-Name-Neut_s>:<> |\
	<NGeo-land-Name-Neut_s>:<><Suff_Stems>	<NGeo-land-Name-Neut_s>:<> |\
	<NGeo-ner-NMasc_s_0>:<>	<Suff_Stems>	<NGeo-ner-NMasc_s_0>:<> |\
	<NGeo-ner-Adj0-Up>:<>	<Suff_Stems>	<NGeo-ner-Adj0-Up>:<> |\
	<NGeo-nisch-Adj+>:<>	<Suff_Stems>	<NGeo-nisch-Adj+>:<>)

$HERKUNFT$ = ($ANY$ $FILTER$)* $TAIL$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% check of stem type feature
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

ALPHABET = [\!-\~¡-ÿ<n><e><d><~n><Ge-Nom><SS><FB>] \
% stem types
<Base_Stems><Kompos_Stems><Deriv_Stems><Pref_Stems><Suff_Stems> \
% category features
<ABK><ADJ><ADV><CARD><DIGCARD><NE><NN><ORD><PRO><V><OTHER> \
% other features
<ge><no-ge><Initial><NoHy><NoPref><NoDef>

$ANY$ = .*

$FILTER$ = (\
	<deriv>:<>  <Suff_Stems> <deriv>:<> |\
	<kompos>:<> <Suff_Stems> <kompos>:<>)


$STEMTYPE$ = ($ANY$ $FILTER$)* $TAIL$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% check of category feature
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

ALPHABET = [\!-\~¡-ÿ<n><e><d><~n><Ge-Nom><SS><FB>] \
% stem types
<Base_Stems><Kompos_Stems><Deriv_Stems><Pref_Stems><Suff_Stems> \
% other features
<ge><no-ge><Initial><NoHy><NoPref><NoDef>

$ANY$ = .*

$FILTER$ = (\
	<ABK>:<>	<Suff_Stems>	<ABK>:<> |\
	<ADJ>:<>	<Suff_Stems>	<ADJ>:<> |\
	<ADV>:<>	<Suff_Stems>	<ADV>:<> |\
	<CARD>:<>	<Suff_Stems>	<CARD>:<> |\
	<DIGCARD>:<>	<Suff_Stems>	<DIGCARD>:<> |\
	<NN>:<>		<Suff_Stems>	<NN>:<> |\
	<NE>:<>		<Suff_Stems>	<NE>:<> |\
	<ORD>:<>	<Suff_Stems>	<ORD>:<> |\
	<PRO>:<>	<Suff_Stems>	<PRO>:<> |\
	<V>:<>		<Suff_Stems>	<V>:<>)

$CATCHECK$ = ($ANY$ $FILTER$)* $TAIL$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% phonological rules
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

ALPHABET = [\!-\~¡-ÿ<n><e><d><~n><Ge-Nom><SS><FB>] \
% stem types
<Base_Stems><Kompos_Stems><Deriv_Stems><Pref_Stems><Suff_Stems> \
% other features
<ge><no-ge><Initial><NoHy><NoPref><NoDef>

$Cons$ = [bcdfghjklmnpqrstvwxyzß]

$UMLAUTUNG$ =  .* \
($Cons$ ([aou]:[äöü] | a:ä (a:<> | u)) $Cons$* (e[rl])?<Suff_Stems><UL>:<>)?\
$TAIL$

ALPHABET = [\!-\~¡-ÿ<n><e><d><~n><Ge-Nom><SS><FB>] \
% stem types
<Base_Stems><Kompos_Stems><Deriv_Stems><Pref_Stems><Suff_Stems> \
% other features
<ge><no-ge><Initial><NoHy><NoPref><NoDef> \
[i]:<>

$SUFFPHON$ = (((i | $Cons$y) <Suff_Stems>) i <=> <>)  $TAIL$

$SUFFFILTER$ = $HERKUNFT$ || $STEMTYPE$ || $CATCHECK$ || $UMLAUTUNG$
$SUFFFILTER$ = $SUFFFILTER$ || $SUFFPHON$



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Prefix Filter
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

ALPHABET = [\!-\~¡-ÿ<n><e><d><~n><Ge-Nom><SS><FB>]\
% stem types
<Base_Stems><Deriv_Stems><Kompos_Stems><Pref_Stems><Suff_Stems> \
% other features
<ge><no-ge><Initial><NoHy><NoPref><NoDef>
 
$ANY$ = .*

$bdk$ = [<base><deriv><kompos>]
$klassisch$ = [<frei><gebunden><kurz><lang>]
$NS$ = [<NSNeut_es_e><NSFem_0_n><NSFem_0_en><NSMasc_es_e><NSMasc_es_$e>\
	<NSMasc-s/$sse>]

$FILTER$ = \
% prefixes like "ver" delete the <ge> marker
(<no-ge>:<> <Pref_Stems> [\!-\~¡-ÿ<n><e><d><~n>]* \
	{<V><nativ>}:{<>} <NoDef>? <ge>:<> $ANY$ <V> $bdk$<nativ>) |\
(<Pref_Stems> [\!-\~¡-ÿ<n><e><d><~n><Ge-Nom><SS><FB>]* (\
	{<ADJ><nativ>}:{<>}    $ANY$ <ADJ>$bdk$ <nativ> |\
	{<ABK><nativ>}:{<>}    $ANY$ <ABK>$bdk$ <nativ> |\
	{<NN><nativ>}:{<>}     $ANY$ <NN> $bdk$ <nativ>  |\
	{<NN><fremd>}:{<>}     $ANY$ <NN> $bdk$ <fremd>  |\
	{<NE><nativ>}:{<>}     $ANY$ <NE> $bdk$ <nativ>  |\
	{<NE><fremd>}:{<>}     $ANY$ <NE> $bdk$ <fremd>  |\
	{<ADJ><fremd>}:{<>}    $ANY$ <ADJ>$bdk$ <fremd>  |\
	{<V><nativ>}:{<>}      $ANY$ <V>  $bdk$ <nativ>   |\
	{<V><nativ>}:{<>}      $ANY$ <V>  $bdk$ $NS$ |\
	{<ADJ><klassisch>}:{<>}$ANY$ <ADJ>$bdk$ $klassisch$ |\
	{<NN><klassisch>}:{<>} $ANY$ <NN> $bdk$ $klassisch$ |\
	{<V><klassisch>}:{<>}  $ANY$ <V>  $bdk$ $klassisch$))

$PREFFILTER$ = $FILTER$ $FLEX$?



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Compound Filter
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% deletes base stem and compound stem features

ALPHABET = [\!-\~¡-ÿ<n><e><d><~n><Ge-Nom><SS><FB>] \
% stem types
<Base_Stems><Deriv_Stems><Kompos_Stems><Pref_Stems><Suff_Stems> \
% category features
[<ABK><ADJ><ADV><CARD><DIGCARD><NE><NN><ORD><PRO><V><OTHER>]:<> \
% Herkunft features
[<nativ><frei><gebunden><kurz><lang><fremd><klassisch>]:<> \
% other features
<ge> <NoPref>:<>

$ANY2$  = .*
$ANY$ = (. | <kompos>:<>)*

$hk$ = [<nativ><frei><gebunden><kurz><lang><fremd><klassisch>]:<>

$KOMPOSFILTER$ = \
	(<Initial>:<> | <NoHy> | <NoDef>)? \
	($ANY2$ [<ABK><ADV><CARD><NE><PRO><V><ORD><OTHER>]:<> |\
	 <>:<VADJ> $ANY$ <kompos>:<> $ANY2$ <V>:<> |\
	 $ANY$  [<ADJ><NN>]:<>) \
	<base>:<> $hk$ $FLEX$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% insertion of the prefix "ge" controlled by the lexical marker <ge>
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

$C1$ = [\!-\~¡-ÿ <n><~n><e><d><NoHy><NoDef><VADJ><CB><FB><UL><DEL-S><SS> \
	<Low#><Up#><Fix#> <^imp><^zz><^UC><^Ax><^pl><^Gen><^Del>]

$C2$ = $C1$|[<Base_Stems><Deriv_Stems><Kompos_Stems><Pref_Stems><Suff_Stems>]

% replace <ge> with "ge" if followed by perfect participle marker
% or ge-nominalisation otherwise delete <ge>
% in complex lexicon entries as for "haushalten" <ge> is not followed
% by <Base_Stems>

$GE$ =  $C2$* |\
	$C2$* <ge>:<> <Base_Stems>? {<>}:{ge} $C1$* <^pp>:<> $C1$* |\
	$C2$* <ge>:<> <Deriv_Stems>? {<>}:{ge} $C1$* <Suff_Stems><Ge-Nom>:<> $C1$* |\
	$C2$* <ge>:<> <Base_Stems>? $C1$* |\
	$C2$* <Base_Stems> $C1$* <^pp>:<> $C1$*


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% insertion of infinitival "zu"
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

$C1$ = [\!-\~¡-ÿ <n><~n><e><d><NoHy><NoDef><VADJ><CB><FB><UL><DEL-S><SS> \
	<Low#><Up#><Fix#> <^imp><^UC><^Ax><^pl><^Gen><^Del>]

$C2$ = $C1$|[<Base_Stems><Deriv_Stems><Kompos_Stems><Pref_Stems><Suff_Stems>]

% insert "zu" after verbal prefixes if followed by infinitive marker

$ZU$ =  $C2$* |\
%	<Base_Stems> $C1$* <^zz>:<> $C1$* |\
	$C2$* <Pref_Stems> $C1$* <Base_Stems> {<>}:{zu} $C1$* <^zz>:<> $C1$*


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Imperatives have no separable prefixes
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

$C1$ = [\!-\~¡-ÿ <n><~n><e><d><NoHy><NoDef><VADJ><CB><FB><UL><DEL-S><SS> \
	<Low#><Up#><Fix#> <^UC><^Ax><^pl><^Gen><^Del>]

$C2$ = $C1$ | [<Base_Stems><Deriv_Stems><Kompos_Stems>]:<CB> \
	    | [<Pref_Stems><Suff_Stems>]:<CB>


$IMP$ =  $C2$* | <Base_Stems>:<CB> $C1$* <^imp>:<> $C1$*


$INFIXFILTER$ = $GE$ || $ZU$ || $IMP$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Upper/Lower Case Markers
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


$C$ = [\!-\~¡-ÿ <n><~n><e><d><FB><UL><DEL-S><SS><NoDef><^imp><^zz><ge><^Ax><^pl><^Gen><^Del>]

$S$  = $C$ ($C$ | <CB>)*
$S2$ = (<CB>:<>[A-ZÀ-Þ] | <CB>:<>?[a-zà-þ]) $S$

$UPLOW$ = <^UC>:<> [<NoHy><NoDef>]? <>:<^UC> $S2$ <Low#>:<> |\
	  <NoHy>? (<CB>:<>  $S$ <Fix#>:<> |\
		   [<CB><>]:<^UC> $S$ <Up#>:<> |\
		   [<CB><>]:<CB>  $S$ <Low#>:<>)
