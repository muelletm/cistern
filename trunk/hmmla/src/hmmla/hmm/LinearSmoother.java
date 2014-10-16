// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.hmm;

import java.util.LinkedList;
import java.util.List;

public class LinearSmoother extends Smoother {
	private static final long serialVersionUID = 1L;
	private double param_;

	public LinearSmoother(double param) {
		param_ = param;	
	}

	protected void smooth(Tree tag, Model model, Statistics statistics,
			Type type) {
		double[] backoff = collectFreqs(tag, model, type);
		
		List<Tree> leaves = new LinkedList<Tree>();
		tag.getLeaves(leaves);
		for (Tree tree : leaves) {
			String tag_name = tree.getName();
			int tag_index = model.getTagTable().toIndex(tag_name);
			smooth(tag_index, backoff, model, statistics, type);
		}
	}

	private void smooth(int tag_index, double[] backoff, Model model,
			Statistics statistics, Type type) {
		int number = getNumber(model, type);
		double total = 0;
		double total_backoff = 0;
		for (int index = 0; index < number; index ++) {
			total += getFreq(model.getStatistics(), tag_index, index, type);
			total_backoff += backoff[index];
		}
		
		if (total < 1.e-20) {
			return;
		}
		
		assert (total_backoff > 1.e-20);
		for (int index = 0; index < number; index ++) {
			double prob = getFreq(model.getStatistics(), tag_index, index, type) / total;
			double backoff_prob = backoff[index] / total_backoff;

			prob = (1 - param_) * prob + (param_* backoff_prob);
			double freq = prob * total;
			
			assert !(Double.isNaN(freq) || Double.isInfinite(freq));
			setFreq(model, statistics, tag_index, index, type, freq);
		}	
	}

	private double[] collectFreqs(Tree tag, Model model, Type type) {
		int number = getNumber(model, type);
		List<Tree> leaves = new LinkedList<Tree>();
		double[] freq = new double[number];	
		tag.getLeaves(leaves);
		for (Tree tree : leaves) {
			String tag_name = tree.getName();
			int tag_index = model.getTagTable().toIndex(tag_name);
			for (int index = 0; index < number; index ++) {
				freq[index] += getFreq(model.getStatistics(), tag_index, index, type);
			}
		}
		return freq;
	}
}
