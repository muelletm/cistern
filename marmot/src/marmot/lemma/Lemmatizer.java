// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.lemma;

import java.io.Serializable;

public interface Lemmatizer extends Serializable {

	public String lemmatize(Instance instance);

	public boolean isOOV(Instance instance);

}
