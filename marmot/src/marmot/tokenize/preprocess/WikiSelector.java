// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.tokenize.preprocess;

import java.util.Iterator;

public class WikiSelector implements Iterable<Pair> {
	private int num_sentences_; 
	private boolean expand_;
	private String tokenized_file_;
	private String untokenized_file_;
	private int token_threshold_;
	private double score_threshold_;

	public WikiSelector(String untokenized_file, String tokenized_file, boolean expand, int num_sentences, int token_threshold, double score_threshold) { 
		num_sentences_ = num_sentences;

		expand_ = expand;
	
		untokenized_file_ = untokenized_file;
		tokenized_file_ = tokenized_file;
		
		token_threshold_ = token_threshold;
		score_threshold_ = score_threshold;
	}

	public WikiSelector(String untokenized_file, String tokenized_file, boolean expand, int max_sentences) {
		this(untokenized_file, tokenized_file, expand, max_sentences, 5, 0.01);
	}
	
	@Override
	public Iterator<Pair> iterator() {
		
		final WikiReader reader_ = new WikiReader(untokenized_file_, tokenized_file_, expand_);
		
		return new Iterator<Pair>() {

			Pair pair_ = null;
			int num_selected_sentences_ = 0;

			
			@Override
			public boolean hasNext() {
				return next_();
			}

			private boolean next_() {
				if (pair_ != null) {
					return true;
				}
				
				if (num_sentences_ > 0 && num_selected_sentences_ >= num_sentences_) {
					return false;
				}
				
				Pair pair = reader_.next();

				int num_tokens = pair.tokenized.split("\\s+").length;

				if (num_tokens > token_threshold_ && pair.score > score_threshold_) {
					num_selected_sentences_ += 1;
					pair_ = pair;
					
					return true;
					
				}
			
				return next_();
			}

			@Override
			public Pair next() {
				next_();
				Pair pair = pair_;
				pair_ = null;
				return pair;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
			
	}
}