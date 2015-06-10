// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.analyzer.cmd;

import java.util.Collection;

import marmot.analyzer.Analyzer;
import marmot.analyzer.AnalyzerInstance;
import marmot.analyzer.AnalyzerResult;
import marmot.analyzer.AnalyzerTrainer;
import marmot.util.FileUtils;

public class Trainer {
	
	public static void main(String[] args) {
		
		String model_type = args[0];
		String options_string = args[1];
		String output_file = args[2];
		String train_file = args[3];
		
		Analyzer lemmatizer = train(model_type, options_string, train_file);
		
		for (int i=4; i < args.length; i += 1) {
			String test_file = args[i];
			test(lemmatizer, test_file);
		}
		
		FileUtils.saveToFile(lemmatizer, output_file);
	}
	
//	static void annotate(Analyzer lemmatizer, String test_file, String pred_file) {
//		
//		try {
//			BufferedWriter writer = new BufferedWriter(new FileWriter(pred_file));
//			for (Sequence sequence : new SentenceReader(test_file)) {
//				for (Token token : sequence) {
//					LemmaInstance instance = LemmaInstance.getInstance((Word) token);
//					String plemma = lemmatizer.lemmatize(instance);
//					
//					writer.write(String.format("%s\t%s\n", instance, plemma));
//				}
//				writer.write('\n');				
//			}
//			writer.close();			
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//		
//	}

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
		
		//LemmaOptions options = trainer.getOptions();	
		//options.readArguments(options_string);
		//Logger logger = Logger.getLogger(Trainer.class.getName());
		//logger.info(options.report());

		Collection<AnalyzerInstance> training_instances = AnalyzerInstance.getInstances(train_file);
		Analyzer lemmatizer = trainer.train(training_instances);
		
		return lemmatizer;
	}

	public static void test(Analyzer lemmatizer, String test_file) {
		AnalyzerResult.logResult(lemmatizer, test_file);
	}

}
