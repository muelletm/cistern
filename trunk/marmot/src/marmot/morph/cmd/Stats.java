// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.cmd;

import java.util.LinkedList;
import java.util.List;

import marmot.core.Sequence;
import marmot.core.Token;
import marmot.morph.MorphModel;
import marmot.morph.MorphOptions;
import marmot.morph.Word;
import marmot.morph.io.SentenceReader;

public class Stats {

	public static void main(String[] args) {
		MorphOptions options = new MorphOptions();
		options.setPropertiesFromStrings(args);

		int train_tokens = 0;
		List<Sequence> train_sentences = new LinkedList<Sequence>();
		for (Sequence sequence : new SentenceReader(options.getTrainFile())) {
			train_sentences.add(sequence);
			train_tokens += sequence.size();
		}

		MorphModel model = new MorphModel();
		model.init(options, train_sentences);

		System.out.println("Train sentences: " + train_sentences.size());
		System.out.println("Train tokens: " + train_tokens);

		System.out.println("Pos tags: "
				+ (model.getTagTables().get(0).size() - 1));
		System.out.println("Morph tags: "
				+ (model.getTagTables().get(1).size() - 1));

		boolean has_test_file = options.getTestFile().length() > 0;
		
		if (has_test_file) {
			int test_tokens = 0;
			int oov_test_tokens = 0;
			List<Sequence> test_sentences = new LinkedList<Sequence>();
			for (Sequence sequence : new SentenceReader(options.getTestFile())) {
				test_sentences.add(sequence);

				for (Token token : sequence) {
					Word word = (Word) token;
					model.addIndexes(word, false);
					if (word.getWordFormIndex() < 0) {
						oov_test_tokens += 1;
					}
				}

				test_tokens += sequence.size();
			}

			System.out.println("OOV rate: " + (oov_test_tokens * 100.)
					/ test_tokens);

		}

	}

}
