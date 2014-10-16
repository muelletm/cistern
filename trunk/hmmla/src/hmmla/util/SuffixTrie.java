// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.util;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SuffixTrie implements Serializable {

	private static final long serialVersionUID = 1L;
	private Character c_;
	private Map<Character, SuffixTrie> child_map_;
	private Map<String, Integer> tag_map_;
	private Integer total_count_;
	private Double entropy_;

	public SuffixTrie(Character c) {
		c_ = c;
		child_map_ = new TreeMap<Character, SuffixTrie>();
		tag_map_ = new TreeMap<String, Integer>();
	}

	public void add(String form, int limit, String tag) {
		add(form, form.length() - 1, limit, tag);
	}

	public void add(String form, int index, int limit, String tag) {
		Integer count = tag_map_.get(tag);

		if (count == null) {
			count = 0;
		}

		tag_map_.put(tag, count + 1);

		if (index >= limit) {
			char c = form.charAt(index);

			SuffixTrie trie = child_map_.get(c);

			if (trie == null) {
				trie = new SuffixTrie(c);
				child_map_.put(c, trie);
			}

			trie.add(form, index - 1, limit, tag);
		}
	}

	public boolean prune(double entropy_threshold, int count_threshold) {
		if (totalCount() < count_threshold) {
			return true;
		}

		if (entropy() < entropy_threshold) {
			child_map_.clear();
			return false;
		}

		Map<Character, SuffixTrie> new_child_map = new TreeMap<Character, SuffixTrie>();
		for (SuffixTrie trie : child_map_.values()) {
			if (!trie.prune(entropy_threshold, count_threshold)) {
				new_child_map.put(trie.c_, trie);
			}
		}
		child_map_ = new_child_map;
		return child_map_.size() == 0;
	}

	private int totalCount() {
		if (total_count_ != null) {
			return total_count_;
		}
		calcTotalCount();
		return total_count_;
	}

	private void calcTotalCount() {
		total_count_ = 0;
		for (Map.Entry<String, Integer> tag_entry : tag_map_.entrySet()) {
			int count = tag_entry.getValue();
			total_count_ += count;
		}
	}

	private double entropy() {
		if (entropy_ != null) {
			return entropy_;
		}

		calcEntropy();
		return entropy_;
	}

	private void calcEntropy() {
		int total_count = totalCount();
		entropy_ = 0.;
		for (Map.Entry<String, Integer> tag_entry : tag_map_.entrySet()) {
			int count = tag_entry.getValue();
			double prob = count / (double) total_count;
			entropy_ -= prob * Math.log(prob);
		}
	}

	private void extractSuffixes(String suffix, List<String> suffixes) {
		
		if (c_ != null) {
			suffix = c_ + suffix;
		}
		
		if (child_map_.size() == 0) {
			suffixes.add(suffix);
			return;
		}	
		
		for (SuffixTrie trie : child_map_.values()) {
			trie.extractSuffixes(suffix, suffixes);
		}
	}
	
	public List<String> extractSuffixes() {
		List<String> suffixes = new LinkedList<String>();
		extractSuffixes("", suffixes);
		return suffixes;
	}
	
	public String getSuffix(String form) {
		return getSuffix(form, form.length() - 1);
	}

	private String getSuffix(String form, int index) {
		char c = form.charAt(index);
		SuffixTrie child = child_map_.get(c);
		if (child == null) {
			return null;
		}
		
		// SuffixTrie is leaf!
		if (child.child_map_.size() == 0) {
			return form.substring(index, form.length());
		}
		
		// form is to short!
		if (index == 0) {
			return null;
		}
		
		return child.getSuffix(form, index - 1);
	} 
	
	public void clean() {
		total_count_ = null;
		entropy_ = null;
		tag_map_ = null;
		for (SuffixTrie trie : child_map_.values()) {
			trie.clean();
		}
	}

}
