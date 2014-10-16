#!/bin/bash

echo 'Downloading and decompressing data:'
wget http://nltk.github.com/nltk_data/packages/corpora/conll2007.zip
unzip -e conll2007

echo
echo 'Compiling:'

ant 

echo
echo 'Training:'

# This trains a refining model. That means that the tagger reads coarse tags and refines them to latent sub-tags.
# The tagger will however never change th underlying tag. In order to train a real tagger that annotates
# unlabeled data set -refine to false.

java -ea -cp hmmla.jar hmmla.Trainer -train-file form-index=1,tag-index=3,conll2007/esp.train\
                                     -model-name esp.hmmla\
                                     -num-tags 50\
                                     -refine true\
                                     -test-file form-index=1,tag-index=3,conll2007/esp.test
echo
echo 'Tagging:'

java -ea -cp hmmla.jar hmmla.Tagger  -model-name esp.hmmla\
                                     -test-file form-index=1,tag-index=3,conll2007/esp.test\
                                     -pred-file esp.test.pred

echo
echo '$ head esp.test.pred:'
head esp.test.pred
                                     
