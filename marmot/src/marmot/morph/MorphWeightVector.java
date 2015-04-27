// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph;

import java.util.Arrays;
import java.util.Collection;

import marmot.core.ArrayFloatFeatureVector;
import marmot.core.ConcatFloatFeatureVector;
import marmot.core.FeatureVector;
import marmot.core.FloatFeatureVector;
import marmot.core.FloatWeights;
import marmot.core.Model;
import marmot.core.Sequence;
import marmot.core.State;
import marmot.core.WeightVector;
import marmot.core.ZeroFloatFeatureVector;
import marmot.lemma.ranker.RankerModel;
import marmot.util.Encoder;
import marmot.util.SymbolTable;

public class MorphWeightVector implements WeightVector, FloatWeights {
	private static final long serialVersionUID = 1L;
	private int max_affix_length_;
	private int num_state_features_;

	private static final int ENCODER_CAPACITY_ = 10;

	private boolean use_hash_vector;

	private transient Encoder encoder_;

	private double accumulated_penalty_;

	private transient double[] accumulated_penalties_;
	private transient double[] accumulated_float_penalties_;
	private double[] weights_;
	private double[] float_weights_;

	private boolean extend_feature_set_;
	private MorphModel model_;
	private SymbolTable<Object> feature_table_;

	private int simple_sub_morph_start_index_;

	private int signature_bits_;
	private int word_bits_;
	private int[] tag_bits_;
	private int state_feature_bits_;
	private int char_bits_;
	private int shape_bits_;
	private int order_bits_;
	private int[] num_tags_;
	private int total_num_tags_;
	private int level_bits_;
	private int max_level_;
	private double scale_factor_;
	private boolean shape_;
	private int initial_vector_size_;
	private int token_feature_bits_;
	private int max_transition_feature_level_;
	private MorphDictionary mdict_;
	private FloatHashDictionary fdict_;
	private int mdict_bits_;

	private boolean use_state_features_;
	private boolean use_form_feature_;
	private boolean use_rare_feature_;
	private boolean use_lexical_context_feature_;
	private boolean use_affix_features_;
	private boolean use_signature_features_;
	private boolean use_infix_features_;
	private boolean use_bigrams_;
	private boolean hash_feature_table_;

	public MorphWeightVector(MorphOptions options) {
		shape_ = options.getShape();
		max_transition_feature_level_ = options.getMaxTransitionFeatureLevel();
		initial_vector_size_ = options.getInitialVectorSize();
		use_state_features_ = options.getUseDefaultFeatures();
		use_hash_vector = options.getUseHashVector();
		max_affix_length_ = options.getMaxAffixLength();
		hash_feature_table_ = options.getUseHashFeatureTable();

		use_form_feature_ = true;
		use_rare_feature_ = true;
		use_lexical_context_feature_ = true;
		use_affix_features_ = true;
		use_signature_features_ = true;
		use_infix_features_ = false;
		use_bigrams_ = true;

		String feature_template = options.getFeatureTemplates();
		if (feature_template != null) {
			use_form_feature_ = false;
			use_rare_feature_ = false;
			use_lexical_context_feature_ = false;
			use_affix_features_ = false;
			use_signature_features_ = false;
			use_bigrams_ = false;

			for (String feat : feature_template.toLowerCase().split(",")) {
				if (feat.equals("form")) {
					use_form_feature_ = true;
				} else if (feat.equals("affix")) {
					use_affix_features_ = true;
				} else if (feat.equals("rare")) {
					use_rare_feature_ = true;
				} else if (feat.equals("context")) {
					use_lexical_context_feature_ = true;
				} else if (feat.equals("sig")) {
					use_signature_features_ = true;
				} else if (feat.equals("bigrams")) {
					use_bigrams_ = true;
				} else if (feat.equals("infix")) {
					use_infix_features_ = true;
				} else {
					throw new RuntimeException("Unknown value: " + feat);
				}
			}

		}

		if (!use_state_features_) {
			use_form_feature_ = false;
			use_rare_feature_ = false;
			use_lexical_context_feature_ = false;
			use_affix_features_ = false;
			use_signature_features_ = false;
			use_bigrams_ = false;
		}

		if (!options.getMorphDict().isEmpty()) {
			mdict_ = MorphDictionary.create(options.getMorphDict());
			mdict_bits_ = Encoder.bitsNeeded(mdict_.numTags());
		}

		num_state_features_ = 0;
		if (use_form_feature_) {
			num_state_features_ += 1;
		}
		if (use_rare_feature_) {
			num_state_features_ += 1;
		}
		if (use_affix_features_) {
			num_state_features_ += 2;
		}
		if (use_lexical_context_feature_) {
			num_state_features_ += 2;
		}
		if (use_signature_features_) {
			num_state_features_ += 1;
		}
		if (shape_) {
			num_state_features_ += 3;
		}
		if (mdict_ != null) {
			num_state_features_ += 1;
		}
		if (use_infix_features_) {
			num_state_features_ += 1;
		}

		// For the token features
		num_state_features_ += 1;

		// num_state_features_ = 3 + 3;

		if (!options.getFloatTypeDict().isEmpty()) {

			fdict_ = new FloatHashDictionary();

			MorphDictionaryOptions opt = MorphDictionaryOptions.parse(
					options.getFloatTypeDict(), false);

			if (opt.getIndexes() == null) {
				int[] indexes = { 0 };
				opt.setIndexes(indexes);
			}

			fdict_.init(opt);

		}
	}

