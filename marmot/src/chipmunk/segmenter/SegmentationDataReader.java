package chipmunk.segmenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marmot.util.LineIterator;

public class SegmentationDataReader {

	private List<Word> data_;
	private int tag_level_;
	private StringNormalizer normalizer_;
	private Map<String, Word> vocab_;
	
	public SegmentationDataReader(String filepath, String lang, int tag_level) {
		data_ = new ArrayList<Word>();		
		normalizer_ = StringNormalizer.labeledCreate(lang);
		tag_level_ = tag_level;
		readFile(filepath, data_);
	}
	
	public List<Word> map(List<Word> words) {
		List<Word> new_words = new LinkedList<>();
		for (Word word : words) {
			Word new_word = vocab_.get(word.getWord());
			assert new_word != null;
			
//			if (!new_word.equals(word)) {
//				System.err.println("diff:\n" + word + "\n" + new_word );
//			}
			
			new_words.add(new_word);
		}
		return new_words;
	}
	
	private void readFile(String fileIn, List<Word> words) {
		
		vocab_ = new HashMap<>();
		
		LineIterator iterator = new LineIterator(fileIn, "\t");
		
		while(iterator.hasNext()) {
			
			List<String> line = iterator.next();
			assert line.size() == 2;
			
			if (line.size() > 0) {
				String word_string = line.get(0);
				
				word_string = normalizer_.normalize(word_string);
				
				Word word = vocab_.get(word_string);
				if (word == null) {
					word = new Word(word_string);
					words.add(word);
					vocab_.put(word_string, word);
				}
				
				String[] word_readings = line.get(1).split(", "); 
				
				for (String word_reading : word_readings) {
					
					String[] readings = word_reading.split(" ");
					List<String> segments = new LinkedList<>();
					List<String> tags = new LinkedList<>();
					
					for (String reading : readings) {

						int index = -1;
						for (int i=0; i< reading.length(); i++) {
							
							char c = reading.charAt(i);
							
							if (c == ':') {
								
								index = i;
								if (i  + 1< reading.length() && reading.charAt(i + 1) == ':') {
									index = i + 1;
								}
								
								break;
								
							}
							
						}
						
						String segment;
						String tag;
						
						if (index < 0 || index == reading.length() - 1) {
							segment = reading;
							tag = null;
						} else {
							segment = reading.substring(0, index);
							tag = reading.substring(index + 1);	
						}
						
						segment = normalizer_.normalize(segment);
						tag = TagSet.getTag(tag, tag_level_);
						
						segments.add(segment);
						tags.add(tag);
					}
					
					StringBuilder rejoint_word = new StringBuilder(word.getLength());
					for (String segment : segments) {
						rejoint_word.append(segment);
					}
					assert rejoint_word.toString().equals(word.getWord()) : line + " " + segments;
					
					if (word.getWord().equals("mtk:hon")) {
						System.err.format("%s %s %s\n", word, segments, tags);
					}
					
					word.add(new SegmentationReading(segments, tags));
				}
			}
		}
	}
	
	public List<Word> getData() {
		return data_;
	}

}
