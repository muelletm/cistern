// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package lemming.lemma.ranker;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import lemming.lemma.LemmaCandidate;
import lemming.lemma.LemmaCandidateGenerator;
import lemming.lemma.LemmaCandidateSet;
import lemming.lemma.LemmaInstance;
import lemming.lemma.LemmatizerGenerator;

public class Ranker implements LemmatizerGenerator {

	private static final long serialVersionUID = 1L;
	private RankerModel model_;
	private List<LemmaCandidateGenerator> generators_;
	private int num_candidates_ = -1;

	public Ranker(RankerModel model, List<LemmaCandidateGenerator> generators) {
		model_ = model;
		generators_ = generators;
	}

	@Override
	public String lemmatize(LemmaInstance instance) {
		LemmaCandidateSet set = new LemmaCandidateSet();
		addCandidates_(instance, set);
		
		if (set.size() == 0) {
			return instance.getForm();
		}
		
		RankerInstance rinstance = new RankerInstance(instance, set);
		model_.addIndexes(rinstance, set, false);
		return model_.select(rinstance);
	}

	public void addCandidates_(LemmaInstance instance, LemmaCandidateSet set) {
		for (LemmaCandidateGenerator generator : generators_) {
			generator.addCandidates(instance, set);
		}
	}
	
	@Override
	public void addCandidates(LemmaInstance instance, LemmaCandidateSet set) {
		
		addCandidates_(instance, set);
		
		if (num_candidates_ >= 0 && set.size() > num_candidates_) {
			
			RankerInstance rinstance = new RankerInstance(instance, set);
			model_.addIndexes(rinstance, set, false);
			
			List<Double> scores = model_.scores(rinstance);
			
			Collections.sort(scores, new Comparator<Double>() {

				@Override
				public int compare(Double o1, Double o2) {
					return - Double.compare(o1, o2);
				}
			});
			
			double lowest_score = scores.get(num_candidates_ - 1);
			
			LemmaCandidateSet copy_set = new LemmaCandidateSet(set);
			set.clear();
			
			for (Map.Entry<String, LemmaCandidate> entry : copy_set) {
				String lemma = entry.getKey();
				LemmaCandidate candidate = entry.getValue();
				
				if (candidate.getScore() + 1e-5 > lowest_score) {
					candidate.setFeatureIndexes(null);
					candidate.setScore(0.0);
					set.addCandidate(lemma, candidate);
				}
			}
		}
		
		if (set.size() == 0) {
			set.getCandidate(instance.getForm());
		}
	}

	@Override
	public boolean isOOV(LemmaInstance instance) {
		return model_.isOOV(instance);
	}

	public RankerModel getModel() {
		return model_;
	}

	public void setNumCandidates(int num_candidates) {
		num_candidates_ = num_candidates;
	}

}
