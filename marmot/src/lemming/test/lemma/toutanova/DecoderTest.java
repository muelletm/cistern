// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package lemming.test.lemma.toutanova;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import lemming.lemma.LemmaInstance;
import lemming.lemma.toutanova.Decoder;
import lemming.lemma.toutanova.FirstOrderDecoder;
import lemming.lemma.toutanova.Result;
import lemming.lemma.toutanova.ToutanovaInstance;
import lemming.lemma.toutanova.ToutanovaModel;
import lemming.lemma.toutanova.ToutanovaTrainer;

import org.junit.Test;

public class DecoderTest {

	@Test
	public void test() {
		
		ToutanovaModel model = new ToutanovaModel();
		
		List<ToutanovaInstance> train_instances = new LinkedList<>();
		
		train_instances.add(new ToutanovaInstance(new LemmaInstance("aaae", "aaa", null, null), Arrays.asList(1, 1, 1, 1, 2, 1)));
		train_instances.add(new ToutanovaInstance(new LemmaInstance("bbbe", "bbb", null, null), Arrays.asList(1, 1, 1, 1, 2, 1)));
		train_instances.add(new ToutanovaInstance(new LemmaInstance("ccce", "ccc", null, null), Arrays.asList(1, 1, 1, 1, 2, 1)));
		train_instances.add(new ToutanovaInstance(new LemmaInstance("aaaf", "aaa", null, null), Arrays.asList(1, 1, 1, 1, 2, 1)));
		train_instances.add(new ToutanovaInstance(new LemmaInstance("bbbf", "bbb", null, null), Arrays.asList(1, 1, 1, 1, 2, 1)));
		train_instances.add(new ToutanovaInstance(new LemmaInstance("cccf", "ccc", null, null), Arrays.asList(1, 1, 1, 1, 2, 1)));
		
		model.init(ToutanovaTrainer.ToutanovaOptions.newInstance(), train_instances, null);
		
		Decoder decoder = new FirstOrderDecoder();
		decoder.init(model);
		
		int a_index = model.getOutputTable().toIndex("a");
		int b_index = model.getOutputTable().toIndex("b");
		int c_index = model.getOutputTable().toIndex("c");
		
//		model.setWeight(model.getTransitionFeatureIndex(a_index, a_index), 1.0);
//		model.setWeight(model.getTransitionFeatureIndex(b_index, b_index), 1.0);
//		model.setWeight(model.getTransitionFeatureIndex(c_index, c_index), 1.0);
//		model.setWeight(model.getPairFeatureIndex(train_instances.get(0).getFormCharIndexes(), 2, 4, a_index), 5.0);
//		model.setWeight(model.getPairFeatureIndex(train_instances.get(1).getFormCharIndexes(), 2, 4, b_index), 5.0);
//		model.setWeight(model.getPairFeatureIndex(train_instances.get(2).getFormCharIndexes(), 2, 4, c_index), 5.0);
//		model.setWeight(model.getPairFeatureIndex(train_instances.get(3).getFormCharIndexes(), 2, 4, a_index), 5.0);
//		model.setWeight(model.getPairFeatureIndex(train_instances.get(4).getFormCharIndexes(), 2, 4, b_index), 5.0);
//		model.setWeight(model.getPairFeatureIndex(train_instances.get(5).getFormCharIndexes(), 2, 4, c_index), 5.0);
//		model.setWeight(model.getPairFeatureIndex(train_instances.get(0).getFormCharIndexes(), 0, 1, a_index), 1.0);
//		model.setWeight(model.getPairFeatureIndex(train_instances.get(1).getFormCharIndexes(), 0, 1, b_index), 1.0);
//		model.setWeight(model.getPairFeatureIndex(train_instances.get(2).getFormCharIndexes(), 0, 1, c_index), 1.0);
//		
		assertResultEquals(Arrays.asList(a_index, a_index, a_index), Arrays.asList(1, 2, 4), decoder.decode(train_instances.get(0)));
		assertResultEquals(Arrays.asList(a_index, a_index, a_index), Arrays.asList(1, 2, 4), decoder.decode(train_instances.get(3)));
		assertResultEquals(Arrays.asList(b_index, b_index, b_index), Arrays.asList(1, 2, 4), decoder.decode(train_instances.get(1)));
		assertResultEquals(Arrays.asList(b_index, b_index, b_index), Arrays.asList(1, 2, 4), decoder.decode(train_instances.get(4)));
		assertResultEquals(Arrays.asList(c_index, c_index, c_index), Arrays.asList(1, 2, 4), decoder.decode(train_instances.get(2)));
		assertResultEquals(Arrays.asList(c_index, c_index, c_index), Arrays.asList(1, 2, 4), decoder.decode(train_instances.get(5)));
		
//		double test_score = model_.getScore(instance, result);
//		assert Math.abs(result.getScore() - test_score) < 1e-5;
		
		
	}

	private void assertResultEquals(List<Integer> outputs, List<Integer> inputs,
			Result result) {
		assertEquals(outputs, result.getOutputs());
		assertEquals(inputs, result.getInputs());
		
	}

}
