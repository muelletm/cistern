%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% inflectional endings --
% converted to S-FST from flexion.lex
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

$Fix#$ = <>:<Fix#>
$Adj#$ = <>:<Low#>
$Adj#Up$ = <>:<Up#>
$N#$ = <>:<Up#>
$N#Low/Up$ = <>:<Low#>
$V#$ = <>:<Low#>
$Closed#$ = <>:<Low#>
$Closed#Up$ = <>:<Up#>


$Abk_ADJ$ =	{<^ABK><+ADJ>}:{<>}		$Adj#$

$Abk_ADV$ =	{<^ABK><+ADV>}:{<>}		$Closed#$

$Abk_ART$ =	{<^ABK><+ART>}:{<>}		$Closed#$

$Abk_DPRO$ =	{<^ABK><+DEMPRO>}:{<>}		$Closed#$

$Abk_KONJ$ =	{<^ABK><+KONJ>}:{<>}		$Closed#$

$Abk_NE$ =	{<^ABK><+NE>}:{<>}		$N#$

$Abk_NE-Low$ =	{<^ABK><+NE>}:{<>}		$N#Low/Up$

$Abk_NN$ =	{<^ABK><+NN>}:{<>}		$N#$

$Abk_NN-Low$ =	{<^ABK><+NN>}:{<>}		$N#Low/Up$

$Abk_PREP$ =	{<^ABK><+PREP>}:{<>}		$Closed#$

$Abk_VPPAST$ =	{<^ABK><^VPAST><+ADJ>}:{<>}	$Adj#$

$Abk_VPPRES$ =	{<^ABK><^VPRES><+ADJ>}:{<>}	$Adj#$

$Adj0$ =	{<+ADJ><Invar>}:{<>}		$Adj#$

$Adj0-Up$ =	{<+ADJ><Invar>}:{<>}		$Adj#Up$

$AdjFlexSuff$ = {<Masc><Nom><Sg><St/Mix>}:{er}	$Adj#$ |\
		{<Masc><Nom><Sg><Sw>}:{e}	$Adj#$ |\
		{<Masc><Gen><Sg>}:{en}		$Adj#$ |\
		{<Masc><Dat><Sg><St>}:{em}	$Adj#$ |\
		{<Masc><Dat><Sg><Sw/Mix>}:{en}	$Adj#$ |\
		{<Masc><Akk><Sg>}:{en}		$Adj#$ |\
		{<Fem><Nom><Sg>}:{e}		$Adj#$ |\
		{<Fem><Gen><Sg><St>}:{er}	$Adj#$ |\
		{<Fem><Gen><Sg><Sw/Mix>}:{en}	$Adj#$ |\
		{<Fem><Dat><Sg><St>}:{er}	$Adj#$ |\
		{<Fem><Dat><Sg><Sw/Mix>}:{en}	$Adj#$ |\
		{<Fem><Akk><Sg>}:{e}		$Adj#$ |\
		{<Neut><Nom><Sg><St/Mix>}:{es}	$Adj#$ |\
		{<Neut><Nom><Sg><Sw>}:{e}	$Adj#$ |\
		{<Neut><Gen><Sg>}:{en}		$Adj#$ |\
		{<Neut><Dat><Sg><St>}:{em}	$Adj#$ |\
		{<Neut><Dat><Sg><Sw/Mix>}:{en}	$Adj#$ |\
		{<Neut><Akk><Sg><St/Mix>}:{es}	$Adj#$ |\
		{<Neut><Akk><Sg><Sw>}:{e}	$Adj#$ |\
		{<NoGend><Nom><Pl><St>}:{e}	$Adj#$ |\
		{<NoGend><Nom><Pl><Sw/Mix>}:{en}$Adj#$ |\
		{<NoGend><Gen><Pl><Sw/Mix>}:{en}$Adj#$ |\
		{<NoGend><Gen><Pl><St>}:{er}	$Adj#$ |\
		{<NoGend><Dat><Pl>}:{en}	$Adj#$ |\
		{<NoGend><Akk><Pl><Sw/Mix>}:{en}$Adj#$ |\
		{<NoGend><Akk><Pl><St>}:{e}	$Adj#$

$AdjNNSuff$ =	{<+NN><Masc><Nom><Sg><St/Mix>}:{er}	$N#$ |\
		{<+NN><Masc><Nom><Sg><Sw>}:{e}		$N#$ |\
		{<+NN><Masc><Gen><Sg>}:{en}		$N#$ |\
		{<+NN><Masc><Dat><Sg><St>}:{em}		$N#$ |\
		{<+NN><Masc><Dat><Sg><Sw/Mix>}:{en}	$N#$ |\
		{<+NN><Masc><Akk><Sg>}:{en}		$N#$ |\
		{<+NN><Fem><Nom><Sg>}:{e}		$N#$ |\
		{<+NN><Fem><Gen><Sg><St>}:{er}		$N#$ |\
		{<+NN><Fem><Gen><Sg><Sw/Mix>}:{en}	$N#$ |\
		{<+NN><Fem><Dat><Sg><St>}:{er}		$N#$ |\
		{<+NN><Fem><Dat><Sg><Sw/Mix>}:{en}	$N#$ |\
		{<+NN><Fem><Akk><Sg>}:{e}		$N#$ |\
		{<+NN><Neut><Nom><Sg><St/Mix>}:{es}	$N#$ |\
		{<+NN><Neut><Nom><Sg><Sw>}:{e}		$N#$ |\
		{<+NN><Neut><Gen><Sg>}:{en}		$N#$ |\
		{<+NN><Neut><Dat><Sg><St>}:{em}		$N#$ |\
		{<+NN><Neut><Dat><Sg><Sw/Mix>}:{en}	$N#$ |\
		{<+NN><Neut><Akk><Sg><St/Mix>}:{es}	$N#$ |\
		{<+NN><Neut><Akk><Sg><Sw>}:{e}		$N#$ |\
		{<+NN><NoGend><Nom><Pl><St>}:{e}	$N#$ |\
		{<+NN><NoGend><Nom><Pl><Sw/Mix>}:{en}	$N#$ |\
		{<+NN><NoGend><Gen><Pl><Sw/Mix>}:{en}	$N#$ |\
		{<+NN><NoGend><Gen><Pl><St>}:{er}	$N#$ |\
		{<+NN><NoGend><Dat><Pl>}:{en}		$N#$ |\
		{<+NN><NoGend><Akk><Pl><Sw/Mix>}:{en}	$N#$ |\
		{<+NN><NoGend><Akk><Pl><St>}:{e}	$N#$

$AdjPos$ = 	{<+ADJ><Pos><Pred>}:{<>}	$Adj#$ |\
		{<+ADJ><Pos><Adv>}:{<>}		$Adj#$ |\
		{<+ADJ><Pos>}:{<>}		$AdjFlexSuff$ \
%		|{<^ADJ><Pos>}:{<>}		$AdjNNSuff$   % nominalization

$AdjPosAttr$ =	{<+ADJ><Pos>}:{<FB>}		$AdjFlexSuff$ \
%		|{<^ADJ><Pos>}:{<>}		$AdjNNSuff$  % nominalization

$AdjPosPred$ =	{<+ADJ><Pos><Pred>}:{<>}	$Adj#$

$AdjSup$ =	{<+ADJ><Sup><Pred>}:{sten}	$Adj#$ |\
		{<+ADJ><Sup><Pred>}:{st}	$Adj#$ |\
		{<+ADJ><Sup><Adv>}:{sten}	$Adj#$ |\
		{<+ADJ><Sup>}:{st}		$AdjFlexSuff$ \
%		|{<^ADJ><Sup>}:{st}		$AdjNNSuff$

$Adv$ =		{<+ADV>}:{<>}		$Closed#$

$Circp$ =	{<+CIRCP>}:{<>}		$Fix#$

$Intj$ =	{<+INTJ>}:{<>}		$Closed#$

$IntjUp$ =	{<+INTJ>}:{<>}		$Closed#Up$

$IpKL$ =	{<+IP><links>}:{<>}	$Fix#$

$IpKR$ =	{<+IP><rechts>}:{<>}	$Fix#$

$IpKo$ =	{<+IP><Komma>}:{<>}	$Fix#$

$IpNorm$ =	{<+IP><Norm>}:{<>}	$Fix#$

$Konj-Inf$ =	{<+KONJ><Inf>}:{<>}	$Closed#$

$Konj-Kon$ =	{<+KONJ><Kon>}:{<>}	$Closed#$

$Konj-Sub$ =	{<+KONJ><Sub>}:{<>}	$Closed#$

$Konj-Vgl$ =	{<+KONJ><Vgl>}:{<>}	$Closed#$

$NMasc-Adj$ =	{r<+NN><Masc><Nom><Sg><St/Mix>}:{r}	$N#$ |\
		{r<+NN><Masc><Nom><Sg><Sw>}:{<>}	$N#$ |\
		{r<+NN><Masc><Gen><Sg>}:{n}		$N#$ |\
		{r<+NN><Masc><Dat><Sg><St>}:{m}		$N#$ |\
		{r<+NN><Masc><Dat><Sg><Sw/Mix>}:{n}	$N#$ |\
		{r<+NN><Masc><Akk><Sg>}:{n}		$N#$ |\
		{r<+NN><Masc><Nom><Pl><St>}:{<>}	$N#$ |\
		{r<+NN><Masc><Nom><Pl><Sw/Mix>}:{n}	$N#$ |\
		{r<+NN><Masc><Gen><Pl><Sw/Mix>}:{n}	$N#$ |\
		{r<+NN><Masc><Gen><Pl><St>}:{r}		$N#$ |\
		{r<+NN><Masc><Dat><Pl>}:{n}		$N#$ |\
		{r<+NN><Masc><Akk><Pl><Sw/Mix>}:{n}	$N#$ |\
		{r<+NN><Masc><Akk><Pl><St>}:{<>}	$N#$

%  Herz-ens
$NNeut-Herz$ =	{<+NN><Neut><Nom><Sg>}:{<FB>}		$N#$ |\
		{<+NN><Neut><Gen><Sg>}:{<FB>ens}	$N#$ |\
		{<+NN><Neut><Dat><Sg>}:{<FB>en}		$N#$ |\
		{<+NN><Neut><Akk><Sg>}:{<FB>}		$N#$

$NPl_0$ =	{<Nom><Pl>}:{<>}	$N#$ |\
		{<Gen><Pl>}:{<>}	$N#$ |\
		{<Dat><Pl>}:{n}		$N#$ |\
		{<Akk><Pl>}:{<>}	$N#$

$NPl_x$ =	{<Nom><Pl>}:{<>}	$N#$ |\
		{<Gen><Pl>}:{<>}	$N#$ |\
		{<Dat><Pl>}:{<>}	$N#$ |\
		{<Akk><Pl>}:{<>}	$N#$

