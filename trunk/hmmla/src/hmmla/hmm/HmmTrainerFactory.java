// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.hmm;

import hmmla.Properties;

import java.security.InvalidParameterException;



public class HmmTrainerFactory {

	public static HmmTrainer getTrainer(Properties props) {
		String trainer_name = props.getHmmTrainer().toLowerCase();
		double delta_t = 1e-4;
		double delta_e = 1e-8;
		
		if (trainer_name.equalsIgnoreCase("simplehmmtrainer")) {
			return new SimpleHmmTrainer(delta_t, delta_e);
		} else if (trainer_name.equalsIgnoreCase("signaturehmmtrainer")) {
			return new SignatureHmmTrainer(delta_t, delta_e);
		} else {
			throw new InvalidParameterException("Unknown trainer name: "
					+ trainer_name);
		}
	}
}
