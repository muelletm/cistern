// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morfessor;

import marmot.util.Counter;
import marmot.util.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class CharEncoder implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final char UNKNOWN_ = '?';
	private Map<Character, Character> encode_map_;
	private Map<Character, Character> decode_map_;

	public CharEncoder(Map<Character, Character> encode_map) {
		encode_map_ = encode_map;
		decode_map_ = new HashMap<Character, Character>();
		for (Map.Entry<Character, Character> entry : encode_map.entrySet()) {
			decode_map_.put(entry.getValue(), entry.getKey());
		}
	}
	
	public static CharEncoder loadFromFile(String filename) {
				Map<Character, Character> encode_map = new HashMap<Character, Character>();
				try {
					BufferedReader reader = FileUtils.openFile(filename);
		
					while (reader.ready()) {
						String line = reader.readLine().trim();
						if (line.isEmpty()) {
							continue;
						}
						char key = line.charAt(0);
						char value = line.charAt(2);
						encode_map.put(key, value);
					}
		
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
		
				return new CharEncoder(encode_map);
	}


	public static CharEncoder fromVocab(Vocab vocab) {
		int length = ('z' - 'a') + ('Z' - 'A') + ('9' - '0') + 3;

		char[] alphabet = new char[length];

		int current = 0;
		for (char index = 'a'; index <= 'z'; index++)
			alphabet[current++] = index;

		for (char index = 'A'; index <= 'Z'; index++)
			alphabet[current++] = index;

		for (char index = '0'; index <= '9'; index++)
			alphabet[current++] = index;

		assert length == current;

		Counter<Character> counter = new Counter<Character>();
		for (Entry<String, Double> entry : vocab.entrySet()) {
			for (int index = 0; index < entry.getKey().length(); index++) {
				counter.increment(entry.getKey().charAt(index),
						entry.getValue());
			}
		}

		Map<Character, Character> encode_map = new HashMap<>();
		current = length - 1;

		Set<Character> alphabet_set = new HashSet<>();
		for (char c : alphabet) {
			alphabet_set.add(c);
		}

		for (Entry<Character, Double> entry : counter.sortedEntries()) {
			if (alphabet_set.isEmpty()) {
				break;
			}
			char c = entry.getKey();
			while (!alphabet_set.contains(c)) {
				c = alphabet[current--];
			}
			alphabet_set.remove(c);
			encode_map.put(entry.getKey(), c);
		}

		return new CharEncoder(encode_map);
	}

	public String encode(String word) {
		StringBuilder sb = new StringBuilder();

		for (int index = 0; index < word.length(); index++) {
			Character c = encode_map_.get(word.charAt(index));
			if (c == null) {
				sb.append(UNKNOWN_);
			} else {
				sb.append(c);
			}
		}

		return sb.toString();
	}

	public String decode(String word) {
		StringBuilder sb = new StringBuilder();
		for (int index = 0; index < word.length(); index++) {		
			Character c = decode_map_.get(word.charAt(index));
			if (c == null) {
				sb.append(UNKNOWN_);
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
}
