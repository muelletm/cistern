// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.lemma;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import marmot.morph.io.SentenceReader;

public class Result {

	private int total_;
	private List<Error> errors_;
	private int total_types_;

	public Result(int num_tokens, int num_types, List<Error> errors) {
		total_ = num_tokens;
		total_types_ = num_types;
		errors_ = errors;
	}

	public static Result test(Lemmatizer lemmatizer, String file) {
		return test(lemmatizer, Instance.getInstances(new SentenceReader(file)));
	}

	public static Result test(Lemmatizer lemmatizer,
			Collection<Instance> instances) {
		int total = 0;

		List<Error> errors = new LinkedList<>();

		for (Instance instance : instances) {
			String predicted_lemma = lemmatizer.lemmatize(instance);

			if (predicted_lemma == null
					|| !predicted_lemma.equals(instance.getLemma())) {
				errors.add(new Error(instance, predicted_lemma));
			}
			total += instance.getCount();
		}

		return new Result(total, instances.size(), errors);
	}

	public static void logTest(Lemmatizer lemmatizer, String file, int limit) {
		Result result = test(lemmatizer, file);

		result.logAccuracy();
		result.logErrors(limit);
	}

	public void logAccuracy() {

		int correct = total_;
		for (Error error : errors_) {
			correct -= error.getInstance().getCount();
		}
		int correct_types = total_types_ - errors_.size();

		Logger.getLogger(getClass().getName()).info(
				String.format("%6d / %6d = %g (Type: %5d / %5d = %g)", correct,
						total_, correct * 100. / total_, correct_types,
						total_types_, correct_types * 100. / total_types_));
	}

	public void logErrors(int limit) {
		StringBuilder sb = new StringBuilder();

		sb.append("Errors:\n");

		int number = 0;
		for (Error error : errors_) {
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
		int correct = total_;
		for (Error error : errors_) {
			correct -= error.getInstance().getCount();
		}
		return correct * 100. / total_;
	}

	public static Result testGenerator(LemmaCandidateGenerator generator,
			String filename) {
		return testGenerator(generator, Instance.getInstances(filename));
	}
	
	public static Result testGenerator(LemmaCandidateGenerator generator, List<Instance> instances) {
		
		int total = 0;

		List<Error> errors = new LinkedList<>();

		for (Instance instance : instances) {
			
			LemmaCandidateSet set = new LemmaCandidateSet(instance.getForm());
			
			generator.addCandidates(instance, set);

			if (!set.contains(instance.getLemma())) {
				errors.add(new Error(instance, null));
			}
			total += instance.getCount();
		}

		return new Result(total, instances.size(), errors);
		
	}

}
