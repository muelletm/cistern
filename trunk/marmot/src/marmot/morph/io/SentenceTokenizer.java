// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.io;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import marmot.core.Sequence;
import marmot.core.Token;
import marmot.morph.Sentence;
import marmot.morph.Word;

public class SentenceTokenizer {

	private static final char SEPERATOR_ = '\t';
	private static final String EMPTY_ = "_";

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

		}

		if (!buffer.isEmpty())
			segments.add(new Sentence(buffer));

		return segments;
	}

	public static void main(String[] args) throws IOException {

		List<Sequence> sentences = new LinkedList<Sequence>();

		for (Sequence sentence : new SentenceReader(args[0])) {
			sentences.add(sentence);
		}

		int blocksize = Integer.parseInt(args[1]);

		Writer writer = new OutputStreamWriter(System.out);

		for (Sequence sequence : resegment(sentences, blocksize)) {
			Sentence sentence = (Sentence) sequence;
			for (int i = 0; i < sentence.size(); i++) {
				Word word = sentence.getWord(i);

				writer.append(Integer.toString(i + 1));
				writer.append(SEPERATOR_);
				writer.append(word.getWordForm());

				writer.append(SEPERATOR_);
				writer.append((word.getPosTag() != null) ? word.getPosTag()
						: EMPTY_);

				writer.append(SEPERATOR_);
				writer.append((word.getMorphTag() != null) ? word.getMorphTag()
						: EMPTY_);
				
				writer.append('\n');
			}
			
			writer.append('\n');
		}
	}

}
