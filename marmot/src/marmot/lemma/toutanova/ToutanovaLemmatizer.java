// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.lemma.toutanova;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import marmot.lemma.LemmaInstance;
import marmot.lemma.LemmaCandidateSet;
import marmot.lemma.Lemmatizer;
import marmot.lemma.LemmatizerGenerator;
import marmot.lemma.toutanova.ToutanovaTrainer.ToutanovaOptions;

public class ToutanovaLemmatizer implements Lemmatizer, LemmatizerGenerator {

	private static final long serialVersionUID = 1L;
	private ToutanovaModel model_;
	private transient Decoder decoder_;
	private transient NbestDecoder nbest_decoder_;
	private ToutanovaOptions options_;
	
	private transient Map<LemmaInstance, String> cache_;

	public ToutanovaLemmatizer(ToutanovaOptions options, ToutanovaModel model) {
		model_ = model;
		options_ = options;
	}

	@Override
	public String lemmatize(LemmaInstance instance) {
		if (cache_ == null) {
			cache_ = new HashMap<>();
		}
		
		String lemma = cache_.get(instance);
		if (lemma != null)
			return lemma;
		
		if (decoder_ == null) {
			decoder_ = options_.getDecoderInstance();
			decoder_.init(model_);
		}
		
		ToutanovaInstance tinstance = getToutanovaInstance(instance);
		lemma = decoder_.decode(tinstance).getOutput();
		
		if (lemma == null || lemma.isEmpty()) {
			lemma = "_";
		}
		
		cache_.put(instance, lemma);
		return lemma;
	}

	private ToutanovaInstance getToutanovaInstance(LemmaInstance instance) {
		ToutanovaInstance tinstance = new ToutanovaInstance(instance, null);
		model_.addIndexes(tinstance, false);
		return tinstance;
	}

	public ToutanovaModel getModel() {
		return model_;
	}

	@Override
	public void addCandidates(LemmaInstance instance, LemmaCandidateSet set) {
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

	@Override
	public boolean isOOV(LemmaInstance instance) {
		return model_.isOOV(instance);
	}

}
