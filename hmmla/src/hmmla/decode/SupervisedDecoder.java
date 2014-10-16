// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.decode;

import hmmla.hmm.HmmModel;
import hmmla.hmm.Model;
import hmmla.io.Sentence;
import hmmla.io.Token;

import java.util.ArrayList;
import java.util.List;


public class SupervisedDecoder extends SimpleDecoder {

	public SupervisedDecoder(Model model, HmmModel hmm_model) {
		super(model, hmm_model, false);
	}
	
	@Override
	public List<String> bestPath(Sentence sentence) {
		List<String> candidates = new ArrayList<String>(sentence.size());
		for (Token token : sentence) {
			candidates.add(token.getTag());
		}
		
		return bestPath(candidates, sentence);
	}

}
