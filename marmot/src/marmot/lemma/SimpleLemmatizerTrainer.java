package marmot.lemma;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SimpleLemmatizerTrainer implements LemmatizerTrainer {

	public static class Options {
		
		private boolean handle_unseen_;
		private boolean use_pos_;

		private Options() {
			handle_unseen_ = false;
			use_pos_ = false;
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

		public void setHandleUnseen(boolean b) {
			handle_unseen_ = b;
		}

		public void setUsePos(boolean b) {
			use_pos_ = b;
		}
		
	}

	private Options options_;
	
	public SimpleLemmatizerTrainer(Options options) {
		options_ = options;
	}
	
	@Override
	public Lemmatizer train(List<Instance> instances,
			List<Instance> dev_instances) {
		
		Map<String, List<String>> map = new HashMap<>();
		
		for (Instance instance : instances) {
			String key = null;
			
			if (options_.use_pos_) {
				key = SimpleLemmatizer.toKey(instance);
				addToMap(key, map, instance);
			}
			
			key = SimpleLemmatizer.toSimpleKey(instance);
			addToMap(key, map, instance);
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
