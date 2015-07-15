// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import marmot.util.LineIterator;
import marmot.util.StringUtils;
import marmot.util.SymbolTable;

public class HashDictionary extends MorphDictionary {
	private static final long serialVersionUID = 1L;
	private HashMap<String, int[]> index_map_;

	public HashDictionary() {
		this(null);
	}

	public HashDictionary(SymbolTable<String> table) {
		super(table);
	}

	@Override
	public void init(MorphDictionaryOptions options) {
		super.init(options);

		LineIterator iterator = new LineIterator(options.getFilename());

		Map<String, Set<Integer>> map = new HashMap<String, Set<Integer>>();
		int[] row_indexes = options.getIndexes();

		SymbolTable<String> table = getTable();

		while (iterator.hasNext()) {
			List<String> line = iterator.next();

			if (!line.isEmpty()) {
				String form = line.get(0);

				form = StringUtils.normalize(form, options.getNormalize());

				Set<Integer> indexes = map.get(form);

				if (indexes == null) {
					indexes = new HashSet<Integer>();
					map.put(form, indexes);
				}

				for (int row_index : row_indexes) {
					if (row_index > 0 && row_index < line.size()) {
						String tag = line.get(row_index);
						int index = table.toIndex(tag, true);
						indexes.add(index);
					}
				}
			}
		}

		index_map_ = new HashMap<String, int[]>();

		for (Map.Entry<String, Set<Integer>> entry : map.entrySet()) {
			String form = entry.getKey();
			Set<Integer> set = entry.getValue();

			if (set.size() <= options.getLimit()) {
				int[] indexes = new int[set.size()];
				int i = 0;
				for (int index : set) {
					indexes[i++] = index;
				}

				index_map_.put(form, indexes);
			}
		}
	}

	@Override
	public int[] getIndexes(String form) {
		form = StringUtils.normalize(form, getOptions().getNormalize());
		return index_map_.get(form);
	}

	public int size() {
		return index_map_.size();
	}

}
