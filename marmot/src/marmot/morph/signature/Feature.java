// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.signature;

import java.io.Serializable;

public abstract class Feature implements Serializable {
	private static final long serialVersionUID = 1L;
	abstract String getName();
	abstract boolean feature(String word);
	
}
