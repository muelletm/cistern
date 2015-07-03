// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package lemming.lemma;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import lemming.lemma.toutanova.ToutanovaTrainer;

public class BackupLemmatizerTrainer implements LemmatizerGeneratorTrainer {

	public static class BackupLemmatizerTrainerOptions extends LemmaOptions {
		
		private static final long serialVersionUID = 1L;
		public static final String LEMMATIZER_TRAINER = "lemmatizer-trainer";
		public static final String BACKUP_TRAINER = "backup-trainer";
		
		public LemmatizerGeneratorTrainer trainer_;
		public LemmatizerGeneratorTrainer backup_trainer_;
		
		public static final String TRAINER_PREF = "backup-lemmatizer-model-";
		public static final String BACKUP_PREF = "backup-lemmatizer-backup-";
		private Map<String, Object> model_options_;
		private Map<String, Object> backup_options_;

		public BackupLemmatizerTrainerOptions() {
			map_.put(LEMMATIZER_TRAINER, SimpleLemmatizerTrainer.class.getName());
			map_.put(BACKUP_TRAINER, ToutanovaTrainer.class.getName());
			
			model_options_ = new HashMap<>();
			backup_options_ = new HashMap<>();
		}

		
		
		@Override 
		public LemmaOptions setOption(String name, Object value) {
			name = name.toLowerCase();
			
			if (name.startsWith(TRAINER_PREF)) {
				model_options_.put(name.substring(TRAINER_PREF.length()), value);
			} else if (name.startsWith(BACKUP_PREF)) {
				backup_options_.put(name.substring(BACKUP_PREF.length()), value);
			} else {
				super.setOption(name, value);
			}
			
			return this;
		}
		
		
		public LemmatizerGeneratorTrainer getLemmatizerTrainer(String name, Map<String, Object> map) {
			String classname = (String) getOption(name);
			LemmatizerGeneratorTrainer trainer;
			try {
				 trainer = (LemmatizerGeneratorTrainer) Class.forName(classname).newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				trainer.getOptions().setOption(entry.getKey(), entry.getValue());
			}
			
			Logger logger = Logger.getLogger(getClass().getName());
			logger.info(String.format("%s (%s) options:\n %s", name, classname, trainer.getOptions().report()));
			return trainer;
		}

		public LemmatizerGeneratorTrainer getLemmatizerTrainer() {
			return getLemmatizerTrainer(LEMMATIZER_TRAINER, model_options_);
		}

		public LemmatizerGeneratorTrainer getBackupTrainer() {
			return getLemmatizerTrainer(BACKUP_TRAINER, backup_options_);
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
	public LemmatizerGenerator train(List<LemmaInstance> instances,
			List<LemmaInstance> dev_instances) {
		
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
	public LemmaOptions getOptions() {
		return options_;
	}
	
}
