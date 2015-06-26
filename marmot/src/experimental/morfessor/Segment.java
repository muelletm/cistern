// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package experimental.morfessor;

public class Segment  implements Comparable<Segment> {
	private Segment segment_;
	private double score_;
	private Double total_score_;
	private int index_;
	private Scorer scorer_;
	
	public Segment(Segment segment, int index, double score, Scorer scorer) {
		segment_ = segment;
		score_ = score;
		index_ = index;
		scorer_ = scorer;
	}

	@Override
	public int compareTo(Segment o) {
		return - Double.compare(getTotalScore(), o.getTotalScore());
	}

	public double getTotalScore() {
		if (total_score_ == null) {
			total_score_ = scorer_.score(this);
		}
		return total_score_;
	}

	public int getIndex() {
		return index_;
	}

	public int getLength() {
		int length = 0;
		Segment run = this;
		while (run != null) {
			length += 1;
			run = run.segment_;				
		}
		return length;
	}

	public Segment getPreviousSegment() {
		return segment_;
	}

	public double getScorer() {
		return score_;
	}
	
}
