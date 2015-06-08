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
import marmot.lemma.LemmaInstance;
import marmot.lemma.Lemmatizer;
import marmot.lemma.ranker.RankerCandidate;

public class MorphTagger extends SimpleTagger {

	private static final long serialVersionUID = 1L;
	private transient Lemmatizer lemmatizer_;
	
	public MorphTagger(Model model, int order, WeightVector weight_vector) {
		super(model, order, weight_vector);
	}
	
	public void setPipeLineLemmatizer(Lemmatizer lemmatizer) {
		lemmatizer_ = lemmatizer;
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
		
		int token_index = 0;
		for (State state : states) {
			List<String> lemma_tags = new ArrayList<>();
			
			List<String> tags = indexesToStrings(stateToIndexes(state));
			
			String lemma = null;
			
			if (state.getLemmaCandidates() != null) {
				RankerCandidate candidate = RankerCandidate.bestCandidate(state.getLemmaCandidates()); 
				lemma = candidate.getLemma(); 
			} else if (lemmatizer_ != null) {
				Word word = (Word) sequence.get(token_index);
				LemmaInstance instance = LemmaInstance.getInstance(word);
				
				instance.setPosTag(tags.get(0));
				if (1 < tags.size()) {
					instance.setMorphTag(tags.get(1));	
				}
				
				lemma = lemmatizer_.lemmatize(instance);
			} 
			
			lemma_tags.add(lemma);
			lemma_tags.addAll(tags);
			list.add(lemma_tags);	
			
			token_index ++;
		}
		
		return list;
	}
	
}
