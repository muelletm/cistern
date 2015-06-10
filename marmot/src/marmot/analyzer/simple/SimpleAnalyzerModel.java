package marmot.analyzer.simple;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import marmot.analyzer.AnalyzerInstance;
import marmot.analyzer.AnalyzerTag;
import marmot.core.DenseArrayFloatFeatureVector;
import marmot.core.FloatFeatureVector;
import marmot.morph.FloatHashDictionary;
import marmot.morph.MorphDictionaryOptions;
import marmot.util.Converter;
import marmot.util.Encoder;
import marmot.util.FeatUtil;
import marmot.util.FeatureTable;
import marmot.util.SymbolTable;

public class SimpleAnalyzerModel implements Serializable {

	private SymbolTable<AnalyzerTag> tag_table_;
	private SymbolTable<String> pos_table_;
	private SymbolTable<String> morph_table_;
	private SymbolTable<Character> char_table_;
	private List<List<Integer>> tag_to_sub_;

	private static enum Features {
		affix_feature, signature_feature, dict_feature
	}

	private static final int feature_bits_ = Encoder.bitsNeeded(Features
			.values().length - 1);

	private int char_bits_;
	private int sig_bits_;
	private Encoder encoder_;
	private FeatureTable feature_table_;
	transient private Context context_;
	private FloatHashDictionary dict_;

	private boolean special_signature_ = true;
	private int max_affix_length_ = 10;
	private boolean use_hash_table_ = false;

	private static class Context {
		public List<Integer> list;
		public boolean insert;

		public Context() {
			list = new ArrayList<>();
		}
	}

	private double[] weights_;
	private long feat_length_;
	private int dict_bits_;

	public void init(Collection<SimpleAnalyzerInstance> instances,
			MorphDictionaryOptions options) {

		tag_table_ = new SymbolTable<>();
		pos_table_ = new SymbolTable<>();
		morph_table_ = new SymbolTable<>();
		tag_to_sub_ = new ArrayList<>();
		char_table_ = new SymbolTable<>();

		Logger logger = Logger.getLogger(getClass().getName());

		if (options != null) {
			dict_ = new FloatHashDictionary();
			dict_.init(options);
			dict_bits_ = Encoder.bitsNeeded(dict_.getDimension());

			logger.info(String.format(
					"read dict with dimension %d and %d entries.",
					dict_.getDimension(), dict_.numEntries()));

		}

		for (SimpleAnalyzerInstance instance : instances) {
			init(instance, true);
		}

//		logger.info(String.format("tags: %d %s", tag_table_.size(), tag_table_));
//		logger.info(String.format("pos tags: %d %s", pos_table_.size(),
//				pos_table_));
//		logger.info(String.format("morph tags: %d %s", morph_table_.size(),
//				morph_table_));

		sig_bits_ = Encoder.bitsNeeded(FeatUtil
				.getMaxSignature(special_signature_));
		char_bits_ = Encoder.bitsNeeded(char_table_.size());

		encoder_ = new Encoder(6);
		feature_table_ = FeatureTable.StaticMethods.create(use_hash_table_);
		

		for (SimpleAnalyzerInstance instance : instances) {
			add_features(instance, true);
		}

		feat_length_ = feature_table_.size();
		weights_ = new double[10_000_000];
	}

	private void add_features(SimpleAnalyzerInstance instance, boolean insert) {
		if (context_ == null) {
			context_ = new Context();
		}
		context_.insert = insert;
		context_.list.clear();

		addAffixIndexes(instance.getFormChars());

		encoder_.append(Features.signature_feature.ordinal(), feature_bits_);
		encoder_.append(instance.getSignature(), sig_bits_);
		addFeature(true);

		instance.setFeatureIndexes(Converter.toIntArray(context_.list));
		context_.list.clear();

		DenseArrayFloatFeatureVector vector = (DenseArrayFloatFeatureVector) instance
				.getVector();
		if (vector != null) {
			for (int i = 0; i < vector.getDim(); i++) {
				encoder_.append(Features.dict_feature.ordinal(), feature_bits_);
				encoder_.append(i, dict_bits_);
				addFeature(true);
			}
			instance.setFloatFeatIndexes(Converter.toIntArray(context_.list));
			instance.setFloatValues(vector.getValues());
		}
	}

