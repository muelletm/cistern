// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.mapper;

import java.util.LinkedList;
import java.util.List;

public class Node {

	public String getForm() {
		return form_;
	}

	public String getLemma() {
		return lemma_;
	}

	public String getDeprel() {
		return deprel_;
	}

	public String getPos() {
		return pos_;
	}

	public String getFeats() {
		return feats_;
	}

	private String form_;
	private String lemma_;
	private String pos_;
	private String feats_;
	private String deprel_;
	private int head_;
	private SyntaxTree tree_;
	private MorphTag tag_;

	public Node(String form, String lemma, String pos, String feats,
			int head, String deprel, SyntaxTree tree) {
		form_ = form;
		lemma_ = lemma;
		pos_ = pos;
		feats_ = feats;
		deprel_ = deprel;
		head_ = head;
		tree_ = tree;
	}

	public Node getHead() {
		if (head_ == 0) {
			return null;
		}
		return tree_.getNodes().get(head_ - 1);
	}
	
	public List<Node> getChildren() {
		List<Node> nodes = new LinkedList<>();
		
		for (Node node : tree_.getNodes()) {
			if (node.getHead() == this) {
				nodes.add(node);
			}
		}
		
		return nodes;
	}

	public void setMorphTag(MorphTag tag) {
		tag_ = tag;
	}
	
	public MorphTag getMorphTag() {
		return tag_;
	}

	public int getHeadIndex() {
		return head_;
	}
	
	@Override
	public String toString() {
		return form_;
	}

	public void setForm(String form) {
		form_ = form;
	}

	public void setLemma(String lemma) {
		lemma_ = lemma;
	}

	public void setDeprel(String deprel) {
		deprel_ = deprel;
	}
}
