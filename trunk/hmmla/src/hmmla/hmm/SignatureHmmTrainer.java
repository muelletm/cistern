// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.hmm;

import hmmla.util.Ling;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class SignatureHmmTrainer implements HmmTrainer {

	private SimpleHmmTrainer trainer_;
	private double delta_e_;
	private double delta_t_;
	
	
	public SignatureHmmTrainer(double delta_t, double delta_e) {
		trainer_ = new SimpleHmmTrainer(delta_t, delta_e);
		delta_e_ = delta_e;
		delta_t_ = delta_t;
	}

	public Map<String, double[]> getUnknownClassProbs(Model model) {
		Map<String, double[]> map = new HashMap<String, double[]>();
		Statistics statistics = model.getStatistics();
		for (Entry<String, Integer> entry : model.getWordTable().entrySet()) {
			String output = entry.getKey();
			String signature = Ling.signature(output, model);

			double[] freqs = map.get(signature);

			if (freqs == null) {
				freqs = new double[statistics.getNumTags()];
				map.put(signature, freqs);
			}

			for (int index = 0; index < freqs.length; index++) {
				freqs[index] += statistics
						.getEmissions(index, entry.getValue());
			}
		}
		
		for (Entry<String, double[]> entry : map.entrySet()) {
			double total = 0.0;
			
			double[] freqs = entry.getValue();
			
			for (int tag = 0; tag < statistics.getNumTags(); tag ++) {
				freqs[tag] += delta_e_;
				total += freqs[tag];
			}
			for (int tag = 0; tag < statistics.getNumTags(); tag ++) {
				freqs[tag] /= total;
			}		
		}
		
		return map;
	}

	@Override
	public HmmModel train(Model model) {
		SimpleHmmModel hmm_model = (SimpleHmmModel) trainer_.train(model);

		Map<String, double[]> signature_map = getUnknownClassProbs(model);

		smoothEmissionProbs(hmm_model.getStatistics(), model, signature_map);

		return new SignatureHmmModel(hmm_model, signature_map, model);
	}

	private double[] getTagPrior(Statistics statistics) {
		double[] tag_prior = new double[statistics.getNumTags()];
		double total_freq = 0;
		for (int tag = 0; tag < statistics.getNumTags(); tag++) {
			for (int tag2 = 0; tag2 < statistics.getNumTags(); tag2++) {
				tag_prior[tag] += statistics.getTransitions(tag, tag2) + delta_t_;
			}
			total_freq += tag_prior[tag];
		}
		
		assert total_freq > 0;
		
		for (int tag = 0; tag < statistics.getNumTags(); tag++) {
			tag_prior[tag] /= total_freq;
			assert tag_prior[tag] > 0;
		}
		
		return tag_prior;
	}

	private void smoothEmissionProbs(Statistics output_statistics, Model model,
			Map<String, double[]> signature_map) {

		Statistics statistics = model.getStatistics();

		double[] tag_prior = getTagPrior(statistics);

		for (Map.Entry<String, Integer> form_entry : model.getWordTable()
				.entrySet()) {
			String word_form = form_entry.getKey();
			String signature = Ling.signature(word_form, model);
			int word_index = form_entry.getValue();
			double[] backoff_log_probs = signature_map.get(signature);
			assert backoff_log_probs != null;
			double backoff_factor = 0;
			double total_freq = 0;
			
			if (!model.isKnown(word_form)) {
				// This might happen during Jacknife training.
				continue;
			}

			for (int tag = 0; tag < statistics.getNumTags(); tag++) {
				double freq = statistics.getEmissions(tag, word_index);
				total_freq += freq;
				backoff_factor += (freq > 1.) ? 1. : freq;
			}
			
			if (total_freq == 0. && backoff_factor == 0.) {
				// This might happen during EM-training with sampling
				// A word has been seen in the training set, but 
				// not in the current sample.
				// We just assign it the full backoff probability.
				
				backoff_factor = 1.;
			}
			
			assert backoff_factor > 0;		
			double prob_sum = 0;
			
			for (int tag = 0; tag < statistics.getNumTags(); tag++) {
				double freq = statistics.getEmissions(tag, word_index);
				double backoff_prob = backoff_log_probs[tag];
				assert backoff_prob > 0;
				double prob = (freq + backoff_factor * backoff_prob) / (total_freq + backoff_factor);
				prob_sum += prob;			
				prob /= tag_prior[tag];
				double log_prob = Math.log(prob);
				assert log_prob != Double.NEGATIVE_INFINITY;
				output_statistics.setEmissions(tag, word_index, log_prob);
			}
			
			assert Math.abs(prob_sum - 1.0) < 1e-6;
			
		}

		for (Entry<String, double[]> entry : signature_map.entrySet()) {
			for (int tag = 0; tag < statistics.getNumTags(); tag ++) {
				entry.getValue()[tag] = Math.log(entry.getValue()[tag] / tag_prior[tag]); 
			}
		}
	}

}
