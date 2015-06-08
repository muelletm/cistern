// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.lemma.ranker;

import java.util.List;

import marmot.lemma.LemmaInstance;
import marmot.lemma.LemmaCandidateGenerator;
import marmot.lemma.LemmaCandidateSet;
import marmot.lemma.LemmatizerGenerator;

public class Ranker implements LemmatizerGenerator {

	private static final long serialVersionUID = 1L;
	private RankerModel model_;
	private List<LemmaCandidateGenerator> generators_;

	public Ranker(RankerModel model, List<LemmaCandidateGenerator> generators) {
		model_ = model;
		generators_ = generators;
	}

	@Override
	public String lemmatize(LemmaInstance instance) {
		LemmaCandidateSet set = new LemmaCandidateSet();
		addCandidates(instance, set);;
		RankerInstance rinstance = new RankerInstance(instance, set);
		model_.addIndexes(rinstance, set, false);
		return model_.select(rinstance);
	}

	@Override
	public void addCandidates(LemmaInstance instance, LemmaCandidateSet set) {
		for (LemmaCandidateGenerator generator : generators_) {
			generator.addCandidates(instance, set);
		}
	}

	@Override
	public boolean isOOV(LemmaInstance instance) {
		return model_.isOOV(instance);
	}

	public RankerModel getModel() {
		return model_;
	}

}
