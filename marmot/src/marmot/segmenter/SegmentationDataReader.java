package marmot.segmenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cc.mallet.util.CommandOption.Set;

import marmot.util.LineIterator;

public class SegmentationDataReader {

	private List<Word> data;
	
	public SegmentationDataReader(String filepath) {
		this.data = new ArrayList<Word>();		
		readFile(filepath, data);
	}
	
	private void readFile(String fileIn, List<Word> words) {
		
		Map<String, Word> vocab = new HashMap<>();
		
		LineIterator iterator = new LineIterator(fileIn, "\t");
		
		while(iterator.hasNext()) {
			
			List<String> line = iterator.next();

			if (line.size() > 0) {
				String word_string = line.get(0).toLowerCase();
				Word word = vocab.get(word_string);
				if (word == null) {
					word = new Word(word_string);
					words.add(word);
					vocab.put(word_string, word);
				}
				
				for (int i=1; i<line.size(); i++) {
					
					String[] readings = line.get(i).split(" ");
					List<String> segments = new LinkedList<>();
					List<String> tags = new LinkedList<>();
					
					for (String reading : readings) {
						
						reading = reading.replace(",","");
						int index = reading.indexOf(':');
						
						if (index < 0) {
							throw new RuntimeException(String.format("Invalid pair: %s in line %s", reading, line));
						}
						
						String segment = reading.substring(0, index);
						//String tag = reading.substring(index + 1);
						String tag = "<TAG>";
						
						segments.add(segment);
						tags.add(tag);
						
					}
					
					word.add(new SegmentationReading(segments, tags));
					
					break;
				}
			}
		}
	}
	
	public Collection<Word> getData() {
		return data;
	}

}
