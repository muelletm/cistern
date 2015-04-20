package marmot.segmenter;

import java.util.List;
import java.util.Map;



public class Word {
	private String word;
	private List<String> goldSegmentation;
	private List<Integer> goldFeatures;
	private int[][] segment2Id;
	private int length;
	
	public Word (String word, Map<String,Integer> seg2int, Map<String,Integer> seg2count) {
		this.word = word;
		segment2Id = new int[word.length()+1][word.length()+1];
		this.length = word.length();
		this.extractSegmentIds(seg2int,seg2count);
		
		
	}
	
	/**
	 * Extract the segment Ids for a given word
	 * @param segmentIds
	 */
	private void extractSegmentIds(Map<String,Integer> seg2int, Map<String,Integer> seg2count) {
		for (int i = 0; i < this.word.length() + 1; ++i) {
			for (int j = i + 1; j < this.word.length() + 1; ++j) {
				String segment = word.substring(i,j);
				if (!seg2int.containsKey(segment)) {
					seg2int.put(segment,seg2int.size());
					seg2count.put(segment,0);
				}
				seg2count.put(segment,seg2count.get(segment)+1);
				segment2Id[i][j] = seg2int.get(segment);
				
			}
		}
	}
	
	public int[][] getSegment2Id() {
		return this.segment2Id;
	}
	
	public int getLength() {
		return this.length;
	}
	
	public String getWord() {
		return this.word;
	}

}
