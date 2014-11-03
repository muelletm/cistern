package marmot.test;

import static org.junit.Assert.*;
import marmot.tokenize.openlp.OpenNlpScript;

import org.junit.Test;

public class OpenNlpScriptTest {

	
	
	
	@Test
	public void simpleTest() {
		String tok, untok, actual, expected;
		
		tok = "really simple .";
		untok = "really simple.";
		expected = "really simple<SPLIT>.";		
		actual = OpenNlpScript.transform(tok, untok);
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void doublePunctTest() { 
		String tok, untok, actual, expected;
		
		tok = "is n't it easy ? ?";
		untok = "isn't it easy??";
		expected = "is<SPLIT>n't it easy<SPLIT>?<SPLIT>?";
		actual = OpenNlpScript.transform(tok, untok);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void sillyExceptionTest() { 
		String tok, untok, actual, expected;
		
		
		tok = "„ „ Kinderschar “ “";
		untok = "\"\"Kinderschar\"\"";
		expected = "\"<SPLIT>\"<SPLIT>Kinderschar<SPLIT>\"<SPLIT>\"";
		actual = OpenNlpScript.transform(tok, untok);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void reallySillyExceptionTest() { 
		String tok, untok, actual, expected;
		
		
		tok = "-LRB- -LRB- -LRB- -LRB- -RRB- -RRB- -RRB- -RRB-";
		untok = "(((())))";
		expected = "(<SPLIT>(<SPLIT>(<SPLIT>(<SPLIT>)<SPLIT>)<SPLIT>)<SPLIT>)";
		actual = OpenNlpScript.transform(tok, untok);
		
		assertEquals(expected, actual);
	}

}
