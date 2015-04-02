package marmot.test.lemma;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import marmot.lemma.Aligner;
import marmot.lemma.SimpleAligner;
import marmot.lemma.Aligner.Pair;

import org.junit.Test;

public class SimpleAlignerTest {

	@Test
	public void test() {
		
		Aligner aligner = new SimpleAligner();
		
		testAligner(aligner, "read", "read", Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1));
		testAligner(aligner, "reads", "read", Arrays.asList(1, 1, 1, 1, 1, 1, 2, 1));
		testAligner(aligner, "^reading$", "^read$", Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4, 1));
	}
	
	@Test
	public void testToPairs() {
		
		testToPairs("read", "read",  Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1), Arrays.asList("r", "r", "e", "e", "a", "a", "d", "d") );
		testToPairs("reads", "read",  Arrays.asList(1, 1, 1, 1, 1, 1, 2, 1), Arrays.asList("r", "r", "e", "e", "a", "a", "ds", "d") );
		testToPairs("^sänge$", "^singen$", Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2), Arrays.asList("^", "^", "s", "s", "ä", "i", "n", "n", "g", "g", "e", "e", "$", "n$"));
	}

	protected void testToPairs(String input, String output,
			List<Integer> indexes, List<String> expected_strings) {

		List<Pair> expected = new LinkedList<>();
		Iterator<String> iterator = expected_strings.iterator();
		while (iterator.hasNext()) {
			
			String input_segment = iterator.next();
			String output_segment = iterator.next();
			
			Pair pair = new Pair(input_segment, output_segment);
			expected.add(pair);
		}
		
		assertEquals(expected, Pair.toPairs(input, output, indexes));
	}

	protected void testAligner(Aligner aligner, String input, String output, List<Integer> expected) {
		List<Integer> actual = aligner.align(input, output);
		
		assertEquals(expected, actual);
	}

}
