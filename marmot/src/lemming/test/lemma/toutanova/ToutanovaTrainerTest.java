// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package lemming.test.lemma.toutanova;

import java.util.List;

import lemming.lemma.BackupLemmatizerTrainer;
import lemming.lemma.LemmaInstance;
import lemming.lemma.LemmaOptions;
import lemming.lemma.Lemmatizer;
import lemming.lemma.LemmatizerGeneratorTrainer;
import lemming.lemma.LemmatizerTrainer;
import lemming.lemma.SimpleLemmatizerTrainer;
import lemming.lemma.BackupLemmatizerTrainer.BackupLemmatizerTrainerOptions;
import lemming.lemma.SimpleLemmatizerTrainer.SimpleLemmatizerTrainerOptions;
import lemming.lemma.toutanova.EditTreeAlignerTrainer;
import lemming.lemma.toutanova.FirstOrderDecoder;
import lemming.lemma.toutanova.ToutanovaTrainer;
import lemming.lemma.toutanova.ToutanovaTrainer.ToutanovaOptions;
import marmot.morph.io.SentenceReader;

import org.junit.Test;

public class ToutanovaTrainerTest extends SimpleTrainerTest {

	@Test
	public void isSerializable() {
		
		testIfLemmatizerIsSerializable(new SimpleLemmatizerTrainer());
		testIfLemmatizerIsSerializable(new ToutanovaTrainer());
		
	}
	
	@Test
	public void copyTest() {
		LemmatizerTrainer trainer = new ToutanovaTrainer();

		String indexes = "form-index=4,lemma-index=5,";
		String trainfile = indexes + getResourceFile("trn_sml.tsv");
		String testfile = indexes + getResourceFile("dev_sml.tsv");

		List<LemmaInstance> training_instances = getCopyInstances(LemmaInstance.getInstances(new SentenceReader(trainfile)));
		Lemmatizer lemmatizer = trainer.train(training_instances, null);

		List<LemmaInstance> instances = LemmaInstance.getInstances(new SentenceReader(
				testfile));
		assertAccuracy(lemmatizer, getCopyInstances(instances), 99.1935);
	}

	@Test
	public void moderateZeroOrderAlignerPosTest() {

		BackupLemmatizerTrainer trainer = new BackupLemmatizerTrainer();
		LemmaOptions options = trainer.getOptions();
		
		options.setOption(BackupLemmatizerTrainerOptions.TRAINER_PREF + SimpleLemmatizerTrainerOptions.USE_BACKUP, false);
		options.setOption(BackupLemmatizerTrainerOptions.BACKUP_PREF + ToutanovaOptions.SEED, 10L);
		
		// 90.75 88.46 HA
		// 93.90 90.72 SA
		// 83.57 81.03 ETA

		//
		// 99.84 92.85 SA
		// 99.84 91.14 ETA seed=5, shuffle in builder
		// 99.89 90.23 ETA seed=4, shuffle in builder
		// 99.84 90.57 ETA seed=3, shuffle in builder
		
		// 99.84 90.43 25s
		// 99.84 90.41 21s
		
		// 92.73 101s
		// 94.54 313s
		// 94.93 814s
		
		runModerateTest(trainer, 1., 92.72);
	}

	@Test
	public void smallTest() {
		LemmatizerGeneratorTrainer simple_trainer = new SimpleLemmatizerTrainer();
		simple_trainer.getOptions().setOption(SimpleLemmatizerTrainerOptions.HANDLE_UNSEEN, false);
		simple_trainer.getOptions().setOption(SimpleLemmatizerTrainerOptions.USE_BACKUP, false);
		simple_trainer.getOptions().setOption(LemmaOptions.USE_POS, true);
		simple_trainer.getOptions().setOption(SimpleLemmatizerTrainerOptions.ABSTAIN_IF_AMBIGIOUS, true);

		ToutanovaTrainer trainer = new ToutanovaTrainer();
		trainer.getOptions().setOption(ToutanovaOptions.NUM_ITERATIONS, 10);
		trainer.getOptions().setOption(ToutanovaOptions.USE_POS, false);
		trainer.getOptions().setOption(ToutanovaOptions.FILTER_ALPHABET, 1);
		trainer.getOptions().setOption(ToutanovaOptions.AVERAGING, false);
		trainer.getOptions().setOption(ToutanovaOptions.DECODER, FirstOrderDecoder.class);
		
		LemmatizerTrainer btrainer = new BackupLemmatizerTrainer(simple_trainer, trainer);
		runSmallTest(btrainer, 1., 79.56);
	}

	@Test
	public void smallPosTest() {
		LemmatizerGeneratorTrainer simple_trainer = new SimpleLemmatizerTrainer();
		simple_trainer.getOptions().setOption(SimpleLemmatizerTrainerOptions.HANDLE_UNSEEN, false);
		simple_trainer.getOptions().setOption(SimpleLemmatizerTrainerOptions.USE_BACKUP, false);
		simple_trainer.getOptions().setOption(LemmaOptions.USE_POS, true);
		simple_trainer.getOptions().setOption(SimpleLemmatizerTrainerOptions.ABSTAIN_IF_AMBIGIOUS, true);
		
		ToutanovaTrainer trainer = new ToutanovaTrainer();
		trainer.getOptions().setOption(ToutanovaOptions.NUM_ITERATIONS, 10);
		trainer.getOptions().setOption(ToutanovaOptions.USE_POS, true);
		trainer.getOptions().setOption(ToutanovaOptions.FILTER_ALPHABET, 1);
		trainer.getOptions().setOption(ToutanovaOptions.AVERAGING, true);
		trainer.getOptions().setOption(ToutanovaOptions.DECODER, FirstOrderDecoder.class);
		
		LemmatizerTrainer btrainer = new BackupLemmatizerTrainer(simple_trainer, trainer);
		runSmallTest(btrainer, 1., 84.88);
	}

}
