package marmot.lemma.toutanova;

import marmot.lemma.Instance;
import marmot.lemma.Lemmatizer;
import marmot.lemma.toutanova.ToutanovaTrainer.Options;

public class ToutanovaLemmatizer implements Lemmatizer {

	private Model model_;
	private transient Decoder decoder_;
	private Options options_;

	public ToutanovaLemmatizer(Options options, Model model) {
		model_ = model;
	}

	@Override
	public String lemmatize(Instance instance) {
		if (decoder_ == null) {
			decoder_ = options_.getDecoderInstance();
			decoder_.init(model_);
		}
		
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

	public Model getModel() {
		return model_;
	}

}
