// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.test.lemma.reranker;

import java.util.List;

import marmot.lemma.Instance;
import marmot.lemma.Lemmatizer;
import marmot.lemma.Options;
import marmot.lemma.Result;
import marmot.lemma.ranker.RankerTrainer;
import marmot.lemma.ranker.RankerTrainer.RerankerTrainerOptions;
import marmot.test.lemma.toutanova.SimpleTrainerTest;
import marmot.test.morph.PipelineTest;

import org.junit.Test;

public class RerankerTrainerTest extends SimpleTrainerTest {

	@Test
	public void isSerializable() {
		testIfLemmatizerIsSerializable(new RankerTrainer());
	}
	

	@Test
	public void smallTest() {
		RankerTrainer trainer = new RankerTrainer();
		
		//((RerankerTrainerOptions) trainer.getOptions()).setOption(RerankerTrainerOptions.GENERATOR_TRAINERS, Arrays.asList(EditTreeGeneratorTrainer.class, SimpleLemmatizerTrainer.class, ToutanovaTrainer.class));
		
		trainer.getOptions().setOption(RerankerTrainerOptions.USE_PERCEPTRON, false).setOption(RerankerTrainerOptions.QUADRATIC_PENALTY, 1.0);
		//trainer.getOptions().setOption(RerankerTrainerOptions.UNIGRAM_FILE, "min-count=5,/mounts/data/proj/marmot/lemmatizer/data/de/unigrams.txt");
		trainer.getOptions().setOption(Options.USE_MORPH, true);
		
		runModerateTest(trainer, 1., 1., true);
		
		// MORPH 88.09
		// POS   87.40
		
		// MORPH 88.06 78.32
		// POS   87.39 86.91
		
		// 97.11
	}
	
	@Test
	public void smallNoMorphMarmotTest() {
		String trainfile = "form-index=1,lemma-index=2,res:///marmot/test/morph/trn.txt";
		String devfile = "form-index=1,lemma-index=2,res:///marmot/test/morph/tst.txt";
		
		
		
		
		List<Instance> instances = Instance.getInstances(PipelineTest.getSentences(trainfile, 100));
		
		System.err.println(instances.size());
		
		RankerTrainer trainer = new RankerTrainer();
		trainer.getOptions().setOption(RerankerTrainerOptions.USE_PERCEPTRON, false).setOption(RerankerTrainerOptions.QUADRATIC_PENALTY, 0.0);
		trainer.getOptions().setOption(Options.USE_MORPH, false);
		trainer.getOptions().setOption(Options.USE_POS, false);
		
		Lemmatizer lemmatizer = trainer.train(instances, null);
	
		instances = Instance.getInstances(PipelineTest.getSentences(devfile, 100));
		Result result = Result.test(lemmatizer, instances);
		
		result.logAccuracy();
	}
	
	@Test
	public void smallMarmotTest() {
		RankerTrainer trainer = new RankerTrainer();
		
		String trainfile = "form-index=1,lemma-index=2,tag-index=4,morph-index=6,res:///marmot/test/morph/trn.txt";
		String devfile = "form-index=1,lemma-index=2,tag-index=4,morph-index=6,res:///marmot/test/morph/tst.txt";
		
		trainer.getOptions().setOption(RerankerTrainerOptions.USE_PERCEPTRON, false).setOption(RerankerTrainerOptions.QUADRATIC_PENALTY, 0.0);
		trainer.getOptions().setOption(Options.USE_MORPH, true);
		
		List<Instance> instances = Instance.getInstances(PipelineTest.getSentences(trainfile, 100));
		
		Lemmatizer lemmatizer = trainer.train(instances, null);
	
		instances = Instance.getInstances(PipelineTest.getSentences(devfile, 100));
		Result result = Result.test(lemmatizer, instances);
		
		result.logAccuracy();
		
		assert result.getTokenAccuracy() > 97.94;
	}

}
