package marmot.lemma.toutanova;

import java.util.List;

public interface NbestDecoder {

	public List<Result> decode(ToutanovaInstance instance);
	public void init(ToutanovaModel model);

}
