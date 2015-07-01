// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package experimental.analyzer.cmd;

import java.util.Collection;

import marmot.util.FileUtils;
import experimental.analyzer.Analyzer;
import experimental.analyzer.AnalyzerInstance;
import experimental.analyzer.AnalyzerTrainer;

public class TreebankTrainer {

	public static void main(String[] args) {
		String model_type = args[0];
		String options_string = args[1];
		String output_file = args[2];
		String train_file = args[3];

		Analyzer analyzer = train(model_type, options_string, train_file);

		for (int i = 3; i < args.length; i += 2) {
			String test_file = args[i];
			String pred_file = args[i + 1];
			System.err.println("File:" + test_file);
			TreebankAnnotator.test(analyzer, test_file);
			TreebankAnnotator.annotate(analyzer, test_file, pred_file);
		}

		FileUtils.saveToFile(analyzer, output_file);
	}

	public static Analyzer train(String model_type, String options_string,
			String train_file) {

		AnalyzerTrainer trainer;
		try {
			Class<?> trainer_class = Class.forName(model_type);
			trainer = (AnalyzerTrainer) trainer_class.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		trainer.setOptions(options_string);

		Collection<AnalyzerInstance> training_instances = AnalyzerInstance
				.getTreebankInstances(train_file);
		Analyzer analyzer = trainer.train(training_instances);

		return analyzer;
	}

}
