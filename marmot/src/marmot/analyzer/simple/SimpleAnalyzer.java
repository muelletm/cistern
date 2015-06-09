package marmot.analyzer.simple;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import marmot.analyzer.Analyzer;
import marmot.analyzer.AnalyzerInstance;
import marmot.analyzer.AnalyzerReading;
import marmot.analyzer.AnalyzerTag;

public class SimpleAnalyzer implements Analyzer {

	private SimpleAnalyzerModel model_; 
	
	public SimpleAnalyzer(SimpleAnalyzerModel model) {
		model_ = model;
	}

	@Override
	public Collection<AnalyzerReading> analyze(AnalyzerInstance instance) {
		SimpleAnalyzerInstance simple_instance = model_.getInstance(instance);
		
		double[] scores = new double[model_.getNumTags()];
		model_.score(simple_instance, scores);
		
		Collection<AnalyzerReading> readings = new LinkedList<>();
		for (Map.Entry<AnalyzerTag, Integer> entry : model_.getTagTable().entrySet()) {
			int tag_index = entry.getValue();
			double score = scores[tag_index];
			
			if (score > 0) {
				AnalyzerTag tag = entry.getKey();
				readings.add(new AnalyzerReading(tag, null));
			}
		}
		
		//Logger logger = Logger.getLogger(getClass().getName());
		//logger.info(String.format("%s : %s [%s]", instance.getForm(), readings, Arrays.toString(scores)));
		
		return readings;
	}

}
