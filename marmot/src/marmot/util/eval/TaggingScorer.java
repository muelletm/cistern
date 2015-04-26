// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util.eval;

import java.util.Collection;

import marmot.test.util.KeyValueOptions;



public class TaggingScorer extends AbstractOneTokenPerLineScorer {

	@Override
	public double getScore(KeyValueOptions actual_opts, String[] actual_tokens,
			KeyValueOptions prediction_opts, String[] prediction_tokens) {
		
		Collection<String> keys = actual_opts.getSortedKeys();
		
		String actual_tag = getTag(keys, actual_opts, actual_tokens);
		String prediction_tag = getTag(keys, prediction_opts, prediction_tokens);
		
		boolean tag_correct = actual_tag.equals(prediction_tag);
			
		if (tag_correct) {
			return 1.0;
		}
		return 0.0;
	}

	private String getTag(Collection<String> keys, KeyValueOptions opts, String[] tokens) {
		StringBuilder sb = new StringBuilder();
		for (String key : keys) {
			int index = opts.getValueAsInteger(key);
			sb.append(tokens[index]);
		}
		return sb.toString();
	}

}
