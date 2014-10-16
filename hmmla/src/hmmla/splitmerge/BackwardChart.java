// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.splitmerge;

import hmmla.hmm.Model;
import hmmla.io.Token;
import hmmla.util.Numerics;

import java.util.Iterator;
import java.util.List;


public class BackwardChart extends ForwardChart {

	@Override
	public synchronized void update(Iterable<Iterable<Integer>> tags,
			List<Token> outputs) {

		T = outputs.size() + 1;

		if (T == 0) {
			return;
		}

		if (T > maxT) {
			a = new double[T][N];
			maxT = T;
		}

		for (int t = 0; t < T; t++) {
			for (int i = 0; i < N; i++) {
				a[t][i] = Double.NEGATIVE_INFINITY;
			}
		}

		Iterator<Iterable<Integer>> iterator = tags.iterator();
		assert iterator.hasNext();
		Iterable<Integer> last_tags = iterator.next();

		double[] scores = new double[N];
		
		for (Integer i : last_tags) {
			a[T - 1][i] = _score(i, Model.BorderIndex);
		}
		int t = T - 2;
		

		while (iterator.hasNext()) {

			Iterable<Integer> current_tags = iterator.next();

			_score(outputs, t, scores);
			for (Integer i : current_tags) {
			for (Integer j : last_tags) {				
					double score = scores[j] + a[t + 1][j] + _score(i, j);
					a[t][i] = Numerics.sumLogProb(a[t][i], score);
				}
			}

			last_tags = current_tags;
			t--;
		}

		assert t == 0;

		_score(outputs, t, scores);
		for (Integer j : last_tags) {
			a[t][Model.BorderIndex] = Numerics.sumLogProb(a[t][Model.BorderIndex], scores[j] + _score(Model.BorderIndex, j));
		}
	}

	@Override
	public synchronized double score(int t, int tag) {
		return a[t + 1][tag];
	}

	@Override
	public synchronized double score() {
		return score(-1, Model.BorderIndex);
	}

}
