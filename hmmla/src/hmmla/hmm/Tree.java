// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.hmm;

import java.io.Serializable;
import java.util.List;

public class Tree implements Serializable {
	private static final long serialVersionUID = 1364049301082188023L;
	private String name_;
	private Tree left_;
	private Tree right_;
	private Tree parent_;
	private int level_;

	public Tree(String name, int level) {
		this.name_ = name;
		level_ = level;
	}

	public void setParent(Tree parent) {
		this.parent_ = parent;
	}

	public Tree setLeft(String name) {
		left_ = new Tree(name, level_ + 1);
		left_.setParent(this);
		return left_;
	}

	public Tree setRight(String name) {
		right_ = new Tree(name, level_ + 1);
		right_.setParent(this);
		return right_;
	}

	public boolean isLeaf() {
		if (left_ == null && right_ == null) {
			return true;
		}
		return false;
	}

	public void getLeaves(List<Tree> leaves) {

		if (isLeaf()) {
			leaves.add(this);
		} else {
			assert left_ != null && right_ != null;
			left_.getLeaves(leaves);
			right_.getLeaves(leaves);
		}

	}

	public void getTreesOverLeaves(List<Tree> descendants) {

		if (!isLeaf()) {

			assert left_ != null && right_ != null;

			if (left_.isLeaf() && right_.isLeaf()) {
				descendants.add(this);
			} else {
				left_.getTreesOverLeaves(descendants);
				right_.getTreesOverLeaves(descendants);
			}
		}

	}

	public String getName() {
		return name_;
	}

	public String toString() {
		return String.format("(%s %d %s %s)", getName(), level_,
				(left_ != null) ? left_.toString() : "_",
				(right_ != null) ? right_.toString() : "_");
	}

	public Tree getRight() {
		return right_;
	}

	public Tree getLeft() {
		return left_;
	}

	public Tree getParent() {
		return parent_;
	}

	public void prune() {
		if (!isLeaf()) {
			assert right_ != null && left_ != null;
			right_.parent_ = null;
			left_.parent_ = null;
			left_ = null;
			right_ = null;
			level_ += 1;
		}
	}

	public Tree getRoot() {

		if (parent_ == null) {
			return this;
		} else {
			return parent_.getRoot();
		}

	}

	public void incrementLevel() {
		level_ += 1;
	}

	public int getLevel() {
		return level_;
	}

	public void getChildrenWithLevel(List<Tree> trees, int level) {
		if (isLeaf() || level_ >= level) {
			trees.add(this);
		} else {
			left_.getChildrenWithLevel(trees, level);
			right_.getChildrenWithLevel(trees, level);
		}
	}

}
