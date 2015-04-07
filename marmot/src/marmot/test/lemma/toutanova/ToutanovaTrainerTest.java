package marmot.test.lemma.toutanova;

import java.util.List;

import marmot.lemma.Instance;
import marmot.lemma.Lemmatizer;
import marmot.lemma.LemmatizerTrainer;
import marmot.lemma.cmd.Trainer;
import marmot.lemma.toutanova.ToutanovaTrainer;
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
	public void moderateTest() {		
		ToutanovaTrainer.Options options = ToutanovaTrainer.Options.newInstance();
		options.setNumIterations(10);
		options.setFilterAlphabet(1);
		runModerateTest(new ToutanovaTrainer(options), 1.00, 1.00);
	}
	
	@Test
	public void moderatePosTest() {	
		ToutanovaTrainer.Options options = ToutanovaTrainer.Options.newInstance();
		options.setNumIterations(10).setUsePos(true);
		runModerateTest(new ToutanovaTrainer(options), 1.00, 1.00);
	}
	
	
	@Test
	public void smallTest() {		
		ToutanovaTrainer.Options options = ToutanovaTrainer.Options.newInstance();
		options.setNumIterations(10);
		options.setFilterAlphabet(1);
		runSmallTest(new ToutanovaTrainer(options), 76.76, 70.44);
	}
	
	@Test
	public void smallPosTest() {	
		ToutanovaTrainer.Options options = ToutanovaTrainer.Options.newInstance();
		options.setNumIterations(10).setUsePos(true);
		runSmallTest(new ToutanovaTrainer(options), 89.96, 81.90);
	}
	
}
