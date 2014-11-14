package marmot.test;

import static org.junit.Assert.*;
import marmot.util.StringUtils;
import marmot.util.StringUtils.Mode;

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

}
