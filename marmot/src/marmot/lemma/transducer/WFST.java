package marmot.lemma.transducer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import marmot.lemma.Instance;
import marmot.lemma.Lemmatizer;
import marmot.lemma.transducer.exceptions.NegativeContext;

public class WFST extends Transducer {

	public WFST(Map<Character,Integer> alphabet, int c1, int c2, int c3, int c4) throws NegativeContext {
		super(alphabet,c1,c2,c3,c4);
	}

	@Override
	protected void gradient(double[][][] gradient) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	protected void gradient(double[][][] gradient, int i) {
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
		

		this.trainingData = instances;
		this.devData = dev_instances;
		
		Pair<int[][][],Integer> result = preextractContexts(instances,this.c1,this.c2, this.c3, this.c4);
		this.contexts = result.getValue0();
			
		// get maximum input and output strings sizes
		this.alphabet = new HashMap<Character,Integer>();
		Pair<Integer,Integer> maxes = extractAlphabet();
		
		// weights and gradients
		this.weights = new double[result.getValue1()][3][this.alphabet.size()];

		double[][][] gradientVector = new double[result.getValue1()][3][this.alphabet.size()];
		double[][][] approxGradientVector = new double[result.getValue1()][3][this.alphabet.size()];

		
		randomlyInitWeights();
		
		this.alphas = new double[maxes.getValue0()][maxes.getValue1()];
		this.betas = new double[maxes.getValue0()][maxes.getValue1()];
		
		zeroOut(alphas);
		zeroOut(betas);
	
		this.gradient(gradientVector,5);
		
		
		return new LemmatizerWFST();
		
		
	}

}
