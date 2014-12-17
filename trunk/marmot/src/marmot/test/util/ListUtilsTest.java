package marmot.test.util;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import marmot.util.ListUtils;

import org.junit.Test;

public class ListUtilsTest {

	@Test
	public void complementTest() {
		List<List<Integer>> items; 
		List<Integer> expected, actual;
		
		items = new LinkedList<List<Integer>>();
		items.add(Arrays.asList(1, 2));
		items.add(Arrays.asList(3, 4));
		items.add(Arrays.asList(5, 6));
		items.add(Arrays.asList(7, 8));
	
		expected = Arrays.asList(1, 2, 3, 4, 7, 8);
		actual = ListUtils.complement(items, 2);
		assertEquals(expected, actual);
		
		expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
		actual = ListUtils.complement(items, 4);
		
		assertEquals(expected, actual);
		actual = ListUtils.complement(items, -1);
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void chunkTest() {
		List<List<Integer>> expected, actual;
		
		List<Integer> items = Arrays.asList(1, 2, 3, 4, 5, 6 , 7, 8);
		
		expected = new LinkedList<List<Integer>>();
		expected.add(Arrays.asList(1, 2));
		expected.add(Arrays.asList(3, 4));
		expected.add(Arrays.asList(5, 6));
		expected.add(Arrays.asList(7, 8));
		
		actual = ListUtils.chunk(items, 4);
		assertEquals(expected, actual);
		
		expected = new LinkedList<List<Integer>>();
		expected.add(Arrays.asList(1));
		expected.add(Arrays.asList(2));
		expected.add(Arrays.asList(3));
		expected.add(Arrays.asList(4));
		expected.add(Arrays.asList(5, 6, 7, 8));
		actual = ListUtils.chunk(items, 5);
		assertEquals(expected, actual);
		
		expected = new LinkedList<List<Integer>>();
		expected.add(Arrays.asList(1));
		expected.add(Arrays.asList(2));
		expected.add(Arrays.asList(3));
		expected.add(Arrays.asList(4));
		expected.add(Arrays.asList(5));
		expected.add(Arrays.asList(6));
		expected.add(Arrays.asList(7));
		expected.add(Arrays.asList(8));
		actual = ListUtils.chunk(items, 8);
		assertEquals(expected, actual);
		
		expected = new LinkedList<List<Integer>>();
		expected.add(Arrays.asList(1));
		expected.add(Arrays.asList(2));
		expected.add(Arrays.asList(3));
		expected.add(Arrays.asList(4));
		expected.add(Arrays.asList(5));
		expected.add(Arrays.asList(6));
		expected.add(Arrays.asList(7));
		expected.add(Arrays.asList(8));
		actual = ListUtils.chunk(items, 9);
		assertEquals(expected, actual);
		
		expected = Collections.singletonList(items);
		actual = ListUtils.chunk(items, 1);
		assertEquals(expected, actual);
		
	}

}
