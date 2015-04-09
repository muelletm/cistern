package marmot.lemma.cmd;

import java.util.List;

import marmot.lemma.Instance;
import marmot.lemma.Lemmatizer;
import marmot.lemma.LemmatizerTrainer;
import marmot.morph.io.SentenceReader;

public class Trainer {
	
	public static void main(String[] args) {
		String model_type = args[0];
		String train_file = args[1];
		String test_file = args[2];
		
		LemmatizerTrainer trainer;
		try {
			trainer = (LemmatizerTrainer) Class.forName(model_type).newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e); 
		}

		train(trainer, train_file, test_file);
	}
	
	public static Lemmatizer train(LemmatizerTrainer trainer, String train_file, String test_file) {
		List<Instance> training_instances = Instance.getInstances(new SentenceReader(train_file));
		List<Instance> test_instances = Instance.getInstances(new SentenceReader(test_file));
		Lemmatizer lemmatizer = trainer.train(training_instances, test_instances);
		Lemmatizer.Result.logTest(lemmatizer, test_file, 200);
		
		return lemmatizer;
	}

}
