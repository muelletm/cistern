package marmot.lemma;

public class Instance {

	double count_;
	String form_;
	String lemma_;
	String tag_;
	String mtag_;
	
	public Instance(String form, String lemma, String tag, String mtag) {
		count_ = 1;
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
	
	public String getFormPadded() {
		return "123" + form_ + "456";
	}
	
	public String getLemmaPadded() {
		return lemma_;
	}

	
	public String getPosTag() {
		return tag_;
	}
	
	public void setCount(double count) {
		count_ = count;
	}
	
	public double getCount() {
		return count_;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((form_ == null) ? 0 : form_.hashCode());
		result = prime * result + ((lemma_ == null) ? 0 : lemma_.hashCode());
		result = prime * result + ((mtag_ == null) ? 0 : mtag_.hashCode());
		result = prime * result + ((tag_ == null) ? 0 : tag_.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Instance other = (Instance) obj;
		if (form_ == null) {
			if (other.form_ != null)
				return false;
		} else if (!form_.equals(other.form_))
			return false;
		if (lemma_ == null) {
			if (other.lemma_ != null)
				return false;
		} else if (!lemma_.equals(other.lemma_))
			return false;
		if (mtag_ == null) {
			if (other.mtag_ != null)
				return false;
		} else if (!mtag_.equals(other.mtag_))
			return false;
		if (tag_ == null) {
			if (other.tag_ != null)
				return false;
		} else if (!tag_.equals(other.tag_))
			return false;
		return true;
	}
	
	

}
