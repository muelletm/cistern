SMOR - A German Computational Morphology

# Introduction #

SMOR is a comprehensive German finite-state morphology covering inflection, derivation and compounding which was developed at the [Institut für Maschinelle Sprachverarbeitung (IMS), University of Stuttgart](http://www.ims.uni-stuttgart.de/) in the years 2002 to 2013.

Here is an example analysis of SMOR for the word "Häuser":
```
Haus<+NN><Neut><Acc><Pl>
Haus<+NN><Neut><Gen><Pl>
Haus<+NN><Neut><Nom><Pl>
```

And here is the analysis of "unübersetzbarstes" showing prefixation, suffixation and gradation:
```
un<PREF>übersetzen<V>bar<SUFF><+ADJ><Sup><Neut><Nom><Sg><St>
un<PREF>übersetzen<V>bar<SUFF><+ADJ><Sup><Neut><Acc><Sg><St>
```

# License #

SMOR is freely available for non-commercial purposes such as research, education, and evaluation. Before using SMOR for commercial purposes, you must
purchase a license.

# Download #
A compiled SMOR transducer is available [here](http://www.cis.uni-muenchen.de/~schmid/tools/SMOR/data/SMOR-linux.tar.gz).


# Documentation #
Gertrud Faaß did an evaluation of SMOR and created a [documentation](http://www.cis.uni-muenchen.de/~schmid/tools/SMOR/dspin/) for it (in German).

# Developers #
SMOR was implemented by Helmut Schmid during his time at the Institut für maschinelle Sprachverarbeitung (IMS) of the University of Stuttgart and builds on earlier work on DMOR by Anne Schiller and on work done in the DeKo project. The SMOR morphology uses the IMSLex lexical database which provides information about stems and their inflection class. The development of the [IMSLex](http://www.ims.uni-stuttgart.de/forschung/ressourcen/lexika/IMSLex.html) resource was led by Prof. Ulrich Heid during his time at IMS. SMOR is jointly maintained by the universities
of Hildesheim ([Prof. Ulrich Heid](http://www.uni-hildesheim.de/fb3/institute/iwist/mitglieder/heid/"), Institut für Informationswissenschaft und
Sprachtechnologie),
Munich ([Dr. Helmut Schmid](http://www.cis.uni-muenchen.de/~schmid/), Centrum für Informations- und Sprachtechnologie) and Stuttgart
([Prof. Jonas Kuhn](http://www.ims.uni-stuttgart.de/institut/mitarbeiter/jonas/), IMS).

# Publications #
Please cite the following publication in order to refer to the SMOR morphology:

Helmut Schmid, Arne Fitschen and Ulrich Heid: SMOR: **A German Computational Morphology Covering Derivation, Composition, and Inflection**, Proceedings of the IVth International Conference on Language Resources and Evaluation (LREC 2004), p. 1263-1266, Lisbon, Portugal. ([bibtex](http://dblp.uni-trier.de/rec/bibtex/conf/lrec/SchmidFH04))

## Contact ##
Please send comments, suggestions and bug reports to Helmut Schmid at FirstName.LastName@cis.uni-muenchen.de. (Insert the name into the email address.)