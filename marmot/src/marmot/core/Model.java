// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import marmot.util.SymbolTable;

public abstract class Model implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String BORDER_SYMBOL_ = "<STOP>";
	public static final String EMPTY_SYMBOL_ = "<EMPTY>";

	private Options options_;

	private List<SymbolTable<String>> tag_tables_;
	private SymbolTable<String> catgegory_table_;

	private int order_;
	private int boundary_index_;

	public State getBoundaryState(int level) {
		State state = new State(boundary_index_);
		for (int clevel = 0; clevel < level; clevel++) {
			state = new State(boundary_index_, state);
		}
		return state;
	}
	
	public void init(Options options, SymbolTable<String> category_tables) {
		options_ = options;
		order_ = options.getOrder();
		catgegory_table_ = category_tables;
		
		tag_tables_ = new ArrayList<SymbolTable<String>>(
				catgegory_table_.size());
		for (int index = 0; index < catgegory_table_.size(); index++) {
			SymbolTable<String> tag_table = new SymbolTable<String>(true);
			tag_table.toIndex(BORDER_SYMBOL_, true);
			tag_tables_.add(tag_table);
		}
		
		boundary_index_ = 0;
	}
	
	public int getBoundaryIndex() {
		return boundary_index_;
	}
	
	public List<SymbolTable<String>> getTagTables() {
		return tag_tables_;
	}

	public int getOrder() {
		return order_;
	}
	
	public Options getOptions() {
		return options_;
	}
	
	public SymbolTable<String> getCategoryTable() {
		return catgegory_table_;
	}
	
	abstract public int[] getTagCandidates(Sequence sequence, int index, State state);

	public abstract void setLemmaCandidates(Token token, State state);

	public abstract void setLemmaCandidates(State state);
	
}
