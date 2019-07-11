# Introduction #

This article assumes that you know how to train standard MarMoT models.
If not read [Training new models](marmotTraining.md) first.

There are two ways of integrating the output of an analyzer (or any hard of soft word clustering).

# Morphological Dictionary #

The first way is to create a morphological dictionary, which is a text file listing a word form and a number of morphological features in each line. Word forms may occur multiple times. A German example could look like this:

<pre>
zweitägige      zweitägige      ADJA    degree=pos|gender=neut|case=nom|number=sg<br>
zweitägige      zweitägige      ADJA    degree=pos|gender=noGend|case=acc|number=pl<br>
zweitägige      zweitägige      ADJA    degree=pos|gender=noGend|case=nom|number=pl<br>
Zustrom         Zustrom         NN      gender=masc|case=acc|number=sg<br>
Zustrom         Zustrom         NN      gender=masc|case=dat|number=sg<br>
Zustrom         Zustrom         NN      gender=masc|case=nom|number=sg<br>
</pre>

In the example the dictionary lists the form, lemma, PoS and the morphological features.

If the dictionary file is 'mdict.txt' you can train a model using the following command:

<pre>
$ java -Xmx5G -cp marmot.jar marmot.morph.cmd.Trainer\<br>
-train-file form-index=1,tag-index=2,train.txt\<br>
-model-file de.marmot<br>
-type-dict mdict.txt,indexes=[2,3]<br>
</pre>

Where 2 and 3 are the columns that hold features you want to use.
The word form must be in column 0 and all other columns (the lemma in the example) are ignored.

**Warning:** The dictionary should not include the symbols `-` or `.` as these are treated specially by the format reader.

# Token Features #

While the morphological dictionary allows for simple feature integrating it does not provide a way to include word forms not know at training time. If MarMoT is to be used in a pipeline the token-feature-index is a more powerful alternative.

In order to use it you have to add a column containing the features to the training and test files:

<pre>
0       El       d       pos=article|gen=m|num=s     feat1#feat2<br>
1       género   n       pos=common|gen=m|num=s      feat4<br>
2       Marmota  n       pos=proper|gen=c|num=c      feat4#feat5<br>
3       incluye  v       pos=main|gen=c|num=s|per=3  feat5<br>
4       catorce  d       postype=numeral|gen=c|num=p feat1<br>
5       especies n       postype=common|gen=f|num=p  feat2#feat4<br>
6       .        f       punct=period                _<br>
</pre>

Where features are separated by the "#" symbol and columns consisting only of "`_`" get ignored. For training you just have to add an option specifying the column:

<pre>
$ java -Xmx5G -cp marmot.jar marmot.morph.cmd.Trainer\<br>
-train-file form-index=1,tag-index=2,morph-index=3,token-feature-index=4,train.txt\<br>
-tag-morph true\<br>
-model-file es.marmot<br>
</pre>

In contrast to the dictionary approach you also have to integrate the features during tagging:

<pre>
El       feat1#feat2<br>
género   feat4<br>
Marmota  feat4#feat5<br>
incluye  feat5<br>
catorce  feat1<br>
especies feat2#feat4<br>
.        _<br>
</pre>

We also have to add the feature index to the tagging command:

<pre>
java -cp marmot.jar marmot.morph.cmd.Annotator\<br>
--model-file de.marmot\<br>
--test-file form-index=0,token-feature-index=1,text.txt\<br>
--pred-file text.out.txt\<br>
</pre>
