package marmot.test.util;

import static org.junit.Assert.*;

import java.util.Map;

import marmot.util.Copy;
import marmot.util.HashLexicon;
import marmot.util.StringUtils.Mode;

import org.junit.Test;

public class HashLexiconTest {

	@Test
	public void testCostumSerialization() {
		
		HashLexicon lexicon = new HashLexicon(Mode.lower);
		
		lexicon.addEntry("Test", 4);
		lexicon.addEntry("test", 3);
		lexicon.addEntry("teSt", 2);
		
		lexicon.addEntry("Fest", 9);
		lexicon.addEntry("fest", 7);
		
		lexicon.addEntry("Rest", 6);
		lexicon.addEntry("reSt",10);
		lexicon.addEntry("REST",11);
		
		HashLexicon copy = Copy.clone(lexicon);
		
		Map<String, int[]> real_map = lexicon.getMap();
		Map<String, int[]> copy_map = copy.getMap();
		
		assertEquals(real_map.size(), copy_map.size());
		for (Map.Entry<String, int[]> entry : real_map.entrySet()) {
			int[] real_counts = entry.getValue();
			int[] copy_counts = copy_map.get(entry.getKey());
			
			assertArrayEquals(real_counts, copy_counts);
		}
	}

}
