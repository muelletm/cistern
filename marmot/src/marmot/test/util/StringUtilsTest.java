// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.test.util;

import static org.junit.Assert.*;
import marmot.util.StringUtils;
import marmot.util.StringUtils.Mode;
import marmot.util.StringUtils.Shape;

import org.junit.Test;

public class StringUtilsTest {

	public void test(String form, Mode mode, String expected) {
		String actual = StringUtils.normalize(form, mode);
		assertEquals(expected, actual);
	}
	
	@Test
	public void identityTest() {
		test("Äb1c-LRB-", Mode.none, "Äb1c-LRB-");
	}
	
	@Test
	public void bracketTest() {
		test("Äb1c-LRB-", Mode.bracket, "Äb1c(");
		test("-RRB-", Mode.bracket, ")");
		test("-RRB-a", Mode.bracket, ")a");
	}
	
	@Test
	public void lowerTest() {
		test("Äb1c-LRB-", Mode.lower, "äb0c(");
	}
	
	@Test
	public void umlautTest() {
		test("Äb1c-LRB-", Mode.umlaut, "aeb0c(");
	}
	
	@Test
	public void shapeTest() {
		testShape("lower", Shape.Lower);
		testShape("Upper", Shape.FirstCap);
		testShape("1Upper", Shape.Mixed);
		testShape("U1AA", Shape.AllCap);
		testShape("123", Shape.NoLetter);
		testShape("", Shape.NoLetter);
		testShape("a", Shape.Lower);
		testShape("A", Shape.AllCap);
		testShape("!", Shape.NoLetter);
	}

	private void testShape(String string, Shape shape) {
		assertEquals(shape, StringUtils.getShape(string));
	}

}
