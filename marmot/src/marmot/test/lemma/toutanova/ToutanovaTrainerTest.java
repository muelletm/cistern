package marmot.test.lemma.toutanova;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import marmot.lemma.Instance;
import marmot.lemma.Lemmatizer;
import marmot.lemma.LemmatizerTrainer;
import marmot.lemma.cmd.Trainer;
import marmot.lemma.toutanova.ToutanovaTrainer;
import marmot.morph.io.SentenceReader;

import org.junit.Test;

public class ToutanovaTrainerTest {

	private List<Instance> getCopyInstances(List<Instance> instances) {
		List<Instance> new_instances = new LinkedList<>();
		for (Instance instance : instances) {
			if (instance.getForm().equals(instance.getLemma())) {
					new_instances.add(instance);
			}
		}
		return new_instances;
	}
	
	private String getResourceFile(String name) {
		return String.format("res:///%s/%s", "marmot/test/lemma", name);
	}
	
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
	public void normalPosTest() {	
		ToutanovaTrainer.Options options = ToutanovaTrainer.Options.newInstance()
				.setNumIterations(10)
				.setUsePos(true);
		
		LemmatizerTrainer trainer = new ToutanovaTrainer(options);
		
		String indexes = "form-index=4,lemma-index=5,tag-index=2,";
		String trainfile = indexes+ getResourceFile("trn_sml.tsv");
		String testfile = indexes + getResourceFile("dev_sml.tsv");
		
		List<Instance> training_instances = Trainer.getInstances(new SentenceReader(trainfile));
		Lemmatizer lemmatizer = trainer.train(training_instances, null);
		
		List<Instance> instances = Trainer.getInstances(new SentenceReader(testfile));
		assertAccuracy(lemmatizer, instances, 80.62);
	}
	
	
	@Test
	public void normalTest() {
		
		ToutanovaTrainer.Options options = ToutanovaTrainer.Options.newInstance().setNumIterations(10);
		
		LemmatizerTrainer trainer = new ToutanovaTrainer(options);
		
		String indexes = "form-index=4,lemma-index=5,";
		String trainfile = indexes+ getResourceFile("trn_sml.tsv");
		String testfile = indexes + getResourceFile("dev_sml.tsv");
		
		List<Instance> training_instances = Trainer.getInstances(new SentenceReader(trainfile));
		Lemmatizer lemmatizer = trainer.train(training_instances, null);
		
		List<Instance> instances = Trainer.getInstances(new SentenceReader(testfile));
		assertAccuracy(lemmatizer, instances, 72.0556);
	}

	private void assertAccuracy(Lemmatizer lemmatizer, Collection<Instance> instances, double min_accuracy) {
		int correct = 0;
		int total = 0;
		
		for (Instance instance : instances) {
			String form = lemmatizer.lemmatize(instance);
			
			if (form.equals(instance.getLemma())) {
				correct ++;
			}
			total ++;
		}
		
		double accuracy = correct * 100. / total;
		
		System.err.println(accuracy);
		assertTrue(accuracy + 1e-5 > min_accuracy);
	}

}
