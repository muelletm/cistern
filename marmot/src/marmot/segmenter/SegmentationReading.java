package marmot.segmenter;

import java.util.Collection;
import java.util.List;

public class SegmentationReading {

	private Collection<String> segments_;
	private Collection<String> tags_;
	
	public SegmentationReading(List<String> segments, List<String> tags) {
		segments_ = segments;
		tags_ = tags;
	}

	public Collection<String> getSegments() {
		return segments_;
	}
	
	public Collection<String> getTags() {
		return tags_;
	}

	@Override
	public String toString() {
		return "[" + segments_ + " " + tags_ + "]";
	}
	
}
