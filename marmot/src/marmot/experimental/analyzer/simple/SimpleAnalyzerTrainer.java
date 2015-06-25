package marmot.experimental.analyzer.simple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import marmot.experimental.analyzer.Analyzer;
import marmot.experimental.analyzer.AnalyzerInstance;
import marmot.experimental.analyzer.AnalyzerReading;
import marmot.experimental.analyzer.AnalyzerTag;
import marmot.experimental.analyzer.AnalyzerTrainer;
import marmot.experimental.analyzer.simple.SimpleAnalyzer.Mode;
import marmot.morph.MorphDictionaryOptions;
import marmot.util.Counter;

import org.javatuples.Pair;

import cc.mallet.optimize.LimitedMemoryBFGS;
import cc.mallet.optimize.Optimizable.ByGradientValue;
import cc.mallet.optimize.OptimizationException;
import cc.mallet.optimize.Optimizer;

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
		boolean couple_tags = false;
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
		
		Collection<Pair<AnalyzerTag, AnalyzerTag>> coupled = null;
		if (couple_tags)
			coupled = getCoupledTags(instances);

		Collection<SimpleAnalyzerInstance> simple_instances = new LinkedList<>();
		for (AnalyzerInstance instance : instances) {
			Collection<AnalyzerTag> tags = AnalyzerReading.toTags(instance
					.getReadings());
			simple_instances.add(new SimpleAnalyzerInstance(instance, tags));
		}

		SimpleAnalyzerModel model = new SimpleAnalyzerModel();

		MorphDictionaryOptions options = null;
		if (options_.containsKey(AnalyzerTrainer.FLOAT_DICT_)) {
			options = MorphDictionaryOptions.parse(options_
					.get(AnalyzerTrainer.FLOAT_DICT_));
		}

		model.init(simple_instances, options);

		Logger logger = Logger.getLogger(getClass().getName());

		logger.info("Start optimization");
		ByGradientValue objective = new SimpleAnalyzerObjective(penalty_,
				model, simple_instances, train_mode_);
		Optimizer optimizer = new LimitedMemoryBFGS(objective);
		Logger.getLogger(optimizer.getClass().getName()).setLevel(Level.OFF);
		objective.setParameters(model.getWeights());

		// SimpleAnalyzer analyzer = new SimpleAnalyzer(model, 0.5);

		try {
			optimizer.optimize(1);

			// double memory_usage_during_optimization =
			// Sys.getUsedMemoryInMegaBytes();
			// logger.info(String.format("Memory usage after first iteration: %g / %g MB",
			// memory_usage_during_optimization,
			// Sys.getMaxHeapSizeInMegaBytes()));

			for (int i = 0; i < 200 && !optimizer.isConverged(); i++) {
				optimizer.optimize(1);
				// logger.info(String.format("Iteration: %3d / %3d: %g", i + 1,
				// 200, objective.getValue()));
				//
				// if (i % 10 == 0) {
				// logger.info("Train results:");
				// AnalyzerResult result = AnalyzerResult.test(analyzer,
				// instances);
				// result.logAcc();
				// result.logFscore();
				// }

			}

		} catch (IllegalArgumentException e) {
		} catch (OptimizationException e) {
		}

		SimpleThresholdOptimizer opt = new SimpleThresholdOptimizer(
				use_simple_optimizer);

		double best_threshold = opt.findTreshold(model, instances, tag_mode_);
		System.err.println("Best threshold on train: " + best_threshold);

		SimpleAnalyzer analyzer = new SimpleAnalyzer(model, best_threshold,
				tag_mode_, coupled);
		return analyzer;
	}

	private Collection<Pair<AnalyzerTag, AnalyzerTag>> getCoupledTags(
			Collection<AnalyzerInstance> instances) {
		
		Counter<AnalyzerTag> tag_counts = new Counter<>();
		Counter<Pair<AnalyzerTag, AnalyzerTag>> tag_tag_counts = new Counter<>();

		
		for (AnalyzerInstance instance : instances) {
			Collection<AnalyzerTag> tags = AnalyzerReading.toTags(instance
					.getReadings());
		
			for (AnalyzerTag tag : tags) {
				tag_counts.increment(tag, 1.0);
			}

			List<AnalyzerTag> tag_list = new ArrayList<>(tags);
			for (int i = 0; i < tag_list.size(); i++) {
				AnalyzerTag tag = tag_list.get(i);
				for (int j = i + 1; j < tag_list.size(); j++) {
					AnalyzerTag other_tag = tag_list.get(j);
					
					if (tag.hashCode() < other_tag.hashCode()) {
						tag_tag_counts.increment(new Pair<>(other_tag, tag), 1.0);
					} else {
						tag_tag_counts.increment(new Pair<>(tag, other_tag), 1.0);	
					}
				}
			}

		}
		
		Collection<Pair<AnalyzerTag, AnalyzerTag>> coupled = new LinkedList<>();
		for (Map.Entry<Pair<AnalyzerTag, AnalyzerTag>, Double> entry : tag_tag_counts
				.entrySet()) {
			Pair<AnalyzerTag, AnalyzerTag> pair = entry.getKey();

			double tag_count = tag_counts.count(pair.getValue0());
			assert tag_count < instances.size();

			double other_tag_count = tag_counts.count(pair.getValue1());
			assert other_tag_count < instances.size();

			double joint_count = entry.getValue();
			assert joint_count < instances.size();

			if (entry.getValue() >= 10) {

				double pseudo_pmi = joint_count
						/ Math.sqrt(tag_count * other_tag_count);

				if (pseudo_pmi > 0.99) {
					coupled.add(pair);
				}
			}
		}

		System.err.println("|Coupled|: " + coupled.size());
		System.err.println("Coupled: " + coupled);
		return coupled;
	}

}
