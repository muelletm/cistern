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

public class SimpleAnalyzerTrainer implements AnalyzerTrainer {

	private MorphDictionaryOptions options_;

	public SimpleAnalyzerTrainer(MorphDictionaryOptions options) {
		options_ = options;
	}
	
	@Override
	public Analyzer train(Collection<AnalyzerInstance> instances) {
		
		Collection<SimpleAnalyzerInstance> simple_instances = new LinkedList<>(); 
		for (AnalyzerInstance instance : instances) {
			Collection<AnalyzerTag> tags = AnalyzerReading.toTags(instance.getReadings());
			simple_instances.add(new SimpleAnalyzerInstance(instance, tags));
		}
		
		SimpleAnalyzerModel model = new SimpleAnalyzerModel();
		
		model.init(simple_instances, options_);
		
		Logger logger =Logger.getLogger(getClass().getName());
		
		logger.info("Start optimization");
		ByGradientValue objective = new SimpleAnalyzerObjective(1.0, model, simple_instances);
		Optimizer optimizer = new LimitedMemoryBFGS(objective);
		Logger.getLogger(optimizer.getClass().getName()).setLevel(Level.OFF);
		objective.setParameters(model.getWeights());
		
		SimpleAnalyzer analyzer = new SimpleAnalyzer(model);
		
        try {
        	optimizer.optimize(1);
        	
        	double memory_usage_during_optimization = Sys.getUsedMemoryInMegaBytes();
        	logger.info(String.format("Memory usage after first iteration: %g / %g MB", memory_usage_during_optimization, Sys.getMaxHeapSizeInMegaBytes()));

        	for (int i=0; i < 200 && !optimizer.isConverged(); i++) {
                optimizer.optimize(1);
                logger.info(String.format("Iteration: %3d / %3d: %g", i + 1, 200, objective.getValue()));
                
                if (i % 10 == 0) {
                	logger.info("Train results:");
                	AnalyzerResult result = AnalyzerResult.test(analyzer, instances);
                	result.logAcc();
                	result.logFscore();
                }
                
        	}
        	

        } catch (IllegalArgumentException e) {
        } catch (OptimizationException e) {
        }
        
        logger.info("Finished optimization");
		
		return analyzer;
	}

}