%  Frau; Mythos; Chaos
$NSg_0$ =	{<Nom><Sg>}:{<FB>}	$N#$ |\
		{<Gen><Sg>}:{<FB>}	$N#$ |\
		{<Dat><Sg>}:{<FB>}	$N#$ |\
		{<Akk><Sg>}:{<FB>}	$N#$

%  Mensch-en
$NSg_en$ =	{<Nom><Sg>}:{<FB>}	$N#$ |\
		{<Gen><Sg>}:{<FB>en}	$N#$ |\
		{<Dat><Sg>}:{<FB>en}	$N#$ |\
		{<Akk><Sg>}:{<FB>en}	$N#$

%  Haus-es, Geist-(e)s
$NSg_es$ =	{<Nom><Sg>}:{<FB>}		$N#$ |\
		{<Gen><Sg>}:{<FB>es<^Gen>}	$N#$ |\
		{<Dat><Sg>}:{<FB>}		$N#$ |\
		{<Dat><Sg>}:{<FB>e}		$N#$ |\
		{<Akk><Sg>}:{<FB>}		$N#$

%  Nachbar-n
$NSg_n$ =	{<Nom><Sg>}:{<FB>}	$N#$ |\
		{<Gen><Sg>}:{<FB>n}	$N#$ |\
		{<Dat><Sg>}:{<FB>n}	$N#$ |\
		{<Akk><Sg>}:{<FB>n}	$N#$

%  Opa-s, Klima-s
$NSg_s$ =	{<Nom><Sg>}:{<FB>}	$N#$ |\
		{<Gen><Sg>}:{<FB>s}	$N#$ |\
		{<Dat><Sg>}:{<FB>}	$N#$ |\
		{<Akk><Sg>}:{<FB>}	$N#$

$N_0_\$$ = 	 			$NSg_0$ |\
		{<>}:{<UL>}		$NPl_0$

$N_0_\$e$ =				$NSg_0$ |\
		{<>}:{<UL>e}		$NPl_0$

$N_0_e$ =				$NSg_0$ |\
		{<>}:{<FB>e}		$NPl_0$

$N_0_en$ =				$NSg_0$ |\
		{<>}:{<FB>en}		$NPl_x$

$N_0_n$ =				$NSg_0$ |\
		{<>}:{<FB>n}		$NPl_x$

$N_0_s$ =				$NSg_0$ |\
		{<>}:{<FB>s}		$NPl_x$

$N_0_x$ =				$NSg_0$ |\
					$NPl_x$

$N_en_en$ =				$NSg_en$ |\
		{<>}:{<FB>en}		$NPl_x$

$N_es_\$e$ =				$NSg_es$ |\
		{<>}:{<UL>e}		$NPl_0$

$N_es_\$er$ =				$NSg_es$ |\
		{<>}:{<UL>er}		$NPl_0$

$N_es_e$ =				$NSg_es$ |\
		{<>}:{<FB>e}		$NPl_0$

$N_es_en$ =				$NSg_es$ |\
		{<>}:{<FB>en}		$NPl_x$

$N_n_n$ =				$NSg_n$ |\
		{<>}:{<FB>n}		$NPl_x$

$N_s_0$ =				$NSg_s$ |\
					$NPl_0$

$N_s_\$$ =				$NSg_s$ |\
		{<>}:{<UL>}		$NPl_0$

$N_s_\$x$ =				$NSg_s$ |\
		{<>}:{<UL>}		$NPl_x$

$N_s_e$ =				$NSg_s$ |\
		{<>}:{<FB>e}		$NPl_0$

$N_s_en$ =				$NSg_s$ |\
		{<>}:{<FB>en}		$NPl_x$

$N_s_n$ =				$NSg_s$ |\
		{<>}:{<FB>n}		$NPl_x$

$N_s_s$ =				$NSg_s$ |\
		{<>}:{<FB>s}		$NPl_x$

$N_s_x$ =				$NSg_s$ |\
					$NPl_x$

$PInd-Invar$ =	{<+INDEF><Invar>}:{<>}	$Closed#$

$Postp-Akk$ =	{<+POSTP><Akk>}:{<>}	$Closed#$

$Postp-Dat$ =	{<+POSTP><Dat>}:{<>}	$Closed#$

$Postp-Gen$ =	{<+POSTP><Gen>}:{<>}	$Closed#$

$Prep-Akk$ =	{<+PREP><Akk>}:{<>}	$Closed#$

$Prep-Dat$ =	{<+PREP><Dat>}:{<>}	$Closed#$

$Prep-Gen$ =	{<+PREP><Gen>}:{<>}	$Closed#$

$Prep/Art-m$ = 	{<+PREP/ART><Masc><Dat><Sg>}:{<>}	$Closed#$ |\
		{<+PREP/ART><Neut><Dat><Sg>}:{<>}	$Closed#$

% untern (Tisch)
$Prep/Art-n$ =	{<+PREP/ART><Masc><Akk><Sg>}:{<>}	$Closed#$

$Prep/Art-r$ =	{<+PREP/ART><Fem><Dat><Sg>}:{<>}	$Closed#$

$Prep/Art-s$ =	{<+PREP/ART><Neut><Akk><Sg>}:{<>}	$Closed#$

$ProAdv$ =	{<+PROADV>}:{<>}	$Closed#$

$Ptkl-Adj$ =	{<+PTKL><Adj>}:{<>}	$Closed#$

$Ptkl-Ant$ =	{<+PTKL><Ant>}:{<>}	$Closed#$

$Ptkl-Neg$ =	{<+PTKL><Neg>}:{<>}	$Closed#$

$Ptkl-Zu$ =	{<+PTKL><zu>}:{<>}	$Closed#$

$SpecChar$ =	{<+CHAR>}:{<>}		$Fix#$

$Symbol$ =	{<+SYMBOL>}:{<>}	$Fix#$

$V+(es)$ =	{/'s}:{'s}?		$V#$

% seid; habt; werdet; tut
$VAImpPl$ =	{<+V><Imp><Pl>}:{<^imp>}		$V+(es)$

% sei; hab/habe; werde; tu
$VAImpSg$ =	{<+V><Imp><Sg>}:{<^imp>}		$V+(es)$

$VAPastKonj2$ = {<+V><2><Sg><Past><Konj>}:{<FB>st}	$V+(es)$ |\ % wär-st
		{<+V><2><Pl><Past><Konj>}:{<FB>t}	$V+(es)$    % wär-t

%  sind; haben; werden; tun
$VAPres1/3PlInd$ = {<+V><1><Pl><Pres><Ind>}:{<>}	$V+(es)$ |\
		{<+V><3><Pl><Pres><Ind>}:{<>}		$V+(es)$

%  bin; habe; werde; tue
$VAPres1SgInd$ = {<+V><1><Sg><Pres><Ind>}:{<>}		$V+(es)$

%  seid; habt; werdet; tut
$VAPres2PlInd$ = {<+V><2><Pl><Pres><Ind>}:{<>}		$V+(es)$

%  bist; hast; wirst; tust
$VAPres2SgInd$ = {<+V><2><Sg><Pres><Ind>}:{<>}		$V+(es)$

%  ist; hat; wird; tut
$VAPres3SgInd$ = {<+V><3><Sg><Pres><Ind>}:{<>}		$V+(es)$

$VAPresKonjPl$ = {<+V><1><Pl><Pres><Konj>}:{<FB>n}	$V+(es)$ |\ % seie-n; habe-n; werde-n; tu-n
		{<+V><2><Pl><Pres><Konj>}:{<FB>t}	$V+(es)$ |\ % seie-t; habe-t; werde-et; tu-t
		{<+V><3><Pl><Pres><Konj>}:{<FB>n}	$V+(es)$

$VAPresKonjSg$ = {<+V><1><Sg><Pres><Konj>}:{<FB>}	$V+(es)$ |\ % sei-; habe-; werde-; tue-
		{<+V><2><Sg><Pres><Konj>}:{<FB>st}	$V+(es)$ |\ % sei-st; habe-st; werde-st; tue-st
		{<+V><3><Sg><Pres><Konj>}:{<FB>}	$V+(es)$ % sei-; habe-; werde-; tue-

$VImpPl$ =	{<+V><Imp><Pl>}:{<DEL-S>t<^imp>}	$V+(es)$ % kommt! schaut! arbeit-e-t

$VImpSg$ =	{<+V><Imp><Sg>}:{<DEL-S><^imp>}		$V+(es)$	% komm! schau! arbeit-e

$VImpSg0$ =	{<+V><Imp><Sg>}:{<^imp>}		$V+(es)$	% flicht! (not: flicht-e!)

$VPastIndReg$ = {<+V><1><Sg><Past><Ind>}:{<DEL-S>te}	$V+(es)$ |\	% (ich) liebte, wollte, arbeit-e-te
		{<+V><2><Sg><Past><Ind>}:{<DEL-S>test}	$V+(es)$ |\	% 	 brachte
		{<+V><3><Sg><Past><Ind>}:{<DEL-S>te}	$V+(es)$ |\
		{<+V><1><Pl><Past><Ind>}:{<DEL-S>ten}	$V+(es)$ |\
		{<+V><2><Pl><Past><Ind>}:{<DEL-S>tet}	$V+(es)$ |\
		{<+V><3><Pl><Past><Ind>}:{<DEL-S>ten}	$V+(es)$

$VPastIndStr$ = {<+V><1><Sg><Past><Ind>}:{<FB>}		$V+(es)$ |\	% (ich) fuhr, ritt, fand
		{<+V><2><Sg><Past><Ind>}:{<DEL-S>st}	$V+(es)$ |\	% (du) fuhrst, ritt-e-st, fand-e-st
		{<+V><3><Sg><Past><Ind>}:{<FB>}		$V+(es)$ |\
		{<+V><1><Pl><Past><Ind>}:{<FB>en}	$V+(es)$ |\
		{<+V><2><Pl><Past><Ind>}:{<DEL-S>t}	$V+(es)$ |\
		{<+V><3><Pl><Past><Ind>}:{<FB>en}	$V+(es)$

$VPastKonjReg$ = {<+V><1><Sg><Past><Konj>}:{<DEL-S>te}	$V+(es)$ |\	% (ich) liebte, wollte, arbeit-e-te
		{<+V><2><Sg><Past><Konj>}:{<DEL-S>test}	$V+(es)$ |\	%       brächte
		{<+V><3><Sg><Past><Konj>}:{<DEL-S>te}	$V+(es)$ |\
		{<+V><1><Pl><Past><Konj>}:{<DEL-S>ten}	$V+(es)$ |\
		{<+V><2><Pl><Past><Konj>}:{<DEL-S>tet}	$V+(es)$ |\
		{<+V><3><Pl><Past><Konj>}:{<DEL-S>ten}	$V+(es)$

