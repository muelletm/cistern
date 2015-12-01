package lemming.test.lemma.edit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import lemming.lemma.LemmaCandidateSet;
import lemming.lemma.LemmaInstance;
import lemming.lemma.edit.EditTreeGenerator;
import lemming.lemma.edit.EditTreeGeneratorTrainer;
import lemming.lemma.edit.EditTreeGeneratorTrainer.EditTreeGeneratorTrainerOptions;

import org.junit.Test;

public class EditTreeGeneratorTest {

	@Test
	public void test() {
		
		EditTreeGeneratorTrainer trainer = new EditTreeGeneratorTrainer();
		trainer.getOptions().setOption(EditTreeGeneratorTrainerOptions.TAG_DEPENDENT, true);
		trainer.getOptions().setOption(EditTreeGeneratorTrainerOptions.MIN_COUNT, 0);
		trainer.getOptions().setOption(EditTreeGeneratorTrainerOptions.NUM_STEPS, 1);
		
		List<LemmaInstance> instances = new LinkedList<>();
		instances.add(new LemmaInstance("abc", "ab", null, "C"));
		instances.add(new LemmaInstance("dec", "de", null, "C"));
		instances.add(new LemmaInstance("ccc", "cc", null, "C"));
		instances.add(new LemmaInstance("abc", "abc", null, "B"));
		instances.add(new LemmaInstance("dec", "dec", null, "B"));
		instances.add(new LemmaInstance("dec", "decc", null, null));
				
		EditTreeGenerator gen = (EditTreeGenerator) trainer.train(instances, null);
		
		LemmaCandidateSet set = null;
		
		set = new LemmaCandidateSet();
		gen.addCandidates(new LemmaInstance("fgc", null, null, "C"), set);
		assertEquals(1, set.size()); 
		assertTrue(set.contains("fg"));
		
		set = new LemmaCandidateSet();
		gen.addCandidates(new LemmaInstance("fgc", null, null, "B"), set);
		assertEquals(1, set.size()); 
		assertTrue(set.contains("fgc"));
		
		set = new LemmaCandidateSet();
		gen.addCandidates(new LemmaInstance("fgc", null, null, null), set);
		assertEquals(3, set.size()); 
		assertTrue(set.contains("fgc"));
		assertTrue(set.contains("fg"));
		assertTrue(set.contains("fgcc"));
	}
	
}