package marmot.lemma;

import java.util.List;

public class BackupLemmatizerTrainer implements LemmatizerTrainer {

	private LemmatizerTrainer trainer_;
	private LemmatizerTrainer backup_trainer_;

	public BackupLemmatizerTrainer(LemmatizerTrainer trainer, LemmatizerTrainer backup_trainer) {
		trainer_ = trainer;
		backup_trainer_ = backup_trainer;
	}

	@Override
	public Lemmatizer train(List<Instance> instances,
			List<Instance> dev_instances) {
		
		Lemmatizer lemmatizer = trainer_.train(instances, dev_instances);
		Lemmatizer backup = backup_trainer_.train(instances, dev_instances);
		
		return new BackupLemmatizer(lemmatizer, backup);
	}
	
}
