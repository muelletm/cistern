// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core;

import java.io.Serializable;
import java.util.Collection;

public interface WeightVector extends Serializable {
	static final long serialVersionUID = 1L;

	void updateWeights(State state, double amount, boolean transition);
	FeatureVector extractTransitionFeatures(State context);
	FeatureVector extractStateFeatures(Sequence sentence, int index);
	double dotProduct(State state, FeatureVector vector);
	FeatureVector extractStateFeatures(State state);
	void init(Model model, Collection<Sequence> sequence);
	void setPenalty(boolean b, double accumalted_penalty);
	void scaleBy(double scale_factor);
	void setExtendFeatureSet(boolean extend);
	double[] getWeights();
	void setWeights(double[] weights);
}
