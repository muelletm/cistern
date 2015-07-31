package lemming.lemma.cmd;

import java.util.List;

import lemming.lemma.LemmaCandidateGenerator;
import lemming.lemma.LemmaCandidateSet;
import lemming.lemma.LemmaInstance;
import lemming.lemma.edit.EditTreeGeneratorTrainer;
import marmot.morph.io.SentenceReader;

public class Stats {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String train_file = args[0];
		String dev_file = args[1];
		
		List<LemmaInstance> training_instances = LemmaInstance.getInstances(new SentenceReader(train_file), -1);
		List<LemmaInstance> dev_instances = LemmaInstance.getInstances(new SentenceReader(dev_file), -1);
		
		EditTreeGeneratorTrainer trainer = new EditTreeGeneratorTrainer();
		LemmaCandidateGenerator generator = trainer.train(training_instances, null);
		
		double num_token_candidates = 0;
		double num_type_candidates = 0;
		double num_tokens = 0;
		double num_types = 0;
		double correct_tokens = 0;
		double correct_types = 0;
				
		for (LemmaInstance instance : dev_instances) {
			
			LemmaCandidateSet set = new LemmaCandidateSet();
			generator.addCandidates(instance, set);
			
			if (set.contains(instance.getLemma())) {
				correct_tokens += instance.getCount();
				correct_types += 1;
			}
			
			num_token_candidates += set.size() * instance.getCount();
			num_type_candidates += set.size();
			
			num_tokens += instance.getCount();
			num_types+= 1.0;
		}
		
		System.out.format("%g & %g & %g & %g \\\\", num_token_candidates / num_tokens, correct_tokens / num_tokens, 
				num_type_candidates / num_types, correct_types / num_types);
	}

}
