// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import marmot.util.LineIterator;
import marmot.util.SymbolTable;


public class MorphDictionary implements Serializable {
	private static final long serialVersionUID = 1L;
	private HashMap<String, int[]> index_map_;
	private int num_tags_;
	private SymbolTable<String> table_;

	public MorphDictionary(String filename) {
		init(filename);
	}

	private void init(String filename) {
		
		int comma_index = filename.indexOf(',');
		int[] row_indexes = {2,3};
		if (comma_index >= 0) {
			String row_string = filename.substring(comma_index + 1);
			filename = filename.substring(0, comma_index);
			System.err.println(filename);
			if (row_string.isEmpty()) {
				throw new RuntimeException("Row string is empty: " + filename);
			}
			String[] rows = row_string.split(",+");
			row_indexes = new int[rows.length];
			for (int i=0; i<rows.length; i++) {
				try {
					int index = Integer.parseInt(rows[i]);
					if (index < 1) {
						throw new RuntimeException("Row indexes must be >= 1: " + filename);
					}
					row_indexes[i] = index;
				} catch (NumberFormatException e) {
					throw new RuntimeException("Row string contains non integer: " + filename);
				}
			}
		}
		Arrays.sort(row_indexes);
		
		LineIterator iterator = new LineIterator(filename);		
		table_ = new SymbolTable<String>();
		Map<String, Set<Integer>> map = new HashMap<String, Set<Integer>>();		
		
		while (iterator.hasNext()) {			
			List<String> line = iterator.next();
						
			if (!line.isEmpty()) {
				String form = line.get(0);
				
				form = replaceDigits(form);
				
				Set<Integer> indexes = map.get(form);
				
				if (indexes == null) {
					indexes = new HashSet<Integer>();
					map.put(form, indexes);
				}
				
				for (int row_index : row_indexes) {
					if (row_index < line.size()) {
						String tag = line.get(row_index);
						int index = table_.toIndex(tag, true);
						indexes.add(index);
					} else {
						break;
					}
				}
			}		
		}
		
		index_map_ = new HashMap<String, int[]>();
		
		for (Map.Entry<String, Set<Integer>> entry : map.entrySet()) {
			String form = entry.getKey();
			Set<Integer> set = entry.getValue();
			
			int[] indexes = new int[set.size()];
			int i = 0;
			for (int index : set) {
				indexes[i] = index;
				i++;
			}
			
			Arrays.sort(indexes);
			index_map_.put(form, indexes);
		}
		
		num_tags_ = table_.size();
	}
	
	public int[] getIndexes(String form) {
		form = replaceDigits(form);
		return index_map_.get(form);
	}

	private String replaceDigits(String form) {
		boolean contains_digit = false;
		for (int i = 0; i < form.length(); i++) {
			if (Character.isDigit(form.charAt(i))) {
				contains_digit = true;
				break;
			}
		}
		
		if (contains_digit) {
			StringBuilder sb = new StringBuilder(form);
			for (int i = 0; i < form.length(); i++) {
				if (Character.isDigit(form.charAt(i))) {
					sb.setCharAt(i, '#');
				}
			}
			form = sb.toString();
		}
		
		return form;
	}

	public int numTags() {
		return num_tags_;
	}

	public void addWordsFromFile(String morphDict) {
		throw new RuntimeException("Not Implemented");
	}

}