	@Override
	public void setExtendFeatureSet(boolean flag) {
		extend_feature_set_ = flag;
	}

	@Override
	public void setPenalty(boolean penalize, double accumulated_penalty) {
		if (!penalize) {
			accumulated_penalties_ = null;
			accumulated_float_penalties_ = null;
			accumulated_penalty_ = 0.0;
		} else {

			accumulated_penalty_ = (double) (accumulated_penalty / scale_factor_);
			if (accumulated_penalties_ == null) {
				accumulated_penalties_ = new double[weights_.length];
			}
			if (accumulated_float_penalties_ == null && float_weights_ != null) {
				accumulated_float_penalties_ = new double[float_weights_.length];
			}
		}

		RankerModel model = model_.getLemmaModel();
		if (model != null) {
			model.setPenalty(penalize, accumulated_penalty);
		}
	}

	@Override
	public FeatureVector extractStateFeatures(State state) {
		prepareEncoder();
		MorphFeatureVector new_vector = new MorphFeatureVector(
				1 + state.getLevel(), state.getVector());

		int fc = 0;

		encoder_.append(0, order_bits_);
		encoder_.append(state.getLevel() + 1, level_bits_);
		encoder_.append(fc, 2);

		State run = state.getZeroOrderState();
		while (run != null) {
			encoder_.append(run.getLevel(), level_bits_);
			encoder_.append(run.getIndex(), tag_bits_[run.getLevel()]);
			addFeature(new_vector);
			run = run.getSubLevelState();
		}
		encoder_.reset();
		fc++;

		new_vector.setIsState(true);
		new_vector.setWordIndex(((MorphFeatureVector) state.getVector())
				.getWordIndex());
		return new_vector;
	}

