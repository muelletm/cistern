package marmot.lemma;

import java.util.List;

public class Instance {

	String form_;
	String lemma_;
	List<Integer> alignment_;
	String tag_;
	String mtag_;
	
	public Instance(String form, String lemma, List<Integer> alignment, String tag, String mtag) {
		form_ = form;
		lemma_ = lemma;
		alignment_ = alignment;
		tag_ = tag;
		mtag_ = mtag;
	}

	public String getForm() {
		return form_;
	}
	
	public String getLemma() {
		return lemma_;
	}

	public List<Integer> getAlignment() {
		return alignment_;
	}

	
}
