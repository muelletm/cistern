package marmot.segmenter;

import java.util.Collection;
import java.util.HashSet;

public class Word {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((readings_ == null) ? 0 : readings_.hashCode());
		result = prime * result + ((word_ == null) ? 0 : word_.hashCode());
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
		Word other = (Word) obj;
		if (readings_ == null) {
			if (other.readings_ != null)
				return false;
		} else if (!readings_.equals(other.readings_))
			return false;
		if (word_ == null) {
			if (other.word_ != null)
				return false;
		} else if (!word_.equals(other.word_))
			return false;
		return true;
	}

	private String word_;
	private Collection<SegmentationReading> readings_;
	
	public Word(String word) {
		word_ = word;
		readings_ = new HashSet<>();
	}

	public int getLength() {
		return word_.length();
	}

	public String getWord() {
		return word_;
	}

	@Override
	public String toString() {
		return "[" + word_ + " " + readings_ + "]";
	}

	public void add(SegmentationReading reading) {
		readings_.add(reading);
	}

	public Collection<SegmentationReading> getReadings() {
		return readings_;
	}
	
}