	@Override
	public FeatureVector extractStateFeatures(Sequence sequence, int token_index) {
		prepareEncoder();
		Word word = (Word) sequence.get(token_index);

		int[] mdict_indexes = null;
		if (mdict_ != null) {
			mdict_indexes = mdict_.getIndexes(word.getWordForm());
		}

		short[] chars = word.getCharIndexes();
		assert chars != null;
		int form_index = word.getWordFormIndex();
		boolean is_rare = model_.isRare(form_index);
		MorphFeatureVector features = new MorphFeatureVector(20);
		int fc = 0;

		if (use_state_features_) {

			if (use_form_feature_) {

				if (form_index >= 0) {
					encoder_.append(0, order_bits_);
					encoder_.append(0, level_bits_);
					encoder_.append(fc, state_feature_bits_);
					encoder_.append(form_index, word_bits_);
					addFeature(features);
					encoder_.reset();
				}

				fc++;
			}

			if (use_rare_feature_) {

				encoder_.append(0, order_bits_);
				encoder_.append(0, level_bits_);
				encoder_.append(fc, state_feature_bits_);
				encoder_.append(is_rare);
				addFeature(features);
				encoder_.reset();

				fc++;
			}

			if (shape_) {
				int shape_index = -1;
				shape_index = word.getWordShapeIndex();
				if (is_rare && shape_index >= 0) {
					encoder_.append(0, order_bits_);
					encoder_.append(0, level_bits_);
					encoder_.append(fc, state_feature_bits_);
					encoder_.append(shape_index, shape_bits_);
					addFeature(features);
					encoder_.reset();
				}
				fc++;
			}

			if (token_index - 1 >= 0) {

				int pform_index = ((Word) sequence.get(token_index - 1))
						.getWordFormIndex();

				if (use_lexical_context_feature_) {

					if (pform_index >= 0) {

						encoder_.append(0, order_bits_);
						encoder_.append(0, level_bits_);
						encoder_.append(fc, state_feature_bits_);
						encoder_.append(pform_index, word_bits_);
						addFeature(features);

						if (form_index >= 0 && use_bigrams_) {
							encoder_.append(form_index, word_bits_);
							addFeature(features);
						}
						encoder_.reset();
					}
				}

				int pshape_index = -1;

				if (shape_) {
					pshape_index = ((Word) sequence.get(token_index - 1))
							.getWordShapeIndex();
				}

				if (pshape_index >= 0) {
					encoder_.append(0, order_bits_);
					encoder_.append(0, level_bits_);
					encoder_.append(fc + 1, state_feature_bits_);
					encoder_.append(pshape_index, shape_bits_);

					if (model_.isRare(pform_index)) {
						addFeature(features);
					}
					encoder_.reset();
				}
			}

			if (use_lexical_context_feature_) {
				fc++;
			}

			if (shape_) {
				fc++;
			}

			if (token_index + 1 < sequence.size()) {

				int nform_index = ((Word) sequence.get(token_index + 1))
						.getWordFormIndex();

				if (use_lexical_context_feature_) {

					if (nform_index >= 0) {
						encoder_.append(0, order_bits_);
						encoder_.append(0, level_bits_);
						encoder_.append(fc, state_feature_bits_);
						encoder_.append(nform_index, word_bits_);
						addFeature(features);

						if (form_index >= 0 && use_bigrams_) {
							encoder_.append(form_index, word_bits_);
							addFeature(features);
						}
						encoder_.reset();
					}
				}

				int nshape_index = -1;
				if (shape_) {
					nshape_index = ((Word) sequence.get(token_index + 1))
							.getWordShapeIndex();
				}
				if (nshape_index >= 0) {
					encoder_.append(0, order_bits_);
					encoder_.append(0, level_bits_);
					encoder_.append(fc + 1, state_feature_bits_);
					encoder_.append(nshape_index, shape_bits_);

					if (model_.isRare(nform_index)) {
						addFeature(features);
					}

					encoder_.reset();
				}
			}

			if (use_lexical_context_feature_) {
				fc++;
			}

			if (shape_) {
				fc++;
			}

			if (use_signature_features_) {
				if (is_rare) {
					int signature = word.getWordSignature();
					encoder_.append(0, order_bits_);
					encoder_.append(0, level_bits_);
					encoder_.append(fc, state_feature_bits_);
					encoder_.append(signature, signature_bits_);
					addFeature(features);
					encoder_.reset();
				}
				fc++;
			}

			// Infix feature
			if (use_infix_features_) {
				if (is_rare) {
					assert chars != null;

					encoder_.append(0, order_bits_);
					encoder_.append(0, level_bits_);
					encoder_.append(fc, state_feature_bits_);

					for (int position = 0; position < chars.length; position++) {

						for (int length = 0; length < max_affix_length_; length++) {

							int end_position = position + length;

							if (end_position >= chars.length) {
								break;
							}

							short c = chars[end_position];
							if (c < 0) {
								break;
							}
							encoder_.append(c, char_bits_);
							addFeature(features);
						}

						encoder_.reset();
					}

				}
				fc++;
			}

			// Prefix feature
			if (use_affix_features_) {
				if (is_rare) {
					encoder_.append(0, order_bits_);
					encoder_.append(0, level_bits_);
					encoder_.append(fc, state_feature_bits_);

					for (int position = 0; position < Math.min(chars.length,
							max_affix_length_); position++) {
						assert chars != null;
						short c = chars[position];
						if (c < 0) {
							// Unknown character!
							break;
						}
						encoder_.append(c, char_bits_);
						addFeature(features);
					}
					encoder_.reset();
				}
				fc++;
			}

			// Suffix feature
			if (use_affix_features_) {
				if (is_rare) {
					encoder_.append(0, order_bits_);
					encoder_.append(0, level_bits_);
					encoder_.append(fc, state_feature_bits_);
					for (int position = 0; position < Math.min(chars.length,
							max_affix_length_); position++) {
						short c = chars[chars.length - position - 1];
						if (c < 0) {
							// Unknown character!
							break;
						}
						encoder_.append(c, char_bits_);
						addFeature(features);
					}
					encoder_.reset();
				}
				fc++;
			}
		}

		int[] token_feature_indexes = word.getTokenFeatureIndexes();
		if (token_feature_indexes != null) {

			for (int token_feature_index : token_feature_indexes) {
				if (token_feature_index >= 0) {
					encoder_.append(0, order_bits_);
					encoder_.append(0, level_bits_);
					encoder_.append(fc, state_feature_bits_);

					encoder_.append(token_feature_index, token_feature_bits_);
					addFeature(features);
					encoder_.reset();
				}
			}
			fc++;
		}

		if (fdict_ != null) {

			FloatFeatureVector vector = extractFloatFeatures(sequence,
					token_index);
			features.setFloatVector(vector);

		} else {
			token_feature_indexes = word.getWeightedTokenFeatureIndexes();
			if (token_feature_indexes != null) {

				features.setFloatVector(new ArrayFloatFeatureVector(
						token_feature_indexes, word
								.getWeightedTokenFeatureWeights(), model_
								.getWeightedTokenFeatureTable().size()));
			}
		}

		if (mdict_ != null) {
			if (mdict_indexes != null) {
				for (int index : mdict_indexes) {
					if (index >= 0) {
						encoder_.append(0, order_bits_);
						encoder_.append(0, level_bits_);
						encoder_.append(fc, state_feature_bits_);
						encoder_.append(index, mdict_bits_);
						addFeature(features);
						encoder_.reset();
					}
				}
			}
			fc++;
		}

		features.setIsState(true);
		features.setWordIndex(form_index);

		assert fc == num_state_features_ || fc + 1 == num_state_features_ : String
				.format("%d != %d", fc, num_state_features_);
		return features;
	}

