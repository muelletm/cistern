package marmot.segmenter;

public class StatSegmenter extends Segmenter {

	private static final long serialVersionUID = 1L;
	private SegmenterModel model_;

	public StatSegmenter(SegmenterModel model) {
		model_ = model;
	}
	
	@Override
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
