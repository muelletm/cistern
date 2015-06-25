package chipmunk.segmenter;

import java.util.LinkedList;
import java.util.List;

public class RulebasedSegmenter extends Segmenter {

	private static final long serialVersionUID = 1L;

	@Override
	public SegmentationReading segment(Word word) {
		
		List<String> segments = new LinkedList<>();
		List<String> tags = new LinkedList<>();
		
		StringBuilder sb = new StringBuilder();
		String current_tag = null;
		
		String form = word.getWord();
		
		for (int i=0; i<form.length(); i++) {
			
			char c = form.charAt(i);
			
			String new_tag;
			
			if (Character.isLetter(c)) {
				new_tag = TagSet.ALPHA;
			} else if (Character.isDigit(c)) {
				new_tag = TagSet.NUMBER;
			} else {
				new_tag = TagSet.SPECIAL;
			}
			
			if (current_tag != null) {
				if (!current_tag.equals(new_tag) || new_tag == TagSet.SPECIAL) {
					assert sb.length() > 0;
					segments.add(sb.toString());
					tags.add(current_tag);
					sb.setLength(0);
				}
			}
			
			sb.append(c);
			current_tag = new_tag;
		}

		if (current_tag != null) {
			segments.add(sb.toString());
			tags.add(current_tag);
		}
		
		return new SegmentationReading(segments, tags);
	}

}
