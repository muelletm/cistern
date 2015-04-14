package marmot.test.lemma.reranker;

import java.util.Arrays;

import marmot.lemma.SimpleLemmatizerTrainer;
import marmot.lemma.edit.EditTreeGeneratorTrainer;
import marmot.lemma.reranker.RerankerTrainer;
import marmot.lemma.reranker.RerankerTrainer.RerankerTrainerOptions;
import marmot.lemma.toutanova.ToutanovaTrainer;
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
		
		((RerankerTrainerOptions) trainer.getOptions()).setOption(RerankerTrainerOptions.GENERATOR_TRAINERS, Arrays.asList(EditTreeGeneratorTrainer.class, SimpleLemmatizerTrainer.class, ToutanovaTrainer.class));
		
		trainer.getOptions().setOption(RerankerTrainerOptions.USE_PERCEPTRON, false).setOption(RerankerTrainerOptions.QUADRATIC_PENALTY, 0.005);
		runModerateTest(trainer, 1., 95.86);
		
		// 95.68
		// 95.75
		
		// 95.89
		
		// 99.89 95.74  0.0
		// 99.89 95.78  0.0001
		// 99.89 95.76  0.005
		// 99.89 95.79  0.001
		// 99.89 95.76  0.01
		         
		// 99.89 95.60  0.5
		// 99.89 95.53    1
		// 99.64 94.92   10
	}

}
