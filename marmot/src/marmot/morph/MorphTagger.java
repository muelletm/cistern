// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph;

import java.util.ArrayList;
import java.util.List;

import marmot.core.Model;
import marmot.core.Sequence;
import marmot.core.SimpleTagger;
import marmot.core.State;
import marmot.core.Token;
import marmot.core.WeightVector;
import marmot.lemma.ranker.RankerCandidate;

public class MorphTagger extends SimpleTagger {

	private static final long serialVersionUID = 1L;
	
	public MorphTagger(Model model, int order, WeightVector weight_vector) {
		super(model, order, weight_vector);
	}

	protected void addIndexes(Sequence sequence) {
		MorphModel model = (MorphModel) getModel();
		for (Token token : sequence) {
			Word word = (Word) token;
			model.addIndexes(word, false);
		}		
	}
	
	@Override
	public List<List<String>> tag(Sequence sequence) {
		addIndexes(sequence);
		return super.tag(sequence);
	}
	
	public List<List<String>> tagWithLemma(Sequence sequence) {
		addIndexes(sequence);
		
		List<State> states = tag_states(sequence);
		
		List<List<String>> list = new ArrayList<>(sequence.size());
		
		for (State state : states) {
			List<String> lemma_tags = new ArrayList<>();
			
			String lemma = null;
			if (state.getLemmaCandidates() != null) {
				RankerCandidate candidate = RankerCandidate.bestCandidate(state.getLemmaCandidates()); 
				lemma = candidate.getLemma(); 
			}
			lemma_tags.add(lemma);
			
			List<String> tags = indexesToStrings(stateToIndexes(state));
			
			lemma_tags.addAll(tags);
			list.add(tags);			
		}
		
		return list;
	}
	
}
