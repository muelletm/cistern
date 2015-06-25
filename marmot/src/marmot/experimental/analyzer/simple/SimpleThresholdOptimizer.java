package marmot.experimental.analyzer.simple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import marmot.experimental.analyzer.AnalyzerInstance;
import marmot.experimental.analyzer.AnalyzerResult;
import marmot.experimental.analyzer.simple.SimpleAnalyzer.Mode;
import marmot.util.Numerics;

public class SimpleThresholdOptimizer {

	boolean simple_;

	private static class Entry implements Comparable<Entry>{
		double prob;
		boolean active;
		
		@Override
		public int compareTo(Entry o) {
			return Double.compare(prob, o.prob);
		}
	}

	public SimpleThresholdOptimizer(boolean simple) {
		simple_ = simple;
	}

	public double findTreshold(SimpleAnalyzerModel model,
			Collection<AnalyzerInstance> instances, Mode mode) {
		if (simple_) {
			return simpleFindTreshold(model, instances, mode);
		}

		int num_tags = model.getNumTags();
		int num_actives = 0;
		
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

			List<Entry> current_entries = new ArrayList<>(num_tags);
			for (int tag_index = 0; tag_index < num_tags; tag_index++) {
				Entry entry = new Entry();
				entry.active = false;

				
				if (mode == Mode.classifier)
					entry.prob = Math.exp(scores[tag_index] - sum);
				else
					entry.prob = Math.exp(scores[tag_index] - Numerics.sumLogProb(scores[tag_index], 0));

				assert entry.prob >= 0.0 && entry.prob < 1.0;
				current_entries.add(entry);
			}
			
			for (int tag_index : simple_instance.getTagIndexes()) {
				current_entries.get(tag_index).active = true;
				num_actives ++;
			}
			
			entries.addAll(current_entries);
		}
		
		Collections.sort(entries);
		
		// Correct at threshold = 0.0;	
		int best_correct = 0;
		double best_threshold = 0.0;
		int correct = num_actives;
		
		for (Entry entry : entries) {
			if (entry.active) {
				correct --;
			} else {
				correct ++;
			}
			
			if (correct > best_correct) {
				best_correct = correct;
				best_threshold = entry.prob + 1e-10;
			}
			
		}
		
		System.err.println("Correct: " + best_correct);
		return best_threshold;
	}

	private double simpleFindTreshold(SimpleAnalyzerModel model,
			Collection<AnalyzerInstance> instances, Mode mode) {
		double[] thresholds = { 0.5, 0.35, 0.3, 0.25, 0.20, 0.15, 0.1, 0.05 };
		System.err.println("Thresholds: " + Arrays.toString(thresholds));
		double best_threshold = 0.0;
		double best_fscore = -1;
		for (double threshold : thresholds) {
			double fscore = getFscore(model, instances, threshold, mode);
			if (fscore > best_fscore) {
				best_fscore = fscore;
				best_threshold = threshold;
			}
			System.err.format("Threshold: %g F1-Score on train: %g\n",
					threshold, fscore);
		}
		return best_threshold;
	}

	private double getFscore(SimpleAnalyzerModel model,
			Collection<AnalyzerInstance> instances, double threshold,
			Mode tag_mode) {
		SimpleAnalyzer analyzer = new SimpleAnalyzer(model, threshold, tag_mode, null);
		AnalyzerResult result = AnalyzerResult.test(analyzer, instances);
		double fscore = result.getFscore();
		return fscore;
	}

}
