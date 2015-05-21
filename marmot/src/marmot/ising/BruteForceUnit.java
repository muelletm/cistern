package marmot.ising;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import marmot.util.Numerics;

import org.javatuples.Pair;

public class BruteForceUnit {

	
	public static void main(String[] args) {
		int correct = 0;
		int total = 100;
		for (int i = 0; i < total; ++i) {
			if (test(6)) {
				++correct;
			}
		}
		System.out.println( ((double) correct / total));
	}
	
	public static boolean test(int numVariables) {
		Random rand = new Random();

		List<String> tagNames = new LinkedList<String>();
		for (int i = 0; i < numVariables; ++i) {
			tagNames.add("");
		}
		
		// adjust tree as seen fit
		List<Pair<Integer,Integer>> pairs = new LinkedList<Pair<Integer,Integer>>();
		pairs.add(new Pair<>(0,1));
		pairs.add(new Pair<>(1,2));
		pairs.add(new Pair<>(1,3));
		pairs.add(new Pair<>(2,4));
		pairs.add(new Pair<>(4,5));

		
		
		IsingFactorGraph fg = new IsingFactorGraph(numVariables, pairs, tagNames);
		
		// random unary potentials
		for (UnaryFactor uf : fg.unaryFactors) {
			uf.setPotential(0, Math.abs(rand.nextGaussian()));
			uf.setPotential(1, Math.abs(rand.nextGaussian()));
		}
	
		// random binary potentials
		for (BinaryFactor bf : fg.binaryFactors) {
			bf.setPotential(0, 0, Math.abs(rand.nextGaussian()));
			bf.setPotential(0, 1, Math.abs(rand.nextGaussian()));
			bf.setPotential(1, 0, Math.abs(rand.nextGaussian()));
			bf.setPotential(1, 1, Math.abs(rand.nextGaussian()));

		}
	
		
		//System.out.println("...brute-force inference...");
		double[][] marginalsBruteForce = fg.inferenceBruteForce();
		
		//System.out.println("...belief propagation...");
		fg.inference(10, 1.0);
	
		for (int n = 0; n < numVariables; ++n) {
			double[] marginal = fg.variables.get(n).getBelief().measure;
			//System.out.println("N:" + n + "\t" + Arrays.toString(marginalsBruteForce[n]));
			//System.out.println("N:" + n + "\t" + Arrays.toString(marginal));

			if (!Numerics.approximatelyEqual(marginalsBruteForce[n],marginal,0.01)) {
				return false;
			}
		}
		return true;
	}
}
