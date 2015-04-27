// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.test.morph;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import junit.framework.AssertionFailedError;

import org.junit.Test;

import marmot.core.Model;
import marmot.core.Options;
import marmot.core.PerceptronTrainer;
import marmot.core.Sequence;
import marmot.core.Tagger;
import marmot.core.Token;
import marmot.morph.MorphEvaluator;
import marmot.morph.MorphModel;
import marmot.morph.MorphOptions;
import marmot.morph.MorphResult;
import marmot.morph.Sentence;
import marmot.morph.Word;
import marmot.morph.io.SentenceReader;
import marmot.util.FileUtils;
import marmot.util.StringUtils.Mode;

public class PipelineTest {

	private String getResourceFile(String name) {
		Package pack = getClass().getPackage();		
		String path = pack.getName().replace(".", "/");
		return String.format("res:///%s/%s", path, name);
	}
	
	public static List<Sequence> getSentences(String filename, int number) {
		List<Sequence> list = new LinkedList<Sequence>();
		for (Sequence sentence : new SentenceReader(filename)) {
			list.add(sentence);
			if (number >= 0 && list.size() >= number) {
				break;
			}
		}
		return list;
	}

	public List<Sequence> getTrainSentences() {
		List<Sequence> sentences = new LinkedList<Sequence>();
		List<Word> tokens;

		tokens = new LinkedList<Word>();
		tokens.add(new Word("das", "A", "c=N|n=S"));
		tokens.add(new Word("ist", "V", "n=S"));
		tokens.add(new Word("ein", "A", "c=N|n=S"));
		tokens.add(new Word("Test", "N", "c=N|n=S"));
		tokens.add(new Word(".", ".", "_"));
		sentences.add(new Sentence(tokens));

		tokens = new LinkedList<Word>();
		tokens.add(new Word("die", "A", "c=N|n=P"));
		tokens.add(new Word("Rüben", "N", "c=N|n=P"));
		tokens.add(new Word("sind", "V", "n=P"));
		tokens.add(new Word("kalt", "J", "c=N|n=P"));
		tokens.add(new Word(".", ".", "_"));
		sentences.add(new Sentence(tokens));

		return sentences;
	}

	public List<Sequence> getTestSentences() {
		List<Word> tokens;
		tokens = new LinkedList<Word>();
		tokens.add(new Word("das", "A", "c=N|n=S"));
		tokens.add(new Word("ist", "V", "n=S"));
		tokens.add(new Word("mein", "A", "c=N|n=S"));
		tokens.add(new Word("Test", "N", "c=N|n=S"));
		tokens.add(new Word(".", ".", "_"));

		return Collections.singletonList((Sequence) new Sentence(tokens));
	}

	public Model getModel(Collection<Sequence> sentences, MorphOptions options) {
		MorphModel model = new MorphModel();
		model.init(options, sentences);
		return model;
	}

	@Test
	public void toyPosTest() {
		MorphOptions options = new MorphOptions();
		options.setProperty(Options.SEED, "42");
		options.setProperty(MorphOptions.NUM_ITERATIONS, "10");
		options.setProperty(MorphOptions.VECTOR_SIZE, "1024");
		options.setProperty(MorphOptions.CANDIDATES_PER_STATE, "[9, 9, 9]");
		options.setProperty(MorphOptions.PRUNE, "false");
		toyTestWithOptions(options);
		options.setProperty(MorphOptions.PRUNE, "true");
		options.setProperty(MorphOptions.TAG_MORPH, "false");
		toyTestWithOptions(options);
		options.setProperty(MorphOptions.CANDIDATES_PER_STATE,
				"[4, 2, 1.5, 1.25]");
		toyTestWithOptions(options);
	}

