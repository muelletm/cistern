package marmot.lemma;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SimpleLemmatizerTrainer implements LemmatizerGeneratorTrainer {

	public static class Options implements Serializable {
		
		private boolean handle_unseen_;
		private boolean use_pos_;
		private boolean use_backup_;
		private boolean abstain_if_ambigious_;

		private Options() {
			handle_unseen_ = false;
			use_pos_ = false;
			use_backup_ = true;
			abstain_if_ambigious_ = false;
		}
		
		public static Options newInstance() {
			return new Options();
		}

		public boolean getUsePos() {
			return use_pos_;
		}

		public boolean getHandleUnseen() {
			return handle_unseen_;
		}

		public Options setHandleUnseen(boolean b) {
			handle_unseen_ = b;
			return this;
		}

		public Options setUsePos(boolean b) {
			use_pos_ = b;
			return this;
		}
		
		public Options setUseBackup(boolean b) {
			use_backup_ = b;
			return this;
		}
		
		public boolean getUseBackup() {
			return use_backup_;
		}

		public boolean getAbstainIfAmbigous() {
			return abstain_if_ambigious_;
		}

		public Options setAbstainIfAmbigous(boolean b) {
			abstain_if_ambigious_ = b;
			return this;
		}
		
	}

	private Options options_;

	public SimpleLemmatizerTrainer() {
		this(Options.newInstance());
	}
	
	public SimpleLemmatizerTrainer(Options options) {
		options_ = options;
	}
	
	@Override
	public LemmatizerGenerator train(List<Instance> instances,
			List<Instance> dev_instances) {
		
		Map<String, List<String>> map = new HashMap<>();
		
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

	private void addToMap(String key, Map<String, List<String>> map,
			Instance instance) {
		List<String> lemmas = map.get(key);
		
		if (lemmas == null) {
			lemmas = new LinkedList<>();
			map.put(key, lemmas);
		}
		
		if (!lemmas.contains(instance.getLemma())) {
			lemmas.add(instance.getLemma());
		}
	}

}
