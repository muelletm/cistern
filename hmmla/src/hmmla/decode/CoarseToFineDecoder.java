// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.decode;

import hmmla.hmm.HmmModel;
import hmmla.hmm.HmmTrainer;
import hmmla.hmm.Model;
import hmmla.hmm.Statistics;
import hmmla.hmm.Tree;
import hmmla.io.Sentence;
import hmmla.io.Token;
import hmmla.splitmerge.BackwardChart;
import hmmla.splitmerge.ForwardChart;
import hmmla.util.Counter;
import hmmla.util.Numerics;
import hmmla.util.SymbolTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;



public class CoarseToFineDecoder implements Decoder {

	private Model[] models_;
	private HmmTrainer trainer_;
	private ForwardChart[] forwards_;
	private BackwardChart[] backwards_;
	private boolean decode_toplevel_;
	private boolean product_;

	public CoarseToFineDecoder(Model model, HmmTrainer trainer,
			boolean decode_toplevel, boolean product) {
		trainer_ = trainer;
		decode_toplevel_ = decode_toplevel;
		product_ = product;	
		init(model);
	}

	protected void init(Model model) {
		int max_level = model.getLevel();
		models_ = new Model[max_level + 1];
		forwards_ = new ForwardChart[max_level + 1];
		backwards_ = new BackwardChart[max_level + 1];
		List<Tree> trees = new LinkedList<Tree>();

		models_[max_level] = model;

		for (int level = max_level - 1; level >= 0; level--) {
			Model new_model = new Model(model);

			// Get Tags at current level
			trees.clear();
			for (Tree tree : model.getTopLevel().values()) {
				tree.getChildrenWithLevel(trees, level);
			}

			SymbolTable<String> tag_table = new SymbolTable<String>();
			tag_table.toIndex(Model.BorderSymbol, true);
			Map<String, Tree> clustering = new HashMap<String, Tree>();
			for (Tree tree : trees) {
				clustering.put(tree.getName(), tree);
				tag_table.toIndex(tree.getName(), true);
			}

			Statistics statistics = new Statistics(tag_table.size(), model
					.getWordTable().size());
			new_model.setClustering(clustering);
			new_model.setTagTable(tag_table);
			new_model.setStatistics(statistics);
			collectStatistics(new_model, model);

			models_[level] = new_model;
			model = new_model;

		}

		for (int level = max_level; level >= 0; level--) {
			model = models_[level];
			HmmModel hmm_model = trainer_.train(model);
			SymbolTable<String> tag_table = model.getTagTable();
			forwards_[level] = new ForwardChart();
			forwards_[level].init(tag_table.size(), hmm_model);
			backwards_[level] = new BackwardChart();
			backwards_[level].init(tag_table.size(), hmm_model);
		}
	}

	private void collectStatistics(Model new_model, Model model) {
		Statistics new_statistics = new_model.getStatistics();
		Statistics statistics = model.getStatistics();

		SymbolTable<String> new_tag_table = new_model.getTagTable();
		SymbolTable<String> tag_table = model.getTagTable();

		for (Entry<String, Integer> entry : tag_table.entrySet()) {
			int index = entry.getValue();
			String name = entry.getKey();
			int new_index = getNewIndex(name, new_tag_table,
					model.getClustering());

			for (int output = 0; output < statistics.getNumOutputs(); output++) {
				double f = statistics.getEmissions(index, output);
				new_statistics.addEmissions(new_index, output, f);
			}

			for (Entry<String, Integer> entry2 : tag_table.entrySet()) {
				int index2 = entry2.getValue();
				String name2 = entry2.getKey();
				int new_index2 = getNewIndex(name2, new_tag_table,
						model.getClustering());

				double f = statistics.getTransitions(index, index2);
				new_statistics.addTransitions(new_index, new_index2, f);
			}
		}
	}

