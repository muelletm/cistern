package experimental.test.analyzer;

import static org.junit.Assert.*;

import org.junit.Test;

import experimental.analyzer.Analyzer;
import experimental.analyzer.AnalyzerInstance;
import experimental.analyzer.AnalyzerTrainer;
import experimental.analyzer.TreebankAnalyzerResult;
import experimental.analyzer.simple.SimpleAnalyzerTrainer;

public class SimpleAnalyzerTest {

	public String getFilenameName(String name) {
		return String
				.format("form-index=0,tag-index=2,morph-index=3,res:///experimental/test/analyzer/data/%s", name);
	}

	@Test
	public void test() {
		AnalyzerTrainer[] trainers = { new SimpleAnalyzerTrainer() };
		for (AnalyzerTrainer trainer : trainers) {
			trainer.setOptions("mode=classifier");
			Analyzer analyzer = trainer.train(AnalyzerInstance.getTreebankInstances(getFilenameName("en/trn.tsv")));
			TreebankAnalyzerResult.logResult(analyzer, getFilenameName("en/dev.tsv"));
		}
	}

}
