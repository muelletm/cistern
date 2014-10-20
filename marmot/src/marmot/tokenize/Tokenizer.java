// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.tokenize;

import java.io.Serializable;
import java.util.List;

public interface Tokenizer extends Serializable {

	public List<String> tokenize(String untokenized);
	public void saveToFile(String path);
	
}
