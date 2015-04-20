// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.lemma.edit;

import java.util.Collection;

import marmot.lemma.Instance;
import marmot.lemma.LemmaCandidateGenerator;
import marmot.lemma.LemmaCandidateSet;
import marmot.util.edit.EditTree;

public class EditTreeGenerator implements LemmaCandidateGenerator {

	private static final long serialVersionUID = 1L;
	private Collection<EditTree> trees_;
	
	public EditTreeGenerator(Collection<EditTree> trees) {
		trees_ = trees;
	}
	
	@Override
	public void addCandidates(Instance instance, LemmaCandidateSet set) {
		String form = instance.getForm();
		
		for (EditTree tree : trees_) {
			String lemma = tree.apply(form, 0, form.length());
			if (lemma != null) {
				set.getCandidate(lemma);
			}
		}
	}
}
