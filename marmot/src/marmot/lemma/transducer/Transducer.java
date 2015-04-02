package marmot.lemma.transducer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

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
	
	protected Set<Character> alphabet;
	
	public Transducer(Set<Character> alphabet, int c1, int c2, int c3, int c4) throws NegativeContext {
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
			cartesianProductArgs.add(this.alphabet);
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
	
	protected abstract void gradient(double[] gradient);
	protected abstract void gradient(double[] gradient, String upper, String lower);
	protected abstract double logLikelihood(String upper, String lower);

	
}
