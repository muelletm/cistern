package marmot.lemma.transducer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
	protected void gradient(double gradient[][][] ) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void gradient(double[][][] gradient, int instanceId) {
		// get data instance
		Instance instance = this.trainingData.get(instanceId);
		String upper = instance.getForm();
		String lower = instance.getLemma();
		
		this.renormalizeAll();
		
	
		LOGGER.info("Starting gradient computation for pair (" + upper + "," + lower + ")...");
		//zero out the relevant positions in the log-semiring
		zeroOut(alphas,upper.length()+1, lower.length()+1);
		zeroOut(betas,upper.length()+1, lower.length()+1);
		//TODO NEED TO MOVE OUT TO MAKE EFFICIENT
		
		double[] contextCounts = new double[gradient.length];
		
		//make the start position unity in the log-semiring
		alphas[0][0] = 0.0;
		betas[upper.length()][lower.length()] = 0.0;
		
		//backward
		for (int i = upper.length() ; i >= 0; --i) {
			for (int j = lower.length(); j >= 0; --j) {
				int contextId = contexts[instanceId][i][j];
				if (j == lower.length()) {
					betas[i][j] = Numerics.sumLogProb(betas[i][j], betas[i+1][j] + Math.log(distribution[contextId][2][0]));
					continue;
				}	
				int outputId = this.alphabet.get(lower.charAt(j));

				// ins 
				betas[i][j] = Numerics.sumLogProb(betas[i][j], betas[i][j+1] + Math.log(distribution[contextId][0][outputId]));
				// sub
				betas[i][j] = Numerics.sumLogProb(betas[i][j], betas[i+1][j+1] + Math.log(distribution[contextId][1][outputId]));
				// del
				betas[i][j] = Numerics.sumLogProb(betas[i][j], betas[i+1][j] + Math.log(distribution[contextId][2][0]));
				
				
			}
		}

		
		double Z = Math.exp(betas[0][0]);

		//forward 
		for (int i = 0; i < upper.length() + 1; ++i) {
			for (int j = 0; j < lower.length() + 1 ; ++j ) {
				int contextId = contexts[instanceId][i][j]; 	
				//gradient observed counts
				// ins 
				if (j < lower.length()) {
					int outputId = this.alphabet.get(lower.charAt(j));
					gradient[contextId][0][outputId] += Math.exp(alphas[i][j]) * distribution[contextId][0][outputId]  / Z *  Math.exp(betas[i][j+1]);
					alphas[i][j+1] = Numerics.sumLogProb(alphas[i][j+1],alphas[i][j] + Math.log(distribution[contextId][0][outputId]));				

				}
				// sub
				if (j < lower.length() && i < upper.length()) {
					int outputId = this.alphabet.get(lower.charAt(j));
					gradient[contextId][1][outputId] += Math.exp(alphas[i][j]) * distribution[contextId][1][outputId] / Z *  Math.exp(betas[i+1][j+1]);
					alphas[i+1][j+1] = Numerics.sumLogProb(alphas[i+1][j+1],alphas[i][j] + Math.log(distribution[contextId][1][outputId]));
				}
				
				// del
				if (i < upper.length()) {
					gradient[contextId][2][0] += Math.exp(alphas[i][j]) * distribution[contextId][2][0] / Z *  Math.exp(betas[i+1][j]);
					alphas[i+1][j] = Numerics.sumLogProb(alphas[i+1][j],alphas[i][j] + Math.log(distribution[contextId][2][0]));
				}
				
				//extract the context counts
				if (j < lower.length() || i < upper.length()) {
					contextCounts[contextId] += Math.exp(alphas[i][j]) * Math.exp(betas[i][j]) / Z;		
				}
			}
		}
		
		// gradient expected counts
		for (int contextId = 0; contextId < gradient.length; ++contextId) {
			// ins 
			for (int symbol = 0; symbol < gradient[contextId][0].length; ++symbol) {
				gradient[contextId][0][symbol] -=  contextCounts[contextId] * this.distribution[contextId][0][symbol];
			}
			// sub 
			for (int symbol = 0; symbol < gradient[contextId][1].length; ++symbol) {
				gradient[contextId][1][symbol] -=  contextCounts[contextId] * this.distribution[contextId][1][symbol];
			}
			// del
			gradient[contextId][2][0] -=  contextCounts[contextId] * this.distribution[contextId][2][0];
		}
		
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
		
		for (int i = upper.length() ; i >= 0; --i) {
			for (int j = lower.length(); j >= 0; --j) {
				int contextId = contexts[instanceId][i][j];
				if (j == lower.length()) {
					betas[i][j] = Numerics.sumLogProb(betas[i][j], betas[i+1][j] + Math.log(distribution[contextId][2][0]));
					continue;
				}
				
			
				int outputId = this.alphabet.get(lower.charAt(j));

				// ins 
				betas[i][j] = Numerics.sumLogProb(betas[i][j], betas[i][j+1] + Math.log(distribution[contextId][0][outputId]));
				// sub
				betas[i][j] = Numerics.sumLogProb(betas[i][j], betas[i+1][j+1] + Math.log(distribution[contextId][1][outputId]));
				// del
				betas[i][j] = Numerics.sumLogProb(betas[i][j], betas[i+1][j] + Math.log(distribution[contextId][2][0]));
				
				
			}
		}
		//System.out.println("LIKELIIHOOD");
		//System.out.println(betas[0][0]);
		return betas[0][0];
	}

	private void renormalizeAll() {
		for (int i = 0; i < this.distribution.length; ++i) {
			double Z = 0.0;
			for (int j = 0; j < this.distribution[i].length - 1; ++j) {
				for (int k = 0; k < this.distribution[i][j].length; ++k) {
					distribution[i][j][k] = Math.exp(this.weights[i][j][k]);
					Z += distribution[i][j][k];
				}
			}
			distribution[i][2][0] = Math.exp(this.weights[i][2][0]);
			Z += distribution[i][2][0];
			for (int j = 0; j <  this.distribution[i].length - 1; ++j) {
				for (int k = 0; k < this.distribution[i][j].length; ++k) {
					distribution[i][j][k] /= Z;
				}
			}
			distribution[i][2][0] /= Z;
		
		}
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
		
		// FOR DEBUGGING
		
		this.alphabet = new HashMap<Character,Integer>();
		this.alphabet.put('$',0);
		this.alphabet.put('a',1);
		this.alphabet.put('b',2);
		this.alphabet.put('c',3);
		this.alphabet.put('d',4);
		this.alphabet.put('e',5);
		this.alphabet.put('f',5);
		/*
		this.alphabet.put('e',2);
		this.alphabet.put('i',3);
		this.alphabet.put('n',4);
		*/
		this.weights = new double[result.getValue1()][3][this.alphabet.size()];
		this.distribution = new double[result.getValue1()][3][this.alphabet.size()];
		double[][][] gradientVector = new double[result.getValue1()][3][this.alphabet.size()];
		double[][][] approxGradientVector = new double[result.getValue1()][3][this.alphabet.size()];

		
		Random rand = new Random();
		for (int i = 0; i < result.getValue1(); ++i) {
			for (int j = 0; j < 3; ++j) {
				for (int k= 0; k < this.alphabet.size(); ++k) {
					this.weights[i][j][k] += rand.nextGaussian();
				}
			}
		}
		
		this.alphas = new double[max1][max2];
		this.betas = new double[max1][max2];
		
		zeroOut(alphas);
		zeroOut(betas);
	
		this.gradient(gradientVector,5);
		
		double eps = 0.01;
		for (int i = 0; i < result.getValue1(); ++i) {
			for (int j = 0; j < 2; ++j) {
				for (int k= 0; k < this.alphabet.size(); ++k) {
					this.weights[i][j][k] += eps;
					renormalizeAll();
					double val1 = logLikelihood(5);
					this.weights[i][j][k] -= 2 * eps;
					renormalizeAll();
					double val2 = logLikelihood(5);
					
					this.weights[i][j][k] += eps;
					
					approxGradientVector[i][j][k] = (val1 - val2) / (2 * eps);

				}
			}
			this.weights[i][2][0] +=  eps;
			renormalizeAll();
			double val1 = logLikelihood(5);
			this.weights[i][2][0] -= 2 * eps;
			renormalizeAll();
			double val2 = logLikelihood(5);
			this.weights[i][2][0] +=  eps;
			approxGradientVector[i][2][0] = (val1 - val2) / (2 * eps);

			


		}
		/*
		System.out.println("Gradient");
		System.out.println(Arrays.deepToString(gradientVector));
		System.out.println("Approximation");
		System.out.println(Arrays.deepToString(approxGradientVector));
		*/
		System.out.println(Numerics.approximatelyEqual(gradientVector, approxGradientVector, 0.001));

		return new LemmatizerPFST();
	}
	
	

	

}
