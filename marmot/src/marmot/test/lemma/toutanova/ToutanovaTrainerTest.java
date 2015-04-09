package marmot.test.lemma.toutanova;

import java.util.List;

import marmot.lemma.BackupLemmatizerTrainer;
import marmot.lemma.Instance;
import marmot.lemma.Lemmatizer;
import marmot.lemma.LemmatizerTrainer;
import marmot.lemma.SimpleLemmatizerTrainer;
import marmot.lemma.toutanova.EditTreeAlignerTrainer;
import marmot.lemma.toutanova.HackyAlignerTrainer;
import marmot.lemma.toutanova.ToutanovaTrainer;
import marmot.lemma.toutanova.ZeroOrderDecoder;
import marmot.morph.io.SentenceReader;

import org.junit.Test;

public class ToutanovaTrainerTest extends SimpleTrainerTest {

	@Test
	public void copyTest() {
		LemmatizerTrainer trainer = new ToutanovaTrainer(
				ToutanovaTrainer.Options.newInstance());

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

		SimpleLemmatizerTrainer.Options soptions = SimpleLemmatizerTrainer.Options
				.newInstance();
		soptions.setHandleUnseen(false).setUseBackup(false).setUsePos(true)
				.setAbstainIfAmbigous(true);
		LemmatizerTrainer simple_trainer = new SimpleLemmatizerTrainer(soptions);

		ToutanovaTrainer.Options options = ToutanovaTrainer.Options
				.newInstance();
		options.setNumIterations(10)
				.setUsePos(true)
				.setFilterAlphabet(5)
				.setSeed(3)
				.setAlignerTrainer(
						new EditTreeAlignerTrainer(options.getSeed()))
				.setDecoder(ZeroOrderDecoder.class).setUseContextFeature(true).setMaxCount(1);

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
		
		LemmatizerTrainer btrainer = new BackupLemmatizerTrainer(
				simple_trainer, new ToutanovaTrainer(options));
		runModerateTest(btrainer, 1., 1.);
	}

	@Test
	public void moderateAlignerPosTest() {
		ToutanovaTrainer.Options options = ToutanovaTrainer.Options
				.newInstance();
		options.setNumIterations(10).setUsePos(true).setFilterAlphabet(5)
				.setAlignerTrainer(new HackyAlignerTrainer()).setSeed(3)
				.setUseContextFeature(false);
		// 88.17 86.09 42
		// 83.51 80.09 10
		// 91.18 89.38 3
		runModerateTest(new ToutanovaTrainer(options), 91.18, 89.38);
	}

	@Test
	public void moderateAveragingAlignerPosTest() {
		ToutanovaTrainer.Options options = ToutanovaTrainer.Options
				.newInstance();
		options.setNumIterations(10).setUsePos(true).setFilterAlphabet(5)
				.setAlignerTrainer(new HackyAlignerTrainer()).setSeed(10)
				.setAveraging(true);
		runModerateTest(new ToutanovaTrainer(options), 91.03, 89.60);
		// 91.15 89.67 3
		// 91.03 89.60 10
	}

	@Test
	public void smallTest() {
		ToutanovaTrainer.Options options = ToutanovaTrainer.Options
				.newInstance();
		options.setNumIterations(10);
		options.setFilterAlphabet(1);
		runSmallTest(new ToutanovaTrainer(options), 76.02, 68.31);
	}

	@Test
	public void smallPosTest() {
		ToutanovaTrainer.Options options = ToutanovaTrainer.Options
				.newInstance();
		options.setNumIterations(10).setUsePos(true).setFilterAlphabet(1)
				.setAveraging(true);
		// .setAlignerTrainer(new EditTreeAlignerTrainer());
		// 89.33 80.79 SA
		// 88.48 81.80 ETA
		runSmallTest(new ToutanovaTrainer(options), 1., 1.);
	}

}
