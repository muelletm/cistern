![http://cistern.cis.lmu.de/marmot/marmot.png](http://cistern.cis.lmu.de/marmot/marmot.png)

# Introduction #

MarMoT is a generic [Conditional Random Field](http://en.wikipedia.org/wiki/Conditional_random_field) (CRF) framework as well as a state-of-the-art morphological tagger.

# Download #

To get the latest binary release of MarMoT, please visit MarMoT's [CIS home page](http://cistern.cis.lmu.de/marmot/CURRENT).

# Source Code #

https://github.com/muelletm/cistern/tree/master/marmot

# Quickstart #

The most typical thing to do with MarMoT is to annotate words with their morphological properties. Given a file text.txt in a one-word-per-line format:
<pre>
Murmeltiere
sind
im
Hochgebirge
zu
Hause
.
</pre>

The following command:
<pre>
java -cp marmot.jar marmot.morph.cmd.Annotator\
--model-file de.marmot\
--test-file form-index=0,text.txt\
--pred-file text.out.txt\
</pre>

Will produce a file in (a truncated) [CoNLL09](http://ufal.mff.cuni.cz/conll2009-st/task-description.html) format:

<pre>
0       Murmeltiere     _       _       _       NN      _       case=nom|number=pl|gender=masc
1       sind            _       _       _       VAFIN   _       number=pl|person=3|tense=pres|mood=ind
2       im              _       _       _       APPRART _       case=dat|number=sg|gender=neut
3       Hochgebirge     _       _       _       NN      _       case=dat|number=sg|gender=neut
4       zu              _       _       _       APPR    _       _
5       Hause           _       _       _       NN      _       case=dat|number=sg|gender=neut
6       .               _       _       _       $.      _       _
</pre>

The actual tags will depend on the annotation of the treebank that was used to train the MarMoT model. The tags here are in the [STTS](https://catalog.clarin.eu/isocat/rest/dcs/376) format used by [TIGER](http://www.ims.uni-stuttgart.de/forschung/ressourcen/korpora/tiger.html).

# Further Reading #
  * [Training new models](marmotTraining.md)
  * [Integrating the output of an Morphological Analyzer](marmotMorphologicalAnalyzer.md)
  * [Predictions for the SPMRL data sets](marmotSPMRL.md)

# Projects that use MarMoT #

  * [Turku NLP](http://turkunlp.github.io/Finnish-dep-parser/)

# References #

If you use MarMoT in your research and would like to acknowledge it, please refer to the following paper.

<pre>
@InProceedings{mueller2013,
author = {M\"uller, Thomas and Schmid, Helmut and Sch\"utze, Hinrich},
title = {Efficient Higher-Order CRFs for Morphological Tagging},
booktitle = {Proceedings of the 2013 Conference on Empirical Methods in Natural Language Processing},
year = {2013},
}
</pre>
