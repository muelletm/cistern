// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.decode;

import hmmla.io.Sentence;

import java.util.List;

public interface Decoder {
	public List<String> bestPath(Sentence sentence);
}