$VPastKonjStr$ = {<+V><1><Sg><Past><Konj>}:{<FB>e}	$V+(es)$ |\	% (ich) führe, ritte, fände
		{<+V><2><Sg><Past><Konj>}:{<FB>est}	$V+(es)$ |\
		{<+V><3><Sg><Past><Konj>}:{<FB>e}	$V+(es)$ |\
		{<+V><1><Pl><Past><Konj>}:{<FB>en}	$V+(es)$ |\
		{<+V><2><Pl><Past><Konj>}:{<FB>et}	$V+(es)$ |\
		{<+V><3><Pl><Past><Konj>}:{<FB>en}	$V+(es)$

$VPres1Irreg$ = {<+V><1><Sg><Pres><Ind>}:{<FB>}		$V+(es)$	% (ich) will, bedarf

$VPres1Reg$ =	{<+V><1><Sg><Pres><Ind>}:{<FB>e}	$V+(es)$	% (ich) liebe, rate, sammle

$VPres2Irreg$ =	{<+V><2><Sg><Pres><Ind>}:{<FB>st}	$V+(es)$	% (du) hilfst, rätst

$VPres2Reg$ =	{<+V><2><Sg><Pres><Ind>}:{<DEL-S>st}	$V+(es)$	% (du) liebst, biet-e-st, sammelst

$VPres3Irreg$ =	{<+V><3><Sg><Pres><Ind>}:{<FB>}		$V+(es)$	% (er) rät, will

$VPres3Reg$ =	{<+V><3><Sg><Pres><Ind>}:{<DEL-S>t}	$V+(es)$	% (er) liebt, hilft, sammelt

$VPresKonj$ =	{<+V><1><Sg><Pres><Konj>}:{<FB>e}	$V+(es)$ |\	% (ich) liebe, wolle, sammle
		{<+V><2><Sg><Pres><Konj>}:{<FB>est}	$V+(es)$ |\	% (du) liebest, wollest, sammelst
		{<+V><3><Sg><Pres><Konj>}:{<FB>e}	$V+(es)$ |\	% (er) liebe, wolle, sammle
		{<+V><1><Pl><Pres><Konj>}:{<FB>en}	$V+(es)$ |\	% (wir) lieben, wollen, sammeln
		{<+V><2><Pl><Pres><Konj>}:{<FB>et}	$V+(es)$ |\	% (ihr) liebet, wollet, sammelt
		{<+V><3><Pl><Pres><Konj>}:{<FB>en}	$V+(es)$	% (sie) lieben, wollen, sammeln

$VPresPlInd$ =	{<+V><1><Pl><Pres><Ind>}:{<FB>en}	$V+(es)$ |\	% (wir) lieben, wollen, sammeln
		{<+V><2><Pl><Pres><Ind>}:{<DEL-S>t}	$V+(es)$ |\	% (ihr) liebt, biet-e-t, sammelt
		{<+V><3><Pl><Pres><Ind>}:{<FB>en}	$V+(es)$	% (sie) lieben, wollen, sammeln

$VVPastIndReg$ =	{en}:{<>}	$VPastIndReg$

$VVPastIndStr$ =	{en}:{<>}	$VPastIndStr$

$VVPastKonjReg$ =	{en}:{<>}	$VPastKonjReg$

$VVPastKonjStr$ =	{en}:{<>}	$VPastKonjStr$

$VVPastStr$ =				$VVPastIndStr$ |\
					$VVPastKonjStr$

$WAdv$ =	{<+WADV>}:{<>}		$Closed#$

$AdjComp$ =	{<+ADJ><Comp><Pred>}:{er}	$Adj#$ |\
		{<+ADJ><Comp><Adv>}:{er}	$Adj#$ |\
		{<+ADJ><Comp>}:{er}		$AdjFlexSuff$ \
%		|{<^ADJ><Comp>}:{er}		$AdjNNSuff$	% nominalization

$AdjNN$ =				$AdjPosPred$

$AdjPosSup$ =	{<>}:{<FB>}		$AdjPosAttr$ |\
		{<>}:{<FB>}		$AdjSup$

$Adj\$$ =	{<>}:{<FB>}		$AdjPos$ |\
		{<>}:{<UL>}		$AdjComp$ |\
		{<>}:{<UL>}		$AdjSup$

$Adj\$e$ =	{<>}:{<FB>}		$AdjPos$ |\
		{<>}:{<UL>}		$AdjComp$ |\
		{<>}:{<UL>e}		$AdjSup$

$Adj~+e$ =	{<>}:{<SS><FB>}		$AdjPos$ |\
		{<>}:{<SS><FB>}		$AdjComp$ |\
		{<>}:{<SS><FB>e}	$AdjSup$

%  family names ending in -s, -z
$FamName_0$ =	{<+NE><NoGend>}:{<>}	$NSg_0$ |\
		{<+NE><NoGend>}:{ens}	$NPl_x$

%  family names
$FamName_s$ =	{<+NE><NoGend>}:{<>}	$NSg_s$ |\
		{<+NE><NoGend>}:{s}	$NPl_x$

%  Leute
$N?/Pl_0$ =	{<+NN><NoGend>}:{<>}	$NPl_0$

%  Kosten
$N?/Pl_x$ =	{<+NN><NoGend>}:{<>}	$NPl_x$

%  -ung, -heit, -keit, -tät, -schaft
$NFem-Deriv$ =	{<+NN><Fem>}:{<>}	$N_0_en$

%  Nuß/Nüsse
$NFem-s/\$sse$ ={<+NN><Fem>}:{<SS>}	$N_0_\$e$

%  Kenntnis/Kenntnisse
$NFem-s/sse$ =	{<+NN><Fem>}:{<SS>}	$N_0_\$e$

%  Hosteß/Hostessen
$NFem-s/ssen$ =	{<+NN><Fem>}:{<SS>}	$N_0_en$

%  Matrizen	
$NFem/Pl$ =	{<+NN><Fem>}:{<>}	$NPl_x$

%  Matrix/--
$NFem/Sg$ =	{<+NN><Fem>}:{<>}	$NSg_0$

%  Mutter/Mütter
$NFem_0_\$$ =	{<+NN><Fem>}:{<>}	$N_0_\$$

%  Wand/Wände
$NFem_0_\$e$ =	{<+NN><Fem>}:{<>}	$N_0_\$e$

%  Drangsal/Drangsale; Retina/Retinae
$NFem_0_e$ =	{<+NN><Fem>}:{<>}	$N_0_e$

%  Frau/Frauen; Arbeit/Arbeiten	
$NFem_0_en$ =	{<+NN><Fem>}:{<>}	$N_0_en$

%  Hilfe/Hilfen; Tafel/Tafeln; Nummer/Nummern 
$NFem_0_n$ =	{<+NN><Fem>}:{<>}	$N_0_n$

%  Oma/Omas	
$NFem_0_s$ =	{<+NN><Fem>}:{<>}	$N_0_s$

%  Ananas/Ananas	
$NFem_0_x$ =	{<+NN><Fem>}:{<>}	$N_0_x$

%  Name-ns/Namen; Gedanke(n); Buchstabe
$NMasc-ns$ =	{<+NN><Masc><Nom><Sg>}:{<>}	$N#$ |\
		{<+NN><Masc><Gen><Sg>}:{<FB>ns}	$N#$ |\
		{<+NN><Masc><Dat><Sg>}:{<FB>n}	$N#$ |\
		{<+NN><Masc><Akk><Sg>}:{<FB>n}	$N#$ |\
		{<+NN><Masc>}:{n}		$NPl_x$

%  Haß-Hasses/--
$NMasc-s/Sg$ =		{<+NN><Masc>}:{<SS>}	$NSg_es$

%  Baß/Bässe
$NMasc-s/\$sse$ =	{<+NN><Masc>}:{<SS>}	$N_es_\$e$

%  Bus/Busse; Erlaß/Erlasse
$NMasc-s/sse$ =		{<+NN><Masc>}:{<SS>}	$N_es_e$

%  Nimbus-/Nimbusse
$NMasc-s0/sse$ =	{<+NN><Masc>}:{<SS>}	$N_0_e$

%  --/Bauten
$NMasc/Pl$ =		{<+NN><Masc>}:{<>}	$NPl_x$

%  Fiskus/--
$NMasc/Sg_0$ =		{<+NN><Masc>}:{<>}	$NSg_0$

%  Abwasch-(e)s/--; Glanz-es/--;
$NMasc/Sg_es$ =		{<+NN><Masc>}:{<>}	$NSg_es$

%  Hagel-s/--; Adel-s/--
$NMasc/Sg_s$ =		{<+NN><Masc>}:{<>}	$NSg_es$

%  Revers/Revers
$NMasc_0_x$ =		{<+NN><Masc>}:{<>}	$N_0_x$

%  Fels-en/Felsen; Mensch-en/Menschen
$NMasc_en_en$ =		{<+NN><Masc>}:{<>}	$N_en_en$

%  Arzt-(e)s/Ärzte;
$NMasc_es_\$e$ =	{<+NN><Masc>}:{<>}	$N_es_\$e$

%  Gott-(e)s/Götter
$NMasc_es_\$er$ =	{<+NN><Masc>}:{<>}	$N_es_\$er$

%  Tag-(e)s/Tage; 
$NMasc_es_e$ =		{<+NN><Masc>}:{<>}	$N_es_e$

%  Fleck-(e)s/Flecken
$NMasc_es_en$ =		{<+NN><Masc>}:{<>}	$N_es_en$

%  Affe-n/Affen; Bauer-n/Bauern
$NMasc_n_n$ =		{<+NN><Masc>}:{<>}	$N_n_n$

%  Adler-s/Adler; Engel-s/Engel
$NMasc_s_0$ =		{<+NN><Masc>}:{<>}	$N_s_0$

%  Apfel-s/Äpfel; Vater-s/Väter
$NMasc_s_\$$ =		{<+NN><Masc>}:{<>}	$N_s_\$$

%  Garten-s/Gärten
$NMasc_s_\$x$ =		{<+NN><Masc>}:{<>}	$N_s_\$x$

%  Drilling-s/Drillinge
$NMasc_s_e$ =		{<+NN><Masc>}:{<>}	$N_s_e$

%  Zeh-s/Zehen
$NMasc_s_en$ =		{<+NN><Masc>}:{<>}	$N_s_en$

%  Muskel-s/Muskeln; See-s/Seen
$NMasc_s_n$ =		{<+NN><Masc>}:{<>}	$N_s_n$

%  Chef-s/Chefs; Bankier-s/Bankiers
$NMasc_s_s$ =		{<+NN><Masc>}:{<>}	$N_s_s$

%  Wagen-s/Wagen
$NMasc_s_x$ =		{<+NN><Masc>}:{<>}	$N_s_x$

