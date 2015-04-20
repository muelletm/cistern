package marmot.segmenter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.javatuples.Pair;

import marmot.util.FileUtils;
import marmot.util.LineIterator;

public class SegmentationDataReader {

	private Map<String,Integer> tag2int;
	private Map<String,Integer> seg2int;
	
	private Map<String,Integer> seg2count;
	
	private List<Word> trainingData;
	private List<Word> devData;
	private List<Word> testData;

	private int numTags;
	private int numSegs;
	private int maxLength;
	
	public SegmentationDataReader(String trainIn, String devIn, String testIn, int threshold) {
		this.tag2int = new HashMap<String,Integer>();
		this.seg2int = new HashMap<String,Integer>();
		this.seg2count = new HashMap<String,Integer>();
		this.trainingData = new ArrayList<Word>();
		this.devData = new ArrayList<Word>();
		this.testData = new ArrayList<Word>();
		
		// read in training data
		readFile(trainIn,trainingData);
		this.numSegs = this.seg2int.size();
		this.numTags = this.tag2int.size();

		
		/*
		tagtag2int = new HashMap<Pair<Integer,Integer>, Integer>();
		tagseg2int = new HashMap<Pair<Integer,Integer>, Integer>();
		
		
		int counter = 0;
		for (int i = 0; i < this.numTags; ++i) {
			for (int j = 0; j < this.numTags; ++j) {
				tagtag2int.put(new Pair<>(i,j),counter);
				++counter;
			}
		}
		counter = 0;
		for (int i = 0; i < this.numTags; ++i) {
			for (int j = 0; j < this.numSegs; ++j) {
				tagseg2int.put(new Pair<>(i,j),counter);
				++counter;
			}
		}
	*/
		
		//while(true);
		
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
	            			this.maxLength = Math.max(this.maxLength,word.getLength());
	            			
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

	public List<Word> getTrainingData() {
		return trainingData;
	}

	public List<Word> getDevData() {
		return devData;
	}

	public List<Word> getTestData() {
		return testData;
	}

	public int getNumTags() {
		return numTags;
	}

	public int getNumSegs() {
		return numSegs;
	}

	public int getMaxLength() {
		return maxLength;
	}


}
