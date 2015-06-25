package chipmunk.segmenter;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TwoPhaseSegmenter extends Segmenter {

	private static final long serialVersionUID = 1L;
	private Segmenter segmenter_;
	
	public TwoPhaseSegmenter(Segmenter segmenter) {
		segmenter_  = segmenter;
	}
	
	@Override
	public SegmentationReading segment(Word word) {
		Segmenter segmenter = new RulebasedSegmenter();
		
		SegmentationReading reading = segmenter.segment(word);
		
		List<String> segments = new LinkedList<String>();
		List<String> tags = new LinkedList<String>();
		
		Iterator<String> segment_it = reading.getSegments().iterator();
		Iterator<String> tags_it = reading.getTags().iterator();
		
		while (segment_it.hasNext()) {
			String segment = segment_it.next();
			String tag = tags_it.next();
			
			if (tag == TagSet.ALPHA) {
				SegmentationReading new_reading = segmenter_.segment(new Word(segment));
				segments.addAll(new_reading.getSegments());
				tags.addAll(new_reading.getTags());
			} else {
				segments.add(segment);
				tags.add(tag);
			}
			
		}

		return new SegmentationReading(segments, tags);
	}

}