	private void addFeature(FeatureVector features) {
		Object feature;
		if (hash_feature_table_) {
			feature = encoder_.hashCode();
		} else {
			feature = encoder_.getFeature();
		}

		int index = feature_table_.toIndex(feature, -1, extend_feature_set_);
		features.add(index);
	}

	private FloatFeatureVector extractFloatFeatures(Sequence sentence,
			int token_index) {
		FloatFeatureVector vector = null;

		for (int offset : fdict_.getOffsets()) {
			int index = token_index + offset;

			FloatFeatureVector current_vector = null;
			if (index >= 0 && index < sentence.size()) {
				String form = ((Word) sentence.get(index)).getWordForm();
				current_vector = fdict_.getVector(form);
			}

			if (current_vector == null) {
				current_vector = new ZeroFloatFeatureVector(fdict_.size());
			}

			assert (current_vector != null);

			if (vector == null) {
				vector = current_vector;
			} else {
				vector = new ConcatFloatFeatureVector(vector, current_vector);
			}
		}

		assert vector != null;
		return vector;
	}

	@Override
	public FeatureVector extractTransitionFeatures(State state) {
		prepareEncoder();

		int max_level = state.getLevel();
		int order = state.getOrder();
		FeatureVector features = new FeatureVector(max_level + 1
				+ model_.getNumSubTags());
		for (int depth = 0; depth <= max_level; depth++) {
			int level = max_level - depth;

			if (max_transition_feature_level_ >= 0
					&& level > max_transition_feature_level_) {
				continue;
			}

			encoder_.append(order, order_bits_);
			encoder_.append(level, level_bits_);
			encoder_.append(0, 1);
			State run = state;
			while (run != null) {

				State sub_state = run.getSubLevel(depth);
				int index = sub_state.getIndex();

				encoder_.append(index, tag_bits_[level]);
				run = run.getPreviousSubOrderState();
			}
			addFeature(features);
			encoder_.reset();

		}
		return features;
	}

