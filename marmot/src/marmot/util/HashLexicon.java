// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import marmot.util.StringUtils.Mode;
import marmot.util.StringUtils.Shape;

public class HashLexicon implements Lexicon {

	private static final long serialVersionUID = 1L;
	private transient Map<String, int[]> map_;
	private Mode mode_;

	public HashLexicon(Mode mode) {
		mode_ = mode;
		map_ = new HashMap<>();
	}

	private static void checkUnigramLine(boolean condition,
			String unigram_file, List<String> line) {
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

			checkUnigramLine(line.size() == 1 || line.size() == 2, path, line);

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

	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
		oos.writeInt(map_.size());
		for (Map.Entry<String, int[]> entry : map_.entrySet()) {
			String string = entry.getKey();
			oos.writeUTF(string);
			int[] counts = entry.getValue();
			int non_zeroes = 0;
			for (int count : counts) {
				if (count > 0) {
					non_zeroes ++;
				}
			}
			oos.writeShort(non_zeroes);
			for (int i = 0; i < counts.length; i++) {
				int count = counts[i];
				if (count > 0) {
					oos.writeShort(i);
					oos.writeShort(count);
				}
			}
		}
	}

	private void readObject(ObjectInputStream ois)
			throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		int map_size = ois.readInt();
		map_ = new HashMap<>(map_size);
		for (int number = 0; number < map_size; number++) {
			String string = ois.readUTF();
			int[] counts = new int[Lexicon.ARRAY_LENGTH];
			int non_zeroes = ois.readShort();
			for (int non_zero = 0; non_zero < non_zeroes; non_zero++) {
				int index = ois.readShort();
				int count = ois.readShort();
				counts[index] = count;
			}
			map_.put(string, counts);
		}
	}

	public Map<String, int[]> getMap() {
		return map_;
	}

}
