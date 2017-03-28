// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.io;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Hashtable;

import marmot.core.Sequence;
import marmot.morph.Sentence;
import marmot.morph.Word;
import marmot.util.Converter;
import marmot.util.LineIterator;

public class SentenceReader implements Iterable<Sequence> {

	private FileOptions options_;

	public SentenceReader(String option_string) {
		this(new FileOptions(option_string));
	}

	public SentenceReader(FileOptions options) {
		options_ = options;
	}

	public Iterator<Sequence> iterator() {

		return new Iterator<Sequence>() {

			int number_ = 0;
			LineIterator line_iterator_ = new LineIterator(
					options_.getInputStream());

			@Override
			public Sequence next() {
				int form_index = options_.getFormIndex();
				int lemma_index = options_.getLemmaIndex();
				int tag_index = options_.getTagIndex();
				int morph_index = options_.getMorphIndex();
				List<Integer> token_feature_indexes = options_.getTokenFeatureIndex();

				if (!hasNext()) {
					throw new NoSuchElementException();
				}

				List<Word> tokens = new LinkedList<Word>();
				List<String> comments = new LinkedList<String>();
				Hashtable<Integer, String> segments = new Hashtable<Integer, String>();
				Hashtable<Integer, String> empty_nodes = new Hashtable<Integer, String>();

				while (line_iterator_.hasNext()) {
					List<String> row = line_iterator_.next();
					if (row.isEmpty()) {
						break;
					}
					if (row.get(0).charAt(0) == '#') { // Comment
						comments.add(row.get(0)); 
						continue;
					}
					if (row.get(0).charAt(0) == '|') { // Segment
						String segline = row.get(0);
						String[] splittok = segline.split("\\|\\$\\|");
						//System.err.println(segline);
						//System.err.println(splittok[0]);
						//System.err.println(splittok[1]);
						//System.err.println(splittok[2]);
						segments.put(Integer.parseInt(splittok[1]), splittok[2]);
						continue;
					}
					if (row.get(0).charAt(0) == '$') { // Empty node
						String segline = row.get(0);
						String[] splittok = segline.split("\\$\\|\\$");
						empty_nodes.put(Integer.parseInt(splittok[1]), splittok[2]);
						continue;
					}

					String word = check_index(form_index, "form_index", row, true);
					String lemma = check_index(lemma_index, "lemma_index", row, false);
					String tag = check_index(tag_index, "tag_index", row, false);
					String morph = check_index(morph_index, "morph_index", row, false);
					
							
					List<String> token_feature_list = null;
					List<String> weighted_token_feature_list = null;
					List<Double> weighted_token_feature_weight_list = null;

					for (int token_feature_index : token_feature_indexes) {
					
					if (token_feature_index >= 0 && token_feature_index < row.size()) {
						String[] token_features = row.get(token_feature_index)
								.split("#");
						

					
						for (String token_feature : token_features) {
							int colon_index = token_feature.indexOf(':');
							Double weight = null;
							if (colon_index > 0) {
								try {
									weight = Double.parseDouble(token_feature
											.substring(colon_index + 1));
									token_feature = token_feature.substring(0,
											colon_index);
								} catch (NumberFormatException e) {
									throw new RuntimeException("Cannot parse double. If this wasn't meant to be a float feature then replace the colon: " + token_feature);
								}
							}

							if (weight != null) {
								if (weighted_token_feature_list == null) {
									weighted_token_feature_list = new LinkedList<String>();
									weighted_token_feature_weight_list = new LinkedList<Double>();
								}
								
								weighted_token_feature_list.add(token_feature);
								weighted_token_feature_weight_list.add(weight);
							} else {
								if (token_feature_list == null) {
									token_feature_list =new LinkedList<String>();
								}
								token_feature_list.add(token_feature);
							}

						}

					}
					}

					tokens.add(new Word(word, lemma, tag, morph, Converter.toStringArray(token_feature_list), Converter.toStringArray(weighted_token_feature_list), Converter.toDoubleArray(weighted_token_feature_weight_list)));
				}

				if (tokens.isEmpty()) {
					System.err.println("Warning: Found empty sentence!");
				}

				number_+= tokens.size();

				Sentence sentence = new Sentence(tokens);
				sentence.setComments(comments);
				sentence.setSegments(segments);
				sentence.setEmptyNodes(empty_nodes);

				return sentence;
			}

			private String check_index(int index, String string, List<String> row, boolean check_zero) {
				if ((index < 0 && check_zero) || index >= row.size()) {
					RuntimeException e = new RuntimeException(String.format(
							"%s out of range: %d : %s\n", string, index, row));
					throw e;					
				}
				
				if (index < 0) {
					return null;
				}
				
				return row.get(index);
			}

			@Override
			public boolean hasNext() {
				int limit = options_.getLimit();
				if (limit >= 0 && number_ > limit) {
					return false;
				}
				return line_iterator_.hasNext();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}

	public FileOptions getFileOptions() {
		return options_;
	}

}
