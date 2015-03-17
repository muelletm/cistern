# Introduction #

HMM-LAs [(Huang et al., 2009)](http://aclweb.org/anthology/N/N09/N09-2054.pdf) are HMMs that induce latent sub-tags to increase tagging accuracy.

This implementation can be either used as a standard HMM tagger or to refine the annotations of an existing treebank.

# Download #

To get the latest binary release of HMMLA, please visit HMMLA's [CIS home page](http://www.cis.lmu.de/~muellets/hmmla/CURRENT).

# Source Code #

# Source Code #

https://github.com/muelletm/cistern/tree/master/hmmla

# Training #

Assuming a training file esp.train with the following form:

<pre>
1	El	el	d<br>
2	aumento	aumento	n<br>
3	del	del	s<br>
4	índice	índice	n<br>
</pre>
(Note, that the lemmata are not necessary.) A refining model can be trained by calling:

<pre>
java -ea -cp hmmla.jar hmmla.Trainer -train-file form-index=1,tag-index=3,esp.train\<br>
-model-name esp.hmmla\<br>
-num-tags 50\<br>
-refine true<br>
</pre>

Where form-index=1 specifies that the word form can bound in the second column.

# Tagging #

Assuming a test file esp.test of the same format as esp.train we can annotate the file with:

<pre>
java -ea -cp hmmla.jar hmmla.Tagger  -model-name esp.hmmla\<br>
-test-file form-index=1,tag-index=3,conll2007/esp.test\<br>
-pred-file esp.test.pred<br>
</pre>

The output esp.test.pred will have the following form:

<pre>
0	Las	d1<br>
1	reservas	n10<br>
2	de	s010<br>
3	oro	n11<br>
</pre>

Have a look at [example.sh](https://code.google.com/p/cistern/source/browse/trunk/hmmla/example.sh) for a complete example.

# References #

If you use HMMLA in your research and would like to acknowledge it, please refer to the following paper.

<pre>
@InProceedings{mueller2014,<br>
author = {M\"uller, Thomas and Farkas, Richard and Alex, Judea and Schmid, Helmut and Sch\"utze, Hinrich},<br>
title = {Dependency parsing with latent refinements of part-of-speech tags},<br>
booktitle = {Proceedings of the 2014 Conference on Empirical Methods in Natural Language Processing},<br>
year = {2014},<br>
}<br>
</pre>
