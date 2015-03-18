## Introduction ##

MarLiN is a program suite to induce word clusters form text.
It is based on the algorithm by Martin, Lierman and Ney (1998).

## Usage ##

The following code will cluster the words in _example.txt_ into 10 classes.

<pre>
marlin_count --text example.txt --bigrams bigrams --words words
marlin_cluster --words words --bigrams bigrams --output --c 10
</pre>

_marlin\_count_ extracts a word list and bigram statistics and _marlin\_cluster_ performs the actual clustering.

## Source Code ##

https://github.com/muelletm/cistern/tree/master/marlin

## Pretrained Clusterings ##

Pretrained clusterings for English, German, Hungarian, Latin and Spanish can be found [here](http://cistern.cis.lmu.de/marmot/naacl2015/).

## References ##

If you use MarLiN in your research and would like to acknowledge it, please refer to the following paper.

<pre>
@InProceedings{mueller2015,
author = {M\"uller, Thomas and Sch\"utze, Hinrich},
title = {Robust Morphological Tagging with Word Representations},
booktitle = {Proceedings of NAACL},
year = {2015},
}</pre>

For the paper describing the algorithm refer to:

<pre>
@article{martin1998,
title={Algorithms for bigram and trigram word clustering},
author={Martin, Sven and Liermann, J{\"o}rg and Ney, Hermann},
journal={Speech communication},
year={1998},
}</pre>
