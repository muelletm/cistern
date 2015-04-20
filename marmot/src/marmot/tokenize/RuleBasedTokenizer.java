// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.tokenize;

import java.util.List;

import marmot.tokenize.Tokenizer;
import marmot.tokenize.rules.RuleProvider;
import marmot.tokenize.rules.RulebasedTransformator;

public class RuleBasedTokenizer extends AbstractTokenizer {

	private static final long serialVersionUID = 1214140578027691025L;
	private Tokenizer tokenizer_;
	private RulebasedTransformator untok_transformator_;
	
	public RuleBasedTokenizer(Tokenizer tokenizer, RuleProvider provider){
		tokenizer_ = tokenizer;
		untok_transformator_ = null;
		if(provider != null){
			untok_transformator_ = provider.getUnTokTransformator();
		}
	}
	
	@Override
	public List<String> tokenize(String untokenized) {
    	if (untok_transformator_ != null) {
    		untokenized = untok_transformator_.applyRules(untokenized);
    	}
    	return tokenizer_.tokenize(untokenized);
   	}

}