	@Test
	public void toyTest() {
		MorphOptions options = new MorphOptions();
		options.setProperty(Options.SEED, "42");
		options.setProperty(MorphOptions.NUM_ITERATIONS, "10");
		options.setProperty(MorphOptions.VECTOR_SIZE, "1024");
		options.setProperty(MorphOptions.CANDIDATES_PER_STATE, "[9, 9, 9]");
		options.setProperty(MorphOptions.PRUNE, "false");
		toyTestWithOptions(options);
		options.setProperty(MorphOptions.PRUNE, "true");
		toyTestWithOptions(options);
		options.setProperty(MorphOptions.CANDIDATES_PER_STATE,
				"[4, 2, 1.5, 1.25]");
		toyTestWithOptions(options);
	}

	@Test
	public void realTest() {
		MorphOptions options = new MorphOptions();
		options.setProperty(Options.SEED, "42");
		options.setProperty(Options.NUM_ITERATIONS, "10");
		options.setProperty(Options.VECTOR_SIZE, "10000000");
		options.setProperty(Options.CANDIDATES_PER_STATE, "[4, 2, 1.5, 1.25]");
		options.setProperty(Options.PRUNE, "true");
		options.setProperty(Options.ORDER, "3");
		options.setProperty(Options.PENALTY, ".1");
		options.setProperty(MorphOptions.USE_HASH_FEATURE_TABLE_, "true");
		options.setProperty(MorphOptions.TRAIN_FILE,
				"form-index=1,tag-index=4,morph-index=6," + getResourceFile("trn.txt"));
		options.setProperty(MorphOptions.TEST_FILE,
				"form-index=1,tag-index=4,morph-index=6," + getResourceFile("tst.txt"));
		realTestWithOptions(options, 98.51, 54.10);
	}
	
	@Test
	public void realInfixTest() {
		MorphOptions options = new MorphOptions();
		options.setProperty(Options.SEED, "42");
		options.setProperty(Options.NUM_ITERATIONS, "10");
		options.setProperty(Options.VECTOR_SIZE, "10000000");
		options.setProperty(Options.CANDIDATES_PER_STATE, "[4, 2, 1.5, 1.25]");
		options.setProperty(Options.PRUNE, "true");
		options.setProperty(Options.ORDER, "3");
		options.setProperty(Options.PENALTY, ".1");
		options.setProperty(MorphOptions.MAX_AFFIX_LENGTH, "5");
		options.setProperty(MorphOptions.FEATURE_TEMPLATES, "form,rare,infix,context,sig,bigrams");
		options.setProperty(MorphOptions.TRAIN_FILE,
				"form-index=1,tag-index=4,morph-index=6," + getResourceFile("trn.txt"));
		options.setProperty(MorphOptions.TEST_FILE,
				"form-index=1,tag-index=4,morph-index=6," + getResourceFile("tst.txt"));
		realTestWithOptions(options, 99, 51.50);
	}
	
	@Test
	public void realFloatTest() {
		MorphOptions options = new MorphOptions();
		options.setProperty(Options.SEED, "42");
		options.setProperty(Options.NUM_ITERATIONS, "10");
		options.setProperty(Options.VECTOR_SIZE, "10000000");
		options.setProperty(Options.CANDIDATES_PER_STATE, "[4, 2, 1.5, 1.25]");
		options.setProperty(Options.PRUNE, "true");
		options.setProperty(Options.ORDER, "3");
		options.setProperty(Options.PENALTY, ".1");
		options.setProperty(MorphOptions.FLOAT_TYPE_DICT, getResourceFile("svd_small.txt"));
		options.setProperty(MorphOptions.TRAIN_FILE,
				"form-index=1,tag-index=4,morph-index=6," + getResourceFile("trn.txt"));
		options.setProperty(MorphOptions.TEST_FILE,
				"form-index=1,tag-index=4,morph-index=6," + getResourceFile("tst.txt"));
		realTestWithOptions(options, 98.89, 54.52);
	}
	
