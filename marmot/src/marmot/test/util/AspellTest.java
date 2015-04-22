package marmot.test.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import marmot.util.Aspell;
import marmot.util.AspellLexicon;
import marmot.util.HashLexicon;
import marmot.util.Lexicon;
import marmot.util.LineIterator;
import marmot.util.StringUtils.Mode;
import marmot.util.StringUtils.Shape;

import org.junit.Test;

public class AspellTest {

	@Test
	public void test() {
		Aspell aspell = new Aspell(Aspell.ASPELL_PATH, "en", "utf-8");
		assertTrue(aspell.isCorrect("home"));
		assertFalse(aspell.isCorrect("hme"));
		aspell.shutdown();		
	}
	
	@Test
	public void lexiconTest() {
		AspellLexicon aspell = new AspellLexicon(Mode.lower, Aspell.ASPELL_PATH, "de");
		
		int[] array = new int[Lexicon.ARRAY_LENGTH];
		
		array[Shape.AllCap.ordinal()] = 1;
		array[Lexicon.ARRAY_LENGTH - 1] = 1;
		assertArrayEquals(aspell.getCount("DDR"), array);
		Arrays.fill(array, 0);
		
		assertArrayEquals(aspell.getCount("123"), null);
		
		array[Shape.FirstCap.ordinal()] = 1;
		array[Shape.Lower.ordinal()] = 1;
		array[Shape.AllCap.ordinal()] = 1;
		array[Lexicon.ARRAY_LENGTH - 1] = 1;
		assertArrayEquals(array, aspell.getCount("Mach"));
		Arrays.fill(array, 0);
		
		array[Shape.NoLetter.ordinal()] = 1;
		array[Lexicon.ARRAY_LENGTH - 1] = 1;
		assertArrayEquals(array, aspell.getCount("."));
		Arrays.fill(array, 0);
	}
	
	@Test
	public void lexiconTest2() {
		String path = "/mounts/data/proj/marmot/lemmatizer/data/de/aspell.txt";
		
		AspellLexicon aspell = new AspellLexicon(Mode.lower, Aspell.ASPELL_PATH, "de");
		HashLexicon lexicon = HashLexicon.readFromFile(path, 1);
		
		LineIterator iterator = new LineIterator(path);
		
		while (iterator.hasNext()) {
			List<String> line = iterator.next();
			
			if (line.size() > 0) {
				
				
				String word = line.get(0);
				
				int[] hash_counts = lexicon.getCount(word);
				int[] aspell_counts = aspell.getCount(word);
				
				if (!Arrays.equals(hash_counts, aspell_counts)) {
					System.err.format("%s %s %s\n", word, Arrays.toString(hash_counts), Arrays.toString(aspell_counts));
				}
				
			}
			
		}
		
		
		
	}

}
