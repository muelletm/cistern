// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package lemming.lemma.cmd;

import lemming.lemma.LemmaResult;
import lemming.lemma.LemmatizerGenerator;
import marmot.util.FileUtils;

public class OracleTester {

	public static void main(String[] args) {		
		String model_file = args[0];
		String test_file = args[1];
		LemmatizerGenerator lemmatizer = FileUtils.loadFromFile(model_file);
		LemmaResult.testGenerator(lemmatizer, test_file).logAccuracy();
	}

}
