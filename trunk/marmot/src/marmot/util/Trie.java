// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Trie<E extends Collectable> implements Serializable {

	private static final long serialVersionUID = 1L;
	private E content_;
	private Map<Character, Trie<E>> children_;

	public Trie() {
		children_ = null;
		content_ = null;
	}

	public Map<Character, Trie<E>> getChildren() {
		return children_;
	}

	public Trie<E> addWord(String word, E content) {
		return addWord(word, 0, content);
	}

	private Trie<E> addWord(String word, int index, E content) {
		if (index >= word.length()) {
			add(content);
			return this;
		}

		char c = word.charAt(index);

		if (children_ == null) {
			children_ = new HashMap<Character, Trie<E>>();
		}

		Trie<E> trie = children_.get(c);
		if (trie == null) {
			trie = new Trie<E>();
			children_.put(c, trie);
		}

		return trie.addWord(word, index + 1, content);
	}

	public Trie<E> getChild(char c) {
		if (children_ == null) {
			return null;
		}

		return children_.get(c);
	}

	public boolean isTerminal() {
		return content_ != null;
	}

	public E getContent() {
		return content_;
	}

	@Override
	public String toString() {
		return "Trie: " + content_ + " " + children_;
	}

	@SuppressWarnings("unchecked")
	public void add(E content) {
		if (content != null) {
			if (content_ == null) {
				content_ = (E) content.copy();
			} else {
				content_.add(content);
			}
		}
	}

	public void propagateContent(int limit) {
		if (children_ != null) {
			for (Trie<E> child : children_.values()) {
				child.propagateContent(limit);
				add(child.content_);
			}

			if (content_ != null && (content_).sum() > limit) {
				content_ = null;
			}

			Iterator<Trie<E>> iterator = children_.values().iterator();

			while (iterator.hasNext()) {
				Trie<E> child = iterator.next();

				if (content_ != null) {
					if (content_.equals(child.content_)) {
						child.content_ = null;
					}
				}

				if (child.content_ == null && child.children_ == null) {
					iterator.remove();
				}
			}

			if (children_.isEmpty()) {
				children_ = null;
			}

		}
	}

	public E getContent(String word) {
		return getContent(word, 0, content_);
	}

	public E getContent(String word, int index, E content) {
		if (index >= word.length()) {
			return content;
		}

		Trie<E> child = getChild(word.charAt(index));
		if (child == null) {
			return content;
		}

		if (child.content_ != null) {
			content = child.content_;
		}

		return child.getContent(word, index + 1, content);
	}

}
