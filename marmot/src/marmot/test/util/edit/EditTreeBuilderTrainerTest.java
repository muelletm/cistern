package marmot.test.util.edit;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import marmot.lemma.Instance;
import marmot.lemma.toutanova.Aligner.Pair;
import marmot.lemma.toutanova.EditTreeAligner;
import marmot.morph.io.SentenceReader;
import marmot.util.edit.EditTree;
import marmot.util.edit.EditTreeBuilder;
import marmot.util.edit.EditTreeBuilderTrainer;

import org.junit.Test;

public class EditTreeBuilderTrainerTest {

	@Test
	public void test() {
		String indexes = "form-index=4,lemma-index=5,tag-index=2,";
		String trainfile = indexes+ getResourceFile("trn_mod.tsv");
		
		List<Instance> instances = Instance.getInstances(new SentenceReader(trainfile));
		
		EditTreeBuilderTrainer trainer = new EditTreeBuilderTrainer(42);
		EditTreeBuilder builder =  trainer.train(instances);
		
		EditTreeAligner aligner = new EditTreeAligner(builder);
		
		testAligner(aligner, "umgezogen", "umziehen", Arrays.asList("u", "m", "ge", "z", "og", "e", "n"), Arrays.asList("u", "m", "", "z", "ieh", "e", "n"));
		testAligner(aligner, "gebissen", "beißen", Arrays.asList("ge", "b", "i", "ss", "e", "n" ), Arrays.asList("", "be", "i", "ß", "e", "n" ));
		testAligner(aligner, "gebogen", "biegen", Arrays.asList("ge", "b", "o", "g", "e", "n"), Arrays.asList("", "b", "ie", "g", "e", "n"));
	}
	
	@Test
	public void testApply() {
		String indexes = "form-index=4,lemma-index=5,tag-index=2,";
		String trainfile = indexes+ getResourceFile("trn_mod.tsv");
		
		List<Instance> instances = Instance.getInstances(new SentenceReader(trainfile));
		
		EditTreeBuilderTrainer trainer = new EditTreeBuilderTrainer(42);
		EditTreeBuilder builder =  trainer.train(instances);
	
		testHashAndEquals(builder, "loves", "love", "hates", "hate", true);
		testHashAndEquals(builder, "lachen", "gelacht", "machen", "gemacht", true);
		testHashAndEquals(builder, "lachen", "gelacht", "aaaaaaaaen", "geaaaaaaaat", true);
		
		Map<EditTree, List<Instance>> map = new HashMap<>();
		
		for (Instance instance : instances) {
			
			String input = instance.getForm();
			String output = instance.getLemma();
			
			EditTree tree = builder.build(input, output);

			tree.prepareHashing();
			String p_output = tree.apply(input, 0, input.length());
			assertEquals(output, p_output);
			
			List<Instance> list = map.get(tree);
			if (list == null) {
				list = new LinkedList<>();
				map.put(tree, list);
			}
			
			list.add(instance);
		}

		System.err.println(map.size());
		
		for (Map.Entry<EditTree, List<Instance>> entry : map.entrySet()) {
			
			System.err.println(entry.getKey() + " " + entry.getValue());
			
		}
		
		applyTest(map, instances, false);
		applyTest(map, Instance.getInstances(indexes + getResourceFile("dev.tsv")), true);
		
	}
	
	private void applyTest(Map<EditTree, List<Instance>> map,
			List<Instance> instances, boolean log_missed_outputs) {
		
		Logger logger = Logger.getLogger(getClass().getName());
		
		int num_trees = 0;
		int num_outputs = 0;
		int missed_outputs = 0;
				
		for (Instance instance : instances) {
			
			String input = instance.getForm();
			String output = instance.getLemma();
			
			Set<String> outputs = new HashSet<>();
			
			for (EditTree tree : map.keySet()) {
				
				String poutput = tree.apply(input, 0, input.length());
				
				if (poutput != null) {
					num_trees++;
					
								
					outputs.add(poutput);
				}
				
			}
			
			if (!outputs.contains(output)) {
				missed_outputs ++;
				logger.info(String.format("Missed: %s", instance));
			}
			
			assertTrue(outputs.contains(input));
			
			num_outputs += outputs.size();	
		}
		
		logger.info("trees: " + num_trees + " " + instances.size() + " " + num_trees * 1.0 / instances.size());
		logger.info("outputs " + num_outputs + " " + instances.size() + " " + num_outputs * 1.0 / instances.size());
		logger.info("coverage " + missed_outputs + " " + instances.size() + " " + missed_outputs * 1.0 / instances.size());
	}

	private void testHashAndEquals(EditTreeBuilder builder, String input_a,
			String output_a, String input_b, String output_b, boolean result) {
		
		EditTree tree_a = builder.build(input_a, output_a);
		tree_a.prepareHashing();
		EditTree tree_b = builder.build(input_b, output_b);
		tree_b.prepareHashing();
		
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
