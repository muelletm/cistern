package marmot.test.lemma.toutanova;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import marmot.lemma.Instance;
import marmot.lemma.toutanova.Decoder;
import marmot.lemma.toutanova.Model;
import marmot.lemma.toutanova.ToutanovaInstance;

import org.junit.Test;

public class DecoderTest {

	@Test
	public void test() {
		
		Model model = new Model();
		
		List<ToutanovaInstance> train_instances = new LinkedList<>();
		
		train_instances.add(new ToutanovaInstance(new Instance("aaae", "aaa", null, null), Arrays.asList(1, 1, 1, 1, 2, 1)));
		train_instances.add(new ToutanovaInstance(new Instance("bbbe", "bbb", null, null), Arrays.asList(1, 1, 1, 1, 2, 1)));
		train_instances.add(new ToutanovaInstance(new Instance("ccce", "ccc", null, null), Arrays.asList(1, 1, 1, 1, 2, 1)));
		train_instances.add(new ToutanovaInstance(new Instance("aaaf", "aaa", null, null), Arrays.asList(1, 1, 1, 1, 2, 1)));
		train_instances.add(new ToutanovaInstance(new Instance("bbbf", "bbb", null, null), Arrays.asList(1, 1, 1, 1, 2, 1)));
		train_instances.add(new ToutanovaInstance(new Instance("cccf", "ccc", null, null), Arrays.asList(1, 1, 1, 1, 2, 1)));
		
		model.init(train_instances, null);
		
		Decoder decoder = new Decoder(model);
		
		int a_index = model.getOutputTable().toIndex("a");
		int b_index = model.getOutputTable().toIndex("b");
		int c_index = model.getOutputTable().toIndex("c");
		
		model.setWeight(model.getTransitionFeatureIndex(a_index, a_index), 1.0);
		model.setWeight(model.getTransitionFeatureIndex(b_index, b_index), 1.0);
		model.setWeight(model.getTransitionFeatureIndex(c_index, c_index), 1.0);
		model.setWeight(model.getPairFeatureIndex(train_instances.get(0).getFormCharIndexes(), 2, 4, a_index), 5.0);
		model.setWeight(model.getPairFeatureIndex(train_instances.get(1).getFormCharIndexes(), 2, 4, b_index), 5.0);
		model.setWeight(model.getPairFeatureIndex(train_instances.get(2).getFormCharIndexes(), 2, 4, c_index), 5.0);
		model.setWeight(model.getPairFeatureIndex(train_instances.get(3).getFormCharIndexes(), 2, 4, a_index), 5.0);
		model.setWeight(model.getPairFeatureIndex(train_instances.get(4).getFormCharIndexes(), 2, 4, b_index), 5.0);
		model.setWeight(model.getPairFeatureIndex(train_instances.get(5).getFormCharIndexes(), 2, 4, c_index), 5.0);
		model.setWeight(model.getPairFeatureIndex(train_instances.get(0).getFormCharIndexes(), 0, 1, a_index), 1.0);
		model.setWeight(model.getPairFeatureIndex(train_instances.get(1).getFormCharIndexes(), 0, 1, b_index), 1.0);
		model.setWeight(model.getPairFeatureIndex(train_instances.get(2).getFormCharIndexes(), 0, 1, c_index), 1.0);
		
		assertEquals(Arrays.asList(a_index, a_index, a_index), decoder.decode(train_instances.get(0)));
		assertEquals(Arrays.asList(a_index, a_index, a_index), decoder.decode(train_instances.get(3)));
		assertEquals(Arrays.asList(b_index, b_index, b_index), decoder.decode(train_instances.get(1)));
		assertEquals(Arrays.asList(b_index, b_index, b_index), decoder.decode(train_instances.get(4)));
		assertEquals(Arrays.asList(c_index, c_index, c_index), decoder.decode(train_instances.get(2)));
		assertEquals(Arrays.asList(c_index, c_index, c_index), decoder.decode(train_instances.get(5)));
	}

}
