package marmot.lemma;

import java.util.List;

public class BackupLemmatizerTrainer implements LemmatizerGeneratorTrainer {

	private LemmatizerGeneratorTrainer trainer_;
	private LemmatizerGeneratorTrainer backup_trainer_;

	public BackupLemmatizerTrainer(LemmatizerGeneratorTrainer trainer, LemmatizerGeneratorTrainer backup_trainer) {
		trainer_ = trainer;
		backup_trainer_ = backup_trainer;
	}

	@Override
	public LemmatizerGenerator train(List<Instance> instances,
			List<Instance> dev_instances) {
		
		LemmatizerGenerator lemmatizer = trainer_.train(instances, dev_instances);
		LemmatizerGenerator backup = backup_trainer_.train(instances, dev_instances);
		
		return new BackupLemmatizer(lemmatizer, backup);
	}
	
}