	private int getNewIndex(String name, SymbolTable<String> tag_table,
			Map<String, Tree> clustering) {
		try {
			return tag_table.toIndex(name);
		} catch (NoSuchElementException e) {
		}
		Tree tree = clustering.get(name);
		String parent_name = tree.getParent().getName();
		try {
			return tag_table.toIndex(parent_name);
		} catch (NoSuchElementException e2) {
			throw new IllegalStateException("Inconsistent Tree hierarchy!");
		}
	}

	@Override
	public List<String> bestPath(Sentence sentence) {
		
		// We want indexes for the coarse model!
		SymbolTable<String> tag_table = models_[0].getTagTable();

		List<Iterable<Integer>> candidates = getInitialCandidates(sentence, tag_table);

		for (int level = 0; level < models_.length; level++) {
			ForwardChart forward = forwards_[level];
			BackwardChart backward = backwards_[level];

			forward.update(candidates, sentence);
			Collections.reverse(candidates);
			backward.update(candidates, sentence);
			Collections.reverse(candidates);

			double logZ = forward.score();

			// Get new candidates!
			if (level < models_.length - 1) {

				tag_table = models_[level]
						.getTagTable();
				SymbolTable<String> next_tag_table = models_[level + 1]
						.getTagTable();

				for (int t = 0; t < sentence.size(); t++) {

					List<Integer> new_candidates = new LinkedList<Integer>();

					for (Integer index : candidates.get(t)) {
						double log_prob = forward.score(t, index)
								+ backward.score(t, index) - logZ;
						double prob = Math.exp(log_prob);
						if (prob > 1e-2) {
							String name = tag_table.toSymbol(index);
							Tree tree = models_[level].getClustering()
									.get(name);

							if (tree.getLevel() >= level + 1) {
								int new_index = next_tag_table.toIndex(name);
								new_candidates.add(new_index);
							} else {
								String child_name = tree.getLeft().getName();
								int new_index = next_tag_table
										.toIndex(child_name);
								new_candidates.add(new_index);
								child_name = tree.getRight().getName();
								new_index = next_tag_table
										.toIndex(child_name);
								new_candidates.add(new_index);
							}

						}

					}
					candidates.set(t, new_candidates);
				}
			}
		}

		if (product_) {
			return bestProductPath(models_[models_.length - 1],
					forwards_[models_.length - 1],
					backwards_[models_.length - 1], candidates, sentence);
		}

		return bestPath(models_[models_.length - 1],
				forwards_[models_.length - 1], backwards_[models_.length - 1],
				candidates, sentence.size());

	}

