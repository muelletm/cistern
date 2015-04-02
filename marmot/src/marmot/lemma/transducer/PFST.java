package marmot.lemma.transducer;

import java.util.Set;

import marmot.lemma.transducer.exceptions.LabelBiasException;
import marmot.lemma.transducer.exceptions.NegativeContext;

public class PFST extends Transducer {

	public PFST(Set<Character> alphabet, int c1, int c2, int c3, int c4) throws LabelBiasException, NegativeContext {
		super(alphabet, c1, c2, c3, c4);
		if (c4 != 0) {
			throw new LabelBiasException();
		}
		// TODO Auto-generated constructor stub
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
