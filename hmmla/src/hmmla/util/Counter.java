// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class Counter<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	Map<T,Double> storage;
	Double totalCount;
	Double defaultValue;
	
	public Set<Entry<T,Double>> entrySet(){
		return storage.entrySet();
	}
	
	public Counter(Double defaultValue, int initialCapacity){
		storage = new HashMap<T,Double>(initialCapacity);
		this.defaultValue = defaultValue;
		totalCount = 0.;
	}

	public Counter(Double defaultValue){
		this(defaultValue, 10);
	}
	
	public Counter(){
		this(0.);	
	}

	public void set(T item,Double freq){
		if (freq != defaultValue){
		storage.put(item, freq);
		}else{
			storage.remove(item);
		}
	}
	
	public void increment(T item,Double freq){
		if (!storage.containsKey(item)){
			storage.put(item, freq + defaultValue);
		}
		else{
			storage.put(item, freq + storage.get(item));
		}
		
		Double newFreq = storage.get(item);
		
		if (newFreq == defaultValue){
			storage.remove(item);
		}

		totalCount += freq;
	}
	
	public Double count(T item){
		if (!storage.containsKey(item)){
			return defaultValue;
		}
		else{
			return storage.get(item);
		}
	}
	
	public Double totalCount(){
		return totalCount;
	}
	
	public int size(){
		return storage.size();
	}
	
	public List<Entry<T,Double>> byFrequency(){
		List<Entry<T,Double>> entries = new ArrayList<Entry<T,Double>>(storage.entrySet());
		Collections.sort(entries, new Comparator<Entry<T,Double>> () {
			
			public int compare(Entry<T,Double> e1, Entry<T,Double> e2) {
				double o1 = e1.getValue();
				double o2 = e2.getValue();
		        return (o1>o2 ? -1 : (o1==o2 ? 0 : 1));
		    }
			
		});
		return entries;
	}
	
	public String toString(){
		return storage.toString();
	}
	
}

