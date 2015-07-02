package experimental.analyzer.simple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import marmot.morph.MorphDictionaryOptions;
import marmot.util.Counter;
import marmot.util.Mutable;

import org.javatuples.Pair;

import cc.mallet.optimize.LimitedMemoryBFGS;
import cc.mallet.optimize.Optimizable.ByGradientValue;
import cc.mallet.optimize.OptimizationException;
import cc.mallet.optimize.Optimizer;
import experimental.analyzer.Analyzer;
import experimental.analyzer.AnalyzerInstance;
import experimental.analyzer.AnalyzerReading;
import experimental.analyzer.AnalyzerTag;
import experimental.analyzer.AnalyzerTrainer;
import experimental.analyzer.simple.SimpleAnalyzer.Mode;

public class SimpleAnalyzerTrainer extends AnalyzerTrainer {

	private Mode train_mode_;
	private Mode tag_mode_;
	private double penalty_;
	public final String MODE = "mode";
	private final String PENALTY = "penalty";
	private boolean optimize_threshold_ = false;
	private boolean mallet_ = false;
	private boolean pair_constraints_ = false;

	@Override
	public Analyzer train(Collection<AnalyzerInstance> instances) {
		System.err.format("Num instances: %d\n", instances.size());

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

		if (pair_constraints_) {
			preparePairConstraints(instances);
		}

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

		if (mallet_) {
			run_mallet(model, simple_instances);
		} else {
			run_sgd(model, simple_instances, 10, true, 0.1);
		}

		double best_threshold = 0.01;
		if (optimize_threshold_) {

			SimpleThresholdOptimizer opt = new SimpleThresholdOptimizer(
					use_simple_optimizer);

			best_threshold = opt.findTreshold(model, instances, tag_mode_);
			System.err.println("Best threshold on train: " + best_threshold);

		}

		SimpleAnalyzer analyzer = new SimpleAnalyzer(model, best_threshold,
				tag_mode_, coupled);
		return analyzer;
	}

	Map<AnalyzerTag, Map<AnalyzerTag, Mutable<Double>>> relative_counts_ = null;
	
	private void preparePairConstraints(Collection<AnalyzerInstance> instances) {

		TagStats stats = getTagStates(instances);

		relative_counts_ = new HashMap<>();

		for (Map.Entry<Pair<AnalyzerTag, AnalyzerTag>, Double> entry : stats.tag_tag_counts
				.entrySet()) {

			Pair<AnalyzerTag, AnalyzerTag> pair = entry.getKey();
			Double count = entry.getValue();

			addRelativeProb(pair.getValue0(), pair.getValue1(), count,
					stats.tag_counts.count(pair.getValue0()), relative_counts_);
			addRelativeProb(pair.getValue1(), pair.getValue0(), count,
					stats.tag_counts.count(pair.getValue1()), relative_counts_);

		}

		for (Map.Entry<AnalyzerTag, Map<AnalyzerTag, Mutable<Double>>> entry : relative_counts_
				.entrySet()) {
			AnalyzerTag tag = entry.getKey();
			Map<AnalyzerTag, Mutable<Double>> map = entry.getValue();
			
			map.put(tag, new Mutable<Double>(1.0));
			
			double sum = 0.0;
			for (Mutable<Double> count : map.values()) {
				sum += count.get();
			}
			
			for (Mutable<Double> count : map.values()) {
				count.set(count.get() / sum);
			} 

			System.err.println(tag + " " + map);
		}

	}

	private void addRelativeProb(AnalyzerTag tag, AnalyzerTag other_tag,
			Double tag_tag_count, Double tag_count,
			Map<AnalyzerTag, Map<AnalyzerTag, Mutable<Double>>> relative_counts) {

		double prob = tag_tag_count / tag_count;

		if (prob > 0.1) {
			Map<AnalyzerTag, Mutable<Double>> map = relative_counts.get(tag);
			if (map == null) {
				map = new HashMap<>();
				relative_counts.put(tag, map);
			}

			assert !map.containsKey(other_tag);
			map.put(other_tag, new Mutable<Double>(prob));
		}
	}

	private void run_sgd(SimpleAnalyzerModel model,
			Collection<SimpleAnalyzerInstance> simple_instances, int steps_,
			boolean verbose_, double step_width_) {
		List<SimpleAnalyzerInstance> instances = new LinkedList<>(
				simple_instances);
		SimpleAnalyzerObjective objective = new SimpleAnalyzerObjective(
				penalty_, model, simple_instances, train_mode_, relative_counts_);
		int number = 0;
		for (int step = 0; step < steps_; step++) {
			if (verbose_)
				System.err.println("step: " + step);
			Collections.shuffle(instances, new Random(42));
			for (SimpleAnalyzerInstance instance : instances) {
				double step_width = step_width_
						/ (1 + (number / (double) instances.size()));
				objective.update(instance, step_width, true);
				number++;
			}
		}
	}

	private void run_mallet(SimpleAnalyzerModel model,
			Collection<SimpleAnalyzerInstance> simple_instances) {
		Logger logger = Logger.getLogger(getClass().getName());
		logger.info("Start optimization");
		ByGradientValue objective = new SimpleAnalyzerObjective(penalty_,
				model, simple_instances, train_mode_, relative_counts_);
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
				logger.info(String.format("Iteration: %3d / %3d: %g", i + 1,
						200, objective.getValue()));

			}

		} catch (IllegalArgumentException e) {
		} catch (OptimizationException e) {
		}

	}

	private static class TagStats {

		Counter<AnalyzerTag> tag_counts = new Counter<>();
		Counter<Pair<AnalyzerTag, AnalyzerTag>> tag_tag_counts = new Counter<>();

		public TagStats() {
			tag_counts = new Counter<>();
			tag_tag_counts = new Counter<>();
		}
	}

	private TagStats getTagStates(Collection<AnalyzerInstance> instances) {
		TagStats stats = new TagStats();

		for (AnalyzerInstance instance : instances) {
			Collection<AnalyzerTag> tags = AnalyzerReading.toTags(instance
					.getReadings());

			for (AnalyzerTag tag : tags) {
				stats.tag_counts.increment(tag, 1.0);
			}
			
			List<AnalyzerTag> tag_list = new ArrayList<>(tags);
			for (int i = 0; i < tag_list.size(); i++) {
				AnalyzerTag tag = tag_list.get(i);
				for (int j = i + 1; j < tag_list.size(); j++) {
					AnalyzerTag other_tag = tag_list.get(j);

					
					
					if (tag.hashCode() < other_tag.hashCode()) {
						stats.tag_tag_counts.increment(new Pair<>(other_tag,
								tag), 1.0);
					} else {
						stats.tag_tag_counts.increment(new Pair<>(tag,
								other_tag), 1.0);
					}
				}
			}

		}

		return stats;
	}

	private Collection<Pair<AnalyzerTag, AnalyzerTag>> getCoupledTags(
			Collection<AnalyzerInstance> instances) {

		TagStats stats = getTagStates(instances);

		Collection<Pair<AnalyzerTag, AnalyzerTag>> coupled = new LinkedList<>();
		for (Map.Entry<Pair<AnalyzerTag, AnalyzerTag>, Double> entry : stats.tag_tag_counts
				.entrySet()) {
			Pair<AnalyzerTag, AnalyzerTag> pair = entry.getKey();

			double tag_count = stats.tag_counts.count(pair.getValue0());
			assert tag_count < instances.size();

			double other_tag_count = stats.tag_counts.count(pair.getValue1());
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