%  Kindchen-s/Kindchen
$NNeut-Dimin$ =		{<+NN><Neut>}:{<>}	$N_s_x$

%  --/Fresken
$NNeut/Pl$ =		{<+NN><Neut>}:{<>}	$NPl_x$

%  Abseits-/--
$NNeut/Sg_0$ =		{<+NN><Neut>}:{<>}	$NSg_0$

%  Ausland-(e)s/--
$NNeut/Sg_es$ =		{<+NN><Neut>}:{<>}	$NSg_es$

%  Deutsch-en/--
$NNeut/Sg_en$ =		{<+NN><Neut>}:{<>}	$NSg_en$

%  Abitur-s/--
$NNeut/Sg_s$ =		{<+NN><Neut>}:{<>}	$NSg_s$

%  Relais-/Relais
$NNeut_0_x$ =		{<+NN><Neut>}:{<>}	$N_0_x$

%  Floß-es/Flöße;
$NNeut_es_\$e$ =	{<+NN><Neut>}:{<>}	$N_es_\$e$

%  Buch-(e)s/Bücher
$NNeut_es_\$er$ =	{<+NN><Neut>}:{<>}	$N_es_\$er$

%  Schild-(e)s/Schilder
$NNeut_es_er$ =		{<+NN><Neut>}:{<>}	$N_es_\$er$

%  Spiel-(e)s/Spiele; Abgas-es/Abgase
$NNeut_es_e$ =		{<+NN><Neut>}:{<>}	$N_es_e$

%  Bett-(e)s/Betten
$NNeut_es_en$ =		{<+NN><Neut>}:{<>}	$N_es_en$

%  Feuer-s/Feuer; Mittel-s/Mittel
$NNeut_s_0$ =		{<+NN><Neut>}:{<>}	$N_s_0$

%  Kloster-s/Klöster
$NNeut_s_\$$ =		{<+NN><Neut>}:{<>}	$N_s_\$$

%  Dreieck-s/Dreiecke
$NNeut_s_e$ =		{<+NN><Neut>}:{<>}	$N_s_e$

%  Juwel-s/Juwelen
$NNeut_s_en$ =		{<+NN><Neut>}:{<>}	$N_s_en$

%  Auge-s/Augen
$NNeut_s_n$ =		{<+NN><Neut>}:{<>}	$N_s_n$

%  Sofa-s/Sofas;
$NNeut_s_s$ =		{<+NN><Neut>}:{<>}	$N_s_s$

%  Almosen-s/Almosen
$NNeut_s_x$ =		{<+NN><Neut>}:{<>}	$N_s_x$
$VFlexPres2$ =					$VPres2Irreg$ |\
						$VPres3Reg$

$VFlexPres2t$ =					$VPres2Irreg$ |\
						$VPres3Irreg$

$VInf$ =		{<+V><Inf>}:{<>}	$V#$ |\
			{<+V><Inf><zu>}:{<^zz>}	$V#$ \
%			|{<^VINF>}:{<>}		$NNeut/Sg_s$

$VMPastKonj$ =		{en}:{<>}	$VPastKonjReg$

$VModFlexSg$ =				$VPres1Irreg$ |\
					$VPres2Reg$ |\
					$VPres3Irreg$

$VVPres2$ =		{en}:{<>}	$VFlexPres2$

$VVPres2+Imp$ =		{en}:{<>}	$VImpSg$ |\
			$VVPres2$

$VVPres2t$ =		{en}:{<>}	$VFlexPres2t$

$VVPresSg$ =		{en}:{<>}	$VModFlexSg$	% bedarf-; weiss-

$Adj&$ =		{<>}:{<FB>}	$AdjPos$ |\
			{<>}:{<FB>}	$AdjComp$ |\
			{<>}:{<DEL-S>}	$AdjSup$

$Adj+$ =		{<>}:{<FB>}	$AdjPos$ |\
			{<>}:{<FB>}	$AdjComp$ |\
			{<>}:{<FB>}	$AdjSup$

$Adj+(e)$ =		{<>}:{<FB>}	$AdjPos$ |\
			{<>}:{<FB>}	$AdjComp$ |\
			{<>}:{<FB>}	$AdjSup$ |\
			{<>}:{<FB>e}	$AdjSup$

%  deutsch; [das] Deutsch
$Adj+Lang$ =				$Adj+$ |\
					$NNeut/Sg_en$

$Adj+e$ =		{<>}:{<FB>}	$AdjPos$ |\
			{<>}:{<FB>}	$AdjComp$ |\
			{<>}:{<FB>e}	$AdjSup$

$Adj-el/er$ =		{<>}:{<^Ax>}	$Adj+$

%  Algebra/Algebren; Firma/Firmen
$NFem-a/en$ =				$NFem/Sg$ |\
			{<>}:{<^pl>en}	$NFem/Pl$

%  Freundin/Freundinnen	
$NFem-in$ =				$NFem/Sg$ |\
			{<>}:{nen}	$NFem/Pl$

%  Basis/Basen
$NFem-is/en$ =				$NFem/Sg$ |\
			{<>}:{<^pl>en}	$NFem/Pl$

%  Neuritis/Neuritiden
$NFem-is/iden$ =			$NFem/Sg$ |\
			{<>}:{<^pl>iden}$NFem/Pl$

%  Virus/Viren
$NMasc-us/en$ =				$NMasc/Sg_0$ |\
	{<+NN><Masc><Gen><Sg>}:{<FB>ses}$N#$ |\
			{<>}:{<^pl>en}	$NMasc/Pl$

%  Intimus/Intimi
$NMasc-us/i$ =				$NMasc/Sg_0$ |\
	{<+NN><Masc><Gen><Sg>}:{<FB>ses}$N#$ |\
			{<>}:{<^pl>i}	$NMasc/Pl$

%  Absolvent/in
$NMasc_en_en=in$ =			$NMasc_en_en$ |\
		{<CB>in}:{<FB>in}	$NFem-in$

%  Schwabe/Schwäbin; Bauer/Bäuerin
$NMasc_n_n=\$in$ =			$NMasc_n_n$ |\
		<CB>:<^Del><>:<UL>in	$NFem-in$

%  Bote/Botin; Nachbar/Nachbarin;
$NMasc_n_n=in$ =			$NMasc_n_n$ |\
		<>:<^Del><CB>:<FB>in	$NFem-in$

%  Lehrer/in
$NMasc_s_0=in$ =			$NMasc_s_0$ |\
			<CB>:<FB> in	$NFem-in$

%  Bibliothekar/in
$NMasc_s_e=in$ =			$NMasc_s_e$ |\
			<CB>:<FB> in	$NFem-in$

%  Professor/in
$NMasc_s_en=in$ =			$NMasc_s_en$ |\
			<CB>:<FB> in	$NFem-in$

%  Adverb/Adverbien
$NNeut-0/ien$ =				$NNeut/Sg_s$ |\
			{<>}:{ien}	$NNeut/Pl$

%  Komma/Kommata
$NNeut-a/ata$ =				$NNeut/Sg_s$ |\
			{<>}:{ta}	$NNeut/Pl$

%  Dogma/Dogmen 
$NNeut-a/en$ =				$NNeut/Sg_s$ |\
			{<>}:{<^pl>en}	$NNeut/Pl$

% Oxymoron/Oxymora
$NNeut-on/a$ =				$NNeut/Sg_s$ |\
			{<>}:{<^pl>a}	$NNeut/Pl$

%  Faß/Fässer
$NNeut-s/\$sser$ =	{<>}:{<SS>}	$NNeut_es_\$er$

%  Zeugnis/Zeugnisse
$NNeut-s/sse$ =		{<>}:{<SS>}	$NNeut_es_e$

%  Aktivum/Aktiva
$NNeut-um/a$ =				$NNeut/Sg_s$ |\
			{<>}:{<^pl>a}	$NNeut/Pl$

%  Museum/Museen
$NNeut-um/en$ =				$NNeut/Sg_s$ |\
			{<>}:{<^pl>en}	$NNeut/Pl$


$Name-Fem_0$ =	{<+NE><Fem>}:{<>}	$NSg_0$

$Name-Fem_s$ =	{<+NE><Fem>}:{<>}	$NSg_s$

$Name-Masc_0$ =	{<+NE><Masc>}:{<>}	$NSg_0$

$Name-Masc_s$ =	{<+NE><Masc>}:{<>}	$NSg_s$

$Name-Neut_0$ =	{<+NE><Neut>}:{<>}	$NSg_0$

$Name-Neut_s$ =	{<+NE><Neut>}:{<>}	$NSg_s$

$Name-Pl_0$ =	{<+NE><NoGend>}:{<>}	$NPl_0$

$Name-Pl_x$ =	{<+NE><NoGend>}:{<>}	$NPl_x$

%  Buenos [Aires]; Tel [Aviv]
$Name-Invar$ =	{<+NE><Invar>}:{<>}	$N#$

% %  Engländer/Engländer-in
% $Name+er/in$ =	{<+ADJ><Invar>}:{<>}	$Adj#Up$ |\
% 					$NMasc_s_0=in$

%  Stuttgart/Stuttgart-er/Stuttgart-er-in
% $Name-Neut+Loc$ =	er		$Name+er/in$ |\
% 					$Name-Neut_s$
$VMPresSg$ =		{en}:{<>}	$VModFlexSg$

$VPPast$ =	{<+V><PPast>}:{<^pp>}	$V#$ \
%		|{<^VPAST>}:{<^pp>}	$Adj&$

$VPPres$ =	{<+V><PPres>}:{<>}	$V#$ |\
	{<+V><PPres><zu>}:{<^zz>}	$V#$ \
%		|{<^VPRES>}:{<>}	$Adj+$ |\
%		{<^VPRES><zu>}:{<^zz>}	$Adj+$

$VVPres2+Imp0$ =	{en}:{<>}	$VImpSg0$ |\
					$VVPres2t$

$VInf+PPres$ =				$VInf$ |\
			{<>}:{d}	$VPPres$

$VInfStem$ =		{<>}:{<FB>en}	$VInf+PPres$

$VModFlexPl$ =				$VPresPlInd$ |\
					$VPresKonj$ |\
					$VInfStem$

$VPP-en$ =		{<>}:{<FB>en}	$VPPast$

$VPP-t$ =		{<>}:{<DEL-S>t}	$VPPast$

$VVPP-en$ =		{en}:{<>}	$VPP-en$

$VVPP-t$ =		{en}:{<>}	$VPP-t$

$VVPresPl$ =		{en}:{<>}	$VModFlexPl$	% beduerf-, wiss-

$VFlexPres1$ =				$VPres1Reg$ |\
					$VPresPlInd$ |\
					$VPresKonj$ |\
					$VImpPl$ |\
					$VInfStem$

