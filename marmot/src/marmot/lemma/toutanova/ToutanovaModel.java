// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.lemma.toutanova;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import marmot.core.Feature;
import marmot.lemma.toutanova.Aligner.Pair;
import marmot.lemma.toutanova.ToutanovaTrainer.ToutanovaOptions;
import marmot.util.DynamicWeights;
import marmot.util.Encoder;
import marmot.util.SymbolTable;

public class ToutanovaModel implements Serializable {

	private static final long serialVersionUID = 1L;
	private String alphabet_[];
	private SymbolTable<String> output_table_;
	private SymbolTable<String> pos_table_;
	private int max_input_segment_length_;
	private int num_output_bits;
	private SymbolTable<Character> char_table;
	transient private Encoder encoder;

	private int num_char_bits;
	private int num_pos_bits;

	private IndexScorer scorer_;
	private IndexUpdater updater_;
	private boolean use_zero_order_;
	private int max_input_segment_length_bits_;
	private boolean use_context_feature_;
	private DynamicWeights weights_;

	private final static int FEATURE_BITS = Encoder.bitsNeeded(2);
	private final static int TRANS_FEAT = 0;
	private final static int OUTPUT_FEAT = 1;
	private final static int PAIR_FEAT = 2;

	private final static String COPY_SYMBOL = "<COPY>";

	public void init(ToutanovaOptions options, List<ToutanovaInstance> train_instances,
			List<ToutanovaInstance> test_instances) {
		Logger logger = Logger.getLogger(getClass().getName());

		createOutputTable(options, train_instances);
		logger.info("Output alphabet size: " + output_table_.size());
		logger.info("Max input segment length: " + max_input_segment_length_);

		if (options.getFilterAlphabet() > 0) {
			filterRareOutputSymbols(options, train_instances);
			createOutputTable(options, train_instances);
			logger.info("Output alphabet size: " + output_table_.size());
			logger.info("Max input segment length: "
					+ max_input_segment_length_);
		}

		char_table = new SymbolTable<>();
		if (options.getUsePos()) {
			pos_table_ = new SymbolTable<>();
		}

		addIndexes(train_instances, true);

		if (test_instances != null)
			addIndexes(test_instances, false);

		num_output_bits = Encoder.bitsNeeded(output_table_.size());
		alphabet_ = new String[output_table_.size()];
		for (Map.Entry<String, Integer> entry : output_table_.entrySet())
			alphabet_[entry.getValue()] = entry.getKey();
		output_table_.setBidirectional(false);

		num_char_bits = Encoder.bitsNeeded(char_table.size());

		num_pos_bits = -1;
		if (pos_table_ != null)
			num_pos_bits = Encoder.bitsNeeded(pos_table_.size());

		encoder = new Encoder(10);

		weights_ = new DynamicWeights(options.getRandom());

		SymbolTable<Feature> feature_map = new SymbolTable<>();
		scorer_ = new IndexScorer(weights_, feature_map, num_pos_bits);
		updater_ = new IndexUpdater(weights_, feature_map, num_pos_bits);

		use_context_feature_ = options.getUseContextFeature();
		use_zero_order_ = options.getDecoderInstance().getOrder() < 1;
		
		setupTemp();
	}

	private void setupTemp() {
		encoder = new Encoder(10);
	}

