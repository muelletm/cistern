// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.io;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

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

				while (line_iterator_.hasNext()) {
					List<String> row = line_iterator_.next();
					if (row.isEmpty()) {
						break;
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

				number_++;

				Sentence sentence = new Sentence(tokens);

				return sentence;
			}

			private String check_index(int index, String string, List<String> row, boolean check_zero) {
				if ((index < 0 && check_zero) || index >= row.size()) {
					RuntimeException e = new RuntimeException(String.format(
							"%s out of range: %d : %s\n", index, string, row));
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