$VFlexPresReg$ =			$VFlexPres1$ |\
					$VPres2Reg$ |\
					$VPres3Reg$ |\
					$VImpSg$

$VFlexReg$ =				$VFlexPresReg$ |\
					$VPastIndReg$ |\
					$VPastKonjReg$ |\
					$VPP-t$

$VMPast$ =		{en}:{<>}	$VPastIndReg$ |\
			{en}:{<>}	$VPP-t$

$VMPresPl$ =		{en}:{<>}	$VModFlexPl$

$VVPres$ =		{en}:{<>}	$VFlexPresReg$

$VVPres1$ =		{en}:{<>}	$VFlexPres1$

$VVPres1+Imp$ =		{en}:{<>}	$VImpSg$ |\
					$VVPres1$

$VVReg$ =		{en}:{<>}	$VFlexReg$

$VVReg-el/er$ =		{n}:{<>}	$VFlexReg$



$Pref/Adv$ =	{<+VPRE><Adv>}:{<>}	$Fix#$

$Pref/Adj$ =	{<+VPRE><Adj>}:{<>}	$Fix#$

$Pref/ProAdv$ =	{<+VPRE><ProAdv>}:{<>}	$Fix#$

$Pref/N$ =	{<+VPRE><NN>}:{<>}	$Fix#$

$Pref/V$ =	{<+VPRE><V>}:{<>}	$Fix#$

$Pref/Sep$ =	{<+VPRE>}:{<>}		$Fix#$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%            Numbers                             %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

$Card$ =	<+CARD>:<>		$Closed#$

$DigOrd$ =	<+ORD>:<>		$Closed#$

$Ord$ =		<+ORD>:<>		$AdjFlexSuff$ |\
		{<+ORD><Pred>}:{<>}	$Closed#$

$NumAdjFlex$ =	{<+ADJ><Pos><Pred>}:{<>}			$Fix#$ |\
		{<+ADJ><Pos><Masc><Nom><Sg><St/Mix>}:{er}	$Fix#$ |\
		{<+ADJ><Pos><Masc><Nom><Sg><Sw>}:{e}		$Fix#$ |\
		{<+ADJ><Pos><Masc><Gen><Sg>}:{en}		$Fix#$ |\
		{<+ADJ><Pos><Masc><Dat><Sg><St>}:{em}		$Fix#$ |\
		{<+ADJ><Pos><Masc><Dat><Sg><Sw/Mix>}:{en}	$Fix#$ |\
		{<+ADJ><Pos><Masc><Akk><Sg>}:{en}		$Fix#$ |\
		{<+ADJ><Pos><Fem><Nom><Sg>}:{e}			$Fix#$ |\
		{<+ADJ><Pos><Fem><Gen><Sg><St>}:{er}		$Fix#$ |\
		{<+ADJ><Pos><Fem><Gen><Sg><Sw/Mix>}:{en}	$Fix#$ |\
		{<+ADJ><Pos><Fem><Dat><Sg><St>}:{er}		$Fix#$ |\
		{<+ADJ><Pos><Fem><Dat><Sg><Sw/Mix>}:{en}	$Fix#$ |\
		{<+ADJ><Pos><Fem><Akk><Sg>}:{e}			$Fix#$ |\
		{<+ADJ><Pos><Neut><Nom><Sg><St/Mix>}:{es}	$Fix#$ |\
		{<+ADJ><Pos><Neut><Nom><Sg><Sw>}:{e}		$Fix#$ |\
		{<+ADJ><Pos><Neut><Gen><Sg>}:{en}		$Fix#$ |\
		{<+ADJ><Pos><Neut><Dat><Sg><St>}:{em}		$Fix#$ |\
		{<+ADJ><Pos><Neut><Dat><Sg><Sw/Mix>}:{en}	$Fix#$ |\
		{<+ADJ><Pos><Neut><Akk><Sg><St/Mix>}:{es}	$Fix#$ |\
		{<+ADJ><Pos><Neut><Akk><Sg><Sw>}:{e}		$Fix#$ |\
		{<+ADJ><Pos><NoGend><Nom><Pl><St>}:{e}		$Fix#$ |\
		{<+ADJ><Pos><NoGend><Nom><Pl><Sw/Mix>}:{en}	$Fix#$ |\
		{<+ADJ><Pos><NoGend><Gen><Pl><Sw/Mix>}:{en}	$Fix#$ |\
		{<+ADJ><Pos><NoGend><Gen><Pl><St>}:{er}		$Fix#$ |\
		{<+ADJ><Pos><NoGend><Dat><Pl>}:{en}		$Fix#$ |\
		{<+ADJ><Pos><NoGend><Akk><Pl><Sw/Mix>}:{en}	$Fix#$ |\
		{<+ADJ><Pos><NoGend><Akk><Pl><St>}:{e}		$Fix#$ |\
		{<+NN><Masc><Nom><Sg><St/Mix>}:{er}		$Fix#$ |\
		{<+NN><Masc><Nom><Sg><Sw>}:{e}			$Fix#$ |\
		{<+NN><Masc><Gen><Sg>}:{en}			$Fix#$ |\
		{<+NN><Masc><Dat><Sg><St>}:{em}			$Fix#$ |\
		{<+NN><Masc><Dat><Sg><Sw/Mix>}:{en}		$Fix#$ |\
		{<+NN><Masc><Akk><Sg>}:{en}			$Fix#$ |\
		{<+NN><Fem><Nom><Sg>}:{e}			$Fix#$ |\
		{<+NN><Fem><Gen><Sg><St>}:{er}			$Fix#$ |\
		{<+NN><Fem><Gen><Sg><Sw/Mix>}:{en}		$Fix#$ |\
		{<+NN><Fem><Dat><Sg><St>}:{er}			$Fix#$ |\
		{<+NN><Fem><Dat><Sg><Sw/Mix>}:{en}		$Fix#$ |\
		{<+NN><Fem><Akk><Sg>}:{e}			$Fix#$ |\
		{<+NN><Neut><Nom><Sg><St/Mix>}:{es}		$Fix#$ |\
		{<+NN><Neut><Nom><Sg><Sw>}:{e}			$Fix#$ |\
		{<+NN><Neut><Gen><Sg>}:{en}			$Fix#$ |\
		{<+NN><Neut><Dat><Sg><St>}:{em}			$Fix#$ |\
		{<+NN><Neut><Dat><Sg><Sw/Mix>}:{en}		$Fix#$ |\
		{<+NN><Neut><Akk><Sg><St/Mix>}:{es}		$Fix#$ |\
		{<+NN><Neut><Akk><Sg><Sw>}:{e}			$Fix#$ |\
		{<+NN><NoGend><Nom><Pl><St>}:{e}		$Fix#$ |\
		{<+NN><NoGend><Nom><Pl><Sw/Mix>}:{en}		$Fix#$ |\
		{<+NN><NoGend><Gen><Pl><Sw/Mix>}:{en}		$Fix#$ |\
		{<+NN><NoGend><Gen><Pl><St>}:{er}		$Fix#$ |\
		{<+NN><NoGend><Dat><Pl>}:{en}			$Fix#$ |\
		{<+NN><NoGend><Akk><Pl><Sw/Mix>}:{en}		$Fix#$ |\
		{<+NN><NoGend><Akk><Pl><St>}:{e}		$Fix#$ |\
		{<+ADJ><Pos><Pred>}:{<>}			$Closed#$ |\
		{<+ADJ><Pos><Masc><Nom><Sg><St/Mix>}:{er}	$Closed#$ |\
		{<+ADJ><Pos><Masc><Nom><Sg><Sw>}:{e}		$Closed#$ |\
		{<+ADJ><Pos><Masc><Gen><Sg>}:{en}		$Closed#$ |\
		{<+ADJ><Pos><Masc><Dat><Sg><St>}:{em}		$Closed#$ |\
		{<+ADJ><Pos><Masc><Dat><Sg><Sw/Mix>}:{en}	$Closed#$ |\
		{<+ADJ><Pos><Masc><Akk><Sg>}:{en}		$Closed#$ |\
		{<+ADJ><Pos><Fem><Nom><Sg>}:{e}			$Closed#$ |\
		{<+ADJ><Pos><Fem><Gen><Sg><St>}:{er}		$Closed#$ |\
		{<+ADJ><Pos><Fem><Gen><Sg><Sw/Mix>}:{en}	$Closed#$ |\
		{<+ADJ><Pos><Fem><Dat><Sg><St>}:{er}		$Closed#$ |\
		{<+ADJ><Pos><Fem><Dat><Sg><Sw/Mix>}:{en}	$Closed#$ |\
		{<+ADJ><Pos><Fem><Akk><Sg>}:{e}			$Closed#$ |\
		{<+ADJ><Pos><Neut><Nom><Sg><St/Mix>}:{es}	$Closed#$ |\
		{<+ADJ><Pos><Neut><Nom><Sg><Sw>}:{e}		$Closed#$ |\
		{<+ADJ><Pos><Neut><Gen><Sg>}:{en}		$Closed#$ |\
		{<+ADJ><Pos><Neut><Dat><Sg><St>}:{em}		$Closed#$ |\
		{<+ADJ><Pos><Neut><Dat><Sg><Sw/Mix>}:{en}	$Closed#$ |\
		{<+ADJ><Pos><Neut><Akk><Sg><St/Mix>}:{es}	$Closed#$ |\
		{<+ADJ><Pos><Neut><Akk><Sg><Sw>}:{e}		$Closed#$ |\
		{<+ADJ><Pos><NoGend><Nom><Pl><St>}:{e}		$Closed#$ |\
		{<+ADJ><Pos><NoGend><Nom><Pl><Sw/Mix>}:{en}	$Closed#$ |\
		{<+ADJ><Pos><NoGend><Gen><Pl><Sw/Mix>}:{en}	$Closed#$ |\
		{<+ADJ><Pos><NoGend><Gen><Pl><St>}:{er}		$Closed#$ |\
		{<+ADJ><Pos><NoGend><Dat><Pl>}:{en}		$Closed#$ |\
		{<+ADJ><Pos><NoGend><Akk><Pl><Sw/Mix>}:{en}	$Closed#$ |\
		{<+ADJ><Pos><NoGend><Akk><Pl><St>}:{e}		$Closed#$ |\
		{<+NN><Masc><Nom><Sg><St/Mix>}:{er}		$Closed#Up$ |\
		{<+NN><Masc><Nom><Sg><Sw>}:{e}			$Closed#Up$ |\
		{<+NN><Masc><Gen><Sg>}:{en}			$Closed#Up$ |\
		{<+NN><Masc><Dat><Sg><St>}:{em}			$Closed#Up$ |\
		{<+NN><Masc><Dat><Sg><Sw/Mix>}:{en}		$Closed#Up$ |\
		{<+NN><Masc><Akk><Sg>}:{en}			$Closed#Up$ |\
		{<+NN><Fem><Nom><Sg>}:{e}			$Closed#Up$ |\
		{<+NN><Fem><Gen><Sg><St>}:{er}			$Closed#Up$ |\
		{<+NN><Fem><Gen><Sg><Sw/Mix>}:{en}		$Closed#Up$ |\
		{<+NN><Fem><Dat><Sg><St>}:{er}			$Closed#Up$ |\
		{<+NN><Fem><Dat><Sg><Sw/Mix>}:{en}		$Closed#Up$ |\
		{<+NN><Fem><Akk><Sg>}:{e}			$Closed#Up$ |\
		{<+NN><Neut><Nom><Sg><St/Mix>}:{es}		$Closed#Up$ |\
		{<+NN><Neut><Nom><Sg><Sw>}:{e}			$Closed#Up$ |\
		{<+NN><Neut><Gen><Sg>}:{en}			$Closed#Up$ |\
		{<+NN><Neut><Dat><Sg><St>}:{em}			$Closed#Up$ |\
		{<+NN><Neut><Dat><Sg><Sw/Mix>}:{en}		$Closed#Up$ |\
		{<+NN><Neut><Akk><Sg><St/Mix>}:{es}		$Closed#Up$ |\
		{<+NN><Neut><Akk><Sg><Sw>}:{e}			$Closed#Up$ |\
		{<+NN><NoGend><Nom><Pl><St>}:{e}		$Closed#Up$ |\
		{<+NN><NoGend><Nom><Pl><Sw/Mix>}:{en}		$Closed#Up$ |\
		{<+NN><NoGend><Gen><Pl><Sw/Mix>}:{en}		$Closed#Up$ |\
		{<+NN><NoGend><Gen><Pl><St>}:{er}		$Closed#Up$ |\
		{<+NN><NoGend><Dat><Pl>}:{en}			$Closed#Up$ |\
		{<+NN><NoGend><Akk><Pl><Sw/Mix>}:{en}		$Closed#Up$ |\
		{<+NN><NoGend><Akk><Pl><St>}:{e}		$Closed#Up$



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% building the inflection transducer             %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