	protected double getWeight(int index) {
		return weights_[index];
	}

	@Override
	public double dotProduct(State state, FeatureVector vector) {
		assert vector != null;

		State zero_order_state = state.getZeroOrderState();
		int tag_index = getUniversalIndex(zero_order_state);
		double score = 0.0;

		for (int findex = 0; findex < vector.size(); findex++) {
			int feature = vector.get(findex);
			int index = getIndex(feature, tag_index);
			score += getWeight(index);
		}

		FloatFeatureVector float_vector = vector.getFloatVector();
		if (float_vector != null) {
			score += float_vector.getDotProduct(this, tag_index, 0);
		}

		score += dotProductSubTags(zero_order_state, vector);

		if (vector.getIsState()) {
			int index = getObservedIndex((MorphFeatureVector) vector, state);
			if (index >= 0) {
				score += getWeight(index);
			}
		}

		return score * scale_factor_;
	}

	public double getFloatWeight(int index) {
		return float_weights_[index];
	}

	public int getFloatIndex(int feature, int tag_index) {
		return feature * total_num_tags_ + tag_index;
	}

	private double dotProductSubTags(State state, FeatureVector vector) {
		int level = state.getLevel();

		if (level >= model_.getTagToSubTags().length) {
			return 0.0;
		}

		int[][] tag_to_subtag = model_.getTagToSubTags()[level];

		if (tag_to_subtag == null) {
			return 0.0;
		}

		int[] indexes = tag_to_subtag[state.getIndex()];

		if (indexes == null) {
			return 0.0;
		}

		double score = 0.0;
		for (int index : indexes) {
			int simple_index = getSimpleSubMorphIndex(index);

			for (int findex = 0; findex < vector.size(); findex++) {
				int feature = vector.get(findex);
				int f_index = getIndex(feature, simple_index);
				score += getWeight(f_index);
			}

			FloatFeatureVector float_vector = vector.getFloatVector();
			if (float_vector != null) {
				score += float_vector.getDotProduct(this, simple_index, 0);
			}
		}

		return score;
	}

	private int getProductIndex(State state) {

		if (state.getLevel() == 0) {
			return state.getIndex();
		}

		int size = num_tags_[state.getLevel()];
		return getProductIndex(state.getSubLevelState()) * size
				+ state.getIndex();
	}

	private int getUniversalIndex(int tag_index, int level) {
		for (int clevel = 0; clevel < level; clevel++) {
			tag_index += num_tags_[clevel];
		}
		return tag_index;
	}

	private int getUniversalIndex(State zero_order_state) {
		return getUniversalIndex(zero_order_state.getIndex(),
				zero_order_state.getLevel());
	}

