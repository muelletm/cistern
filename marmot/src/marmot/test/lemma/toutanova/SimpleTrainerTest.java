// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.test.lemma.toutanova;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import junit.framework.AssertionFailedError;
import marmot.lemma.Instance;
import marmot.lemma.Lemmatizer;
import marmot.lemma.LemmatizerTrainer;
import marmot.lemma.Result;
import marmot.lemma.SimpleLemmatizerTrainer;
import marmot.lemma.SimpleLemmatizerTrainer.SimpleLemmatizerTrainerOptions;
import marmot.morph.io.SentenceReader;
import marmot.util.Copy;
import marmot.util.Numerics;

import org.junit.Test;

public class SimpleTrainerTest {

	@Test
	public void moderateTest() {
		runModerateTest(new SimpleLemmatizerTrainer(), 98.41, 64.47);
	}
	
	@Test
	public void moderateUnseenTest() {
		SimpleLemmatizerTrainer trainer = new SimpleLemmatizerTrainer();
		trainer.getOptions().setOption(SimpleLemmatizerTrainerOptions.HANDLE_UNSEEN, true);
		trainer.getOptions().setOption(SimpleLemmatizerTrainerOptions.USE_POS, false);
		runModerateTest(trainer, 99.48, 86.63);
	}
	
	@Test
	public void moderateUnseenPosTest() {
		SimpleLemmatizerTrainer trainer = new SimpleLemmatizerTrainer();
		trainer.getOptions().setOption(SimpleLemmatizerTrainerOptions.HANDLE_UNSEEN, true);
		trainer.getOptions().setOption(SimpleLemmatizerTrainerOptions.USE_POS, true);
		runModerateTest(trainer, 99.96, 86.84);
	}
	
	protected String getResourceFile(String name) {
		return String.format("res:///%s/%s", "marmot/test/lemma", name);
	}
	
	protected List<Instance> getCopyInstances(List<Instance> instances) {
		List<Instance> new_instances = new LinkedList<>();
		for (Instance instance : instances) {
			if (instance.getForm().equals(instance.getLemma())) {
					new_instances.add(instance);
			}
		}
		return new_instances;
	}
	
	protected void runSmallTest(LemmatizerTrainer trainer, double train_acc, double test_acc) {
		runSmallTest(trainer, train_acc, test_acc, false);
	}
	
	protected void runSmallTest(LemmatizerTrainer trainer, double train_acc, double test_acc, boolean add_morph_indexes) {
		runTest(trainer, train_acc, test_acc, "trn_sml.tsv", add_morph_indexes);
	}
	
	protected void runModerateTest(LemmatizerTrainer trainer, double train_acc, double test_acc) {
		runModerateTest(trainer, train_acc, test_acc, false);
	}
	
	protected void runModerateTest(LemmatizerTrainer trainer, double train_acc, double test_acc, boolean add_morph_indexes) {
		runTest(trainer, train_acc, test_acc, "trn_mod.tsv", add_morph_indexes);
	}
	
	private final static String pos_indexes = "form-index=4,lemma-index=5,tag-index=2,";
	private final static String morph_indexes = "form-index=4,lemma-index=5,tag-index=2,morph-index=3,";
	
	protected void runTest(LemmatizerTrainer trainer, double train_acc, double test_acc, String trainfile_name) {
		runTest(trainer, train_acc, test_acc, trainfile_name, false);
	}
	
	protected void runTest(LemmatizerTrainer trainer, double train_acc, double test_acc, String trainfile_name, boolean add_morph_indexes) {	
		
		String indexes = pos_indexes;
		if (add_morph_indexes) {
			indexes = morph_indexes;
		}
		
		
		String trainfile = indexes+ getResourceFile(trainfile_name);
		
		
		List<Instance> training_instances = Instance.getInstances(new SentenceReader(trainfile));
		Lemmatizer lemmatizer = trainer.train(training_instances, null);
			
		assertAccuracy(lemmatizer, training_instances, train_acc);
		
		String testfile = indexes + getResourceFile("dev.tsv");
		List<Instance> instances = Instance.getInstances(new SentenceReader(testfile));
		assertAccuracy(lemmatizer, instances, test_acc);
		
		testfile = indexes + getResourceFile("dev.tsv.morfette");
		instances = Instance.getInstances(new SentenceReader(testfile));
		assertAccuracy(lemmatizer, instances, 1.);
	}
	
	protected void testIfLemmatizerIsSerializable(LemmatizerTrainer trainer) {
		String trainfile = pos_indexes + getResourceFile("trn_sml.tsv");
		Copy.clone(trainer.train(Instance.getInstances(trainfile), null));
	}

	protected void assertAccuracy(Lemmatizer lemmatizer, Collection<Instance> instances, double min_accuracy) {
		Result result = Result.test(lemmatizer, instances);
		
		double accuracy = result.getTokenAccuracy();
		
		result.logAccuracy();
		//result.logErrors(50);
		
		if (!Numerics.approximatelyGreaterEqual(accuracy, min_accuracy)) {
			throw new AssertionFailedError(String.format("%g > %g", accuracy, min_accuracy));
		}		
	}
	
}
