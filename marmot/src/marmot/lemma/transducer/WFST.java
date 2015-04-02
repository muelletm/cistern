package marmot.lemma.transducer;

import java.util.List;
import java.util.Set;

import marmot.lemma.Instance;
import marmot.lemma.Lemmatizer;
import marmot.lemma.transducer.exceptions.NegativeContext;

public class WFST extends Transducer {

	public WFST(Set<Character> alphabet, int c1, int c2, int c3, int c4) throws NegativeContext {
		super(alphabet,c1,c2,c3,c4);
	}

	@Override
	protected void gradient(double[] gradient) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	protected void gradient(double[] gradient, int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected double logLikelihood() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	protected double logLikelihood(int i ) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Lemmatizer train(List<Instance> instances,
			List<Instance> dev_instances) {
		return new LemmatizerWFST();
	}

}
