% *************************************************************************
%  File:         stems.fst
%  Author:       Helmut Schmid; IMS, University of Stuttgart
%  Date:         April 2003
%  Content:      reads the stems from the lexicon files and deletes
% 		 certain symbols on the upper and lower layer
%**************************************************************************

%**************************************************************************
% definition of the lexical features which are deleted in the analysis string
%**************************************************************************

$CAT$ = [<ABK><ADJ><ADV><CARD><DIGCARD><NE><NN><PRO><V><ORD><OTHER><KSF>]

$DELCAT$ = <>:[<ABK><ADJ><ADV><CARD><DIGCARD><NE><NN><PRO><V><ORD><OTHER> \
<CARD,DIGCARD,NE><ADJ,CARD><ADJ,NN><CARD,NN><CARD,NE><ABK,ADJ,NE,NN> \
<ADJ,NE,NN><ABK,NE,NN><NE,NN><ABK,CARD,NN><ABK,NN> \
<ADJ,CARD,NN,V><ADJ,NN,V><ABK,ADJ,NE,NN,V><ADJ,NE,NN,V> \
<ADV,NE,NN,V><ABK,NE,NN,V><NE,NN,V><ABK,NN,V><NN,V>]

ALPHABET = [\!-\~¡-ÿ] <SS><FB> n:<n> e:<e> d:<d> <>:<~n> <Ge-Nom> <>:<UL> \
% stem types
<>:[<Base_Stems><Kompos_Stems><Deriv_Stems><Suff_Stems><Pref_Stems>] \
% prefix suffix marker
<PREF><SUFF><QUANT> \
% stem type features
<>:[<base><deriv><kompos>] \
% marker for ge prefix
<>:<ge> |\
% Herkunft features
<>:[<nativ><frei><gebunden><kurz><lang><fremd><klassisch> \
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
<NGeo-0-$er-$er><NGeo-0-$er-$isch><NGeo-0-aner-aner><NGeo-0-aner-anisch> \
<NGeo-0-e-isch><NGeo-0-er-er><NGeo-0-er-erisch><NGeo-0-er-isch> \
<NGeo-0-ese-esisch><NGeo-0-ianer-ianisch><NGeo-0-0-0> \
<NGeo-0-ner-isch><NGeo-0-ner-nisch><NGeo-0fem-er-erisch><NGeo-0masc-er-isch> \
<NGeo-0masc-ese-esisch><NGeo-a-er-isch><NGeo-a-ese-esisch><NGeo-afem-er-isch>\
<NGeo-e-er-er><NGeo-e-er-isch><NGeo-efem-er-isch> \
<NGeo-ei-e-isch><NGeo-en-aner-anisch><NGeo-en-e-$isch> \
<NGeo-en-e-isch><NGeo-en-er-er><NGeo-en-er-isch> \
<NGeo-ien-e-isch><NGeo-ien-er-isch><NGeo-ien-ese-esisch> \
<NGeo-ien-ianer-ianisch><NGeo-ien-ier-isch> \
<NGeo-istan-e-isch><NGeo-land-$er-$er><NGeo-land-e-isch><NGeo-land-e-nisch>] \
% complexity agreement features
<>:[<simplex><komposit><suffderiv><prefderiv>] \
% complex lexicon entries
<>:[<Simplex><Komplex><Komplex_abstrakt><Komplex_semi><Nominalisierung><Kurzwort>] \
% inflection classes
<>:[<Abk_ADJ><Abk_ADV><Abk_ART><Abk_DPRO><Abk_KONJ><Abk_NE-Low> \
<Abk_NE><Abk_NN-Low><Abk_NN><Abk_PREP><Abk_VPPAST><Abk_VPPRES><Adj$><Adj$e> \
<Adj+(e)><Adj+><Adj&><Adj+Lang><Adj+e><Adj-el/er><Adj0><Adj0-Up><AdjComp> \
<AdjSup><AdjNN><AdjNNSuff><AdjPos><AdjPosAttr><AdjPosPred><AdjPosSup><AdjSup>\
<Adj~+e><Adv><Circp><FamName_0><FamName_s><Name-Pl_0><Name-Pl_x> \
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
<Postp-Akk><Postp-Dat><Postp-Gen><Pref/Adj> \
<Pref/Adv><Pref/N><Pref/ProAdv><Pref/Sep><Pref/V><Prep-Akk><Prep-Dat> \
<Prep-Gen><Prep/Art-m><Prep/Art-n><Prep/Art-r><Prep/Art-s><ProAdv><PInd-Invar><Ptkl-Adj> \
<Ptkl-Ant><Ptkl-Neg><Ptkl-Zu><VAImpPl><VAImpSg><VAPastKonj2><VAPres1/3PlInd> \
<VAPres1SgInd><VAPres2PlInd><VAPres2SgInd><VAPres3SgInd><VAPresKonjPl> \
<VAPresKonjSg><VInf+PPres><VInf><VMPast><VMPastKonj><VMPresPl><VMPresSg> \
<VPPast><VPPres><VPastIndReg><VPastIndStr><VPastKonjStr><VPresKonj> \
<VPresPlInd><VVPP-en><VVPP-t><VVPastIndReg><VVPastIndStr><VVPastKonjReg> \
<VVPastKonjStr><VVPastStr><VVPres1+Imp><VVPres1><VVPres2+Imp0><VVPres2+Imp> \
<VVPres2><VVPres2t><VVPres><VVPresPl><VVPresSg><VVReg-el/er><VVReg><WAdv>] \
% disjunctive features ???
<>:[<CARD,DIGCARD,NE><ADJ,CARD><ADJ,NN><CARD,NN><CARD,NE><ABK,ADJ,NE,NN>\
<ADJ,NE,NN><ABK,NE,NN><NE,NN><ABK,CARD,NN><ABK,NN><ADJ,CARD,NN,V><ADJ,NN,V> \
<ABK,ADJ,NE,NN,V><ADJ,NE,NN,V><ADV,NE,NN,V><ABK,NE,NN,V><NE,NN,V><ABK,NN,V> \
<NN,V> \
<frei,fremd,gebunden><frei,fremd,gebunden,kurz><frei,fremd,gebunden,lang> \
<fremd,gebunden,lang><frei,fremd,kurz><frei,fremd,lang><frei,gebunden> \
<frei,gebunden,kurz,lang><frei,gebunden,lang><frei,lang><klassisch,nativ> \
<fremd,klassisch,nativ><fremd,klassisch><frei,nativ><frei,fremd,nativ> \
<fremd,nativ><komposit,prefderiv,simplex,suffderiv><prefderiv,suffderiv> \
<komposit,prefderiv,simplex><komposit,simplex,suffderiv><komposit,simplex>\
<prefderiv,simplex,suffderiv><prefderiv,simplex><simplex,suffderiv>]

