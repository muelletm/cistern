// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package lemming.lemma;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import marmot.morph.io.SentenceReader;

public class LemmaResult {

	private int num_tokens_;
	private List<LemmaError> errors_;
	private int num_oov_tokens_;

	public LemmaResult(int num_tokens, int num_oov_tokens, List<LemmaError> errors) {
		num_tokens_ = num_tokens;
		num_oov_tokens_ = num_oov_tokens;
		errors_ = errors;
	}

	public static LemmaResult test(Lemmatizer lemmatizer, String file) {
		return test(lemmatizer, LemmaInstance.getInstances(new SentenceReader(file)));
	}

	public static LemmaResult test(Lemmatizer lemmatizer,
			Collection<LemmaInstance> instances) {
		int total = 0;
		
		int num_oovs = 0;
		

		List<LemmaError> errors = new LinkedList<>();

		for (LemmaInstance instance : instances) {
			String predicted_lemma = lemmatizer.lemmatize(instance);
			
			if (lemmatizer.isOOV(instance)) {
				num_oovs += instance.getCount();
			}

			if (predicted_lemma == null
					|| !predicted_lemma.equals(instance.getLemma())) {
				errors.add(new LemmaError(instance, predicted_lemma, lemmatizer.isOOV(instance)));
			}
			total += instance.getCount();
		}

		return new LemmaResult(total, num_oovs, errors);
	}

	public static void logTest(Lemmatizer lemmatizer, String file, int limit) {
		LemmaResult result = test(lemmatizer, file);

		result.logAccuracy();
		result.logErrors(limit);
	}

	private String format(int correct, int total) {
		double acc = correct * 100. / total;
		return String.format("%6d / %6d = %g", correct, total, acc);
	}
	
	public void logAccuracy() {
		int errors = 0;
		int oov_errors = 0;
				
		for (LemmaError error : errors_) {
			errors += error.getInstance().getCount();
						
			if (error.isOOV()) {
				oov_errors += error.getInstance().getCount();
			}
			
		}
		
		int correct = num_tokens_ - errors;
		int oov_correct = num_oov_tokens_ - oov_errors;
		
		Logger.getLogger(getClass().getName()).info(
			String.format("%s (OOV: %s)", format(correct, num_tokens_), format(oov_correct, num_oov_tokens_)));
	}

	public void logErrors(int limit) {
		StringBuilder sb = new StringBuilder();

		sb.append("Errors:\n");

		int number = 0;
		for (LemmaError error : errors_) {
			sb.append(error);
			sb.append('\n');

			number++;
			if (limit >= 0 && number >= limit) {
				break;
			}
		}

		Logger.getLogger(getClass().getName()).info(sb.toString());
	}

	public double getTokenAccuracy() {
		int correct = num_tokens_;
		for (LemmaError error : errors_) {
			correct -= error.getInstance().getCount();
		}
		return correct * 100. / num_tokens_;
	}

	public static LemmaResult testGenerator(LemmatizerGenerator generator,
			String filename) {
		return testGenerator(generator, LemmaInstance.getInstances(filename));
	}
	
	public static LemmaResult testGenerator(LemmatizerGenerator generator, List<LemmaInstance> instances) {
		
		int total = 0;
		int oov_total = 0;

		List<LemmaError> errors = new LinkedList<>();

		for (LemmaInstance instance : instances) {
			
			if (generator.isOOV(instance)) {
				oov_total += instance.getCount();
			}			
			
			LemmaCandidateSet set = new LemmaCandidateSet();
			
			generator.addCandidates(instance, set);

			if (!set.contains(instance.getLemma())) {
				errors.add(new LemmaError(instance, null, generator.isOOV(instance)));
			}
			total += instance.getCount();
		}

		return new LemmaResult(total, oov_total, errors);
		
	}

}
