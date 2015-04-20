// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.test.lemma.toutanova;

import java.util.Arrays;

import marmot.lemma.toutanova.Aligner;
import marmot.lemma.toutanova.HackyAligner;

import org.junit.Test;

public class HackyAlignerTest extends SimpleAlignerTest {

	@Test
	public void test() {
		
		Aligner aligner = new HackyAligner();
		
		testAligner(aligner, "read", "read", Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1));
		testAligner(aligner, "reads", "read", Arrays.asList(1, 1, 1, 1, 1, 1, 2, 1));
		testAligner(aligner, "^reading$", "^read$", Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4, 1));
		testAligner(aligner, "^gibt$", "^geben$", Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1));
		testAligner(aligner, "^gesungen$", "^singen$", Arrays.asList(3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1));
		testAligner(aligner, "^sänge$", "^singen$", Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2));
	}
	
}
