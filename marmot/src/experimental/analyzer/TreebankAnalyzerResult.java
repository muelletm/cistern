package experimental.analyzer;

import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Logger;

public class TreebankAnalyzerResult {

	private static final Logger logger_ = Logger
			.getLogger(TreebankAnalyzerResult.class.getName());

	int num_tokens_;
	int num_types_;

	int type_ambiguity_;
	int token_ambiguity_;

	double type_coverage_;
	int token_coverage_;

	public static void logResult(Analyzer analyzer, String filename) {
		logResult(analyzer, filename, 100);
	}

	public static TreebankAnalyzerResult test(Analyzer analyzer, String filename) {
		return test(analyzer, AnalyzerInstance.getTreebankInstances(filename));
	}

	public static TreebankAnalyzerResult test(Analyzer analyzer,
			Collection<AnalyzerInstance> instances) {
		TreebankAnalyzerResult result = new TreebankAnalyzerResult();
		for (AnalyzerInstance instance : instances) {
			result.test(analyzer, instance);
		}
		return result;
	}

	public void test(Analyzer analyzer, AnalyzerInstance instance) {

		if (analyzer.isUnknown(instance)) {

			Collection<AnalyzerReading> actual = new HashSet<>(
					analyzer.analyze(instance));
			Collection<AnalyzerReading> expected = instance.getReadings();

			int instance_count = 0;

			for (AnalyzerReading reading : expected) {
				instance_count += reading.getCount();

				if (actual.contains(reading)) {

					type_coverage_ += 1. / expected.size();
					token_coverage_ += reading.getCount();

				}
			}

			num_tokens_ += instance_count;
			num_types_ += 1;

			type_ambiguity_ += actual.size();
			token_ambiguity_ += actual.size() * instance_count;

		}
	}

	public static void logResult(Analyzer analyzer, String filename,
			int num_errors) {
		TreebankAnalyzerResult result = test(analyzer, filename);
		result.logToken();
		result.logType();
	}

	public void logType() {
		logTypeCoverage();
		logTypeAmbiguity();
	}

	private void logTypeAmbiguity() {
		logger_.info(String.format("Type Amb: %d / %d = %g", type_ambiguity_,
				num_types_, type_ambiguity_ / (double) num_types_));
	}

	private void logTypeCoverage() {
		logger_.info(String.format("Type Cov: %g / %d = %g", type_coverage_,
				num_types_, type_coverage_ / (double) num_types_));
	}

	public void logToken() {
		logTokenCoverage();
		logTokenAmbiguity();
	}

	private void logTokenAmbiguity() {
		logger_.info(String.format("Token Amb: %d / %d = %g", token_ambiguity_,
				num_tokens_, token_ambiguity_ / (double) num_tokens_));
	}

	private void logTokenCoverage() {
		logger_.info(String.format("Token Cov: %d / %d = %g", token_coverage_,
				num_tokens_, token_coverage_ / (double) num_tokens_));
	}

}
