package marmot.test.tokenizer.opennlp;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import marmot.tokenize.openlp.Aligner;
import marmot.tokenize.openlp.Aligner.Result;
import marmot.tokenize.openlp.Aligner.ResultType;
import marmot.tokenize.openlp.LevenshteinAligner;
import marmot.tokenize.openlp.Aligner.Pair;

import org.junit.Test;

public class LevenshteinAlignerTest {

	public void bothWayTest(String tok, String untok, List<Pair> expected, long time) {
		Result actual;
		Aligner a = new LevenshteinAligner();
		
		actual = a.align(untok, tok);
		assertEquals(expected, actual.pairs);
		
		// Reverse problem
		List<Pair> expected_reversed = new LinkedList<Pair>();
		for (Pair pair : expected) {
			expected_reversed.add(new Pair(pair.b, pair.a));
		}
		actual = a.align(tok, untok);
		assertEquals(expected_reversed, actual.pairs);
	}
	
	public void bothWayTest(String tok, String untok, List<Pair> expected) {
		bothWayTest(tok, untok, expected, 1000);
	}
	
	@Test
	public void toyExampleAlignTest() {
		String tok, untok;
		List<Pair> expected;
		
		tok = "A -LRB- B";
		untok = "A (B";
		expected = new LinkedList<Pair>();
		expected.add(new Pair(0, 0));
		expected.add(new Pair(1, 1));
		expected.add(new Pair(2, 2));
		expected.add(new Pair(2, 3));
		expected.add(new Pair(2, 4));
		expected.add(new Pair(2, 5));
		expected.add(new Pair(2, 6));
		expected.add(new Pair(-1, 7));
		expected.add(new Pair(3, 8));
		bothWayTest(tok, untok, expected);
		
		tok = "A -- C -- B";
		untok = "A (C) B";
		expected = new LinkedList<Pair>();
		expected.add(new Pair(0, 0));
		expected.add(new Pair(1, 1));
		expected.add(new Pair(2, 2));
		expected.add(new Pair(2, 3));
		expected.add(new Pair(-1, 4));
		expected.add(new Pair(3, 5));
		expected.add(new Pair(-1, 6));
		expected.add(new Pair(4, 7));
		expected.add(new Pair(4, 8));
		expected.add(new Pair(5, 9));
		expected.add(new Pair(6, 10));
		bothWayTest(tok, untok, expected);

		tok = "A L B";
		untok = "A (B";
		expected = new LinkedList<Pair>();
		expected.add(new Pair(0, 0));
		expected.add(new Pair(1, 1));
		expected.add(new Pair(2, 2));
		expected.add(new Pair(-1, 3));
		expected.add(new Pair(3, 4));
		bothWayTest(tok, untok, expected);
	}
	
	@Test
	public void insertProblemTest() {
		String tok, untok;
		List<Pair> expected;

		tok = "A -- -- B";
		untok = "A () B";
		expected = new LinkedList<Pair>();
		expected.add(new Pair(0, 0));
		expected.add(new Pair(1, 1));
		expected.add(new Pair(2, 2));
		expected.add(new Pair(2, 3));
		expected.add(new Pair(-1, 4));
		expected.add(new Pair(3, 5));
		expected.add(new Pair(3, 6));
		expected.add(new Pair(4, 7));
		expected.add(new Pair(5, 8));
		bothWayTest(tok, untok, expected);
	}
	
	public List<Pair> getParanExpected(String tok) {
		List<Pair> expected = new LinkedList<Pair>();
		int untok_index = 0;
		for (int i=0; i<tok.length(); i++) {
			if (i % 3 == 2) {
				expected.add(new Pair(-1, i));
				untok_index += 1;
			} else {
				expected.add(new Pair(untok_index, i));
			}			
		}
		return expected;
	}
	
	@Test
	public void paranTest() {
		String tok, untok;
		List<Pair> expected;
		
		tok = "-- --";
		untok = "()";
		expected = new LinkedList<Pair>();
		expected.add(new Pair(0, 0));
		expected.add(new Pair(0, 1));
		expected.add(new Pair(-1, 2));
		expected.add(new Pair(1, 3));
		expected.add(new Pair(1, 4));
		bothWayTest(tok, untok, expected);
		
		tok = "-- -- -- --";
		untok = "(())";
		expected = getParanExpected(tok);
		bothWayTest(tok, untok, expected);
		
		tok = "-- -- -- -- -- -- -- --";
		untok = "(((())))";
		expected = getParanExpected(tok);
		bothWayTest(tok, untok, expected);
		
		tok = "-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --";
		untok = "(((((((())))))))";
		expected = getParanExpected(tok);
		bothWayTest(tok, untok, expected);
	}
	