	private int getIndex(int feature, int tag_index) {
		int index = feature * total_num_tags_ + tag_index;

		int capacity = weights_.length - 2 * max_level_;

		int h = index;
		if (use_hash_vector) {

			h ^= (h >>> 20) ^ (h >>> 12);
			h = h ^ (h >>> 7) ^ (h >>> 4);
			h = h & (capacity - 1);

		} else {

			if (index >= capacity) {

				int old_capacity = capacity;

				capacity = (3 * (index + 1)) / 2;
				int length = capacity + 2 * max_level_;

				weights_ = Arrays.copyOf(weights_, length);

				if (accumulated_penalties_ != null) {
					accumulated_penalties_ = Arrays.copyOf(
							accumulated_penalties_, length);
				}

				for (int i = 0; i < 2 * max_level_; i++) {

					weights_[capacity + i] = weights_[old_capacity + i];
					weights_[old_capacity + i] = 0.0;
					if (accumulated_penalties_ != null) {
						accumulated_penalties_[capacity + i] = accumulated_penalties_[old_capacity
								+ i];
						accumulated_penalties_[old_capacity + i] = 0.0;
					}

				}

			}
		}

		assert h >= 0 : String.format("H: %d", h);
		assert h < capacity : String.format("H: %d Capacity: %d", h, capacity);
		return h;
	}

	@Override
	public void init(Model model, Collection<Sequence> sequences) {
		int max_level = model.getTagTables().size();
		feature_table_ = new SymbolTable<>();
		model_ = (MorphModel) model;
		max_level_ = max_level;
		num_tags_ = new int[max_level];
		total_num_tags_ = 0;
		tag_bits_ = new int[max_level];
		for (int level = 0; level < Math.min(model.getTagTables().size(),
				max_level); level++) {
			num_tags_[level] = model.getTagTables().get(level).size();
			tag_bits_[level] = Encoder.bitsNeeded(num_tags_[level]);
			total_num_tags_ += num_tags_[level];
		}

		simple_sub_morph_start_index_ = total_num_tags_;
		total_num_tags_ += model_.getNumSubTags();

		word_bits_ = Encoder.bitsNeeded(model_.getWordTable().size());
		state_feature_bits_ = Encoder.bitsNeeded(num_state_features_);
		char_bits_ = Encoder.bitsNeeded(model_.getCharTable().size());
		if (shape_)
			shape_bits_ = Encoder.bitsNeeded(model_.getNumShapes());
		order_bits_ = Encoder.bitsNeeded(model.getOrder());
		level_bits_ = Encoder.bitsNeeded(max_level);
		signature_bits_ = Encoder.bitsNeeded(model_.getMaxSignature());

		token_feature_bits_ = Encoder.bitsNeeded(model_.getTokenFeatureTable()
				.size());

		if (fdict_ != null) {
			float_weights_ = new double[fdict_.getOffsets().length
					* fdict_.size() * total_num_tags_];
		} else {
			float_weights_ = new double[model_.getWeightedTokenFeatureTable()
					.size() * total_num_tags_];
		}

		extend_feature_set_ = true;
		scale_factor_ = 1.;

		int capacity = 1;
		int initial_size = initial_vector_size_;
		while (capacity < initial_size)
			capacity <<= 1;

		weights_ = new double[capacity + 2 * max_level];
	}

	private void update(State state, double value) {
		FeatureVector vector = state.getVector();
		if (vector != null) {
			State run = state.getZeroOrderState();

			while (run != null) {

				int tag_index = getUniversalIndex(run);
				for (int findex = 0; findex < vector.size(); findex++) {
					int feature = vector.get(findex);
					int index = getIndex(feature, tag_index);
					updateWeight(index, value);
				}

				FloatFeatureVector float_vector = vector.getFloatVector();
				if (float_vector != null) {
					float_vector.updateFloatWeight(this, tag_index, 0, value);
				}

				updateSubTags(run, vector, value);

				if (state.getOrder() == 1) {
					run = null;

					State sub_level_state = state.getSubLevelState();
					if (sub_level_state != null)
						update(sub_level_state, value);

					if (vector.getIsState()) {
						int index = getObservedIndex(
								(MorphFeatureVector) vector, state);
						if (index >= 0) {
							updateWeight(index, value);
						}
					}

				} else {
					run = run.getSubLevelState();
				}
			}
		}
	}

