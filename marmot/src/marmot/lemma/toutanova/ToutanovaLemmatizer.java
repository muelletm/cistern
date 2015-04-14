package marmot.lemma.toutanova;

import java.util.Collection;

import marmot.lemma.Instance;
import marmot.lemma.LemmaCandidateSet;
import marmot.lemma.Lemmatizer;
import marmot.lemma.LemmatizerGenerator;
import marmot.lemma.toutanova.ToutanovaTrainer.ToutanovaOptions;

public class ToutanovaLemmatizer implements Lemmatizer, LemmatizerGenerator {

	private Model model_;
	private transient Decoder decoder_;
	private transient NbestDecoder nbest_decoder_;
	private ToutanovaOptions options_;

	public ToutanovaLemmatizer(ToutanovaOptions options, Model model) {
		model_ = model;
		options_ = options;
	}

	@Override
	public String lemmatize(Instance instance) {
		if (decoder_ == null) {
			decoder_ = options_.getDecoderInstance();
			decoder_.init(model_);
		}
		
		ToutanovaInstance tinstance = getToutanovaInstance(instance);
		return decoder_.decode(tinstance).getOutput();
	}

	private ToutanovaInstance getToutanovaInstance(Instance instance) {
		ToutanovaInstance tinstance = new ToutanovaInstance(instance, null);
		model_.addIndexes(tinstance, false);
		return tinstance;
	}

	public Model getModel() {
		return model_;
	}

	@Override
	public void addCandidates(Instance instance, LemmaCandidateSet set) {
		ToutanovaInstance tinstance = getToutanovaInstance(instance);
		
		if (nbest_decoder_ == null) {
			nbest_decoder_ = new ZeroOrderNbestDecoder(options_.getNbestRank());
			nbest_decoder_.init(model_);
		}
		
		Collection<marmot.lemma.toutanova.Result> results = nbest_decoder_.decode(tinstance);
		if (results == null)
			return;
		
		for (marmot.lemma.toutanova.Result result : results) {
			set.getCandidate(result.getOutput());
		}
	}

}
