// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

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
	private Map<T, Integer> toIndex;
	private Map<Integer, T> fromIndex;
	private boolean bidirectional_;

	public SymbolTable(boolean bidirectional, int capacity) {
		toIndex = new HashMap<T, Integer>(capacity);
		if (bidirectional) {
			fromIndex = new HashMap<Integer, T>(capacity);
		}
		bidirectional_ = bidirectional;
	}

	public SymbolTable(int capacity) {
		this(false, capacity);
	}
	
	public SymbolTable() {
		this(false, 10);
	}

	public SymbolTable(SymbolTable<T> symbol_table) {
		toIndex = new HashMap<T, Integer>(
				(HashMap<T, Integer>) symbol_table.toIndex);
		bidirectional_ = symbol_table.bidirectional_;
		if (bidirectional_) {
			fromIndex = new HashMap<Integer, T>(
					(HashMap<Integer, T>) symbol_table.fromIndex);
		}
	}

	public SymbolTable(boolean bidirectional) {
		this(bidirectional, 10);
	}

	public List<Integer> toIndexes(Collection<T> symbols) {
		List<Integer> indexes = new ArrayList<Integer>(symbols.size());
		for (T symbol : symbols) {
			indexes.add(toIndex(symbol));
		}
		return indexes;
	}

	public List<T> toSymbols(Collection<Integer> indexes) {		
		List<T> symbols = new ArrayList<T>(indexes.size());
		for (Integer i : indexes) {
			symbols.add(toSymbol(i));
		}
		return symbols;
	}

	public int toIndex(T symbol) {
		return toIndex(symbol, false);
	}
	
	public int toIndex(T symbol, int default_index) {
		return toIndex(symbol, default_index, false);
	}
	
	public int toIndex(T symbol, int default_index, boolean insert) {
		if (symbol == null)
			throw new NullPointerException();
		
		Integer index = toIndex.get(symbol);
		if (index == null) {
			if (insert) {
				index = toIndex.size();
				toIndex.put(symbol, index);
				if (bidirectional_) {
					fromIndex.put(index, symbol);
				}
			} else {
				return default_index;
			}
		}
		return index;
	}

	public int toIndex(T symbol, boolean insert) {
		int index = toIndex(symbol, -1, insert);
		if (index == -1) {
			throw new NoSuchElementException(symbol.toString());
		}
		return index;
	}

	public T toSymbol(Integer index) {
		if (!bidirectional_) {
			throw new UnsupportedOperationException("Table is unidirectional!");
		}
		
		T t = fromIndex.get(index);
		if (t == null) {
			throw new NoSuchElementException();
		}
		return t;
	}

	public int size() {
		assert (!bidirectional_) || (toIndex.size() == fromIndex.size());
		return toIndex.size();
	}

	public String toString() {
		return toIndex.toString();
	}

	public boolean hasSymbol(T object) {
		return toIndex.containsKey(object);
	}

	public Set<Entry<T, Integer>> entrySet() {
		return toIndex.entrySet();
	}

	public Collection<T> getSymbols() {
		return toIndex.keySet();
	}

	public void setBidirectional(boolean bidirectional) {
		if (bidirectional_ != bidirectional) {
			
			if (!bidirectional_) {
				fromIndex = new HashMap<Integer, T>((int)(toIndex.size() * 1.25));
				for (Map.Entry<T, Integer> entry : toIndex.entrySet()) {
					fromIndex.put(entry.getValue(), entry.getKey());
				}
				bidirectional_ = true;
			} else {
				bidirectional_ = false;
				fromIndex = null;
			}
			
		}
		
	}

	public boolean isBidirectional() {
		return bidirectional_;
	}

	public void insert(T symbol) {
		toIndex(symbol, true);
	}

	public Set<T> keySet() {
		return toIndex.keySet();
	}

}
