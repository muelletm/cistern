// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.io;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import marmot.core.Sequence;
import marmot.morph.Sentence;
import marmot.morph.Word;
import marmot.util.LineIterator;

public class SentenceTextReader implements Iterable<Sequence> {

	private String filename_;

	public SentenceTextReader(String filename) {
		filename_ = filename;
	}

	@Override
	public Iterator<Sequence> iterator() {
		
		final LineIterator iterator = new LineIterator(filename_);
		
		return new Iterator<Sequence>() {

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public Sequence next() {
				List<Word> tokens = new LinkedList<Word>();
							
				while (iterator.hasNext()) {
					
					List<String> fields = iterator.next();
					
					if (fields.isEmpty()) {					
						if (!tokens.isEmpty()) {
							break;
						}
					} else {
						
						for (String field : fields) {
							Word word = new Word(field);
							tokens.add(word);
						}
						
					}
					
				}
								
				return new Sentence(tokens);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

}
