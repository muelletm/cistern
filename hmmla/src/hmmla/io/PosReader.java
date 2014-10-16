// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.io;

import hmmla.util.LineIterator;
import hmmla.util.Mapping;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public class PosReader implements Iterable<Sentence> {

	private Mapping map_;
	private PosFileOptions options_;

	public PosReader(String option_string) {
		this(option_string, null);
	}

	public PosReader(String option_string, Mapping map) {
		this(new PosFileOptions(option_string), map);
	}
	
	public PosReader(PosFileOptions options, Mapping map) {
		options_ = options;
		map_ = map;
	}

	public Iterator<Sentence> iterator() {

		return new Iterator<Sentence>() {

			int number_ = 0;
			LineIterator lineIterator = new LineIterator(options_.getFilename());

			@Override
			public Sentence next() {				
				int form_index_ = options_.getFormIndex();
				int tag_index_ = options_.getTagIndex();

				if (!hasNext()) {
					throw new NoSuchElementException();
				}

				List<String> tags = new LinkedList<String>();
				List<String> words = new LinkedList<String>();

				while (lineIterator.hasNext()) {
					List<String> tokens = lineIterator.next();
					if (tokens.isEmpty()) {
						break;
					}

					if (form_index_ < 0 || form_index_ >= tokens.size()) {
						RuntimeException e = new RuntimeException("form_index out of range: "
								+ form_index_ + " : " + tokens);
						
						e.initCause(new Throwable("form_index"));
						throw e;
					}

					if (tag_index_ >= tokens.size()) {
						RuntimeException e = new RuntimeException("tag_index out of range: "
								+ tag_index_ + " : " + tokens);
						
						e.initCause(new Throwable("tag_index"));
						throw e;
					}

					String word = tokens.get(form_index_);
					words.add(word);

					String tag = null;
					if (tag_index_ >= 0) {
						tag = tokens.get(tag_index_);

						if (map_ != null) {
							tag = map_.get(tag);
						}
					}

					tags.add(tag);
				}

				number_++;
				return new Sentence(words, tags);
			}

			@Override
			public boolean hasNext() {
				int limit = options_.getLimit();
				if (limit >= 0 && number_ > limit) {
					return false;
				}
				return lineIterator.hasNext();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}

}
