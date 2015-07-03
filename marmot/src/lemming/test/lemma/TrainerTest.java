// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package lemming.test.lemma;

import lemming.lemma.cmd.Trainer;
import lemming.lemma.ranker.RankerTrainer;

import org.junit.Test;

public class TrainerTest {

	@Test
	public void test() {
		
		String[] args = {RankerTrainer.class.getName(), "generator-trainers=marmot.lemma.SimpleLemmatizerTrainer;marmot.lemma.edit.EditTreeGeneratorTrainer;marmot.lemma.toutanova.ToutanovaTrainer",  getResourceFile("trn_sml.tsv"), getResourceFile("dev.tsv"), getResourceFile("dev.tsv.morfette")};
		
		Trainer.main(args);
		
	}
	
	protected String getResourceFile(String name) {
		return INDEXES + String.format("res:///%s/%s", "marmot/test/lemma", name);
	}
	
	private final static String INDEXES = "form-index=4,lemma-index=5,tag-index=2,";

}
