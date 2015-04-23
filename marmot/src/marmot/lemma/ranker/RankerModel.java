// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.lemma.ranker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import marmot.core.Feature;
import marmot.lemma.Instance;
import marmot.lemma.LemmaCandidate;
import marmot.lemma.LemmaCandidateSet;
import marmot.lemma.ranker.RankerTrainer.RerankerTrainerOptions;
import marmot.lemma.toutanova.EditTreeAligner;
import marmot.util.AspellLexicon;
import marmot.util.Converter;
import marmot.util.Encoder;
import marmot.util.HashLexicon;
import marmot.util.Lexicon;
import marmot.util.StringUtils.Mode;
import marmot.util.SymbolTable;
import marmot.util.edit.EditTree;

public class RankerModel implements Serializable {

	private static final long serialVersionUID = 1L;
	private double[] weights_;
	private SymbolTable<String> form_table_;
	private SymbolTable<String> lemma_table_;
	private SymbolTable<String> pos_table_;
	private SymbolTable<Feature> feature_table_;
	private SymbolTable<Character> char_table_;
	private SymbolTable<EditTree> tree_table_;
	private EditTreeAligner aligner_;

	private static final int max_window = 5;
	private static final int window_bits_ = Encoder.bitsNeeded(max_window);
	private static final int max_affix_length_ = 10;

	private static final int lemma_feature_ = 0;
	private static final int lemma_form_feature_ = 1;
	private static final int align_feature_ = 2;
	private static final int align_copy_feature_ = 3;
	private static final int align_window_feature_ = 4;
	private static final int tree_feature_ = 5;
	private static final int affix_feature_ = 6;
	private static final int lexicon_feature_ = 7;
	private static final int feature_bits_ = Encoder.bitsNeeded(7);
	private static final int unigram_count_position_bits_ = Encoder
			.bitsNeeded(HashLexicon.ARRAY_LENGTH - 1);
	private static final long max_weights_length_ = 10_000_000;
	private static final int encoder_capacity_ = 8;

	private Set<Integer> ignores_indexes_;

	private int lemma_bits_;
	private int form_bits_;
	private int char_bits_;
	private int tree_bits_;
	private List<Lexicon> unigram_lexicons_;
	private int unigram_lexicons_bits_;

	private SymbolTable<String> morph_table_;

	private transient Feature feature_;
	private transient Encoder encoder_;
	private transient Context context_;

	private int real_capacity_;
	private long pos_length_;
	private long feat_length_;
	private boolean use_shape_lexicon_;
	private boolean use_core_features_;
	private boolean use_alignment_features_;

	private transient double[] accumulated_penalties_;
	private double accumulated_penalty_;
	private final static double EPSILON = 1e-10;;

	private static class Context {
		public List<Integer> list;
		public boolean insert;

		// public Shape shape;

		public Context() {
			list = new ArrayList<>();
		}
	}

	private static final int length_bits_ = Encoder
			.bitsNeeded(2 * max_window + 10);

	public void init(RerankerTrainerOptions options,
			List<RankerInstance> instances, EditTreeAligner aligner) {

		SymbolTable<String> pos_table = null;
		if (options.getUsePos()) {
			pos_table = new SymbolTable<>();
		}

		SymbolTable<String> morph_table = null;
		if (options.getUseMorph()) {
			morph_table = new SymbolTable<>();
		}

		init(options, instances, aligner, pos_table, morph_table);
	}

