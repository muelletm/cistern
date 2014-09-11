// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.signature;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import marmot.core.Sequence;
import marmot.core.Token;
import marmot.morph.Word;
import marmot.morph.io.SentenceReader;
import marmot.util.Counter;
import marmot.util.FileUtils;
import marmot.util.SymbolTable;


public class Trie implements Serializable {
	private static final long serialVersionUID = 1L;
	protected transient List<String> words_;
	protected transient List<List<List<Integer>>> tags_;
	private transient boolean[] feature_map_;

	private List<Trie> children_;
	private double[] entropy_;
	private Feature feature_;
	private int child_index_;
	private Trie parent_;
	private int index_;
	private Set<String> no_signature_;
	private boolean verbose_;

	public Trie(Trie trie, int feature_index, int child_index) {
		this(null, trie.verbose_);
		feature_map_ = new boolean[trie.feature_map_.length];
		System.arraycopy(trie.feature_map_, 0, feature_map_, 0,
				feature_map_.length);
		feature_map_[feature_index] = false;
		child_index_ = child_index;
		parent_ = trie;
	}

	public Trie(Set<String> no_signature, boolean verbose) {
		entropy_ = null;
		words_ = new ArrayList<String>();
		tags_ = new ArrayList<List<List<Integer>>>();
		child_index_ = -1;
		parent_ = null;
		no_signature_ = no_signature;
		verbose_ = verbose;
	}

	public void add(List<List<Integer>> tags, String word) {
		words_.add(word);
		tags_.add(tags);
	}

	public void split(int limit, Set<String> vocab) {
		children_ = null;
		List<Feature> features = getFeatures(vocab);
		int num_leaves = 1;
		PriorityQueue<Split> splits = new PriorityQueue<Split>();
		feature_map_ = new boolean[features.size()];
		Arrays.fill(feature_map_, true);

		List<Trie> tries = new LinkedList<Trie>();
		tries.add(this);

		while (num_leaves < limit && !tries.isEmpty()) {
			for (Trie trie : tries) {
				for (int feature_index = 0; feature_index < features.size(); feature_index++) {
					if (trie.feature_map_[feature_index]) {
						Split split = new Split(features, trie, feature_index);

						if (split.valid_) {
							splits.add(split);
						} else {
							trie.feature_map_[feature_index] = false;
						}
					}
				}
			}
			tries.clear();

			Split split;
			while (true) {
				split = splits.poll();

				if (split == null)
					break;

				if (split.trie_.isLeaf()) {
					break;
				}

			}

			if (split == null)
				break;

			Trie trie = split.trie_;
			assert trie.children_ == null;
			trie.children_ = split.children_;
			trie.feature_ = features.get(split.feature_index_);
			for (Trie child : trie.children_) {
				tries.add(child);
			}
			num_leaves += 1;
		}

		List<Trie> leaves = new LinkedList<Trie>();
		this.getLeafes(leaves);

		int words = 0;
		for (Trie leaf : leaves) {
			words += leaf.words_.size();
		}
		assert words_.size() == words;
		clear(0);
	}

