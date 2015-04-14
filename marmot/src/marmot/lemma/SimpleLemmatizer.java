package marmot.lemma;

import java.util.List;
import java.util.Map;

import marmot.lemma.SimpleLemmatizerTrainer.SimpleLemmatizerTrainerOptions;

public class SimpleLemmatizer implements LemmatizerGenerator {

	private static final String SEPARATOR = "%|%|%";
	private Map<String, List<String>> map_;
	private SimpleLemmatizerTrainerOptions options_;

	public SimpleLemmatizer(SimpleLemmatizerTrainerOptions options, Map<String, List<String>> map) {
		map_ = map;
		options_ = options;
	}

	public static String toKey(Instance instance) {
		String pos_tag = instance.getPosTag();
		if (pos_tag == null) {
			return null;
		}
		
		String form = instance.getForm();
		return String.format("%s%s%s", form, SEPARATOR, pos_tag);
	}

	@Override
	public String lemmatize(Instance instance) {
		List<String> lemmas = null;
		String key = null;

		if (options_.getUsePos()) {
			key = toKey(instance);
			if (key != null) {
				lemmas = map_.get(key);
				if (lemmas != null && (!options_.getAbstainIfAmbigous() || lemmas.size() == 1 )) {
					return lemmas.get(0);
				}
			}
		}

		if (options_.getUseBackup()) {
			key = toSimpleKey(instance);
			if (key != null) {
				lemmas = map_.get(key);
				if (lemmas != null && (!options_.getAbstainIfAmbigous() || lemmas.size() == 1 )) {
					return lemmas.get(0);
				}
			}
		}

		if (options_.getHandleUnseen()) {
			return instance.getForm();
		}

		return null;
	}

	public static String toSimpleKey(Instance instance) {
		return instance.getForm();
	}

	@Override
	public void addCandidates(Instance instance, LemmaCandidateSet set) {	
		String key = toKey(instance);
		if (key != null) {
			List<String> lemmas = map_.get(key);
			if (lemmas != null) {
				for (String lemma : lemmas) {
					set.getCandidate(lemma);
				}
			}
		}
		
		key = toSimpleKey(instance);
		if (key != null) {
			List<String> lemmas = map_.get(key);
			if (lemmas != null) {
				for (String lemma : lemmas) {
					set.getCandidate(lemma);
				}
			}
		}

		
		
	}

}
