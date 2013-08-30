// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.io;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import marmot.core.Sequence;
import marmot.core.Token;
import marmot.morph.Sentence;
import marmot.morph.Word;


public class SentenceTokenizer implements Iterator<Sequence> {

	private static final String NEW_LINE = "<Newline>";
	private static final String SENTENCE_BOUNDARY = "<Sent>";

	private class WordIterator implements Iterator<Word> {

		Word word_;
		Iterator<Sequence> sentence_iterator_;
		Iterator<Token> token_iterator_;

		public WordIterator(List<Sequence> sequences) {
			sentence_iterator_ = sequences.iterator();
		}

		@Override
		public boolean hasNext() {
			return sentence_iterator_.hasNext() || (token_iterator_ != null) || word_ != null;
		}

		@Override
		public Word next() {
			if (word_ != null) {
				Word word = word_;
				word_ = null;
				return word;
			}

			if (token_iterator_ == null) {
				token_iterator_ = sentence_iterator_.next().iterator();
			}

			Token token = token_iterator_.next();
			Word word = (Word) token;

			if (!token_iterator_.hasNext()) {
				if (!isPunct(word.getWordForm())) {
					word_ = getNewLine();
				} else {
					word = word.shallowCopy();
					if (word.getPosTag() != null) {
						word.setPosTag(SENTENCE_BOUNDARY + "|"
								+ word.getPosTag());
					}
				}
			}

			return word;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	WordIterator iterator_;
	int blocksize_;
	List<Word> buffer_;

	public SentenceTokenizer(List<Sequence> sentences, int blocksize) {
		iterator_ = new WordIterator(sentences);
		blocksize_ = blocksize;
		buffer_ = new ArrayList<Word>(blocksize);
	}

	public static List<Sequence> resegment(List<Sequence> sentences,
			int blocksize) {
		List<Sequence> segments = new LinkedList<Sequence>();

		List<Word> buffer = new ArrayList<Word>(blocksize);

		for (Sequence sequence : sentences) {

			for (Token token : sequence) {

				if (buffer.size() == blocksize) {
					segments.add(new Sentence(buffer));
					buffer.clear();
				}

				buffer.add((Word) token);

			}

			Word last_word = buffer.get(buffer.size() - 1);
			if (!isPunct(last_word.getWordForm())) {
				buffer.add(getNewLine());
			} else {
				Word word = last_word.shallowCopy();
				word.setPosTag(SENTENCE_BOUNDARY + "|" + last_word.getPosTag());
				buffer.set(buffer.size() - 1, word);
			}

		}

		if (!buffer.isEmpty())
			segments.add(new Sentence(buffer));

		return segments;
	}

	private static boolean isPunct(String word_form) {
		for (int i = 0; i < word_form.length(); i++) {
			char c = word_form.charAt(i);
			if (Character.isLetterOrDigit(c)) {
				return false;
			}
			if (Character.isWhitespace(c)) {
				return false;
			}
		}
		return true;
	}

	private static Word getNewLine() {
		return new Word(NEW_LINE, SENTENCE_BOUNDARY, "_", null);
	}

	@Override
	public boolean hasNext() {
		return iterator_.hasNext();
	}

	@Override
	public Sequence next() {
		buffer_.clear();

		while (iterator_.hasNext() && buffer_.size() < blocksize_) {
			buffer_.add(iterator_.next());
		}

		return new Sentence(buffer_);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