	private void init(SimpleAnalyzerInstance instance, boolean insert) {
		List<Integer> tag_indexes = new LinkedList<>();

		if (insert) {
			for (AnalyzerTag tag : instance.getTags()) {
				int tag_index = tag_table_.toIndex(tag, insert);

				if (tag_to_sub_.size() <= tag_index) {
					assert insert;
					assert tag_to_sub_.size() == tag_index;
					int pos_index = pos_table_.toIndex(tag.getPosTag(), true);
					int morph_index = morph_table_.toIndex(tag.getMorphTag(),
							true);
					tag_to_sub_.add(Arrays.asList(pos_index, morph_index));
				}

				tag_indexes.add(tag_index);
			}
		}

		instance.setTagIndexes(tag_indexes);

		AnalyzerInstance a_instance = instance.getInstance();
		String form = a_instance.getForm();

		int signature = FeatUtil.getSignature(form, special_signature_);
		instance.setSignature(signature);

		String lower_form = form.toLowerCase();

		short[] form_chars = FeatUtil.getCharIndexes(lower_form, char_table_,
				insert);

		instance.setFormChars(form_chars);

		if (dict_ != null) {
			FloatFeatureVector vector = dict_.getVector(form);
			instance.setVector(vector);
		}

	}

	private void addPrefixFeatures(short[] chars) {
		encoder_.append(false);
		for (int i = 0; i < Math.min(chars.length, max_affix_length_); i++) {
			int c = chars[i];
			if (c < 0)
				return;
			encoder_.append(c, char_bits_);
			addFeature(false);
		}
	}

	private void addSuffixFeatures(short[] chars) {
		encoder_.append(true);
		for (int i = chars.length - 1; i >= Math.max(0, chars.length
				- max_affix_length_); i--) {
			int c = chars[i];
			if (c < 0)
				return;
			encoder_.append(c, char_bits_);
			addFeature(false);
		}
	}

	private void addAffixIndexes(short[] lemma_chars) {
		encoder_.append(Features.affix_feature.ordinal(), feature_bits_);
		addPrefixFeatures(lemma_chars);
		encoder_.reset();

		encoder_.append(Features.affix_feature.ordinal(), feature_bits_);
		addSuffixFeatures(lemma_chars);
		encoder_.reset();
	}

	private void addFeature(boolean reset) {

		int index = feature_table_.getFeatureIndex(encoder_, context_.insert);
		if (index >= 0) {
			context_.list.add(index);
		}

		if (reset)
			encoder_.reset();
	}

	public double[] getWeights() {
		return weights_;
	}

	public void setWeights(double[] weights) {
		weights_ = weights;
	}

	public int getNumTags() {
		return tag_table_.size();
	}

	private void updateScore(SimpleAnalyzerInstance instance, double[] scores,
			double[] updates) {

		for (int tag_index = 0; tag_index < getNumTags(); tag_index++) {
			for (int feat_index : instance.getFeatIndexes()) {
				updateScores(feat_index, scores, updates, tag_index, 1.0);
			}

			int[] float_feat_indexes = instance.getFloatFeatIndexes();
			if (float_feat_indexes != null) {
				double[] values = instance.getFloatValues();
				for (int i = 0; i < float_feat_indexes.length; i++) {
					double value = values[i];
					updateScores(float_feat_indexes[i], scores, updates, tag_index, value);
				}
			}
		}
	}

	private void updateScores(int feat_index, double[] scores,
			double[] updates, int tag_index, double value) {
		long index;

		List<Integer> sub_indexes = tag_to_sub_.get(tag_index);
		long pos_index = sub_indexes.get(0);
		index = (long) feat_index + feat_length_ * pos_index;
		updateScore(index, scores, updates, tag_index, value);

		long morph_index = sub_indexes.get(1);
		index = (long) feat_index + feat_length_
				* (morph_index + (long) pos_table_.size());
		updateScore(index, scores, updates, tag_index, value);

		index = (long) feat_index
				+ feat_length_
				* ((long) tag_index + (long) pos_table_.size() + (long) morph_table_
						.size());
		updateScore(index, scores, updates, tag_index, value);
	}

	private void updateScore(long index, double[] scores, double[] updates,
			int tag_index, double value) {
		int int_index = (int) (index % (long) weights_.length);
		if (updates != null) {
			weights_[int_index] += updates[tag_index] * value;
		}

		if (scores != null) {
			scores[tag_index] += weights_[int_index] * value;
		}
	}

	public void score(SimpleAnalyzerInstance instance, double[] scores) {
		Arrays.fill(scores, 0.0);
		updateScore(instance, scores, null);
	}

	public void update(SimpleAnalyzerInstance instance, double[] updates) {
		updateScore(instance, null, updates);
	}

	public SimpleAnalyzerInstance getInstance(AnalyzerInstance instance) {
		SimpleAnalyzerInstance simple_instance = new SimpleAnalyzerInstance(
				instance, null);
		init(simple_instance, false);
		add_features(simple_instance, false);
		return simple_instance;
	}

	public SymbolTable<AnalyzerTag> getTagTable() {
		return tag_table_;
	}

}
