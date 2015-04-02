package marmot.lemma.toutanova;

import java.util.List;

import marmot.lemma.Instance;
import marmot.morph.Word;
import marmot.util.SymbolTable;

public class Model {

	int alphabet_[];
	private SymbolTable<Object> input_table_;
	private SymbolTable<Object> output_table_;

	public void init(List<Instance> train_instances, List<Instance> test_instances) {
		input_table_ = new SymbolTable<>();
		output_table_ = new SymbolTable<>();
	}

	
}
