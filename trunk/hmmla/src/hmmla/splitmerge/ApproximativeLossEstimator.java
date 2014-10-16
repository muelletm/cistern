// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.splitmerge;

import hmmla.hmm.HmmModel;
import hmmla.hmm.HmmTrainer;
import hmmla.hmm.Model;
import hmmla.hmm.Statistics;
import hmmla.hmm.Tree;
import hmmla.io.Sentence;
import hmmla.io.Token;
import hmmla.util.Numerics;
import hmmla.util.SymbolTable;
import hmmla.util.Tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ApproximativeLossEstimator implements LossEstimator {

	ForwardChart forward_;
	BackwardChart backward_;
	HmmTrainer trainer_;

	public ApproximativeLossEstimator(HmmTrainer trainer) {
		trainer_ = trainer;
	}

	private void calcPrior(Model model, double[] prior) {
		SymbolTable<String> tagTable = model.getTagTable();
		Statistics statistics = model.getStatistics();
		for (int i = 0; i < tagTable.size(); i++) {
			for (int j = 0; j < tagTable.size(); j++) {
				prior[j] += statistics.getTransitions(i, j);
			}
		}
	}

	@Override
	public void estimateLosses(Model model, Iterable<Sentence> reader,
			List<Tuple<Integer, Double>> tuples) {

		SymbolTable<String> tagTable = model.getTagTable();

		int N = (tagTable.size() - 1) / 2;

		double[] loss = new double[1 + N];
		double[] prior = new double[tagTable.size()];
		calcPrior(model, prior);

		HmmModel hmm_model = trainer_.train(model);
		if (forward_ == null) {
			forward_ = new ForwardChart();
		}
		forward_.init(tagTable.size(), hmm_model);

		if (backward_ == null) {
			backward_ = new BackwardChart();
		}
		backward_.init(tagTable.size(), hmm_model);

		for (Sentence sentence : reader) {
			addLoss(model, sentence, loss, prior);
		}

		for (int i = 1; i < N + 1; i++) {
			tuples.add(new Tuple<Integer, Double>(i, loss[i]));
		}
	}

	protected void addLoss(Model model, Sentence sentence, double[] loss,
			double[] prior) {
		SymbolTable<String> tagTable = model.getTagTable();
		Map<String, Tree> topLevel = model.getTopLevel();

		int T = sentence.size();

		List<Iterable<Integer>> candidates = new ArrayList<Iterable<Integer>>(T);
		List<Iterable<Tree>> parents = new ArrayList<Iterable<Tree>>(T);

		for (Token token : sentence) {
			List<Tree> leaves = new LinkedList<Tree>();
			Tree tree = topLevel.get(token.getTag());
			tree.getTreesOverLeaves(leaves);
			List<Integer> ileaves = new ArrayList<Integer>(leaves.size() * 2);
			List<Tree> cparents = new ArrayList<Tree>(leaves.size());

			for (Tree leaf : leaves) {
				Tree left = leaf.getLeft();
				String lname = left.getName();
				ileaves.add(tagTable.toIndex(lname));
				Tree right = leaf.getRight();
				String rname = right.getName();
				ileaves.add(tagTable.toIndex(rname));
				cparents.add(leaf);
			}

			parents.add(cparents);
			candidates.add(ileaves);
		}
		forward_.update(candidates, sentence);
		Collections.reverse(candidates);
		backward_.update(candidates, sentence);
		Collections.reverse(candidates);
		addStateLoss(model, loss, prior, parents, sentence);
	}

	protected void addStateLoss(Model model, double[] loss, double[] prior,
			List<Iterable<Tree>> parents, List<Token> sentence) {
		double[] scores = new double[model.getTagTable().size()];
		SymbolTable<String> tag_table = model.getTagTable();
		HmmModel normalizedStatistics = forward_.getHmmModel();
		int t = 0;
		for (Iterable<Tree> cparents : parents) {
			for (Tree parent : cparents) {

				// It is quite inefficient to this computation here: (we just
				// have to do it once per POS)
				Tree root = parent.getRoot();
				assert root != null;
				double sum = Double.NEGATIVE_INFINITY;
				List<Tree> leaves = new LinkedList<Tree>();
				root.getLeaves(leaves);
				for (Tree leaf : leaves) {
					int leaf_index = tag_table.toIndex(leaf.getName());
					double f = forward_.score(t, leaf_index);
					double b = backward_.score(t, leaf_index);
					sum = Numerics.sumLogProb(sum, f + b);
				}
				sum = Math.exp(sum);
				assert sum + 1e-5 > 0.;

				int lindex = tag_table.toIndex(parent.getLeft().getName());
				int rindex = tag_table.toIndex(parent.getRight().getName());

				Arrays.fill(scores, 0.0);
				normalizedStatistics.getEmissions(
						sentence.get(t).getWordForm(), scores);

				double p_w_l = Math.exp(scores[lindex]);
				double p_w_r = Math.exp(scores[rindex]);
				double f_l = Math.exp(forward_.score(t, lindex));
				double f_r = Math.exp(forward_.score(t, rindex));
				double b_l = Math.exp(backward_.score(t, lindex));
				double b_r = Math.exp(backward_.score(t, rindex));
				double p_l = prior[lindex] / (prior[lindex] + prior[rindex]);
				double p_r = 1 - p_l;
				double premerge = (f_l * b_l) + (f_r * b_r);
				double postmerge = ((p_w_l * p_l + p_w_r * p_r) * (f_l / p_w_l + f_r
						/ p_w_r))
						* (p_l * b_l + p_r * b_r);

				if (sum - premerge + sum * 1e-5 < 0.) {
					assert false;
				}

				assert premerge >= 0;
				assert postmerge >= 0;

				double new_premerge = sum;
				double new_postmerge = sum - premerge + postmerge;

				if (new_premerge > 0.0 && new_postmerge > 0.0) {
					double delta = new_postmerge / new_premerge;
					loss[lindex] += Math.log(delta);
				}
			}
			t++;
		}
	}

}
