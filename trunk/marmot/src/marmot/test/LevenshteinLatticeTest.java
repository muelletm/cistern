// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.test;

import static org.junit.Assert.*;

import marmot.util.LevenshteinLattice;

import org.junit.Test;

public class LevenshteinLatticeTest {

	@Test
	public void test() {
		String input, output, expected, actual;
		LevenshteinLattice lattice;

		input = "<i>This isn't a sentence.</i>";
		output = "This is n't a santence .";
		lattice = new LevenshteinLattice(input, output, 1, 1, 2);
		actual = lattice.searchOperationSequence();
		expected = "DDDCCCCCCCICCCCCCCRCCCCCCICDDDD";
		assertEquals(expected, actual);
		
		// Some special cases
		
		input = "ABCD";
		output = "";
		lattice = new LevenshteinLattice(input, output, 1, 1, 2);
		actual = lattice.searchOperationSequence();
		expected = "DDDD";
		assertEquals(expected, actual);
		
		input = "";
		output = "ABCD";
		lattice = new LevenshteinLattice(input, output, 1, 1, 2);
		actual = lattice.searchOperationSequence();
		expected = "IIII";
		assertEquals(expected, actual);
		
		input = "";
		output = "";
		lattice = new LevenshteinLattice(input, output, 1, 1, 2);
		actual = lattice.searchOperationSequence();
		expected = "";
		assertEquals(expected, actual);
	
		input = "ABCD";
		output = "ABCD";
		lattice = new LevenshteinLattice(input, output, 1, 1, 2);
		actual = lattice.searchOperationSequence();
		expected = "CCCC";
		assertEquals(expected, actual);
	}

}
