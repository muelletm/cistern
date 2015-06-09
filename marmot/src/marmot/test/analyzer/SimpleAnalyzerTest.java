package marmot.test.analyzer;

import static org.junit.Assert.*;

import marmot.analyzer.Analyzer;
import marmot.analyzer.AnalyzerInstance;
import marmot.analyzer.AnalyzerResult;
import marmot.analyzer.AnalyzerTrainer;
import marmot.analyzer.simple.SimpleAnalyzerTrainer;
import marmot.morph.MorphDictionaryOptions;

import org.junit.Test;

public class SimpleAnalyzerTest {

	public String getFilenameName(String name) {
		return String.format("form-index=0,tag-index=2,morph-index=3,res:///marmot/test/analyzer/%s", name);
	}
	
	@Test
	public void test() {
		
		MorphDictionaryOptions options = MorphDictionaryOptions.parse("dense=true,res:///marmot/test/analyzer/cwindow_d100_w5.txt");
		
		AnalyzerTrainer trainer = new SimpleAnalyzerTrainer(options);
		Analyzer analyzer = trainer.train(AnalyzerInstance.getInstances(getFilenameName("en.trn_1k.tsv")));
		AnalyzerResult.logResult(analyzer, getFilenameName("en.trn_1k.tsv"));
		AnalyzerResult.logResult(analyzer, getFilenameName("en.dev.tsv"));
		
//		Analyzer analyzer = trainer.train(AnalyzerInstance.getInstances(getFilenameName("de.trn_1k.tsv")));
//		AnalyzerResult.logResult(analyzer, getFilenameName("de.trn_1k.tsv"));
//		AnalyzerResult.logResult(analyzer, getFilenameName("de.dev.tsv"));
	}

}
