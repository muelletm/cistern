// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.cmd;

import java.util.Iterator;

import marmot.morph.EvalResult;
import marmot.morph.MorphModel;
import marmot.morph.MorphTagger;
import marmot.util.FileUtils;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

public class ModelEvaluator {

	public static void main(String[] args) throws JSAPException {

		FlaggedOption opt;
		JSAP jsap = new JSAP();

		opt = new FlaggedOption("train-file").setRequired(true).setLongFlag(
				"train-file");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("test-file").setRequired(true).setLongFlag(
				"test-file");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("model-file").setRequired(true).setLongFlag("model-file");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("mdict").setRequired(false)
				.setLongFlag("mdict");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("rareness").setRequired(false)
				.setLongFlag("rareness").setStringParser(JSAP.INTEGER_PARSER)
				.setDefault("-1");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("punct").setRequired(false)
				.setLongFlag("punct").setStringParser(JSAP.BOOLEAN_PARSER)
				.setDefault("false");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("normalize").setRequired(false)
				.setLongFlag("normalize").setStringParser(JSAP.BOOLEAN_PARSER)
				.setDefault("false");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("verbose").setRequired(false)
				.setLongFlag("verbose").setStringParser(JSAP.BOOLEAN_PARSER)
				.setDefault("false");
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

		String[] test_files = config.getString("test-file").split(":");

		MorphTagger tagger = FileUtils.loadFromFile(config.getString("model-file"));				
		
		((MorphModel) tagger.getModel()).setVerbose(false);

		Evaluator eval = new Evaluator(config.getString("train-file"),
				config.getBoolean("normalize"), config.getString("mdict"));

		for (String test_file : test_files) {
			EvalResult result = eval.eval(tagger, test_file,
					config.getInt("rareness"), config.getBoolean("punct"));

			result.report(config.getBoolean("verbose"));
		}
	}

}
