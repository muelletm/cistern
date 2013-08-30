// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.cmd;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import marmot.core.FeatureVector;
import marmot.core.Sequence;
import marmot.core.Token;
import marmot.morph.MorphModel;
import marmot.morph.MorphOptions;
import marmot.morph.MorphWeightVector;
import marmot.morph.Word;
import marmot.morph.io.SentenceReader;



public class FeaturePrinter {

	public static void main(String[] args) {
		MorphOptions options = new MorphOptions();
		options.setPropertiesFromStrings(args);
		options.setProperty(MorphOptions.VECTOR_SIZE, "1");
		
		MorphModel model = new MorphModel();
		List<Sequence> sentences = new LinkedList<Sequence>();
		for (Sequence sentence : new SentenceReader(options.getTrainFile())) {
			sentences.add(sentence);
		}
		model.init(options, sentences);
		sentences = null;
		MorphWeightVector weights = new MorphWeightVector(options);
		weights.init(model, sentences);

		try {
			weights.setExtendFeatureSet(true);
			printFeatures(model, options, weights, options.getTrainFile(),
					"trn.txt.feat");
			weights.setExtendFeatureSet(false);
			printFeatures(model, options, weights, options.getTestFile(),
					"tst.txt.feat");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void printFeatures(MorphModel model, MorphOptions options,
			MorphWeightVector weights, String filename, String out_filename)
			throws IOException {
		String seperator = "\t";
		BufferedWriter writer = new BufferedWriter(new FileWriter(out_filename));

		for (Sequence sentence : new SentenceReader(filename)) {
			int index = 0;
			
			for (Token token : sentence) {
				Word word = (Word) token;
				model.addIndexes(word, false);
			}
			
			for (Token token : sentence) {
				Word word = (Word) token;
				
				FeatureVector vector = weights.extractStateFeatures(sentence, index);

				writer.write("pos=");
				writer.write(word.getPosTag());
				
				if (options.getTagMorph()) {
					writer.write("|");
					writer.write(word.getMorphTag());
				}
				for (int feature : vector) {
					writer.write(seperator);
					writer.write(toString(feature));
				}
				writer.write('\n');
				index++;
			}
			writer.write('\n');
		}

		writer.close();
	}

	private static String toString(int feature) {
		return Integer.toString(feature, Character.MAX_RADIX);
	}

}
