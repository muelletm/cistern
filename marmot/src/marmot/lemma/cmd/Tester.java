// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.lemma.cmd;

import marmot.lemma.Lemmatizer;
import marmot.util.FileUtils;

public class Tester {

	public static void main(String[] args) {		
		String model_file = args[0];
		String test_file = args[1];
		Lemmatizer lemmatizer = FileUtils.loadFromFile(model_file);
		Trainer.test(lemmatizer, test_file);
	}

}
