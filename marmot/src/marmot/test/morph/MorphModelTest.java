package marmot.test.morph;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import marmot.core.Sequence;
import marmot.morph.MorphModel;
import marmot.morph.MorphOptions;
import marmot.morph.Word;
import marmot.util.StringUtils.Mode;

import org.junit.Test;

public class MorphModelTest {

	public void testFormExtraction(String inform, String expected_form,
			Collection<Character> expected_chars, MorphOptions options) {
		MorphModel model = new MorphModel();
		model.init(options, new LinkedList<Sequence>());
		Word word = new Word(inform);
		model.addIndexes(word, true);

		System.err.println(model.getWordTable());
		System.err.println(model.getCharTable());

		assertEquals(0, model.getWordTable().toIndex(expected_form));
		assertEquals(1, model.getWordTable().size());

		Set<Character> expected_char_set = new HashSet<>(expected_chars);
		assertEquals(expected_char_set, model.getCharTable().keySet());
	}

	@Test
	public void testFormExtraction() {
		MorphOptions options;

		options = new MorphOptions();
		testFormExtraction("Abc", "Abc", Arrays.asList('A', 'b', 'c'), options);

		options = new MorphOptions();
		options.setProperty(MorphOptions.FORM_NORMALIZATION,
				Mode.lower.toString());
		testFormExtraction("Abc", "abc", Arrays.asList('a', 'b', 'c'), options);
		testFormExtraction("-LRB-", "(", Arrays.asList('('), options);

		options = new MorphOptions();
		options.setProperty(MorphOptions.FORM_NORMALIZATION,
				Mode.umlaut.toString());
		testFormExtraction("Öl", "oel", Arrays.asList('o', 'e', 'l'), options);
	}

	public void testShapeExtraction(String inform, int expected,
			MorphOptions options) {
		MorphModel model = new MorphModel();
		model.init(options, new LinkedList<Sequence>());
		Word word = new Word(inform);
		model.addIndexes(word, true);
		assertEquals(expected, word.getWordSignature());
	}

	@Test
	public void testShapeExtraction() {
		MorphOptions options;

		options = new MorphOptions();
		options.setProperty(MorphOptions.SPECIAL_SIGNATURE, "false");
		testShapeExtraction("?", 0, options);
		testShapeExtraction("abc", 1, options);
		testShapeExtraction("DEF", 2, options);
		testShapeExtraction("-", 4, options);
		testShapeExtraction("1234", 8, options);
		testShapeExtraction("1234abc", 8 + 1, options);
		testShapeExtraction("1234-abc", 8 + 1 + 4, options);
		testShapeExtraction("1234-DEF", 8 + 2 + 4, options);
		testShapeExtraction("1234-DEF-abc", 8 + 1 + 2 + 4, options);
		
		options = new MorphOptions();
		options.setProperty(MorphOptions.SPECIAL_SIGNATURE, "true");
		testShapeExtraction("abc", 1, options);
		testShapeExtraction("DEF", 2, options);
		testShapeExtraction("-", 4 + 16, options);
		testShapeExtraction("1234", 8, options);
		testShapeExtraction("?", 16, options);
		testShapeExtraction("1234abc", 8 + 1, options);
		testShapeExtraction("1234#abc", 8 + 1 + 16, options);
		testShapeExtraction("1234-abc", 8 + 1 + 4 + 16, options);
		testShapeExtraction("1234-DEF", 8 + 2 + 4 + 16, options);
		testShapeExtraction("1234-DEF-abc", 8 + 1 + 2 + 4 + 16, options);
		
		// Make sure that changing the form doesn't affect signatures.
		options = new MorphOptions();
		options.setProperty(MorphOptions.SPECIAL_SIGNATURE, "true");
		options.setProperty(MorphOptions.FORM_NORMALIZATION, Mode.lower.toString());
		testShapeExtraction("abc", 1, options);
		testShapeExtraction("DEF", 2, options);
		testShapeExtraction("-", 4 + 16, options);
		testShapeExtraction("1234", 8, options);
		testShapeExtraction("?", 16, options);
		testShapeExtraction("1234abc", 8 + 1, options);
		testShapeExtraction("1234#abc", 8 + 1 + 16, options);
		testShapeExtraction("1234-abc", 8 + 1 + 4 + 16, options);
		testShapeExtraction("1234-DEF", 8 + 2 + 4 + 16, options);
		testShapeExtraction("1234-DEF-abc", 8 + 1 + 2 + 4 + 16, options);
	}

}
