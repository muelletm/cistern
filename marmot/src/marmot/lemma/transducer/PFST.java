package marmot.lemma.transducer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;
import java.util.Timer;

import org.javatuples.Pair;

import com.google.common.collect.Sets;

import marmot.lemma.LemmaInstance;
import marmot.lemma.Lemmatizer;
import marmot.lemma.LemmaOptions;
import marmot.lemma.transducer.exceptions.LabelBiasException;
import marmot.lemma.transducer.exceptions.NegativeContext;
import marmot.util.Numerics;



public class PFST extends Transducer {

	// logger
    private static final Logger LOGGER = Logger.getLogger(PFST.class.getName());
    
    //distribution
    private double[][][] distribution;

	private Integer numContexts;

	private HashSet<Integer> internedAlphabet;

	private int[][] upperContexts;

	private int insertion_limit;

	/**
	 * Don't ever call
	 * @throws NegativeContext
	 * @throws LabelBiasException 
	 */
	public PFST() throws NegativeContext, LabelBiasException {
		this(null,6,6,0,0);
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
			for (double[][] matrix : gradientVector) {
				for (double[] row : matrix) {
					Arrays.fill(row,0.0);
				}
			}
			gradient(gradientVector);
			double norm = 0.0;

			for (int i = 0; i < gradientVector.length; ++i) {
				for (int j = 0; j < gradientVector[i].length; ++j) {
					for (int k = 0; k < gradientVector[i][j].length; ++k) {
						//System.out.println(i + " , " + j + " , " + k + " , " + " : " + gradientVector[i][j][k]);
						norm += Math.pow(gradientVector[i][j][k],2);
						this.weights[i][j][k] += learningRate *  gradientVector[i][j][k];
					}
				}
			}
			System.out.println("NORM:" + norm);
			System.out.println(this.logLikelihood());
			/*
			for (int instanceId = 0; instanceId < this.trainingData.size(); ++instanceId) {
				//System.out.println(instanceId);
				// fill up
				//System.out.println(gradientVector.length);
				for (double[][] matrix : gradientVector) {
					for (double[] row : matrix) {
						Arrays.fill(row,0.0);
					}
				}
				this.gradient(gradientVector,instanceId);
				double norm = 0.0;
				// update weight vector
				for (int i = 0; i < gradientVector.length; ++i) {
					for (int j = 0; j < gradientVector[i].length; ++j) {
						for (int k = 0; k < gradientVector[i][j].length; ++k) {
							//System.out.println(i + " , " + j + " , " + k + " , " + " : " + gradientVector[i][j][k]);
							norm += Math.pow(gradientVector[i][j][k],2);
							this.weights[i][j][k] += learningRate *  gradientVector[i][j][k];
						}
					}
				}
				System.out.println("NORM:" + norm);
				System.out.println(this.logLikelihood());

			}
			*/
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
		LemmaInstance instance = this.trainingData.get(instanceId);
		String upper = instance.getFormPadded();
		String lower = instance.getLemmaPadded();
				
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
		/*
		System.out.println("OBSERVED COUNTS");
		for (int i = 0; i < gradient.length; ++i) {
			for (int j = 0; j < gradient[i].length; ++j) {
				for (int k = 0; k < gradient[i][j].length; ++k) {
					System.out.println(i + " , " + j + " , " + k + " , " + " : " + gradient[i][j][k]);
				}
			}
		}
		System.exit(0);
		*/
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
		LemmaInstance instance = this.trainingData.get(instanceId);
		String upper = instance.getFormPadded();
		String lower = instance.getLemmaPadded();
		
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
	
	public void decode (List<LemmaInstance> instances) {
		//List<Set<Integer>> cartesianProductArgsC3 = new ArrayList<Set<Integer>>();
		int instanceId = 0;
		int correct = 0;
		for (LemmaInstance instance : instances) {

			String upper = instance.getFormPadded();
			String lower = instance.getLemmaPadded();
			
			double[][] gammas = new double[upper.length() + 1][upper.length() + 1 + this.insertion_limit];

			int[][] backpointers1 = new int[upper.length() + 1][upper.length() + 1 + this.insertion_limit];
			int[][] backpointers2 = new int[upper.length() + 1][upper.length() + 1 + this.insertion_limit];
			
			char[][] values = new char[upper.length() + 1][upper.length() + 1 + this.insertion_limit];

			for (int g = 0; g < gammas.length; ++g) {
				Arrays.fill(gammas[g], Double.NEGATIVE_INFINITY);
			}
			gammas[0][0] = 0.0;
			int maxFinalJ = -1;
			double maxFinalJValue = Double.NEGATIVE_INFINITY;
			
	
			//forward
			for (int i = 0; i < upper.length() + 1; ++i) {
				// insertion limit
				for (int j = 0; j <  upper.length() + 1 + this.insertion_limit; ++j) {	
					int contextId = contexts[instanceId][i][0];
					/*
					System.out.println(i + " , " + j);
					System.out.println(Arrays.toString(distribution[contextId][0]));
					System.out.println(Arrays.toString(distribution[contextId][1]));
					System.out.println(Arrays.toString(distribution[contextId][2]));
					
					System.out.println("");
					*/
					for (Map.Entry<Character, Integer>  entry: this.alphabet.entrySet()) {
						
						// ins
						if (j < upper.length() + this.insertion_limit) {
							int outputId = entry.getValue();
							double newValue = gammas[i][j] + Math.log(distribution[contextId][0][outputId]);
							//System.out.println(i + " , " + j + " : " + contextId);
							//System.out.println(newValue);
							//System.out.println(gammas[i][j]);
							
							if (newValue >= gammas[i][j+1]) {

								//System.out.println("HERE!");
								//System.out.println(i);
								//System.out.println(gammas[i][j+1]);
								
								gammas[i][j+1] = newValue;
								backpointers1[i][j+1] = i;
								backpointers2[i][j+1] = j;
								values[i][j+1] = entry.getKey();
								
								
							}
							
							if (i == upper.length() && maxFinalJValue <= newValue) {
								maxFinalJValue = newValue;
								maxFinalJ = j+1;
							}
							//gammas[i][j+1] = Math.max(gammas[i][j+1],gammas[i][j] + Math.log(distribution[contextId][0][outputId]));				
	
						}
						// sub
						if (j < upper.length() + this.insertion_limit && i < upper.length()) {
							int outputId = entry.getValue();
							double newValue = gammas[i][j] + Math.log(distribution[contextId][1][outputId]);
							
							if (newValue >= gammas[i+1][j+1]) {
								gammas[i+1][j+1] = newValue;
								backpointers1[i+1][j+1] = i;
								backpointers2[i+1][j+1] = j;
								values[i+1][j+1] = entry.getKey();
								
								

							}
							

							if (i + 1 == upper.length() && maxFinalJValue <= newValue) {
								maxFinalJValue = newValue;
								maxFinalJ = j+1;
							}
							//gammas[i+1][j+1] = Math.max(gammas[i+1][j+1],gammas[i][j] + Math.log(distribution[contextId][1][outputId]));
						}
					}
					// del
					if (i < upper.length()) {
						double newValue = gammas[i][j] + Math.log(distribution[contextId][2][0]);
						if (newValue >= gammas[i+1][j]) {
							gammas[i+1][j] = newValue;
							backpointers1[i+1][j] = i;
							backpointers2[i+1][j] = j;
							values[i+1][j] = '\0';
							
						}
							

						if (i + 1 == upper.length() && maxFinalJValue <= newValue) {
							maxFinalJValue = newValue;
							maxFinalJ = j;
						}
							//gammas[i+1][j] = Math.max(gammas[i+1][j],gammas[i][j] + Math.log(distribution[contextId][2][0]));
					}
					

				}
			}
		
			//System.out.println("MAX FINAL J: " + maxFinalJ);
			String s = "";
			int backpointerI = upper.length();
			int backpointerJ = maxFinalJ;
			int lastBackpointerJ = -1;
			while (backpointerI > 0 && backpointerJ > 0) {
				if (lastBackpointerJ != backpointerJ &&  values[backpointerI][backpointerJ] != '\0') {
					s = values[backpointerI][backpointerJ] + s;
				}
				int lastBackPointerJ = backpointerJ;
				backpointerJ = backpointers2[backpointerI][lastBackPointerJ];

				backpointerI = backpointers1[backpointerI][lastBackPointerJ];

			}

			
			if (s.equals(lower)) {
				correct += 1;
			} else {

				System.out.println(upper + "\t" + lower + "\t" + s + "\t" + Math.exp(this.logLikelihood(instanceId)));
				
			}
			++instanceId;
			
			
		}
		
		System.out.println("ACCURACY: " + correct + " - " +  instances.size());
	}
	@Override
	public Lemmatizer train(List<LemmaInstance> instances,
			List<LemmaInstance> dev_instances) {
		
		
		this.trainingData = new ArrayList<LemmaInstance>(instances);
		this.devData =  new ArrayList<LemmaInstance>(dev_instances);
		
		this.contextToCharacter = new HashMap<Integer,Character>();
		
		Pair<int[][][],Integer> result = preextractContexts(instances,this.c1,this.c2, this.c3, this.c4);
		this.contexts = result.getValue0();
		this.numContexts = result.getValue1();
			
		Pair<int[][],Integer> resultUpper = preextractUpperContexts(instances,this.c1,this.c2);
		this.upperContexts = resultUpper.getValue0();
		int numUpperContexts = resultUpper.getValue1();
		
		// get maximum input and output strings sizes
		this.alphabet = new HashMap<Character,Integer>();
		Pair<Integer,Integer> maxes = extractAlphabet();
			
		// FOR DEBUGGING
		/*
		this.alphabet = new HashMap<Character,Integer>();
		this.alphabet.put('v',0);
		this.alphabet.put('e',1);
		this.alphabet.put('r',2);
		this.alphabet.put('m',3);
		this.alphabet.put('u',4);
		this.alphabet.put('t',5);
		this.alphabet.put('n',6);

		this.alphabet.put('$',7);
		*/
		
		// weights and gradients
		
		this.internedAlphabet = new HashSet<Integer>();
		
		for (int a = 0; a < this.alphabet.size(); ++a) {
			this.internedAlphabet.add(a);
		}
		
		this.weights = new double[this.numContexts][3][this.alphabet.size()];
	
		this.distribution = new double[this.numContexts][3][this.alphabet.size()];
		double[][][] gradientVector = new double[this.numContexts][3][this.alphabet.size()];
		double[][][] approxGradientVector = new double[this.numContexts][3][this.alphabet.size()];

		//randomlyInitWeights();
		// most weight on substitution
		for (int context = 0; context < this.weights.length; ++context) {
			Character c = this.contextToCharacter.get(context);
			if (!this.alphabet.keySet().contains(c)) {
				continue;
			}
			int charId = this.alphabet.get(c);
			if (c == '$') {
				//this.weights[context][1][charId] = 100.0;

			} else {
				this.weights[context][1][charId] = 10.0;
			}
			
			for (int d = 0; d < this.weights[context][1].length; ++d) {
				if (d != charId) {
					//this.weights[context][1][d] = -5.0;
				}
			}
		}
		this.alphas = new double[maxes.getValue0()][maxes.getValue1()];
		this.betas = new double[maxes.getValue0()][maxes.getValue1()];
		this.insertion_limit = 5;
		
		zeroOut(alphas);
		zeroOut(betas);
		System.out.println(weights[0][2][0]);
		this.renormalizeAll();
		System.out.println("SGD START");
		
		sgd(1.0,.999,100);
		this.renormalizeAll();
		/*
		System.out.println(Arrays.toString(weights[6][0]));

		System.out.println(Arrays.toString(weights[6][1]));

		System.out.println(Arrays.toString(weights[6][2]));
		
		for (int i = 0; i < 1; ++i) {
			System.out.println("STEP:"+ i);
			System.out.println(Arrays.toString(distribution[i][0]));
			System.out.println(Arrays.toString(distribution[i][1]));

			System.out.println(Arrays.toString(distribution[i][2]));
		}
		System.out.println(logLikelihood());
		System.exit(0);
		*/
		/*System.out.println(distribution[5][2][0]);
		System.out.println(distribution[5][2][1]);
		System.out.println(distribution[5][2][2]);
		System.out.println(distribution[5][2][3]);

		System.exit(0);
		*/
		System.out.println("SGD DONE");
		this.decode(instances);
		System.exit(0);
		this.gradient(gradientVector,5);
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
		System.out.println(Arrays.deepToString(gradientVector));

		//System.out.println(Arrays.deepToString(approxGradientVector));
		System.out.println(Numerics.approximatelyEqual(gradientVector, approxGradientVector, 0.001));
		 
		return new LemmatizerPFST();
	}
	
	@Override
	public LemmaOptions getOptions() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

	

}
