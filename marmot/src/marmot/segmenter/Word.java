package marmot.segmenter;

import java.util.Collection;
import java.util.LinkedList;

public class Word {

	private String word_;
	private Collection<SegmentationReading> readings_;
	
	public Word(String word) {
		word_ = word;
		readings_ = new LinkedList<>();
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
