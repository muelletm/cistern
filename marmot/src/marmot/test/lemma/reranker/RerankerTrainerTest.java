package marmot.test.lemma.reranker;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import marmot.lemma.LemmaCandidateGeneratorTrainer;
import marmot.lemma.SimpleLemmatizerTrainer;
import marmot.lemma.edit.EditTreeGeneratorTrainer;
import marmot.lemma.reranker.RerankerTrainer;
import marmot.lemma.toutanova.ToutanovaTrainer;
import marmot.test.lemma.toutanova.SimpleTrainerTest;

import org.junit.Test;

public class RerankerTrainerTest extends SimpleTrainerTest {

	@Test
	public void isSerializable() {
		testIfLemmatizerIsSerializable(new RerankerTrainer(Arrays.asList(new SimpleLemmatizerTrainer())));
	}
	

	@Test
	public void smallTest() {
		List<LemmaCandidateGeneratorTrainer> generator_trainers = new LinkedList<>();
		generator_trainers.add(new EditTreeGeneratorTrainer(new Random(42), 1));
		generator_trainers.add(new SimpleLemmatizerTrainer());
		generator_trainers.add(new ToutanovaTrainer(ToutanovaTrainer.Options.newZeroOrderInstance()));
		
		RerankerTrainer trainer = new RerankerTrainer(generator_trainers);
		runModerateTest(trainer, 1., 1.);
		//runModerateTest(trainer, 99.89, 95.02);
	}

}
