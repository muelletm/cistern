%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  File:     inflection.fst
%  Author:   Helmut Schmid; IMS, Universitaet Stuttgart
%  Content:  definition of inflectional classes
%  Modified: Fri Jun 17 16:04:22 2005 (schmid)   
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% definition of the symbol classes
#include "symbols.fst"


%%% definition of the inflectional classes %%%%%%%%%%%%%%%%%%%%%%%%%%%%

$AdjReg$ =  {<positive>}:{} |\
	    {<comparative>}:{er} |\
	    {<superlative>}:{est}

$AdvReg$ =  <>

$NounReg$ = {<sg>}:{} |\
	    {<pl>}:{s}

$NounSg$ =  <sg>:<>

$NounPl$ =  <pl>:<>

$VerbReg$ = {<pres><sg><3>}:{s} |\
	    {<pres><sg>[<1><2>]}:{} |\
	    {<pres><pl>[<1><2><3>]}:{} |\
	    {<past>[<sg><pl>][<1><2><3>]}:{ed} |\
	    {<past><part>}:{ed} |\
	    {<pres><part>}:{ing}


% adding a tag for the inflectional class

$LCInfl$ = <>:<AdjReg>	$AdjReg$ |\
	   <>:<AdvReg>	$AdvReg$ |\
	   <>:<NounReg>	$NounReg$ |\
	   <>:<NounSg>	$NounSg$ |\
	   <>:<NounPl>	$NounPl$ |\
	   <>:<VerbReg>	$VerbReg$

% no capitalized or fixed word forms yet
% $UCInfl$ = ...
% $FixInfl$ = ...

% The capitalization of the resulting word form is indicated by
% the three feature tags <LC> (lower case the first character),
% <Cap> (capitalize the first character) and <Fix> (do nothing)

$LCInfl$ = $LCInfl$ <>:<LC>

% $UCInfl$ = $UCInfl$ <>:<UC>
% $FixInfl$ = $FixInfl$ <>:<Fix>

$LCInfl$ % | $UCInfl$ | $FixInfl$
