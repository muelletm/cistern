package marmot.test.lemma.toutanova;

import java.util.List;

import marmot.lemma.BackupLemmatizerTrainer;
import marmot.lemma.Instance;
import marmot.lemma.Lemmatizer;
import marmot.lemma.LemmatizerGeneratorTrainer;
import marmot.lemma.LemmatizerTrainer;
import marmot.lemma.Options;
import marmot.lemma.SimpleLemmatizerTrainer;
import marmot.lemma.SimpleLemmatizerTrainer.SimpleLemmatizerTrainerOptions;
import marmot.lemma.toutanova.EditTreeAlignerTrainer;
import marmot.lemma.toutanova.FirstOrderDecoder;
import marmot.lemma.toutanova.HackyAlignerTrainer;
import marmot.lemma.toutanova.SimpleAlignerTrainer;
import marmot.lemma.toutanova.ToutanovaTrainer;
import marmot.lemma.toutanova.ToutanovaTrainer.ToutanovaOptions;
import marmot.lemma.toutanova.ZeroOrderDecoder;
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

		List<Instance> training_instances = getCopyInstances(Instance.getInstances(new SentenceReader(trainfile)));
		Lemmatizer lemmatizer = trainer.train(training_instances, null);

		List<Instance> instances = Instance.getInstances(new SentenceReader(
				testfile));
		assertAccuracy(lemmatizer, getCopyInstances(instances), 99.1935);
	}

	@Test
	public void moderateZeroOrderAlignerPosTest() {

		LemmatizerGeneratorTrainer simple_trainer = new SimpleLemmatizerTrainer();
		simple_trainer.getOptions().setOption(SimpleLemmatizerTrainerOptions.HANDLE_UNSEEN, false);
		simple_trainer.getOptions().setOption(SimpleLemmatizerTrainerOptions.USE_BACKUP, false);
		simple_trainer.getOptions().setOption(Options.USE_POS, true);
		simple_trainer.getOptions().setOption(SimpleLemmatizerTrainerOptions.ABSTAIN_IF_AMBIGIOUS, true);
		
		ToutanovaTrainer trainer = new ToutanovaTrainer();
		trainer.getOptions().setOption(ToutanovaOptions.NUM_ITERATIONS, 10);
		trainer.getOptions().setOption(ToutanovaOptions.USE_POS, true);
		trainer.getOptions().setOption(ToutanovaOptions.FILTER_ALPHABET, 5);
		trainer.getOptions().setOption(ToutanovaOptions.AVERAGING, true);
		trainer.getOptions().setOption(ToutanovaOptions.DECODER, FirstOrderDecoder.class);
		trainer.getOptions().setOption(ToutanovaOptions.SEED, 10L);
		trainer.getOptions().setOption(ToutanovaOptions.ALIGNER_TRAINER, EditTreeAlignerTrainer.class);
		trainer.getOptions().setOption(ToutanovaOptions.MAX_COUNT, 1);
		trainer.getOptions().setOption(ToutanovaOptions.USE_CONSTEXT_FEATURE, true);
		
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
		
		LemmatizerTrainer btrainer = new BackupLemmatizerTrainer(simple_trainer, trainer);
		runModerateTest(btrainer, 1., 92.72);
	}

	@Test
	public void smallTest() {
		LemmatizerGeneratorTrainer simple_trainer = new SimpleLemmatizerTrainer();
		simple_trainer.getOptions().setOption(SimpleLemmatizerTrainerOptions.HANDLE_UNSEEN, false);
		simple_trainer.getOptions().setOption(SimpleLemmatizerTrainerOptions.USE_BACKUP, false);
		simple_trainer.getOptions().setOption(Options.USE_POS, true);
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
		simple_trainer.getOptions().setOption(Options.USE_POS, true);
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