	private void readObject(ObjectInputStream ois)
			throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		setupTemp();
	}

	private void createOutputTable(ToutanovaOptions options,
			List<ToutanovaInstance> train_instances) {
		output_table_ = new SymbolTable<>(true);
		output_table_.insert(COPY_SYMBOL);

		max_input_segment_length_ = 0;

		for (ToutanovaInstance instance : train_instances) {

			if (instance.isRare()) {
				instance.setResult(null);
				continue;
			}

			String form = instance.getInstance().getForm();

			assert instance.getAlignment() != null;

			List<Pair> pairs = Aligner.Pair.toPairs(form, instance
					.getInstance().getLemma(), instance.getAlignment());

			List<Integer> form_indexes = new ArrayList<>(pairs.size());
			List<Integer> lemma_segments = new ArrayList<>(pairs.size());

			int start_index = 0;

			for (Pair pair : pairs) {
				int current_input_length = pair.getInputSegment().length();

				max_input_segment_length_ = Math.max(max_input_segment_length_,
						current_input_length);

				start_index += current_input_length;

				form_indexes.add(start_index);

				int output_segment_index = 0;
				if (!pair.getInputSegment().equals(pair.getOutputSegment())) {
					output_segment_index = output_table_.toIndex(
							pair.getOutputSegment(), true);
				}

				lemma_segments.add(output_segment_index);
			}

			Result result = new Result(this, lemma_segments, form_indexes, form);

			assert (result.getOutput()
					.equals(instance.getInstance().getLemma()));

			instance.setResult(result);
		}

		max_input_segment_length_bits_ = Encoder
				.bitsNeeded(max_input_segment_length_);
	}

	private void filterRareOutputSymbols(ToutanovaOptions options,
			List<ToutanovaInstance> train_instances) {

		Logger logger = Logger.getLogger(getClass().getName());

		int[] count = new int[output_table_.size()];
		for (ToutanovaInstance instance : train_instances) {
			for (int output_index : instance.getResult().getOutputs()) {
				count[output_index]++;
			}
		}

		int rare_output_symbols = 0;
		for (int i = 0; i < count.length; i++) {
			if (count[i] == 1) {
				rare_output_symbols++;
			}
		}
		logger.info(String.format("Num rare output symbols (< %d): %d",
				options.getFilterAlphabet(), rare_output_symbols));

		for (ToutanovaInstance instance : train_instances) {
			boolean instance_is_rare = false;
			for (int output_index : instance.getResult().getOutputs()) {
				if (count[output_index] <= options.getFilterAlphabet()) {
					instance_is_rare = true;
					break;
				}
			}
			instance.setRare(instance_is_rare);
		}

	}

	public SymbolTable<String> getOutputTable() {
		return output_table_;
	}

	public int getMaxInputSegmentLength() {
		return max_input_segment_length_;
	}

	public String getOutput(int o) {
		if (alphabet_ == null) {
			return output_table_.toSymbol(o);
		}

		return alphabet_[o];
	}

	public void consumeTransitionFeature(IndexConsumer consumer,
			ToutanovaInstance instance, int l_start, int l_end, int last_o,
			int o) {
		if (last_o < 0) {
			return;
		}
		encoder.reset();
		encoder.append(TRANS_FEAT, FEATURE_BITS);
		encoder.append(last_o, num_output_bits);
		encoder.append(o, num_output_bits);
		if (use_context_feature_) {
			encoder.append(l_start == 0);
			encoder.append(l_end == instance.getFormCharIndexes().length);
		}
		consumer.consume(instance, encoder);
	}

	public void consumeOutputFeature(IndexConsumer consumer,
			ToutanovaInstance instance, int l_start, int l_end, int o) {
		encoder.reset();
		encoder.append(OUTPUT_FEAT, FEATURE_BITS);
		encoder.append(o, num_output_bits);
		if (use_context_feature_) {
			encoder.append(l_start == 0);
			encoder.append(l_end == instance.getFormCharIndexes().length);
		}
		consumer.consume(instance, encoder);
	}

	public void consumePairFeature(IndexConsumer consumer,
			ToutanovaInstance instance, int l_start, int l_end, int o) {
		int[] chars = instance.getFormCharIndexes();
		encoder.reset();
		encoder.append(PAIR_FEAT, FEATURE_BITS);
		encoder.append(o, num_output_bits);
		encoder.append(l_end - l_start, max_input_segment_length_bits_);
		if (use_context_feature_) {
			encoder.append(l_start == 0);
			encoder.append(l_end == instance.getFormCharIndexes().length);
		}
		for (int l = l_start; l < l_end; l++) {
			int c = chars[l];
			if (c < 0) {
				return;
			}
			encoder.append(c, num_char_bits);
		}

		consumer.consume(instance, encoder);
	}

	private void consumeOutputPair(IndexConsumer consumer,
			ToutanovaInstance instance, int l_start, int l_end, int o) {
		consumePairFeature(consumer, instance, l_start, l_end, o);
		consumeOutputFeature(consumer, instance, l_start, l_end, o);
	}

	private void consumeTransition(IndexConsumer consumer,
			ToutanovaInstance instance, int l_start, int l_end, int last_o,
			int o) {

		if (use_zero_order_) {
			return;
		}

		consumeTransitionFeature(consumer, instance, l_start, l_end, last_o, o);
	}

	public double getPairScore(ToutanovaInstance instance, int l_start,
			int l_end, int o) {
		scorer_.reset();
		consumeOutputPair(scorer_, instance, l_start, l_end, o);
		return scorer_.getScore();
	}

	public double getTransitionScore(ToutanovaInstance instance, int last_o,
			int o, int l_start, int l_end) {
		scorer_.reset();
		consumeTransition(scorer_, instance, l_start, l_end, last_o, o);
		return scorer_.getScore();
	}

	public double getScore(ToutanovaInstance instance, Result result) {
		scorer_.reset();

		Iterator<Integer> output_iterator = result.getOutputs().iterator();
		Iterator<Integer> input_iterator = result.getInputs().iterator();

		int last_o = -1;
		int l_start = 0;

		while (output_iterator.hasNext()) {
			int o = output_iterator.next();
			int l_end = input_iterator.next();

			if (last_o >= 0) {
				consumeTransition(scorer_, instance, l_start, l_end, last_o, o);
			}

			consumeOutputPair(scorer_, instance, l_start, l_end, o);
			last_o = o;
			l_start = l_end;
		}

		return scorer_.getScore();
	}

	public void update(ToutanovaInstance instance, Result result, double update) {
		updater_.setUpdate(update);
		Iterator<Integer> output_iterator = result.getOutputs().iterator();
		Iterator<Integer> input_iterator = result.getInputs().iterator();

		int last_o = -1;
		int l_start = 0;

		while (output_iterator.hasNext()) {
			int o = output_iterator.next();
			int l_end = input_iterator.next();

			if (last_o >= 0) {
				consumeTransition(updater_, instance, l_start, l_end, last_o, o);
			}

			consumeOutputPair(updater_, instance, l_start, l_end, o);

			last_o = o;
			l_start = l_end;
		}

	}

	public void addIndexes(List<ToutanovaInstance> instances, boolean insert) {
		for (ToutanovaInstance instance : instances) {
			addIndexes(instance, insert);
		}
	}

	public void addIndexes(ToutanovaInstance instance, boolean insert) {
		if (!instance.isRare()) {
			String form = instance.getInstance().getForm();
			int[] char_indexes = new int[form.length()];
			for (int i = 0; i < form.length(); i++) {
				char_indexes[i] = char_table
						.toIndex(form.charAt(i), -1, insert);
			}
			instance.setFormCharIndexes(char_indexes);

			if (pos_table_ != null) {
				String pos_tag = instance.getInstance().getPosTag();
				if (pos_tag != null) {
					int index = pos_table_.toIndex(pos_tag, -1, insert);
					instance.setPosTagIndex(index);
				}
			}
		}
	}

	public DynamicWeights getWeights() {
		return weights_;
	}

	public void setWeights(DynamicWeights weights) {
		weights_ = weights;
		scorer_.setWeights(weights);
		updater_.setWeights(weights);
	}

}
