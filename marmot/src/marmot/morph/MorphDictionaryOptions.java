// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph;

import java.io.Serializable;
import java.util.Arrays;

import marmot.util.StringUtils.Mode;
import marmot.util.Mutable;
import marmot.util.StringUtils;

public class MorphDictionaryOptions implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum KeyType {
		norm,
		filename,
		indexes,
		type,
		limit,
		reverse,
		dense
	}
	
	public enum DictionaryType {
		hash,
		suffix
	}
	
	private String filename_;
	private int[] indexes_;
	private Mode norm_;
	private DictionaryType type_;
	private boolean reverse_;
	private int limit_;
	private boolean dense_;
	
	public static MorphDictionaryOptions parse(String option_string) {
		return parse(option_string, true);
	}
	
	public static MorphDictionaryOptions parse(String option_string, boolean set_defaults) {
		MorphDictionaryOptions options = new MorphDictionaryOptions();
		
		if (set_defaults) {
			options.setDefaultOptions();
		}
		
		Mutable<Integer> index = new Mutable<Integer>(0);
		
		while (index.get() < option_string.length()) {
			KeyType key = readKey(index, option_string);
			
			try {
			
			switch (key) {
			case filename:
				options.filename_ = readStringValue(index, option_string);
				break;
			case type:
				options.type_ = readTypeValue(index, option_string);
				break;
			case indexes:
				options.indexes_ = readArrayValue(index, option_string);
				break;
			case norm:
				options.norm_ = readModeValue(index, option_string);
				break;
			case limit:
				options.limit_ = readIntValue(index, option_string);
				break;
			case reverse:
				options.reverse_ = readBooleanValue(index, option_string);
				break;
			case dense:
				options.dense_ = readBooleanValue(index, option_string);
				break;
			}
			
			} catch (Exception e) {
				System.err.println("Error: Couldn't parse string: "  + option_string + " because of:");
				throw new RuntimeException(e);
			}
			
		}
		
		
		return options;
	}

	private static Mode readModeValue(Mutable<Integer> index,
			String option_string) {
		String type = readStringValue(index, option_string);
		return Mode.valueOf(type);
	}

	private static int readIntValue(Mutable<Integer> index, String option_string) {
		String type = readStringValue(index, option_string);
		return Integer.valueOf(type);
	}

	private void setDefaultOptions() {
		int[] indexes = {2,3};
		indexes_ = indexes;
		norm_ = Mode.none;
		type_ = DictionaryType.hash;
		reverse_ = true;
		dense_ = false;
		limit_ = 10;
	}

	private static boolean readBooleanValue(Mutable<Integer> index,
			String option_string) {
		String type = readStringValue(index, option_string);
		return Boolean.valueOf(type);
	}

	private static int[] readArrayValue(Mutable<Integer> index,
			String option_string) {
		
		String[] array = StringUtils.parseArray(option_string, index);
		
		int[] indexes = new int[array.length];
		for (int i = 0; i < array.length; i ++) {
			indexes[i] = Integer.parseInt(array[i]);
			
//			if (indexes[i] < 1) {
//				throw new InvalidParameterException("Index must be > 0!");
//			}
			
		}
		
		Arrays.sort(indexes);
		
		if (index.get() < option_string.length()) {
			if (option_string.charAt(index.get()) == ',') {
				index.set(index.get() + 1);
			}
		}
		
		
		return indexes;
	}

	private static DictionaryType readTypeValue(Mutable<Integer> index,
			String option_string) {
		String type = readStringValue(index, option_string);
		return DictionaryType.valueOf(type);
	}

	private static String readStringValue(Mutable<Integer> index,
			String option_string) {
		int eq_index = option_string.indexOf(',', index.get());
		
		if (eq_index == -1) {
			eq_index = option_string.length();
		}

		String value = option_string.substring(index.get(), eq_index);
		index.set(eq_index + 1);
		return value;
	}

	private static KeyType readKey(Mutable<Integer> index, String option_string) {
		int eq_index = option_string.indexOf('=', index.get());
		int co_index = option_string.indexOf(',', index.get());
		
		if (eq_index == -1 || (co_index < eq_index && co_index != -1)) {
			return KeyType.filename;
		}
		
		String key_string = option_string.substring(index.get(), eq_index);
		index.set(eq_index + 1);
		
		KeyType type = KeyType.valueOf(key_string);
		
		return type;
	}

	public String getFilename() {
		return filename_;
	}

	public Mode getNormalize() {
		return norm_;
	}

	public int[] getIndexes() {
		return indexes_;
	}

	public DictionaryType getDictType() {
		return type_;
	}

	public boolean getReverse() {
		return reverse_;
	}

	public int getLimit() {
		return limit_;
	}

	public void setIndexes(int[] indexes) {
		indexes_ = indexes;
	}

	public boolean getDense() {
		return dense_; 
	}

	public void setNormalize(Mode mode) {
		norm_ = mode;
	}

	
}
