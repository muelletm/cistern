package marmot.lemma.toutanova;

import marmot.lemma.Instance;
import marmot.lemma.Lemmatizer;

public class ToutanovaLemmatizer implements Lemmatizer {

	private Model model_;
	private Decoder decoder_;

	public ToutanovaLemmatizer(Model model) {
		model_ = model;
		decoder_ = new Decoder(model);
	}

	@Override
	public String lemmatize(Instance instance) {
		ToutanovaInstance tinstance = new ToutanovaInstance(instance, null);
		model_.addIndexes(tinstance, true);
		return decoder_.decode(tinstance).getOutput();
	}

}
