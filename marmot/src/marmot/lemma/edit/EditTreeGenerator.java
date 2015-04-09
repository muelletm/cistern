package marmot.lemma.edit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import marmot.lemma.Instance;
import marmot.lemma.LemmaCandidateGenerator;
import marmot.util.edit.EditTree;

public class EditTreeGenerator implements LemmaCandidateGenerator {

	private Set<String> set_;
	private Collection<EditTree> trees_;
	
	public EditTreeGenerator(Collection<EditTree> trees) {
		trees_ = trees;
		set_ = new HashSet<>();
	}
	
	@Override
	public List<String> getCandidates(Instance instance) {
		set_.clear();
		
		String form = instance.getForm();
		
		for (EditTree tree : trees_) {
			
			String lemma = tree.apply(form, 0, form.length());
			if (lemma != null) {
				set_.add(lemma);
			}
		}
		
		return new ArrayList<>(set_);
	}
}