$FLEXION$ = 	<>:<Abk_ADJ>		$Abk_ADJ$ |\
		<>:<Abk_ADV>		$Abk_ADV$ |\
		<>:<Abk_ART>		$Abk_ART$ |\
		<>:<Abk_DPRO>		$Abk_DPRO$ |\
		<>:<Abk_KONJ>		$Abk_KONJ$ |\
		<>:<Abk_NE>		$Abk_NE$ |\
		<>:<Abk_NE-Low>		$Abk_NE-Low$ |\
		<>:<Abk_NN>		$Abk_NN$ |\
		<>:<Abk_NN-Low>		$Abk_NN-Low$ |\
		<>:<Abk_PREP>		$Abk_PREP$ |\
		<>:<Abk_VPPAST>		$Abk_VPPAST$ |\
		<>:<Abk_VPPRES>		$Abk_VPPRES$ |\
		<>:<Adj+>		$Adj+$ |\
		<>:<Adj&>		$Adj&$ |\
		<>:<Adj+(e)>		$Adj+(e)$ |\
		<>:<Adj+Lang>		$Adj+Lang$ |\
		<>:<Adj+e>		$Adj+e$ |\
		<>:<Adj-el/er>		$Adj-el/er$ |\
		<>:<Adj0>		$Adj0$ |\
		<>:<Adj0-Up>		$Adj0-Up$ |\
		<>:<AdjComp>		$AdjComp$ |\
		<>:<AdjSup>		$AdjSup$ |\
		<>:<AdjFlexSuff>	$AdjFlexSuff$ |\
		<>:<AdjNN>		$AdjNN$ |\
		<>:<AdjNNSuff>		$AdjNNSuff$ |\
		<>:<AdjPos>		$AdjPos$ |\
		<>:<AdjPosAttr>		$AdjPosAttr$ |\
		<>:<AdjPosPred>		$AdjPosPred$ |\
		<>:<AdjPosSup>		$AdjPosSup$ |\
		<>:<Adj$>		$Adj\$$ |\
		<>:<Adj$e>		$Adj\$e$ |\
		<>:<Adj~+e>		$Adj~+e$ |\
		<>:<Adv>		$Adv$ |\
		<>:<Card>		$Card$ |\
		<>:<Ord>		$Ord$ |\
		<>:<DigOrd>		$DigOrd$ |\
		<>:<Circp>		$Circp$ |\
		<>:<FamName_0>		$FamName_0$ |\
		<>:<FamName_s>		$FamName_s$ |\
		<>:<Intj>		$Intj$ |\
		<>:<IntjUp>		$IntjUp$ |\
		<>:<IpKL>		$IpKL$ |\
		<>:<IpKR>		$IpKR$ |\
		<>:<IpKo>		$IpKo$ |\
		<>:<IpNorm>		$IpNorm$ |\
		<>:<Konj-Inf>		$Konj-Inf$ |\
		<>:<Konj-Kon>		$Konj-Kon$ |\
		<>:<Konj-Sub>		$Konj-Sub$ |\
		<>:<Konj-Vgl>		$Konj-Vgl$ |\
		<>:<N?/Pl_0>		$N?/Pl_0$ |\
		<>:<N?/Pl_x>		$N?/Pl_x$ |\
		<>:<Name-Pl_x>		$Name-Pl_x$ |\
		<>:<Name-Pl_0>		$Name-Pl_0$ |\
		<>:<NFem-Deriv>		$NFem-Deriv$ |\
		<>:<NFem-a/en>		$NFem-a/en$ |\
		<>:<NFem-in>		$NFem-in$ |\
		<>:<NFem-is/en>		$NFem-is/en$ |\
		<>:<NFem-is/iden>	$NFem-is/iden$ |\
		<>:<NFem-s/$sse>	$NFem-s/\$sse$ |\
		<>:<NFem-s/sse>		$NFem-s/sse$ |\
		<>:<NFem-s/ssen>	$NFem-s/ssen$ |\
		<>:<NFem/Pl>		$NFem/Pl$ |\
		<>:<NFem/Sg>		$NFem/Sg$ |\
		<>:<NFem_0_$>		$NFem_0_\$$ |\
		<>:<NFem_0_$e>		$NFem_0_\$e$ |\
		<>:<NFem_0_e>		$NFem_0_e$ |\
		<>:<NFem_0_en>		$NFem_0_en$ |\
		<>:<NFem_0_n>		$NFem_0_n$ |\
		<>:<NFem_0_s>		$NFem_0_s$ |\
		<>:<NFem_0_x>		$NFem_0_x$ |\
		<>:<NMasc-Adj>		$NMasc-Adj$ |\
		<>:<NMasc-ns>		$NMasc-ns$ |\
		<>:<NMasc-s/$sse>	$NMasc-s/\$sse$ |\
		<>:<NMasc-s/Sg>		$NMasc-s/Sg$ |\
		<>:<NMasc-s/sse>	$NMasc-s/sse$ |\
		<>:<NMasc-s0/sse>	$NMasc-s0/sse$ |\
		<>:<NMasc-us/en>	$NMasc-us/en$ |\
		<>:<NMasc-us/i>		$NMasc-us/i$ |\
		<>:<NMasc/Pl>		$NMasc/Pl$ |\
		<>:<NMasc/Sg_0>		$NMasc/Sg_0$ |\
		<>:<NMasc/Sg_es>	$NMasc/Sg_es$ |\
		<>:<NMasc/Sg_s>		$NMasc/Sg_s$ |\
		<>:<NMasc_0_x>		$NMasc_0_x$ |\
		<>:<NMasc_en_en=in>	$NMasc_en_en=in$ |\
		<>:<NMasc_en_en>	$NMasc_en_en$ |\
		<>:<NMasc_es_$e>	$NMasc_es_\$e$ |\
		<>:<NMasc_es_$er>	$NMasc_es_\$er$ |\
		<>:<NMasc_es_e>		$NMasc_es_e$ |\
		<>:<NMasc_es_en>	$NMasc_es_en$ |\
		<>:<NMasc_n_n=$in>	$NMasc_n_n=\$in$ |\
		<>:<NMasc_n_n=in>	$NMasc_n_n=in$ |\
		<>:<NMasc_n_n>		$NMasc_n_n$ |\
		<>:<NMasc_s_$>		$NMasc_s_\$$ |\
		<>:<NMasc_s_$x>		$NMasc_s_\$x$ |\
		<>:<NMasc_s_0=in>	$NMasc_s_0=in$ |\
		<>:<NMasc_s_0>		$NMasc_s_0$ |\
		<>:<NMasc_s_e=in>	$NMasc_s_e=in$ |\
		<>:<NMasc_s_e>		$NMasc_s_e$ |\
		<>:<NMasc_s_en=in>	$NMasc_s_en=in$ |\
		<>:<NMasc_s_en>		$NMasc_s_en$ |\
		<>:<NMasc_s_n>		$NMasc_s_n$ |\
		<>:<NMasc_s_s>		$NMasc_s_s$ |\
		<>:<NMasc_s_x>		$NMasc_s_x$ |\
		<>:<NNeut-0/ien>	$NNeut-0/ien$ |\
		<>:<NNeut-Dimin>	$NNeut-Dimin$ |\
		<>:<NNeut-Herz>		$NNeut-Herz$ |\
		<>:<NNeut-a/ata>	$NNeut-a/ata$ |\
		<>:<NNeut-a/en>		$NNeut-a/en$ |\
		<>:<NNeut-on/a>		$NNeut-on/a$ |\
		<>:<NNeut-s/$sser>	$NNeut-s/\$sser$ |\
		<>:<NNeut-s/sse>	$NNeut-s/sse$ |\
		<>:<NNeut-um/a>		$NNeut-um/a$ |\
		<>:<NNeut-um/en>	$NNeut-um/en$ |\
		<>:<NNeut/Pl>		$NNeut/Pl$ |\
		<>:<NNeut/Sg_0>		$NNeut/Sg_0$ |\
		<>:<NNeut/Sg_es>	$NNeut/Sg_es$ |\
		<>:<NNeut/Sg_en>	$NNeut/Sg_en$ |\
		<>:<NNeut/Sg_s>		$NNeut/Sg_s$ |\
		<>:<NNeut_0_x>		$NNeut_0_x$ |\
		<>:<NNeut_es_$e>	$NNeut_es_\$e$ |\
		<>:<NNeut_es_$er>	$NNeut_es_\$er$ |\
		<>:<NNeut_es_e>		$NNeut_es_e$ |\
		<>:<NNeut_es_en>	$NNeut_es_en$ |\
		<>:<NNeut_es_er>	$NNeut_es_er$ |\
		<>:<NNeut_s_$>		$NNeut_s_\$$ |\
		<>:<NNeut_s_0>		$NNeut_s_0$ |\
		<>:<NNeut_s_e>		$NNeut_s_e$ |\
		<>:<NNeut_s_en>		$NNeut_s_en$ |\
		<>:<NNeut_s_n>		$NNeut_s_n$ |\
		<>:<NNeut_s_s>		$NNeut_s_s$ |\
		<>:<NNeut_s_x>		$NNeut_s_x$ |\
