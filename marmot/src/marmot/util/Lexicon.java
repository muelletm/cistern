package marmot.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import marmot.util.StringUtils.Mode;

public class Lexicon implements Serializable {
	
	private Map<Integer, Integer> map_;
	private Mode mode_;

	public Lexicon(Mode mode) {
		mode_ = mode;
		map_ = new HashMap<>();
	}
	
	public void addEntry(String word, Integer value) {
		int code = getHashCode(word);
		Integer current_value = map_.get(code);
		if (current_value != null && current_value != value) {
			value = Math.min(current_value, value);
		}
		map_.put(code, value);
	}
	
	public int getCount(String word) {
		int code = getHashCode(word);
		Integer current_value = map_.get(code);
		if (current_value == null)
			return 0;
		return current_value;
	}

	private int getHashCode(String word) {
		word = normalize(word);
		return word.hashCode();
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
