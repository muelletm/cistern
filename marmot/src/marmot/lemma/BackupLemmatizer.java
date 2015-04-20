// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.lemma;

public class BackupLemmatizer implements Lemmatizer, LemmatizerGenerator {

	private static final long serialVersionUID = 1L;
	private LemmatizerGenerator lemmatizer_;
	private LemmatizerGenerator backup_;

	public BackupLemmatizer(LemmatizerGenerator lemmatizer, LemmatizerGenerator backup) {
		lemmatizer_ = lemmatizer;
		backup_ = backup;
	}
	
	@Override
	public String lemmatize(Instance instance) {
		String lemma = lemmatizer_.lemmatize(instance);
		if (lemma != null) {
			return lemma;
		}
		
		return backup_.lemmatize(instance);
	}

	@Override
	public void addCandidates(Instance instance, LemmaCandidateSet set) {
		lemmatizer_.addCandidates(instance, set);
		backup_.addCandidates(instance, set);
	}

}
