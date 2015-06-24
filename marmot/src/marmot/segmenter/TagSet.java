package marmot.segmenter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class TagSet {

	private static final String SEGMENT = "<SEGMENT>";

	public static String getTag(String tag, int tag_level) {
		
		if (tag == null || tag_level < 1) {
			return SEGMENT;
		}
		
		String[] subtags = split(tag);
		
		List<String> newtags = new LinkedList<>();
		
		for (String subtag : subtags) {
			
			if (subtag.equals("ROOT") || subtag.equals("PREFIX") || subtag.equals("SUFFIX")) {
				newtags.add(subtag);
			} else if (tag_level > 1 && (subtag.equals("INFL") || subtag.equals("DERI"))) {
				newtags.add(subtag);
			}
			
			
		}
		
		newtags.add(SEGMENT);
		
		return join(newtags);
	}
	
	private static String join(Collection<String> subtags) {
		StringBuilder sb = new StringBuilder();
		for (String subtag : subtags) {
			if (sb.length() > 0) {
				sb.append('|');
			}
			sb.append(subtag);
		}
		return sb.toString();
	}

	public static String[] split(String tag) {
		return tag.split("\\|");
	}

}
