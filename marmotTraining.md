# Introduction #

MarMoT can be used to train plain [part-of-speech (POS)](http://en.wikipedia.org/wiki/Part-of-speech) taggers as well as joint POS and morphological taggers.

# Training POS Taggers #

Given a training file train.txt in a column format:

<pre>
1  Marmots    NNS<br>
2  are        VBP<br>
3  large      JJ<br>
4  ground     NN<br>
5  squirrels  NNS<br>
6  .          .<br>
</pre>

A model can be trained by running:

<pre>
$ java -Xmx5G -cp marmot.jar marmot.morph.cmd.Trainer\<br>
-train-file form-index=1,tag-index=2,train.txt\<br>
-tag-morph false\<br>
-model-file en.marmot<br>
</pre>

# Training Morphological Taggers #

For training a morphological tagger we also need morphological information in the training file.

<pre>
0       El       d       pos=article|gen=m|num=s<br>
1       género   n       pos=common|gen=m|num=s<br>
2       Marmota  n       pos=proper|gen=c|num=c<br>
3       incluye  v       pos=main|gen=c|num=s|per=3|mood=ind|tense=p<br>
4       catorce  d       postype=numeral|gen=c|num=p<br>
5       especies n       postype=common|gen=f|num=p<br>
6       .        f       punct=period<br>
</pre>

And we have to specify the index of the morphological tag:

<pre>
$ java -Xmx5G -cp marmot.jar marmot.morph.cmd.Trainer\<br>
-train-file form-index=1,tag-index=2,morph-index=3,train.txt\<br>
-tag-morph true\<br>
-model-file es.marmot<br>
</pre>

# Advanced Training Options #

Run the training command without arguments to have a look at advanced training options.

<pre>
$ java marmot.jar marmot.morph.cmd.Trainer<br>
<br>
General Options:<br>
prune:<br>
Whether to use pruning.<br>
Default value: "true"<br>
effective-order:<br>
Maximal order to reach before increasing the level.<br>
Default value: "1"<br>
seed:<br>
Random seed to use for shuffling. 0 for nondeterministic seed<br>
Default value: "0"<br>
prob-threshold:<br>
Initial pruning threshold. Changing this value should have almost no effect.<br>
Default value: "0.01"<br>
very-verbose:<br>
Whether to print a lot of status messages.<br>
Default value: "false"<br>
oracle:<br>
Whether to do oracle pruning. Probably not relevant. Have a look at the paper!<br>
Default value: "false"<br>
trainer:<br>
Which trainer to use. (There is also a perceptron trainer but don't use it.)<br>
Default value: "marmot.core.CrfTrainer"<br>
num-iterations:<br>
Number of training iterations.<br>
Default value: "10"<br>
candidates-per-state:<br>
Average number of states to obtain after pruning at each order. These are the mu values from the paper.<br>
Default value: "[4, 2, 1.5]"<br>
max-transition-feature-level:<br>
Something for testing the code. Don't change it.<br>
Default value: "-1"<br>
beam-size:<br>
Specify the beam size of the n-best decoder.<br>
Default value: "1"<br>
order:<br>
Set the model order.<br>
Default value: "2"<br>
initial-vector-size:<br>
Size of the weight vector.<br>
Default value: "10000000"<br>
averaging:<br>
Whether to use averaging. Perceptron only!<br>
Default value: "true"<br>
shuffle:<br>
Whether to shuffle between training iterations.<br>
Default value: "true"<br>
verbose:<br>
Whether to print status messages.<br>
Default value: "false"<br>
quadratic-penalty:<br>
L2 penalty parameter.<br>
Default value: "0.0"<br>
penalty:<br>
L1 penalty parameter.<br>
Default value: "0.0"<br>
<br>
Morph Options:<br>
type-embeddings:<br>
Word type embeddings file (optional)<br>
Default value: ""<br>
model-file:<br>
Output model file.<br>
Default value: ""<br>
observed-feature:<br>
Whether to use the observed feature. Have a look at the paper!<br>
Default value: "true"<br>
split-pos:<br>
Whether to split POS tags. See subtag-seperator. Have a look at the paper!<br>
Default value: "false"<br>
shape:<br>
Whether to use shape features.<br>
Default value: "false"<br>
restrict-transitions:<br>
Whether to only allow POS -> MORPH transitions that have been seen during training.<br>
Default value: "true"<br>
type-dict:<br>
Word type dictionary file (optional)<br>
Default value: ""<br>
split-morphs:<br>
Whether to split MORPG tags. See subtag-seperator. Have a look at the paper!<br>
Default value: "true"<br>
rare-word-max-freq:<br>
Maximal frequency of a rare word.<br>
Default value: "10"<br>
normalize-forms:<br>
Whether to normalize word forms before tagging.<br>
Default value: "false"<br>
train-file:<br>
Input training file<br>
Default value: ""<br>
pred-file:<br>
Output prediction file in CoNLL09. (optional for training)<br>
Default value: ""<br>
subtag-seperator:<br>
Regular expression to use for splitting tags. (Has to work with Java's String.split)<br>
Default value: "\\|"<br>
shape-trie-path:<br>
Path to the shape trie. Will be created if non-existent.<br>
Default value: ""<br>
tag-morph:<br>
Whether to train a morphological tagger or a POS tagger.<br>
Default value: "true"<br>
test-file:<br>
Input test file. (optional for training)<br>
Default value: ""<br>
<br>
</pre>