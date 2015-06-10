package marmot.test.analyzer;

import static org.junit.Assert.*;

import marmot.analyzer.Analyzer;
import marmot.analyzer.AnalyzerInstance;
import marmot.analyzer.AnalyzerResult;
import marmot.analyzer.AnalyzerTrainer;
import marmot.analyzer.simple.SimpleAnalyzerTrainer;
import marmot.analyzer.tagger.TaggerAnalyzerTrainer;
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

		AnalyzerTrainer[] trainers = { new TaggerAnalyzerTrainer(),
				new SimpleAnalyzerTrainer(null) };

		for (AnalyzerTrainer trainer : trainers) {

			Analyzer analyzer = trainer.train(AnalyzerInstance
					.getInstances(getFilenameName("en.trn_10k.tsv")));
			AnalyzerResult
					.logResult(analyzer, getFilenameName("en.trn_10k.tsv"));
			AnalyzerResult.logResult(analyzer, getFilenameName("en.dev.tsv"));

			analyzer = trainer.train(AnalyzerInstance
					.getInstances(getFilenameName("de.trn_10k.tsv")));
			AnalyzerResult
					.logResult(analyzer, getFilenameName("de.trn_10k.tsv"));
			AnalyzerResult.logResult(analyzer, getFilenameName("de.dev.tsv"));
			
		}

	}

}
