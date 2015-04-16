package marmot.test.lemma.reranker;

import marmot.lemma.Options;
import marmot.lemma.reranker.RerankerTrainer;
import marmot.lemma.reranker.RerankerTrainer.RerankerTrainerOptions;
import marmot.test.lemma.toutanova.SimpleTrainerTest;

import org.junit.Test;

public class RerankerTrainerTest extends SimpleTrainerTest {

	@Test
	public void isSerializable() {
		testIfLemmatizerIsSerializable(new RerankerTrainer());
	}
	

	@Test
	public void smallTest() {
		RerankerTrainer trainer = new RerankerTrainer();
		
		//((RerankerTrainerOptions) trainer.getOptions()).setOption(RerankerTrainerOptions.GENERATOR_TRAINERS, Arrays.asList(EditTreeGeneratorTrainer.class, SimpleLemmatizerTrainer.class, ToutanovaTrainer.class));
		
		trainer.getOptions().setOption(RerankerTrainerOptions.USE_PERCEPTRON, false).setOption(RerankerTrainerOptions.QUADRATIC_PENALTY, 1.0);
		//trainer.getOptions().setOption(RerankerTrainerOptions.UNIGRAM_FILE, "min-count=5,/mounts/data/proj/marmot/lemmatizer/data/de/unigrams.txt");
		trainer.getOptions().setOption(Options.USE_MORPH, true);
		
		runModerateTest(trainer, 1., 1., true);
		
		// MORPH 88.09
		// POS   87.40
		
		// MORPH 88.06 78.32
		// POS   87.39 86.91
		
		// 97.11
	}

}
