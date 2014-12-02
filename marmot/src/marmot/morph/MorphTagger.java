// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph;

import java.util.List;

import marmot.core.Model;
import marmot.core.Sequence;
import marmot.core.SimpleTagger;
import marmot.core.Token;
import marmot.core.WeightVector;

public class MorphTagger extends SimpleTagger {

	private static final long serialVersionUID = 1L;
	
	public MorphTagger(Model model, int order, WeightVector weight_vector) {
		super(model, order, weight_vector);
	}

	@Override
	public List<List<String>> tag(Sequence sequence) {
		MorphModel model = (MorphModel) getModel();
		for (Token token : sequence) {
			Word word = (Word) token;
			model.addIndexes(word, false);
		}
		
		return super.tag(sequence);
	}
	
}
