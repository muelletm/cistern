package marmot.segmenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marmot.util.LineIterator;

public class SegmentationDataReader {

	private List<Word> data_;
	private boolean keep_tag_;
	private StringNormalizer normalizer_;
	
	public SegmentationDataReader(String filepath, String lang, boolean keep_tag) {
		data_ = new ArrayList<Word>();		
		normalizer_ = StringNormalizer.labeledCreate(lang);
		readFile(filepath, data_);
	}
	
	private void readFile(String fileIn, List<Word> words) {
		
		Map<String, Word> vocab = new HashMap<>();
		
		LineIterator iterator = new LineIterator(fileIn, "\t");
		
		while(iterator.hasNext()) {
			
			List<String> line = iterator.next();
			assert line.size() == 2;
			
			if (line.size() > 0) {
				String word_string = line.get(0);
				
				word_string = normalizer_.normalize(word_string);
				
				Word word = vocab.get(word_string);
				if (word == null) {
					word = new Word(word_string);
					words.add(word);
					vocab.put(word_string, word);
				}
				
				String[] word_readings = line.get(1).split(", "); 
				
				for (String word_reading : word_readings) {
					
					String[] readings = word_reading.split(" ");
					List<String> segments = new LinkedList<>();
					List<String> tags = new LinkedList<>();
					
					for (String reading : readings) {
						
						//reading = reading.replace(",","");
						int index = reading.indexOf(':');
						
						String segment;
						String tag;
						
						if (index < 0) {
							segment = reading;
							tag = "<TAG>";
						} else {
							segment = reading.substring(0, index);
							tag = reading.substring(index + 1);	
						}
						
						segment = normalizer_.normalize(segment);
						
						if (!keep_tag_)
							tag = "<TAG>";
						
						if (segment.equals("~")) {
							System.err.println("Skipping segment: ~ from line " + line);
						} else {
							segments.add(segment);
							tags.add(tag);
						}
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
