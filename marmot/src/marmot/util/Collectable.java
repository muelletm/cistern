// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

import java.io.Serializable;

public interface Collectable extends Serializable {

	public void add(Collectable t);
	public int sum();
	public Collectable copy();

}
