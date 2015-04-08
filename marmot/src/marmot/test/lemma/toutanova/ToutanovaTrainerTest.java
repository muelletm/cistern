package marmot.test.lemma.toutanova;

import java.util.List;

import marmot.lemma.Instance;
import marmot.lemma.Lemmatizer;
import marmot.lemma.LemmatizerTrainer;
import marmot.lemma.cmd.Trainer;
import marmot.lemma.toutanova.HackyAligner;
import marmot.lemma.toutanova.ToutanovaTrainer;
import marmot.lemma.toutanova.ZeroOrderDecoder;
import marmot.morph.io.SentenceReader;

import org.junit.Test;

public class ToutanovaTrainerTest extends SimpleTrainerTest {

	@Test
	public void copyTest() {
		LemmatizerTrainer trainer = new ToutanovaTrainer(ToutanovaTrainer.Options.newInstance());
		
		String indexes = "form-index=4,lemma-index=5,";
		String trainfile = indexes+ getResourceFile("trn_sml.tsv");
		String testfile = indexes + getResourceFile("dev_sml.tsv");
		
		List<Instance> training_instances = getCopyInstances(Trainer.getInstances(new SentenceReader(trainfile)));
		Lemmatizer lemmatizer = trainer.train(training_instances, null);
		
		List<Instance> instances = Trainer.getInstances(new SentenceReader(testfile));
		assertAccuracy(lemmatizer, getCopyInstances(instances), 99.1935);
	}

	@Test
	public void moderateZeroOrderAlignerPosTest() {	
		ToutanovaTrainer.Options options = ToutanovaTrainer.Options.newInstance();
		options.setNumIterations(10).setUsePos(true).setFilterAlphabet(5).setAligner(new HackyAligner()).setSeed(3).setDecoder(ZeroOrderDecoder.class).setUseContextFeature(true);
		//84.53 82.52
		//84.26 82.69 # fixed pair feature
		//89.12 87.48
		runModerateTest(new ToutanovaTrainer(options), 1., 1.);
	}
	
	@Test
	public void moderateAlignerPosTest() {	
		ToutanovaTrainer.Options options = ToutanovaTrainer.Options.newInstance();
		options.setNumIterations(10).setUsePos(true).setFilterAlphabet(5).setAligner(new HackyAligner()).setSeed(3).setUseContextFeature(false);
		// 88.17 86.09 42
		// 83.51 80.09 10
		// 91.18 89.38  3
		runModerateTest(new ToutanovaTrainer(options), 91.18, 89.38);
	}
	
	@Test
	public void moderateAveragingAlignerPosTest() {	
		ToutanovaTrainer.Options options = ToutanovaTrainer.Options.newInstance();
		options.setNumIterations(10).setUsePos(true).setFilterAlphabet(5).setAligner(new HackyAligner()).setSeed(10).setAveraging(true);
		runModerateTest(new ToutanovaTrainer(options), 91.03, 89.60);
		// 91.15 89.67  3
		// 91.03 89.60 10
	}
	
	@Test
	public void smallTest() {		
		ToutanovaTrainer.Options options = ToutanovaTrainer.Options.newInstance();
		options.setNumIterations(10);
		options.setFilterAlphabet(1);
		runSmallTest(new ToutanovaTrainer(options), 76.02, 68.31);
	}
	
	@Test
	public void smallPosTest() {	
		ToutanovaTrainer.Options options = ToutanovaTrainer.Options.newInstance();
		options.setNumIterations(10).setUsePos(true).setFilterAlphabet(1).setAveraging(true);
		// 89.33 81.19 
		// 88.70 81.30
		runSmallTest(new ToutanovaTrainer(options), 89.33, 81.19);
	}
		
}
