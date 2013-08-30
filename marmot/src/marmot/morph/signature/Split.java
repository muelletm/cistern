// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.signature;

import java.util.ArrayList;
import java.util.List;

public class Split implements Comparable<Split> {

	protected double score_;
	protected List<Trie> children_;
	protected int feature_index_;
	protected Trie trie_;
	protected boolean valid_;

	public Split(List<Feature> features, Trie trie, int feature_index) {
		feature_index_ = feature_index;
		trie_ = trie;
		Feature feature = features.get(feature_index);

		children_ = new ArrayList<Trie>(2);
		children_.add(new Trie(trie, feature_index, 0));
		children_.add(new Trie(trie, feature_index, 1));

		for (int index = 0; index < trie.words_.size(); index++) {
			boolean value = feature.feature(trie.words_.get(index));
			int int_value = value ? 0 : 1;

			children_.get(int_value).words_.add(trie.words_.get(index));
			children_.get(int_value).tags_.add(trie.tags_.get(index));

		}

		score_ = 0.;

		double[] entropy = trie.getEntropy();
		valid_ = true;

		for (int k = 0; k < entropy.length; k++) {

			double current_score = entropy[k];
			
			for (Trie child : children_) {
				double p = child.words_.size() / (double) trie.words_.size();

				if (p < 0.001 || child.words_.size() < 50) {
					valid_ = false;
					break;
				}

				double[] child_entropy = child.getEntropy();				
				current_score -= p * child_entropy[k];
			}
			
			score_ += current_score;

		}

		score_ *= trie.words_.size();
	}

	@Override
	public int compareTo(Split o) {
		return -Double.compare(score_, o.score_);
	}

	public static List<String> shorten(List<String> list) {
		if (list.size() < 5)
			return list;
		return list.subList(0, 5);
	}

}
