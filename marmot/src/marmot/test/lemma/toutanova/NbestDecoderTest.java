package marmot.test.lemma.toutanova;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.logging.Logger;

import junit.framework.AssertionFailedError;
import marmot.lemma.Instance;
import marmot.lemma.toutanova.Decoder;
import marmot.lemma.toutanova.Model;
import marmot.lemma.toutanova.NbestDecoder;
import marmot.lemma.toutanova.Result;
import marmot.lemma.toutanova.ToutanovaInstance;
import marmot.lemma.toutanova.ToutanovaLemmatizer;
import marmot.lemma.toutanova.ToutanovaTrainer;
import marmot.lemma.toutanova.ZeroOrderDecoder;
import marmot.lemma.toutanova.ZeroOrderNbestDecoder;
import marmot.morph.io.SentenceReader;
import marmot.util.Numerics;

import org.junit.Test;

public class NbestDecoderTest {

	private static final double DELTA = 1e-2;

	public void trainDecodeTest(String trainfile, String devfile, int num_iters, int rank_max) {
		
		// Train a standard Toutanova model.
		List<Instance> train_instances = Instance.getInstances(new SentenceReader(trainfile));
		ToutanovaTrainer.Options options = ToutanovaTrainer.Options.newInstance();
		options.setDecoder(ZeroOrderDecoder.class).setNumIterations(num_iters).setFilterAlphabet(5).setUsePos(true).setUseContextFeature(true);
		ToutanovaTrainer trainer = new ToutanovaTrainer(options);
		ToutanovaLemmatizer lemmatizer = (ToutanovaLemmatizer) trainer.train(train_instances, null);

		testDecoder(lemmatizer, devfile, rank_max);
		testDecoder(lemmatizer, devfile, rank_max * 10);
		
		
	}
	
	private void testDecoder(ToutanovaLemmatizer lemmatizer, String devfile, int rank_max) {
		
		Model model = lemmatizer.getModel();
		
		Decoder decoder = new ZeroOrderDecoder();
		decoder.init(model);
		NbestDecoder nbest_decoder = new ZeroOrderNbestDecoder(rank_max);
		nbest_decoder.init(model);
		
		List<Instance> test_instances = Instance.getInstances(new SentenceReader(devfile));
		
		int correct = 0;
		int nbest_correct = 0;
		int total = 0;
		
		for (Instance instance : test_instances) {
			
			ToutanovaInstance tinstance = new ToutanovaInstance(instance, null);
			model.addIndexes(tinstance, false);
			
			Result result = decoder.decode(tinstance);
			
			double expected_score = model.getScore(tinstance, result);
			double first_best_score = result.getScore();
			
			assertEquals(expected_score, first_best_score, DELTA);
			
			List<Result> nbest_results = nbest_decoder.decode(tinstance);
			
			assertTrue(!nbest_results.isEmpty());
			
			Result first_nbest_result = nbest_results.get(0);
			
			assertEquals(result.getOutput(), first_nbest_result.getOutput());
			assertEquals(first_best_score, first_nbest_result.getScore(), DELTA);
			
			Result last_result = null;
			
			boolean found_lemma = false;
			
			for (Result nbest_result : nbest_results) {
				assertEquals(model.getScore(tinstance, nbest_result), nbest_result.getScore(), DELTA);
				if (last_result != null) {
					
					if (!Numerics.approximatelyLesserEqual(nbest_result.getScore(), last_result.getScore())) {
						throw new AssertionFailedError(String.format("%g <= %g", nbest_result.getScore(), last_result.getScore()));
					}
				}
				last_result = nbest_result;
				
				if (nbest_result.getOutput().equals(instance.getLemma())) {
					found_lemma = true;
				}
				
			}
			
			if (found_lemma) {
				nbest_correct += instance.getCount();
			}
			
			if (result.getOutput().equals(instance.getLemma())) {
				correct += instance.getCount();
			}
			
			total += instance.getCount();
		}
		
		Logger logger = Logger.getLogger(getClass().getName());
		
		logger.info(String.format("One-best : %5d %5d = %g", correct, total, correct * 100. / total));
		logger.info(String.format("N-best : %5d %5d = %g", nbest_correct, total, nbest_correct * 100. / total));
	}

	@Test
	public void test() {
		String indexes = "form-index=4,lemma-index=5,tag-index=2,";
		
		String train_sml = indexes + getResourceFile("trn_mod.tsv");
		String dev = indexes + getResourceFile("dev.tsv");
		
		trainDecodeTest(train_sml, train_sml, 1, 5);
		trainDecodeTest(train_sml, dev, 10, 10);
	}
	
	protected String getResourceFile(String name) {
		return String.format("res:///%s/%s", "marmot/test/lemma", name);
	}

}
