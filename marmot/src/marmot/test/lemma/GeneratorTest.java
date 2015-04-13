package marmot.test.lemma;

import java.util.Random;

import marmot.lemma.BackupLemmatizerTrainer;
import marmot.lemma.Instance;
import marmot.lemma.LemmaCandidateGenerator;
import marmot.lemma.LemmaCandidateGeneratorTrainer;
import marmot.lemma.LemmatizerGeneratorTrainer;
import marmot.lemma.Result;
import marmot.lemma.SimpleLemmatizerTrainer;
import marmot.lemma.edit.EditTreeGeneratorTrainer;
import marmot.lemma.toutanova.ToutanovaTrainer;
import marmot.lemma.toutanova.ToutanovaTrainer.Options;

import org.junit.Test;

public class GeneratorTest {

	@Test
	public void testGeneratorTrainer() {
		String train = "trn_mod.tsv";
		String dev = "dev.tsv";
		
		LemmaCandidateGeneratorTrainer trainer = new EditTreeGeneratorTrainer(new Random(42), 2);
		testGeneratorTrainer(trainer, train, dev);
		
		trainer = new SimpleLemmatizerTrainer();
		testGeneratorTrainer(trainer, train, dev);
		
		trainer = new BackupLemmatizerTrainer((LemmatizerGeneratorTrainer) trainer, new ToutanovaTrainer(Options.newZeroOrderInstance()));
		testGeneratorTrainer(trainer, train, dev);
	}
	
	public void testGeneratorTrainer(LemmaCandidateGeneratorTrainer trainer, String trainfile, String testfile) {
		LemmaCandidateGenerator generator = trainer.train(Instance.getInstances(getResourceFile(trainfile)), null);
		testGenerator(generator, testfile);
	}

	private void testGenerator(LemmaCandidateGenerator generator, String testfile) {
		Result result = Result.testGenerator(generator, getResourceFile(testfile));
		result.logAccuracy();
		result = Result.testGenerator(generator, getResourceFile(testfile + ".morfette"));
		result.logAccuracy();
	}

	protected String getResourceFile(String name) {
		return INDEXES + String.format("res:///%s/%s", "marmot/test/lemma", name);
	}
	
	private final static String INDEXES = "form-index=4,lemma-index=5,tag-index=2,";
	
}
