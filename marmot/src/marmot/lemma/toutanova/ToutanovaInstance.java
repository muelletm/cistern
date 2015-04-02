package marmot.lemma.toutanova;

import java.util.List;

import marmot.lemma.Instance;

public class ToutanovaInstance {

	private Instance instance_;
	private List<Integer> alignment_;
	private int[] form_segments_;
	private int[] lemma_segments_;
	private int[] form_char_indexes_;
	
	public ToutanovaInstance(Instance instance, List<Integer> alignment) {
		instance_ = instance;
		alignment_ = alignment;
	}
	
	Instance getInstance() {
		return instance_;
	}
	
	List<Integer> getAlignment() {
		return alignment_;
	}

	public void setFormSegments(int[] form_segments) {
		form_segments_ = form_segments;
	}

	public void setLemmaSegments(int[] lemma_segments) {
		lemma_segments_ = lemma_segments; 
	}

	public int[] getFormCharIndexes() {
		return form_char_indexes_;
	}

	public void setFormCharIndexes(int[] char_indexes) {
		form_char_indexes_ = char_indexes;
	}
	
}
