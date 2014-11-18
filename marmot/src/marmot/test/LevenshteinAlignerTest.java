package marmot.test;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import marmot.tokenize.openlp.Aligner;
import marmot.tokenize.openlp.LevenshteinAligner;
import marmot.tokenize.openlp.Aligner.Pair;

import org.junit.Test;

public class LevenshteinAlignerTest {

	public void bothWayTest(String tok, String untok, List<Pair> expected) {
		List<Pair> actual;
		Aligner a = new LevenshteinAligner();
		
		actual = a.align(untok, tok);
		assertEquals(expected, actual);
		
		// Reverse problem
		List<Pair> expected_reversed = new LinkedList<Pair>();
		for (Pair pair : expected) {
			expected_reversed.add(new Pair(pair.b, pair.a));
		}
		actual = a.align(tok, untok);
		assertEquals(expected_reversed, actual);
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
		expectTimeout(tok, untok);
	}
	
	private void expectTimeout(String tok, String untok) {
		List<Pair> actual;
		Aligner a = new LevenshteinAligner(1000);
		actual = a.align(untok, tok);
		assertEquals(null, actual);
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
		
		tok = "En el siglo VI Fz a . Fz C. el imperio neobabilónico o caldeo se convertía en el imperio más poderoso de la antigüedad , sustituyendo a el poderío asirio .";
		untok = "En el siglo VI a. C. el imperio neobabilónico o caldeo se convertía en el imperio más poderoso de la antigüedad, sustituyendo al poderío asirio.";
		expectTimeout(tok, untok);
		
		tok = "El rey Nabucodonosor II se constituyó a sí mismo , en el soberano más poderoso de la antigüedad antes de la aparición de el imperio aqueménida y de el aplastante éxito de Alejandro Magno sobre éste .";
		untok = "El rey Nabucodonosor II se constituyó a sí mismo, en el soberano más poderoso de la antigüedad antes de la aparición del imperio aqueménida y del aplastante éxito de Alejandro Magno sobre éste.";
		expectTimeout(tok, untok);
		
		tok = "Für die Landesstraßen im Gebiet des Landschaftsverbandes Rheinland sind die Bezeichnungen von L 1 bis L 499 vergeben bzw.";
		untok = "Für die Landesstraßen im Gebiet des Landschaftsverbandes Rheinland sind die Bezeichnungen von L 1 bis L 499 vergeben bzw.";
		expectTimeout(tok, untok);
		
		tok = "vorgesehen , für diejenigen im Gebiet des Landschaftsverbandes Westfalen-Lippe die Bezeichnungen von L 501 bis L 999 .";
		untok = "vorgesehen, für diejenigen im Gebiet des Landschaftsverbandes Westfalen-Lippe die Bezeichnungen von L 501 bis L 999.";
		expectTimeout(tok, untok);
	}
}