	public void init(RerankerTrainerOptions options,
			List<RankerInstance> instances, EditTreeAligner aligner,
			SymbolTable<String> pos_table, SymbolTable<String> morph_table) {
		Logger logger = Logger.getLogger(getClass().getName());
		aligner_ = aligner;

		form_table_ = new SymbolTable<>();
		lemma_table_ = new SymbolTable<>();
		char_table_ = new SymbolTable<>();
		tree_table_ = new SymbolTable<>();

		pos_table_ = pos_table;
		morph_table_ = morph_table;

		for (RankerInstance instance : instances) {
			fillTables(instance, instance.getCandidateSet());
		}

		form_bits_ = Encoder.bitsNeeded(form_table_.size() - 1);
		lemma_bits_ = Encoder.bitsNeeded(lemma_table_.size() - 1);
		char_bits_ = Encoder.bitsNeeded(char_table_.size());
		tree_bits_ = Encoder.bitsNeeded(tree_table_.size() - 1);
		logger.info(String.format("Number of edit trees: %5d",
				tree_table_.size()));

		if (pos_table_ != null) {
			logger.info(String.format("Number of POS features: %3d",
					pos_table_.size()));
			logger.info(String.format("POS features: %s", pos_table_.keySet()));
		}

		if (morph_table_ != null) {
			logger.info(String.format("Number of morph features: %3d",
					morph_table_.size()));
			logger.info(String.format("Morph features: %s",
					morph_table_.keySet()));
		}

		int num_candidates = 0;
		for (RankerInstance instance : instances) {
			num_candidates += instance.getCandidateSet().size();
		}
		logger.info(String.format("Candidates per token: %g", num_candidates
				/ (double) instances.size()));

		List<Object> unigram_files = options.getUnigramFile();
		unigram_lexicons_ = new LinkedList<>();
		for (Object unigram_file : unigram_files)
			prepareUnigramFeature((String) unigram_file);

		String aspell_path = options.getAspellPath();
		if (!aspell_path.isEmpty()) {
			String aspell_lang = options.getAspellLang();
			logger.info(String.format("Adding aspell dictionary: %s",
					aspell_lang));
			unigram_lexicons_.add(new AspellLexicon(Mode.lower, aspell_path,
					aspell_lang));
		}

		unigram_lexicons_bits_ = Encoder.bitsNeeded(unigram_lexicons_.size());
		use_shape_lexicon_ = options.getUseShapeLexicon();
		use_core_features_ = options.getUseCoreFeatures();
		use_alignment_features_ = options.getUseAlignmentFeatures();

		feature_table_ = new SymbolTable<>();

		logger.info("Starting feature index extraction.");

		for (RankerInstance instance : instances) {
			addIndexes(instance, instance.getCandidateSet(), true);
		}

		feat_length_ = feature_table_.size();
		pos_length_ = (pos_table_ == null) ? 1 : pos_table_.size() + 1;
		long morph_length = (morph_table_ == null) ? 1
				: morph_table_.size() + 1;

		long actual_length = feat_length_ * pos_length_ * morph_length;
		logger.info(String.format("Actual weights length: %12d", actual_length));

		int length = (int) Math.min(actual_length, max_weights_length_);

		weights_ = new double[length];
		logger.info(String.format("Number of features: %10d",
				feature_table_.size()));
		logger.info(String.format("Weights length: %6d", weights_.length));

		logger.info(String.format("Real encoder capacity: %2d", real_capacity_));

		String ignore_string = options.getIgnoreFeatures();
		if (!ignore_string.isEmpty() && morph_table_ != null) {
			ignores_indexes_ = new HashSet<>();

			logger.info(String.format("Ignore-string: %s (%s)", ignore_string,
					morph_table_));

			for (String feat : ignore_string.split("\\|")) {
				int index = morph_table_.toIndex(feat, -1);
				ignores_indexes_.add(index);
				logger.info(String
						.format("Ignore-string: %s (%d)", feat, index));
			}
		}
	}

	private void prepareUnigramFeature(String unigram_file) {
		Logger logger = Logger.getLogger(getClass().getName());

		if (unigram_file.isEmpty()) {
			return;
		}

		String filename = null;
		int min_count = 0;
		for (String argument : unigram_file.split(",")) {

			int index = argument.indexOf('=');

			if (index >= 0) {

				String argname = argument.substring(0, index);
				String value = argument.substring(index + 1);

				if (argname.equalsIgnoreCase("min-count")) {
					min_count = Integer.valueOf(value);
				} else {
					throw new RuntimeException(String.format(
							"Unknown option: %s", argname));
				}

			} else {
				filename = argument;
			}

		}

		if (filename == null) {
			throw new RuntimeException(String.format(
					"No filename specified: %s", unigram_file));
		}

		logger.info(String
				.format("Creating unigram lexicon from file: %s and with min-count %d.",
						filename, min_count));

		HashLexicon lexicon = HashLexicon.readFromFile(filename, min_count);

		logger.info(String.format("Created unigram lexicon with %7d entries.",
				lexicon.size()));

		unigram_lexicons_.add(lexicon);
	}

