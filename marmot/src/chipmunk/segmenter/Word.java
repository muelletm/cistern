package chipmunk.segmenter;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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
	
	static public void printStats(List<Word> words) {
		int word_length = 0;
		int very_long_words = 0;
		int max_word_length = 0;
		int max_segment_length = 0;
		int segment_length = 0;
		int num_segments = 0;
		int num_words = 0;
		int num_readings = 0;
		
		int words_with_many_readings = 0;
		
		for (Word word : words) {
			
			max_word_length = Math.max(max_segment_length, word.getLength());
			
			word_length += word.getLength();
			
			if (word.getLength() > 15) {
				very_long_words ++;
			}
			
			for (SegmentationReading reading : word.getReadings()) {				
				for (String segment : reading.getSegments()) {
					
					max_segment_length = Math.max(max_segment_length, segment.length());
					
					num_segments ++;
					segment_length += segment.length();
				}
				num_readings ++;	
			}
			
			if (word.getReadings().size() > 3) {
				words_with_many_readings ++;
			}
			
			num_words ++;
		}
		System.err.format("Avg word length: %d/%d = %g\n", word_length, num_words, word_length * 1.0 / num_words);
		System.err.format("Num very long words (> 15): %d\n", very_long_words);
		System.err.format("Max word length: %d\n", max_word_length);
		System.err.format("Avg segment length: %d/%d = %g\n", segment_length, num_segments, segment_length * 1.0 / num_segments);
		System.err.format("Max segment length: %d\n", max_segment_length);
		System.err.format("Segments / reading: %d/%d = %g\n", num_segments, num_readings, num_segments * 1.0 / num_readings);
		System.err.format("Readings / word: %d/%d = %g\n", num_readings, num_words, num_readings * 1.0 / num_words);
		System.err.format("Words with many readings (> 3): %d\n", words_with_many_readings);
	}
	
}
