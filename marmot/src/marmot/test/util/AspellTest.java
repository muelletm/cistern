package marmot.test.util;

import java.util.Arrays;

import marmot.util.Aspell;
import marmot.util.AspellLexicon;
import marmot.util.Lexicon;
import marmot.util.StringUtils.Mode;
import marmot.util.StringUtils.Shape;

import static org.junit.Assert.*;

import org.junit.Test;

public class AspellTest {

	@Test
	public void test() {
		Aspell aspell = new Aspell("/home/thomas/Desktop/cistern/marmot/cmd/marmot_aspell", "en", "utf-8");
		assertTrue(aspell.isCorrect("home"));
		assertFalse(aspell.isCorrect("hme"));
		aspell.shutdown();		
	}
	
	@Test
	public void lexiconTest() {
		AspellLexicon aspell = new AspellLexicon(Mode.lower, "/home/thomas/Desktop/cistern/marmot/cmd/marmot_aspell", "de");
		
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

}
