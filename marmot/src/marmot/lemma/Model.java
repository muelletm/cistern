package marmot.lemma;

import java.util.LinkedList;
import java.util.List;

import marmot.core.Sequence;
import marmot.core.Token;
import marmot.lemma.Aligner.Pair;
import marmot.morph.Word;
import marmot.morph.io.SentenceReader;
import marmot.util.SymbolTable;

public class Model {

	int alphabet_[];
	private SymbolTable<Object> input_table_;
	private SymbolTable<Object> output_table_;

	protected List<Instance> getInstances(SentenceReader reader, boolean for_training) {
		List<Instance> instances = new LinkedList<Instance>(); 
		Aligner aligner = new SimpleAligner();
		
		for (Sequence sentence : reader) {
			
			for (Token token : sentence) {
				
				Word word = (Word) token;
				
				String form = word.getWordForm();
				String lemma = word.getLemma();
				
				List<Integer> alignment = null;
				if (for_training) {
					alignment = aligner.align(form, lemma);
				}
				
				Instance instance = new Instance(form, lemma, alignment, word.getPosTag(), word.getMorphTag());
				instances.add(instance);
			}
			
			
		}

		return instances;
	}

	
	public void init(String train_file, String test_file) {

		List<Instance> training_instances = getInstances(new SentenceReader(train_file), true);
		List<Instance> test_instances = getInstances(new SentenceReader(test_file), false);

		input_table_ = new SymbolTable<>();
		output_table_ = new SymbolTable<>();
		
		for (Instance instance : training_instances) {
			
			List<Pair> pairs = Pair.toPairs(instance.getForm(), instance.getLemma(), instance.getAlignment());
			
			
			
			for (Pair pair : pairs) {
				
				int input_index = input_table_.toIndex(pair.getInputSegment(), true);
				int output_index = output_table_.toIndex(pair.getOutputSegment(), true);
				
			}
			
		}
		
		
		
	}

	
}
