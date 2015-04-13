package marmot.test.lemma.reranker;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import marmot.lemma.BackupLemmatizerTrainer;
import marmot.lemma.Instance;
import marmot.lemma.LemmaCandidateGeneratorTrainer;
import marmot.lemma.Lemmatizer;
import marmot.lemma.LemmatizerGeneratorTrainer;
import marmot.lemma.LemmatizerTrainer;
import marmot.lemma.SimpleLemmatizerTrainer;
import marmot.lemma.edit.EditTreeGeneratorTrainer;
import marmot.lemma.reranker.RerankerTrainer;
import marmot.lemma.toutanova.EditTreeAlignerTrainer;
import marmot.lemma.toutanova.HackyAlignerTrainer;
import marmot.lemma.toutanova.ToutanovaTrainer;
import marmot.lemma.toutanova.ZeroOrderDecoder;
import marmot.morph.io.SentenceReader;
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
		generator_trainers.add(new EditTreeGeneratorTrainer(1));
		generator_trainers.add(new SimpleLemmatizerTrainer());
		
		runSmallTest(new SimpleLemmatizerTrainer(), 1., 1.);
		
		RerankerTrainer trainer = new RerankerTrainer(generator_trainers);
		runModerateTest(trainer, 1., 1.);
	}

}
