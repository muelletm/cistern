package marmot.lemma.transducer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import java.util.Timer;

import org.javatuples.Pair;

import marmot.lemma.Instance;
import marmot.lemma.Lemmatizer;
import marmot.lemma.Options;
import marmot.lemma.transducer.exceptions.LabelBiasException;
import marmot.lemma.transducer.exceptions.NegativeContext;
import marmot.util.Numerics;



public class PFST extends Transducer {

	// logger
    private static final Logger LOGGER = Logger.getLogger(PFST.class.getName());
    
    //distribution
    private double[][][] distribution;

	private Integer numContexts;

	/**
	 * Don't ever call
	 * @throws NegativeContext
	 * @throws LabelBiasException 
	 */
	public PFST() throws NegativeContext, LabelBiasException {
		this(null,1,2,0,0);
	}
	public PFST(Map<Character,Integer> alphabet, int c1, int c2, int c3, int c4) throws LabelBiasException, NegativeContext {
		super(alphabet, c1, c2, c3, c4);
		if (c4 != 0) {
			throw new LabelBiasException();
		}
	}
	
	@Override
	protected void gradient(double gradient[][][] ) {
		// TODO we need selective renormalization to speed this up
		this.renormalizeAll();
		double[] contextCounts = new double[gradient.length];
		
		// computation of observed counts
		for (int instanceId = 0; instanceId < this.trainingData.size(); ++instanceId) {
			observedCounts(gradient,contextCounts,instanceId);
		}
		// note that the expected counts are computed *outside* a loop over
		// the data
		expectedCounts(gradient,contextCounts);
	}
	
	
	@Override
	protected void gradient(double gradient[][][], int instanceId ) {
		// TODO we need selective renormalization to speed this up
		this.renormalizeAll();
		double[] contextCounts = new double[gradient.length];
		observedCounts(gradient,contextCounts,instanceId);
		expectedCounts(gradient,contextCounts);
	}
	
	protected void sgd(double learningRate, double scaleFactor, int numIterations) {
		
		
		double[][][] gradientVector = new double[this.numContexts][3][this.alphabet.size()];
		//this.gradient(gradientVector);
		
		for (int iteration = 0; iteration < numIterations; ++iteration) {
			for (int instanceId = 0; instanceId < this.trainingData.size(); ++instanceId) {
				//System.out.println(instanceId);
				// fill up
				/*for (double[][] matrix : gradientVector) {
					for (double[] row : matrix) {
						Arrays.fill(row,0.0);
					}
				}*/
				this.gradient(gradientVector,instanceId);
				// update weight vector
				/*for (int i = 0; i < gradientVector.length; ++i) {
					for (int j = 0; j < gradientVector[i].length; ++j) {
						for (int k = 0; k < gradientVector[k].length; ++k) {
							this.weights[i][j][k] += learningRate *  gradientVector[i][j][k];
						}
					}
				}*/
				
				//System.out.println(this.logLikelihood());

			}

			learningRate *= scaleFactor;
		}
		
	}
	
