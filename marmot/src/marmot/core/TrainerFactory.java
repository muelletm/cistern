// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core;

public class TrainerFactory {

	public static Trainer create(Options options) {
		
		Trainer trainer;
		try {
			trainer = (Trainer)(Class.forName(options.getTrainer()).newInstance());
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		
		trainer.setOptions(options);
		return trainer;
		
	}
	
}
