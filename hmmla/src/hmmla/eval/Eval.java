// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.eval;

import hmmla.decode.Decoder;
import hmmla.hmm.Model;
import hmmla.io.Sentence;
import hmmla.io.Token;

import java.util.List;

public class Eval {

	public static Result eval(List<String> actual_tags, Sentence sentence, Model model) {
		Result result = new Result();
		
		if (actual_tags.size() != sentence.size()) {
			throw new RuntimeException();
		}

		boolean sent_error = false;
		for (int token_index = 0; token_index < sentence.size(); token_index++) {
			Token token = sentence.get(token_index);

			String word_form = token.getWordForm();
			String actual_tag = actual_tags.get(token_index);
			String expected_tag = token.getTag();

			boolean is_error = !actual_tag.equals(expected_tag);
			boolean is_oov = !model.isKnown(word_form);

			if (is_error) {
				result.error++;
			}
			result.total++;

			if (is_oov) {
				if (is_error) {
					result.oov_error++;
				}
				result.oov_total++;
			}

			sent_error |= is_error;
		}

		if (sent_error) {
			result.sentence_error++;
		}

		result.sentence_total++;

		return result;
	}

	public static Result eval(Decoder decoder, Iterable<Sentence> testReader,
			Model model) {
		Result result = new Result();

		for (Sentence sentence : testReader) {
			List<String> actual_tags = decoder.bestPath(sentence);
			Result r = eval(actual_tags, sentence, model);
			result.increment(r);
		}

		return result;
	}
}
