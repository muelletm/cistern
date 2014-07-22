ALPHABET = [A-Za-z] y:i [e#]:<> 

% Read the lexical items from a separate file
% wherein each line contains a form like "dark"
$WORDS$ = "adj"

% Define a rule replacing y with i if a morpheme boundary and e follows
% easy#er -> easier
$R1$ = y<=>i (#:<> e)

% Define a rule eliminating e in the context "#e"
% late#er -> later
$R2$ = e<=><> (#:<> e)

% Compute the intersection of the two rule transducers
$R$ = $R1$ & $R2$

% Define a transducer for the inflectional endings
$INFL$ = <ADJ>:<> (<pos>:<> | <comp>:{er} | <sup>:{est})

% Concatenate the lexical forms and the inflectional endings with
% a morpheme boundary in between
$S$ = $WORDS$ <>:# $INFL$

% Apply the two level rules
$S$ || $R$
