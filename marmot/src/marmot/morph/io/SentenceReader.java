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
				int tag_index = options_.getTagIndex();
				int morph_index = options_.getMorphIndex();
				int token_feature_index = options_.getTokenFeatureIndex();
				boolean over_tokenize = options_.getOverTokenizer();

				if (!hasNext()) {
					throw new NoSuchElementException();
				}

				List<Word> tokens = new LinkedList<Word>();

				while (line_iterator_.hasNext()) {
					List<String> row = line_iterator_.next();
					if (row.isEmpty()) {
						break;
					}

					if (form_index < 0 || form_index >= row.size()) {
						RuntimeException e = new RuntimeException(
								"form_index out of range: " + form_index
										+ " : " + row);
						throw e;
					}

					if (tag_index >= row.size()) {
						RuntimeException e = new RuntimeException(
								"tag_index out of range: " + tag_index + " : "
										+ row);
						throw e;
					}

					if (morph_index >= row.size()) {
						RuntimeException e = new RuntimeException(
								"morph_index out of range: " + tag_index
										+ " : " + row);
						throw e;
					}

					String word = row.get(form_index);
					

					String tag = null;
					if (tag_index >= 0) {
						tag = row.get(tag_index);
					}

					String morph = null;
					if (morph_index >= 0) {
						morph = row.get(morph_index);
					}
					
					String token_feature = null;
					if (token_feature_index >= 0) {
						token_feature = row.get(token_feature_index);
					}

					tokens.add(new Word(word, tag, morph, token_feature));
				}
				
				if (tokens.isEmpty()) {
					throw new RuntimeException("Error: Found empty sentence!");
				}

				number_++;
				
				Sentence sentence = new Sentence(tokens);
				
				if (over_tokenize) {
					sentence = Tokenizer.overTokenize(sentence);
				}
				
				return sentence;
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