	@Test
	public void realOptimizerTest() {
		MorphOptions options = new MorphOptions();
		options.setProperty(Options.SEED, "42");
		options.setProperty(Options.NUM_ITERATIONS, "10");
		options.setProperty(Options.VECTOR_SIZE, "10000000");
		options.setProperty(Options.CANDIDATES_PER_STATE, "[4, 2, 1.5, 1.25]");
		options.setProperty(Options.PRUNE, "true");
		options.setProperty(Options.OPTIMIZE_NUM_ITERATIONS, "true");
		options.setProperty(MorphOptions.TRAIN_FILE,
				"form-index=1,tag-index=4,morph-index=6," + getResourceFile("trn.txt"));
		options.setProperty(MorphOptions.TEST_FILE,
				"form-index=1,tag-index=4,morph-index=6," + getResourceFile("tst.txt"));
		// realOptimizerTestWithOptions(options, 97.96, 54.18);
	}
	
	@Test
	public void realNonHashTest() {
		MorphOptions options = new MorphOptions();
		options.setProperty(Options.SEED, "42");
		options.setProperty(Options.NUM_ITERATIONS, "10");
		options.setProperty(Options.VECTOR_SIZE, "1");
		options.setProperty(MorphOptions.USE_HASH_VECTOR, "false");
		options.setProperty(Options.CANDIDATES_PER_STATE, "[4, 2, 1.5, 1.25]");
		options.setProperty(Options.PRUNE, "true");
		options.setProperty(Options.ORDER, "3");
		options.setProperty(Options.PENALTY, "0.1");
		options.setProperty(MorphOptions.TRAIN_FILE,
				"form-index=1,tag-index=4,morph-index=6," + getResourceFile("trn.txt"));
		options.setProperty(MorphOptions.TEST_FILE,
				"form-index=1,tag-index=4,morph-index=6," + getResourceFile("tst.txt"));
		realTestWithOptions(options, 98.51, 54.10);
	}
	
	@Test
	public void realNormalizeFormTest() {
		MorphOptions options = new MorphOptions();
		options.setProperty(Options.SEED, "42");
		options.setProperty(Options.NUM_ITERATIONS, "10");
		options.setProperty(Options.VECTOR_SIZE, "10000000");
		options.setProperty(Options.CANDIDATES_PER_STATE, "[4, 2, 1.5, 1.25]");
		options.setProperty(Options.PRUNE, "true");
		options.setProperty(Options.ORDER, "3");
		options.setProperty(Options.PENALTY, ".1");
		options.setProperty(MorphOptions.FORM_NORMALIZATION, Mode.lower.toString());
		options.setProperty(MorphOptions.TRAIN_FILE,
				"form-index=1,tag-index=4,morph-index=6," + getResourceFile("trn.txt"));
		options.setProperty(MorphOptions.TEST_FILE,
				"form-index=1,tag-index=4,morph-index=6," + getResourceFile("tst.txt"));
		realTestWithOptions(options, 99.11, 55.38);
	}
	
	@Test
	public void realSpecialSignatureTest() {
		MorphOptions options = new MorphOptions();
		options.setProperty(Options.SEED, "42");
		options.setProperty(Options.NUM_ITERATIONS, "10");
		options.setProperty(Options.VECTOR_SIZE, "10000000");
		options.setProperty(Options.CANDIDATES_PER_STATE, "[4, 2, 1.5, 1.25]");
		options.setProperty(Options.PRUNE, "true");
		options.setProperty(Options.ORDER, "3");
		options.setProperty(Options.PENALTY, ".1");
		options.setProperty(MorphOptions.SPECIAL_SIGNATURE, "true");
		options.setProperty(MorphOptions.TRAIN_FILE,
				"form-index=1,tag-index=4,morph-index=6," + getResourceFile("trn.txt"));
		options.setProperty(MorphOptions.TEST_FILE,
				"form-index=1,tag-index=4,morph-index=6," + getResourceFile("tst.txt"));
		realTestWithOptions(options, 98.45, 53.96);
	}
	
