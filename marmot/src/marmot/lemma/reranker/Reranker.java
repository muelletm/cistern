package marmot.lemma.reranker;

import java.util.List;

import marmot.lemma.Instance;
import marmot.lemma.LemmaCandidateGenerator;
import marmot.lemma.LemmaCandidateSet;
import marmot.lemma.LemmatizerGenerator;

public class Reranker implements LemmatizerGenerator {

	private Model model_;
	private List<LemmaCandidateGenerator> generators_;

	public Reranker(Model model, List<LemmaCandidateGenerator> generators) {
		model_ = model;
		generators_ = generators;
	}

	@Override
	public String lemmatize(Instance instance) {
		LemmaCandidateSet set = new LemmaCandidateSet(instance.getForm());
		addCandidates(instance, set);;
		RerankerInstance rinstance = new RerankerInstance(instance, set);
		model_.addIndexes(rinstance, set, false);
		return model_.select(rinstance);
	}

	@Override
	public void addCandidates(Instance instance, LemmaCandidateSet set) {
		for (LemmaCandidateGenerator generator : generators_) {
			generator.addCandidates(instance, set);
		}
	}

}
