package marmot.analyzer.simple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marmot.analyzer.Analyzer;
import marmot.analyzer.AnalyzerInstance;
import marmot.analyzer.AnalyzerReading;
import marmot.analyzer.AnalyzerTag;
import marmot.util.Numerics;
import marmot.util.SymbolTable;

import org.javatuples.Pair;

public class SimpleAnalyzer implements Analyzer {

	private static final long serialVersionUID = 1L;
	public static enum Mode {binary, classifier};
	private SimpleAnalyzerModel model_;
	private double threshold_;
	private Mode mode_;
	private List<List<Integer>> pairs_; 
	
	public SimpleAnalyzer(SimpleAnalyzerModel model, double threshold, Mode mode, Collection<Pair<AnalyzerTag, AnalyzerTag>> coupled) {
		model_ = model;
		threshold_ = threshold;
		mode_ = mode;
		
		if (coupled != null) {
		
			SymbolTable<AnalyzerTag> table = model_.getTagTable();
		
			pairs_ = new ArrayList<>(table.size());
			for (int i=0;i<table.size();i++) {
				pairs_.add(Collections.<Integer> emptyList());
			}
		
			for (Pair<AnalyzerTag, AnalyzerTag> pair : coupled) {
				int index = table.toIndex(pair.getValue0());
				int other_index = table.toIndex(pair.getValue1());
				
				assert index != other_index;
				
				if (index > other_index) {
					int temp = other_index;
					other_index = index;
					index = temp;
				}
				
				List<Integer> list = pairs_.get(other_index);
				if (list.isEmpty()) {
					list = new LinkedList<>();
					pairs_.set(other_index, list);
				}
				
				list.add(index);
			}
			
		}
		
		
	}

	@Override
	public Collection<AnalyzerReading> analyze(AnalyzerInstance instance) {
		SimpleAnalyzerInstance simple_instance = model_.getInstance(instance);
		
		double[] scores = new double[model_.getNumTags()];
		model_.score(simple_instance, scores);
		
		Collection<AnalyzerReading> readings = new LinkedList<>();
		SymbolTable<AnalyzerTag> tag_table = model_.getTagTable();
		tag_table.setBidirectional(true);
		Collection<Map.Entry<AnalyzerTag, Integer>> tags = tag_table.entrySet();

		switch (mode_) {
		case binary:
			binaryScore(scores); 
			break;
		case classifier:
			classifierScore(scores); 
			break;
		default:
			throw new RuntimeException("Unknown mode: " + mode_);
		}
		
		
		boolean[] activated = new boolean[scores.length];
		
		for (int tag_index = 0; tag_index < activated.length; tag_index ++) {
			activated[tag_index] = scores[tag_index]> threshold_;
			
			if (pairs_ != null) {
				
				List<Integer> related_tags = pairs_.get(tag_index);
				
				for (int other_index : related_tags) {
					assert other_index < tag_index;
					
					if (activated[other_index] != activated[tag_index]) {
						double diff = Math.abs(threshold_ - scores[tag_index]);
						double other_diff = Math.abs(threshold_ - scores[other_index]);
						
						if (diff > other_diff) {
							activated[other_index] = activated[tag_index];
						} else {
							activated[tag_index] = activated[other_index];
						}
						
						System.err.format("%s: %s %s [%s]\n", instance.getForm(), tag_table.toSymbol(tag_index), tag_table.toSymbol(other_index), activated[tag_index]);
						
					}
				}
			}
			
		}
		
		AnalyzerTag max_tag = null;
		double max_prob = Double.NEGATIVE_INFINITY;		

		for (Map.Entry<AnalyzerTag, Integer> entry : tags) {
			int tag_index = entry.getValue();
			
			if (activated[tag_index]) {
				readings.add(new AnalyzerReading(entry.getKey(), null));
			}
			
			double prob = scores[tag_index];
			if (prob > max_prob) {
				max_prob = prob;
				max_tag = entry.getKey();
			}

			
		}
		
		if (readings.isEmpty()) {
			readings.add(new AnalyzerReading(max_tag, null));
		}

		return readings;
	}

	private void classifierScore(double[] scores) {
		
		double sum = Double.NEGATIVE_INFINITY;
		for (int tag_index=0; tag_index < scores.length; tag_index++) {
			
			sum = Numerics.sumLogProb(scores[tag_index], sum);
		}
		
		for (int tag_index=0; tag_index < scores.length; tag_index++) {
			double score = scores[tag_index];
			double prob = Math.exp(score - sum); 
			scores[tag_index] = prob;
		};
	}

	private void binaryScore(double[] scores) {
		for (int tag_index=0; tag_index < scores.length; tag_index++) {
			double score = scores[tag_index];
			double prob = Math.exp(score - Numerics.sumLogProb(score, 0)); 
			scores[tag_index] = prob;
		}
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
