# Introduction #

MarMoT can be used to train plain [part-of-speech (POS)](http://en.wikipedia.org/wiki/Part-of-speech) taggers as well as joint POS and morphological taggers.

# Training POS Taggers #

Given a training file train.txt in a column format:

<pre>
1  Marmots    NNS
2  are        VBP
3  large      JJ
4  ground     NN
5  squirrels  NNS
6  .          .
</pre>

A model can be trained by running:

<pre>
$ java -Xmx5G -cp marmot.jar marmot.morph.cmd.Trainer\
-train-file form-index=1,tag-index=2,train.txt\
-tag-morph false\
-model-file en.marmot
</pre>

# Training Morphological Taggers #

For training a morphological tagger we also need morphological information in the training file.

<pre>
0       El       d       pos=article|gen=m|num=s
1       género   n       pos=common|gen=m|num=s
2       Marmota  n       pos=proper|gen=c|num=c
3       incluye  v       pos=main|gen=c|num=s|per=3|mood=ind|tense=p
4       catorce  d       postype=numeral|gen=c|num=p
5       especies n       postype=common|gen=f|num=p
6       .        f       punct=period
</pre>

And we have to specify the index of the morphological tag:

<pre>
$ java -Xmx5G -cp marmot.jar marmot.morph.cmd.Trainer\
-train-file form-index=1,tag-index=2,morph-index=3,train.txt\
-tag-morph true\
-model-file es.marmot
</pre>

# Advanced Training Options #

Run the training command without arguments to have a look at advanced training options.

<pre>
$ java marmot.jar marmot.morph.cmd.Trainer
General Options:
	very-verbose:
		Whether to print a lot of status messages.
		Default value: "false"
	num-iterations:
		Number of training iterations.
		Default value: "10"
	oracle:
		Whether to do oracle pruning. Probably not relevant. Have a look at the paper!
		Default value: "false"
	seed:
		Random seed to use for shuffling. 0 for nondeterministic seed
		Default value: "42"
	penalty:
		L1 penalty parameter.
		Default value: "0.0"
	averaging:
		Whether to use averaging. Perceptron only!
		Default value: "true"
	optimize-num-iterations:
		Whether to optimize the number of training iterations on the dev set.
		Default value: "false"
	beam-size:
		Specify the beam size of the n-best decoder.
		Default value: "1"
	prob-threshold:
		Initial pruning threshold. Changing this value should have almost no effect.
		Default value: "0.01"
	initial-vector-size:
		Size of the weight vector.
		Default value: "10000000"
	verbose:
		Whether to print status messages.
		Default value: "false"
	max-transition-feature-level:
		Something for testing the code. Don't change it.
		Default value: "-1"
	prune:
		Whether to use pruning.
		Default value: "true"
	trainer:
		Which trainer to use. (There is also a perceptron trainer but don't use it.)
		Default value: "marmot.core.CrfTrainer"
	effective-order:
		Maximal order to reach before increasing the level.
		Default value: "1"
	candidates-per-state:
		Average number of states to obtain after pruning at each order. These are the mu values from the paper.
		Default value: "[4, 2, 1.5]"
	quadratic-penalty:
		L2 penalty parameter.
		Default value: "0.0"
	shuffle:
		Whether to shuffle between training iterations.
		Default value: "true"
	order:
		Set the model order.
		Default value: "2"

Morph Options:
	special-signature:
		Whether to mark if a word contains a special character in the word signature.
		Default value: "false"
	train-file:
		Input training file
		Default value: ""
	tag-morph:
		Whether to train a morphological tagger or a POS tagger.
		Default value: "true"
	restrict-transitions:
		Whether to only allow POS -> MORPH transitions that have been seen during training.
		Default value: "true"
	num-chunks:
		Number of chunks. CrossAnnotator only.
		Default value: "5"
	shape:
		Whether to use shape features.
		Default value: "false"
	type-dict:
		Word type dictionary file (optional)
		Default value: ""
	shape-trie-path:
		Path to the shape trie. Will be created if non-existent.
		Default value: ""
	use-hash-vector:
		Whether to use a hashed feature vector. Saves memory decreases accuracy.
		Default value: "true"
	split-pos:
		Whether to split POS tags. See subtag-separator. Have a look at the paper!
		Default value: "false"
	subtag-separator:
		Regular expression to use for splitting tags. (Has to work with Java's String.split)
		Default value: "\\|"
	use-default-features:
		Whether to extract default features such as prefixes, suffixes, word forms.
		Default value: "true"
	model-file:
		Output model file.
		Default value: ""
	pred-file:
		Output prediction file in CoNLL09. (optional for training)
		Default value: ""
	observed-feature:
		Whether to use the observed feature. Have a look at the paper!
		Default value: "true"
	test-file:
		Input test file. (optional for training)
		Default value: ""
	type-embeddings:
		Word type embeddings file (optional)
		Default value: ""
	form-normalization:
		Whether to normalize word forms before tagging.
		Default value: "none"
	num-folds:
		Number of folds used for estimation of open word classes.
		Default value: "10"
	max-affix-length:
		Max affix length to use in feature extraction.
		Default value: "10"
	rare-word-max-freq:
		Maximal frequency of a rare word.
		Default value: "10"
	internal-analyzer:
		Use an internal morphological analyzer. Currently supported: 'ar' for AraMorph (Arabic)
		Default value: ""
	split-morph:
		Whether to split MORPH tags. See subtag-separator. Have a look at the paper!
		Default value: "true"
	feature-templates:
		Comma separated list, activates individual templates.
		Default value: "form,rare,affix,context,sig,bigrams"</pre>
