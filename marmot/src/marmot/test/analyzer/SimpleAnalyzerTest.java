package marmot.test.analyzer;

import static org.junit.Assert.*;

import marmot.experimental.analyzer.Analyzer;
import marmot.experimental.analyzer.AnalyzerInstance;
import marmot.experimental.analyzer.AnalyzerResult;
import marmot.experimental.analyzer.AnalyzerTrainer;
import marmot.experimental.analyzer.simple.SimpleAnalyzerTrainer;
import marmot.experimental.analyzer.tagger.TaggerAnalyzerTrainer;
import marmot.morph.MorphDictionaryOptions;

import org.junit.Test;

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