	@Test
	public void realPosTest() {
		MorphOptions options = new MorphOptions();
		options.setProperty(Options.SEED, "42");
		options.setProperty(Options.NUM_ITERATIONS, "10");
		options.setProperty(Options.VECTOR_SIZE, "10000000");
		options.setProperty(Options.CANDIDATES_PER_STATE, "[4, 2, 1.5, 1.25]");
		options.setProperty(Options.PRUNE, "true");
		options.setProperty(Options.ORDER, "3");
		options.setProperty(Options.PENALTY, ".1");
		options.setProperty(MorphOptions.TRAIN_FILE,
				"form-index=1,tag-index=4," + getResourceFile("trn.txt"));
		options.setProperty(MorphOptions.TEST_FILE,
				"form-index=1,tag-index=4," + getResourceFile("tst.txt"));
		options.setProperty(MorphOptions.TAG_MORPH, "false");
		realTestWithOptions(options, 99.66, 79.14);
	}

	@Test
	public void realPerceptronPosTest() {
		MorphOptions options = new MorphOptions();
		options.setProperty(Options.SEED, "42");
		options.setProperty(Options.NUM_ITERATIONS, "10");
		options.setProperty(Options.VECTOR_SIZE, "10000000");
		options.setProperty(Options.PRUNE, "false");
		options.setProperty(Options.ORDER, "1");
		options.setProperty(Options.TRAINER,
				PerceptronTrainer.class.getCanonicalName());
		options.setProperty(MorphOptions.TAG_MORPH, "false");
		options.setProperty(MorphOptions.TRAIN_FILE,
				"form-index=1,tag-index=4,morph-index=6," + getResourceFile("trn.txt"));
		options.setProperty(MorphOptions.TEST_FILE,
				"form-index=1,tag-index=4,morph-index=6," + getResourceFile("tst.txt"));
		realTestWithOptions(options, 98.84, 77.49);
	}

	@Test
	public void realOracleTest() {
		MorphOptions options = new MorphOptions();
		options.setProperty(Options.SEED, "42");
		options.setProperty(Options.NUM_ITERATIONS, "10");
		options.setProperty(Options.VECTOR_SIZE, "10000000");
		options.setProperty(Options.CANDIDATES_PER_STATE, "[4, 2, 1.5, 1.25]");
		options.setProperty(Options.PRUNE, "true");
		options.setProperty(Options.ORDER, "3");
		options.setProperty(Options.PENALTY, ".1");
		options.setProperty(Options.ORACLE, "true");
		options.setProperty(MorphOptions.TRAIN_FILE,
				"form-index=1,tag-index=4,morph-index=6," + getResourceFile("trn.txt"));
		options.setProperty(MorphOptions.TEST_FILE,
				"form-index=1,tag-index=4,morph-index=6," + getResourceFile("tst.txt"));
		realTestWithOptions(options, 99.94, 53.39);
	}

	@Test
	public void realFstTest() {
		MorphOptions options = new MorphOptions();
		options.setProperty(Options.SEED, "42");
		options.setProperty(Options.NUM_ITERATIONS, "10");
		options.setProperty(Options.VECTOR_SIZE, "10000000");
		options.setProperty(Options.CANDIDATES_PER_STATE, "[4, 2, 1.5, 1.25]");
		options.setProperty(Options.PRUNE, "true");
		options.setProperty(Options.ORDER, "3");
		options.setProperty(Options.PENALTY, ".1");
		options.setProperty(MorphOptions.TRAIN_FILE,
				"form-index=1,tag-index=4,morph-index=6,token-feature-index=7," + getResourceFile("trn.fst.txt"));
		options.setProperty(MorphOptions.TEST_FILE,
				"form-index=1,tag-index=4,morph-index=6,token-feature-index=7," + getResourceFile("tst.fst.txt"));
		realTestWithOptions(options, 99.33, 70.10);
	}
	
	@Test
	public void realFstNoDefaultFeaturesTest() {
		MorphOptions options = new MorphOptions();
		options.setProperty(Options.SEED, "42");
		options.setProperty(Options.NUM_ITERATIONS, "10");
		options.setProperty(Options.VECTOR_SIZE, "10000000");
		options.setProperty(Options.CANDIDATES_PER_STATE, "[4, 2, 1.5, 1.25]");
		options.setProperty(Options.PRUNE, "true");
		options.setProperty(Options.ORDER, "3");
		options.setProperty(Options.PENALTY, ".1");
		options.setProperty(MorphOptions.TRAIN_FILE,
				"form-index=1,tag-index=4,morph-index=6,token-feature-index=7," + getResourceFile("trn.fst.txt"));
		options.setProperty(MorphOptions.TEST_FILE,
				"form-index=1,tag-index=4,morph-index=6,token-feature-index=7," + getResourceFile("tst.fst.txt"));
		options.setProperty(MorphOptions.USE_DEFAULT_FEATURES, "false");
		realTestWithOptions(options, 60.84, 50.13);
	}
	
