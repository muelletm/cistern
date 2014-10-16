// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

public class SymbolTable<T> implements Serializable {
		
	private static final long serialVersionUID = 1L;
	Map<T,Integer> toIndex;
	Map<Integer,T> fromIndex;
		
	public SymbolTable(){
		toIndex = new HashMap<T,Integer>();
		fromIndex = new HashMap<Integer,T>();
	}
	
	public SymbolTable(SymbolTable<T> symbol_table) {
		toIndex = new HashMap<T, Integer>((HashMap<T, Integer>)symbol_table.toIndex);
		fromIndex = new HashMap<Integer, T>((HashMap<Integer, T>)symbol_table.fromIndex);
	}

	public List<Integer> toIndexes (Collection<T> symbols) {
		List<Integer> indexes = new ArrayList<Integer>(symbols.size());
		for (T symbol : symbols){
			indexes.add(toIndex(symbol));
		}
		return indexes;
	}
	
	public List<T> toSymbols(Collection<Integer> indexes){
		List<T> symbols = new ArrayList<T>(indexes.size());
		for (Integer i : indexes){
			symbols.add(toSymbol(i));
		}
		return symbols;
	}
	
	public Integer toIndex(T symbol) {
		return toIndex(symbol, false);
	}
	
	public Integer toIndex(T symbol,boolean insert) {
		Integer index = toIndex.get(symbol);
		if (index == null){
			if (insert){
				index = toIndex.size();
				toIndex.put(symbol, index);
				fromIndex.put(index,symbol);
			}
			else{
				throw new NoSuchElementException(symbol.toString());
			}
		}
		return index;
	}
	
	public T toSymbol(Integer index){
		T t = fromIndex.get(index);
		if (t == null){
			throw new NoSuchElementException();
		}
		return t;
	}
	
	public Integer size(){
		assert toIndex.size() == fromIndex.size();
		return toIndex.size();
	} 
	
	public String toString(){
		return toIndex.toString();
	}

	public boolean hasSymbol(T object) {
		return toIndex.containsKey(object);
	}
	
	public Set<Entry<T,Integer>> entrySet(){
		return toIndex.entrySet();
	}

	public Collection<T> getSymbols() {
		return toIndex.keySet();
	}
	
	public void resize(int size){
		
		if (size >= this.size()){
			return;
		}
		
		HashMap<T,Integer> toIndex2 = new HashMap<T,Integer>();
		HashMap<Integer,T> fromIndex2 = new HashMap<Integer,T>();
		
		for (int i=0;i<size;i++){
			T t = fromIndex.get(i);
			toIndex2.put(t, i);
			fromIndex2.put(i, t);
		}
		
		toIndex = toIndex2;
		fromIndex = fromIndex2;
	}
	
}
