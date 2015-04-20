// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph;

import java.text.NumberFormat;
import java.util.Locale;

import marmot.core.Model;
import marmot.core.Result;
import marmot.core.Tagger;



public class MorphResult implements Result {
	private static final long serialVersionUID = 1L;
	public int num_sentences;
	public int sentence_errors;
	public int num_unreachable_sentences;
	public int[] rank;

	public int num_states;
	public int candidates_length;

	public int num_oovs;
	public int[] oov_errors;
	public int morph_oov_errors;

	public int[] token_errors;
	public int morph_errors;
	public int num_tokens;
	public long time;

	private Model model_;
	public long sum_lattice_time;
	public int lemma_errors;

	public MorphResult(Tagger tagger) {
		this(tagger.getModel(), tagger.getNumLevels());
	}
	
	public MorphResult(Model model, int level) {
		rank = new int[model.getOptions().getBeamSize()];
		oov_errors = new int[level];
		token_errors = new int[level];
		model_ = model;
	}

	public void increment(MorphResult eval) {
		num_sentences += eval.num_sentences;
		sentence_errors += eval.sentence_errors;
		num_unreachable_sentences += eval.num_unreachable_sentences;

		morph_errors += eval.morph_errors;
		morph_oov_errors += eval.morph_oov_errors;

		num_states += eval.num_states;
		candidates_length += eval.candidates_length;

		num_oovs += eval.num_oovs;
		assert oov_errors.length == eval.oov_errors.length;
		for (int index = 0; index < eval.oov_errors.length; index++)
			oov_errors[index] += eval.oov_errors[index];

		num_tokens += eval.num_tokens;
		assert token_errors.length == eval.token_errors.length;
		for (int index = 0; index < eval.token_errors.length; index++)
			token_errors[index] += eval.token_errors[index];

		assert rank.length == eval.rank.length;
		for (int index = 0; index < rank.length; index++) {
			rank[index] += eval.rank[index];
		}

		sum_lattice_time += eval.sum_lattice_time;

		lemma_errors += eval.lemma_errors;
		
		time += eval.time;
	}

	@Override
	public String toString() {
		NumberFormat nf2 = NumberFormat.getInstance(Locale.ENGLISH);

		StringBuilder sb = new StringBuilder();
		sb.append("Eval\n");
		sb.append(String.format("Token : %s\n",
				toString(token_errors, num_tokens)));
		sb.append(String.format("all : %s\n\n",
				toString(morph_errors, num_tokens)));
		sb.append(String.format("OOV : %s\n", toString(oov_errors, num_oovs)));
		sb.append(String.format("all : %s\n\n",
				toString(morph_oov_errors, num_oovs)));
		
		sb.append(String.format("lemma : %s\n\n",
				toString(lemma_errors, num_tokens)));
		
		sb.append(String.format("Sentence : %s",
				toString(sentence_errors, num_sentences)));
		for (int i = 0; i < rank.length; i++) {
			sb.append(' ');
			sb.append(nf2.format(rank[i] * 100. / num_sentences));
			sb.append('%');
		}
		sb.append('\n');

		if (num_unreachable_sentences > 0) {
			sb.append(String.format(
					"Unreachable : %s\n",
					toString(num_sentences - num_unreachable_sentences,
							num_sentences)));
		}

		if (candidates_length > 0) {
			sb.append(String.format("Candidates / Position : %s\n", num_states
					/ (double) candidates_length));
		}

		if (time > 1000) {
			System.err.format("Processed %d sentences at %g sentences/s\n",
					num_sentences, num_sentences / (time / 1000.));
		}

		if (sum_lattice_time > 500)
			System.err.format("Lattice time: %gs\n", (sum_lattice_time / 1000.));
		if (time - sum_lattice_time > 500)
			System.err.format("Viterbi time: %gs\n",
					((time - sum_lattice_time) / 1000.));

		return sb.toString();
	}

	private String toString(int[] error, int total) {
		StringBuilder sb = new StringBuilder("\n");

		for (int index = 0; index < error.length; index++) {
			String key = model_.getCategoryTable().toSymbol(index);
			sb.append(key);
			sb.append(": ");
			sb.append(toString(error[index], total));
			if (index < error.length - 1) {
				sb.append('\n');
			}
		}

		return sb.toString();
	}

	private String toString(int error, int total) {
		int correct = total - error;
		return String.format("%d / %d = %g%%", correct, total, correct * 100.
				/ total);
	}

	public double getTokenAccuracy() {
		return 100. - (morph_errors * 100. / num_tokens);
	}

	public double getOovTokenAccuracy() {
		return 100. - (morph_oov_errors * 100. / num_oovs);
	}

	@Override
	public double getScore() {
		return getTokenAccuracy();
	}

}