	@Test
	public void realAramorphBaselineTest() {
		MorphOptions options = new MorphOptions();
		options.setProperty(Options.SEED, "42");
		options.setProperty(Options.NUM_ITERATIONS, "10");
		options.setProperty(Options.VECTOR_SIZE, "10000000");
		options.setProperty(Options.CANDIDATES_PER_STATE, "[4, 2, 1.5, 1.25]");
		options.setProperty(Options.PRUNE, "true");
		options.setProperty(Options.ORDER, "3");
		options.setProperty(Options.PENALTY, ".1");
		options.setProperty(MorphOptions.INTERNAL_ANALYZER, "ar");
		options.setProperty(MorphOptions.TRAIN_FILE,
				"form-index=1,tag-index=4,morph-index=6," + getResourceFile("trn.aramorph.txt"));
		options.setProperty(MorphOptions.TEST_FILE,
				"form-index=1,tag-index=4,morph-index=6," + getResourceFile("tst.aramorph.txt"));
		realTestWithOptions(options, 100.00, 66.26);
	}


	@Test
	public void realFstMaxLevelTest() {
		MorphOptions options = new MorphOptions();
		options.setProperty(Options.SEED, "42");
		options.setProperty(Options.NUM_ITERATIONS, "10");
		options.setProperty(Options.VECTOR_SIZE, "10000000");
		options.setProperty(Options.CANDIDATES_PER_STATE, "[4, 2, 1.5, 1.25]");
		options.setProperty(Options.PRUNE, "true");
		options.setProperty(Options.ORDER, "3");
		options.setProperty(Options.PENALTY, ".1");
		options.setProperty(Options.MAX_TRANSITION_FEATURE_LEVEL, "0");
		options.setProperty(MorphOptions.TRAIN_FILE,
				"form-index=1,tag-index=4,morph-index=6,token-feature-index=7," + getResourceFile("trn.fst.txt"));
		options.setProperty(MorphOptions.TEST_FILE,
				"form-index=1,tag-index=4,morph-index=6,token-feature-index=7," + getResourceFile("tst.fst.txt"));
		realTestWithOptions(options, 99.06, 69.46);
	}

	@Test
	public void realOracleFstTest() {
		MorphOptions options = new MorphOptions();
		options.setProperty(Options.SEED, "42");
		options.setProperty(Options.NUM_ITERATIONS, "10");
		options.setProperty(Options.VECTOR_SIZE, "10000000");
		options.setProperty(Options.CANDIDATES_PER_STATE, "[4, 2, 1.5, 1.25]");
		options.setProperty(Options.PRUNE, "true");
		options.setProperty(Options.ORDER, "3");
		options.setProperty(Options.PENALTY, ".1");
		options.setProperty(Options.ORACLE, "true");
		options.setProperty(MorphOptions.TRAIN_FILE,
				"form-index=1,tag-index=4,morph-index=6,token-feature-index=7," + getResourceFile("trn.fst.txt"));
		options.setProperty(MorphOptions.TEST_FILE,
				"form-index=1,tag-index=4,morph-index=6,token-feature-index=7," + getResourceFile("tst.fst.txt"));
		realTestWithOptions(options, 99.83, 70.94);
	}

	public void toyTestWithOptions(MorphOptions options) {
		options.setProperty(MorphOptions.SHAPE, "false");
		testWithOptions(options, getTrainSentences(), getTestSentences(), 100.0, 100.0, 0., 0.);
	}

