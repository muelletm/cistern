package marmot.lemma.toutanova;

import java.util.List;

import marmot.lemma.Instance;
import marmot.lemma.toutanova.Aligner.Pair;
import marmot.morph.Word;
import marmot.util.SymbolTable;

public class Model {

	int alphabet_[];
	private SymbolTable<String> input_table_;
	private SymbolTable<String> output_table_;
	private int max_input_segment_length_;

	public void init(List<ToutanovaInstance> train_instances, List<ToutanovaInstance> test_instances) {
		input_table_ = new SymbolTable<>();
		output_table_ = new SymbolTable<>();
		
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
			
			instance.setFormSegments(form_segments);
			instance.setLemmaSegments(form_segments);
			
		}
		
		max_input_segment_length_ = 0;
		for (String segment : input_table_.getSymbols()) {
			max_input_segment_length_ = Math.max(max_input_segment_length_, segment.length());
		}
	}

	public SymbolTable<String> getOutputTable() {
		return output_table_;
	}

	public int getMaxInputSegmentLength() {
		return max_input_segment_length_;
	}

	
}
