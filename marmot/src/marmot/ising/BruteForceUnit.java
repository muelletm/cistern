package marmot.ising;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

public class BruteForceUnit {

	
	public static void main(String[] args) {
		
		int numVariables = 4;
		List<String> tagNames = new LinkedList<String>();
		for (int i = 0; i < numVariables; ++i) {
			tagNames.add("");
		}
		
		List<Pair<Integer,Integer>> pairs = new LinkedList<Pair<Integer,Integer>>();
		pairs.add(new Pair<>(0,1));
		pairs.add(new Pair<>(1,2));
		pairs.add(new Pair<>(1,3));
		
		
		IsingFactorGraph fg = new IsingFactorGraph(numVariables, pairs, tagNames);
		fg.unaryFactors.get(0).setPotential(0, 2.0);
		
		System.out.println("...brute-force inference...");
		fg.inferenceBruteForce();
		
		System.out.println("...belief propagation...");
		fg.inference(10, 1.0);
		
		for (int n = 0; n < numVariables; ++n) {
			System.out.println("Var Id: " + n + "\t" + Arrays.toString(fg.variables.get(n).getBelief().measure));
		}
	
	}
}