	public void realOptimizerTestWithOptions(final MorphOptions options, double train_acc, double test_acc) {
		testOptimizerWithOptions(options, getSentences(options.getTrainFile(), 100),
				getSentences(options.getTestFile(), 100), train_acc, test_acc);
	}
	
	public static void realTestWithOptions(final MorphOptions options, double train_acc, double test_acc) {
		realTestWithOptions(options, train_acc, test_acc, 0.0, 0.0);
	}
	
	public static void realTestWithOptions(final MorphOptions options, double train_acc, double test_acc, double lemma_train_acc, double lemma_test_acc) {
		testWithOptions(options, getSentences(options.getTrainFile(), 100),
				getSentences(options.getTestFile(), 100), train_acc, test_acc, lemma_train_acc, lemma_test_acc);
	}

	public static void testWithOptions(MorphOptions options,
			List<Sequence> train_sentences, List<Sequence> test_sentences,
			double train_threshold, double test_threshold, double train_lemma_threshold, double test_lemma_threshold) {

		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		
		String caller = "None";
		if (stack.length > 3) {
			caller = stack[3].getMethodName();
		}
		
		Tagger tagger = MorphModel.train(options, train_sentences, null);
		
		assertModelPerformanceOnTestset(caller + " Train", tagger, train_sentences, train_threshold, train_lemma_threshold);
		assertModelPerformanceOnTestset(caller + " Test ", tagger, test_sentences, test_threshold, test_lemma_threshold);
		
		File tempfile;
		try {
			tempfile = File.createTempFile("tagger", ".marmot");
			tempfile.deleteOnExit();
			FileUtils.saveToFile(tagger, tempfile);
			Tagger loaded_tagger = FileUtils.loadFromFile(tempfile);
			assertModelPerformanceOnTestset(caller + " Test (reload) ", loaded_tagger, test_sentences, test_threshold);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void testOptimizerWithOptions(MorphOptions options,
			List<Sequence> train_sentences, List<Sequence> test_sentences,
			double train_threshold, double test_threshold) {

		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		
		String caller = "None";
		if (stack.length > 3) {
			caller = stack[3].getMethodName();
		}
		
		assert test_sentences != null;
		Tagger tagger = MorphModel.trainOptimal(options, train_sentences, null);

		assertModelPerformanceOnTestset(caller + " Train", tagger, train_sentences, train_threshold);
		assertModelPerformanceOnTestset(caller + " Test ", tagger, test_sentences, test_threshold);
		
		File tempfile;
		try {
			tempfile = File.createTempFile("tagger", ".marmot");
			tempfile.deleteOnExit();
			FileUtils.saveToFile(tagger, tempfile);
			Tagger loaded_tagger = FileUtils.loadFromFile(tempfile);
			assertModelPerformanceOnTestset(caller + " Test (reload) ", loaded_tagger, test_sentences, test_threshold);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void assertModelPerformanceOnTestset(String name, Tagger tagger,
			List<Sequence> sentences, double threshold) {
		assertModelPerformanceOnTestset(name, tagger, sentences, threshold, 0.0);
	}

	public static void assertModelPerformanceOnTestset(String name, Tagger tagger,
			List<Sequence> sentences, double threshold, double lemma_threshold) {
		MorphResult result = new MorphResult(tagger.getModel(), tagger.getNumLevels());
		MorphModel model = (MorphModel) tagger.getModel();

		for (Sequence sentence : sentences) {

			for (Token token : sentence) {
				Word word = (Word) token;
				model.addIndexes(word, false);
			}

			result.increment(MorphEvaluator.eval(tagger, (Sentence) sentence));
		}

		double accuracy = (result.num_tokens - result.morph_errors) * 100.
				/ result.num_tokens;
		
		double lemma_accuracy = (result.num_tokens - result.lemma_errors) * 100. / result.num_tokens;

		System.err.println(result.toString());

		if (accuracy - threshold < -1e-5) {
			throw new AssertionFailedError(accuracy + " < " + threshold);
		}
		
		if (lemma_accuracy - lemma_threshold < -1e-5) {
			throw new AssertionFailedError(accuracy + " < " + threshold);
		}
	}
}
