// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morfessor;

public class QueryScorer implements Scorer {

	@Override
	public double score(Segment segment) {
		double total_score = 0;
		
		while (segment != null) {
			Segment prev_segment = segment.getPreviousSegment();

			int from_index = segment.getIndex();
			int to_index = 0;
			if (prev_segment != null) {
				to_index = prev_segment.getIndex();
			}
			
			int length = from_index - to_index;
			total_score += length * segment.getScorer();
			
			segment = prev_segment;
			
		}

		return total_score;
	}

}
