// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.test;

import static org.junit.Assert.*;

import java.util.Arrays;

import marmot.morph.MorphDictionaryOptions;
import marmot.morph.MorphDictionaryOptions.DictionaryType;

import org.junit.Test;

public class MorphDictionaryOptionsTest {

	@Test
	public void simpleFilenameTest() {
		String option_string;
		MorphDictionaryOptions options;
		option_string = "/bla/bla";
		options = MorphDictionaryOptions.parse(option_string);
		assertEquals(options.getFilename(), option_string);
	}
	
	@Test
	public void indexTest() {
		String option_string;
		MorphDictionaryOptions options;
		
		int[] indexes = {1,2,3};
		option_string = "/bla/bla,indexes=[1,2,3]";
		options = MorphDictionaryOptions.parse(option_string);
		assertEquals(options.getFilename(), "/bla/bla");
		assertTrue(Arrays.equals(options.getIndexes(), indexes));
		
		option_string = "indexes=[3,1,2],/bla/bla";
		options = MorphDictionaryOptions.parse(option_string);
		assertEquals(options.getFilename(), "/bla/bla");
		assertTrue(Arrays.equals(options.getIndexes(), indexes));
		
	}
	
	@Test
	public void suffixTest() {
		String option_string;
		MorphDictionaryOptions options;
		
		int[] indexes = {1,2,3};
				
		option_string = "type=suffix,indexes=[3,1,2],/bla/bla";
		options = MorphDictionaryOptions.parse(option_string);
		assertEquals(options.getFilename(), "/bla/bla");
		assertTrue(Arrays.equals(options.getIndexes(), indexes));
		assertEquals(options.getDictType(), DictionaryType.suffix);
		
		option_string = "type=suffix,/bla/bla,indexes=[3,1,2]";
		options = MorphDictionaryOptions.parse(option_string);
		assertEquals(options.getFilename(), "/bla/bla");
		assertTrue(Arrays.equals(options.getIndexes(), indexes));
		assertEquals(options.getDictType(), DictionaryType.suffix);
		
		option_string = "/bla/bla,indexes=[3,1,2],type=suffix";
		options = MorphDictionaryOptions.parse(option_string);
		assertEquals(options.getFilename(), "/bla/bla");
		assertTrue(Arrays.equals(options.getIndexes(), indexes));
		assertEquals(options.getDictType(), DictionaryType.suffix);
		
	}

	@Test
	public void normalizeTest() {
		String option_string;
		MorphDictionaryOptions options;
		
		int[] indexes = {1,2,3};
				
		option_string = "normalize=true,type=suffix,indexes=[3,1,2],/bla/bla";
		options = MorphDictionaryOptions.parse(option_string);
		assertEquals(options.getFilename(), "/bla/bla");
		assertTrue(Arrays.equals(options.getIndexes(), indexes));
		assertTrue(options.getNormalize());
		assertEquals(options.getDictType(), DictionaryType.suffix);
		
		option_string = "type=suffix,normalize=true,/bla/bla,indexes=[3,1,2]";
		options = MorphDictionaryOptions.parse(option_string);
		assertEquals(options.getFilename(), "/bla/bla");
		assertTrue(Arrays.equals(options.getIndexes(), indexes));
		assertTrue(options.getNormalize());
		assertEquals(options.getDictType(), DictionaryType.suffix);
		
		option_string = "/bla/bla,indexes=[3,1,2],normalize=true,type=suffix";
		options = MorphDictionaryOptions.parse(option_string);
		assertEquals(options.getFilename(), "/bla/bla");
		assertTrue(Arrays.equals(options.getIndexes(), indexes));
		assertTrue(options.getNormalize());
		assertEquals(options.getDictType(), DictionaryType.suffix);
		
	}

}
