package marmot.analyzer;

public class AnalyzerTag {

	private String pos_tag_;
	private String morph_tag_;
	
	public AnalyzerTag(String pos_tag, String morph_tag) {
		pos_tag_ = pos_tag;
		morph_tag_ = morph_tag;
	}
	
	@Override
	public String toString() {
		return String.format("%s %s", pos_tag_, morph_tag_);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((morph_tag_ == null) ? 0 : morph_tag_.hashCode());
		result = prime * result
				+ ((pos_tag_ == null) ? 0 : pos_tag_.hashCode());
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
		AnalyzerTag other = (AnalyzerTag) obj;
		if (morph_tag_ == null) {
			if (other.morph_tag_ != null)
				return false;
		} else if (!morph_tag_.equals(other.morph_tag_))
			return false;
		if (pos_tag_ == null) {
			if (other.pos_tag_ != null)
				return false;
		} else if (!pos_tag_.equals(other.pos_tag_))
			return false;
		return true;
	}

	public String getPosTag() {
		return pos_tag_;
	}

	public String getMorphTag() {
		return morph_tag_;
	}
	
}
