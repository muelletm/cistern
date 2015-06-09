// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.lemma;

import java.util.Map;

import marmot.lemma.SimpleLemmatizerTrainer.SimpleLemmatizerTrainerOptions;
import marmot.util.Counter;

public class SimpleLemmatizer implements LemmatizerGenerator {

	private static final long serialVersionUID = 1L;
	private static final String SEPARATOR = "\t";
	private Map<String, Counter<String>> map_;
	private SimpleLemmatizerTrainerOptions options_;

	public SimpleLemmatizer(SimpleLemmatizerTrainerOptions options, Map<String, Counter<String>> map) {
		map_ = map;
		options_ = options;
	}

	public static String toKey(LemmaInstance instance) {
		String pos_tag = instance.getPosTag();
		if (pos_tag == null) {
			return null;
		}
		
		String form = instance.getForm();
		return String.format("%s%s%s", form, SEPARATOR, pos_tag);
	}

	@Override
	public String lemmatize(LemmaInstance instance) {
		Counter<String> lemmas = null;
		String key = null;

		if (options_.getUsePos()) {
			key = toKey(instance);
			if (key != null) {
				lemmas = map_.get(key);
				if (lemmas != null && (!options_.getAbstainIfAmbigous() || lemmas.size() == 1 )) {
					return lemmas.max();
				}
			}
		}

		if (options_.getUseBackup()) {
			key = toSimpleKey(instance);
			if (key != null) {
				lemmas = map_.get(key);
				if (lemmas != null && (!options_.getAbstainIfAmbigous() || lemmas.size() == 1 )) {
					return lemmas.max();
				}
			}
		}

		if (options_.getHandleUnseen()) {
			return instance.getForm();
		}

		return null;
	}

	public static String toSimpleKey(LemmaInstance instance) {
		return instance.getForm();
	}

	private void addCandidates(Counter<String> lemmas, LemmaCandidateSet set) {
		if (lemmas != null) {
			for (Map.Entry<String, Double> lemma : lemmas.entrySet()) {
				set.getCandidate(lemma.getKey());
			}
		}
	}
	
	@Override
	public void addCandidates(LemmaInstance instance, LemmaCandidateSet set) {	
		String key = toKey(instance);
		if (key != null) {
			Counter<String> lemmas = map_.get(key);
			addCandidates(lemmas, set);
		}
		
		key = toSimpleKey(instance);
		if (key != null) {
			Counter<String> lemmas = map_.get(key);
			addCandidates(lemmas, set);
		}

	}

	@Override
	public boolean isOOV(LemmaInstance instance) {
		return map_.get(toSimpleKey(instance)) == null;
	}

}
