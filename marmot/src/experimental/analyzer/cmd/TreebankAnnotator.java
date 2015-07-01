// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package experimental.analyzer.cmd;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import marmot.util.FileUtils;
import experimental.analyzer.Analyzer;
import experimental.analyzer.AnalyzerInstance;
import experimental.analyzer.AnalyzerReading;
import experimental.analyzer.AnalyzerTrainer;
import experimental.analyzer.TreebankAnalyzerResult;

public class TreebankAnnotator {

	public static void main(String[] args) {
		String model_file = args[0];
		
		Analyzer analyzer = FileUtils.loadFromFile(model_file);

		for (int i = 1; i < args.length; i += 2) {
			String test_file = args[i];
			String pred_file = args[i + 1];
			System.err.println("File:" + test_file);
			test(analyzer, test_file);
			annotate(analyzer, test_file, pred_file);
		}
	}

	public static void annotate(Analyzer analyzer, String test_file,
			String pred_file) {
		Collection<AnalyzerInstance> instances = AnalyzerInstance.getTreebankInstances(test_file);
		try {
			BufferedWriter writer = new BufferedWriter(
					new FileWriter(pred_file));
			for (AnalyzerInstance instance : instances) {
				if (analyzer.isUnknown(instance)) {

					Collection<AnalyzerReading> readings = analyzer
							.analyze(instance);

					writer.write(instance.getForm());
					writer.write('\t');
//					writer.write(analyzer.represent(instance));
//					writer.write('\t');
					writer.write(readings.toString());
					writer.newLine();
				}
			}
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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

	public static void test(Analyzer analyzer, String test_file) {
		TreebankAnalyzerResult.logResult(analyzer, test_file);
	}

}
