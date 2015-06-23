package marmot.segmenter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class Scorer {

	Score precision = new Score();
	Score recall = new Score();
	
	private static class Bracket{
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + end_;
			result = prime * result + start_;
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
			Bracket other = (Bracket) obj;
			if (end_ != other.end_)
				return false;
			if (start_ != other.start_)
				return false;
			return true;
		}
		public Bracket(int start, int end) {
			start_ = start;
			end_ = end;
		}
		
		int start_;
		int end_;
	}
	
	private Set<Bracket> getBrackets(SegmentationReading reading) {
		Set<Bracket> brackets = new HashSet<>();
		int start = 0;
		Iterator<String> segment_iterator = reading.getSegments().iterator();
		while (segment_iterator.hasNext()) {
			String segment = segment_iterator.next();
			int end = start + segment.length();
			
			brackets.add(new Bracket(start, end));
			
			start = end;
		}
		return brackets;
	}
	
	public void eval(Collection<Word> words, Segmenter segmenter) {
		for (Word word : words) {
			
			SegmentationReading reading = segmenter.segment(word);
			Set<Bracket> brackets = getBrackets(reading);
			List<Set<Bracket>> predicted = Collections.singletonList(brackets);
			
			List<Set<Bracket>> reference = new LinkedList<>();
			for (SegmentationReading ref_reading : word.getReadings()) {
				reference.add(getBrackets(ref_reading));
			}
			
			eval(predicted, reference, recall);
			eval(reference, predicted, precision);
			
		}
		
	}
	
	public String report() {
		double p = getPrecision();
		double r = getRecall();
		double f = getFscore();
		return String.format("F1: %g Pr: %g / %g = %g Re:%g / %g = %g", f, precision.score, precision.total, p, recall.score, recall.total, r);
	}
	
	private static class Score {
		double score;
		double total;
	}
	
	void eval(Collection<Set<Bracket>> predicted, Collection<Set<Bracket>> reference, Score s) {
			double max_score = 0;
	        double max_total = -1;
	        for (Set<Bracket> ref : reference) {
	        	
	        	double total = ref.size();
	        	
	            for (Set<Bracket> pre : predicted) {
	            	Score m_tmp = new Score();
	                eval_single(pre, ref, m_tmp);
	                if (max_total == -1 || m_tmp.score > max_score) {
	                    max_score = m_tmp.score;
	                    max_total = total;
	                }
	            }
	        }

	        // Macro-average: 
	        // max_score is proportion of correct boundaries, max_total is one
	        max_total = 1;
	        s.total += max_total;
	        s.score += max_score;
	}

	private void eval_single(Set<Bracket> pre, Set<Bracket> ref, Score s) {
	    int total = ref.size();
	    if (total == 0) {
	    	s.score = 1.0;
	    	s.total = 0.0;
	        return;
	    }
	    Set<Bracket> intersect = new HashSet<>(pre);
	    intersect.retainAll(ref);
	    s.score = intersect.size() / (double) total;
	    s.total = total;
	}

	public double getFscore() {
		double p = getPrecision();
		double r = getRecall();
		double f = (2. * p * r) / (p + r);
		return f;
	}

	private double getRecall() {
		return 100. * recall.score / recall.total;
	}

	private double getPrecision() {
		return 100. * precision.score / precision.total;
	}
	
}
