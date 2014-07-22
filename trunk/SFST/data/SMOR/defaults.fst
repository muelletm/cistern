%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  File:         defaults.fst
%  Author:       Helmut Schmid; IMS, University of Stuttgart
%  Date:         July 2003
%  Content:      generation of default base, derivation and composition stems
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


$TMP$ = $VPrefStems$ $BaseStems$ || $NoDef2NULL$ || $PREFFILTER$

$TMP$ = ($BaseStems$ | $TMP$) || $KOMPOSFILTER$


$ANY$ = [\!-\~¡-ÿ <FB><SS><n><~n><e><d><Ge-Nom><UL> <NoHy><NoDef><ge><no-ge><CB>\
	<Base_Stems><Deriv_Stems><Kompos_Stems><Pref_Stems><Suff_Stems>]*

$TMP$ = $TMP$ $FLEXION$ 
$TMP$ = $TMP$ || $ANY$ $FLEXFILTER$
$TMP$ = $TMP$ || $INFIXFILTER$
$TMP$ = $TMP$ || $UPLOW$


$TMP$ = <>:<WB> $TMP$ <>:<WB> || $PHON$


% default noun composition stems

$DefKomposNN$ = \
([\!-\~¡-ÿ]+  \
 ({<>}:{<+NN>[<Masc><Neut><Fem><NoGend>]<Nom><Sg>} |\
  {<>}:{<+NN>[<Masc><Neut>] <Gen><Sg>} |\
%  {<>}:{<^ADJ><Pos><+NN><NoGend><Nom><Pl><Sw/Mix>} |\
  {<>}:{<+NN>[<Masc><Neut><Fem><NoGend>] <Nom><Pl>} <>:<Sw/Mix>?) || $TMP$) \
<NN>

$T$ = [Aa]rbeit|[hk]eit|[Ff]ahrt|[Gg]ang|kunft|[Nn]acht|ion|[Pp]flicht|\
	schaft|[Ss]chrift|[Ss]icht|[Ss]ucht|tät|ung

$T$ = [\!-\~¡-ÿ]* $T$ {<>}:{<+NN>[<Fem><Masc>]<Nom><Sg>}

$T$ = $T$ || $TMP$

$DefKomposNN$ = $DefKomposNN$ |\
	($T$ <NN> || [\!-\~¡-ÿ]* <>:s <NN>)


% default noun derivation stems

% delete final e and en in derivation stems unless a vowel precedes
$c$ = [bcdfghj-np-tvwxzß]
$C$ = [BCDFGHJ-NP-TVWXZbcdfghj-np-tvwxzß]

ALPHABET = [\!-\~¡-ÿ] <NN> [en]:<>
$Del-e$ =  ($C$ e <=> <> ((n:.)? <NN>)) &\
	(e:. n <=> <> <NN>)

% allow Umlautung in verbal and nominal derivation stems
ALPHABET = [\!-\~¡-ÿ<e>] [Aa]:[Ää] a:<> [<NN><V>]
$R1$ =	(    A  => Ä  (u $c$* (e[rl])? [<NN><V>])) &\
	($C$ a  => ä  ([au]:. $c$* (e[rl])? [<NN><V>])) &\
	(a:ä a <=> <> ($c$* (e[rl])? [<NN><V>]))

ALPHABET = [\!-\~¡-ÿ<e>] [AOUaou]:[ÄÖÜäöü] [<NN><V>]
$R2$ =	(    [AOU] => [ÄÖÜ] ($c$* (e[rl])? [<NN><V>])) &\
	($C$ [aou] => [äöü] ($c$* (e[rl])? [<NN><V>]))

$Uml$ = $R1$ || $R2$

$DefDerivNN$ = (\
([\!-\~¡-ÿ]* \
 <>:<+NN> <>:[<Masc><Fem><Neut><NoGend>] <>:<Nom> <>:<Sg> || $TMP$) \
<NN>) || $Del-e$ || $Uml$

% default proper name derivation stems

$DefDerivNE$ = \
([\!-\~¡-ÿ]* <>:<+NE> <>:[<Masc><Neut><Fem><NoGend>] <>:<Nom> <>:<Sg> || $TMP$) \
<NE>


% default proper name composition stems

$DefKomposNE$ = $DefDerivNE$


% default adjective base stems

$DefBaseADJ$ = \
  (([\!-\~¡-ÿ<PREF>]* <V>:<+V><zu>?<PPast> ||\
   $TMP$ || $NoDef2NULL$ t) <>:<ADJ><SUFF>:<><>:<base><>:<nativ><>:<Adj+e>) |\
  (([\!-\~¡-ÿ<PREF>]* <V>:<+V><zu>?[<PPres><PPast>] ||\
   $TMP$ || $NoDef2NULL$ (en|nd)) <>:<ADJ><SUFF>:<><>:<base><>:<nativ><>:<Adj+>)

% default adjective composition and derivation stems

$DefKomposADJ$ = (\
  ([\!-\~¡-ÿ]* <>:<+ADJ> <>:[<Pos><Comp><Sup>] <>:<Pred> |\
   [\!-\~¡-ÿ<PREF>]* <V>:<+V><zu>?[<PPres><PPast>]) ||\
  $TMP$ || $NoDef2NULL$) <ADJ>

$DefDerivADJ$ = $DefKomposADJ$


% default verb composition stems

$DefKomposV$ = ([\!-\~¡-ÿ<PREF>]* {<>}:{<+V><Inf>} || $TMP$) <V> ||\
	[\!-\~¡-ÿ]* ({en}:{<>} | e[rl]n:<>) <V>

% default verb derivation stems

$DefDerivV$ = ([\!-\~¡-ÿ<PREF>]* {<>}:{<+V><Inf>} || $TMP$) <V> ||\
	<NoDef>:<>? [\!-\~¡-ÿ]* ({en}:{<>} | e:<e> [rl] n:<>) <V> || $Uml$

ALPHABET = [\!-\~¡-ÿ] <NoDef> <e> <V> n:<en>

$R$ = ([bdgptkfs] | ch) n <=> <en> (<V>)

ALPHABET = [\!-\~¡-ÿ] <NoDef> <e> <V>

$R$ = $R$ || (. | <>:<e><en>:n)*

$DefDerivV$ = $DefDerivV$ || $R$

$BDKStems$ = ($BDKStems$ || $NoDef2NULL$) | <>:<Base_Stems> $DefBaseADJ$ |\
(<>:<Deriv_Stems> ($DefDerivADJ$ | $DefDerivNN$ | $DefDerivNE$ | $DefDerivV$)\
 <>:<deriv> |\
 <>:<Kompos_Stems> ($DefKomposADJ$ | $DefKomposNE$ |\
		    $DefKomposNN$ | $DefKomposV$) <>:<kompos>)\
<>:<nativ>

% default stems for generating "Gejammer", "Gejammere", "Gejammre"

$T$ = $BDKStems$ || (\
  <NoDef>:<>? <ge> <Base_Stems>:<Deriv_Stems> [\!-\~¡-ÿ<e>]+ \
    (<>:e<V><base>:<deriv><nativ> [<VVReg><VVPres><VVPres1><VVPres1+Imp>]:<>|\
     ({<e>l}:{le}|{<e>r}:{re}|<>:e)? \
              <V><base>:<deriv><nativ><VVReg-el/er>:<>))

$BDKStems$ = $BDKStems$ | $T$