	protected void expectedCounts(double[][][] gradient , double[] contextCounts) {
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
	 
	protected void observedCounts(double[][][] gradient, double[] contextCounts, int instanceId) {
		// get data instance
		Instance instance = this.trainingData.get(instanceId);
		String upper = instance.getForm();
		String lower = instance.getLemma();
				
		//LOGGER.info("Starting observed count computation for pair (" + upper + "," + lower + ")...");
		//zero out the relevant positions in the log-semiring
		zeroOut(alphas,upper.length()+1, lower.length()+1);
		zeroOut(betas,upper.length()+1, lower.length()+1);
		
		//make the start position unity in the log-semiring
		alphas[0][0] = 0.0;
		betas[upper.length()][lower.length()] = 0.0;
		
		//backward
		for (int i = upper.length() ; i >= 0; --i) {
			for (int j = lower.length(); j >= 0; --j) {
				int contextId = contexts[instanceId][i][j];
				// del 
				if (i < upper.length()) {
					betas[i][j] = Numerics.sumLogProb(betas[i][j], betas[i+1][j] + Math.log(distribution[contextId][2][0]));
				}
				// ins
				if (j < lower.length()) {
					int outputId = this.alphabet.get(lower.charAt(j));
					betas[i][j] = Numerics.sumLogProb(betas[i][j], betas[i][j+1] + Math.log(distribution[contextId][0][outputId]));
				}
				// sub
				if (i < upper.length() && j < lower.length()) {
					int outputId = this.alphabet.get(lower.charAt(j));
					betas[i][j] = Numerics.sumLogProb(betas[i][j], betas[i+1][j+1] + Math.log(distribution[contextId][1][outputId]));
				}
			}
		}

		// partition function
		double Z = Math.exp(betas[0][0]);

		// forward 
		for (int i = 0; i < upper.length() + 1; ++i) {
			for (int j = 0; j < lower.length() + 1 ; ++j ) {
				int contextId = contexts[instanceId][i][j]; 
				
				//alpha updates and gradient observed counts
				double thisAlpha =  Math.exp(alphas[i][j]);
				// ins 
				if (j < lower.length()) {
					int outputId = this.alphabet.get(lower.charAt(j));
					gradient[contextId][0][outputId] += thisAlpha * distribution[contextId][0][outputId]  / Z *  Math.exp(betas[i][j+1]);
					alphas[i][j+1] = Numerics.sumLogProb(alphas[i][j+1],alphas[i][j] + Math.log(distribution[contextId][0][outputId]));				

				}
				// sub
				if (j < lower.length() && i < upper.length()) {
					int outputId = this.alphabet.get(lower.charAt(j));
					gradient[contextId][1][outputId] += thisAlpha * distribution[contextId][1][outputId] / Z *  Math.exp(betas[i+1][j+1]);
					alphas[i+1][j+1] = Numerics.sumLogProb(alphas[i+1][j+1],alphas[i][j] + Math.log(distribution[contextId][1][outputId]));
				}
				
				// del
				if (i < upper.length()) {
					gradient[contextId][2][0] += thisAlpha * distribution[contextId][2][0] / Z *  Math.exp(betas[i+1][j]);
					alphas[i+1][j] = Numerics.sumLogProb(alphas[i+1][j],alphas[i][j] + Math.log(distribution[contextId][2][0]));
				}
				
				//extract the context counts
				if (j < lower.length() || i < upper.length()) {
					contextCounts[contextId] += thisAlpha * Math.exp(betas[i][j]) / Z;		
				}
			}
		}
	}
	
	@Override
	protected double logLikelihood() {
		double logLikelihood = 0.0;
		for (int instanceId = 0; instanceId < this.trainingData.size(); ++instanceId) {
			logLikelihood += this.logLikelihood(instanceId);
		}
		return logLikelihood;
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
		for (int i = upper.length() ; i >= 0; --i) {
			for (int j = lower.length(); j >= 0; --j) {
				int contextId = contexts[instanceId][i][j];
				// del 
				if (i < upper.length()) {
					betas[i][j] = Numerics.sumLogProb(betas[i][j], betas[i+1][j] + Math.log(distribution[contextId][2][0]));
				}
				// ins
				if (j < lower.length()) {
					int outputId = this.alphabet.get(lower.charAt(j));
					betas[i][j] = Numerics.sumLogProb(betas[i][j], betas[i][j+1] + Math.log(distribution[contextId][0][outputId]));
				}
				// sub
				if (i < upper.length() && j < lower.length()) {
					int outputId = this.alphabet.get(lower.charAt(j));
					betas[i][j] = Numerics.sumLogProb(betas[i][j], betas[i+1][j+1] + Math.log(distribution[contextId][1][outputId]));
				}
			}
		}
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
		
		this.trainingData = new ArrayList<Instance>(instances);
		this.devData =  new ArrayList<Instance>(dev_instances);
		
		Pair<int[][][],Integer> result = preextractContexts(instances,this.c1,this.c2, this.c3, this.c4);
		this.contexts = result.getValue0();
		this.numContexts = result.getValue1();
			
		// get maximum input and output strings sizes
		this.alphabet = new HashMap<Character,Integer>();
		Pair<Integer,Integer> maxes = extractAlphabet();

		
		// FOR DEBUGGING
		/*
		this.alphabet = new HashMap<Character,Integer>();
		this.alphabet.put('$',0);
		this.alphabet.put('p',1);
		this.alphabet.put('e',2);
		this.alphabet.put('r',3);
		this.alphabet.put('o',4);
		this.alphabet.put('t',5);
		this.alphabet.put('f',6);
		*/
		
		// weights and gradients
		this.weights = new double[this.numContexts][3][this.alphabet.size()];
		this.distribution = new double[this.numContexts][3][this.alphabet.size()];
		double[][][] gradientVector = new double[this.numContexts][3][this.alphabet.size()];
		double[][][] approxGradientVector = new double[this.numContexts][3][this.alphabet.size()];

		randomlyInitWeights();
		
		this.alphas = new double[maxes.getValue0()][maxes.getValue1()];
		this.betas = new double[maxes.getValue0()][maxes.getValue1()];
		
		zeroOut(alphas);
		zeroOut(betas);
	
		sgd(1.0,.99,1);

		/*
		// finite difference check
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
	
		System.out.println(Numerics.approximatelyEqual(gradientVector, approxGradientVector, 0.001));
		 */	
		return new LemmatizerPFST();
	}
	
	@Override
	public Options getOptions() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

	

}
