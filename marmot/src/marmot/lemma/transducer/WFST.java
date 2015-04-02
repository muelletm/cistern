package marmot.lemma.transducer;

import java.util.Set;

import marmot.lemma.transducer.exceptions.NegativeContext;

public class WFST extends Transducer {

	public WFST(Set<Character> alphabet, int c1, int c2, int c3, int c4) throws NegativeContext {
		super(alphabet,c1,c2,c3,c4);
	}

	@Override
	protected void gradient() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected double logLikelihood() {
		// TODO Auto-generated method stub
		return 0;
	}
}
