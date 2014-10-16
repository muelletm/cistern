// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.hmm;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class WbSmoother extends Smoother {
	private static final long serialVersionUID = 1L;

	protected void smooth(Tree tag, Model model, Statistics statistics, Type type) {
		int number = getNumber(model, type);

		double[] freqs = new double[number];
		collectFreqs(tag, model, type, freqs);
		smooth(tag, model, statistics, type, freqs);
	}

	private void collectFreqs(Tree tag, Model model, Type type, double[] freqs) {
		Statistics unsmoothed_statistics = model.getStatistics();
		List<Tree> leaves = new LinkedList<Tree>();
		tag.getLeaves(leaves);

		Arrays.fill(freqs, 0.0);
		for (Tree leaf : leaves) {
			int tag_index = model.getTagTable()
					.toIndex(leaf.getName(), false);
			for (int index = 0; index < freqs.length; index++) {
				freqs[index] += getFreq(unsmoothed_statistics, tag_index,
						index, type);
			}
		}
	}

	private void smooth(Tree tag, Model model, Statistics statistics,
			Type type, double[] freqs) {
		int number = freqs.length;
		if (tag.getLeft() == null && tag.getRight() == null) {
			int tag_index = model.getTagTable().toIndex(tag.getName());
			for (int index = 0; index < number; index++) {
				setFreq(model, statistics, tag_index, index, type, freqs[index]);
			}
			return;
		}

		double[] fine_freqs = new double[number];

		if (tag.getLeft() == null || tag.getRight() == null) {
			Tree child_tag;
			if (tag.getLeft() == null) {
				child_tag = tag.getRight();
			} else {
				child_tag = tag.getLeft();
			}
			collectFreqs(child_tag, model, type, fine_freqs);
			smooth(freqs, fine_freqs, type);
			smooth(child_tag, model, statistics, type, fine_freqs);
			return;
		}

		collectFreqs(tag.getLeft(), model, type, fine_freqs);
		smooth(freqs, fine_freqs, type);
		smooth(tag.getLeft(), model, statistics, type, fine_freqs);

		collectFreqs(tag.getRight(), model, type, fine_freqs);
		smooth(freqs, fine_freqs, type);
		smooth(tag.getRight(), model, statistics, type, fine_freqs);
	}

	private double computeBackoffFactor(double[] freqs) {
		double backoff_factor = 0.0;
		for (double freq : freqs) {
			backoff_factor += Math.min(1., freq);
		}
		return backoff_factor + 1.;
	}

	private void smooth(double[] freqs, double[] fine_freqs, Type type) {
		double total = 0.0;
		for (double freq : freqs) {
			total += freq;
		}
		
		double total_fine = 0.0;
		for (double freq : fine_freqs) {
			total_fine += freq;
		}

		boolean uniform_backoff_prob = false;
		if (total < 1e-10) {
			uniform_backoff_prob = true;
		}

		double backoff_factor = computeBackoffFactor(fine_freqs);

		for (int index = 0; index < fine_freqs.length; index++) {
			double backoff_prob;
			if (uniform_backoff_prob) {
				backoff_prob = 1. / (double) fine_freqs.length;
			} else {
				backoff_prob = freqs[index] / total;
			}

			double freq = fine_freqs[index] + (backoff_factor * backoff_prob);
			double prob = freq / (total_fine + backoff_factor);
					
			fine_freqs[index] = prob * total_fine;
		}
	}
}
