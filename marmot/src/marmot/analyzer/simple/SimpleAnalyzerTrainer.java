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
import marmot.analyzer.AnalyzerTag;
import marmot.analyzer.AnalyzerTrainer;
import marmot.analyzer.simple.SimpleAnalyzer.Mode;
import marmot.morph.MorphDictionaryOptions;

public class SimpleAnalyzerTrainer extends AnalyzerTrainer {

	private Mode train_mode_;
	private Mode tag_mode_;
	private double penalty_;
	public final String MODE = "mode";
	private final String PENALTY = "penalty";

	public SimpleAnalyzerTrainer() {
		
	}
	
	@Override
	public Analyzer train(Collection<AnalyzerInstance> instances) {
		boolean use_simple_optimizer = false;
		tag_mode_ = Mode.binary;
		train_mode_ = Mode.binary;
		if (options_.containsKey(MODE)) {
			Mode mode = Mode.valueOf(options_.get(MODE));
			tag_mode_ = mode;
			train_mode_ = mode;
		}
		System.err.format("Modes: %s / %s\n", tag_mode_, train_mode_);
		
		penalty_ = 1.0;	
		if (options_.containsKey(PENALTY)) {
			penalty_ = Double.valueOf(options_.get(PENALTY));
		}
		System.err.format("Penalty: %g\n", penalty_);
				
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
		ByGradientValue objective = new SimpleAnalyzerObjective(penalty_ , model, simple_instances, train_mode_);
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
        
        SimpleThresholdOptimizer opt = new SimpleThresholdOptimizer(use_simple_optimizer);
        
        double best_threshold = opt.findTreshold(model, instances, tag_mode_);
		System.err.println("Best threshold on train: " + best_threshold);
        
		SimpleAnalyzer analyzer = new SimpleAnalyzer(model, best_threshold, tag_mode_);
		return analyzer;
	}


}
