// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.test.lemma;

import marmot.lemma.BackupLemmatizerTrainer;
import marmot.lemma.BackupLemmatizerTrainer.BackupLemmatizerTrainerOptions;
import marmot.lemma.Instance;
import marmot.lemma.LemmaCandidateGenerator;
import marmot.lemma.LemmaCandidateGeneratorTrainer;
import marmot.lemma.Result;
import marmot.lemma.SimpleLemmatizerTrainer;
import marmot.lemma.edit.EditTreeGeneratorTrainer;
import marmot.lemma.toutanova.ToutanovaTrainer;

import org.junit.Test;

public class GeneratorTest {

	@Test
	public void testGeneratorTrainer() {
		String train = "trn_mod.tsv";
		String dev = "dev.tsv";
		
		LemmaCandidateGeneratorTrainer trainer = new EditTreeGeneratorTrainer();
		testGeneratorTrainer(trainer, train, dev);
		
		trainer = new SimpleLemmatizerTrainer();
		testGeneratorTrainer(trainer, train, dev);
		
		trainer = new BackupLemmatizerTrainer();
		
		BackupLemmatizerTrainerOptions options = (BackupLemmatizerTrainerOptions) trainer.getOptions();
		
		options.setOption(BackupLemmatizerTrainerOptions.LEMMATIZER_TRAINER, SimpleLemmatizerTrainer.class.getName());
		options.setOption(BackupLemmatizerTrainerOptions.BACKUP_TRAINER, ToutanovaTrainer.class.getName());
		
		testGeneratorTrainer(trainer, train, dev);
	}
	
	public void testGeneratorTrainer(LemmaCandidateGeneratorTrainer trainer, String trainfile, String testfile) {
		LemmaCandidateGenerator generator = trainer.train(Instance.getInstances(getResourceFile(trainfile)), null);
		testGenerator(generator, testfile);
	}

	private void testGenerator(LemmaCandidateGenerator generator, String testfile) {
//		Result result = Result.testGenerator(generator, getResourceFile(testfile));
//		result.logAccuracy();
//		result = Result.testGenerator(generator, getResourceFile(testfile + ".morfette"));
//		result.logAccuracy();
	}

	protected String getResourceFile(String name) {
		return INDEXES + String.format("res:///%s/%s", "marmot/test/lemma", name);
	}
	
	private final static String INDEXES = "form-index=4,lemma-index=5,tag-index=2,";
	
}
