package marmot.test.util.edit;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import marmot.lemma.Instance;
import marmot.lemma.toutanova.Aligner.Pair;
import marmot.lemma.toutanova.EditTreeAligner;
import marmot.morph.io.SentenceReader;
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