	private void updateSubTags(State state, FeatureVector vector, double value) {
		int level = state.getLevel();

		if (level >= model_.getTagToSubTags().length) {
			return;
		}

		int[][] tag_to_subtag = model_.getTagToSubTags()[level];

		if (tag_to_subtag == null) {
			return;
		}

		int[] indexes = tag_to_subtag[state.getIndex()];

		if (indexes == null) {
			return;
		}

		for (int index : indexes) {
			int simple_index = getSimpleSubMorphIndex(index);
			for (int findex = 0; findex < vector.size(); findex++) {
				int feature = vector.get(findex);
				int f_index = getIndex(feature, simple_index);
				updateWeight(f_index, value);
			}

			FloatFeatureVector float_vector = vector.getFloatVector();
			if (float_vector != null) {
				float_vector.updateFloatWeight(this, simple_index, 0, value);
			}
		}

	}

	protected int getSimpleSubMorphIndex(int sub_morph_index) {
		return simple_sub_morph_start_index_ + sub_morph_index;
	}

	protected void updateWeight(int index, double value) {
		weights_[index] += value;
		if (accumulated_penalties_ != null) {
			weights_[index] = applyPenalty(index, weights_[index],
					accumulated_penalties_);
		}
	}

	public void updateFloatWeight(int index, double value) {
		float_weights_[index] += value;
		if (accumulated_penalties_ != null) {
			float_weights_[index] = applyPenalty(index, float_weights_[index],
					accumulated_float_penalties_);
		}
	}

	protected int getObservedIndex(MorphFeatureVector vector, State state) {
		int word_index = vector.getWordIndex();
		int level = state.getLevel();
		int product_index = getProductIndex(state);

		int feature = model_.hasBeenObserved(word_index, level, product_index) ? 0
				: 1;

		int start_index = weights_.length - max_level_ * 2;
		int index = start_index + level * 2 + feature;
		return index;
	}

	protected double applyPenalty(int index, double weight,
			double[] accumulated_penalty) {
		double z = weight;

		if (z - 1e-10 > 0.) {
			weight = Math.max(0, z
					- (accumulated_penalty_ + accumulated_penalty[index]));
		} else if (z + 1e-10 < 0.) {
			weight = Math.min(0, z
					+ (accumulated_penalty_ - accumulated_penalty[index]));
		}

		accumulated_penalty[index] += weight - z;
		return weight;
	}

	protected void prepareEncoder() {
		if (encoder_ == null) {
			encoder_ = new Encoder(ENCODER_CAPACITY_);
		}
		encoder_.reset();
	}

	@Override
	public void updateWeights(State state, double value, boolean is_transition) {
		value /= scale_factor_;

		update(state, value);
		if (!is_transition) {
			while ((state = state.getSubOrderState()) != null) {
				update(state, value);
			}
		}
	}

	@Override
	public void scaleBy(double scale_factor) {
		accumulated_penalty_ /= scale_factor;
		scale_factor_ *= scale_factor;
	}

	@Override
	public double[] getWeights() {
		return weights_;
	}

	@Override
	public void setWeights(double[] weights) {
		weights_ = weights;
	}

	public MorphDictionary getMorphDict() {
		return mdict_;
	}

	public SymbolTable<Object> getFeatureTable() {
		return feature_table_;
	}

	@Override
	public double[] getFloatWeights() {
		return float_weights_;
	}

	@Override
	public void setFloatWeights(double[] weights) {
		float_weights_ = weights;
	}

	public MorphModel getModel() {
		return model_;
	}
}
