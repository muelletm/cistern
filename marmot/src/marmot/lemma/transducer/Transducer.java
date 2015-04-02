package marmot.lemma.transducer;

import java.util.Set;

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
	
	
	protected void extractContexts() {
		
	}
	
	protected abstract void gradient();
	protected abstract double logLikelihood();
	
}
