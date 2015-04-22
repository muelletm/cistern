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
		
		trainer.getOptions().setOption(RerankerTrainerOptions.USE_PERCEPTRON, false).setOption(RerankerTrainerOptions.QUADRATIC_PENALTY, 1.0);
		//trainer.getOptions().setOption(RerankerTrainerOptions.UNIGRAM_FILE, "min-count=5,/mounts/data/proj/marmot/lemmatizer/data/de/unigrams.txt");
		//trainer.getOptions().setOption(RerankerTrainerOptions.UNIGRAM_FILE, Arrays.asList("min-count=1,/mounts/data/proj/marmot/lemmatizer/data/de/aspell.txt", "min-count=5,/mounts/data/proj/marmot/lemmatizer/data/de/unigrams.txt"));
		trainer.getOptions().setOption(RerankerTrainerOptions.USE_SHAPE_LEXICON, true);
		
		trainer.getOptions().setOption(RerankerTrainerOptions.ASPELL_PATH, "/home/thomas/Desktop/cistern/marmot/cmd/marmot_aspell");
		trainer.getOptions().setOption(RerankerTrainerOptions.ASPELL_LANG, "de");
		
		trainer.getOptions().setOption(Options.USE_MORPH, false);
		
		runModerateTest(trainer, 1., 1., true);
		
//		Apr 21, 2015 6:17:25 PM marmot.lemma.Result logAccuracy
//		INFORMATION:   9501 /   9504 = 99,9684 (Type:  3809 /  3812 = 99,9213)
//		Apr 21, 2015 6:17:32 PM marmot.lemma.Result logAccuracy
//		INFORMATION:  73917 /  76704 = 96,3666 (Type: 18517 / 20389 = 90,8186)
//		Apr 21, 2015 6:17:39 PM marmot.lemma.Result logAccuracy
//		INFORMATION:  73192 /  76704 = 95,4214 (Type: 14354 / 16640 = 86,2620)
		
//		INFORMATION:   9501 /   9504 = 99,9684 (Type:  3809 /  3812 = 99,9213)
//		Apr 21, 2015 6:30:49 PM marmot.lemma.Result logAccuracy
//		INFORMATION:  73899 /  76704 = 96,3431 (Type: 18504 / 20389 = 90,7548)
//		Apr 21, 2015 6:30:57 PM marmot.lemma.Result logAccuracy
//		INFORMATION:  73193 /  76704 = 95,4227 (Type: 14355 / 16640 = 86,2680)
		
//		INFORMATION:   9501 /   9504 = 99,9684 (Type:  3809 /  3812 = 99,9213)
//		Apr 21, 2015 6:46:05 PM marmot.lemma.Result logAccuracy
//		INFORMATION:  74038 /  76704 = 96,5243 (Type: 18577 / 20389 = 91,1129)
//		Apr 21, 2015 6:46:11 PM marmot.lemma.Result logAccuracy
//		INFORMATION:  73361 /  76704 = 95,6417 (Type: 14423 / 16640 = 86,6767)
		
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
