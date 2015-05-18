package marmot.test.lemma.edit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import marmot.lemma.Instance;
import marmot.lemma.LemmaCandidateSet;
import marmot.lemma.edit.EditTreeGenerator;
import marmot.lemma.edit.EditTreeGeneratorTrainer;
import marmot.lemma.edit.EditTreeGeneratorTrainer.EditTreeGeneratorTrainerOptions;
import marmot.lemma.ranker.Ranker;
import marmot.lemma.ranker.RankerTrainer;

import org.junit.Test;

public class EditTreeGeneratorTest {

	@Test
	public void test() {
		
		EditTreeGeneratorTrainer trainer = new EditTreeGeneratorTrainer();
		trainer.getOptions().setOption(EditTreeGeneratorTrainerOptions.TAG_DEPENDENT, true);
		trainer.getOptions().setOption(EditTreeGeneratorTrainerOptions.MIN_COUNT, 0);
		trainer.getOptions().setOption(EditTreeGeneratorTrainerOptions.NUM_STEPS, 1);
		
		List<Instance> instances = new LinkedList<>();
		
		instances.add(new Instance("abc", "ab", null, "C"));
		instances.add(new Instance("dec", "de", null, "C"));
		instances.add(new Instance("ccc", "cc", null, "C"));
		instances.add(new Instance("abc", "abc", null, "B"));
		instances.add(new Instance("dec", "dec", null, "B"));
		instances.add(new Instance("dec", "decc", null, null));
				
		EditTreeGenerator gen = (EditTreeGenerator) trainer.train(instances, null);
		
		LemmaCandidateSet set = null;
		
		
		set = new LemmaCandidateSet();
		gen.addCandidates(new Instance("fgc", null, null, "C"), set);
		assertEquals(1, set.size()); 
		assertTrue(set.contains("fg"));
		
		set = new LemmaCandidateSet();
		gen.addCandidates(new Instance("fgc", null, null, "B"), set);
		assertEquals(1, set.size()); 
		assertTrue(set.contains("fgc"));
		
		set = new LemmaCandidateSet();
		gen.addCandidates(new Instance("fgc", null, null, null), set);
		assertEquals(3, set.size()); 
		assertTrue(set.contains("fgc"));
		assertTrue(set.contains("fg"));
		assertTrue(set.contains("fgcc"));
	}
	
}