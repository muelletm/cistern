// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.lemma;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import marmot.util.Counter;

public class SimpleLemmatizerTrainer implements LemmatizerGeneratorTrainer {

	public static class SimpleLemmatizerTrainerOptions extends Options {
		
		private static final long serialVersionUID = 1L;
		public static final String HANDLE_UNSEEN = "handle-unseen";
		public static final String USE_BACKUP = "use-backup";
		public static final String ABSTAIN_IF_AMBIGIOUS = "abstain-if-ambigious";
		
		private SimpleLemmatizerTrainerOptions() {
			super();
			map_.put(HANDLE_UNSEEN, false);
			map_.put(USE_BACKUP, true);
			map_.put(ABSTAIN_IF_AMBIGIOUS, false);
		}
		
		public static SimpleLemmatizerTrainerOptions newInstance() {
			return new SimpleLemmatizerTrainerOptions();
		}

		public boolean getHandleUnseen() {
			return (Boolean) getOption(HANDLE_UNSEEN);
		}

		public boolean getUseBackup() {
			return (Boolean) getOption(USE_BACKUP);
		}

		public boolean getAbstainIfAmbigous() {
			return (Boolean) getOption(ABSTAIN_IF_AMBIGIOUS);
		}
	
	}

	private SimpleLemmatizerTrainerOptions options_;

	public SimpleLemmatizerTrainer() {
		options_ = new SimpleLemmatizerTrainerOptions();
	}
	
	@Override
	public LemmatizerGenerator train(List<Instance> instances,
			List<Instance> dev_instances) {
		
		Map<String, Counter<String>> map = new HashMap<>();
		
		for (Instance instance : instances) {
			String key = null;
			
			if (options_.getUsePos()) {
				key = SimpleLemmatizer.toKey(instance);
				addToMap(key, map, instance);
			}
			
			if (options_.getUseBackup()) {
				key = SimpleLemmatizer.toSimpleKey(instance);
				addToMap(key, map, instance);
			}
		}
		
		return new SimpleLemmatizer(options_, map);
	}

	private void addToMap(String key, Map<String, Counter<String>> map,
			Instance instance) {
		Counter<String> lemmas = map.get(key);
		
		if (lemmas == null) {
			lemmas = new Counter<>();
			map.put(key, lemmas);
		}
		
		lemmas.increment(instance.getLemma(), instance.getCount());
	}

	@Override
	public Options getOptions() {
		return options_;
	}

}
