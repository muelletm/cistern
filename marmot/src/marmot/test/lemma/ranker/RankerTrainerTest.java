// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.test.lemma.ranker;

import java.util.List;

import marmot.lemma.LemmaInstance;
import marmot.lemma.Lemmatizer;
import marmot.lemma.LemmaOptions;
import marmot.lemma.LemmaResult;
import marmot.lemma.ranker.RankerTrainer;
import marmot.lemma.ranker.RankerTrainer.RankerTrainerOptions;
import marmot.test.lemma.toutanova.SimpleTrainerTest;
import marmot.test.morph.PipelineTest;

import org.junit.Test;

public class RankerTrainerTest extends SimpleTrainerTest {

	@Test
	public void isSerializable() {
		testIfLemmatizerIsSerializable(new RankerTrainer());
	}
	

	@Test
	public void smallTest() {
		RankerTrainer trainer = new RankerTrainer();
		
		trainer.getOptions().setOption(RankerTrainerOptions.USE_PERCEPTRON, false).setOption(RankerTrainerOptions.QUADRATIC_PENALTY, 1.0);
		//trainer.getOptions().setOption(RerankerTrainerOptions.UNIGRAM_FILE, "min-count=5,/mounts/data/proj/marmot/lemmatizer/data/de/unigrams.txt");
		//trainer.getOptions().setOption(RerankerTrainerOptions.UNIGRAM_FILE, Arrays.asList("min-count=1,/mounts/data/proj/marmot/lemmatizer/data/de/aspell.txt", "min-count=5,/mounts/data/proj/marmot/lemmatizer/data/de/unigrams.txt"));
		//trainer.getOptions().setOption(RerankerTrainerOptions.UNIGRAM_FILE, Arrays.asList("min-count=1,/mounts/data/proj/marmot/lemmatizer/data/de/aspell.txt"));
		trainer.getOptions().setOption(RankerTrainerOptions.USE_SHAPE_LEXICON, true);
		trainer.getOptions().setOption(RankerTrainerOptions.NUM_EDIT_TREE_STEPS, 0);
		
//		trainer.getOptions().setOption(RerankerTrainerOptions.UNIGRAM_FILE, Arrays.asList("min-count=5,/mounts/data/proj/marmot/lemmatizer/data/de/unigrams.txt"));
//		trainer.getOptions().setOption(RerankerTrainerOptions.ASPELL_PATH, Aspell.ASPELL_PATH);
//		trainer.getOptions().setOption(RerankerTrainerOptions.ASPELL_LANG, "de");
		
//		INFORMATION:   9501 /   9504 = 99,9684 (OOV:      0 /      0 = NaN)
//				Apr 22, 2015 3:39:51 PM marmot.lemma.Result logAccuracy
//				INFORMATION:  74304 /  76704 = 96,8711 (OOV:  24266 /  26505 = 91,5525)
//				Apr 22, 2015 3:40:02 PM marmot.lemma.Result logAccuracy
//				INFORMATION:  73640 /  76704 = 96,0054 (OOV:  23714 /  26505 = 89,4699)
		
		trainer.getOptions().setOption(LemmaOptions.USE_MORPH, false);
		trainer.getOptions().setOption(RankerTrainerOptions.USE_HASH_FEATURE_TABLE, true);
		
//		INFORMATION:   9501 /   9504 = 99,9684 (Type:  3809 /  3812 = 99,9213)
//		Apr 22, 2015 10:25:52 AM marmot.lemma.Result logAccuracy
//		INFORMATION:  74272 /  76704 = 96,8294 (Type: 18713 / 20389 = 91,7799)
//		Apr 22, 2015 10:25:59 AM marmot.lemma.Result logAccuracy
//		INFORMATION:  73591 /  76704 = 95,9415 (Type: 14530 / 16640 = 87,3197)
		
//		INFORMATION:   9501 /   9504 = 99,9684 (Type:  3809 /  3812 = 99,9213)
//		Apr 22, 2015 9:46:37 AM marmot.lemma.Result logAccuracy
//		INFORMATION:  74105 /  76704 = 96,6116 (Type: 18580 / 20389 = 91,1276)
//		Apr 22, 2015 9:46:42 AM marmot.lemma.Result logAccuracy
//		NFORMATION:  73375 /  76704 = 95,6599 (Type: 14385 / 16640 = 86,4483)
		
// 		ASPELL (explicit), Wiki
//		Apr 22, 2015 9:44:05 AM marmot.lemma.Result logAccuracy
//		INFORMATION:   9501 /   9504 = 99,9684 (Type:  3809 /  3812 = 99,9213)
//		Apr 22, 2015 9:44:13 AM marmot.lemma.Result logAccuracy
//		INFORMATION:  74325 /  76704 = 96,8985 (Type: 18737 / 20389 = 91,8976)
//		Apr 22, 2015 9:44:19 AM marmot.lemma.Result logAccuracy
//		INFORMATION:  73640 /  76704 = 96,0054 (Type: 14551 / 16640 = 87,4459)		
		
// 		ASPELL
//		Apr 22, 2015 9:35:46 AM marmot.lemma.Result logAccuracy
//		INFORMATION:   9501 /   9504 = 99,9684 (Type:  3809 /  3812 = 99,9213)
//		Apr 22, 2015 9:36:09 AM marmot.lemma.Result logAccuracy
//		INFORMATION:  74053 /  76704 = 96,5439 (Type: 18548 / 20389 = 90,9706)
//		Apr 22, 2015 9:36:15 AM marmot.lemma.Result logAccuracy
//		INFORMATION:  73336 /  76704 = 95,6091 (Type: 14367 / 16640 = 86,3401)

		
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
		
		
		
		
		List<LemmaInstance> instances = LemmaInstance.getInstances(PipelineTest.getSentences(trainfile, 100));
		
		System.err.println(instances.size());
		
		RankerTrainer trainer = new RankerTrainer();
		trainer.getOptions().setOption(RankerTrainerOptions.USE_PERCEPTRON, false).setOption(RankerTrainerOptions.QUADRATIC_PENALTY, 0.0);
		trainer.getOptions().setOption(LemmaOptions.USE_MORPH, false);
		trainer.getOptions().setOption(LemmaOptions.USE_POS, false);
		
		Lemmatizer lemmatizer = trainer.train(instances, null);
	
		instances = LemmaInstance.getInstances(PipelineTest.getSentences(devfile, 100));
		LemmaResult result = LemmaResult.test(lemmatizer, instances);
		
		result.logAccuracy();
	}
	
	@Test
	public void smallMarmotTest() {
		RankerTrainer trainer = new RankerTrainer();
		
		String trainfile = "form-index=1,lemma-index=2,tag-index=4,morph-index=6,res:///marmot/test/morph/trn.txt";
		String devfile = "form-index=1,lemma-index=2,tag-index=4,morph-index=6,res:///marmot/test/morph/tst.txt";
		
		trainer.getOptions().setOption(RankerTrainerOptions.USE_PERCEPTRON, false).setOption(RankerTrainerOptions.QUADRATIC_PENALTY, 0.0);
		trainer.getOptions().setOption(LemmaOptions.USE_MORPH, true);
		
		List<LemmaInstance> instances = LemmaInstance.getInstances(PipelineTest.getSentences(trainfile, 100));
		
		Lemmatizer lemmatizer = trainer.train(instances, null);
	
		instances = LemmaInstance.getInstances(PipelineTest.getSentences(devfile, 100));
		LemmaResult result = LemmaResult.test(lemmatizer, instances);
		
		result.logAccuracy();
		
		assert result.getTokenAccuracy() > 97.94;
	}

}
