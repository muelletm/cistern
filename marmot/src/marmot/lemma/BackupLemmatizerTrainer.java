// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.lemma;

import java.util.List;

import marmot.lemma.toutanova.ToutanovaTrainer;

public class BackupLemmatizerTrainer implements LemmatizerGeneratorTrainer {

	public static class BackupLemmatizerTrainerOptions extends Options {
		
		private static final long serialVersionUID = 1L;
		public static final String LEMMATIZER_TRAINER = "lemmatizer-trainer";
		public static final String BACKUP_TRAINER = "backup-trainer";

		public BackupLemmatizerTrainerOptions() {
			map_.put(LEMMATIZER_TRAINER, SimpleLemmatizerTrainer.class.getName());
			map_.put(BACKUP_TRAINER, ToutanovaTrainer.class.getName());
		}

		public LemmatizerGeneratorTrainer getLemmatizerTrainer() {
			String classname = (String) getOption(LEMMATIZER_TRAINER);
			try {
				return (LemmatizerGeneratorTrainer) Class.forName(classname).newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		
		public LemmatizerGeneratorTrainer getBackupTrainer() {
			String classname = (String) getOption(BACKUP_TRAINER);
			try {
				return (LemmatizerGeneratorTrainer) Class.forName(classname).newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		
	}
	
	BackupLemmatizerTrainerOptions options_;
	private LemmatizerGeneratorTrainer standard_trainer_;
	private ToutanovaTrainer backup_trainer_;
	
	public BackupLemmatizerTrainer() {
		standard_trainer_ = null;
		backup_trainer_ = null;
		options_ = new BackupLemmatizerTrainerOptions();
	}

	public BackupLemmatizerTrainer(LemmatizerGeneratorTrainer simple_trainer,
			ToutanovaTrainer trainer) {
		this();
		standard_trainer_ = simple_trainer;
		backup_trainer_ = trainer;
	}

	@Override
	public LemmatizerGenerator train(List<Instance> instances,
			List<Instance> dev_instances) {
		
		LemmatizerGeneratorTrainer trainer;
		
		if (standard_trainer_ == null)
			trainer = options_.getLemmatizerTrainer();
		else
			trainer = standard_trainer_;
		
		LemmatizerGenerator lemmatizer = trainer.train(instances, dev_instances);
		
		if (backup_trainer_ == null) {
			trainer = options_.getBackupTrainer();
		} else {
			trainer = backup_trainer_;	
		}
		
		LemmatizerGenerator backup = trainer.train(instances, dev_instances);
		
		return new BackupLemmatizer(lemmatizer, backup);
	}

	@Override
	public Options getOptions() {
		return options_;
	}
	
}
