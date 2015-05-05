// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morfessor;

import marmot.util.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* This is a java port of the perl code in bin/viterbitagsplit_testset.pl 
 * of morfessor_catmap0.9.2
 */

public class ViterbiDecoder implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final double LOG_PROB_ZERO_ = 100000;
	private static final Pattern TAG_LINE_PATTERN_ = Pattern
			.compile("P\\(([^ ]+) -> ([^\\)]+)\\) = ([0-9.]+) \\(N = ([0-9]+)\\)");
	private static final Pattern MORPH_LINE_PATTERN_ = Pattern
			.compile("([^\\s]+)\\s(.+)");

	// Bad probability (maximum negative logprob)
	private double log_prob_max_; 
	private Map<String, Integer> tag_ids_;
	private String[] tag_names_;
	private Map<String, Integer> morph_ids_;
	private double[][] transition_probs_;
	private double[][] emission_probs_;
	private int zzz_index_;

	public ViterbiDecoder(String probs_file, double cutoff) {
		read(probs_file, cutoff);
	}

	public ViterbiDecoder(String probs_file) {
		this(probs_file, 0.00000000001);
	}

	private void readSymbols(String probsfile) {
		tag_ids_ = new HashMap<String, Integer>();
		List<String> tag_names = new LinkedList<String>();
		morph_ids_ = new HashMap<String, Integer>();

		try {
			BufferedReader reader = FileUtils.openFile(probsfile);

			while (reader.ready()) {
				String line = reader.readLine();

				if (line.startsWith("#"))
					continue;

				Matcher m = TAG_LINE_PATTERN_.matcher(line);
				if (m.matches()) {

					String tag1 = m.group(1);
					String tag2 = m.group(2);

					if (!tag_ids_.containsKey(tag1)) {
						int tagid = tag_ids_.size();
						tag_ids_.put(tag1, tagid);
						tag_names.add(tag1);
					}

					if (!tag_ids_.containsKey(tag2)) {
						int tagid = tag_ids_.size();
						tag_ids_.put(tag2, tagid);
						tag_names.add(tag2);
					}

					continue;
				}

				m = MORPH_LINE_PATTERN_.matcher(line);

				if (m.matches()) {
					String morph = m.group(1);
					morph_ids_.put(morph, morph_ids_.size());
					continue;
				}
			}
			reader.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		tag_names_ = tag_names.toArray(new String[0]);
		transition_probs_ = new double[this.tag_names_.length][this.tag_names_.length];
		emission_probs_ = new double[morph_ids_.size()][this.tag_names_.length];
	}

	private void read(String probsfile, double cutoff) {
		readSymbols(probsfile);
		log_prob_max_ = 0;
		int[] ntagged = new int[tag_names_.length];

		try {
			BufferedReader reader = FileUtils.openFile(probsfile);

			while (reader.ready()) {
				String line = reader.readLine();

				if (line.startsWith("#"))
					continue;

				Matcher m = TAG_LINE_PATTERN_.matcher(line);

				if (m.matches()) {

					String tag1 = m.group(1);
					String tag2 = m.group(2);
					double p = Double.parseDouble(m.group(3));
					int n = Integer.parseInt(m.group(4));

					int tagid1 = tag_ids_.get(tag1);
					int tagid2 = tag_ids_.get(tag2);

					double logprob;
					if (p == 0.) {
						logprob = LOG_PROB_ZERO_;
					} else {
						logprob = -Math.log(p);
					}

					transition_probs_[tagid1][tagid2] = logprob;
					ntagged[tagid1] += n;

					continue;
				}

				m = MORPH_LINE_PATTERN_.matcher(line);
				if (m.matches()) {
					String morph = m.group(1);
					String[] probs = m.group(2).split("\\s");
					int morph_id = morph_ids_.get(morph);

					for (int i = 0; i < probs.length; i++) {
						double prob = Double.parseDouble(probs[i]);
						double noocs = ntagged[i] * prob;
						double logp;
						if (noocs < cutoff) {
							logp = LOG_PROB_ZERO_;
						} else {
							logp = -Math.log(prob);
							if (logp > log_prob_max_) {
								log_prob_max_ = logp;
							}
						}

						/*
						 * Note: The tag ids are here one less than in the
						 * transition probabilities!! It would not make sense to
						 * reserve $logpmorphwhentag[x][0] = 0 for all x except
						 * word boundary:
						 */

						emission_probs_[morph_id][i] = logp;

					}
					continue;
				}
			}
			reader.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		zzz_index_ = tag_ids_.get(Morpheme.NON_MORPHEME);
	}

	public List<Morpheme> split(String word) {
		/*
		 * Delta is the lowest accumulated cost ending in each possible
		 * tag/morph combination. Delta is 3-dimensional:
		 * delta[POSITION][MORPHLEN][TAGID]
		 */

		double[][][] delta = new double[word.length() + 1][word.length() + 1][tag_ids_
				.size()];

		/*
		 * Psi is a table of back pointers that indicate the best path. Psi
		 * consists of @psi_prevlen, @psi_prevtag and @psi_asterisk and they are
		 * 3D: e.g., psi_prevlen[POSITION_IN_WORD]
		 * [MORPHLEN_OF_MORPH_ENDING_AT_POSITION]
		 * [TAGID_OF_MORPH_ENDING_AT_POSITION]
		 */

		int[][][] psi_prevlen = new int[word.length() + 1][word.length() + 1][tag_ids_
				.size()];
		int[][][] psi_prevtag = new int[word.length() + 1][word.length() + 1][tag_ids_
				.size()];
		String[][][] psi_asterisk = new String[word.length() + 1][word.length() + 1][tag_ids_
				.size()];

		List<String> morphs = new ArrayList<String>();

		for (int position = 1; position <= word.length(); position++) {
			for (int length = 1; length <= position; length++) {
				int prev_position = position - length;

				/*
				 * # Collect all context-dependent variants for a morph #
				 * corresponding to this substring of the word
				 */

				String morphnoasterisk = word
						.substring(prev_position, position);

				String[] tails = { "", "*0", "*1", "*1", "*2", "*3", "*4" };
				morphs.clear();
				for (String tail : tails) {
					String morph = morphnoasterisk + tail;
					if (morph_ids_.containsKey(morph)) {
						morphs.add(morph);
					}
				}

				if (morphs.isEmpty()) {
					/*
					 * There is no morph that corresponds to this substring of
					 * the word: Store zero probability and continue, unless the
					 * length of the morph is one: accept it as a non-morpheme
					 * morph with low probability.
					 */

					if (length == 1) {
						morphs.add(morphnoasterisk);
					} else {
						for (int tag = 1; tag < tag_ids_.size(); tag++) {
							delta[position][length][tag] = LOG_PROB_ZERO_;
							psi_prevlen[position][length][tag] = 0;
							psi_prevtag[position][length][tag] = 0;
							psi_asterisk[position][length][tag] = "";
						}
						continue;
					}
				}

				for (int tag = 1; tag < tag_ids_.size(); tag++) {
					/*
					 * Find the best previous morph/tag combination for the
					 * current morph/tag combination:
					 */

					double best_cost = LOG_PROB_ZERO_;
					int best_prev_length = -1;
					int best_prevtag = -1;
					String best_asterisk = null;

					for (String morph : morphs) {

						Integer morph_id = morph_ids_.get(morph);
						/*
						 * Find out probability of current morph/tag combination
						 * P(morph_i | tag_i)
						 */
						double log_prob_morph;
						if (morph_id == null) {
							// Add charcater as morph
							log_prob_morph = LOG_PROB_ZERO_;
							if (tag == zzz_index_) {
								log_prob_morph = 10 * log_prob_max_;
							}
						} else {
							log_prob_morph = emission_probs_[morph_id][tag - 1];
						}

						if (prev_position == 0) {
							/*
							 * First morph in word Add cost of transition:
							 * P(tag_i | # )
							 */

							double cost = transition_probs_[0][tag]
									+ log_prob_morph;
							if (cost <= best_cost) {
								best_cost = cost;
								best_prev_length = 0;
								best_prevtag = 0;
								best_asterisk = morph;
							}

						} else {
							// Preceded by other morphs
							for (int prev_length = 1; prev_length <= prev_position; prev_length++) {
								for (int prev_tag = 1; prev_tag < tag_ids_
										.size(); prev_tag++) {
									// Add cost of transition: P(tag_i | tag_j)
									double cost = delta[prev_position][prev_length][prev_tag]
											+ transition_probs_[prev_tag][tag]
											+ log_prob_morph;

									if (cost <= best_cost) {
										best_cost = cost;
										best_prev_length = prev_length;
										best_prevtag = prev_tag;
										best_asterisk = morph;
									}
								}
							}

						}
					}

					// Store info about best path to the current state
					delta[position][length][tag] = best_cost;
					psi_prevlen[position][length][tag] = best_prev_length;
					psi_prevtag[position][length][tag] = best_prevtag;
					psi_asterisk[position][length][tag] = best_asterisk;
				}
			}
		}

		// Find the best transition to the final word boundary
		double best_cost = LOG_PROB_ZERO_;
		int best_length = -1;
		int best_tag = -1;
		for (int length = 1; length <= word.length(); length++) {
			for (int tag = 1; tag < tag_ids_.size(); tag++) {

				// Add cost of transition: P(# | tag_j)
				double cost = delta[word.length()][length][tag]
						+ transition_probs_[tag][0];

				// System.err.println(cost + " " + word.length() + " " + length
				// + " " + tag);

				if (cost <= best_cost) {
					best_cost = cost;
					best_length = length;
					best_tag = tag;
				}
			}
		}

		if (best_cost == LOG_PROB_ZERO_) {
			System.err.println("best cost is zero");
			return Collections.singletonList(new Morpheme(word + "/"
					+ Morpheme.NON_MORPHEME));
		}

		List<Morpheme> morpheme_list = new LinkedList<Morpheme>();

		// Trace back

		int position = word.length();
		while (position > 0) {
			String morph = psi_asterisk[position][best_length][best_tag];
			morpheme_list.add(new Morpheme(morph + "/" + tag_names_[best_tag]));
			int best_prev_length = psi_prevlen[position][best_length][best_tag];
			int best_prev_tag = psi_prevtag[position][best_length][best_tag];
			position -= best_length;
			best_length = best_prev_length;
			best_tag = best_prev_tag;
		}

		Collections.reverse(morpheme_list);
		return morpheme_list;
	}
}
