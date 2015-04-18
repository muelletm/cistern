package marmot.test.util;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import marmot.util.HashableIntArray;

import org.junit.Test;

public class HashableIntArrayTest {

	@Test
	public void test() {
		
		int[] a = {0, 1, 2, 3};
		int[] b = a.clone();
		int[] c = new int[4];
		c[1] = 1;
		c[2] = 2;
		c[3] = 3;
		Set<HashableIntArray> set = new HashSet<>();
		set.add(new HashableIntArray(a));
		set.add(new HashableIntArray(b));
		set.add(new HashableIntArray(c));
		assertEquals(1, set.size());
		
	}

}
