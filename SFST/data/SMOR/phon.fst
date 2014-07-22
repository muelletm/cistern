% *************************************************************************
%  File:         phon.fst
%  Author:       Helmut Schmid; IMS, University of Stuttgart
%  Date:         April 2003
%  Content:      two-level rules for German (TWOLC) --
%		 phonological and orthographic rules -- 
% 		 converted to S-FST from phon.rules
%**************************************************************************

%**************************************************************************
% Allomorphs
% i<n>loyal ==> illoyal
% i<n>materiell ==> immateriell
% i<n>materiell ==> immateriell
%**************************************************************************

ALPHABET = [\!-\~¡-ÿ] <e><d> \
	<CB><FB><UL><DEL-S><SS><WB> \
	<^UC><^Ax><^pl><^Gen><^Del><NoHy><NoDef> \
	<n>:[nlmrn] <d>:[dfgklnpst] <~n>:[<>n]

$R0$ =  (. |\
	<n>:n <CB> [ac-knoqs-zäöüßAC-KNOQS-ZÄÖÜ] |\
	<n>:l <CB> [Ll] |\
	<n>:m <CB> [BbMmPp] |\
	<n>:[rn] <CB> [Rr] |\
        <d>:d <CB> [a-ehijmoqru-xäöüßA-EHIJMOQRU-XÄÖÜ] |\
        <d>:f <CB> [Ff] |\
        <d>:g <CB> [Gg] |\
        <d>:k <CB> [Kk] |\
        <d>:l <CB> [Ll] |\
        <d>:n <CB> [Nn] |\
        <d>:p <CB> [Pp] |\
        <d>:s <CB> [Ss] |\
        <d>:t <CB> [Tt] |\
       <~n>:<><CB> [bcdfghjklmnpqrstvwxyz] |\
       <~n>:n <CB> [AEIOUÄÖÜaeiouäöü]) *


%**************************************************************************
% Umlaut
% Apfel$		==> Äpfel
% alter$e 		==> ältere
% Saal$e		==> Säle
% Schwabe<^Del>$in	==> Schwäbin
% Tochter$		==> Töchter
%**************************************************************************

ALPHABET = [\!-\~¡-ÿ] \
	<CB><FB><DEL-S><SS><WB> \
	<^UC><^Ax><e><^pl><^Gen><^Del><NoHy><NoDef><UL>:<FB> \
	[aouAOU]:[äöüÄÖÜ] a:<>

$Cons$ = [bcdfghjklmnpqrstvwxyzß]
$ConsUp$ = [BCDFGHJKLMNPQRSTVWXYZ]
$LC$ = <CB> | <WB> | <NoHy> | <NoDef> | <^UC> | $Cons$ | $ConsUp$

$R1$ =	($LC$ [aouAOU]<=>[äöüÄÖÜ] ([au]:.? $Cons$* <CB>?(<SS>|(e($Cons$|<^Del>)))?<UL>:<FB>)) &\
	(([aA]:[äÄ]) a <=> <> ($Cons$))


%**************************************************************************
% ß/ss-alternation
% (1) obligatorisch nach kurzem Vokal und vor 'e'
% Fluß~+es	==> Flusses
% Fuß+es	==> Fußes
% Zeugnis~+es	==> Zeugnisses
%**************************************************************************

ALPHABET = [\!-\~¡-ÿ] \
	<CB><FB><DEL-S><SS><WB> \
	<^UC><^Ax><e><^pl><^Gen><^Del><NoHy><NoDef> \
	[ß<SS>]:s
$Bound$ = [<FB><DEL-S>]

$R2$ =	(ß <=> s (<CB>? <SS>:. $Bound$ e)) & \
	((ß:. <CB>? | s) <SS> <=> s $Bound$ e)


%**************************************************************************
% e-elision after e
% Bote+e	==> Bote
% leise$er	==> leiser
%**************************************************************************

ALPHABET = [\!-\~¡-ÿ] \
	<CB><FB><DEL-S><SS><WB> \
	<^UC><^Ax><e><^pl><^Gen><^Del><NoHy><NoDef> \
	e:<>

$R3$ = e <=> <> ($Bound$ e)