	private List<Feature> getFeatures(Set<String> vocab) {
		List<Feature> features = new ArrayList<Feature>();

		features.add(new Feature() {
			private static final long serialVersionUID = 1L;

			@Override
			boolean feature(String word) {
				for (int index = 0; index < word.length(); index++) {
					char c = word.charAt(index);
					if (Character.isDigit(c)) {
						return true;
					}
				}
				return false;
			}

			@Override
			String getName() {
				return "HasDigit";
			}
		});

		features.add(new Feature() {
			private static final long serialVersionUID = 1L;

			@Override
			boolean feature(String word) {
				for (int index = 0; index < word.length(); index++) {
					char c = word.charAt(index);
					if (Character.isLetter(c)) {
						return true;
					}
				}
				return false;
			}

			@Override
			String getName() {
				return "HasLetter";
			}

		});

		features.add(new Feature() {
			private static final long serialVersionUID = 1L;

			@Override
			boolean feature(String word) {
				for (int index = 0; index < word.length(); index++) {
					char c = word.charAt(index);
					if (Character.isUpperCase(c)) {
						return true;
					}
				}
				return false;
			}

			@Override
			String getName() {
				return "HasUpper";
			}

		});

		features.add(new Feature() {
			private static final long serialVersionUID = 1L;

			@Override
			boolean feature(String word) {
				for (int index = 0; index < word.length(); index++) {
					char c = word.charAt(index);
					if (Character.isLowerCase(c)) {
						return true;
					}
				}
				return false;
			}

			@Override
			String getName() {
				return "HasLower";
			}

		});

		for (int length = 1; length < 10; length++) {

			final int length_ = length;

			features.add(new Feature() {

				private static final long serialVersionUID = 1L;

				@Override
				boolean feature(String word) {
					return word.length() > length_;
				}

				@Override
				String getName() {
					return "Length>" + length_;
				}

			});

		}

		Counter<Character> alphabet = new Counter<Character>();
		for (String word : words_) {
			for (int index = 0; index < word.length(); index++) {
				char c = Character.toLowerCase(word.charAt(index));
				alphabet.increment(c, 1.0);
			}
		}

		for (Map.Entry<Character, Double> entry : alphabet.entrySet()) {
			if (entry.getValue() > 50) {
				final char C = entry.getKey();
				features.add(new Feature() {

					private static final long serialVersionUID = 1L;

					@Override
					boolean feature(String word) {

						for (int index = 0; index < word.length(); index++) {
							char c = Character.toLowerCase(word.charAt(index));
							if (c == C) {
								return true;
							}
						}
						return false;
					}

					@Override
					String getName() {
						return "Contains=" + C;
					}

				});
			}
		}

		for (int position = 1; position <= 5; position++) {

			final int POSITION = position;

			for (Map.Entry<Character, Double> entry : alphabet.entrySet()) {

				if (entry.getValue() > 50) {
					final char C = entry.getKey();
					features.add(new Feature() {

						private static final long serialVersionUID = 1L;

						@Override
						boolean feature(String word) {

							int index = word.length() - POSITION;

							if (index < 0)
								return false;

							return Character.toLowerCase(word.charAt(index)) == C;
						}

						@Override
						String getName() {
							return "Char[-" + POSITION + "]=" + C;
						}

					});
				}
			}
		}

		for (int position = 0; position < 5; position++) {

			final int POSITION = position;

			for (Map.Entry<Character, Double> entry : alphabet.entrySet()) {

				if (entry.getValue() > 50) {
					final char C = entry.getKey();
					features.add(new Feature() {

						private static final long serialVersionUID = 1L;

						@Override
						boolean feature(String word) {

							int index = POSITION;

							if (index >= word.length())
								return false;

							return Character.toLowerCase(word.charAt(index)) == C;
						}

						@Override
						String getName() {
							return "Char[" + POSITION + "]=" + C;
						}

					});
				}
			}
		}

		final Set<String> known_lowercase_words = new HashSet<String>();
		for (String word : vocab) {
			if (word.toLowerCase().equals(word)) {
				known_lowercase_words.add(word);
			}
		}

		features.add(new Feature() {
			private static final long serialVersionUID = 1L;

			@Override
			String getName() {
				return "LowerIsKnown";
			}

			@Override
			boolean feature(String word) {

				String lower = word.toLowerCase();

				if (lower.equals(word)) {
					return true;
				}

				return known_lowercase_words.contains(lower);
			}
		});

		return features;
	}

	public boolean isLeaf() {
		return children_ == null;
	}

	public double[] getEntropy() {
		if (entropy_ == null) {

			if (tags_.isEmpty())
				return null;

			int K = tags_.get(0).size();
			entropy_ = new double[K];

			for (int k = 0; k < K; k++) {

				double entropy = 0.;

				Counter<Integer> counter = new Counter<Integer>();

				assert !tags_.get(k).isEmpty();

				for (List<List<Integer>> tag_list : tags_) {
					for (int tag : tag_list.get(k)) {
						counter.increment(tag, 1.0);
					}
				}

				// assert counter.size() > 0;

				for (double count : counter.counts()) {
					double prob = count / counter.totalCount();
					entropy -= prob * Math.log(prob);
				}

				entropy_[k] = entropy;

			}
		}
		return entropy_;

	}

