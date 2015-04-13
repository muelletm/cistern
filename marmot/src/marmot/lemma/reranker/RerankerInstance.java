package marmot.lemma.reranker;

import marmot.lemma.Instance;
import marmot.lemma.LemmaCandidateSet;
import marmot.util.SymbolTable;

public class RerankerInstance {

	private Instance instance_;
	private LemmaCandidateSet set_;
	private int[] form_chars_;

	public RerankerInstance(Instance instance, LemmaCandidateSet set) {
		instance_ = instance;
		set_ = set;
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

}
