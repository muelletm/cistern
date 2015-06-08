// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.lemma.toutanova;

import java.util.List;

import marmot.lemma.LemmaInstance;

public class ToutanovaInstance {

	private LemmaInstance instance_;
	private List<Integer> alignment_;
	private int[] form_char_indexes_;
	private Result result_;
	private int pos_tag_index_;
	private boolean rare_;
	
	public ToutanovaInstance(LemmaInstance instance, List<Integer> alignment) {
		instance_ = instance;
		alignment_ = alignment;
		rare_ = false;
	}
	
	LemmaInstance getInstance() {
		return instance_;
	}
	
	List<Integer> getAlignment() {
		return alignment_;
	}

	public int[] getFormCharIndexes() {
		return form_char_indexes_;
	}

	public void setFormCharIndexes(int[] char_indexes) {
		form_char_indexes_ = char_indexes;
	}

	public Result getResult() {
		return result_;
	}

	public void setResult(Result result) {
		result_ = result;
	}

	public void setPosTagIndex(int index) {
		pos_tag_index_ = index;
	}

	public int getPosTagIndex() {
		return pos_tag_index_;
	}

	public void setRare(boolean instance_is_rare) {
		rare_ = instance_is_rare;
	}
	
	public boolean isRare() {
		return rare_;
	}
	
}
