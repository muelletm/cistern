// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.cmd;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

import marmot.core.Evaluator;
import marmot.core.Sequence;
import marmot.core.Token;
import marmot.morph.MorphEvaluator;
import marmot.morph.MorphModel;
import marmot.morph.MorphResult;
import marmot.morph.MorphTagger;
import marmot.morph.Word;
import marmot.morph.io.SentenceReader;
import marmot.util.FileUtils;

public class Test {

	public static void main(String[] args) throws JSAPException {
		FlaggedOption opt;
		JSAP jsap = new JSAP();

		opt = new FlaggedOption("file").setRequired(true).setLongFlag("file");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("model-file").setRequired(true).setLongFlag(
				"model-file");
		jsap.registerParameter(opt);

		JSAPResult config = jsap.parse(args);

		if (!config.success()) {
			for (Iterator<?> errs = config.getErrorMessageIterator(); errs
					.hasNext();) {
				System.err.println("Error: " + errs.next());
			}
			System.err.println("Usage: ");
			System.err.println(jsap.getUsage());
			System.err.println(jsap.getHelp());
			System.err.println();
			System.exit(1);
		}

		MorphResult result = null;

		for (String modelfile : config.getString("model-file").split(",")) {

			MorphTagger tagger = FileUtils.loadFromFile(modelfile);
			MorphModel model = (MorphModel) tagger.getModel();
			model.setVerbose(false);

			List<Sequence> test_sentences = new LinkedList<Sequence>();
			for (Sequence sequence : new SentenceReader(
					config.getString("file"))) {
				for (Token token : sequence) {
					Word word = (Word) token;
					model.addIndexes(word, false);
				}
				test_sentences.add(sequence);
			}

			Evaluator evaluator = new MorphEvaluator(test_sentences);

			MorphResult current_result = (MorphResult) evaluator.eval(tagger);

			if (result == null) {
				result = current_result;
			} else {
				result.increment(current_result);
			}
			
		}

		System.out.print(result);
	}
}
