package marmot.lemma.toutanova;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import marmot.core.Feature;
import marmot.lemma.toutanova.Aligner.Pair;
import marmot.lemma.toutanova.ToutanovaTrainer.Options;
import marmot.util.Encoder;
import marmot.util.SymbolTable;

public class Model {

	private String alphabet_[];
	private SymbolTable<String> output_table_;
	private SymbolTable<String> pos_table_;
	private int max_input_segment_length_;
	private int num_output_bits;
	private SymbolTable<Character> char_table;
	private Encoder encoder;
	private double[] weights;
	private int num_char_bits;
	private int num_pos_bits;
	
	private IndexScorer scorer_;
	private IndexUpdater updater_;
	private final static int FEATURE_BITS = Encoder.bitsNeeded(3);
	private final static int TRANS_FEAT = 0;
	private final static int OUTPUT_FEAT = 1;
	private final static int PAIR_FEAT = 2;
	private final static int COPY_FEAT = 3;

	public void init(Options options, List<ToutanovaInstance> train_instances, List<ToutanovaInstance> test_instances) {
		output_table_ = new SymbolTable<>(false);
		char_table = new SymbolTable<>();
		
		if (options.getUsePos()) {
			pos_table_ = new SymbolTable<>();
		}
		
		max_input_segment_length_ = 0;
		
		for (ToutanovaInstance instance : train_instances) {
			
			assert instance.getAlignment() != null;
			
			List<Pair> pairs = Aligner.Pair.toPairs(instance.getInstance().getForm(), instance.getInstance().getLemma(), instance.getAlignment());
			
			List<Integer> form_indexes = new ArrayList<>(pairs.size());
			List<Integer> lemma_segments = new ArrayList<>(pairs.size());
			
			int start_index = 0;
			
			for (Pair pair : pairs) {
				
				int current_input_length = pair.getInputSegment().length(); 
				
				max_input_segment_length_ = Math.max(max_input_segment_length_, current_input_length);
				
				start_index += current_input_length;
				
				form_indexes.add(start_index);
				lemma_segments.add(output_table_.toIndex(pair.getOutputSegment(), true));
			}	
			
			Result result = new Result(this, lemma_segments, form_indexes);
			instance.setResult(result);
		}
		
		addIndexes(train_instances, true);
		if (test_instances != null)
			addIndexes(test_instances, false);
				
		num_output_bits = Encoder.bitsNeeded(output_table_.size());
		alphabet_ = new String[output_table_.size()];
		for (Map.Entry<String, Integer> entry : output_table_.entrySet())
			alphabet_[entry.getValue()] = entry.getKey();
		
		num_char_bits = Encoder.bitsNeeded(char_table.size());
		
		num_pos_bits = -1;
		if (pos_table_ != null)
			num_pos_bits = Encoder.bitsNeeded(pos_table_.size());
		
		SymbolTable<Feature> feature_map = new SymbolTable<>();
		encoder = new Encoder(10);
		weights = new double[10000000];
		
		scorer_ = new IndexScorer(weights);
		scorer_.setFeatureMap(feature_map).setPosBits(num_pos_bits);
		updater_ = new IndexUpdater(weights);
		updater_.setFeatureMap(feature_map).setPosBits(num_pos_bits);
	}

	public SymbolTable<String> getOutputTable() {
		return output_table_;
	}
	
	public int getMaxInputSegmentLength() {
		return max_input_segment_length_;
	}
	
	public String getOutput(int o) {
		return alphabet_[o];
	}

	public void consumeTransitionFeature(IndexConsumer consumer,ToutanovaInstance instance, int last_o, int o) {
		if (last_o < 0) {
			return;
		}
		encoder.reset();
		encoder.append(TRANS_FEAT, FEATURE_BITS);
		encoder.append(last_o, num_output_bits);
		encoder.append(o, num_output_bits);
		consumer.consume(instance, encoder);
	}
	
	public void consumeOutputFeature(IndexConsumer consumer, ToutanovaInstance instance, int o) {
		encoder.reset();
		encoder.append(OUTPUT_FEAT, FEATURE_BITS);
		encoder.append(o, num_output_bits);
		consumer.consume(instance, encoder);
	}
	
	public void consumePairFeature(IndexConsumer consumer, ToutanovaInstance instance, int l_start, int l_end, int o) {
		int[] chars = instance.getFormCharIndexes();
		encoder.reset();
		encoder.append(PAIR_FEAT, FEATURE_BITS);
		encoder.append(o, num_output_bits);
		for (int l=l_start; l<l_end; l++) {
			int c = chars[l];
			if (c < 0) {
				return;
			}
			encoder.append(c, num_char_bits);
		}
		consumer.consume(instance, encoder);
	}
	
	private void consumeCopyFeature(IndexConsumer consumer, ToutanovaInstance instance, int l_start,
			int l_end, int o) {
		if (l_end - l_start == 1) {
			String output_sequence = alphabet_[o];
			if (output_sequence.length() == 1) {
				char input_char = instance.getInstance().getForm().charAt(l_start);
				char output_char = output_sequence.charAt(0);
				if (input_char == output_char) {
					encoder.reset();
					encoder.append(COPY_FEAT, FEATURE_BITS);
					consumer.consume(instance, encoder);
				}
			}
		}
	}
	
	private void consumeOutputPair(IndexConsumer consumer, ToutanovaInstance instance, int l_start, int l_end, int o) {
		consumePairFeature(consumer, instance, l_start, l_end, o);
		consumeOutputFeature(consumer, instance, o);
		consumeCopyFeature(consumer, instance, l_start, l_end, o);
	}

	private void consumeTransition(IndexConsumer consumer, ToutanovaInstance instance,  int l_start, int l_end, int last_o,
			int o) {
		consumeTransitionFeature(consumer, instance, last_o, o);
	}

	public double getPairScore(ToutanovaInstance instance, int l_start, int l_end,
			int o) {
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
		String form = instance.getInstance().getForm();
		int[] char_indexes = new int[form.length()];
		for (int i=0; i < form.length(); i++) {
			char_indexes[i] = char_table.toIndex(form.charAt(i), -1, insert);
		}
		instance.setFormCharIndexes(char_indexes);
		
		if (pos_table_ != null) {
			String pos_tag = instance.getInstance().getPosTag();
			if (pos_tag != null)
				instance.setPosTagIndex(pos_table_.toIndex(pos_tag, -1, insert));
		}
	}


}
