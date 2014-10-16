// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.hmm;

import hmmla.util.Ling;

import java.util.Map;


public class SignatureHmmModel implements HmmModel {

	HmmModel hmm_model_;
	Map<String, double[]> signature_map_;
	Model model_;
	
	public SignatureHmmModel(HmmModel hmm_model,
			Map<String, double[]> signature_map, Model model) {
		hmm_model_ = hmm_model;
		signature_map_ = signature_map;
		model_ = model;
	}

	@Override
	public void getTransitions(int i, double[] scores) {
		hmm_model_.getTransitions(i, scores);
	}

	@Override
	public double getTransitions(int i, int j) {
		return hmm_model_.getTransitions(i, j);
	}

	@Override
	public void getEmissions(String word, double[] scores) {
		if (model_.isKnown(word)) {
			hmm_model_.getEmissions(word, scores);
			return;
		} 
		
		String signature = Ling.signature(word, model_);

		double[] probs = signature_map_.get(signature);			
		if (probs == null) {
			System.err.format("Warning: Unknown signature: %s (%s)\n", signature, word);
			hmm_model_.getEmissions(word, scores);
			return;
		}
		
		System.arraycopy(probs, 0, scores, 0, scores.length);
	}
}
