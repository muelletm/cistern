// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.decode;

import hmmla.hmm.HmmModel;
import hmmla.hmm.Model;
import hmmla.hmm.Tree;
import hmmla.io.Sentence;
import hmmla.splitmerge.BackwardChart;
import hmmla.splitmerge.ForwardChart;
import hmmla.util.Counter;
import hmmla.util.Numerics;
import hmmla.util.SymbolTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;




public class SimpleDecoder implements Decoder {
	protected ForwardChart forward_;
	protected BackwardChart backward_;
	protected SymbolTable<String> tag_table_;
	protected SymbolTable<String> outputTable_;
	protected Map<String, Tree> clustering_;
	protected Map<String, Tree> top_level_;
	private Model model_;
	private boolean decode_top_level_;

	public SimpleDecoder(Model model, HmmModel hmm_model) {
		this(model, hmm_model, true);
	}

	public SimpleDecoder(Model model, HmmModel hmm_model,
			boolean top_level) {
		model_ = model;
		clustering_ = model.getClustering();
		tag_table_ = model.getTagTable();
		outputTable_ = model.getWordTable();
		forward_ = new ForwardChart();
		forward_.init(tag_table_.size(), hmm_model);
		backward_ = new BackwardChart();
		backward_.init(tag_table_.size(), hmm_model);
		decode_top_level_ = top_level;
		top_level_ = model.getTopLevel();
	}

	public List<String> bestPath(Sentence sentence) {
		List<Iterable<Integer>> candidates = model_.getSentenceCandidates(sentence);
		return bestPath_(candidates, sentence);
	}

	private List<String> bestPath_(List<Iterable<Integer>> candidates,
			Sentence sentence) {
		forward_.update(candidates, sentence);
		Collections.reverse(candidates);
		backward_.update(candidates, sentence);
		Collections.reverse(candidates);
		
		List<String> path = new ArrayList<String>(sentence.size());
		for (int t = 0; t < sentence.size(); t++) {
			Counter<String> counter = new Counter<String>(
					Double.NEGATIVE_INFINITY);
			double bestP = Double.NEGATIVE_INFINITY;
			String bestName = null;
			for (Integer i : candidates.get(t)) {
				String name = tag_table_.toSymbol(i);
				if (i == Model.BorderIndex) {
					continue;
				}

				double prob = forward_.score(t, i) + backward_.score(t, i);

				if (decode_top_level_) {
					Tree tree = clustering_.get(name);
					Tree parent = tree.getRoot();
					assert parent != null;
					Double cached_prob = counter.count(parent.getName());
					prob = Numerics.sumLogProb(prob, cached_prob);
					counter.set(parent.getName(), prob);
					name = parent.getName();
				}

				if (prob > bestP) {
					bestP = prob;
					bestName = name;
				}
			}

			if (bestName == null) {
				throw new RuntimeException("Didn't find candidate!");
			}

			path.add(bestName);
		}
		return path;
	}

	public List<String> bestPath(List<String> candidates, Sentence outputs) {
		List<Iterable<Integer>> icandidates = new ArrayList<Iterable<Integer>>(
				outputs.size());
		List<Tree> leaves = new LinkedList<Tree>();

		for (String candidate : candidates) {
			leaves.clear();

			List<Integer> candidate_tags = new LinkedList<Integer>();
			Tree tree = top_level_.get(candidate);

			if (tree == null) {
				throw new RuntimeException(String.format(
						"Tree is null! candidate: %s", candidate));
			}

			tree.getLeaves(leaves);
			for (Tree leaf : leaves) {
				Integer tag_index = tag_table_.toIndex(leaf.getName());
				candidate_tags.add(tag_index);
			}
			icandidates.add(candidate_tags);
		}

		return bestPath_(icandidates, outputs);
	}
}
