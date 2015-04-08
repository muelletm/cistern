package marmot.lemma;

public class BackupLemmatizer implements Lemmatizer {

	private Lemmatizer lemmatizer_;
	private Lemmatizer backup_;

	public BackupLemmatizer(Lemmatizer lemmatizer, Lemmatizer backup) {
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

}
