package marmot.segmenter;

import java.io.Serializable;

import marmot.segmenter.Word;

public class Segmenter implements Serializable {

	private static final long serialVersionUID = 1L;
	private SegmenterModel model_;

	public Segmenter(SegmenterModel model) {
		model_ = model;
	}
	
	public SegmentationReading segment(Word word) {
		SegmentationInstance instance = model_.getInstance(word);
		SegmentationDecoder decoder = new SegmentationDecoder(model_);
		SegmentationResult result = decoder.decode(instance);
		return model_.toWord(word.getWord(), result).getReadings().iterator().next();
	}

	public SegmenterModel getModel() {
		return model_;
	}

}
