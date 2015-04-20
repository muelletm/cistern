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
		options.setProperty(Options.PENALTY, "0.0");
		
		options.setProperty(MorphOptions.TAG_MORPH, "true");
		options.setProperty(MorphOptions.LEMMATIZE, "true");
		options.setProperty(Options.NUM_ITERATIONS, "10");
		
		options.setProperty(MorphOptions.TRAIN_FILE,
				"form-index=1,lemma-index=2,tag-index=4,morph-index=6,res:///marmot/test/morph/trn.txt");
		options.setProperty(MorphOptions.TEST_FILE,
				"form-index=1,lemma-index=2,tag-index=4,morph-index=6,res:///marmot/test/morph/tst.txt");
		
		List<Sequence> train_sentences = getSentences(options.getTrainFile(), 100);
		List<Sequence> test_sentences = getSentences(options.getTestFile(), -1);
		
		PipelineTest.testWithOptions(options, train_sentences, test_sentences, 99.56, 53.13, 100., 87.66);
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
