package marmot.lemma.transducer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import org.javatuples.Pair;

import marmot.lemma.Instance;
import marmot.lemma.Lemmatizer;
import marmot.lemma.transducer.exceptions.LabelBiasException;
import marmot.lemma.transducer.exceptions.NegativeContext;
import marmot.util.Numerics;



public class PFST extends Transducer {

	// logger
    private static final Logger LOGGER = Logger.getLogger(PFST.class.getName());
    
    //distribution
    private double[][][] distribution;

	/**
	 * Don't ever call
	 * @throws NegativeContext
	 * @throws LabelBiasException 
	 */
	public PFST() throws NegativeContext, LabelBiasException {
		this(null,0,1,0,0);
	}
	public PFST(Map<Character,Integer> alphabet, int c1, int c2, int c3, int c4) throws LabelBiasException, NegativeContext {
		super(alphabet, c1, c2, c3, c4);
		if (c4 != 0) {
			throw new LabelBiasException();
		}
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void gradient(double gradient[] ) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void gradient(double[] gradient, int instanceId) {
		// get data instance
		Instance instance = this.trainingData.get(instanceId);
		String upper = instance.getForm();
		String lower = instance.getLemma();
		
		LOGGER.info("Starting gradient computation for pair (" + upper + "," + lower + ")...");
		//zero out the relevant positions in the log-semiring
		zeroOut(alphas,upper.length()+1, lower.length()+1);
		zeroOut(betas,upper.length()+1, lower.length()+1);
		
		//make the start position unity in the log-semiring
		alphas[0][0] = 0.0;
		betas[upper.length()][lower.length()] = 0.0;
		
		//backward
		for (int i = upper.length() - 1 ; i >= 0; --i) {
			for (int j = lower.length() - 1; j >= 0; --j) {
	
				int context_id = contexts[instanceId][i][j];
				double w  = 0.0;
				
				// ins 
				betas[i][j] = Numerics.sumLogProb(betas[i][j], betas[i+1][j] + w);
				// del
				betas[i][j] = Numerics.sumLogProb(betas[i][j], betas[i][j+1] + w);
				// sub
				betas[i][j] = Numerics.sumLogProb(betas[i][j], betas[i+1][j+1] + w);
			}
		}
		

		//PRINT
		//backward
		for (int i = 0; i < upper.length() + 1; ++i) {
			for (int j = 0; j < lower.length() + 1; ++j ) {
				System.out.print(Double.toString(betas[i][j]) + "\t");
			}
			System.out.println();
		}
		
		//forward 
		for (int i = 1; i < upper.length() + 1; ++i) {
			for (int j = 1; j < lower.length() + 1; ++j ) {
				
				int contextId = contexts[instanceId][i-1][j-1];				
				// ins
				double tmp = alphas[i-1][j] * betas[i][j];
				// del
				tmp = alphas[i][j-1] * betas[i][j];
				
				// sub
				tmp = alphas[i-1][j-1] * betas[i][j];
				
				double w = 0.0;
				
				// ins 
				alphas[i][j] = Numerics.sumLogProb(alphas[i][j],alphas[i-1][j] + w);
				// del
				alphas[i][j] = Numerics.sumLogProb(alphas[i][j],alphas[i][j-1] + w);
				// sub
				alphas[i][j] = Numerics.sumLogProb(alphas[i][j],alphas[i-1][j-1] + w);
			}
		}
		// PRINT
		// forward
		for (int i = 0; i < upper.length() + 1; ++i) {
			for (int j = 0; j < lower.length() + 1; ++j ) {
				System.out.print(Double.toString(alphas[i][j]) + "\t");
			}
			System.out.println();
		}
		
		System.out.println(betas[0][0]);
		System.out.println(alphas[upper.length()][lower.length()]);
	}
	
	@Override
	protected double logLikelihood() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected double logLikelihood(int instanceId) {
		// get data instance
		Instance instance = this.trainingData.get(instanceId);
		String upper = instance.getForm();
		String lower = instance.getLemma();
		
		zeroOut(betas,upper.length()+1, lower.length()+1);
		betas[upper.length()][lower.length()] = 0.0;
		
		//backward
		for (int i = upper.length() - 1 ; i >= 0; --i) {
			for (int j = lower.length() - 1; j >= 0; --j) {
	
				// TODO EXTRACT CONTEXT
				
				
				// ins 
				betas[i][j] = Numerics.sumLogProb(betas[i][j], betas[i+1][j] + 1.0);
				// del
				betas[i][j] = Numerics.sumLogProb(betas[i][j], betas[i][j+1] + 1.0);
				// sub
				betas[i][j] = Numerics.sumLogProb(betas[i][j], betas[i+1][j+1] + 1.0);
			}
		}
		
		// TODO Auto-generated method stub
		return 0;
	}

	private void renormalizeAll() {
		
	}
	
	@Override
	public Lemmatizer train(List<Instance> instances,
			List<Instance> dev_instances) {
		
		this.trainingData = instances;
		this.devData = dev_instances;
		
		Pair<int[][][],Integer> result = preextractContexts(instances,this.c1,this.c2, this.c3, this.c4);
		this.contexts = result.getValue0();
		this.weights = new double[result.getValue1()][3][this.alphabet.size()];
		
		Random rand = new Random();
		for (int i = 0; i < result.getValue1(); ++i) {
			for (int j = 0; j < 3; ++j) {
				for (int k= 0; k < this.alphabet.size(); ++k) {
					this.weights[i][j][k] += rand.nextGaussian();
				}
			}
		}
		
		// get maximum input and output strings sizes
		this.alphabet = new HashMap<Character,Integer>();
		
		// TERMINATION SYMBOL
		this.alphabet.put('$',0);
		
		
		int max1 = 0;
		int max2 = 0;
		
		int alphabetCounter = 1;
		for (Instance instance : instances) {
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
		for (Instance instance : dev_instances) {
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
		
		this.distribution = new double[result.getValue1()][3][this.alphabet.size()];
		
		this.alphas = new double[max1][max2];
		this.betas = new double[max1][max2];
		
		zeroOut(alphas);
		zeroOut(betas);
	
		double[] gradient_vector = new double[5];
		this.gradient(gradient_vector,5);
		
		return new LemmatizerPFST();
	}
	
	

	

}
