// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph;

import java.io.Serializable;

import marmot.util.SymbolTable;



public abstract class MorphDictionary implements Serializable {

	private static final long serialVersionUID = 1L;
	private SymbolTable<String> table_;
	private MorphDictionaryOptions options_;
	
	public MorphDictionary(SymbolTable<String> table) {
		table_ = table;
		if (table_ == null) {
			table_ = new SymbolTable<>(true);
		}
	}
	
	public MorphDictionaryOptions getOptions() {
		return options_;
	}
	
	public int numTags() {
		return table_.size();
	}
	
	public SymbolTable<String> getTable() {
		return table_;
	}
	
	abstract public int[] getIndexes(String word_form);
	
	public static MorphDictionary create(MorphDictionaryOptions options) {
		MorphDictionary dict;
		
		switch (options.getDictType()) {
		case hash:
			dict = new HashDictionary();
			break;
		case suffix:
			dict = new SuffixDictionary();
			break;
		default:
			throw new UnsupportedOperationException();
		}
		
		dict.init(options);
		return dict;
	}
	
	public static MorphDictionary create(String mdict_file) {
		return create(MorphDictionaryOptions.parse(mdict_file));
	}
	
	public void init(MorphDictionaryOptions options) {
		options_ = options;
	}
	
	public String getName(int i) {
		String symbol = getTable().toSymbol(i);
		return symbol;
	}
	
	public void addWordsFromFile(String morph_dict) {
		throw new RuntimeException("Not Implemented");
	}

	
}
