package marmot.test.lemma.toutanova;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import marmot.lemma.toutanova.Model;
import marmot.lemma.toutanova.ToutanovaInstance;
import marmot.util.SymbolTable;

import org.junit.Test;

public class DecoderTest {

	@Test
	public void test() {
		
		Model model = new Model();
		
		List<ToutanovaInstance> train_instances = new LinkedList<>();
		
		
		int[] alignment = {};
		train_instances.add(new ToutanovaInstance(new Instance("aaae", "aaa", null, null), alignment));
		
		
		model.init(train_instances, null);
		
		
		
		
	}

}
