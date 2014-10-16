// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.splitmerge;

import hmmla.hmm.HmmModel;
import hmmla.hmm.Model;
import hmmla.io.Token;
import hmmla.util.Numerics;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;



public class ForwardChart {

	protected double a[][];
	protected int maxT;
	protected int N;
	protected int T;
	protected HmmModel model;

	public void init(int N, HmmModel model) {
		maxT = 0;
		this.N = N;
		this.model = model;
	}

	public HmmModel getHmmModel() {
		return model;
	}

	protected double _score(int i, int j) {
		return model.getTransitions(i, j);
	}

	public synchronized void update(Iterable<Iterable<Integer>> tags,
			List<Token> sentence) {

		T = sentence.size() + 1;

		if (T == 1) {
			return;
		}

		if (T > maxT) {
			a = new double[T][N];
			maxT = T;
		}

		for (int t = 0; t < T; t++) {
			Arrays.fill(a[t], Double.NEGATIVE_INFINITY);
		}

		double[] scores = new double[N];

		Iterator<Iterable<Integer>> iterator = tags.iterator();
		assert iterator.hasNext();
		Iterable<Integer> last_tags = iterator.next();

		_score(sentence, 0, scores);
		for (Integer i : last_tags) {
			a[0][i] = scores[i] + _score(Model.BorderIndex, i);		
			assert (a[0][i] != Double.NEGATIVE_INFINITY);
		}

		int t = 1;
		while (iterator.hasNext()) {

			Iterable<Integer> current_tags = iterator.next();

			_score(sentence, t, scores);
			for (Integer j : last_tags) {

				if (Double.isInfinite(a[t - 1][j])) {
					continue;
				}

				for (Integer i : current_tags) {
					double score = a[t - 1][j] + scores[i] + _score(j, i);
					a[t][i] = Numerics.sumLogProb(a[t][i], score);
				}
			}

			last_tags = current_tags;
			t++;
		}

		assert t == T - 1;

		for (Integer i : last_tags) {
			double score = a[t - 1][i] + _score(i, Model.BorderIndex);
			a[t][Model.BorderIndex] = Numerics.sumLogProb(score,
					a[t][Model.BorderIndex]);
		}
	}

	protected void _score(List<Token> outputs, int t, double[] scores) {
		Token token = outputs.get(t);
		Arrays.fill(scores, 0.0);
		model.getEmissions(token.getWordForm(), scores);
	}

	public synchronized double score(int index, int tag) {
		return a[index][tag];
	}

	public synchronized double score() {
		return score(T - 1, Model.BorderIndex);
	}

}
