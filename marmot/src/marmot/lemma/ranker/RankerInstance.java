package marmot.lemma.ranker;

import java.util.LinkedList;
import java.util.List;

import marmot.lemma.Instance;
import marmot.lemma.LemmaCandidateGenerator;
import marmot.lemma.LemmaCandidateSet;
import marmot.util.Converter;
import marmot.util.SymbolTable;

public class RankerInstance {

	private static final int[] EMPTY_ARRAY = {};

	private Instance instance_;
	private LemmaCandidateSet set_;
	private int[] form_chars_;
	private int pos_index_;
	private int[] morph_indexes_;

	public RankerInstance(Instance instance, LemmaCandidateSet set) {
		instance_ = instance;
		set_ = set;
		form_chars_ = null;
		pos_index_ = -2;
		morph_indexes_ = null;
	}

	public Instance getInstance() {
		return instance_;
	}

	public LemmaCandidateSet getCandidateSet() {
		return set_;
	}

	public int[] getFormChars(SymbolTable<Character> char_table_, boolean insert) {
		if (form_chars_ == null) {
			String form = instance_.getForm();
			form_chars_ = new int[form.length()];
			for (int i = 0; i < form.length(); i++) {
				int c = char_table_.toIndex(form.charAt(i), -1, insert);
				form_chars_[i] = c;
			}
		}
		return form_chars_;
	}

	public int getPosIndex(SymbolTable<String> pos_table, boolean insert) {
		if (pos_table == null)
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

	public static List<RankerInstance> getInstances(List<Instance> instances,
			List<LemmaCandidateGenerator> generators) {
		List<RankerInstance> rinstances = new LinkedList<>();
		for (Instance instance : instances) {
			rinstances.add(getInstance(instance, generators));	
		}
		return rinstances;
	}

	public static RankerInstance getInstance(Instance instance,
			List<LemmaCandidateGenerator> generators) {
		LemmaCandidateSet set = new LemmaCandidateSet(instance.getForm());
		for (LemmaCandidateGenerator generator : generators) {
			generator.addCandidates(instance, set);
		}
		set.getCandidate(instance.getLemma());
		return new RankerInstance(instance, set);
	}

}
