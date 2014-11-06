// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.test;

import static org.junit.Assert.*;

import marmot.util.LevenshteinLattice;

import org.junit.Test;

public class LevenshteinLatticeTest {

	@Test
	public void costTest() {
		String input, output, expected, actual;
		LevenshteinLattice lattice;
		
		input = "A C B";
		output = "A D B";
		lattice = new LevenshteinLattice(input, output, 2, 2, 3);
		actual = lattice.searchOperationSequence();
		expected = "CCRCC";
		assertEquals(expected, actual);
		assertEquals(3, lattice.getDistance());
		
		input = "A C B";
		output = "A DD B";
		lattice = new LevenshteinLattice(input, output, 2, 2, 3);
		actual = lattice.searchOperationSequence();
		expected = "CCIRCC";
		assertEquals(expected, actual);
		assertEquals(5, lattice.getDistance());
		
		input = "A C B";
		output = "A DDDD B";
		lattice = new LevenshteinLattice(input, output, 2, 2, 3);
		actual = lattice.searchOperationSequence();
		expected = "CCIIIRCC";
		assertEquals(expected, actual);
		assertEquals(9, lattice.getDistance());
		
		input = "A C B";
		output = "A DDDDD B";
		lattice = new LevenshteinLattice(input, output, 2, 2, 3);
		actual = lattice.searchOperationSequence();
		expected = "CCIIIIRCC";
		assertEquals(expected, actual);
		assertEquals(11, lattice.getDistance());
		
		input = "A C B";
		output = "A DDDDDD B";
		lattice = new LevenshteinLattice(input, output, 2, 2, 3);
		actual = lattice.searchOperationSequence();
		expected = "CCIIIIIRCC";
		assertEquals(expected, actual);
		assertEquals(13, lattice.getDistance());
	
		input = "C";
		output = "DD";
		lattice = new LevenshteinLattice(input, output, 2, 2, 3);
		actual = lattice.searchOperationSequence();
		System.err.println(actual);
		assertEquals(5, lattice.getDistance());
		expected = "IR";
		assertEquals(expected, actual);
		
		input = "A CB";
		output = "A DDDDD B";
		lattice = new LevenshteinLattice(input, output, 2, 2, 3);
		actual = lattice.searchOperationSequence();
		assertEquals(13, lattice.getDistance());
		expected = "CCIIIIIRC";
		assertEquals(expected, actual);
		

	}
	
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
