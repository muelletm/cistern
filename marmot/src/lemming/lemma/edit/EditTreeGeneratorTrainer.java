// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package lemming.lemma.edit;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lemming.lemma.LemmaCandidateGenerator;
import lemming.lemma.LemmaCandidateGeneratorTrainer;
import lemming.lemma.LemmaInstance;
import lemming.lemma.LemmaOptions;
import marmot.util.Counter;
import marmot.util.edit.EditTree;
import marmot.util.edit.EditTreeBuilder;
import marmot.util.edit.EditTreeBuilderTrainer;

public class EditTreeGeneratorTrainer implements LemmaCandidateGeneratorTrainer {

	private EditTreeGeneratorTrainerOptions options_;

	public static class EditTreeGeneratorTrainerOptions extends LemmaOptions {

		private static final long serialVersionUID = 1L;
		public static final String MIN_COUNT = "min-count";
		public static final String NUM_STEPS = "num-steps";
		public static final String TAG_DEPENDENT = "tag-dependent";
		public static final String UNKNOWN = "unknown";
		public static final String MAX_DEPTH = "max-depth";

		public EditTreeGeneratorTrainerOptions() {
			map_.put(MIN_COUNT, 1);
			map_.put(NUM_STEPS, 1);
			map_.put(TAG_DEPENDENT, false);
			map_.put(UNKNOWN, "<UNKNOWN>");
			map_.put(MAX_DEPTH, -1);
		}

		public int getNumSteps() {
			return (Integer) getOption(NUM_STEPS);
		}

		public boolean getIsTagDependent() {
			return (Boolean) getOption(TAG_DEPENDENT);
		}

		public String getUnknown() {
			return (String) getOption(UNKNOWN);
		}

		public Integer getMinCount() {
			return (Integer) getOption(MIN_COUNT);
		}

		public int getMaxDepth() {
			return (Integer) getOption(MAX_DEPTH);
		}

	}

	public EditTreeGeneratorTrainer() {
		options_ = new EditTreeGeneratorTrainerOptions();
	}

	@Override
	public LemmaCandidateGenerator train(List<LemmaInstance> instances,
			List<LemmaInstance> dev_instances) {

		EditTreeBuilder builder = new EditTreeBuilderTrainer(
				options_.getRandom(), options_.getNumSteps(), options_.getMaxDepth()).train(instances);

		Map<String, Counter<EditTree>> map = new HashMap<String, Counter<EditTree>>();
		map.put(options_.getUnknown(), new Counter<EditTree>());

		for (LemmaInstance instance : instances) {
			String form = instance.getForm();
			String lemma = instance.getLemma();
			EditTree tree = builder.build(form, lemma);

			Counter<EditTree> counter = map.get(options_.getUnknown());
			counter.increment(tree, 1.0);

			if (options_.getIsTagDependent()) {
				String tag = instance.getPosTag();
				if (tag != null) {
					counter = map.get(tag);
					if (counter == null) {
						counter = new Counter<>();
						map.put(tag, counter);
					}
					counter.increment(tree, 1.0);
				}
			}
		}

		Map<String, List<EditTree>> list_map = new HashMap<>();
		for (Map.Entry<String, Counter<EditTree>> map_entry : map.entrySet()) {
			List<EditTree> list = new LinkedList<>();
			Counter<EditTree> counter = map_entry.getValue();
			list_map.put(map_entry.getKey(), list);
			for (Map.Entry<EditTree, Double> entry : counter.entrySet()) {
				double count = entry.getValue();
				if (count >= options_.getMinCount()) {
					EditTree tree = entry.getKey();
					list.add(tree);
				}
			}
		}

		return new EditTreeGenerator(options_.getUnknown(), list_map);
	}

	@Override
	public LemmaOptions getOptions() {
		return options_;
	}

}
