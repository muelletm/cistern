package lemming.lemma.cmd;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import lemming.lemma.LemmaCandidateGenerator;
import lemming.lemma.LemmaCandidateSet;
import lemming.lemma.LemmaInstance;
import lemming.lemma.edit.EditTreeGeneratorTrainer;
import lemming.lemma.edit.EditTreeGeneratorTrainer.EditTreeGeneratorTrainerOptions;
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
		
		String tag_independent = getStats(training_instances, dev_instances, false);
		String tag_dependent = getStats(training_instances, dev_instances, true);
		
		System.out.format("%s & %s \\\\\n", tag_independent, tag_dependent);
		
	}
	
	public static String getStats(List<LemmaInstance> training_instances,
			List<LemmaInstance> dev_instances, boolean tag_dependent) {
			
		EditTreeGeneratorTrainer trainer = new EditTreeGeneratorTrainer();
		trainer.getOptions().setOption(EditTreeGeneratorTrainerOptions.TAG_DEPENDENT, tag_dependent);
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
		
		return String.format("%s & %s\\%% & %s & %s\\%%", nice(num_token_candidates / num_tokens), nice(correct_tokens * 100. / num_tokens), 
				nice(num_type_candidates / num_types), nice(correct_types * 100. / num_types));
	}
	
	private static NumberFormat formatter = new DecimalFormat("#0.00");

	private static String nice(double number) {
		return formatter.format(number);
	}

}
