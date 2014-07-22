%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  File:         NUM.fst
%  Author:       Helmut Schmid; IMS, Universitaet Stuttgart
%  Date:         July 2003
%  Content:      definition of cardinal and ordinal number stems
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%          Cardinals                             %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

$CardStem/2-9$ = zw(ei|o) | drei | vier | fünf | sechs | sieben | acht | neun
$CardStem/1-9$ = eins | $CardStem/2-9$
$NumStem/10-19$ = zehn|elf|zwölf | (drei|vier|fünf|sech|sieb|acht|neun)zehn
$NumStem/10-19$ = zehn|elf|zwölf | (drei|vier|fünf|sech|sieb|acht|neun)zehn
$NumStem/20-90$ = dreißig | (zwan|vier|fünf|sech|sieb|acht|neun)zig

$Card/2-99$ = ( \
	$CardStem/1-9$ |\
	$NumStem/10-19$ |\
	((ein | $CardStem/2-9$) und)? $NumStem/20-90$ )

$Card/1-99$ = eins | $Card/2-99$

$Card/2-999$ = ( \
	$Card/2-99$ |\
	(ein | $CardStem/2-9$)? hundert ((und)? $Card/1-99$ )?)
$Card/1-999$ = eins | $Card/2-999$

$Card/2-999999$ = ( \
	$Card/2-999$ | \
	(ein | $Card/2-999$)? tausend ((und)? $Card/1-999$)? )

$CardBase0$ = null | eins | $Card/2-999999$

$CardDeriv0$ = null | ein | $Card/2-999999$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%         Ordinals                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

$OrdStem/3-9$ = dritt | viert | fünft | sechst | siebt | acht | neunt
$OrdStem/1-9$ = erst | zweit | $OrdStem/3-9$

$Ord/3-99$ = ( \
	$OrdStem/3-9$ |\
	$NumStem/10-19$t |\
	$NumStem/20-90$st |\
	(ein | $CardStem/2-9$) und $NumStem/20-90$st )
$Ord/1-99$ = erst | zweit | $Ord/3-99$

$Ord/3-999$ = ( \
	$Ord/3-99$ |\
	(ein | $CardStem/2-9$)? hundertst |\
	(ein | $CardStem/2-9$)? hundert (und)? $Ord/1-99$ )
$Ord/1-999$ = erst | zweit | $Ord/3-999$

$Ord/3-999999$ = ( \
	$Ord/3-999$ | \
	(ein | $Card/2-999$)? tausend (und)? $Ord/1-999$ )

$Ord0$ = nullt | erst | zweit | $Ord/3-999999$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%         Digit Numbers                          %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

$DigCard$ = [0-9]+ ([\,\./] [0-9]+)*
$DigOrd$ = $DigCard$\.


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%        Resulting transducers                   %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

$Quant$ = $CardDeriv0$ <QUANT>:<> |\
	  $Ord0$ <QUANT>:<> |\
	  $DigCard$ \-? <QUANT>:<> |\
	  (beid | mehr | viel | dies | doppel | ganz | gegen) <QUANT>:<>

$CbnC$ = {<>}:{<CARD><base><nativ><Card>}
$ObnO$ = {<>}:{<ORD><base><nativ><Ord>}
$ObnD$ = {<>}:{<ORD><base><nativ><DigOrd>}

$NumBase$ = {<>}:{<Initial><Base_Stems>}\
	($CardBase0$	$CbnC$ |\
	 $Ord0$		$ObnO$ |\
	 $DigCard$	$CbnC$ |\
	 $DigOrd$	$ObnD$)

$NumDeriv$ = <>:<Deriv_Stems>\
	 ($CardDeriv0$	<CARD> |\
	  $Ord0$	<ORD> |\
	  $DigCard$	<DIGCARD>) {<>}:{<deriv><nativ>}

$NumKompos$ = <>:<Kompos_Stems> $Ord0$ \
		<ORD>{<>}:{<kompos><nativ>}


$Num_Stems$ = $NumBase$ | $NumDeriv$ | $NumKompos$
