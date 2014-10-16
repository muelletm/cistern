// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.hmm;

import hmmla.util.SymbolTable;

import java.util.Map.Entry;

public class SimpleHmmTrainer implements HmmTrainer {

	private double delta_e;
	private double delta_t;

	public SimpleHmmTrainer(double delta_t, double delta_e) {
		this.delta_e = delta_e;
		this.delta_t = delta_t;
	}

	@Override
	public HmmModel train(Model model) {
		int num_tags = model.getTagTable().size();
		int num_outputs = model.getWordTable().size();
		Statistics normalized_stats = new Statistics(num_tags, num_outputs);
		setTransmissionProbabilities(model, normalized_stats);	
		setEmissionProbabilities(model, normalized_stats);
		return new SimpleHmmModel(normalized_stats, model);
	}

	protected void setTransmissionProbabilities(Model model,
			Statistics normalized_stats) {
		SymbolTable<String> tags = model.getTagTable();
		Statistics statistics = model.getStatistics();
		int num_tags = tags.size();

		for (int fromIndex = 0; fromIndex < num_tags; fromIndex++) {
			double prior = 0.0;
			for (int toIndex = 0; toIndex < num_tags; toIndex++) {
				prior += statistics.getTransitions(fromIndex, toIndex);
			}

			prior = Math.log(prior + delta_t * num_tags);

			for (int toIndex = 0; toIndex < num_tags; toIndex++) {
				double p = -prior
						+ Math.log(statistics
								.getTransitions(fromIndex, toIndex) + delta_t);

				normalized_stats.setTransitions(fromIndex, toIndex, p);
			}
		}
	}

	protected void setEmissionProbabilities(Model model,
			Statistics normalized_stats) {
		SymbolTable<String> tag_table = model.getTagTable();
		SymbolTable<String> word_table = model.getWordTable();
		Statistics statistics = model.getStatistics();
		int num_outputs = word_table.size();
		int num_tags = tag_table.size();

		for (int tag = 0; tag < num_tags; tag++) {

			if (tag == Model.BorderIndex) {
				for (Entry<String, Integer> entry : word_table.entrySet()) {
					normalized_stats.setEmissions(tag, entry.getValue(),
							Double.NEGATIVE_INFINITY);
				}
				continue;
			}
			
			double total = 0;
			for (int output = 0; output < num_outputs; output ++) {
				total += statistics.getEmissions(tag, output) + delta_e;
			}

			for (int output = 0; output < num_outputs; output ++) {			
				double freq = statistics.getEmissions(tag, output) + delta_e;			
				double prob = freq / total;
				normalized_stats.setEmissions(tag, output, Math.log(prob));
			}

		}
	}
}