	private void fillTables(RankerInstance instance, LemmaCandidateSet set) {
		String form = instance.getInstance().getForm();
		form_table_.insert(form);

		instance.getFormChars(char_table_, true);

		instance.getPosIndex(pos_table_, true);
		instance.getMorphIndexes(morph_table_, true);

		for (Map.Entry<String, LemmaCandidate> candidate_pair : set) {
			String lemma = candidate_pair.getKey();
			LemmaCandidate candidate = candidate_pair.getValue();

			if (use_alignment_features_) {
				candidate.getLemmaChars(char_table_, lemma, true);
				candidate.getAlignment(aligner_, form, lemma);
			}

			candidate.getTreeIndex(aligner_.getBuilder(), form, lemma,
					tree_table_, true);

			lemma_table_.insert(lemma);
		}

	}

	public void addIndexes(RankerInstance instance, LemmaCandidateSet set,
			boolean insert) {
		if (context_ == null || encoder_ == null || feature_ == null) {
			context_ = new Context();
			encoder_ = new Encoder(encoder_capacity_);
			feature_ = new Feature(encoder_capacity_);
		}

		String form = instance.getInstance().getForm();
		int form_index = form_table_.toIndex(form, -1);

		context_.insert = insert;
		// context_.shape = instance.getInstance().getShape();

		int[] form_chars = instance.getFormChars(char_table_, false);

		for (Map.Entry<String, LemmaCandidate> candidate_pair : set) {
			context_.list.clear();

			String lemma = candidate_pair.getKey();
			int lemma_index = lemma_table_.toIndex(lemma, -1, false);

			LemmaCandidate candidate = candidate_pair.getValue();

			if (use_core_features_) {

				if (lemma_index >= 0) {
					encoder_.append(lemma_feature_, feature_bits_);
					encoder_.append(lemma_index, lemma_bits_);
					addFeature();
				}

				if (lemma_index >= 0 && form_index >= 0) {
					encoder_.append(lemma_form_feature_, feature_bits_);
					encoder_.append(lemma_index, lemma_bits_);
					encoder_.append(form_index, form_bits_);
					addFeature();
				}

				int tree_index = candidate.getTreeIndex(aligner_.getBuilder(),
						form, lemma, tree_table_, false);

				if (tree_index >= 0) {
					encoder_.append(tree_feature_, feature_bits_);
					encoder_.append(tree_index, tree_bits_);
					addFeature();

					encoder_.append(tree_feature_, feature_bits_);
					encoder_.append(tree_index, tree_bits_);
					addPrefixFeatures(context_, form_chars, encoder_);
					encoder_.reset();

					encoder_.append(tree_feature_, feature_bits_);
					encoder_.append(tree_index, tree_bits_);
					addSuffixFeatures(context_, form_chars, encoder_);
					encoder_.reset();
				}

			}

			if (use_alignment_features_) {
				int[] lemma_chars = candidate.getLemmaChars(char_table_, lemma,
						false);

				List<Integer> alignment = candidate.getAlignment(aligner_,
						form, lemma);

				addAlignmentIndexes(context_, form_chars, lemma_chars,
						alignment, encoder_);

				addAffixIndexes(context_, lemma_chars, encoder_);
			}

			int lexicon_index = 0;
			for (Lexicon lexicon : unigram_lexicons_) {
				addUnigramFeature(lexicon_index, lexicon, lemma);
				lemma_index++;
			}

			candidate.setFeatureIndexes(Converter.toIntArray(context_.list));
		}
	}

	private void addUnigramFeature(int lexicon_index, Lexicon unigram_lexicon,
			String lemma) {
		int[] counts = unigram_lexicon.getCount(lemma);
		if (counts == null)
			return;

		if (use_shape_lexicon_) {
			for (int i = 0; i < HashLexicon.ARRAY_LENGTH; i++) {
				int count = counts[i];
				if (count > 0) {
					encoder_.append(lexicon_feature_, feature_bits_);
					encoder_.append(lexicon_index, unigram_lexicons_bits_);
					encoder_.append(i, unigram_count_position_bits_);
					addFeature();
				}
			}
		} else {
			int count = counts[HashLexicon.ARRAY_LENGTH - 1];
			if (count > 0) {
				encoder_.append(lexicon_feature_, feature_bits_);
				encoder_.append(lexicon_index, unigram_lexicons_bits_);
				addFeature();
			}
		}

	}

