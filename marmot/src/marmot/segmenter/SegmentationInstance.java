package marmot.segmenter;

import java.util.Collection;
import marmot.util.FeatUtil;
import marmot.util.SymbolTable;

public class SegmentationInstance {

	private Word word_;
	private Collection<SegmentationResult> results_;
	private short[] chars_;
	
	public SegmentationInstance(Word word, Collection<SegmentationResult> results) {
		word_ = word;
		results_ = results;
	}
	
	public int getLength() {
		return word_.getLength();
	}

	public short[] getFormCharIndexes(SymbolTable<Character> char_table) {
		if (chars_ == null)
			chars_ = FeatUtil.getCharIndexes(word_.getWord(), char_table, false);
		
		return chars_;
	}

	public Word getWord() {
		return word_;
	}

	public Collection<SegmentationResult> getResults() {
		return results_;
	}

//	public SegmentationResult getFirstResult() {
//		return results_.iterator().next();
//	}

}
