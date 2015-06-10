package marmot.ising;

import java.util.ArrayList;
import java.util.Arrays;
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
			if (test(3)) {
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
		pairs.add(new Pair<>(0,2));
		
		//pairs.add(new Pair<>(1,3));
		//pairs.add(new Pair<>(2,4));
		//pairs.add(new Pair<>(4,5));
		//pairs.add(new Pair<>(0,5));


		// golden
		List<Integer> golden = new ArrayList<Integer>();
		/*
		for (int i = 0; i < numVariables; ++i) {
			if (rand.nextBoolean()) {
				golden.add(1);
			} else {
				golden.add(0);
			}
		}*/
		
		for (int i = 0; i < numVariables; ++i) {
			golden.add(0);

		}
		
		IsingFactorGraph fg = new IsingFactorGraph(numVariables, pairs, golden, tagNames);
		int numParameters = 2 * fg.unaryFactors.size() + 4 * fg.binaryFactors.size();
		double[] parameters = new double[numParameters];
		//for (int i = 0; i < parameters.length; ++i) {
		//	parameters[i] = 1.0;
		//}
		//parameters[0] = 1.0;

		parameters[2 * fg.unaryFactors.size()] = 1.0;
		parameters[2 * fg.unaryFactors.size() + 2] = 1.0;
		
		//System.out.println(Arrays.toString(parameters));
		// random unary potentials
		int counter = 0;
		for (UnaryFactor uf : fg.unaryFactors) {
			//parameters[counter] = rand.nextGaussian();
			uf.setPotential(0, Math.exp(parameters[counter]));
			++counter;
			
			//parameters[counter] = rand.nextGaussian();
			uf.setPotential(1, Math.exp(parameters[counter]));
			++counter;
		}
	
		// random binary potentials
		for (BinaryFactor bf : fg.binaryFactors) {
			//parameters[counter] = rand.nextGaussian();
			bf.setPotential(0, 0, Math.exp(parameters[counter]));
			++counter;
			
			//parameters[counter] = rand.nextGaussian();
			bf.setPotential(0, 1, Math.exp(parameters[counter]));
			++counter;
			
			//parameters[counter] = rand.nextGaussian();
			bf.setPotential(1, 0, Math.exp(parameters[counter]));
			++counter;
			
			//parameters[counter] = rand.nextGaussian();
			bf.setPotential(1, 1, Math.exp(parameters[counter]));
			++counter;

		}
	
		
		//System.out.println("...brute-force inference...");
		double[][] marginalsBruteForce = fg.inferenceBruteForce();
	
		//System.out.println("...belief propagation...");
		fg.inference(10, 1.0);
	
		for (int n = 0; n < numVariables; ++n) {
			double[] marginal = fg.variables.get(n).getBelief().measure;
			//System.out.println(fg.approximateZ());

			//System.out.println(fg.logLikelihood());
			//System.out.println("REAL GRAD:\t" + Arrays.toString(fg.unfeaturizedGradient()));

			//System.out.println("FINITE DIFF:\t" + Arrays.toString(fg.finiteDifference(parameters, 0.0001)));

			/*
			System.out.println("BRUTE FORCE 0:" + 0 + "\t" + Arrays.toString(marginalsBruteForce[0]));
			System.out.println("MARGINALS 0:" + 0 + "\t" + Arrays.toString(marginal));

	
			System.out.println("BRUTE FORCE 1:" + 1 + "\t" + Arrays.toString(marginalsBruteForce[1]));
			System.out.println("MARGINALS 1:" + 1 + "\t" + Arrays.toString(marginal));
			*/
			if (!Numerics.approximatelyEqual(marginalsBruteForce[n],marginal,0.1)) {
				//System.out.println("False");
				return false;
			} else {
				//System.out.println("True");
			}
			//System.exit(0);

		}
		return true;
	}
}
