// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.cmd;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import marmot.core.Sequence;
import marmot.core.Tagger;
import marmot.core.Token;
import marmot.morph.MorphEvaluator;
import marmot.morph.MorphModel;
import marmot.morph.MorphOptions;
import marmot.morph.Word;
import marmot.morph.io.FileOptions;
import marmot.morph.io.SentenceReader;


public class Trainer {

	public static void main(String[] args) {
		MorphOptions options = new MorphOptions();
		options.setPropertiesFromStrings(args);
		
		options.dieIfPropertyIsEmpty(MorphOptions.TRAIN_FILE);
		options.dieIfPropertyIsEmpty(MorphOptions.MODEL_FILE);
		
		Tagger tagger = MorphModel.train(options);
		MorphModel model = (MorphModel) tagger.getModel();

		if (!options.getTestFile().isEmpty()) {
			List<Sequence> sentences = new LinkedList<Sequence>();
			
			SentenceReader reader = new SentenceReader(options.getTestFile());
			if (options.getTagMorph())
				reader.getFileOptions().dieIfPropertyIsEmpty(FileOptions.MORPH_INDEX);
			
			for (Sequence sentence : reader) {
				for (Token token : sentence) {
					Word word = (Word) token;
					model.addIndexes(word, false);
				}
				sentences.add(sentence);
			}

			MorphEvaluator.eval(tagger, sentences);

			if (!options.getPredFile().isEmpty()) {
				try {
					Writer writer = new BufferedWriter(new FileWriter(
							options.getPredFile()));
					Annotator.annotate(tagger, options.getTestFile(), writer);
					writer.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
}
