// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.test.util;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import marmot.util.GeneralLevenshteinLattice;

import org.junit.Test;

public class GeneralLevenshteinLatticeTest {

	@Test
	public void costTest() {
		List<String> input, output;
		GeneralLevenshteinLattice<String> lattice;
		
		input = Arrays.asList("A", "C", "B");
		output = Arrays.asList("A", "D", "B");
		lattice = new GeneralLevenshteinLattice<String>(input, output, 2, 2, 3);
		assertEquals(3, lattice.getDistance());
		
		input = Arrays.asList("A", "C", "B");
		output = Arrays.asList("A", "D", "D", "B");
		lattice = new GeneralLevenshteinLattice<String>(input, output, 2, 2, 3);
		assertEquals(5, lattice.getDistance());
		
		input = Arrays.asList("A", "C", "B");
		output = Arrays.asList("A","D", "D", "D", "D", "B");
		lattice = new GeneralLevenshteinLattice<String>(input, output, 2, 2, 3);
		assertEquals(9, lattice.getDistance());
		
		input = Arrays.asList("A", "C", "B");
		output = Arrays.asList("A","D", "D", "D", "D", "D", "B");
		lattice = new GeneralLevenshteinLattice<String>(input, output, 2, 2, 3);
		assertEquals(11, lattice.getDistance());
		
		input = Arrays.asList("A", "C", "B");
		output = Arrays.asList("A","D", "D", "D", "D", "D", "D", "B");
		lattice = new GeneralLevenshteinLattice<String>(input, output, 2, 2, 3);
		assertEquals(13, lattice.getDistance());
	
	}
	
}
