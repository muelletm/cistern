package marmot.analyzer;

import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Logger;

public class AnalyzerResult {

	private int errors_;
	private int total_;
	private double macro_pre_;
	private double macro_rec_;
	
	
	public AnalyzerResult() {
		this(0, 0, 0., 0.);
	}
	
	public AnalyzerResult(int errors, int total, double macro_pre, double macro_rec) {
		errors_ = errors;
		total_ = total;
		macro_pre_ = macro_pre;
		macro_rec_ = macro_rec;
	}
	
	public void increment(AnalyzerResult result) {
		errors_ += result.errors_;
		total_ += result.total_;
		macro_pre_ += result.macro_pre_;
		macro_rec_ += result.macro_rec_;
	}

	public static void logResult(Analyzer analyzer, String filename) {
		AnalyzerResult result = test(analyzer, filename);
		result.logAcc();
		result.logFscore();
	}
	
	public static AnalyzerResult test(Analyzer analyzer, String filename) {
		return test(analyzer, AnalyzerInstance.getInstances(filename));
	}
	
	public static AnalyzerResult test(Analyzer analyzer, Collection<AnalyzerInstance> instances) {
		AnalyzerResult result = new AnalyzerResult();
		for (AnalyzerInstance instance : instances) {
			result.increment(test(analyzer, instance));
		}
		return result;
	}
	
	public static AnalyzerResult test(Analyzer analyzer, AnalyzerInstance instance) {
		Collection<AnalyzerTag> actual = AnalyzerReading.toTags(analyzer.analyze(instance));
		Collection<AnalyzerTag> expected = new HashSet<>(AnalyzerReading.toTags(instance.getReadings()));
		
		int correct = 0;
		
		for (AnalyzerTag reading : actual) {
			if (expected.contains(reading)) {
				correct ++;
			}
		}
		
		double macro_pre;
		if (actual.isEmpty())
			macro_pre = 1.0;
		else
			macro_pre = correct / (double) actual.size();
		
		double macro_rec = correct / (double) expected.size();
		
		int total = 1;
		int errors = (correct == actual.size() && actual.size() == expected.size()) ? 0 : 1; 
		
		return new AnalyzerResult(errors, total, macro_pre, macro_rec);
	}
	
	public void logFscore() {
		Logger logger = Logger.getLogger(getClass().getName());
		
		double recall = macro_rec_ / total_;
		double prec = macro_pre_ / total_;
		
		double macro_fsc;
		if (recall + prec < 1e-5) {
			macro_fsc = 0.0;
		} else {
			macro_fsc = 2. * prec * recall / (prec + recall);
		}
		
		logger.info(String.format("F1: %g Pr: %g Re %g", macro_fsc * 100., prec * 100., recall * 100.));
	}
	
	public void logAcc() {
		Logger logger = Logger.getLogger(getClass().getName());
		logger.info(String.format("Acc: %g", 100. * (total_ - errors_) / total_));
	}

}
