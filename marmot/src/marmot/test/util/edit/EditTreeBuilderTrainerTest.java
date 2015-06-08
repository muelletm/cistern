// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.test.util.edit;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import marmot.lemma.LemmaInstance;
import marmot.lemma.toutanova.Aligner.Pair;
import marmot.lemma.toutanova.EditTreeAligner;
import marmot.morph.io.SentenceReader;
import marmot.util.Numerics;
import marmot.util.edit.EditTree;
import marmot.util.edit.EditTreeBuilder;
import marmot.util.edit.EditTreeBuilderTrainer;

import org.junit.Test;

public class EditTreeBuilderTrainerTest {

	@Test
	public void test() {
		String indexes = "form-index=4,lemma-index=5,tag-index=2,";
		String trainfile = indexes+ getResourceFile("trn_mod.tsv");
		
		List<LemmaInstance> instances = LemmaInstance.getInstances(new SentenceReader(trainfile));
		
		EditTreeBuilderTrainer trainer = new EditTreeBuilderTrainer(new Random(42), 1, -1);
		EditTreeBuilder builder =  trainer.train(instances);
		
		EditTreeAligner aligner = new EditTreeAligner(builder, true);
		
		testAligner(aligner, "umgezogen", "umziehen", Arrays.asList("u", "m", "ge", "z", "og", "e", "n"), Arrays.asList("u", "m", "", "z", "ieh", "e", "n"));
		testAligner(aligner, "gebissen", "beißen", Arrays.asList("ge", "b", "i", "ss", "e", "n" ), Arrays.asList("", "be", "i", "ß", "e", "n" ));
		testAligner(aligner, "gebogen", "biegen", Arrays.asList("ge", "b", "o", "g", "e", "n"), Arrays.asList("", "b", "ie", "g", "e", "n"));
	}
	
	@Test
	public void testApply() {
		String indexes = "form-index=4,lemma-index=5,tag-index=2,";
		String trainfile = indexes+ getResourceFile("trn_mod.tsv");
		
		List<LemmaInstance> instances = LemmaInstance.getInstances(new SentenceReader(trainfile));
		
		EditTreeBuilderTrainer trainer = new EditTreeBuilderTrainer(new Random(42), 1, -1);
		EditTreeBuilder builder =  trainer.train(instances);
	
		testHashAndEquals(builder, "loves", "love", "hates", "hate", true);
		testHashAndEquals(builder, "lachen", "gelacht", "machen", "gemacht", true);
		testHashAndEquals(builder, "lachen", "gelacht", "aaaaaaaaen", "geaaaaaaaat", true);
		
		Map<EditTree, List<LemmaInstance>> map = new HashMap<>();
		
		for (LemmaInstance instance : instances) {
			
			String input = instance.getForm();
			String output = instance.getLemma();
			
			EditTree tree = builder.build(input, output);

			String p_output = tree.apply(input, 0, input.length());
			assertEquals(output, p_output);
			
			List<LemmaInstance> list = map.get(tree);
			if (list == null) {
				list = new LinkedList<>();
				map.put(tree, list);
			}
			
			list.add(instance);
		}

		applyTest(map, instances, false, 0.0);
		applyTest(map, LemmaInstance.getInstances(indexes + getResourceFile("dev.tsv")), false, 0.02526);
	}
	
	private void applyTest(Map<EditTree, List<LemmaInstance>> map,
			List<LemmaInstance> instances, boolean log_missed_outputs, double expected_miss_rate) {
		
		Logger logger = Logger.getLogger(getClass().getName());
		
		int missed_outputs = 0;
				
		for (LemmaInstance instance : instances) {
			
			String input = instance.getForm();
			String output = instance.getLemma();
			
			Set<String> outputs = new HashSet<>();
			
			for (EditTree tree : map.keySet()) {
				
				String poutput = tree.apply(input, 0, input.length());
				
				if (poutput != null) {
					outputs.add(poutput);
				}
				
			}
			
			if (!outputs.contains(output)) {
				missed_outputs ++;
				if (log_missed_outputs)
					logger.info(String.format("Missed: %s", instance));
			}
			
			assertTrue(outputs.contains(input));
		}
		
		double missed_rate = missed_outputs * 1.0 / instances.size();
		logger.info(Double.toString(missed_rate));
		assertTrue(Numerics.approximatelyLesserEqual(missed_rate, expected_miss_rate));
	}

	private void testHashAndEquals(EditTreeBuilder builder, String input_a,
			String output_a, String input_b, String output_b, boolean result) {
		
		EditTree tree_a = builder.build(input_a, output_a);
		EditTree tree_b = builder.build(input_b, output_b);
		
		assertEquals(result, tree_a.equals(tree_b));
		assertEquals(result, tree_a.hashCode() == tree_b.hashCode());
		
	}

	public void testAligner(EditTreeAligner aligner, String input,
			String output, List<String> input_segments, List<String> output_segments) {
		
		List<Integer> indexes = aligner.align(input, output);
		List<Pair> pairs = Pair.toPairs(input, output, indexes);
		
		List<String> real_input_segments = new LinkedList<>();
		List<String> real_output_segments = new LinkedList<>();
		
		for (Pair pair : pairs) {
			real_input_segments.add(pair.getInputSegment());
			real_output_segments.add(pair.getOutputSegment());
		}
		
		assertEquals(input_segments, real_input_segments);
		assertEquals(output_segments, real_output_segments);
	}

	protected String getResourceFile(String name) {
		return String.format("res:///%s/%s", "marmot/test/lemma", name);
	}

}
