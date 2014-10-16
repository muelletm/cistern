// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.splitmerge;

import hmmla.hmm.Model;
import hmmla.hmm.Statistics;
import hmmla.hmm.Tree;
import hmmla.io.Sentence;
import hmmla.util.SymbolTable;
import hmmla.util.Tuple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;



public class Merger {
	private LossEstimator estimator_;
	
	public Merger(LossEstimator estimator) {
		estimator_ = estimator;
	}

	public double merge(Model model, Iterable<Sentence> reader, double mergeFactor) {
		SymbolTable<String> inputTable = model.getTagTable();
		int N = (inputTable.size() - 1) / 2;
		List<Tuple<Integer, Double>> tuples = new ArrayList<Tuple<Integer, Double>>(N);
		estimator_.estimateLosses(model, reader, tuples);
		Collections.sort(tuples);
		double loss = merge(model, tuples,
				(int) (N * mergeFactor));
		return loss;
	}

	public double merge(Model model, List<Tuple<Integer, Double>> tuples,
			int limit) {
		assert limit <= tuples.size();
		
		SymbolTable<String> tag_table = model.getTagTable();
		SymbolTable<String> word_table = model.getWordTable();
		Statistics statistics = model.getStatistics();
		
		int N = (tag_table.size() - 1) / 2;
		Set<Integer> set = new HashSet<Integer>();
		double loss = 0;
		for (int k = 0; k < limit; k++) {
			Tuple<Integer, Double> t = tuples.get(k);
			int lindex = t.x;
			int rindex = lindex + N;
			set.add(lindex);
			double f;

			for (int o = 0; o < word_table.size(); o++) {
				f = statistics.getEmissions(rindex, o);
				statistics.addEmissions(lindex, o, f);
				statistics.setEmissions(rindex, o, 0);
			}

			for (int i = 0; i < tag_table.size(); i++) {
				f = statistics.getTransitions(i, rindex);
				statistics.addTransitions(i, lindex, f);
				statistics.setTransitions(i, rindex, 0.0);

				f = statistics.getTransitions(rindex, i);
				statistics.addTransitions(lindex, i, f);
				statistics.setTransitions(rindex, i, 0.0);
			}

			f = statistics.getTransitions(rindex, lindex);

			statistics.addTransitions(lindex, lindex, f);
			statistics.setTransitions(rindex, lindex, 0.0);

			f = statistics.getTransitions(lindex, rindex);

			statistics.addTransitions(lindex, lindex, f);
			statistics.setTransitions(lindex, rindex, 0.0);

			loss += t.y;
		}
		
		Map<String, Tree> clustering = model.getClustering();

		SymbolTable<String> new_tagtable = new SymbolTable<String>();
		List<Tuple<Integer, Integer>> indexes = new LinkedList<Tuple<Integer, Integer>>();
	
		for (int i = 0; i < N + 1; i++) {
			int nindex;
			int lindex = i;
			int rindex = i + N;

			if (set.contains(lindex)) {
				// merge
				String leftName = tag_table.toSymbol(lindex);
				String rightName = tag_table.toSymbol(rindex);

				Tree parent = clustering.get(leftName).getParent();
				parent.prune();

				assert parent.getLeft() == null && parent.getRight() == null;

				clustering.remove(leftName);
				clustering.remove(rightName);

				String name = parent.getName();

				clustering.put(name, parent);
				nindex = new_tagtable.toIndex(name, true);
				indexes.add(new Tuple<Integer, Integer>(lindex, nindex));

			} else {
				nindex = new_tagtable.toIndex(tag_table.toSymbol(lindex),
						true);
				indexes.add(new Tuple<Integer, Integer>(lindex, nindex));

				if (lindex != Model.BorderIndex) {
					nindex = new_tagtable.toIndex(tag_table.toSymbol(rindex),
							true);
					indexes.add(new Tuple<Integer, Integer>(rindex, nindex));
				}
			}

		}

		Statistics new_statistics = new Statistics(indexes.size(), word_table.size());

		for (Tuple<Integer, Integer> tuple : indexes) {

			int newIndex = tuple.y;
			int oldIndex = tuple.x;

			for (int o = 0; o < word_table.size(); o++) {
				double f = statistics.getEmissions(oldIndex, o);
				new_statistics.setEmissions(newIndex, o, f);
				statistics.setEmissions(oldIndex, o, 0.0);
			}

			for (Tuple<Integer, Integer> tuple2 : indexes) {
				int newIndex2 = tuple2.y;
				int oldIndex2 = tuple2.x;
				double f;
				f = new_statistics.getTransitions(newIndex, newIndex2);
				assert f == 0.0;
				f = statistics.getTransitions(oldIndex, oldIndex2);
				new_statistics.setTransitions(newIndex, newIndex2, f);
				statistics.setTransitions(oldIndex, oldIndex2, 0.0);
			}

		}

		assert statistics.totalEmission() == 0.0;
		assert statistics.totalTransmission() == 0.0;

		model.setStatistics(new_statistics);
		model.setTagTable(new_tagtable);
		return loss;
	}
}
