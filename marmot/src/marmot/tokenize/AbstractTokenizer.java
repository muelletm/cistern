// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.tokenize;

import marmot.util.FileUtils;

public abstract class AbstractTokenizer implements Tokenizer {

	private static final long serialVersionUID = 1L;

	@Override
	public void saveToFile(String path) {
		FileUtils.saveToFile(this, path);
	}
	
}
