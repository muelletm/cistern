// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

import java.util.HashMap;
import java.util.Map;

import marmot.util.StringUtils.Mode;
import marmot.util.StringUtils.Shape;

public class HashLexicon implements Lexicon {

	private static final long serialVersionUID = 1L;
	private Map<String, int[]> map_;
	private Mode mode_;
	
	public HashLexicon(Mode mode) {
		mode_ = mode;
		map_ = new HashMap<>();
	}
	
	public void addEntry(String word, Integer value) {
		String key = StringUtils.normalize(word, mode_);
		int[] current_value = map_.get(key);
		if (current_value == null) {
			current_value = new int[ARRAY_LENGTH];
			map_.put(key, current_value);	
		}
		
		Shape shape = StringUtils.getShape(word);
		current_value[shape.ordinal()] += value;
		current_value[ARRAY_LENGTH - 1] += value;
	}
	
	public int[] getCount(String word) {
		String key = StringUtils.normalize(word, mode_);
		int[] value = map_.get(key);
		return value;
	}

	public int size() {
		return map_.size();
	}

}