	@Test
	public void bigParanTest() {
//		Algorithm is to slow for the following example. But it's pretty extreme ...		
		String tok, untok;
		tok = "-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --";
		untok = "(((((((((((((((())))))))))))))))";
		expectTimeout(tok, untok, 1000);
	}
	
	private void expectTimeout(String tok, String untok, long time) {
		expectNoAlign(tok, untok, ResultType.Timeout, time);
	}
	
	private void expectNoAlign(String tok, String untok) {
		expectNoAlign(tok, untok, ResultType.NoAlignmentFound, 1000);
	}
	
	private void expectNoAlign(String tok, String untok, ResultType type, long time) {
		Aligner a = new LevenshteinAligner(time);
		Result actual = a.align(untok, tok);
		
		System.err.println(actual);
		
		assertEquals(null, actual.pairs);
		assertEquals(type, actual.result_type);
	}

	@Test
	public void quasiRealWorldTest() {
		String tok, untok;
		List<Pair> expected;
		
		tok = "„ „ AAA “ “";
		untok = "\"\"AAA\"\"";
		expected = new LinkedList<Pair>();
		expected.add(new Pair(0, 0));
		expected.add(new Pair(-1, 1));
		expected.add(new Pair(1, 2));
		expected.add(new Pair(-1, 3));
		expected.add(new Pair(2, 4));
		expected.add(new Pair(3, 5));
		expected.add(new Pair(4, 6));
		expected.add(new Pair(-1, 7));
		expected.add(new Pair(5, 8));
		expected.add(new Pair(-1, 9));
		expected.add(new Pair(6, 10));
		bothWayTest(tok, untok, expected);
		
		tok = "B BBB B";
		untok = "B(B";
		expected = new LinkedList<Pair>();
		expected.add(new Pair(0, 0));
		expected.add(new Pair(-1, 1));
		expected.add(new Pair(1, 2));
		expected.add(new Pair(1, 3));
		expected.add(new Pair(1, 4));
		expected.add(new Pair(-1, 5));
		expected.add(new Pair(2, 6));
		bothWayTest(tok, untok, expected);
	}
	
	@Test
	public void realSentenceTest() {
		//timeout is to expected
		String tok, untok;
		List<Pair> expected = new LinkedList<>();
		
		tok = "VI Fz Fz C.";
		untok = "VI C.";
		expectNoAlign(tok, untok);
		
		tok = "dá me lo";
		untok = "dámelo";
		expected.clear();
		expected.add(new Pair(0,0));
		expected.add(new Pair(1,1));
		expected.add(new Pair(-1,2));
		expected.add(new Pair(2,3));
		expected.add(new Pair(3,4));
		expected.add(new Pair(-1,5));
		expected.add(new Pair(4,6));
		expected.add(new Pair(5,7));
		bothWayTest(tok, untok, expected);

		tok = "de el";
		untok = "deL";
		expected.clear();
		expected.add(new Pair(0,0));
		expected.add(new Pair(1,1));
		expected.add(new Pair(-1,2));
		expected.add(new Pair(2,3));
		expected.add(new Pair(2,4));
		bothWayTest(tok, untok, expected);
		
		
		tok = "de el";
		untok = "del";
		expectNoAlign(tok, untok);
		
		// untok contains a weird space character (char value 160 instead of 32)
		tok = "L 1 bis L 499";
		untok = "L 1 bis L 499";
		expected.clear();
		for (int i=0; i<tok.length(); i++) {
			expected.add(new Pair(i,i));
		}
		bothWayTest(tok, untok, expected);
		
		// untok contains a weird space character (char value 160 instead of 32)
		tok = "vorgesehen , für diejenigen im Gebiet des Landschaftsverbandes Westfalen-Lippe die Bezeichnungen von L 501 bis L 999 .";
		untok = "vorgesehen, für diejenigen im Gebiet des Landschaftsverbandes Westfalen-Lippe die Bezeichnungen von L 501 bis L 999.";
		expected.clear();
		for (int i=0; i< 10; i++) {
			expected.add(new Pair(i,i));
		}
		expected.add(new Pair(-1, 10));
		for (int i=10; i< untok.length() - 1; i++) {
			expected.add(new Pair(i,i + 1));
		}		
		expected.add(new Pair(-1, untok.length()));
		expected.add(new Pair(untok.length() - 1, untok.length() + 1));
		bothWayTest(tok, untok, expected);
	}
}
