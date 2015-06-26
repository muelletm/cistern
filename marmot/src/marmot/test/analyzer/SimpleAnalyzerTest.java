package marmot.test.analyzer;

import static org.junit.Assert.*;

import marmot.morph.MorphDictionaryOptions;

import org.junit.Test;

import experimental.analyzer.Analyzer;
import experimental.analyzer.AnalyzerInstance;
import experimental.analyzer.AnalyzerResult;
import experimental.analyzer.AnalyzerTrainer;
import experimental.analyzer.simple.SimpleAnalyzerTrainer;
import experimental.analyzer.tagger.TaggerAnalyzerTrainer;

public class SimpleAnalyzerTest {

	public String getFilenameName(String name) {
		return String
				.format("form-index=0,tag-index=2,morph-index=3,res:///marmot/test/analyzer/%s",
						name);
	}

	@Test
	public void test() {

		MorphDictionaryOptions options = MorphDictionaryOptions
				.parse("dense=true,res:///marmot/test/analyzer/cwindow_d100_w5.txt");

		AnalyzerTrainer[] trainers = { new SimpleAnalyzerTrainer() };

		for (AnalyzerTrainer trainer : trainers) {

			Analyzer analyzer = trainer.train(AnalyzerInstance
					.getInstances(getFilenameName("de.trn_1k.tsv")));
			AnalyzerResult
					.logResult(analyzer, getFilenameName("de.trn_1k.tsv"));
			AnalyzerResult.logResult(analyzer, getFilenameName("de.dev.tsv"), 0);

//			analyzer = trainer.train(AnalyzerInstance
//					.getInstances(getFilenameName("de.trn_10k.tsv")));
//			AnalyzerResult
//					.logResult(analyzer, getFilenameName("de.trn_10k.tsv"));
//			AnalyzerResult.logResult(analyzer, getFilenameName("de.dev.tsv"));
			
		}

	}

}
