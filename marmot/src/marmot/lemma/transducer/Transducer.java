package marmot.lemma.transducer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.Sets;

import org.javatuples.Pair;

import marmot.lemma.Instance;
import marmot.lemma.LemmatizerTrainer;
import marmot.lemma.transducer.exceptions.NegativeContext;

public abstract class Transducer implements LemmatizerTrainer {
	
	// context values
	// See Cotterell et al. (2014) for more details
	// c1 = upper left hand context
	// c2 = upper right hand context
	// c3 = lower left hand context
	// c4 = lower right hand context
	protected int c1;
	protected int c2;
	protected int c3;
	protected int c4;
	
	// arrays for dynamic programming
	protected double[][] alphas;
	protected double[][] betas;
	
	// context to int assignment
	protected int[][][] contexts;
	
	// weights (in context space)
	protected double[][][] weights;
	
	//data
	protected List<Instance> trainingData;
	protected List<Instance> devData;
	
	protected Map<Character,Integer> alphabet;
	
	public Transducer(Map<Character,Integer> alphabet, int c1, int c2, int c3, int c4) throws NegativeContext {
		this.c1 = c1;
		this.c2 = c2;
		this.c3 = c3;
		this.c4 = c4;
		
		if (this.c1 < 0 || this.c2 < 0 || this.c3 < 0 || this.c4 < 0) {
			throw new NegativeContext();
		}
		
		this.alphabet = alphabet;
	}
	
	
	@SuppressWarnings("unchecked")
	protected void extractContexts() {
		List<Set<Character>> cartesianProductArgs = new ArrayList<Set<Character>>();
		for (int i = 0; i < this.c1 + this.c2 + this.c3 + this.c4; ++i) {
			//cartesianProductArgs.add(this.alphabet);
		}
				
		Sets.cartesianProduct((Set[]) cartesianProductArgs.toArray());
		
	}
	
	/**
	 *  zerosOut an array in the log semiring.
	 * @param array
	 */
	protected static void zeroOut(double[][] array) {
		for (int i = 0; i < array.length; ++i) {
			for (int j = 0; j < array[0].length; ++j) {
				array[i][j] = Double.NEGATIVE_INFINITY;
			}
		}
	}
	
	/**
	 *  zerosOut an array in the log semiring.
	 * @param array
	 */
	protected static void zeroOut(double[][] array, int first, int second) {
		for (int i = 0; i < first; ++i) {
			for (int j = 0; j < second; ++j) {
				array[i][j] = Double.NEGATIVE_INFINITY;
			}
		}
	}
	
	
	protected void randomlyInitWeights() {
		Random rand = new Random();
		for (int i = 0; i < this.weights.length; ++i) {
			for (int j = 0; j < 3; ++j) {
				for (int k= 0; k < this.alphabet.size(); ++k) {
					this.weights[i][j][k] += rand.nextGaussian();
				}
			}
		}
	}
	
	protected Pair<Integer,Integer> extractAlphabet() {
		// TERMINATION SYMBOL
		this.alphabet.put('$',0);
				
		int max1 = 0;
		int max2 = 0;
				
		int alphabetCounter = 1;
		for (Instance instance : this.trainingData) {
				max1 = Math.max(max1,instance.getForm().length()+1);
				max2 = Math.max(max2,instance.getLemma().length()+1);	
						
				//extract alphabet
				for (Character c : instance.getForm().toCharArray()) {
					if (!this.alphabet.keySet().contains(c)) {
						this.alphabet.put(c,alphabetCounter);
						alphabetCounter += 1;
					}
				}
				for (Character c : instance.getLemma().toCharArray()) {
					if (!this.alphabet.keySet().contains(c)) {
						this.alphabet.put(c,alphabetCounter);
						alphabetCounter += 1;
					}
				}
		}
		
		for (Instance instance : this.devData) {
			max1 = Math.max(max1,instance.getForm().length()+1);
			max2 = Math.max(max2,instance.getLemma().length()+1);
			
			//extract alphabet
			for (Character c : instance.getForm().toCharArray()) {
				if (!this.alphabet.keySet().contains(c)) {
					this.alphabet.put(c,alphabetCounter);
					alphabetCounter += 1;
				}
			}
			for (Character c : instance.getLemma().toCharArray()) {
				if (!this.alphabet.keySet().contains(c)) {
					this.alphabet.put(c,alphabetCounter);
					alphabetCounter += 1;
				}
			}
		}		
		return new Pair<Integer,Integer>(max1,max2);
	}
	
