# SPMRL #

A team of [IMS](http://www.ims.uni-stuttgart.de/), [Szeged ](http://www.inf.u-szeged.hu) and [CIS](http://www.cis.lmu.de) participated in the [SPMRL 2013 shared task](http://www.spmrl.org/spmrl2013-sharedtask.html) on parsing morphologically rich languages.

The team used MarMoT for the prediction of PoS and morphological tags and submitted the highest scoring systems for all languages but French.

Here you can find the used predictions for training, development and test files. The training predictions were created by 5-fold jack-knifing. Details can be found in the system description [paper](http://aclweb.org/anthology/W/W13/W13-4916.pdf).

The predictions as well as a script for creating CoNLL 2009 files out of the SPMRL files can be downloaded [here](http://cistern.cis.lmu.de/marmot/marmot_spmrl.tar.bz2).

You can run the script with the following command:

<pre>
python make_conll09.py\<br>
-g GERMAN_SPMRL/gold/conll/train/train.German.gold.conll\<br>
-p GERMAN_SPMRL/pred/conll/train/train.German.pred.conll\<br>
-m train.German.marmot.txt -o train.German.conll09<br>
</pre>

Where the predicted SPMRL file is used to extract predicted lemmas. The predicted file can also be left unspecified.


**IMPORTANT: Until June, 13 2014 some of the files contained gold tags, please be sure to use the current version.**