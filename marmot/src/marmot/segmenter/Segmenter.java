package marmot.segmenter;

import marmot.segmenter.Word;

public class Segmenter {

	SegmenterModel model_;

	public Segmenter(SegmenterModel model) {
		model_ = model;
	}
	
	public SegmentationReading segment(Word word) {
		SegmentationInstance instance = model_.getInstance(word);
		SegmentationDecoder decoder = new SegmentationDecoder(model_);
		SegmentationResult result = decoder.decode(instance);
		return model_.toWord(word.getWord(), result).getReadings().iterator().next();
	}

}
