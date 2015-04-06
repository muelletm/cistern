package marmot.lemma.toutanova;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import marmot.core.Feature;
import marmot.lemma.toutanova.Aligner.Pair;
import marmot.lemma.toutanova.ToutanovaTrainer.Options;
import marmot.util.Encoder;
import marmot.util.SymbolTable;

public class Model {

	int alphabet_[];
	private SymbolTable<String> output_table_;
	private SymbolTable<String> pos_table_;
	private int max_input_segment_length_;
	private int num_output_bits;
	private SymbolTable<Feature> feature_map;
	private SymbolTable<Character> char_table;
	private Encoder encoder;
	private double[] weights;
	private int num_char_bits;
	private static final int OFFSET = 1;

	public void init(Options options, List<ToutanovaInstance> train_instances, List<ToutanovaInstance> test_instances) {
		output_table_ = new SymbolTable<>(true);
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
			
			assert (result.getOutput().equals(instance.getInstance().getLemma())) : result.getOutput() + " " + instance.getInstance().getLemma();
			
			instance.setResult(result);
		}
		
		addIndexes(train_instances, true);
		if (test_instances != null)
			addIndexes(test_instances, false);
				
		num_output_bits = Encoder.bitsNeeded(output_table_.size());
		num_char_bits = Encoder.bitsNeeded(char_table.size());
		
		feature_map = new SymbolTable<>();
		encoder = new Encoder(10);
		weights = new double[10000000];
	}

	public SymbolTable<String> getOutputTable() {
		return output_table_;
	}
	
	public int getMaxInputSegmentLength() {
		return max_input_segment_length_;
	}

	public int getTransitionFeatureIndex(int last_o, int o) {
		
		if (last_o < 0) {
			return -1;
		}
		
		encoder.reset();
		encoder.append(0, Encoder.bitsNeeded(2));
		encoder.append(last_o, num_output_bits);
		encoder.append(o, num_output_bits);
		Feature feature = encoder.getFeature();
		return feature_map.toIndex(feature, true) + OFFSET;
	}
	
	public int getOutputFeatureIndex(int o) {
		encoder.reset();
		encoder.append(1, Encoder.bitsNeeded(2));
		encoder.append(o, num_output_bits);
		Feature feature = encoder.getFeature();
		return feature_map.toIndex(feature, true) + OFFSET;
	}
	
	public int getPairFeatureIndex(int[] chars, int l_start, int l_end, int o) {
		encoder.reset();
		encoder.append(2, Encoder.bitsNeeded(2));
		
		for (int l=l_start; l<l_end; l++) {
			int c = chars[l];
			if (c < 0) {
				return - 1;
			}
			encoder.append(c, num_char_bits);
		}
		
		encoder.append(o, num_output_bits);
		Feature feature = encoder.getFeature();
		return feature_map.toIndex(feature, true) + OFFSET;
	}
	
	public double getWeight(int index) {
		if (index < 0)
			return 0.;
		
		return weights[index];
	}
	
	public void setWeight(int index, double w) {
		weights[index] = w;
	}

	public String getOutput(int o) {
		return output_table_.toSymbol(o);
	}

	public double getPairScore(ToutanovaInstance instance, int l_start, int l_end,
			int o) {				
		int index = getOutputFeatureIndex(o);
		double score = getWeight(index);
		
		index = getPairFeatureIndex(instance.getFormCharIndexes(), l_start, l_end, o);
		score += getWeight(index);
		
		index = getCopyFeatureIndex(instance, l_start, l_end, o);
		score += getWeight(index);
		
		return score;
	}

	public double getTransitionScore(ToutanovaInstance instance, int last_o,
			int o, int l_start, int l) {
		int index = getTransitionFeatureIndex(last_o, o);	
		double score = getWeight(index);
		return score;
	}

	public void update(ToutanovaInstance instance, Result result, double update) {
			
		Iterator<Integer> output_iterator = result.getOutputs().iterator();
		Iterator<Integer> input_iterator = result.getInputs().iterator();
		
		int last_o = -1;
		int l_start = 0;
		
		while (output_iterator.hasNext()) {	
			int o = output_iterator.next();
			int l_end = input_iterator.next();
			
			if (last_o >= 0) {
				updateTransitionScore(instance, last_o, o, l_start, l_end, update);
			}
			
			updatePairScore(instance, o, l_start, l_end, update);
			
			last_o = o;
			l_start = l_end;
		}
		
	}

	private void updatePairScore(ToutanovaInstance instance, int o,
			int l_start, int l_end, double update) {
		int index = getPairFeatureIndex(instance.getFormCharIndexes(), l_start, l_end, o);
		updateWeight(index, update);	
		
		index = getOutputFeatureIndex(o);
		updateWeight(index, update);
		
		index = getCopyFeatureIndex(instance, l_start, l_end, o);
		updateWeight(index, update);
		
	}

	private int getCopyFeatureIndex(ToutanovaInstance instance, int l_start,
			int l_end, int o) {
		if (l_end - l_start == 1) {
			String output_sequence = output_table_.toSymbol(o);			
			if (output_sequence.length() == 1) {
				char input_char = instance.getInstance().getForm().charAt(l_start);
				char output_char = output_sequence.charAt(0);
				if (input_char == output_char) {
					return 1;
				}
			}
		}
		return 0;
	}

	private void updateWeight(int index, double update) {
		if (index >= 0) {
			weights[index] += update;
		}
	}

	private void updateTransitionScore(ToutanovaInstance instance, int last_o,
			int o, int l_start, int l_end, double update) {
		int index = getTransitionFeatureIndex(last_o, o);
		updateWeight(index, update);
	}

	public double getScore(ToutanovaInstance instance, Result result) {
		double score = 0.0;
		
		Iterator<Integer> output_iterator = result.getOutputs().iterator();
		Iterator<Integer> input_iterator = result.getInputs().iterator();
		
		int last_o = -1;
		int l_start = 0;
		
		while (output_iterator.hasNext()) {	
			int o = output_iterator.next();
			int l_end = input_iterator.next();
			
			if (last_o >= 0) {
				score += getTransitionScore(instance, last_o, o, l_start, l_end);
			}
			
			score += getPairScore(instance, l_start, l_end, o);			
			last_o = o;
			l_start = l_end;
		}
		
		return score;
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
		
		if (pos_table_ != null) {
			String pos_tag = instance.getInstance().getPosTag();
			if (pos_tag != null)
				instance.setPosTagIndex(pos_table_.toIndex(pos_tag, -1, insert));
		}
		instance.setFormCharIndexes(char_indexes);		
	}


}
