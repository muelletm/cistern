package marmot.lemma.transducer;

import java.util.Set;

import marmot.lemma.transducer.exceptions.NegativeContext;

public abstract class Transducer {
	
	// context values
	// See Cotterell et al. (2014) for more details
	// c1 = upper left hand context
	// c2 = upper right hand context
	// c3 = lower left hand context
	// c4 = lower right hand context
	private int c1;
	private int c2;
	private int c3;
	private int c4;
	
	private Set<Character> alphabet;
	
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
	
	private void extractContexts() {
		
	}
	
	protected abstract void gradient();
	protected abstract double logLikelihood();
	
}
