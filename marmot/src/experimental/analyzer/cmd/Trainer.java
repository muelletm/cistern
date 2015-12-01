// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package experimental.analyzer.cmd;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import experimental.analyzer.Analyzer;
import experimental.analyzer.AnalyzerInstance;
import experimental.analyzer.AnalyzerReading;
import experimental.analyzer.AnalyzerResult;
import experimental.analyzer.AnalyzerTrainer;

import marmot.util.FileUtils;

public class Trainer {
	
	public static void main(String[] args) {
		
		String model_type = args[0];
		String options_string = args[1];
		String output_file = args[2];
		String train_file = args[3];
		
		Analyzer analyzer = train(model_type, options_string, train_file);
		
		for (int i=3; i < args.length; i += 2) {
			String test_file = args[i];
			String pred_file = args[i + 1];
			System.err.println("File:" + test_file);
			test(analyzer, test_file);
			annotate(analyzer, test_file, pred_file);			
		}
		
		FileUtils.saveToFile(analyzer, output_file);
	}
	
	private static void annotate(Analyzer analyzer, String test_file,
			String pred_file) {
		Collection<AnalyzerInstance> instances = AnalyzerInstance.getInstances(test_file);
		
		try {
			Writer writer = FileUtils.openFileWriter(pred_file);
			for (AnalyzerInstance instance : instances) {
				Collection<AnalyzerReading> readings = analyzer.analyze(instance);
				writer.write(instance.getForm());
				writer.write('\t');
				writer.write(analyzer.represent(instance));
				writer.write('\t');
				writer.write(readings.toString());
				writer.write('\n');			}
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
		
		Collection<AnalyzerInstance> training_instances = AnalyzerInstance.getInstances(train_file);
		Analyzer analyzer = trainer.train(training_instances);
		
		return analyzer;
	}

	public static void test(Analyzer analyzer, String test_file) {
		AnalyzerResult.logResult(analyzer, test_file);
	}

}
