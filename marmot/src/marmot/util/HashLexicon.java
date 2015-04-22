// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

import java.util.HashMap;
import java.util.List;
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
	
	private static void checkUnigramLine(boolean condition, String unigram_file,
			List<String> line) {
		if (!condition) {
			throw new RuntimeException(
					String.format(
							"Line in file %s should be of format <WORD> [<COUNT>], but is \"%s\"",
							unigram_file, line));
		}
	}
	
	public static HashLexicon readFromFile(String path, int min_count) {
		LineIterator iterator = new LineIterator(path);

		HashLexicon unigram_lexicon = new HashLexicon(Mode.lower);

		while (iterator.hasNext()) {

			List<String> line = iterator.next();

			if (line.isEmpty())
				continue;

			checkUnigramLine(line.size() == 1 || line.size() == 2,
					path, line);

			String word = line.get(0);

			int count = 1;

			if (line.size() > 1) {
				try {
					String count_string = line.get(1);
					count = Integer.valueOf(count_string);
				} catch (NumberFormatException e) {
					checkUnigramLine(false, path, line);
				}
			}

			if (count >= min_count)
				unigram_lexicon.addEntry(word, count);
			
		}
		
		return unigram_lexicon;
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
