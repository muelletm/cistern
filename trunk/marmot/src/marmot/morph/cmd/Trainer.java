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
import marmot.morph.MorphModel;
import marmot.morph.MorphOptions;
import marmot.morph.Word;
import marmot.morph.io.FileOptions;
import marmot.morph.io.SentenceReader;
import marmot.util.FileUtils;


public class Trainer {
	
	public static Tagger train(MorphOptions options) {
		long time = System.currentTimeMillis();
		List<Sequence> train_sentences = new LinkedList<Sequence>();

		SentenceReader reader = new SentenceReader(options.getTrainFile());
		if (options.getTagMorph())
			reader.getFileOptions().dieIfPropertyIsEmpty(
					FileOptions.MORPH_INDEX);

		for (Sequence sentence : reader) {
			train_sentences.add(sentence);
		}
		reader = null;

		List<Sequence> test_sentences = null;
		if (!options.getTestFile().isEmpty()) {
			reader = new SentenceReader(options.getTestFile());
			if (options.getTagMorph())
				reader.getFileOptions().dieIfPropertyIsEmpty(
						FileOptions.MORPH_INDEX);

			test_sentences = new LinkedList<Sequence>();
			for (Sequence sentence : reader) {
				test_sentences.add(sentence);
			}
			
			reader = null;
		}

		Tagger tagger = MorphModel.train(options, train_sentences, test_sentences);

		if (!options.getModelFile().isEmpty())			
			FileUtils.saveToFile(tagger, options.getModelFile());

		if (options.getVerbose())
			System.err.format("Training took: %ds\n",
					(System.currentTimeMillis() - time) / 1000);

		return tagger;
	}

	public static void main(String[] args) {
		MorphOptions options = new MorphOptions();
		options.setPropertiesFromStrings(args);
		
		options.dieIfPropertyIsEmpty(MorphOptions.TRAIN_FILE);
		options.dieIfPropertyIsEmpty(MorphOptions.MODEL_FILE);
		
		Tagger tagger = train(options);
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
