package marmot.experimental.analyzer.tagger;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import marmot.core.Options;
import marmot.core.Sequence;
import marmot.experimental.analyzer.Analyzer;
import marmot.experimental.analyzer.AnalyzerInstance;
import marmot.experimental.analyzer.AnalyzerReading;
import marmot.experimental.analyzer.AnalyzerResult;
import marmot.experimental.analyzer.AnalyzerTag;
import marmot.experimental.analyzer.AnalyzerTrainer;
import marmot.morph.MorphModel;
import marmot.morph.MorphOptions;
import marmot.morph.MorphTagger;
import marmot.morph.Sentence;
import marmot.morph.Word;

public class TaggerAnalyzerTrainer extends AnalyzerTrainer {

	public double getFscore(MorphTagger tagger, Collection<AnalyzerInstance> instances, double threshold) {
		TaggerAnalyzer analyzer = new TaggerAnalyzer(tagger, threshold);
		AnalyzerResult result = AnalyzerResult.test(analyzer, instances);
		double fscore = result.getFscore();
		return fscore;
	}
	
	@Override
	public Analyzer train(Collection<AnalyzerInstance> instances) {
		Collection<Sequence> sentences = new LinkedList<>();
		for (AnalyzerInstance instance : instances) {
			for (AnalyzerReading reading : instance.getReadings()) {
				AnalyzerTag tag = reading.getTag();
				Word word = new Word(instance.getForm(), tag.getPosTag(), tag.getMorphTag());
				Sentence sentence = new Sentence(Collections.singletonList(word));
				sentences.add(sentence);
			}
		}
		
		MorphOptions options = new MorphOptions();
		options.setProperty(Options.ORDER, "0");
		options.setProperty(MorphOptions.FEATURE_TEMPLATES, "affix,sig");
		options.setProperty(MorphOptions.OBSERVED_FEATURE, "false");
		options.setProperty(Options.PENALTY, "0.0");
		if (options_.containsKey(AnalyzerTrainer.FLOAT_DICT_)) {
			options.setProperty(MorphOptions.FLOAT_TYPE_DICT, options_.get(AnalyzerTrainer.FLOAT_DICT_));
		}
				
		MorphTagger tagger = (MorphTagger) MorphModel.train(options, sentences, null);
		
		double [] thresholds = {0.5, 0.45, 0.4, 0.35, 0.3, 0.25, 0.2, 0.15, 0.1, 0.05, 0.01};
		double best_threshold = 0.0;
		double best_fscore = -1;
		for (double threshold : thresholds) {
			double fscore = getFscore(tagger, instances, threshold);
			if (fscore > best_fscore) {
				best_fscore = fscore;
				best_threshold = threshold;
			}		
		}
		System.err.println("Best threshold: " + best_threshold);
		return new TaggerAnalyzer(tagger, best_threshold);
	}

}
