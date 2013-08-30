// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Cache<K, V> {
	protected int current_time_;
	protected Map<K, CacheEntry> storage_;
	
	class CacheEntry {
		protected V value_;
		protected int time_;		
		
		public CacheEntry(V value, int time) {
			time_ = time; 
			value_ = value;
		}
		
		public void setTime(int time) {
			time_ = time;
		}
		public V getValue() {
			return value_;
		}

		public int getTime() {
			return time_;
		}
	}
	
	public Cache() {
		current_time_ = 0;
		storage_ = new HashMap<K, CacheEntry>();
	}
	
	public void put(K key, V value) {
		CacheEntry entry = new CacheEntry(value, current_time_);		
		storage_.put(key, entry);
	}
	
	public V get(K key) {
		current_time_ += 1;
		CacheEntry entry = storage_.get(key);
		
		if (entry == null) {
			return null;
		}
		
		entry.setTime(current_time_);
		return entry.getValue();
	}
	
	public void cleanup(int delta) {
		List<K> list = new LinkedList<K>();
		
		for (Entry<K, CacheEntry> entry : storage_.entrySet()) {
			CacheEntry centry = entry.getValue();
			if (current_time_ - centry.getTime() > delta) {
				list.add(entry.getKey());
			}
		}
		
		for (K key : list) {
			storage_.remove(key);
		}
	}
	
}
