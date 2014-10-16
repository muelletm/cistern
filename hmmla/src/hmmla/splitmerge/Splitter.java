// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.splitmerge;

import hmmla.hmm.Model;
import hmmla.hmm.Statistics;
import hmmla.hmm.Tree;
import hmmla.util.SymbolTable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class Splitter {

	private Random rng_;
	private double randomness_;

	public Splitter(double randomness, Random rng) {
		this.randomness_ = randomness;
		this.rng_ = rng;
	}

	public void split(Model model) {
		SymbolTable<String> tag_table = model.getTagTable();
		SymbolTable<String> word_table = model.getWordTable();
		Map<String, Tree> clustering = model.getClustering();
		Statistics stats = model.getStatistics();

		int num_tags = tag_table.size();
		SymbolTable<String> new_tag_table = new SymbolTable<String>();
		Statistics new_statistics = new Statistics((num_tags - 1) * 2 + 1, word_table.size());
		Map<String, Tree> new_clustering = new HashMap<String, Tree>();

		new_tag_table.toIndex(Model.BorderSymbol, true);
		model.getTopLevel().get(Model.BorderSymbol).incrementLevel();

		for (int i = 1; i < num_tags; i++) {
			String name = tag_table.toSymbol(i);
			String lname = String.format("%s0", name);
			int left = new_tag_table.toIndex(lname, true);
			assert left == i;
			assert clustering.containsKey(name);
			new_clustering.put(lname, clustering.get(name).setLeft(lname));
		}

		for (int i = 1; i < num_tags; i++) {
			String name = tag_table.toSymbol(i);
			String rname = String.format("%s1", name);
			int right = new_tag_table.toIndex(rname, true);
			assert right == i + num_tags - 1;
			assert clustering.containsKey(name);
			new_clustering.put(rname, clustering.get(name).setRight(rname));
		}

		for (int index = 1; index < num_tags; index++) {
			double random_double;
			double freq;
			int left = index;
			int right = index + num_tags - 1;
			for (int o = 0; o < word_table.size(); o++) {
				freq = 0.5 * stats.getEmissions(index, o);

				random_double = (rng_.nextDouble() - 0.5) * 2.0 * freq
						* randomness_;

				assert (freq + random_double >= 0.0)
				&& (freq - random_double >= 0.0);
				
				new_statistics.setEmissions(left, o, freq + random_double);
				new_statistics.setEmissions(right, o, freq - random_double);
			}

			for (int i = 1; i < tag_table.size(); i++) {
				freq = 0.25 * stats.getTransitions(i, index);

				random_double = (rng_.nextDouble() - 0.5) * 2.0 * freq
						* randomness_;
				new_statistics.setTransitions(i, left, freq + random_double);
				new_statistics.setTransitions(i, right, freq - random_double);

				random_double = (rng_.nextDouble() - 0.5) * 2.0 * freq
						* randomness_;
				new_statistics.setTransitions(i + num_tags - 1, left, freq
						+ random_double);
				new_statistics.setTransitions(i + num_tags - 1, right, freq
						- random_double);

			}

			freq = 0.5 * stats.getTransitions(Model.BorderIndex, index);
			random_double = (rng_.nextDouble() - 0.5) * 2.0 * freq * randomness_;
			new_statistics.setTransitions(Model.BorderIndex, left, freq + random_double);
			new_statistics.setTransitions(Model.BorderIndex, right, freq - random_double);
			freq = 0.5 * stats.getTransitions(index, Model.BorderIndex);
			random_double = (rng_.nextDouble() - 0.5) * 2.0 * freq * randomness_;
			new_statistics.setTransitions(left, Model.BorderIndex, freq + random_double);
			new_statistics.setTransitions(right, Model.BorderIndex, freq - random_double);

		}


		model.setStatistics(new_statistics);
		model.setTagTable(new_tag_table);
		model.setClustering(new_clustering);
	}
}
