package experimental.test.analyzer;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import experimental.analyzer.Analyzer;
import experimental.analyzer.AnalyzerInstance;
import experimental.analyzer.AnalyzerTrainer;
import experimental.analyzer.TreebankAnalyzerResult;
import experimental.analyzer.simple.SimpleAnalyzer;
import experimental.analyzer.simple.SimpleAnalyzerTrainer;
import experimental.analyzer.simple.SimpleEvaluator;

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
			
			SimpleAnalyzer sanalyzer = (SimpleAnalyzer) analyzer;
			SimpleEvaluator evaluator = new SimpleEvaluator();
			evaluator.eval(sanalyzer, AnalyzerInstance.getTreebankInstances(getFilenameName("en/dev.tsv")), Arrays.asList(1.0, 1.5, 2.0, 2.5, 5.0));
			
		}
	}

}
