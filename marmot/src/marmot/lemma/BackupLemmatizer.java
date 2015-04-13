package marmot.lemma;

public class BackupLemmatizer implements Lemmatizer, LemmatizerGenerator {

	private LemmatizerGenerator lemmatizer_;
	private LemmatizerGenerator backup_;

	public BackupLemmatizer(LemmatizerGenerator lemmatizer, LemmatizerGenerator backup) {
		lemmatizer_ = lemmatizer;
		backup_ = backup;
	}
	
	@Override
	public String lemmatize(Instance instance) {
		String lemma = lemmatizer_.lemmatize(instance);
		if (lemma != null) {
			return lemma;
		}
		
		return backup_.lemmatize(instance);
	}

	@Override
	public void addCandidates(Instance instance, LemmaCandidateSet set) {
		lemmatizer_.addCandidates(instance, set);
		backup_.addCandidates(instance, set);
	}

}
