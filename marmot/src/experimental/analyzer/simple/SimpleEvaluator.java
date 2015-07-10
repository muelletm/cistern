package experimental.analyzer.simple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import experimental.analyzer.AnalyzerInstance;
import experimental.analyzer.simple.SimpleAnalyzer.Mode;

import marmot.util.Numerics;

public class SimpleEvaluator {

	private static class Entry implements Comparable<Entry>{
		double prob;
		boolean active;
		int num_tags;
		
		@Override
		public int compareTo(Entry o) {
			return -Double.compare(prob, o.prob);
		}
	}

	public void eval(SimpleAnalyzer analyzer,
			Collection<AnalyzerInstance> in_instances, List<Double> ambiguities) {
		
		Collection<AnalyzerInstance> instances = new LinkedList<>();
		for (AnalyzerInstance instance : instances) {
			if (analyzer.isUnknown(instance)) {
				instances.add(instance);
			}
		}
		
		SimpleAnalyzerModel model = analyzer.getModel();
		Mode mode = analyzer.getMode();
		
		int num_tags = model.getNumTags();
		
		List<Entry> entries = new LinkedList<>();

		for (AnalyzerInstance instance : instances) {
			SimpleAnalyzerInstance simple_instance = model
					.getInstance(instance);
			
			double[] scores = new double[num_tags];
			model.score(simple_instance, scores);
			double sum = Double.NEGATIVE_INFINITY;

			if (mode == Mode.classifier) {
				for (double score : scores) {
					sum = Numerics.sumLogProb(score, sum);
				}
			}

			int num_readings = simple_instance.getTagIndexes().size();
			
			List<Entry> current_entries = new ArrayList<>(num_tags);
			for (int tag_index = 0; tag_index < num_tags; tag_index++) {
				Entry entry = new Entry();
				entry.active = false;

				
				if (mode == Mode.classifier)
					entry.prob = Math.exp(scores[tag_index] - sum);
				else
					entry.prob = Math.exp(scores[tag_index] - Numerics.sumLogProb(scores[tag_index], 0));

				assert entry.prob >= 0.0 && entry.prob < 1.0;
				
				entry.num_tags = num_readings;
				
				current_entries.add(entry);
			}
			
			for (int tag_index : simple_instance.getTagIndexes()) {
				current_entries.get(tag_index).active = true;
			}
			
			
			
			entries.addAll(current_entries);
		}
		
		Collections.sort(entries);
		
		double current_coverage = 0.0;
		double current_ambiguity = 0.0;
		
		for (Entry entry : entries) {
			if (entry.active) {
				current_coverage += 1. / (entry.num_tags * instances.size());
			}
			
			double prev_ambiguity = current_ambiguity;
			current_ambiguity += 1. / (instances.size());
			
			for (double ambiguity_value : ambiguities) {
				
				if (prev_ambiguity <= ambiguity_value && current_ambiguity >= ambiguity_value) {
					System.err.format("Amb: %g Cov: %g (Th: %g)\n", current_ambiguity, current_coverage, entry.prob);
				}
				
			}
			
		}
	}

}
