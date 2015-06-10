package marmot.analyzer.simple;

import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import cc.mallet.optimize.LimitedMemoryBFGS;
import cc.mallet.optimize.OptimizationException;
import cc.mallet.optimize.Optimizer;
import cc.mallet.optimize.Optimizable.ByGradientValue;

import marmot.analyzer.Analyzer;
import marmot.analyzer.AnalyzerInstance;
import marmot.analyzer.AnalyzerReading;
import marmot.analyzer.AnalyzerResult;
import marmot.analyzer.AnalyzerTag;
import marmot.analyzer.AnalyzerTrainer;
import marmot.morph.MorphDictionaryOptions;
import marmot.util.Sys;

public class SimpleAnalyzerTrainer extends AnalyzerTrainer {

	public SimpleAnalyzerTrainer() {
	}
	
	@Override
	public Analyzer train(Collection<AnalyzerInstance> instances) {
		
		Collection<SimpleAnalyzerInstance> simple_instances = new LinkedList<>(); 
		for (AnalyzerInstance instance : instances) {
			Collection<AnalyzerTag> tags = AnalyzerReading.toTags(instance.getReadings());
			simple_instances.add(new SimpleAnalyzerInstance(instance, tags));
		}
		
		SimpleAnalyzerModel model = new SimpleAnalyzerModel();
		
		MorphDictionaryOptions options = null;
		if (options_.containsKey(AnalyzerTrainer.FLOAT_DICT_)) {
			options = MorphDictionaryOptions.parse(options_.get(AnalyzerTrainer.FLOAT_DICT_));
		}
		
		model.init(simple_instances, options);
		
		Logger logger =Logger.getLogger(getClass().getName());
		
		logger.info("Start optimization");
		ByGradientValue objective = new SimpleAnalyzerObjective(2.0, model, simple_instances);
		Optimizer optimizer = new LimitedMemoryBFGS(objective);
		Logger.getLogger(optimizer.getClass().getName()).setLevel(Level.OFF);
		objective.setParameters(model.getWeights());
		
		//SimpleAnalyzer analyzer = new SimpleAnalyzer(model, 0.5);
		
        try {
        	optimizer.optimize(1);
        	
        	//double memory_usage_during_optimization = Sys.getUsedMemoryInMegaBytes();
        	//logger.info(String.format("Memory usage after first iteration: %g / %g MB", memory_usage_during_optimization, Sys.getMaxHeapSizeInMegaBytes()));

        	for (int i=0; i < 200 && !optimizer.isConverged(); i++) {
                optimizer.optimize(1);
//                logger.info(String.format("Iteration: %3d / %3d: %g", i + 1, 200, objective.getValue()));
//                
//                if (i % 10 == 0) {
//                	logger.info("Train results:");
//                	AnalyzerResult result = AnalyzerResult.test(analyzer, instances);
//                	result.logAcc();
//                	result.logFscore();
//                }
                
        	}
        	

        } catch (IllegalArgumentException e) {
        } catch (OptimizationException e) {
        }
        
        //logger.info("Finished optimization");
        
        double [] thresholds = {0.5, 0.45, 0.35, 0.25};
		double best_threshold = 0.0;
		double best_fscore = -1;
		for (double threshold : thresholds) {
			double fscore = getFscore(model, instances, threshold);
			if (fscore > best_fscore) {
				best_fscore = fscore;
				best_threshold = threshold;
			}		
		}
		System.err.println("Best threshold: " + best_threshold);
        
		SimpleAnalyzer analyzer = new SimpleAnalyzer(model, best_threshold);
		return analyzer;
	}

	private double getFscore(SimpleAnalyzerModel model,
			Collection<AnalyzerInstance> instances, double threshold) {
		SimpleAnalyzer analyzer = new SimpleAnalyzer(model, threshold);
		AnalyzerResult result = AnalyzerResult.test(analyzer, instances);
		double fscore = result.getFscore();
		return fscore;
	}

}