	private void addPrefixFeatures(Context context, int[] chars, Encoder encoder) {
		encoder.append(false);
		for (int i = 0; i < Math.min(chars.length, max_affix_length_); i++) {
			int c = chars[i];
			if (c < 0)
				return;
			encoder.append(c, char_bits_);
			addFeature(false);
		}
	}

	private void addSuffixFeatures(Context context, int[] chars, Encoder encoder) {
		encoder.append(true);
		for (int i = chars.length - 1; i >= Math.max(0, chars.length
				- max_affix_length_); i--) {
			int c = chars[i];
			if (c < 0)
				return;
			encoder.append(c, char_bits_);
			addFeature(false);
		}
	}

	private void addAffixIndexes(Context c, int[] lemma_chars, Encoder encoder) {
		encoder.append(affix_feature_, feature_bits_);
		addPrefixFeatures(c, lemma_chars, encoder);
		encoder.reset();

		encoder.append(affix_feature_, feature_bits_);
		addSuffixFeatures(c, lemma_chars, encoder);
		encoder.reset();

	}

	private void addAlignmentIndexes(Context c, int[] form_chars,
			int[] lemma_chars, List<Integer> alignment, Encoder encoder) {

		Iterator<Integer> iterator = alignment.iterator();

		int input_start = 0;
		int output_start = 0;
		while (iterator.hasNext()) {
			int input_length = iterator.next();
			int output_length = iterator.next();

			int input_end = input_start + input_length;
			int output_end = output_start + output_length;

			addAlignmentSegmentIndexes(c, form_chars, lemma_chars, encoder,
					input_start, input_end, output_start, output_end);

			input_start = input_end;
			output_start = output_end;
		}
	}

	private void addAlignmentSegmentIndexes(Context c, int[] form_chars,
			int[] lemma_chars, Encoder encoder, int input_start, int input_end,
			int output_start, int output_end) {

		if (isCopySegment(form_chars, lemma_chars, input_start, input_end,
				output_start, output_end)) {
			encoder.append(align_copy_feature_, feature_bits_);
			addFeature();
		} else {

			encoder.append(align_feature_, feature_bits_);
			addSegment(form_chars, input_start, input_end, encoder);
			addSegment(form_chars, output_start, output_end, encoder);
			addFeature();

			for (int window = 1; window <= max_window; window++) {

				encoder.append(align_window_feature_, feature_bits_);
				encoder.append(window, window_bits_);
				encoder.append(true);
				addSegment(form_chars, input_start - window,
						input_end + window, encoder);
				addSegment(form_chars, output_start, output_end, encoder);
				addFeature();

				encoder.append(align_window_feature_, feature_bits_);
				encoder.append(window, window_bits_);
				encoder.append(false);
				addSegment(form_chars, input_start, input_end, encoder);
				addSegment(form_chars, output_start - window, output_end
						+ window, encoder);
				addFeature();

			}
		}
	}

	private boolean isCopySegment(int[] form_chars, int[] lemma_chars,
			int input_start, int input_end, int output_start, int output_end) {
		if (input_end - input_start != 1)
			return false;

		if (output_end - output_start != 1) {
			return false;
		}

		return form_chars[input_start] == lemma_chars[output_start];
	}

	private void addSegment(int[] chars, int start, int end, Encoder encoder) {
		encoder.append(end - start, length_bits_);

		for (int i = start; i < end; i++) {

			int c;
			if (i >= 0 && i < chars.length) {
				c = chars[i];
			} else {
				c = char_table_.size();
			}

			if (c < 0)
				return;

			encoder.append(c, char_bits_);
		}
	}

	private int getFeatureIndex() {
		if (feature_table_ != null) {

			encoder_.copyToFeature(feature_);

			int index = feature_table_.toIndex(feature_, -1, false);
			if (index >= 0)
				return index;

			if (context_.insert) {
				real_capacity_ = Math.max(real_capacity_,
						feature_.getCurrentLength());
				index = feature_table_.toIndex(feature_, true);
				feature_ = new Feature(encoder_capacity_);
			}

			return index;
		}
		return encoder_.hashCode();
	}

