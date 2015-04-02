package marmot.lemma;

import java.util.List;

public class Instance {

	String form_;
	String lemma_;
	String tag_;
	String mtag_;
	
	public Instance(String form, String lemma, String tag, String mtag) {
		form_ = form;
		lemma_ = lemma;
		tag_ = tag;
		mtag_ = mtag;
	}

	public String getForm() {
		return form_;
	}
	
	public String getLemma() {
		return lemma_;
	}

}
