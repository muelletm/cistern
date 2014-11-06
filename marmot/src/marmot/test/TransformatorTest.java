package marmot.test;

import static org.junit.Assert.*;

import marmot.tokenize.openlp.Transformator;

import org.junit.Test;

public class TransformatorTest {

	@Test
	public void quoteTest() {
		String tok, untok, actual, expected;
		
		tok = "album `` Violent by Design '' bla";
		untok = "album \"Violent by Design\" bla";
		expected = "album \"<SPLIT>Violent by Design<SPLIT>\" bla";		
		actual = Transformator.transform(tok, untok);
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void simpleTest() {
		String tok, untok, actual, expected;
		
		tok = "really simple .";
		untok = "really simple.";
		expected = "really simple<SPLIT>.";		
		actual = Transformator.transform(tok, untok);
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void doublePunctTest() { 
		String tok, untok, actual, expected;
		
		tok = "is n't it easy ? ?";
		untok = "isn't it easy??";
		expected = "is<SPLIT>n't it easy<SPLIT>?<SPLIT>?";
		actual = Transformator.transform(tok, untok);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void sillyExceptionTest() { 
		String tok, untok, actual, expected;
			
		tok = "„ „ Kinderschar “ “";
		untok = "\"\"Kinderschar\"\"";
		expected = "\"<SPLIT>\"<SPLIT>Kinderschar<SPLIT>\"<SPLIT>\"";
		actual = Transformator.transform(tok, untok);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void reallySillyExceptionTest() { 
		String tok, untok, actual, expected;
		
		
		tok = "-LRB- -LRB- -LRB- -LRB- -RRB- -RRB- -RRB- -RRB-";
		untok = "(((())))";
		expected = "(<SPLIT>(<SPLIT>(<SPLIT>(<SPLIT>)<SPLIT>)<SPLIT>)<SPLIT>)";
		actual = Transformator.transform(tok, untok);
		
		assertEquals(expected, actual);
	}

	@Test
	public void reallySillyExceptionTest2() { 
		String tok, untok, actual, expected;
		
		tok = "-LRB- a -LRB- a -LRB- a -LRB- a -RRB- a -RRB- a -RRB- a -RRB-";
		untok = "(a(a(a(a)a)a)a)";
		expected = "(<SPLIT>a<SPLIT>(<SPLIT>a<SPLIT>(<SPLIT>a<SPLIT>(<SPLIT>a<SPLIT>)<SPLIT>a<SPLIT>)<SPLIT>a<SPLIT>)<SPLIT>a<SPLIT>)";
		actual = Transformator.transform(tok, untok);
		
		assertEquals(expected, actual);
	}

	
	@Test
	public void repetionTest() { 
		String tok, untok, actual, expected;
		
		tok = "bla -RRB- Robert";
		untok = "bla(Robert";
		expected = "bla<SPLIT>(<SPLIT>Robert";
		actual = Transformator.transform(tok, untok);
		
		assertEquals(expected, actual);
	}

	@Test
	public void entityTest() { 
		String tok, untok, actual, expected;
		
		tok = "Nabucodonosor_II";
		untok = "Nabucodonosor II";
		expected = "Nabucodonosor II";
		actual = Transformator.transform(tok, untok);
		
		assertEquals(expected, actual);
	}
	
}
