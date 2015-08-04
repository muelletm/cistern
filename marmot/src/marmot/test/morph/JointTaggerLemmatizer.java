// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.test.morph;

import java.util.LinkedList;
import java.util.List;

import marmot.core.Options;
import marmot.core.Sequence;
import marmot.morph.MorphOptions;
import marmot.morph.io.SentenceReader;

import org.junit.Test;

public class JointTaggerLemmatizer {

	@Test
	public void smallTest() {
		
		MorphOptions options = new MorphOptions();
		options.setProperty(Options.VERBOSE, "true");
		options.setProperty(Options.SEED, "42");
		
		options.setProperty(Options.VECTOR_SIZE, "10000000");
		options.setProperty(Options.CANDIDATES_PER_STATE, "[4, 2, 1.5, 1.25]");
		options.setProperty(Options.PRUNE, "true");
		options.setProperty(Options.ORDER, "1");
		options.setProperty(Options.PENALTY, "1.0");
		
		options.setProperty(MorphOptions.TAG_MORPH, "true");
		options.setProperty(MorphOptions.LEMMATIZE, "true");
		options.setProperty(MorphOptions.GOLD_LEMMA, "false");
		options.setProperty(MorphOptions.LEMMA_PRETRAINING, "true");
		options.setProperty(MorphOptions.MARGINALIZE_LEMMAS, "false");
		options.setProperty(MorphOptions.LEMMA_TAG_DEPENDENT, "true");
		options.setProperty(MorphOptions.USE_HASH_FEATURE_TABLE, "true");
		options.setProperty(MorphOptions.LEMMA_LEMMING_GENERATOR, "true");
		//options.setProperty(MorphOptions.LEMMAS_IGNORE_FEATURES, "case=*|case=nom|case=acc|case=dat|case=gen");
		//options.setProperty(MorphOptions.LEMMA_USE_MORPH, "false");
		options.setProperty(Options.NUM_ITERATIONS, "10");
		
		options.setProperty(MorphOptions.TRAIN_FILE,
				"form-index=1,lemma-index=2,tag-index=4,morph-index=6,res:///marmot/test/morph/trn.txt");
		options.setProperty(MorphOptions.TEST_FILE,
				"form-index=1,lemma-index=2,tag-index=4,morph-index=6,res:///marmot/test/morph/tst.txt");
		
		List<Sequence> train_sentences = getSentences(options.getTrainFile(), 1000);
		List<Sequence> test_sentences = getSentences(options.getTestFile(), -1);

//		Joint
//		all : 10304 / 18939 = 54,4063%			0.0
//		lemma : 16691 / 18939 = 88,1303%
//		all : 9160 / 18939 = 48,3658%			1.0
//		lemma : 16536 / 18939 = 87,3119%
//		all : 10301 / 18939 = 54,3904%			0.01
//		lemma : 16637 / 18939 = 87,8452%
		
//		all :   14281 / 18939 = 75,4052%		0.0		1000
//		lemma : 18237 / 18939 = 96,2934%
		
//		all : 14300 / 18939 = 75,5056%			0.01	1000
//		lemma : 18234 / 18939 = 96,2775%
		
//		all : 14397 / 18939 = 76,0177%			0.1
//		lemma : 18217 / 18939 = 96,1878%
		
//		Pipeline
//		all : 9988 / 18939 = 52,7377%			0.0
//		all : 7946 / 18939 = 41,9558%			1.0
//		all : 9949 / 18939 = 52,5318%			0.01
		
//		all : 14613 / 18939 = 77,1582%			0.0		1000
//		all : 14628 / 18939 = 77,2374%			0.01	1000

		PipelineTest.testWithOptions(options, train_sentences, test_sentences, 1., 1., 1., 1.);
		//PipelineTest.testWithOptions(options, train_sentences, test_sentences, 99.56, 53.13, 100., 87.66);
	}
	
	@Test
	public void test() {
		
		MorphOptions options = new MorphOptions();
		options.setProperty(Options.VERBOSE, "true");
		options.setProperty(Options.SEED, "42");
		
		options.setProperty(Options.VECTOR_SIZE, "10000000");
		options.setProperty(Options.CANDIDATES_PER_STATE, "[4, 2, 1.5, 1.25]");
		options.setProperty(Options.PRUNE, "true");
		options.setProperty(Options.ORDER, "1");
		options.setProperty(Options.PENALTY, "0.0");
		
		options.setProperty(MorphOptions.TAG_MORPH, "false");
		options.setProperty(MorphOptions.LEMMATIZE, "false");
		options.setProperty(Options.NUM_ITERATIONS, "10");
		
		options.setProperty(MorphOptions.TRAIN_FILE,
				"form-index=1,lemma-index=2,tag-index=4,morph-index=6,res:///marmot/test/morph/trn.txt");
		options.setProperty(MorphOptions.TEST_FILE,
				"form-index=1,lemma-index=2,tag-index=4,morph-index=6,res:///marmot/test/morph/tst.txt");
		
		List<Sequence> train_sentences = getSentences(options.getTrainFile(), 1000);
		List<Sequence> test_sentences = getSentences(options.getTestFile(), -1);
		
		PipelineTest.testWithOptions(options, train_sentences, test_sentences, 98.53, 75.22, 99.88, 96.23);
	}

	private List<Sequence> getSentences(String trainFile, int limit) {
		List<Sequence> list = new LinkedList<>();
		for (Sequence sequence : new SentenceReader(trainFile)) {
			list.add(sequence);
		}
		
		if (limit >= 0 && list.size() > limit) {
			list = list.subList(0, limit);
		}
		
		return list;
	}

}
