package marmot.lemma.toutanova;

import java.util.List;

import marmot.core.Feature;
import marmot.lemma.toutanova.Aligner.Pair;
import marmot.util.Encoder;
import marmot.util.SymbolTable;

public class Model {

	int alphabet_[];
	private SymbolTable<String> input_table_;
	private SymbolTable<String> output_table_;
	private int max_input_segment_length_;
	private int num_output_bits;
	private SymbolTable<Feature> feature_map;
	private SymbolTable<Character> char_table;
	private Encoder encoder;
	private double[] weights;
	private int num_char_bits;

	public void init(List<ToutanovaInstance> train_instances, List<ToutanovaInstance> test_instances) {
		input_table_ = new SymbolTable<>();
		output_table_ = new SymbolTable<>(true);
		char_table = new SymbolTable<>();
		
		for (ToutanovaInstance instance : train_instances) {
			
			List<Pair> pairs = Aligner.Pair.toPairs(instance.getInstance().getForm(), instance.getInstance().getLemma(), instance.getAlignment());
			
			int[] form_segments = new int[pairs.size()];
			int[] lemma_segments = new int[pairs.size()];
			
			int index = 0;
			
			for (Pair pair : pairs) {
				
				form_segments[index] = input_table_.toIndex(pair.getInputSegment(), true);
				lemma_segments[index] = output_table_.toIndex(pair.getOutputSegment(), true);
						
						
				index++;
				
			}	
			
			String form = instance.getInstance().getForm();
			int[] char_indexes = new int[form.length()];
			for (int i=0; i < form.length(); i++) {
				char_indexes[i] = char_table.toIndex(form.charAt(i), true);
			}
			instance.setFormCharIndexes(char_indexes);
			instance.setFormSegments(form_segments);
			instance.setLemmaSegments(form_segments);
		}
		
		max_input_segment_length_ = 0;
		for (String segment : input_table_.getSymbols()) {
			max_input_segment_length_ = Math.max(max_input_segment_length_, segment.length());
		}
		
		num_output_bits = Encoder.bitsNeeded(output_table_.size());
		num_char_bits = Encoder.bitsNeeded(char_table.size());
		
		feature_map = new SymbolTable<>();
		encoder = new Encoder(10);
		weights = new double[10000];
		
		
		
	}

	public SymbolTable<String> getOutputTable() {
		return output_table_;
	}
	
	public SymbolTable<String> getInputTable() {
		return input_table_;
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
		return feature_map.toIndex(feature, true);
	}
	
	public int getOutputFeatureIndex(int o) {
		encoder.reset();
		encoder.append(1, Encoder.bitsNeeded(2));
		encoder.append(o, num_output_bits);
		Feature feature = encoder.getFeature();
		return feature_map.toIndex(feature, true);
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
		return feature_map.toIndex(feature, true);
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

	public double getPairScore(ToutanovaInstance instance, int l_start, int l,
			int o) {
		int out_f = getOutputFeatureIndex(o);
		int pair_f = getPairFeatureIndex(instance.getFormCharIndexes(), l_start, l, o);
		double score = getWeight(out_f) + getWeight(pair_f);
		return score;
	}

	public double getTransitionScore(ToutanovaInstance instance, int last_o,
			int o, int l_start, int l) {
		int tran_f = getTransitionFeatureIndex(last_o, o);	
		double score = getWeight(tran_f);
		return score;
	}


}