$ANY$ = .*
$ANY2$ = .* ([\!-\~¡-ÿ] ([\!-\~¡-ÿ] | $CAT$)*)? .*

% category feature is only preserved to mark komp and deriv stems
$MAP1$ = <>:[<QUANT><Initial><NoHy><ge><no-ge><NoPref><NoDef>]* (\
<>:[<Base_Stems><Pref_Stems>] $ANY2$ $DELCAT$ |\
<>:[<Deriv_Stems><Kompos_Stems>] $ANY2$ $CAT$ |\
<>:<Pref_Stems> $ANY$ $DELCAT$ |\
<>:<Suff_Stems> $ANY$ $DELCAT$ $ANY$ $CAT$ <>:<base> |\
<>:<Suff_Stems> $ANY$ $DELCAT$ $ANY$ $DELCAT$ <SUFF> <>:<base>|\
<>:<Suff_Stems> $ANY$ $DELCAT$ $ANY$ $CAT$ <SUFF> <>:[<deriv><kompos>]) $ANY$


%**************************************************************************
% mapping from disjunctive features to disjunctions
%**************************************************************************

ALPHABET = [\!-\~¡-ÿ]<SS><FB><n><e><d><~n><Ge-Nom> <UL> \
<Initial><NoHy><ge><no-ge><NoPref><NoDef> \
% stem types
<Base_Stems><Kompos_Stems><Deriv_Stems><Suff_Stems><Pref_Stems> \
% category features
<ABK><ADJ><ADV><CARD><DIGCARD><NE><NN><PRO><V><ORD><OTHER><KSF>:<NN> \
% prefix suffix marker (not needed by morphology)
[<PREF><SUFF>]:<> <QUANT> \
% stem type features
<base><deriv><kompos> \
% marker for ge prefix
<ge> |\
% Herkunft features
<nativ><frei><gebunden><kurz><lang><fremd><klassisch> \
<NSNeut_es_e><NSFem_0_n><NSFem_0_en><NSMasc_es_e><NSMasc_es_$e> \
<NSMasc-s/$sse> \
<NGeo-$er-NMasc_s_0><NGeo-$er-Adj0-Up><NGeo-$isch-Adj+><NGeo-0-Name-Fem_0>\
<NGeo-0-Name-Masc_s><NGeo-0-Name-Neut_s><NGeo-a-Name-Fem_s> \
<NGeo-a-Name-Neut_s><NGeo-aner-NMasc_s_0><NGeo-aner-Adj0-Up> \
<NGeo-anisch-Adj+><NGeo-e-NMasc_n_n><NGeo-e-Name-Fem_0><NGeo-e-Name-Neut_s> \
<NGeo-ei-Name-Fem_0><NGeo-en-Name-Neut_s> \
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
% complexity features
<simplex><komposit><suffderiv><prefderiv> \
% complex lexicon entries
[<Simplex><Komplex><Komplex_abstrakt><Komplex_semi><Nominalisierung><Kurzwort>]:<> \
% inflection classes
<Abk_ADJ><Abk_ADV><Abk_ART><Abk_DPRO><Abk_KONJ><Abk_NE-Low><Abk_NE> \
<Abk_NN-Low><Abk_NN><Abk_PREP><Abk_VPPAST><Abk_VPPRES><Adj$><Adj$e> \
<Adj+(e)><Adj+><Adj&><Adj+Lang><Adj+e><Adj-el/er><Adj0><Adj0-Up><AdjComp> \
<AdjSup><AdjNN><AdjNNSuff><AdjPos><AdjPosAttr><AdjPosPred><AdjPosSup><AdjSup>\
<Adj~+e><Adv><Circp><FamName_0><FamName_s><Name-Pl_0><Name-Pl_x> \
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
<Postp-Akk><Postp-Dat><Postp-Gen><Pref/Adj> \
<Pref/Adv><Pref/N><Pref/ProAdv><Pref/Sep><Pref/V><Prep-Akk><Prep-Dat> \
<Prep-Gen><Prep/Art-m><Prep/Art-n><Prep/Art-r><Prep/Art-s><ProAdv><PInd-Invar><Ptkl-Adj> \
<Ptkl-Ant><Ptkl-Neg><Ptkl-Zu><VAImpPl><VAImpSg><VAPastKonj2><VAPres1/3PlInd> \
<VAPres1SgInd><VAPres2PlInd><VAPres2SgInd><VAPres3SgInd><VAPresKonjPl> \
<VAPresKonjSg><VInf+PPres><VInf><VMPast><VMPastKonj><VMPresPl><VMPresSg> \
<VPPast><VPPres><VPastIndReg><VPastIndStr><VPastKonjStr><VPresKonj> \
<VPresPlInd><VVPP-en><VVPP-t><VVPastIndReg><VVPastIndStr><VVPastKonjReg> \
<VVPastKonjStr><VVPastStr><VVPres1+Imp><VVPres1><VVPres2+Imp0><VVPres2+Imp> \
<VVPres2><VVPres2t><VVPres><VVPresPl><VVPresSg><VVReg><WAdv> \
% disjunctive features
<CARD,DIGCARD,NE>:[<CARD><DIGCARD><NE>] \
<ADJ,CARD>:[<ADJ><CARD>] \
<ADJ,NN>:[<ADJ><NN>] \
<CARD,NN>:[<CARD><NN>] \
<CARD,NE>:[<CARD><NE>] \
<ABK,ADJ,NE,NN>:[<NN><NE><ADJ><ABK>] \
<ADJ,NE,NN>:[<NN><NE><ADJ>] \
<ABK,NE,NN>:[<NN><NE><ABK>] \
<NE,NN>:[<NN><NE>] \
<ABK,CARD,NN>:[<NN><ABK><CARD>] \
<ABK,NN>:[<NN><ABK>] \
<ADJ,CARD,NN,V>:[<V><NN><ADJ><CARD>] \
<ADJ,NN,V>:[<V><NN><ADJ>] \
<ABK,ADJ,NE,NN,V>:[<V><NN><NE><ADJ><ABK>] \
<ADJ,NE,NN,V>:[<V><NN><NE><ADJ>] \
<ADV,NE,NN,V>:[<V><NN><NE><ADV>] \
<ABK,NE,NN,V>:[<V><NN><NE><ABK>] \
<NE,NN,V>:[<V><NN><NE>] \
<ABK,NN,V>:[<V><NN><ABK>] \
<NN,V>:[<V><NN>] \
<frei,fremd,gebunden>:[<frei><fremd><gebunden>] \
<frei,fremd,gebunden,kurz>:[<frei><fremd><gebunden><kurz>] \
<frei,fremd,gebunden,lang>:[<frei><fremd><gebunden><lang>] \
<fremd,gebunden,lang>:[<fremd><gebunden><lang>] \
<frei,fremd,kurz>:[<frei><fremd><kurz>] \
<frei,fremd,lang>:[<frei><fremd><lang>] \
<frei,gebunden>:[<frei><gebunden>] \
<frei,gebunden,kurz,lang>:[<frei><gebunden><kurz><lang>] \
<frei,gebunden,lang>:[<frei><gebunden><lang>] \
<frei,lang>:[<lang><frei>] \
<fremd,klassisch,nativ>:[<fremd><klassisch><nativ>] \
<fremd,klassisch>:[<fremd><klassisch>] \
<klassisch,nativ>:[<klassisch><nativ>] \
<frei,nativ>:[<nativ><frei>] \
<frei,fremd,nativ>:[<frei><fremd><nativ>] \
<fremd,nativ>:[<fremd><nativ>] \
<komposit,prefderiv,simplex,suffderiv>:[<komposit><prefderiv><simplex><suffderiv>] \
<komposit,prefderiv,simplex>:[<komposit><prefderivsimplex><>] \
<komposit,simplex,suffderiv>:[<komposit><simplex><suffderiv>] \
<komposit,simplex>:[<komposit><simplex>] \
<prefderiv,suffderiv>:[<simplex><suffderiv>] \
<prefderiv,simplex,suffderiv>:[<prefderiv><simplex><suffderiv>] \
<prefderiv,simplex>:[<prefderiv><simplex>] \
<simplex,suffderiv>:[<simplex><suffderiv>] \
% Herkunft features
<NGeo-0-$er-$er>:	[<NGeo-0-Name-Neut_s><NGeo-$er-NMasc_s_0><NGeo-$er-Adj0-Up>] \
<NGeo-0-$er-$isch>:	[<NGeo-0-Name-Neut_s><NGeo-$er-NMasc_s_0><NGeo-$isch-Adj+>] \
<NGeo-0-aner-aner>:	[<NGeo-0-Name-Neut_s><NGeo-aner-NMasc_s_0><NGeo-aner-Adj0-Up>] \
<NGeo-0-aner-anisch>:	[<NGeo-0-Name-Neut_s><NGeo-aner-NMasc_s_0><NGeo-anisch-Adj+>] \
<NGeo-0-e-isch>:	[<NGeo-0-Name-Neut_s><NGeo-e-NMasc_n_n><NGeo-isch-Adj+>] \
<NGeo-0-er-er>:		[<NGeo-0-Name-Neut_s><NGeo-er-NMasc_s_0><NGeo-er-Adj0-Up>] \
<NGeo-0-0-0>:		[<NGeo-0-Name-Neut_s><NGeo-0-NMasc_s_0><NGeo-0-Adj0-Up>] \
<NGeo-0-er-erisch>:	[<NGeo-0-Name-Neut_s><NGeo-er-NMasc_s_0><NGeo-erisch-Adj+>] \
<NGeo-0-er-isch>:	[<NGeo-0-Name-Neut_s><NGeo-er-NMasc_s_0><NGeo-isch-Adj+>] \
<NGeo-0-ese-esisch>:	[<NGeo-0-Name-Neut_s><NGeo-ese-NMasc_n_n><NGeo-esisch-Adj+>] \
<NGeo-0-ianer-ianisch>:	[<NGeo-0-Name-Neut_s><NGeo-ianer-NMasc_s_0><NGeo-ianisch-Adj+>] \
<NGeo-0-ner-isch>:	[<NGeo-0-Name-Neut_s><NGeo-ner-NMasc_s_0><NGeo-isch-Adj+>] \
<NGeo-0-ner-nisch>:	[<NGeo-0-Name-Neut_s><NGeo-ner-NMasc_s_0><NGeo-nisch-Adj+>] \
<NGeo-0fem-er-erisch>:	[<NGeo-0-Name-Fem_0> <NGeo-er-NMasc_s_0><NGeo-erisch-Adj+>] \
<NGeo-0masc-er-isch>:	[<NGeo-0-Name-Masc_s><NGeo-er-NMasc_s_0><NGeo-isch-Adj+>] \
<NGeo-0masc-ese-esisch>:[<NGeo-0-Name-Masc_s><NGeo-ese-NMasc_n_n><NGeo-esisch-Adj+>] \
<NGeo-a-er-isch>:	[<NGeo-a-Name-Neut_s><NGeo-er-NMasc_s_0><NGeo-isch-Adj+>] \
<NGeo-a-ese-esisch>:	[<NGeo-a-Name-Neut_s><NGeo-ese-NMasc_n_n><NGeo-esisch-Adj+>] \
<NGeo-afem-er-isch>:	[<NGeo-a-Name-Fem_s> <NGeo-er-NMasc_s_0><NGeo-isch-Adj+>] \
<NGeo-e-er-er>:		[<NGeo-e-Name-Neut_s><NGeo-er-NMasc_s_0><NGeo-er-Adj0-Up>] \
<NGeo-e-er-isch>:	[<NGeo-e-Name-Neut_s><NGeo-er-NMasc_s_0><NGeo-isch-Adj+>] \
<NGeo-efem-er-isch>:	[<NGeo-e-Name-Fem_0> <NGeo-er-NMasc_s_0><NGeo-isch-Adj+>] \
<NGeo-ei-e-isch>:	[<NGeo-ei-Name-Fem_0><NGeo-e-NMasc_n_n><NGeo-isch-Adj+>] \
<NGeo-en-aner-anisch>:	[<NGeo-en-Name-Neut_s><NGeo-aner-NMasc_s_0><NGeo-anisch-Adj+>] \
<NGeo-en-e-$isch>:	[<NGeo-en-Name-Neut_s><NGeo-e-NMasc_n_n><NGeo-$isch-Adj+>] \
<NGeo-en-e-isch>:	[<NGeo-en-Name-Neut_s><NGeo-e-NMasc_n_n><NGeo-isch-Adj+>] \
<NGeo-en-er-er>:	[<NGeo-en-Name-Neut_s><NGeo-er-NMasc_s_0><NGeo-er-Adj0-Up>] \
<NGeo-en-er-isch>:	[<NGeo-en-Name-Neut_s><NGeo-er-NMasc_s_0><NGeo-isch-Adj+>] \
<NGeo-ien-e-isch>:	[<NGeo-ien-Name-Neut_s><NGeo-e-NMasc_n_n><NGeo-isch-Adj+>] \
<NGeo-ien-er-isch>:	[<NGeo-ien-Name-Neut_s><NGeo-er-NMasc_s_0><NGeo-isch-Adj+>] \
<NGeo-ien-ese-esisch>:	[<NGeo-ien-Name-Neut_s><NGeo-ese-NMasc_n_n><NGeo-esisch-Adj+>] \
<NGeo-ien-ianer-ianisch>:[<NGeo-ien-Name-Neut_s><NGeo-ianer-NMasc_s_0><NGeo-ianisch-Adj+>] \
<NGeo-ien-ier-isch>:	[<NGeo-ien-Name-Neut_s><NGeo-ier-NMasc_s_0><NGeo-isch-Adj+>] \
<NGeo-istan-e-isch>:	[<NGeo-istan-Name-Neut_s><NGeo-e-NMasc_n_n><NGeo-isch-Adj+>] \
<NGeo-land-$er-$er>:	[<NGeo-land-Name-Neut_s><NGeo-$er-NMasc_s_0><NGeo-$er-Adj0-Up>] \
<NGeo-land-e-isch>:	[<NGeo-land-Name-Neut_s><NGeo-e-NMasc_n_n><NGeo-isch-Adj+>] \
<NGeo-land-e-nisch>:	[<NGeo-land-Name-Neut_s><NGeo-e-NMasc_n_n><NGeo-nisch-Adj+>]


$MAP2$ = .* (e:<e> [lr] <V><base><nativ>[<Simplex><Komplex><Komplex_abstrakt><Komplex_semi><Nominalisierung><Kurzwort>]:<>?<VVReg-el/er>)?
