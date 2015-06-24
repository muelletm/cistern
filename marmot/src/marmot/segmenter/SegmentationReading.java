package marmot.segmenter;

import java.util.Collection;
import java.util.List;

public class SegmentationReading {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((segments_ == null) ? 0 : segments_.hashCode());
		result = prime * result + ((tags_ == null) ? 0 : tags_.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SegmentationReading other = (SegmentationReading) obj;
		if (segments_ == null) {
			if (other.segments_ != null)
				return false;
		} else if (!segments_.equals(other.segments_))
			return false;
		if (tags_ == null) {
			if (other.tags_ != null)
				return false;
		} else if (!tags_.equals(other.tags_))
			return false;
		return true;
	}

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
