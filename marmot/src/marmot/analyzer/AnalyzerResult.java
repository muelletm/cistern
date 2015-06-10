package marmot.analyzer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Logger;

public class AnalyzerResult {

	private int num_errors_;
	private int total_;
	private double macro_pre_;
	private double macro_rec_;
	
	private static class Error {

		private AnalyzerInstance instance_;
		private Collection<AnalyzerTag> missed_;
		private Collection<AnalyzerTag> toomuch_;
		
		public Error(AnalyzerInstance instance, Collection<AnalyzerTag> missed, Collection<AnalyzerTag> tomuch) {
			instance_ = instance;
			missed_ = missed;
			toomuch_ = tomuch;
			
		}
		
		@Override
		public String toString() {
			
			StringBuilder sb = new StringBuilder(String.format("%s:", instance_.getForm()));
			if (!missed_.isEmpty()) {
				sb.append(String.format(" missed: %s", missed_));
			}
			if (!toomuch_.isEmpty()) {
				sb.append(String.format(" toomuch: %s", toomuch_));
			}
			
			return sb.toString();
		}
		
	};
	
	private Collection<Error> errors_;
	
	public AnalyzerResult() {
		this(0, 0, 0., 0., new LinkedList<Error>());
	}
	
	public AnalyzerResult(int num_errors, int total, double macro_pre, double macro_rec, Collection<Error> errors) {
		num_errors_ = num_errors;
		total_ = total;
		macro_pre_ = macro_pre;
		macro_rec_ = macro_rec;
		errors_ = errors;
	}
	
	public void increment(AnalyzerResult result) {
		num_errors_ += result.num_errors_;
		total_ += result.total_;
		macro_pre_ += result.macro_pre_;
		macro_rec_ += result.macro_rec_;
		errors_.addAll(result.errors_);
	}

	public static void logResult(Analyzer analyzer, String filename) {
		AnalyzerResult result = test(analyzer, filename);
		result.logAcc();
		result.logFscore();
		result.logErrors();
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
		Collection<AnalyzerTag> actual = new HashSet<>(AnalyzerReading.toTags(analyzer.analyze(instance)));
		Collection<AnalyzerTag> expected = new HashSet<>(AnalyzerReading.toTags(instance.getReadings()));
		
		Collection<AnalyzerTag> missed = new LinkedList<>();
		Collection<AnalyzerTag> toomuch = new LinkedList<>();
		
		int correct = 0;
		
		for (AnalyzerTag tag : actual) {
			if (expected.contains(tag)) {
				correct ++;
			} else {
				toomuch.add(tag);
			}
		}
		
		for (AnalyzerTag tag : expected) {
			if (!actual.contains(tag)) {
				missed.add(tag);
			}
		}
		
		double macro_pre;
		if (actual.isEmpty())
			macro_pre = 1.0;
		else
			macro_pre = correct / (double) actual.size();
		
		double macro_rec = correct / (double) expected.size();
		
		int total = 1;
		int num_errors = (correct == actual.size() && actual.size() == expected.size()) ? 0 : 1; 
		
		Collection<Error> errors;
		if (missed.isEmpty() && toomuch.isEmpty()) {
			errors = Collections.emptyList();
		} else {
			errors = Collections.singletonList(new Error(instance, missed, toomuch));
		}
		
		return new AnalyzerResult(num_errors, total, macro_pre, macro_rec, errors);
	}
	
	public void logFscore() {
		Logger logger = Logger.getLogger(getClass().getName());
		
		double recall = macro_rec_ / total_;
		double prec = macro_pre_ / total_;
		
		double macro_fsc = getFscore();
		logger.info(String.format("F1: %g Pr: %g Re %g", macro_fsc * 100., prec * 100., recall * 100.));
	}
	
	public void logAcc() {
		Logger logger = Logger.getLogger(getClass().getName());
		logger.info(String.format("Acc: %g", 100. * (total_ - num_errors_) / total_));
	}
	
	public void logErrors() {
		logSubList(errors_, 50);
	}

	private void logSubList(Collection<Error> errors, int first) {
		Logger logger = Logger.getLogger(getClass().getName());
		if (errors.size() > first) {
			errors = new LinkedList<Error>(errors).subList(0, first);
		}
		
		StringBuilder sb = new StringBuilder("Errors:\n");
		for (Error error : errors) {
			sb.append(error.toString());
			sb.append('\n');
		}
		logger.info(sb.toString());
	}

	public double getFscore() {
		double recall = macro_rec_ / total_;
		double prec = macro_pre_ / total_;
		
		double macro_fsc;
		if (recall + prec < 1e-5) {
			macro_fsc = 0.0;
		} else {
			macro_fsc = 2. * prec * recall / (prec + recall);
		}
		
		return macro_fsc;
	}

}
