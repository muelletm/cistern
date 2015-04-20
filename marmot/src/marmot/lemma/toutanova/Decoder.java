// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.lemma.toutanova;

public interface Decoder {

	public Result decode(ToutanovaInstance instance);

	public void init(ToutanovaModel model_);

	public int getOrder();

}
