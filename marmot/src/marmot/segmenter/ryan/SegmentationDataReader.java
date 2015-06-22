package marmot.segmenter.ryan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SegmentationDataReader {

	private Map<String,Integer> tag2int;
	private Map<String,Integer> seg2int;
	private Map<String,Integer> seg2count;
	
	private List<Word> data;
	

	private int numTags;
	private int numSegs;
	private int maxLength;
	
	public SegmentationDataReader(String filepath) {
		this.tag2int = new HashMap<String,Integer>();
		this.seg2int = new HashMap<String,Integer>();
		this.seg2count = new HashMap<String,Integer>();
		this.data = new ArrayList<Word>();
		
		this.tag2int.put("START",0);
		// read in training data
		readFile(filepath, data);

		this.numSegs = this.seg2int.size();
		this.numTags = this.tag2int.size();
	}
	
	private void readFile(String fileIn, List<Word> lst) {
		this.maxLength = Integer.MIN_VALUE;
		try {
	        BufferedReader reader = new BufferedReader(new FileReader(fileIn));
	        try {
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	            	int counter = 0;
	            	for (String value : line.split("\t")) {
	            		if (counter == 0) {
	            			Word word = new Word(value,seg2int,seg2count);
	            			lst.add(word);
	            			if (word.getLength() > this.maxLength) {
	            				this.maxLength = word.getLength();
	            			}


	            			
	            		} else if (counter == 1) {
	            			for (String token : line.split(" ")) {
	            				String withoutComma = token.replace(",","");
	            				int counter2 = 0;
	            				for (String thingy : withoutComma.split(":")) {
	            					if (counter2 == 0) {
	            						String segment = thingy;
	            					} else if (counter2 == 1) {
	            						String tag = thingy;
	            						
	            						//intern
	            						if (!this.tag2int.containsKey(tag)) {
	            							this.tag2int.put(tag,this.tag2int.size());
	            						}
	            						
	            					}
	            					++counter2;
	            				}
	            						
	            			}
	            		}
	            		++counter;
	            	}
	            }
	        } finally {
	            reader.close();
	        }
	    } catch (IOException ioe) {
	        System.err.println("oops " + ioe.getMessage());
	    }
	}
	

	public Map<String, Integer> getTag2int() {
		return tag2int;
	}

	public Map<String, Integer> getSeg2int() {
		return seg2int;
	}

	public Map<String, Integer> getSeg2count() {
		return seg2count;
	}

	public Collection<Word> getData() {
		return data;
	}

	public int getNumTags() {
		return numTags;
	}

	public int getNumSegs() {
		return numSegs;
	}

	public int getMaxLength() {
		return this.maxLength;
	}

}
