// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class Counter<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	private Map<T, Double> storage_;
	private Double total_count_;
	private Double default_value_;

	public Set<Entry<T, Double>> entrySet() {
		return storage_.entrySet();
	}

	public Counter(Double defaultValue, int initialCapacity) {
		storage_ = new HashMap<T, Double>(initialCapacity);
		this.default_value_ = defaultValue;
		total_count_ = 0.;
	}

	public Counter(Double defaultValue) {
		this(defaultValue, 10);
	}

	public Counter() {
		this(0.);
	}

	public void set(T item, Double freq) {
		if (freq != default_value_) {
			storage_.put(item, freq);
		} else {
			storage_.remove(item);
		}
	}

	public void increment(T item, Double freq) {
		if (!storage_.containsKey(item)) {
			storage_.put(item, freq + default_value_);
		} else {
			storage_.put(item, freq + storage_.get(item));
		}

		Double newFreq = storage_.get(item);

		if (newFreq == default_value_) {
			storage_.remove(item);
		}

		total_count_ += freq;
	}

	public Double count(T item) {
		if (!storage_.containsKey(item)) {
			return default_value_;
		} else {
			return storage_.get(item);
		}
	}

	public Double totalCount() {
		return total_count_;
	}

	public int size() {
		return storage_.size();
	}

	public String toString() {
		return storage_.toString();
	}

	public Collection<Double> counts() {
		return storage_.values();
	}

	public void clear() {
		storage_.clear();
	}

	public Set<T> keySet() {
		return storage_.keySet();
	}

	public List<Entry<T, Double>> sortedEntries() {
		List<Entry<T, Double>> list = new ArrayList<Entry<T, Double>>(
				storage_.entrySet());

		Collections.sort(list, new Comparator<Entry<T, Double>>() {

			@Override
			public int compare(Entry<T, Double> o1, Entry<T, Double> o2) {
				return -Double.compare(o1.getValue(), o2.getValue());
			}
		});
		
		return list;
	}

	public List<Entry<T, Double>> sortedEntries(int length) {
		List<Entry<T, Double>> list = sortedEntries();

		if (list.size() > length) {
			list = list.subList(0, length);
		}
		
		return list;	
	}

	public T max() {
		Map.Entry<T, Double> max_entry = null;
		
		for (Map.Entry<T, Double> entry : storage_.entrySet()) {
			if (max_entry == null || max_entry.getValue() < entry.getValue()) {
				max_entry = entry;
			}
		}
		
		return max_entry.getKey();
	}

}
