package marmot.segmenter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import marmot.util.Numerics;


public class Scorer {

	Score precision = new Score();
	Score recall = new Score();
	
	private static class Boundary{
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + position_;
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
			Boundary other = (Boundary) obj;
			if (position_ != other.position_)
				return false;
			return true;
		}

		public Boundary(int position) {
			position_ = position;
		}
		
		int position_;

	}
	
	private Set<Boundary> getBrackets(SegmentationReading reading, int length) {
		Set<Boundary> brackets = new HashSet<>();
		int start = 0;
		Iterator<String> segment_iterator = reading.getSegments().iterator();
		while (segment_iterator.hasNext()) {
			String segment = segment_iterator.next();
			
			int end = start + segment.length();
			
			if (end < length)
				brackets.add(new Boundary(end));
			
			start = end;
		}
		return brackets;
	}
	
	private static Set<Boundary> getBoundary(SegmentationResult candidate, int length) {
		Set<Boundary> brackets = new HashSet<>();
		Iterator<Integer> index_iterator = candidate.getInputIndexes().iterator();
		while (index_iterator.hasNext()) {
			
			int end = index_iterator.next();
			
			if (end < length)
				brackets.add(new Boundary(end));
		}
		return brackets;
	}
	
	public void eval(List<Set<Boundary>> predicted, List<Set<Boundary>> reference) {
			eval(predicted, reference, recall);
			eval(reference, predicted, precision);
	}
	
	public void eval(Collection<Word> words, Segmenter segmenter) {
		for (Word word : words) {
			
			SegmentationReading reading = segmenter.segment(word);
			Set<Boundary> brackets = getBrackets(reading, word.getLength());
			List<Set<Boundary>> predicted = Collections.singletonList(brackets);
			
			List<Set<Boundary>> reference = new LinkedList<>();
			for (SegmentationReading ref_reading : word.getReadings()) {
				reference.add(getBrackets(ref_reading, word.getLength()));
			}
			
			eval(reference, predicted);
			
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
	
	void eval(Collection<Set<Boundary>> predicted, Collection<Set<Boundary>> reference, Score s) {
			double max_score = 0;
	        double max_total = -1;
	        for (Set<Boundary> ref : reference) {
	        	
	        	double total = ref.size();
	        	
	            for (Set<Boundary> pre : predicted) {
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

	private void eval_single(Set<Boundary> pre, Set<Boundary> ref, Score s) {
	    int total = ref.size();
	    if (total == 0) {
	    	s.score = 1.0;
	    	s.total = 0.0;
	        return;
	    }
	    Set<Boundary> intersect = new HashSet<>(pre);
	    intersect.retainAll(ref);
	    s.score = intersect.size() / (double) total;
	    s.total = total;
	}

	public double getFscore() {
		double p = getPrecision();
		double r = getRecall();
		
		if (Numerics.approximatelyLesserEqual(p + r, 0.0)) {
			return 0.0;
		}
		
		double f = (2. * p * r) / (p + r);
		return f;
	}

	private double getRecall() {
		return 100. * recall.score / recall.total;
	}

	private double getPrecision() {
		return 100. * precision.score / precision.total;
	}

	public static SegmentationResult closest(SegmentationResult result,
			Collection<SegmentationResult> results, int length) {
		double best_score = Double.NEGATIVE_INFINITY;
		SegmentationResult best_result = null;
		Set<Boundary> brackets = getBoundary(result, length);
		
		for (SegmentationResult candidate : results) {
			
			Set<Boundary> other = getBoundary(candidate, length);
			
			Scorer scorer = new Scorer();
			scorer.eval(Collections.singletonList(brackets), Collections.singletonList(other));
			
			double score = scorer.getFscore();
			
			assert !Double.isNaN(score);
			assert !Double.isInfinite(score);
			
			if (score > best_score) {
				best_result = candidate;
				best_score = score;
			}
		}
		
		return best_result;
	}

}