	private List<String> bestPath(Model model, ForwardChart forward,
			BackwardChart backward, List<Iterable<Integer>> candidates, int T) {

		SymbolTable<String> tag_table = model.getTagTable();
		Map<String, Tree> clustering = model.getClustering();

		List<String> path = new ArrayList<String>(T);
		for (int t = 0; t < T; t++) {
			Counter<String> counter = new Counter<String>(
					Double.NEGATIVE_INFINITY);
			double bestP = Double.NEGATIVE_INFINITY;
			String bestName = null;
			for (Integer candidate : candidates.get(t)) {
				String name = tag_table.toSymbol(candidate);
				double prob = forward.score(t, candidate)
						+ backward.score(t, candidate);

				if (decode_toplevel_) {
					Tree tree = clustering.get(name);
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

	private List<String> bestProductPath(Model model, ForwardChart forward,
			BackwardChart backward, List<Iterable<Integer>> candidates,
			List<Token> sentence) {

		double logZ = forward.score();

		// Calculate posterior probabilities.
		double[][] state_scores = new double[sentence.size()][];
		double[][][] transition_scores = new double[sentence.size()][][];

		SymbolTable<String> tag_table = model.getTagTable();
		List<SymbolTable<String>> tables = new ArrayList<SymbolTable<String>>(
				sentence.size());
		HmmModel hmm_model = forward.getHmmModel();

		double[] emmission_scores = new double[model.getTagTable().size()];

		for (int t = 0; t < sentence.size(); t++) {

			List<Integer> current_candidates = (List<Integer>) candidates
					.get(t);

			tables.add(new SymbolTable<String>());
			state_scores[t] = new double[current_candidates.size()];
			if (t > 0) {
				List<Integer> last_candidates = (List<Integer>) candidates
						.get(t - 1);
				transition_scores[t] = new double[last_candidates.size()][current_candidates
						.size()];
				Arrays.fill(emmission_scores, 0.0);
				hmm_model.getEmissions(sentence.get(t).getWordForm(), emmission_scores);
			}

			for (int candidate : candidates.get(t)) {
				String name = tag_table.toSymbol(candidate);
				Tree tree = model.getClustering().get(name);
				String parent_name = tree.getRoot().getName();
				int index = tables.get(t).toIndex(parent_name, true);
				double log_prob = forward.score(t, candidate)
						+ backward.score(t, candidate) - logZ;
				double prob = Math.exp(log_prob);
				assert prob > 0.0 && prob <= 1.0;
				state_scores[t][index] += prob;

				if (t > 0) {
					for (int last_candidate : candidates.get(t - 1)) {
						String last_name = tag_table.toSymbol(last_candidate);
						Tree last_tree = model.getClustering().get(last_name);
						String last_root_name = last_tree.getRoot().getName();
						int last_index = tables.get(t - 1).toIndex(
								last_root_name);
						log_prob = forward.score(t - 1, last_candidate)
								+ emmission_scores[candidate]
								+ hmm_model.getTransitions(last_candidate,
										candidate)
								+ backward.score(t, candidate) - logZ;
						prob = Math.exp(log_prob);
						assert prob > 0.0 && prob <= 1.0;
						transition_scores[t][last_index][index] += prob;
					}
				}
			}
		}

		// Fill Viterbi charts.
		double chart[][] = new double[sentence.size()][];
		int backtrace[][] = new int[sentence.size()][];

		for (int t = 0; t < sentence.size(); t++) {
			chart[t] = new double[tables.get(t).size()];
			backtrace[t] = new int[tables.get(t).size()];
			Arrays.fill(backtrace[t], -1);

			if (t > 0) {
				for (int last_index = 0; last_index < tables.get(t - 1).size(); last_index++) {
					for (int index = 0; index < tables.get(t).size(); index++) {

						assert chart[t - 1][last_index] > 0.0;

						double prob = chart[t - 1][last_index]
								* transition_scores[t][last_index][index];
						if (prob > chart[t][index]) {
							chart[t][index] = prob;
							backtrace[t][index] = last_index;
						}
					}
				}
			} else {
				Arrays.fill(chart[0], 1.);
			}

			for (int index = 0; index < tables.get(t).size(); index++) {
				chart[t][index] *= state_scores[t][index];
			}
		}

		// Backtrace
		List<String> path = new ArrayList<String>(sentence.size());

		double best_prob = 0.0;
		int best_index = -1;
		int T = sentence.size();
		for (int index = 0; index < tables.get(T - 1).size(); index++) {
			double prob = chart[T - 1][index];
			if (prob > best_prob) {
				best_prob = prob;
				best_index = index;
			}
		}

		if (best_index == -1)
			throw new RuntimeException();

		path.add(tables.get(T - 1).toSymbol(best_index));

		for (int t = T - 1; t > 0; t--) {
			best_index = backtrace[t][best_index];
			if (best_index == -1)
				throw new RuntimeException();
			path.add(tables.get(t - 1).toSymbol(best_index));
		}

		Collections.reverse(path);
		return path;
	}

	protected List<Iterable<Integer>> getInitialCandidates(List<Token> sentence, SymbolTable<String> tag_table) {
		List<Iterable<Integer>> candidates = new ArrayList<Iterable<Integer>>(
				sentence.size());

		// Only the last model (the original model) contains the candidate maps!
		Model model = models_[models_.length - 1];

		for (int t = 0; t < sentence.size(); t++) {
			String word_form = sentence.get(t).getWordForm();
			List<String> string_candidates = model.getCandidates(word_form);
			candidates.add(tag_table.toIndexes(string_candidates));
		}

		return candidates;
	}

}