% 		<>:<Name+er/in>		$Name+er/in$ |\
		<>:<Name-Fem_0>		$Name-Fem_0$ |\
		<>:<Name-Fem_s>		$Name-Fem_s$ |\
		<>:<Name-Invar>		$Name-Invar$ |\
		<>:<Name-Masc_0>	$Name-Masc_0$ |\
		<>:<Name-Masc_s>	$Name-Masc_s$ |\
% 		<>:<Name-Neut+Loc>	$Name-Neut+Loc$ |\
		<>:<Name-Neut_0>	$Name-Neut_0$ |\
		<>:<Name-Neut_s>	$Name-Neut_s$ |\
		<>:<Name-Pl_0>		$Name-Pl_0$ |\
		<>:<Name-Pl_x>		$Name-Pl_x$ |\
		<>:<NumAdjFlex>		$NumAdjFlex$ |\
		<>:<PInd-Invar>		$PInd-Invar$ |\
		<>:<Postp-Akk>		$Postp-Akk$ |\
		<>:<Postp-Dat>		$Postp-Dat$ |\
		<>:<Postp-Gen>		$Postp-Gen$ |\
		<>:<Prep-Akk>		$Prep-Akk$ |\
		<>:<Prep-Dat>		$Prep-Dat$ |\
		<>:<Prep-Gen>		$Prep-Gen$ |\
		<>:<Pref/Adj>		$Pref/Adj$ |\
		<>:<Pref/Adv>		$Pref/Adv$ |\
		<>:<Pref/N>		$Pref/N$ |\
		<>:<Pref/ProAdv>	$Pref/ProAdv$ |\
		<>:<Pref/Sep>		$Pref/Sep$ |\
		<>:<Pref/V>		$Pref/V$ |\
		<>:<Prep/Art-m>		$Prep/Art-m$ |\
		<>:<Prep/Art-n>		$Prep/Art-n$ |\
		<>:<Prep/Art-r>		$Prep/Art-r$ |\
		<>:<Prep/Art-s>		$Prep/Art-s$ |\
		<>:<ProAdv>		$ProAdv$ |\
		<>:<Ptkl-Adj>		$Ptkl-Adj$ |\
		<>:<Ptkl-Ant>		$Ptkl-Ant$ |\
		<>:<Ptkl-Neg>		$Ptkl-Neg$ |\
		<>:<Ptkl-Zu>		$Ptkl-Zu$ |\
		<>:<SpecChar>		$SpecChar$ |\
		<>:<Symbol>		$Symbol$ |\
		<>:<VAImpPl>		$VAImpPl$ |\
		<>:<VAImpSg>		$VAImpSg$ |\
		<>:<VAPastKonj2>	$VAPastKonj2$ |\
		<>:<VAPres1/3PlInd>	$VAPres1/3PlInd$ |\
		<>:<VAPres1SgInd>	$VAPres1SgInd$ |\
		<>:<VAPres2PlInd>	$VAPres2PlInd$ |\
		<>:<VAPres2SgInd>	$VAPres2SgInd$ |\
		<>:<VAPres3SgInd>	$VAPres3SgInd$ |\
		<>:<VAPresKonjPl>	$VAPresKonjPl$ |\
		<>:<VAPresKonjSg>	$VAPresKonjSg$ |\
		<>:<VInf>		$VInf$ |\
		<>:<VInf+PPres>		$VInf+PPres$ |\
		<>:<VMPast>		$VMPast$ |\
		<>:<VMPastKonj>		$VMPastKonj$ |\
		<>:<VMPresPl>		$VMPresPl$ |\
		<>:<VMPresSg>		$VMPresSg$ |\
		<>:<VPPast>		$VPPast$ |\
		<>:<VPPres>		$VPPres$ |\
		<>:<VPastIndReg>	$VPastIndReg$ |\
		<>:<VPastIndStr>	$VPastIndStr$ |\
		<>:<VPastKonjStr>	$VPastKonjStr$ |\
		<>:<VPresKonj>		$VPresKonj$ |\
		<>:<VPresPlInd>		$VPresPlInd$ |\
		<>:<VVPP-en>		$VVPP-en$ |\
		<>:<VVPP-t>		$VVPP-t$ |\
		<>:<VVPastIndReg>	$VVPastIndReg$ |\
		<>:<VVPastIndStr>	$VVPastIndStr$ |\
		<>:<VVPastKonjReg>	$VVPastKonjReg$ |\
		<>:<VVPastKonjStr>	$VVPastKonjStr$ |\
		<>:<VVPastStr>		$VVPastStr$ |\
		<>:<VVPres>		$VVPres$ |\
		<>:<VVPres1>		$VVPres1$ |\
		<>:<VVPres1+Imp>	$VVPres1+Imp$ |\
		<>:<VVPres2>		$VVPres2$ |\
		<>:<VVPres2+Imp>	$VVPres2+Imp$ |\
		<>:<VVPres2+Imp0>	$VVPres2+Imp0$ |\
		<>:<VVPres2t>		$VVPres2t$ |\
		<>:<VVPresPl>		$VVPresPl$ |\
		<>:<VVPresSg>		$VVPresSg$ |\
		<>:<VVReg>		$VVReg$ |\
		<>:<VVReg-el/er>	$VVReg-el/er$ |\
		<>:<WAdv>		$WAdv$



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% definition of a filter which enforces the correct inflection %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


ALPHABET = [\!-\~¡-ÿ] <FB><UL><DEL-S><n><e><d><~n><Ge-Nom><UL><SS>\
	<^imp><^zz><ge><no-ge><^pp><^Ax><^pl><^Gen><^Del> \
% upper/lower case features
<Fix#><Low#><Up#>

$ANY$ = .*

