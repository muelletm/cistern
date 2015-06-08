package marmot.analyzer;

import marmot.morph.Word;

public class AnalyzerReading {

	private String pos_tag_;
	private String morph_tag_;
	private String lemma_;
	
	public AnalyzerReading(String pos_tag, String morph_tag, String lemma) {
		pos_tag_ = pos_tag;
		morph_tag_ = morph_tag;
		lemma_ = lemma;
	}
	
	public AnalyzerReading(Word word) {
		this(word.getPosTag(), word.getPosTag(), word.getLemma());
	}

	@Override
	public String toString() {
		return String.format("%s %s %s", pos_tag_, morph_tag_, lemma_);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lemma_ == null) ? 0 : lemma_.hashCode());
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
		AnalyzerReading other = (AnalyzerReading) obj;
		if (lemma_ == null) {
			if (other.lemma_ != null)
				return false;
		} else if (!lemma_.equals(other.lemma_))
			return false;
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

}
