// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import marmot.util.StringUtils.Mode;
import marmot.util.StringUtils.Shape;

public class Lexicon implements Serializable {

	private static final long serialVersionUID = 1L;
	private Map<String, int[]> map_;
	private Mode mode_;
	public static final int ARRAY_LENGTH = Shape.values().length + 1;

	public Lexicon(Mode mode) {
		mode_ = mode;
		map_ = new HashMap<>();
	}
	
	public void addEntry(String word, Integer value) {
		String key = normalize(word);
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
		String key = normalize(word);
		int[] value = map_.get(key);
		return value;
	}

	private String normalize(String word) {
		if (mode_ == null) {
			return word;
		}
		return StringUtils.normalize(word, mode_);
	}

	public int size() {
		return map_.size();
	}

}
