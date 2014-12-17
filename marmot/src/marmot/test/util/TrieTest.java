// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.test.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import marmot.util.CollectableDouble;
import marmot.util.CollectableSet;
import marmot.util.StringUtils;
import marmot.util.Trie;

import org.junit.Test;

public class TrieTest {

	@Test
	public void testSimpleDoubleInsertion() {	
		Trie<CollectableDouble> trie = new Trie<CollectableDouble>();
		trie.addWord("a", new CollectableDouble(10.));
		assertFalse(trie.isTerminal());
		
		Map<Character, Trie<CollectableDouble>> children = trie.getChildren();
		assertNotSame(null, children);
		assertEquals(1, children.size());
		
		Trie<CollectableDouble> child = children.get('a');
		assertNotSame(null, child);
		assertEquals(10., child.getContent().getValue(), 0.001);
		assertTrue(child.isTerminal());
		
		trie.addWord("a", new CollectableDouble(20.));		
		assertEquals(30., child.getContent().getValue(), 0.001);
		
		trie.addWord("ab", new CollectableDouble(20.));		
		assertEquals(30., child.getContent().getValue(), 0.001);
	}
	
	@Test
	public void testSimpleWord() {
		Trie<CollectableDouble> trie = new Trie<CollectableDouble>();
		trie.addWord("ab", new CollectableDouble(10.));
		
		assertFalse(trie.isTerminal());
		
		Map<Character, Trie<CollectableDouble>> children = trie.getChildren();
		assertNotSame(null, children);
		assertEquals(1, children.size());
		
		Trie<CollectableDouble> child = children.get('a');
		assertNotSame(null, child);
		assertEquals(null, child.getContent());
		assertFalse(child.isTerminal());
		
		children = child.getChildren();
		assertNotSame(null, children);
		assertEquals(1, children.size());
		
		child = children.get('b');
		assertNotSame(null, child);
		assertEquals(10., child.getContent().getValue(), 0.001);
		assertTrue(child.isTerminal());

		
	}
	
	@Test
	public void toyTestPropagation() {
		Trie<CollectableSet> trie, child;
		Map<Character, Trie<CollectableSet>> children;
		
		trie = new Trie<CollectableSet>();
		trie.addWord("abc", new CollectableSet("A"));		
		trie.propagateContent(100);
		
		children = trie.getChildren();
		assertTrue(null == children);
		assertEquals(Collections.singleton("A"), trie.getContent().getValue());
				
		trie = new Trie<CollectableSet>();	
		
		trie.addWord("abc", new CollectableSet("A"));
		trie.addWord("a", new CollectableSet("B"));
		trie.propagateContent(100);

		children = trie.getChildren();
		assertTrue(null != children);
		assertEquals(1, children.size());
		child = children.get('a');
		assertTrue(null != child);
		assertTrue(null == child.getContent());
		Set<String> expected_set = new HashSet<String>();
		expected_set.add("A");
		expected_set.add("B");
		assertEquals(expected_set, trie.getContent().getValue());
		children = child.getChildren();
		assertTrue(null != children);
		assertEquals(1, children.size());
		child = children.get('b');
		children = child.getChildren();
		assertTrue(null == children);
		assertEquals(Collections.singleton("A"), child.getContent().getValue());
	}

	public String reverse(String s) {
		return StringUtils.reverse(s);
	}
	
	@Test
	public void testPropagation() {
		Trie<CollectableSet> trie = new Trie<CollectableSet>();
		Set<String> expected_set = new HashSet<String>();
		CollectableSet cset;
		
		trie.addWord(reverse("booking"), new CollectableSet("V"));
		trie.addWord(reverse("booking"), new CollectableSet("N"));
		trie.addWord(reverse("sleeping"), new CollectableSet("V"));
		trie.addWord(reverse("reads"), new CollectableSet("V"));
		trie.addWord(reverse("knows"), new CollectableSet("V"));
		
		trie.propagateContent(100);
		
		cset = trie.getContent(reverse("ing"));
		assertTrue(null != cset);
		expected_set.add("V");
		expected_set.add("N");
		assertEquals(expected_set, cset.getValue());
		
		cset = trie.getContent(reverse("ping"));
		assertTrue(null != cset);
		expected_set.clear();
		expected_set.add("V");
		assertEquals(expected_set, cset.getValue());
		
		cset = trie.getContent(reverse("s"));
		assertTrue(null != cset);
		expected_set.clear();
		expected_set.add("V");
		assertEquals(expected_set, cset.getValue());
		
		cset = trie.getContent(reverse("viking"));
		assertTrue(null != cset);
		expected_set.clear();
		expected_set.add("V");
		expected_set.add("N");
		assertEquals(expected_set, cset.getValue());
		
		cset = trie.getContent(reverse("weeping"));
		assertTrue(null != cset);
		expected_set.clear();
		expected_set.add("V");
		assertEquals(expected_set, cset.getValue());
		
		cset = trie.getContent(reverse("geese"));
		assertTrue(null != cset);
		expected_set.clear();
		expected_set.add("V");
		expected_set.add("N");
		assertEquals(expected_set, cset.getValue());
	}
	
}