	protected Pair<int[][],Integer> preextractUpperContexts(List<Instance> instances, int c1, int c2) {
		int[][] contexts = new int[instances.size()][];
		
		String END_SYMBOL = "$";
		String BREAK_SYMBOL = "*****";
		
		//String upper = "abcd";
		//String lower = "efgh";
		
		Map<String,Integer> hash = new HashMap<String,Integer>();
		int counter = 0;
		int instanceI = 0;
		
		for (Instance instance : instances) {
			
			String upper = instance.getForm();
			
			contexts[instanceI] = new int[upper.length()+1];
			
			for (int i = 0; i < upper.length() + 1; ++i) {

				int pointI = Math.min(i, upper.length());
				
				int ul_limit = Math.max(0, pointI - c1);
				int ur_limit = Math.min(upper.length(), pointI + c2);


				String ul = upper.substring(ul_limit, pointI);
				String ur = upper.substring(pointI, ur_limit);

				// pad
				while (ul.length() < c1) {
					ul = END_SYMBOL + ul;
				}

				while (ur.length() < c2) {
					ur = ur + END_SYMBOL;
				}

			
				String contextString = ul + BREAK_SYMBOL + ur;
				
				if (!hash.keySet().contains(contextString)) {
					hash.put(contextString, counter);
					++counter;
				}
					
				contexts[instanceI][i] = hash.get(contextString);

				
			}
			instanceI += 1;
		}
		return new Pair<int[][],Integer> (contexts,counter-1);
	}
	

	protected Pair<int[][][],Integer> preextractContexts(List<Instance> instances, int c1, int c2, int c3, int c4) {
		int[][][] contexts = new int[instances.size()][][];
		
		String END_SYMBOL = "$";
		String BREAK_SYMBOL = "*****";
		
		//String upper = "abcd";
		//String lower = "efgh";
		
		Map<String,Integer> hash = new HashMap<String,Integer>();
		int counter = 0;
		int instanceI = 0;
		
		for (Instance instance : instances) {
			
			String upper = instance.getForm();
			String lower = instance.getLemma();
			
			contexts[instanceI] = new int[upper.length()+1][lower.length()+1];
			
			for (int i = 0; i < upper.length() + 1; ++i) {

				int pointI = Math.min(i, upper.length());
				
				int ul_limit = Math.max(0, pointI - c1);
				int ur_limit = Math.min(upper.length(), pointI + c2);


				String ul = upper.substring(ul_limit, pointI);
				String ur = upper.substring(pointI, ur_limit);

				// pad
				while (ul.length() < c1) {
					ul = END_SYMBOL + ul;
				}

				while (ur.length() < c2) {
					ur = ur + END_SYMBOL;
				}

				for (int j = 0; j < lower.length() + 1; ++j) {

					int pointJ = Math.min(j, lower.length());

					int ll_limit = Math.max(0, pointJ - c3);
					int lr_limit = Math.min(lower.length(), pointJ + c4);
					
					
					String ll = lower.substring(ll_limit, pointJ);
					String lr = lower.substring(pointJ, lr_limit);

					// pad
					while (lr.length() < c3) {
						lr = lr + END_SYMBOL;
					}
					while (ll.length() < c4) {
						ll = END_SYMBOL + ll;
					}

					// hash the string
					String contextString = ul + BREAK_SYMBOL + ur
							+ BREAK_SYMBOL + ll + BREAK_SYMBOL + lr;

					/*
					System.out.println(i);
					System.out.println(j);
					System.out.println("C1: " + ul + ", C2: " + ur + ", C3: "
							+ ll + ", C4: " + lr);
					System.out.println();
					*/
					if (!hash.keySet().contains(contextString)) {
						hash.put(contextString, counter);
						++counter;
					}
					
					contexts[instanceI][i][j] = hash.get(contextString);

				}
			}
			instanceI += 1;
		}
		return new Pair<int[][][],Integer> (contexts,counter-1);
	}
	
	protected abstract void gradient(double[][][] gradient);
	protected abstract void gradient(double[][][] gradient, int instanceId);
	protected abstract double logLikelihood(int instanceId);
	protected abstract double logLikelihood();
	
}
