package experimental.igel;

import java.util.HashMap;
import java.util.Map;

import org.javatuples.Pair;

public class Word {
	
	private String BOS = "^";
	private String EOS = "$";
	
	private String word;
	private String paddedWord;
	
	private Map<Pair<Integer,Integer>,String> pos2String;
	
	public Word(String word) {
		this.word = word;
		this.paddedWord = BOS + word + EOS;		
		this.pos2String = new HashMap<Pair<Integer,Integer>,String>();
		this.extractSubstrings();

	}
	/**
	 * Extracts all substrings. To review,
	 * in this model every word is represented as 
	 * 	a separate factor graph. 
	 */
	public void extractSubstrings() {
		for (int i = 0; i < paddedWord.length() + 1; ++i) {
			for (int j = i + 1; j < paddedWord.length() + 1; ++j) {
				String sub = paddedWord.substring(i, j);
				this.pos2String.put(new Pair<>(i,j), sub);
			}
		}
	}
	
	public Map<Pair<Integer,Integer>,String> getPos2String() {
		return this.pos2String;
	}
	
	public String getWord() {
		return this.word;
	}
}
