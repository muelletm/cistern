package marmot.test.lemma.reranker;

import marmot.lemma.reranker.RerankerTrainer;
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
		runModerateTest(trainer, 1., 95.86);
	}

}
