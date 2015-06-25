// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.experimental.morfessor;

public class MeanScorer implements Scorer {

	@Override
	public double score(Segment segment) {
		double total_score = 1.;
		int length = 0;
		while (segment != null) {
			total_score *= segment.getScorer();
			length += 1;
			segment = segment.getPreviousSegment();
			
		}
		total_score = Math.pow(total_score, 1.0 / (double) length );
		return total_score;
	}

}