	public String signature() {

		if (parent_ == null) {
			return "";
		}

		if (isLeaf()) {
			assert feature_ == null;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(parent_.signature());
		Feature feature = parent_.feature_;

		if (sb.length() > 0)
			sb.append(',');
		sb.append(feature.getName());
		sb.append('=');
		sb.append((child_index_ == 0) ? 't' : 'f');

		return sb.toString();

	}

	public int classify(String word) {
		if (no_signature_.contains(word))
			return -1;

		return classify_(word);
	}

	private int classify_(String word) {
		if (isLeaf()) {
			return index_;
		}

		assert feature_ != null;
		int value = feature_.feature(word) ? 0 : 1;

		return children_.get(value).classify_(word);
	}

	public void getLeafes(List<Trie> leaves) {
		if (isLeaf()) {
			leaves.add(this);
		} else {
			for (Trie child : children_) {
				child.getLeafes(leaves);
			}
		}
	}

	public int clear(int index) {
		if (isLeaf()) {
			if (verbose_) {
				System.err.println(index);
				System.err.println(Arrays.toString(getEntropy()));
				System.err.println(signature());
				System.err.println("words " + words_.size() + " "
						+ Split.shorten(words_));
				System.err.println();
			}

			index_ = index;
			index += 1;
		} else {
			index_ = -1;
			for (Trie child : children_) {
				index = child.clear(index);
			}
		}

		words_ = null;
		tags_ = null;
		feature_map_ = null;

		if (parent_ == null) {
			index_ = index;
		}

		return index;
	}

	public int getIndex() {
		return index_;
	}
	
	public static Trie train(String trainfile, boolean verbose) {
		return train(trainfile, verbose, 20, 1);
	}
	
	public static Trie train(String trainfile, boolean verbose, int num_folds, int K) {
		List<Sequence> sentences = new LinkedList<Sequence>();
		for (Sequence sentence : new SentenceReader(trainfile)) {
			sentences.add(sentence);
		}
		
		return train(sentences, verbose, num_folds, K);
	}
	
	public static Trie train(Collection<Sequence> sentences, boolean verbose) {
		return train(sentences, verbose, 20, 1);
	}
	
	public static Trie train(Collection<Sequence> sentences, boolean verbose, int num_folds, int K) {
		int sentences_per_fold = sentences.size() / num_folds;

		if (sentences.size() < num_folds) {
			throw new RuntimeException("Training set is to small: |sentences| = " + sentences.size() + " num folds =" + num_folds);
		}
		
		Set<String> known = new HashSet<String>();
		Map<String, List<List<Integer>>> map = new HashMap<String, List<List<Integer>>>();

		SymbolTable<String> tags = new SymbolTable<String>();
		Set<String> vocab = new HashSet<String>();
		for (Sequence sentence : sentences) {
			for (Token token : sentence) {
				Word word = (Word) token;
				vocab.add(word.getWordForm());
				tags.toIndex(word.getPosTag(), true);
			}
		}

		int start_index = 0;
		while (start_index < sentences.size()) {
			known.clear();

			int end_index = start_index + sentences_per_fold;
			if (end_index + sentences_per_fold >= sentences.size()) {
				end_index = sentences.size();
			}

			int index = 0;
			for (Sequence sentence : sentences) {
				if (index >= start_index && index < end_index) {
					for (Token token : sentence) {
						Word word = (Word) token;
						known.add(word.getWordForm());
					}
				}
				index++;
			}

			vocab.retainAll(known);

			start_index = end_index;
		}

		for (Sequence sentence : sentences) {
			for (int i = 0; i < sentence.size(); i++) {
				Word word = (Word) sentence.get(i);
				String form = word.getWordForm();
				if (!vocab.contains(form)) {

					List<List<Integer>> tag_list = map.get(form);

					if (tag_list == null) {
						tag_list = new LinkedList<List<Integer>>();
						map.put(form, tag_list);
						for (int k = 0; k < K; k++) {
							tag_list.add(new LinkedList<Integer>());
						}
					}

					for (int k = 0; k < K; k++) {

						int shifted_index = i + k - K / 2;

						if (shifted_index >= sentence.size()
								|| shifted_index < 0) {
							continue;
						}

						int tag = tags.toIndex(((Word) sentence
								.get(shifted_index)).getPosTag());
						tag_list.get(k).add(tag);

					}
				}
			}
		}

		Trie trie = new Trie(vocab, verbose);
		for (Map.Entry<String, List<List<Integer>>> entry : map.entrySet()) {
			trie.add(entry.getValue(), entry.getKey());
		}
		trie.split(100, vocab);
		
		return trie;
	} 

	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Usage: Trie form-index=?,tag-index=?,train-file outputfile");
			System.exit(1);
		}
		
		Trie trie = train(args[0], true);
		FileUtils.saveToFile(trie, args[1]);
	}

}
