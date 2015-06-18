package marmot.analyzer;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import marmot.morph.Word;

public class AnalyzerReading {

	private String lemma_;
	private AnalyzerTag tag_;
	
	public AnalyzerReading(AnalyzerTag tag, String lemma) {
		tag_ = tag;
		lemma_ = lemma;
	}
	
	public AnalyzerReading(Word word) {
		this(new AnalyzerTag(word.getPosTag(), word.getMorphTag()), word.getLemma());
	}

	@Override
	public String toString() {
		if (lemma_ == null) {
			return tag_.toString();
		}
		
		return String.format("%s %s", tag_, lemma_);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lemma_ == null) ? 0 : lemma_.hashCode());
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
		AnalyzerReading other = (AnalyzerReading) obj;
		if (lemma_ == null) {
			if (other.lemma_ != null)
				return false;
		} else if (!lemma_.equals(other.lemma_))
			return false;
		if (tag_ == null) {
			if (other.tag_ != null)
				return false;
		} else if (!tag_.equals(other.tag_))
			return false;
		return true;
	}
	
	public static Collection<AnalyzerTag> toTags(Collection<AnalyzerReading> readings) {
		Set<AnalyzerTag> tags = new HashSet<>();
		for (AnalyzerReading reading : readings) {
			tags.add(reading.getTag());
		}
		return new LinkedList<>(tags);
	}

	public AnalyzerTag getTag() {
		return tag_;
	}

}
