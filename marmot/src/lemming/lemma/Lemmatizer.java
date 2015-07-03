// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package lemming.lemma;

import java.io.Serializable;

public interface Lemmatizer extends Serializable {

	public String lemmatize(LemmaInstance instance);

	public boolean isOOV(LemmaInstance instance);

}