	private void addFeature(boolean reset) {
		int index = getFeatureIndex();
		if (index >= 0) {
			context_.list.add(index);
		}

		// if (use_shape_) {
		// Shape shape = context_.shape;
		// encoder_.storeState();
		// encoder_.append(shape.ordinal(), shape_bits_);
		// index = getFeatureIndex();
		// if (index >= 0) {
		// context_.list.add(index);
		// }
		// encoder_.restoreState();
		// }

		if (reset)
			encoder_.reset();
	}

	private void addFeature() {
		addFeature(true);
	}

	public String select(RankerInstance instance) {
		Map.Entry<String, LemmaCandidate> best_pair = null;
		for (Map.Entry<String, LemmaCandidate> candidate_pair : instance
				.getCandidateSet()) {
			LemmaCandidate candidate = candidate_pair.getValue();
			double score = score(candidate,
					instance.getPosIndex(pos_table_, false),
					instance.getMorphIndexes(morph_table_, false));

			candidate.setScore(score);

			if (best_pair == null || score > best_pair.getValue().getScore()) {
				best_pair = candidate_pair;
			}
		}
		return best_pair.getKey();
	}

	public double score(LemmaCandidate candidate, int pos_index,
			int[] morph_indexes) {
		assert candidate != null;
		double score = 0.0;
		for (int index : candidate.getFeatureIndexes()) {
			score += updateScore(index, pos_index, morph_indexes, 0.0);
		}
		return score;
	}

	public void update(RankerInstance instance, String lemma, double update) {
		LemmaCandidate candidate = instance.getCandidateSet().getCandidate(
				lemma);
		update(candidate, instance.getPosIndex(pos_table_, false),
				instance.getMorphIndexes(morph_table_, false), update);
	}

	public void update(LemmaCandidate candidate, int pos_index,
			int[] morph_indexes, double update) {
		for (int index : candidate.getFeatureIndexes()) {
			updateScore(index, pos_index, morph_indexes, update);
		}
	}

	private double updateScore(long index, long pos_index, int[] morph_indexes,
			double update) {
		double score = 0.0;

		long f_index = index;
		score += updateScore(f_index, update);

		if (pos_index >= 0) {
			long p_index = f_index + feat_length_ * (pos_index + 1L);
			score += updateScore(p_index, update);

			for (long morph_index : morph_indexes) {

				if (ignores_indexes_ != null) {
					if (ignores_indexes_.contains((int) morph_index)) {
						continue;
					}
				}

				long m_index = p_index + (morph_index + 1L) * feat_length_
						* pos_length_;
				score += updateScore(m_index, update);
			}
		}

		return score;
	}

	private double updateScore(long index, double update) {
		int int_index = (int) (index % (long) weights_.length);
		weights_[int_index] += update;

		if (accumulated_penalties_ != null && Math.abs(update) > EPSILON) {
			applyPenalty(weights_[int_index], int_index);
		}

		return weights_[int_index];
	}

	public void setPenalty(boolean penalize, double accumulated_penalty) {
		if (penalize) {
			accumulated_penalty_ = accumulated_penalty;
			if (accumulated_penalties_ == null) {
				accumulated_penalties_ = new double[weights_.length];
			}
		} else {
			accumulated_penalties_ = null;
		}
	}

	private void applyPenalty(double weight, int index) {
		double old_weight = weight;

		if (old_weight - EPSILON > 0.) {
			weight = Math.max(0, old_weight
					- (accumulated_penalty_ + accumulated_penalties_[index]));
		} else if (old_weight + EPSILON < 0.) {
			weight = Math.min(0, old_weight
					+ (accumulated_penalty_ - accumulated_penalties_[index]));
		}

		accumulated_penalties_[index] += weight - old_weight;
		weights_[index] = weight;
	}

	public double[] getWeights() {
		return weights_;
	}

	public void setWeights(double[] weights) {
		weights_ = weights;
	}

	public SymbolTable<String> getPosTable() {
		return pos_table_;
	}

	public SymbolTable<String> getMorphTable() {
		return morph_table_;
	}

	public boolean isOOV(Instance instance) {
		return form_table_.toIndex(instance.getForm(), -1) == -1;
	}

}
