package marmot.lemma.toutanova;

import marmot.lemma.Instance;
import marmot.lemma.Lemmatizer;

public class ToutanovaLemmatizer implements Lemmatizer {

	private Model model_;
	private Decoder decoder_;

	public ToutanovaLemmatizer(ToutanovaTrainer.Options options, Model model) {
		model_ = model;
		
		decoder_ = options.getDecoderInstance();
		decoder_.init(model_);
	}

	@Override
	public String lemmatize(Instance instance) {
		Lemmatizer lemmatizer = model_.getSimpleLemmatizer();
		if (lemmatizer != null) {
			String lemma = lemmatizer.lemmatize(instance);
			if (lemma != null) {
				return lemma;
			}
		}
		
		ToutanovaInstance tinstance = new ToutanovaInstance(instance, null);
		model_.addIndexes(tinstance, false);
		return decoder_.decode(tinstance).getOutput();
	}

}
