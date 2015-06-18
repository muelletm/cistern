package marmot.analyzer.simple;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import marmot.analyzer.Analyzer;
import marmot.analyzer.AnalyzerInstance;
import marmot.analyzer.AnalyzerReading;
import marmot.analyzer.AnalyzerTag;
import marmot.util.Numerics;

public class SimpleAnalyzer implements Analyzer {

	private static final long serialVersionUID = 1L;
	public static enum Mode {binary, classifier};
	private SimpleAnalyzerModel model_;
	private double threshold_;
	private Mode mode_; 
	
	public SimpleAnalyzer(SimpleAnalyzerModel model, double threshold, Mode mode) {
		model_ = model;
		threshold_ = threshold;
		mode_ = mode;
	}

	@Override
	public Collection<AnalyzerReading> analyze(AnalyzerInstance instance) {
		SimpleAnalyzerInstance simple_instance = model_.getInstance(instance);
		
		double[] scores = new double[model_.getNumTags()];
		model_.score(simple_instance, scores);
		
		AnalyzerTag max_tag = null;
		Collection<AnalyzerReading> readings = new LinkedList<>();
		Collection<Map.Entry<AnalyzerTag, Integer>> tags = model_.getTagTable().entrySet();
				
		switch (mode_) {
		case binary:
			max_tag = binaryAddTags(scores, tags, readings); 
			break;
		case classifier:
			max_tag = classifierAddTags(scores, tags, readings); 
			break;
		default:
			throw new RuntimeException("Unknown mode: " + mode_);
		}
		
		if (readings.isEmpty()) {
			readings.add(new AnalyzerReading(max_tag, null));
		}

		return readings;
	}

	private AnalyzerTag classifierAddTags(double[] scores,
			Collection<Entry<AnalyzerTag, Integer>> tags,
			Collection<AnalyzerReading> readings) {
		
		double max_prob = Double.NEGATIVE_INFINITY;
		AnalyzerTag max_tag = null;
		
		double sum = Double.NEGATIVE_INFINITY;
		for (Map.Entry<AnalyzerTag, Integer> entry : tags) {
			int tag_index = entry.getValue();
			sum = Numerics.sumLogProb(scores[tag_index], sum);
		}
		
		for (Map.Entry<AnalyzerTag, Integer> entry : tags) {
			int tag_index = entry.getValue();
			double score = scores[tag_index];
			double prob = Math.exp(score - sum); 
			
			AnalyzerTag tag = entry.getKey();
			
			if (prob > threshold_) {
				readings.add(new AnalyzerReading(tag, null));
			}
			
			if (prob > max_prob) {
				max_prob = prob;
				max_tag = tag;
			}
			
		}
		
		return max_tag;
	}

	private AnalyzerTag binaryAddTags(double[] scores,
			Collection<Entry<AnalyzerTag, Integer>> tags,
			Collection<AnalyzerReading> readings) {
		
		double max_prob = Double.NEGATIVE_INFINITY;
		AnalyzerTag max_tag = null;
		
		for (Map.Entry<AnalyzerTag, Integer> entry : tags) {
			int tag_index = entry.getValue();
			double score = scores[tag_index];
			double prob = Math.exp(score - Numerics.sumLogProb(score, 0)); 
			
			AnalyzerTag tag = entry.getKey();
			
			if (prob > threshold_) {
				readings.add(new AnalyzerReading(tag, null));
			}
			
			if (prob > max_prob) {
				max_prob = prob;
				max_tag = tag;
			}
			
		}
		
		return max_tag;
	}

	@Override
	public String represent(AnalyzerInstance instance) {
		SimpleAnalyzerInstance simple_instance = model_.getInstance(instance);
		return Arrays.toString(simple_instance.getFeatIndexes());
	}

	@Override
	public int getNumTags() {
		return model_.getNumTags();
	}

}
