// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.mapper;

import java.util.Iterator;
import java.util.List;

import marmot.util.LineIterator;

public class SyntaxTreeIterator implements Iterator<SyntaxTree> {

	int form_index_;
	int lemma_index_;
	int pos_index_;
	int feat_index_;
	int head_index_;
	int deprel_index_;
	LineIterator iterator_;
	boolean lowercase_;

	public SyntaxTreeIterator(String filename, int form_index,
			int lemma_index, int pos_index, int feat_index, int head_index,
			int deprel_index, boolean lowercase) {
		form_index_ = form_index;
		lemma_index_ = lemma_index;
		pos_index_ = pos_index;
		feat_index_ = feat_index;
		head_index_ = head_index;
		deprel_index_ = deprel_index;
		iterator_ = new LineIterator(filename);
		lowercase_ = lowercase;
	}

	@Override
	public boolean hasNext() {
		return iterator_.hasNext();
	}

	@Override
	public SyntaxTree next() {
		SyntaxTree tree = new SyntaxTree();
		List<String> line = iterator_.next();
		while (!line.isEmpty()) {
			
			String form = line.get(form_index_);
			if (lowercase_) {
				form = form.toLowerCase();
			}
			
			String lemma = line.get(lemma_index_);
			if (lowercase_) {
				lemma = lemma.toLowerCase();
			}

			
			Node node = new Node(form,
					  			 lemma,
					  			 line.get(pos_index_),
					             line.get(feat_index_), Integer.parseInt(line
							.get(head_index_)), line.get(deprel_index_),
					tree);

			tree.addNode(node);
			line = iterator_.next();
		}
		return tree;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
