// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.tokenize.preprocess;

public interface InternalReader {

	void mark();

	void reset();

	String readLine();

}
