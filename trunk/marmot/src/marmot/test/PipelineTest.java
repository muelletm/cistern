// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import marmot.morph.io.FileOptions;
import marmot.morph.io.SentenceReader;
import marmot.util.FileUtils;
import marmot.util.StringUtils.Mode;

public class PipelineTest {

	public List<Sequence> getSentences(String filename, int number) {
		FileOptions options = new FileOptions(filename);
		InputStream input_stream = getClass().getResourceAsStream(
				options.getFilename());
		options.setInputStream(input_stream);
		List<Sequence> list = new LinkedList<Sequence>();
		for (Sequence sentence : new SentenceReader(options)) {
			list.add(sentence);
			if (list.size() >= number) {
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
		options.setProperty(MorphOptions.TRAIN_FILE,
				"form-index=1,tag-index=4,morph-index=6,trn.txt");
		options.setProperty(MorphOptions.TEST_FILE,
				"form-index=1,tag-index=4,morph-index=6,tst.txt");
		realTestWithOptions(options);
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
				"form-index=1,tag-index=4,morph-index=6,trn.txt");
		options.setProperty(MorphOptions.TEST_FILE,
				"form-index=1,tag-index=4,morph-index=6,tst.txt");
		realTestWithOptions(options);
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
				"form-index=1,tag-index=4,morph-index=6,trn.txt");
		options.setProperty(MorphOptions.TEST_FILE,
				"form-index=1,tag-index=4,morph-index=6,tst.txt");
		realTestWithOptions(options);
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
				"form-index=1,tag-index=4,trn.txt");
		options.setProperty(MorphOptions.TEST_FILE,
				"form-index=1,tag-index=4,tst.txt");
		options.setProperty(MorphOptions.TAG_MORPH, "false");
		realTestWithOptions(options);
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
				"form-index=1,tag-index=4,morph-index=6,trn.txt");
		options.setProperty(MorphOptions.TEST_FILE,
				"form-index=1,tag-index=4,morph-index=6,tst.txt");
		realTestWithOptions(options);
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
				"form-index=1,tag-index=4,morph-index=6,trn.txt");
		options.setProperty(MorphOptions.TEST_FILE,
				"form-index=1,tag-index=4,morph-index=6,tst.txt");
		realTestWithOptions(options);
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
				"form-index=1,tag-index=4,morph-index=6,token-feature-index=7,train_fst.txt");
		options.setProperty(MorphOptions.TEST_FILE,
				"form-index=1,tag-index=4,morph-index=6,token-feature-index=7,test_fst.txt");
		realTestWithOptions(options);
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
				"form-index=1,tag-index=4,morph-index=6,token-feature-index=7,train_fst.txt");
		options.setProperty(MorphOptions.TEST_FILE,
				"form-index=1,tag-index=4,morph-index=6,token-feature-index=7,test_fst.txt");
		realTestWithOptions(options);
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
				"form-index=1,tag-index=4,morph-index=6,token-feature-index=7,train_fst.txt");
		options.setProperty(MorphOptions.TEST_FILE,
				"form-index=1,tag-index=4,morph-index=6,token-feature-index=7,test_fst.txt");
		realTestWithOptions(options);
	}

	public void toyTestWithOptions(MorphOptions options) {
		options.setProperty(MorphOptions.SHAPE, "false");
		testWithOptions(options, getTrainSentences(), getTestSentences(), 100.0, 100.0);
	}

	public void realTestWithOptions(final MorphOptions options) {
		testWithOptions(options, getSentences(options.getTrainFile(), 100),
				getSentences(options.getTestFile(), 100), 90., 45.);
	}

	public void testWithOptions(MorphOptions options,
			List<Sequence> train_sentences, List<Sequence> test_sentences,
			double train_threshold, double test_threshold) {

		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		
		String caller = "None";
		if (stack.length > 3) {
			caller = stack[3].getMethodName();
		}
		
		Tagger tagger = MorphModel.train(options, train_sentences,
				test_sentences);

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

	private void assertModelPerformanceOnTestset(String name, Tagger tagger,
			List<Sequence> sentences, double threshold) {
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

		System.err.format("%s: %g\n",name, accuracy);

		if (accuracy - threshold < -1e-5) {
			throw new AssertionFailedError(accuracy + " < " + threshold);
		}
	}
}
