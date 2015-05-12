package marmot.segmenter;

import java.util.List;
import java.util.Map;



public class Word {
	private String PADDING = "#";
	private String word;
	private String paddedWord;
	private List<String> goldSegmentation;
	private List<Integer> goldFeatures;
	private int[][] segment2Id;
	private int length;
	
	public Word (String word, Map<String,Integer> seg2int, Map<String,Integer> seg2count) {
		this.word = word;
		this.paddedWord = PADDING + word + PADDING + PADDING;
		segment2Id = new int[this.paddedWord.length()][this.paddedWord.length()];
		this.length = word.length();
		this.extractSegmentIds(seg2int,seg2count);
			
		
	}
	
	
	private void extractRightContextIds(int contextSize, Map<String,Integer> leftContext2int) {
		for (int index = 0; index < this.paddedWord.length(); ++index) {
			int leftValue = Math.max(0, contextSize - index);
			String context = this.paddedWord.substring(0, leftValue);
			
			if (!leftContext2int.containsKey(context)) {
				leftContext2int.put(context,leftContext2int.size());
			}
		}
	}
	
	private void extractLeftContextIds(int contextSize, Map<String,Integer> rightContext2int) {
		for (int index = 0; index < this.paddedWord.length(); ++index) {
			int rightValue = Math.min(this.paddedWord.length(), contextSize + index);
			String context = this.paddedWord.substring(rightValue,this.paddedWord.length());
			
			if (!rightContext2int.containsKey(context)) {
				rightContext2int.put(context,rightContext2int.size());
			}
			
		}
	}
	
	/**
	 * Extract the segment Ids for a given word
	 * @param segmentIds
	 */
	private void extractSegmentIds(Map<String,Integer> seg2int, Map<String,Integer> seg2count) {
		for (int i = 0; i < this.paddedWord.length(); ++i) {
			for (int j = i + 1; j < this.paddedWord.length(); ++j) {
				String segment = paddedWord.substring(i,j);
				
				if (segment.contains("#")) {
					continue;
				}
				
				if (!seg2int.containsKey(segment)) {
					seg2int.put(segment,seg2int.size());
					seg2count.put(segment,0);
				}
				seg2count.put(segment,seg2count.get(segment)+1);
				segment2Id[i][j] = seg2int.get(segment);
				System.out.println(segment + "\t" + seg2int.get(segment));
				
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
	
	public String getPaddedWord() {
		return this.paddedWord;
	}

}
