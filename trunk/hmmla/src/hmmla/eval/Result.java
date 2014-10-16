// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.eval;

public class Result {

	public int error;
	public int total;
	
	public int oov_error;
	public int oov_total;
	
	public int sentence_error;
	public int sentence_total;

	public Result(int error, int total, int oov_error, int oov_total, int sentence_error, int sentence_total) {
		this.error = error;
		this.total = total;
		this.oov_error = oov_error;
		this.oov_total = oov_total;
		this.sentence_error = sentence_error;
		this.sentence_total = sentence_total;
	}

	public Result() {
		this(0, 0, 0, 0, 0, 0);
	}
	
	public void increment(Result result) {
		this.error += result.error;
		this.total += result.total;
		this.oov_error += result.oov_error;
		this.oov_total += result.oov_total;
		this.sentence_error += result.sentence_error;
		this.sentence_total += result.sentence_total;
	}
	
	public String toString(int error, int total) {
		int correct = total - error;
		double acc = correct * 100. / total;
		
		return String.format("%d / %d = %g%%", correct, total, acc);
	}
	
	public String toString() {
		return String.format("" +
				"Token: %s OOV: %s Sentence: %s",
				toString(error, total),
				toString(oov_error, oov_total),
				toString(sentence_error, sentence_total));
	}

}
