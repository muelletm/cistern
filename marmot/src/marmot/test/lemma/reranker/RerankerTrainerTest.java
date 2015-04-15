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
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		
		System.err.println("Let's go!");
		
		RerankerTrainer trainer = new RerankerTrainer();
		
		//((RerankerTrainerOptions) trainer.getOptions()).setOption(RerankerTrainerOptions.GENERATOR_TRAINERS, Arrays.asList(EditTreeGeneratorTrainer.class, SimpleLemmatizerTrainer.class, ToutanovaTrainer.class));
		
		trainer.getOptions().setOption(RerankerTrainerOptions.USE_PERCEPTRON, false).setOption(RerankerTrainerOptions.QUADRATIC_PENALTY, 1.0);
		trainer.getOptions().setOption(RerankerTrainerOptions.UNIGRAM_FILE, "min-count=5,/mounts/data/proj/marmot/lemmatizer/data/de/unigrams.txt");
		trainer.getOptions().setOption(Options.USE_MORPH, true);
		
		runSmallTest(trainer, 1., 1., true);
				
		// POS
		// CG    19.00s 88.59
		// CG    22.00s 89.53
		
		// MORPH
		// CG    69.00s 89.18
		
		// POS
		// LBFGS 22.81s 88.89 
		// LBFGS XX.XXs 89.10
		
		// MORPH 
		// LBFGS 53.14s 89.43 c2=0.005
		// LBFGS 52.27s 89.62 c2=0.100
        // LBFGS 60.24s 89.55 c2=1.000
		
		// POS
		// LBFGS 67.52s 96.51	95.59 (8)
		// CG    63.33s 96.22   95.30 (4)
	}

}
