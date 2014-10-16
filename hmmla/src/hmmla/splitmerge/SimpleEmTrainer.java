// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.splitmerge;

import hmmla.hmm.HmmModel;
import hmmla.hmm.Model;
import hmmla.hmm.Statistics;
import hmmla.hmm.Tree;
import hmmla.io.Sentence;
import hmmla.io.Token;
import hmmla.util.Numerics;
import hmmla.util.SymbolTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class SimpleEmTrainer extends EmTrainer {

	protected ForwardChart forward_;
	protected BackwardChart backward_;
	protected Model model_;
		
	public double estep(Model model, HmmModel hmm_model,
			Iterable<Sentence> reader) {
		return estep(model, hmm_model, reader, true);
	}
	
	public double estep(Model model, HmmModel hmm_model,
			Iterable<Sentence> reader, boolean update) {
		reset(model, hmm_model);

		if (update)
			model.getStatistics().setZero();

		double ll = 0.0;	
		for (Sentence sentence : reader) {
			ll += estep(sentence, update);
		}

		return ll;
	}
	
	protected void reset(Model model, HmmModel hmm_model) {
		SymbolTable<String> tag_table = model.getTagTable();
		
		if (forward_ == null)
			forward_ = new ForwardChart();

		if (backward_ == null)
			backward_ = new BackwardChart();

		forward_.init(tag_table.size(), hmm_model);
		backward_.init(tag_table.size(), hmm_model);
		model_ = model;
	}

	protected double estep(Sentence sentence, boolean update) {
		Map<String, Tree> topLevel = model_.getTopLevel();
		SymbolTable<String> tag_table = model_.getTagTable();

		int T = sentence.size();
		
		List<Iterable<Integer>> candidates = new ArrayList<Iterable<Integer>>(T);

		for (Token token : sentence) {
			List<Tree> leaves = new LinkedList<Tree>();
			Tree tree = topLevel.get(token.getTag());
			tree.getLeaves(leaves);
			List<Integer> ileaves = new LinkedList<Integer>();

			for (Tree leaf : leaves) {
				ileaves.add(tag_table.toIndex(leaf.getName()));
			}

			candidates.add(ileaves);
		}

		assert candidates.size() == sentence.size();

		forward_.update(candidates, sentence);
		Collections.reverse(candidates);
		backward_.update(candidates, sentence);
		Collections.reverse(candidates);

		double logZ = forward_.score();
		addStateScores(model_, sentence, candidates, logZ, update);
		addTransitionScores(model_, sentence, candidates, logZ, update);

		return logZ;
	}

	private void addTransitionScores(Model model, List<Token> sentence,
			List<Iterable<Integer>> candidates, double logZ, boolean update) {
		Statistics statistics = model.getStatistics();
		HmmModel hmm_model = forward_.getHmmModel();

		int t = 0;
		Iterator<Iterable<Integer>> iterator = candidates.iterator();
		assert iterator.hasNext();
		Iterable<Integer> last_tags = iterator.next();
		Token token = sentence.get(t);

		double newLogZ = Double.NEGATIVE_INFINITY;
		
		double[] scores = new double[model.getTagTable().size()];
		Arrays.fill(scores, 0.0);
		hmm_model.getEmissions(token.getWordForm(), scores);
		
		for (Integer tag : last_tags) {

			double score = scores[tag] +
					+ hmm_model.getTransitions(Model.BorderIndex, tag)
					+ backward_.score(t, tag);
			double p = Math.exp(score - logZ);
			if (update)
				statistics.addTransitions(Model.BorderIndex, tag, p);
			newLogZ = Numerics.sumLogProb(newLogZ, score);
		}
		t++;

		while (iterator.hasNext()) {

			Iterable<Integer> current_tags = iterator.next();
			token = sentence.get(t);

			Arrays.fill(scores, 0.0);
			hmm_model.getEmissions(token.getWordForm(), scores);
			
			newLogZ = Double.NEGATIVE_INFINITY;
			for (Integer fromIndex : last_tags) {

				for (Integer toIndex : current_tags) {

					if (toIndex == Model.BorderIndex) {
						continue;
					}

					double score = forward_.score(t - 1, fromIndex)
							+ scores[toIndex]
							+ hmm_model.getTransitions(fromIndex,
									toIndex) + backward_.score(t, toIndex);

					newLogZ = Numerics.sumLogProb(newLogZ, score);

					double p = Math.exp(score - logZ);
					if (update)
						statistics.addTransitions(fromIndex, toIndex, p);

				}
			}
			last_tags = current_tags;
			t++;
		}

		newLogZ = Double.NEGATIVE_INFINITY;
		for (int from_index : last_tags) {

			double score = forward_.score(t - 1, from_index)
					+ hmm_model.getTransitions(from_index, Model.BorderIndex);

			double p = Math.exp(score - logZ);
			if (update)
				statistics.addTransitions(from_index, Model.BorderIndex, p);
			newLogZ = Numerics.sumLogProb(newLogZ, score);
		}
	}

	private void addStateScores(Model model, List<Token> outputs,
			Iterable<Iterable<Integer>> candidates, double logZ, boolean update) {
		Statistics statistics = model.getStatistics();

		int t = 0;
		
		
		for (Iterable<Integer> candidate : candidates) {

			Token output = outputs.get(t);
			int ioutput = model.getWordTable().toIndex(output.getWordForm());
			

			double newLogZ = Double.NEGATIVE_INFINITY;
			for (int index : candidate) {

				double score = forward_.score(t, index)
						+ backward_.score(t, index);

				double p = Math.exp(score - logZ);

				if (update) {
					statistics.addEmissions(index, ioutput, p);
				}

				newLogZ = Numerics.sumLogProb(newLogZ, score);
			}
			t++;
		}
	}
}
