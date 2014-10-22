// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph;

import java.util.List;
import java.util.Set;

import marmot.util.CollectableSet;
import marmot.util.LineIterator;
import marmot.util.StringUtils;
import marmot.util.SymbolTable;
import marmot.util.Trie;

public class SuffixDictionary extends HashDictionary {

	private static final long serialVersionUID = 1L;
	private Trie<CollectableSet> trie_;

	public SuffixDictionary() {
		this(null);
	}

	public SuffixDictionary(SymbolTable<String> table) {
		super(table);
	}

	@Override
	public int[] getIndexes(String word_form) {
		int[] indexes = super.getIndexes(word_form);

		if (indexes == null) {
			
			if (getOptions().getNormalize()) {
				word_form = StringUtils.normalize(word_form, true);
			}
			
			if (getOptions().getReverse()) {
				word_form = StringUtils.reverse(word_form);
			}
		
			CollectableSet cset = trie_.getContent(word_form);
			
			if (cset != null) {
				Set<Object> set = cset.getValue();
				indexes = toArray(set);
			}
		}
		
		return indexes;
	}

	private int[] toArray(Set<Object> set) {
		int[] array = new int[set.size()];
		int i = 0;
		for (Object o : set) {
			array[i++] = (Integer) o; 
		}
		return array;
	}

	@Override
	public void init(MorphDictionaryOptions options) {
		super.init(options);

		LineIterator iterator = new LineIterator(options.getFilename());
		trie_ = new Trie<CollectableSet>();
		int[] row_indexes = options.getIndexes();

		SymbolTable<String> table = getTable();
		
		while (iterator.hasNext()) {
			List<String> line = iterator.next();

			if (!line.isEmpty()) {
				String form = line.get(0);

				if (options.getNormalize())
					form = StringUtils.normalize(form, true);
				
				if (options.getReverse()) {
					form = StringUtils.reverse(form);
				}

				Trie<CollectableSet> trie = trie_.addWord(form, null);
				
				for (int row_index : row_indexes) {
					if (row_index > 0 && row_index < line.size()) {
						String tag = line.get(row_index);
						int index = table.toIndex(tag, true);
						trie.add(new CollectableSet(index));
					}
				}
			}
		}
		
		trie_.propagateContent(options.getLimit());		
		
	}

}
