// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.hmm;

import java.io.Serializable;

public abstract class Smoother implements Serializable {
	private static final long serialVersionUID = 1L;

	public enum Type {
		Emission, Transition
	}
	
	public Statistics smooth(Model model) {
		Statistics statistics = new Statistics(model.getStatistics());
		for (Tree tag : model.getTopLevel().values()) {
			smooth(tag, model, statistics, Type.Emission);
			smooth(tag, model, statistics, Type.Transition);
		}
		return statistics;
	}

	protected abstract void smooth(Tree tag, Model model, Statistics statistics,
			Type type);
	
	protected double getFreq(Statistics unsmoothed_statistics, int tag,
			int index, Type type) {
		if (type == Type.Emission) {
			return unsmoothed_statistics.getEmissions(tag, index);
		}
		return unsmoothed_statistics.getTransitions(tag, index);
	}

	protected void setFreq(Model model, Statistics statistics, int tag_index, int index,
			Type type, double d) {
		if (type == Type.Emission) {
			statistics.setEmissions(tag_index, index, d);
		} else {
			statistics.setTransitions(tag_index, index, d);
		}
	}

	protected int getNumber(Model model, Type type) {
		if (type == Type.Emission) {
			return model.getWordTable().size();
		}
		return model.getTagTable().size();
	}
	
}
