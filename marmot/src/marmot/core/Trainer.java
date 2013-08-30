// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core;

import java.util.List;

public interface Trainer {

	void train(Tagger tagger, List<Sequence> sequences,
			Evaluator evaluator);

	void setOptions(Options options);

}