%**************************************************************************
% optional e-elision with genitive
% Tisch+es	==> Tisches, Tischs
% Haus+es	==> Hauses
% Fluß~+es	==> Flusses
% Fuß+es	==> Fußes
% Zeugnis~+es	==> Zeugnisses
%**************************************************************************

$R4$ = ([bcdfghjklmnpqrtuvwy] <CB>? $Bound$) e => <> (s <^Gen>)


%**************************************************************************
% e-elision before '
% hab+e's	==> hab's
% kauf+t's	==> kauft's
%**************************************************************************

$R5$ = e <=> <> ('s)


%**************************************************************************
% adjective-el/er e-elision
% dunkel<^Ax>+e		==> dunkle
% sicher<^Ax>+e		==> sichere, sichre 
% sicher<^Ax>+em		==> sicherem, sicherm 
% schwer+e		==> schwere
%**************************************************************************

$R6$ = e <=> <> (l <^Ax> $Bound$ e)

$R7$ = e => <> (r <^Ax> $Bound$ e)
% $R7$ = e => <> ((r <^Ax> $Bound$ e) | ([lr] (<CB>|<FB>)+ [eui]))

$R8$ = (er <^Ax> $Bound$) e => <> ([mns])


%**************************************************************************
% verb-el/er e-elision
% sicher<^Vx>+en		==> sichern
% handel<^Vx>+en		==> handeln
% sicher<^Vx>+e		==> sichre, sichere
% handel<^Vx>+e		==> handle, ?handele
% sicher<^Vx>+est	==> sicherst, *sichrest, ?sicherest
% handel<^Vx>+est	==> handelst, *handlest, ?handelest
%**************************************************************************

$R9$ = (<e>[lr] (<CB> | $Bound$)) e <=> <> (n | s?t)

ALPHABET = [\!-\~¡-ÿ] \
	<CB><FB><DEL-S><SS><WB> \
	<^UC><^Ax><e><^pl><^Gen><^Del><NoHy><NoDef> \
	<e>:<>

$R11$ = <e> => <> ([lr] (<CB>|$Bound$) [eui])


%**************************************************************************
% s-elimination
% ras&st	==> (du) rast
% feix&st	==> (du) feixt
% birs+st	==> (du) birst
% groß$st 	==> größt
%**************************************************************************

ALPHABET = [\!-\~¡-ÿ] \
	<CB><FB><DEL-S><SS><WB> <e>:e\
	<^UC><^Ax><^pl><^Gen><^Del><NoHy><NoDef> \
	s:<>

$R12$ = ([xsßz<SS>] $Bound$) s <=> <> t


%**************************************************************************
% e-epenthesis
% regn&t	==> regnet
% find&st	==> findest
% bet&st	==> betest
% gelieb&t&st	==> geliebtest
% gewappn&t&st  ==> gewappnetst
%**************************************************************************
% different to DMOR

ALPHABET = [\!-\~¡-ÿ] \
	<CB><FB><DEL-S><SS><WB> \
	<^UC><^Ax><^pl><^Gen><^Del><NoHy><NoDef> \
	<DEL-S>:[e<>]

% gewappn&t&st  ==> gewappnetst
$R13$ = ((((c[hk])|[bdfgmp])n) <DEL-S> <=> e) & \
	((<DEL-S>:e[dt]) <DEL-S> <=> <>)


ALPHABET = [\!-\~¡-ÿ] \
	<CB><FB><DEL-S><SS><WB> \
	<^UC><^Ax><^pl><^Gen><^Del><NoHy><NoDef> \
	<DEL-S>:e

$R14$ = ([dt]m? | tw ) <DEL-S> <=> e


%**************************************************************************
% Consonant reduction for analysis of old orthography
% Schiff=fahrt		==> Schiffahrt, Schifffahrt
% Schiff=fracht		==> Schifffracht
% voll=laufen		==> vollaufen, volllaufen
% Sperr=rad		==> Sperrad, Sperrrad
%**************************************************************************

ALPHABET = [\!-\~¡-ÿ] \
	<CB><FB><DEL-S><SS><WB> \
	<^UC><^Ax><^pl><^Gen><^Del><NoHy><NoDef> \
	f:<>
$Rf$ = f f => <> (<CB> [fF] [aeiouäöü])

ALPHABET = [\!-\~¡-ÿ] \
	<CB><FB><DEL-S><SS><WB> \
	<^UC><^Ax><^pl><^Gen><^Del><NoHy><NoDef> \
	t:<>
$Rt$ = t t => <> (<CB> [tT] [aeiouäöü])

ALPHABET = [\!-\~¡-ÿ] \
	<CB><FB><DEL-S><SS><WB> \
	<^UC><^Ax><^pl><^Gen><^Del><NoHy><NoDef> \
	m:<>
$Rm$ = m m => <> (<CB> [mM] [aeiouäöü])

ALPHABET = [\!-\~¡-ÿ] \
	<CB><FB><DEL-S><SS><WB> \
	<^UC><^Ax><^pl><^Gen><^Del><NoHy><NoDef> \
	n:<>
$Rn$ = n n => <> (<CB> [nN] [aeiouäöü])

ALPHABET = [\!-\~¡-ÿ] \
	<CB><FB><DEL-S><SS><WB> \
	<^UC><^Ax><^pl><^Gen><^Del><NoHy><NoDef> \
	l:<>
$Rl$ = l l => <> (<CB> [lL] [aeiouäöü])

ALPHABET = [\!-\~¡-ÿ] \
	<CB><FB><DEL-S><SS><WB> \
	<^UC><^Ax><^pl><^Gen><^Del><NoHy><NoDef> \
	r:<>
$Rr$ = r r => <> (<CB> [rR] [aeiouäöü])

$R15$ = ($Rf$ || $Rt$ || $Rm$) || ($Rn$ || $Rl$ || $Rr$)


%**************************************************************************
% eliminate letters
% Virus<^pl>+en		==> Viren
% Museum<^pl>+en	==> Museen
% Affrikata<^pl>+en	==> Affrikaten		
%**************************************************************************

ALPHABET = [\!-\~¡-ÿ] \
	<CB><FB><DEL-S><SS><WB> \
	<^UC><^Ax><^pl><^Gen><^Del><NoHy><NoDef> \
	[uio]:<>

% eliminate -is/-us/-um/-on/-os
$R16$ = [uio] <=> <> ([mns]:. <^pl>)

ALPHABET = [\!-\~¡-ÿ] \
	<CB><FB><DEL-S><SS><WB> \
	<^UC><^Ax><^pl><^Gen><^Del><NoHy><NoDef> \
	[mnsa]:<>
$R17$ = [mnsa] <=> <> <^pl>

% eliminate e 
ALPHABET = [\!-\~¡-ÿ] \
	<CB><FB><DEL-S><SS><WB> \
	<^UC><^Ax><^pl><^Gen><^Del><NoHy><NoDef> \
	e:<>
$R18$ = e <=> <> <^Del>
	

%**************************************************************************
% Eliminate markers 
%**************************************************************************

ALPHABET = [\!-\~¡-ÿ] <CB><^UC><NoHy><NoDef> \
	[<DEL-S><SS><FB><^Gen><^Del><^pl><^Ax><WB>]:<>

$R19$ = .*


%**************************************************************************
% up to low
%**************************************************************************

ALPHABET = [\!-\~¡-ÿ] <^UC><NoHy><NoDef> <CB>:<> [A-ZÄÖÜ]:[a-zäöü]

$R20$ = <CB>:<> [A-ZÄÖÜ] <=> [a-zäöü]


%**************************************************************************
% low to up
%**************************************************************************

ALPHABET = [\!-\~¡-ÿ] <NoHy><NoDef> <^UC>:<> [a-zäöü]:[A-ZÄÖÜ]

$R21$ = ((<^UC>:<>) [a-zäöü] <=> [A-ZÄÖÜ]) & \
	!(.* <^UC>:<> .:[a-zäöü] .*)


%**************************************************************************
%  Composition of rules  
%**************************************************************************

$T1$ = $R0$ || $R1$ || $R2$ || $R3$ || $R4$
$T2$ = $R5$ || $R6$ || $R7$ || $R8$
$T3$ = $R9$ || $R11$ || $R12$
$T4$ = $R13$ || $R14$ || $R15$
$T5$ = $R16$ || $R17$ || $R18$
$T6$ = $R19$ || $R20$ || $R21$

$X1$ = $T1$ || $T2$ || $T3$
$X2$ = $T4$ || $T5$ || $T6$

% result transducer
$X1$ || $X2$
