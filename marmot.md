![http://cistern.cis.lmu.de/marmot/marmot.png](http://cistern.cis.lmu.de/marmot/marmot.png)

# Introduction #

MarMoT is a generic [Conditional Random Field](http://en.wikipedia.org/wiki/Conditional_random_field) (CRF) framework as well as a state-of-the-art morphological tagger.

# Download #

To get the latest binary release of MarMoT, please visit MarMoT's [CIS home page](http://cistern.cis.lmu.de/marmot/CURRENT).


# Quickstart #

The most typical thing to do with MarMoT is to annotate words with their morphological properties. Given a file text.txt in a one-word-per-line format:
<pre>
Murmeltiere<br>
sind<br>
im<br>
Hochgebirge<br>
zu<br>
Hause<br>
.<br>
</pre>

The following command:
<pre>
java -cp marmot.jar marmot.morph.cmd.Annotator\<br>
--model-file de.marmot\<br>
--test-file form-index=0,text.txt\<br>
--pred-file text.out.txt\<br>
</pre>

Will produce a file in (a truncated) [CoNLL09](http://ufal.mff.cuni.cz/conll2009-st/task-description.html) format:

<pre>
0       Murmeltiere     _       _       _       NN      _       case=nom|number=pl|gender=masc<br>
1       sind            _       _       _       VAFIN   _       number=pl|person=3|tense=pres|mood=ind<br>
2       im              _       _       _       APPRART _       case=dat|number=sg|gender=neut<br>
3       Hochgebirge     _       _       _       NN      _       case=dat|number=sg|gender=neut<br>
4       zu              _       _       _       APPR    _       _<br>
5       Hause           _       _       _       NN      _       case=dat|number=sg|gender=neut<br>
6       .               _       _       _       $.      _       _<br>
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
@InProceedings{mueller2013,<br>
author = {M\"uller, Thomas and Schmid, Helmut and Sch\"utze, Hinrich},<br>
title = {Efficient Higher-Order CRFs for Morphological Tagging},<br>
booktitle = {Proceedings of the 2013 Conference on Empirical Methods in Natural Language Processing},<br>
year = {2013},<br>
}<br>
</pre>