// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package lemming.lemma.ranker;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import lemming.lemma.LemmaCandidateGenerator;
import lemming.lemma.LemmaCandidateSet;
import lemming.lemma.LemmaInstance;
import marmot.util.Converter;
import marmot.util.FeatUtil;
import marmot.util.SymbolTable;

public class RankerInstance {

	public static final int[] EMPTY_ARRAY = {};

	private LemmaInstance instance_;
	private LemmaCandidateSet set_;
	private short[] form_chars_;
	private int pos_index_;
	private int[] morph_indexes_;

	public RankerInstance(LemmaInstance instance, LemmaCandidateSet set) {
		instance_ = instance;
		set_ = set;
		form_chars_ = null;
		pos_index_ = -2;
		morph_indexes_ = null;
	}

	public LemmaInstance getInstance() {
		return instance_;
	}

	public LemmaCandidateSet getCandidateSet() {
		return set_;
	}

	public short[] getFormChars(SymbolTable<Character> char_table_, boolean insert) {
		if (form_chars_ == null) {
			form_chars_ = FeatUtil.getCharIndexes(instance_.getForm(), char_table_, insert);
		}
		return form_chars_;
	}

	public int getPosIndex(SymbolTable<String> pos_table, boolean insert) {
		if (pos_table == null || instance_.getPosTag() == null)
			return -1;

		if (pos_index_ == -2) {
			pos_index_ = pos_table.toIndex(instance_.getPosTag(), -1, insert);
		}

		return pos_index_;
	}

	public int[] getMorphIndexes(SymbolTable<String> morph_table, boolean insert) {
		if (morph_table == null) {
			return EMPTY_ARRAY;
		}

		if (morph_indexes_ == null) {

			List<Integer> list = new LinkedList<>();
			setMorphFeatures(morph_table, instance_.getMorphTag(), insert, list);

			if (list.isEmpty())
				morph_indexes_ = EMPTY_ARRAY;
			else
				morph_indexes_ = Converter.toIntArray(list);
		}

		return morph_indexes_;
	}

	private void setMorphFeatures(SymbolTable<String> morph_table,
			String morphtag, boolean insert, List<Integer> list) {
		if (morphtag == null || morph_table == null || morphtag.equals('_')) {
			return;
		}

		for (String feat : morphtag.split("\\|")) {
			int index = morph_table.toIndex(feat, -1, insert);
			if (index >= 0 && list != null) {
				list.add(index);
			}
		}
	}

	public static List<RankerInstance> getInstances(List<LemmaInstance> instances,
			List<LemmaCandidateGenerator> generators) {
		List<RankerInstance> rinstances = new LinkedList<>();
		for (LemmaInstance instance : instances) {
			rinstances.add(getInstance(instance, generators));	
		}
		return rinstances;
	}

	public static RankerInstance getInstance(LemmaInstance instance,
			List<LemmaCandidateGenerator> generators) {
		LemmaCandidateSet set = new LemmaCandidateSet();
		for (LemmaCandidateGenerator generator : generators) {
			generator.addCandidates(instance, set);
		}	
		return new RankerInstance(instance, set);
	}

	@Override
	public String toString() {
		return "RankerInstance [instance=" + instance_ + ", set_=" + set_
				+ ", form_chars=" + Arrays.toString(form_chars_)
				+ ", pos_index=" + pos_index_ + ", morph_indexes="
				+ Arrays.toString(morph_indexes_) + "]";
	}

	public void setCandidateSet(LemmaCandidateSet set) {
		set_ = set;
	}
	
}