$FLEXFILTER$ = (<Abk_ADJ>:<>		<Abk_ADJ>:<> |\
		<Abk_ADV>:<>		<Abk_ADV>:<> |\
		<Abk_ART>:<>		<Abk_ART>:<> |\
		<Abk_DPRO>:<>		<Abk_DPRO>:<> |\
		<Abk_KONJ>:<>		<Abk_KONJ>:<> |\
		<Abk_NE-Low>:<>		<Abk_NE-Low>:<> |\
		<Abk_NE>:<>		<Abk_NE>:<> |\
		<Abk_NN-Low>:<>		<Abk_NN-Low>:<> |\
		<Abk_NN>:<>		<Abk_NN>:<> |\
		<Abk_PREP>:<>		<Abk_PREP>:<> |\
		<Abk_VPPAST>:<>		<Abk_VPPAST>:<> |\
		<Abk_VPPRES>:<>		<Abk_VPPRES>:<> |\
		<Adj$>:<>		<Adj$>:<> |\
		<Adj$e>:<>		<Adj$e>:<> |\
		<Adj+(e)>:<>		<Adj+(e)>:<> |\
		<Adj+>:<>		<Adj+>:<> |\
		<Adj&>:<>		<Adj&>:<> |\
		<Adj+Lang>:<>		<Adj+Lang>:<> |\
		<Adj+e>:<>		<Adj+e>:<> |\
		<Adj-el/er>:<>		<Adj-el/er>:<> |\
		<Adj0>:<>		<Adj0>:<> |\
		<Adj0-Up>:<>		<Adj0-Up>:<> |\
		<AdjComp>:<>		<AdjComp>:<> |\
		<AdjSup>:<>		<AdjSup>:<> |\
		<AdjFlexSuff>:<>	<AdjFlexSuff>:<> |\
		<AdjNN>:<>		<AdjNN>:<> |\
		<AdjNNSuff>:<>		<AdjNNSuff>:<> |\
		<AdjPos>:<>		<AdjPos>:<> |\
		<AdjPosAttr>:<>		<AdjPosAttr>:<> |\
		<AdjPosPred>:<>		<AdjPosPred>:<> |\
		<AdjPosSup>:<>		<AdjPosSup>:<> |\
		<Adj~+e>:<>		<Adj~+e>:<> |\
		<Adv>:<>		<Adv>:<> |\
		<Card>:<>		<Card>:<> |\
		<Ord>:<>		<Ord>:<> |\
		<DigOrd>:<>		<DigOrd>:<> |\
		<Circp>:<>		<Circp>:<> |\
		<FamName_0>:<>		<FamName_0>:<> |\
		<FamName_s>:<>		<FamName_s>:<> |\
		<Intj>:<>		<Intj>:<> |\
		<IntjUp>:<>		<IntjUp>:<> |\
		<IpKL>:<>		<IpKL>:<> |\
		<IpKR>:<>		<IpKR>:<> |\
		<IpKo>:<>		<IpKo>:<> |\
		<IpNorm>:<>		<IpNorm>:<> |\
		<Konj-Inf>:<>		<Konj-Inf>:<> |\
		<Konj-Kon>:<>		<Konj-Kon>:<> |\
		<Konj-Sub>:<>		<Konj-Sub>:<> |\
		<Konj-Vgl>:<>		<Konj-Vgl>:<> |\
		<N?/Pl_0>:<>		<N?/Pl_0>:<> |\
		<N?/Pl_x>:<>		<N?/Pl_x>:<> |\
		<Name-Pl_x>:<>		<Name-Pl_x>:<> |\
		<Name-Pl_0>:<>		<Name-Pl_0>:<> |\
		<NFem-Deriv>:<>		<NFem-Deriv>:<> |\
		<NFem-a/en>:<>		<NFem-a/en>:<> |\
		<NFem-in>:<>		<NFem-in>:<> |\
		<NFem-is/en>:<>		<NFem-is/en>:<> |\
		<NFem-is/iden>:<>	<NFem-is/iden>:<> |\
		<NFem-s/$sse>:<>	<NFem-s/$sse>:<> |\
		<NFem-s/sse>:<>		<NFem-s/sse>:<> |\
		<NFem-s/ssen>:<>	<NFem-s/ssen>:<> |\
		<NFem/Pl>:<>		<NFem/Pl>:<> |\
		<NFem/Sg>:<>		<NFem/Sg>:<> |\
		<NFem_0_$>:<>		<NFem_0_$>:<> |\
		<NFem_0_$e>:<>		<NFem_0_$e>:<> |\
		<NFem_0_e>:<>		<NFem_0_e>:<> |\
		<NFem_0_en>:<>		<NFem_0_en>:<> |\
		<NFem_0_n>:<>		<NFem_0_n>:<> |\
		<NFem_0_s>:<>		<NFem_0_s>:<> |\
		<NFem_0_x>:<>		<NFem_0_x>:<> |\
		<NMasc-Adj>:<>		<NMasc-Adj>:<> |\
		<NMasc-ns>:<>		<NMasc-ns>:<> |\
		<NMasc-s/$sse>:<>	<NMasc-s/$sse>:<> |\
		<NMasc-s/Sg>:<>		<NMasc-s/Sg>:<> |\
		<NMasc-s/sse>:<>	<NMasc-s/sse>:<> |\
		<NMasc-s0/sse>:<>	<NMasc-s0/sse>:<> |\
		<NMasc-us/en>:<>	<NMasc-us/en>:<> |\
		<NMasc-us/i>:<>		<NMasc-us/i>:<> |\
		<NMasc/Pl>:<>		<NMasc/Pl>:<> |\
		<NMasc/Sg_0>:<>		<NMasc/Sg_0>:<> |\
		<NMasc/Sg_es>:<>	<NMasc/Sg_es>:<> |\
		<NMasc/Sg_s>:<>		<NMasc/Sg_s>:<> |\
		<NMasc_0_x>:<>		<NMasc_0_x>:<> |\
		<NMasc_en_en,NMasc_n_n>:<> [<NMasc_en_en><NMasc_n_n>]:<> |\
		<NMasc_en_en=in>:<>	<NMasc_en_en=in>:<> |\
		<NMasc_en_en>:<>	<NMasc_en_en>:<> |\
		<NMasc_es_$e>:<>	<NMasc_es_$e>:<> |\
		<NMasc_es_$er>:<>	<NMasc_es_$er>:<> |\
		<NMasc_es_e>:<>		<NMasc_es_e>:<> |\
		<NMasc_es_en>:<>	<NMasc_es_en>:<> |\
		<NMasc_n_n=$in>:<>	<NMasc_n_n=$in>:<> |\
		<NMasc_n_n=in>:<>	<NMasc_n_n=in>:<> |\
		<NMasc_n_n>:<>		<NMasc_n_n>:<> |\
		<NMasc_s_$>:<>		<NMasc_s_$>:<> |\
		<NMasc_s_$x>:<>		<NMasc_s_$x>:<> |\
		<NMasc_s_0=in>:<>	<NMasc_s_0=in>:<> |\
		<NMasc_s_0>:<>		<NMasc_s_0>:<> |\
		<NMasc_s_e=in>:<>	<NMasc_s_e=in>:<> |\
		<NMasc_s_e>:<>		<NMasc_s_e>:<> |\
		<NMasc_s_en=in>:<>	<NMasc_s_en=in>:<> |\
		<NMasc_s_en>:<>		<NMasc_s_en>:<> |\
		<NMasc_s_n>:<>		<NMasc_s_n>:<> |\
		<NMasc_s_s>:<>		<NMasc_s_s>:<> |\
		<NMasc_s_x>:<>		<NMasc_s_x>:<> |\
		<NNeut-0/ien>:<>	<NNeut-0/ien>:<> |\
		<NNeut-Dimin>:<>	<NNeut-Dimin>:<> |\
		<NNeut-Herz>:<>		<NNeut-Herz>:<> |\
		<NNeut-a/ata>:<>	<NNeut-a/ata>:<> |\
		<NNeut-a/en>:<>		<NNeut-a/en>:<> |\
		<NNeut-on/a>:<>		<NNeut-on/a>:<> |\
		<NNeut-s/$sser>:<>	<NNeut-s/$sser>:<> |\
		<NNeut-s/sse>:<>	<NNeut-s/sse>:<> |\
		<NNeut-um/a>:<>		<NNeut-um/a>:<> |\
		<NNeut-um/en>:<>	<NNeut-um/en>:<> |\
		<NNeut/Pl>:<>		<NNeut/Pl>:<> |\
		<NNeut/Sg_0>:<>		<NNeut/Sg_0>:<> |\
		<NNeut/Sg_es>:<>	<NNeut/Sg_es>:<> |\
		<NNeut/Sg_en>:<>	<NNeut/Sg_en>:<> |\
		<NNeut/Sg_s>:<>		<NNeut/Sg_s>:<> |\
		<NNeut_0_x>:<>		<NNeut_0_x>:<> |\
		<NNeut_es_$e>:<>	<NNeut_es_$e>:<> |\
		<NNeut_es_$er>:<>	<NNeut_es_$er>:<> |\
		<NNeut_es_e>:<>		<NNeut_es_e>:<> |\
		<NNeut_es_en>:<>	<NNeut_es_en>:<> |\
		<NNeut_es_er>:<>	<NNeut_es_er>:<> |\
		<NNeut_s_$>:<>		<NNeut_s_$>:<> |\
		<NNeut_s_0>:<>		<NNeut_s_0>:<> |\
		<NNeut_s_e>:<>		<NNeut_s_e>:<> |\
		<NNeut_s_en>:<>		<NNeut_s_en>:<> |\
		<NNeut_s_n>:<>		<NNeut_s_n>:<> |\
		<NNeut_s_s>:<>		<NNeut_s_s>:<> |\
		<NNeut_s_x>:<>		<NNeut_s_x>:<> |\
% 		<Name+er/in>:<>		<Name+er/in>:<> |\
		<Name-Fem_0>:<>		<Name-Fem_0>:<> |\
		<Name-Fem_s>:<>		<Name-Fem_s>:<> |\
		<Name-Invar>:<>		<Name-Invar>:<> |\
		<Name-Masc_0>:<>	<Name-Masc_0>:<> |\
		<Name-Masc_s>:<>	<Name-Masc_s>:<> |\
% 		<Name-Neut+Loc>:<>	<Name-Neut+Loc>:<> |\
		<Name-Neut_0>:<>	<Name-Neut_0>:<> |\
		<Name-Neut_s>:<>	<Name-Neut_s>:<> |\
		<Name-Pl_0>:<>		<Name-Pl_0>:<> |\
		<Name-Pl_x>:<>		<Name-Pl_x>:<> |\
		<NumAdjFlex>:<>		<NumAdjFlex>:<> |\
		<PInd-Invar>:<>		<PInd-Invar>:<> |\
		<Postp-Akk>:<>		<Postp-Akk>:<> |\
		<Postp-Dat>:<>		<Postp-Dat>:<> |\
		<Postp-Gen>:<>		<Postp-Gen>:<> |\
		<Pref/Adj>:<>		<Pref/Adj>:<> |\
		<Pref/Adv>:<>		<Pref/Adv>:<> |\
		<Pref/N>:<>		<Pref/N>:<> |\
		<Pref/ProAdv>:<>	<Pref/ProAdv>:<> |\
		<Pref/Sep>:<>		<Pref/Sep>:<> |\
		<Pref/V>:<>		<Pref/V>:<>|\
		<Prep-Akk>:<>		<Prep-Akk>:<> |\
		<Prep-Dat>:<>		<Prep-Dat>:<> |\
		<Prep-Gen>:<>		<Prep-Gen>:<> |\
		<Prep/Art-m>:<>		<Prep/Art-m>:<> |\
		<Prep/Art-n>:<>		<Prep/Art-n>:<> |\
		<Prep/Art-r>:<>		<Prep/Art-r>:<> |\
		<Prep/Art-s>:<>		<Prep/Art-s>:<> |\
		<ProAdv>:<>		<ProAdv>:<> |\
		<Ptkl-Adj>:<>		<Ptkl-Adj>:<> |\
		<Ptkl-Ant>:<>		<Ptkl-Ant>:<> |\
		<Ptkl-Neg>:<>		<Ptkl-Neg>:<> |\
		<Ptkl-Zu>:<>		<Ptkl-Zu>:<> |\
		<SpecChar>:<>		<SpecChar>:<> |\
		<Symbol>:<>		<Symbol>:<> |\
		<VAImpPl>:<>		<VAImpPl>:<> |\
		<VAImpSg>:<>		<VAImpSg>:<> |\
		<VAPastKonj2>:<>	<VAPastKonj2>:<> |\
		<VAPres1/3PlInd>:<>	<VAPres1/3PlInd>:<> |\
		<VAPres1SgInd>:<>	<VAPres1SgInd>:<> |\
		<VAPres2PlInd>:<>	<VAPres2PlInd>:<> |\
		<VAPres2SgInd>:<>	<VAPres2SgInd>:<> |\
		<VAPres3SgInd>:<>	<VAPres3SgInd>:<> |\
		<VAPresKonjPl>:<>	<VAPresKonjPl>:<> |\
		<VAPresKonjSg>:<>	<VAPresKonjSg>:<> |\
		<VInf>:<>		<VInf>:<> |\
		<VInf+PPres>:<>		<VInf+PPres>:<> |\
		<VMPast>:<>		<VMPast>:<> |\
		<VMPastKonj>:<>		<VMPastKonj>:<> |\
		<VMPresPl>:<>		<VMPresPl>:<> |\
		<VMPresSg>:<>		<VMPresSg>:<> |\
		<VPPast>:<>		<VPPast>:<> |\
		<VPPres>:<>		<VPPres>:<> |\
		<VPastIndReg>:<>	<VPastIndReg>:<> |\
		<VPastIndStr>:<>	<VPastIndStr>:<> |\
		<VPastKonjStr>:<>	<VPastKonjStr>:<> |\
		<VPresKonj>:<>		<VPresKonj>:<> |\
		<VPresPlInd>:<>		<VPresPlInd>:<> |\
		<VVPP-en>:<>		<VVPP-en>:<> |\
		<VVPP-t>:<>		<VVPP-t>:<> |\
		<VVPastIndReg>:<>	<VVPastIndReg>:<> |\
		<VVPastIndStr>:<>	<VVPastIndStr>:<> |\
		<VVPastKonjReg>:<>	<VVPastKonjReg>:<> |\
		<VVPastKonjStr>:<>	<VVPastKonjStr>:<> |\
		<VVPastStr>:<>		<VVPastStr>:<> |\
		<VVPres>:<>		<VVPres>:<> |\
		<VVPres1>:<>		<VVPres1>:<> |\
		<VVPres1+Imp>:<>	<VVPres1+Imp>:<> |\
		<VVPres2>:<>		<VVPres2>:<> |\
		<VVPres2+Imp>:<>	<VVPres2+Imp>:<> |\
		<VVPres2+Imp0>:<>	<VVPres2+Imp0>:<> |\
		<VVPres2t>:<>		<VVPres2t>:<> |\
		<VVPresPl>:<>		<VVPresPl>:<> |\
		<VVPresSg>:<>		<VVPresSg>:<> |\
		<VVReg>:<>		<VVReg>:<> |\
		<VVReg-el/er>:<>	<VVReg-el/er>:<> |\
		<WAdv>:<>		<WAdv>:<>) $ANY$
