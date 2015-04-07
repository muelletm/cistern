package marmot.lemma.cmd;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marmot.core.Sequence;
import marmot.core.Token;
import marmot.lemma.Instance;
import marmot.lemma.Lemmatizer;
import marmot.lemma.LemmatizerTrainer;
import marmot.morph.Word;
import marmot.morph.io.SentenceReader;
import marmot.util.MutableInteger;

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
	
	public static List<Instance> getInstances(SentenceReader reader) {
		return getInstances(reader, -1);
	}
	
	public static List<Instance> getInstances(SentenceReader reader, int limit) {
		 
		Map<Instance, MutableInteger> map = new HashMap<>();
		
		int number = 0;
		for (Sequence sentence : reader) {
			for (Token token : sentence) {
				
				number ++;
				
				Word word = (Word) token;
				String form = word.getWordForm();
				String lemma = word.getLemma();			
				Instance instance = new Instance(form, lemma, word.getPosTag(), word.getMorphTag());
				
				MutableInteger mi = map.get(instance);
				if (mi == null) {
					mi = new MutableInteger();
					map.put(instance, mi);
				}
				
				mi.add(1);
			}
			
			if (limit >= 0 && number > limit)
				break;
			
		}
		
		List<Instance> instances = new LinkedList<Instance>();
		for (Map.Entry<Instance, MutableInteger> entry : map.entrySet()) {

			Instance instance = entry.getKey();
			double count = entry.getValue().get();
			
			instance.setCount(count);
			instances.add(instance);
		}

		return instances;
	}
	
	
	public static Lemmatizer train(LemmatizerTrainer trainer, String train_file, String test_file) {
		
		List<Instance> training_instances = getInstances(new SentenceReader(train_file));
		List<Instance> test_instances = getInstances(new SentenceReader(test_file));
		
		Lemmatizer lemmatizer = trainer.train(training_instances, test_instances);
		
		
		return lemmatizer;
	}

}
