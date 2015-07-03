// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package lemming.lemma.toutanova;

import java.util.List;

public interface NbestDecoder {

	public List<Result> decode(ToutanovaInstance instance);
	public void init(ToutanovaModel model);

}
