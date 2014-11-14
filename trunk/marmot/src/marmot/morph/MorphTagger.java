// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph;

import java.util.List;

import marmot.core.Model;
import marmot.core.Sequence;
import marmot.core.SimpleTagger;
import marmot.core.Token;
import marmot.core.WeightVector;
import marmot.morph.io.SentenceNormalizer;
import marmot.util.StringUtils.Mode;

public class MorphTagger extends SimpleTagger {

	private static final long serialVersionUID = 1L;
	private Mode norm_mode_;

	public MorphTagger(Model model, int order, WeightVector weight_vector) {
		super(model, order, weight_vector);
		norm_mode_ = ((MorphOptions) model.getOptions()).getNormalizeForms();
	}

	@Override
	public List<List<String>> tag(Sequence sequence) {
		sequence = SentenceNormalizer.normalizeSentence(sequence, norm_mode_);

		MorphModel model = (MorphModel) getModel();
		for (Token token : sequence) {
			Word word = (Word) token;
			model.addIndexes(word, false);
		}
		
		return super.tag(sequence);
	}
	
}
