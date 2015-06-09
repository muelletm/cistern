package marmot.lemma.transducer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.javatuples.Pair;

import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

import marmot.lemma.LemmaInstance;
import marmot.lemma.Lemmatizer;
import marmot.lemma.LemmaOptions;
import marmot.lemma.transducer.exceptions.LabelBiasException;
import marmot.lemma.transducer.exceptions.NegativeContext;
import marmot.util.Numerics;
import mikera.arrayz.NDArray;

public class WFST extends Transducer {

	private static final Logger LOGGER = Logger.getLogger(PFST.class.getName());

	private int[][] upperContexts;
	private int insertionLimit;
	private int keyDimension; // lookup dimensions
	private NDArray alphasExpected;
	private NDArray betasExpected;

	private HashSet<Integer> internedAlphabet;
	
	public WFST() throws NegativeContext, LabelBiasException {
		this(null,0,1,1,1,0);
	}
	
	public WFST(Map<Character,Integer> alphabet, int c1, int c2, int c3, int c4, int insertionLimit) throws NegativeContext {
		super(alphabet,c1,c2,c3,c4);
		this.insertionLimit = insertionLimit;
	}

	
	protected void observedCounts(double[][][] gradient, int instanceId) {
		// get data instance
		LemmaInstance instance = this.trainingData.get(instanceId);
		String upper = instance.getForm();
		String lower = instance.getLemma();
				
		LOGGER.info("Starting observed computation for pair (" + upper + "," + lower + ")...");
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
					//betas[i][j] = Numerics.sumLogProb(betas[i][j], betas[i+1][j] + Math.log(distribution[contextId][2][0]));
				}
				// ins
				if (j < lower.length()) {
					int outputId = this.alphabet.get(lower.charAt(j));
					//betas[i][j] = Numerics.sumLogProb(betas[i][j], betas[i][j+1] + Math.log(distribution[contextId][0][outputId]));
				}
				// sub
				if (i < upper.length() && j < lower.length()) {
					int outputId = this.alphabet.get(lower.charAt(j));
					//betas[i][j] = Numerics.sumLogProb(betas[i][j], betas[i+1][j+1] + Math.log(distribution[contextId][1][outputId]));
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
					//gradient[contextId][0][outputId] += thisAlpha * distribution[contextId][0][outputId]  / Z *  Math.exp(betas[i][j+1]);
					//alphas[i][j+1] = Numerics.sumLogProb(alphas[i][j+1],alphas[i][j] + Math.log(distribution[contextId][0][outputId]));				

				}
				// sub
				if (j < lower.length() && i < upper.length()) {
					int outputId = this.alphabet.get(lower.charAt(j));
					//gradient[contextId][1][outputId] += thisAlpha * distribution[contextId][1][outputId] / Z *  Math.exp(betas[i+1][j+1]);
					//alphas[i+1][j+1] = Numerics.sumLogProb(alphas[i+1][j+1],alphas[i][j] + Math.log(distribution[contextId][1][outputId]));
				}
				
				// del
				if (i < upper.length()) {
					//gradient[contextId][2][0] += thisAlpha * distribution[contextId][2][0] / Z *  Math.exp(betas[i+1][j]);
					//alphas[i+1][j] = Numerics.sumLogProb(alphas[i+1][j],alphas[i][j] + Math.log(distribution[contextId][2][0]));
				}
			}
		}
	}
	
	protected void expectedCounts(double[][][] gradient, int instanceId) {
		// get data instance
		LemmaInstance instance = this.trainingData.get(instanceId);
		String upper = instance.getForm();
		String lower = instance.getLemma();
				
		LOGGER.info("Starting expected count computation for pair (" + upper + "," + lower + ")...");
		//zero out the relevant positions in the log-semiring
		
		// TODO ZERO OUT ALPHAS EXPECTED
		//zeroOut(alphas,upper.length()+1, lower.length()+1);
		//zeroOut(betas,upper.length()+1, lower.length()+1);
		
		//make the start position unity in the log-semiring
		// TODO Initialize
		//alphasExpected[0][0] = 0.0;
		//betas[upper.length()][lower.length()] = 0.0;
		
		//INITILIZATION
		List<Set<Integer>> cartesianProductArgs = new ArrayList<Set<Integer>>();
		for (int i = 0; i < this.c4; ++i) {	
			cartesianProductArgs.add(this.internedAlphabet);
		}
		int[] key = new int[this.keyDimension];
		key[0] = 0; // first position of upper word
		key[1] = 0; // first position of outer word
		for (int c = 0; c < c3; ++c) {
			key[c + 2] = 0; // c3 position
		}
		for (List<Integer> product : Sets.cartesianProduct(cartesianProductArgs)) {
			int counter = 2;
			for (Integer c : product) {
				int charId = c;
				key[counter] = charId;
				counter += 1;
			}
			System.out.println(Arrays.toString(key));
			this.alphasExpected.set(key, 1.0);
		}
		
		// arguments for the Cartesian product
		List<Set<Integer>> cartesianProductArgsC3 = new ArrayList<Set<Integer>>();
		List<Set<Integer>> cartesianProductArgsC4 = new ArrayList<Set<Integer>>();

		for (int i = 0; i < this.c3 ; ++i) {	
			cartesianProductArgsC3.add(this.internedAlphabet);
		}

		for (int i = 0; i < this.c4; ++i) {	
			cartesianProductArgsC4.add(this.internedAlphabet);
		}

		List<Integer> contextKey = new ArrayList<Integer>();
		List<Integer> contextKeyStart = new ArrayList<Integer>();
		List<Integer> contextKeyInsEnd = new ArrayList<Integer>();
		List<Integer> contextKeyDelEnd = new ArrayList<Integer>();
		List<Integer> contextKeySubEnd = new ArrayList<Integer>();
		
		double prevAlpha;
		double prevVal;
		
		//forward
		for (int i = 0; i < upper.length() + 1; ++i) {
			for (int j = 0; j <  lower.length() + 1 + this.insertionLimit; ++j) {	
				// all combinations of c3 + c4 characters
				for (List<Integer> productC3 : Sets.cartesianProduct(cartesianProductArgsC3)) {
					// check if valid
					for (List<Integer> productC4 : Sets.cartesianProduct(cartesianProductArgsC4)) {
						//check if valid
						
						contextKey = new ArrayList<Integer>();
						contextKeyStart = new ArrayList<Integer>();
						contextKeyDelEnd = new ArrayList<Integer>();
						
						// starting position
						contextKeyStart.add(i);
						contextKeyStart.add(j);
						
						// advance one on the output
						contextKeyDelEnd.add(i+1);
						contextKeyDelEnd.add(j);
					

						if (this.c3 > 0 && this.c4 > 0) {
							contextKey.addAll(productC3);
							contextKey.addAll(productC4);
						}
						else if (this.c3 > 0) {
							contextKey.addAll(productC3);
						}
						else if (this.c4 > 0) {
							contextKey.addAll(productC4);
						}
						

						if (contextKey.size() > 0) {
							contextKeyStart.addAll(contextKey.subList(0, contextKey.size()));
							contextKeyDelEnd.addAll(contextKey.subList(0, contextKey.size()));
						}
						
						
						// del
						if (i < upper.length()) {
							//gradient[contextId][2][0] += thisAlpha * distribution[contextId][2][0] / Z *  Math.exp(betas[i+1][j]);
							//alphas[i+1][j] = Numerics.sumLogProb(alphas[i+1][j],alphas[i][j] + Math.log(distribution[contextId][2][0]));
														prevAlpha = this.alphasExpected.get(Ints.toArray(contextKeyStart));
							prevVal = this.alphasExpected.get(Ints.toArray(contextKeyDelEnd));
									
							//this.alphasExpected.set(Ints.toArray(contextKeyDelEnd), prevVal + prevAlpha * 1.0);
						}
						
						for (int cur = 1; cur < this.internedAlphabet.size(); ++cur) {
						
							contextKey = new ArrayList<Integer>();
							contextKeyStart = new ArrayList<Integer>();
							contextKeyInsEnd = new ArrayList<Integer>();
							contextKeySubEnd = new ArrayList<Integer>();
							
							// starting position
							contextKeyStart.add(i);
							contextKeyStart.add(j);
							// advance one on the input
							contextKeyInsEnd.add(i);
							contextKeyInsEnd.add(j+1);
							// advance one on the output
							contextKeyDelEnd.add(i+1);
							contextKeyDelEnd.add(j);
							// advance one on the input and on the output
							contextKeySubEnd.add(i+1);
							contextKeySubEnd.add(j+1);
							
							contextKey = new ArrayList<Integer>();
							// get the proper context key
							
							if (this.c3 > 0 && this.c4 > 0) {
								contextKey.addAll(productC3);
								contextKey.add(cur);
								contextKey.addAll(productC4);
							}
							else if (this.c3 > 0) {
								contextKey.addAll(productC3);
								contextKey.add(cur);
							}
							else if (this.c4 > 0) {
								contextKey.add(cur);
								contextKey.addAll(productC4);
							}
							
							if (contextKey.size() > 0) {
								contextKeyStart.addAll(contextKey.subList(0, contextKey.size()-1));
								contextKeyDelEnd.addAll(contextKey.subList(0, contextKey.size()-1));
							}
							
							if (contextKey.size() > 1) {
								contextKeyInsEnd.addAll(contextKey.subList(1, contextKey.size()));
								contextKeySubEnd.addAll(contextKey.subList(1, contextKey.size()));
							
							}
							//sub
							if (j < lower.length() + this.insertionLimit && i < upper.length()) {
								System.out.println("SUB START");
								System.out.println(contextKeyStart);
								prevAlpha = this.alphasExpected.get(Ints.toArray(contextKeyStart));
								prevVal = this.alphasExpected.get(Ints.toArray(contextKeySubEnd));
								this.alphasExpected.set(Ints.toArray(contextKeySubEnd), prevVal + prevAlpha * 1.0);
		
							}
							//ins 
							if (j < lower.length() + this.insertionLimit) {
								//int outputId = this.alphabet.get(lower.charAt(j));
								//alphas[i][j+1] = Numerics.sumLogProb(alphas[i][j+1],alphas[i][j] + Math.log(distribution[contextId][0][outputId]));				
		
								
								// a += a * 1.0
		
								prevAlpha = this.alphasExpected.get(Ints.toArray(contextKeyStart));
								prevVal = this.alphasExpected.get(Ints.toArray(contextKeyInsEnd));
								//this.alphasExpected.set(Ints.toArray(contextKeyInsEnd), prevVal + prevAlpha * 1.0);
	
								
							}
							
				
							
	
					}
						
					
				}
				
					/*
					for (int last = 1; last < this.alphabet.size(); ++last) {
					
						//key2[2] = last;
						// ins 
						if (j < lower.length() + this.insertionLimit) {
							//int outputId = this.alphabet.get(lower.charAt(j));
							//alphas[i][j+1] = Numerics.sumLogProb(alphas[i][j+1],alphas[i][j] + Math.log(distribution[contextId][0][outputId]));				
	
							key1[0] = i;
							key1[1] = j;
							key2[0] = i;
							key2[1] = j + 1;
							// a += a * 1.0
	
							double prevAlpha = this.alphasExpected.get(key1);
							double prevVal = this.alphasExpected.get(key2);
							this.alphasExpected.set(key2, prevVal + prevAlpha * 1.0);
	
							
						}
						// sub
						if (j < lower.length() + this.insertionLimit && i < upper.length()) {
							//	int outputId = this.alphabet.get(lower.charAt(j));
							//gradient[contextId][1][outputId] += thisAlpha * distribution[contextId][1][outputId] / Z *  Math.exp(betas[i+1][j+1]);
							//alphas[i+1][j+1] = Numerics.sumLogProb(alphas[i+1][j+1],alphas[i][j] + Math.log(distribution[contextId][1][outputId]));
							
							key1[0] = i;
							key1[1] = j;
							key2[0] = i + 1;
							key2[1] = j + 1;
							// a += a * 1.0
	
							System.out.println("KEY!");
							System.out.println(Arrays.toString(key1));
							System.out.println(Arrays.toString(key2));

							double prevAlpha = this.alphasExpected.get(key1);
							double prevVal = this.alphasExpected.get(key2);
							this.alphasExpected.set(key2, prevVal + prevAlpha * 1.0);
	
						}
					}
					*/
					
				
					
				}
			}
		}
		
		System.out.println(this.keyDimension);
		//System.out.println(this.alphasExpected.dimensionality());
		System.out.println(this.alphasExpected.toStringFull());
		System.exit(0);

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
					//gradient[contextId][0][outputId] += thisAlpha * distribution[contextId][0][outputId]  / Z *  Math.exp(betas[i][j+1]);
					//alphas[i][j+1] = Numerics.sumLogProb(alphas[i][j+1],alphas[i][j] + Math.log(distribution[contextId][0][outputId]));				

				}
				// sub
				if (j < lower.length() && i < upper.length()) {
					int outputId = this.alphabet.get(lower.charAt(j));
					//gradient[contextId][1][outputId] += thisAlpha * distribution[contextId][1][outputId] / Z *  Math.exp(betas[i+1][j+1]);
					//alphas[i+1][j+1] = Numerics.sumLogProb(alphas[i+1][j+1],alphas[i][j] + Math.log(distribution[contextId][1][outputId]));
				}
				
				// del
				if (i < upper.length()) {
					//gradient[contextId][2][0] += thisAlpha * distribution[contextId][2][0] / Z *  Math.exp(betas[i+1][j]);
					//alphas[i+1][j] = Numerics.sumLogProb(alphas[i+1][j],alphas[i][j] + Math.log(distribution[contextId][2][0]));
				}
			}
		}
	}
	
	@Override
	protected void gradient(double[][][] gradient) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	protected void gradient(double[][][] gradient, int i) {
		expectedCounts(gradient,i);
		
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
	public Lemmatizer train(List<LemmaInstance> instances,
			List<LemmaInstance> dev_instances) {
		
		LOGGER.info("Instantiating WFST...");
		
		this.trainingData = instances;
		this.devData = dev_instances;
		
		Pair<int[][][],Integer> result = preextractContexts(instances,this.c1,this.c2, this.c3, this.c4);
		Pair<int[][],Integer> resultUpper = preextractUpperContexts(instances,this.c1,this.c2);
		this.upperContexts = resultUpper.getValue0();
		int numUpperContexts = resultUpper.getValue1();
		
		this.contexts = result.getValue0();
			
		// get maximum input and output strings sizes
		this.alphabet = new HashMap<Character,Integer>();
		Pair<Integer,Integer> maxes = extractAlphabet();
		
		// weights and gradients
		this.weights = new double[result.getValue1()][3][this.alphabet.size()];
		
		double[][][] gradientVector = new double[result.getValue1()][3][this.alphabet.size()];
		double[][][] approxGradientVector = new double[result.getValue1()][3][this.alphabet.size()];

		
		// FOR DEBUGGING
				
		this.alphabet = new HashMap<Character,Integer>();
		this.alphabet.put('$',0);
		this.alphabet.put('a',1);
		//this.alphabet.put('b',2);
		/*
		this.alphabet.put('r',3);
		this.alphabet.put('o',4);
	    this.alphabet.put('t',5);
	    this.alphabet.put('f',6);
		*/
		
		this.internedAlphabet = new HashSet<Integer>();
		
		for (int a = 0; a < this.alphabet.size(); ++a) {
			this.internedAlphabet.add(a);
		}
		//randomlyInitWeights();
		
		this.alphas = new double[maxes.getValue0()][maxes.getValue1()];
		this.betas = new double[maxes.getValue0()][maxes.getValue1()];
		
		zeroOut(alphas);
		zeroOut(betas);
		
		this.keyDimension = 2 + this.c3 + this.c4 ;
		int[] dimensions = new int[this.keyDimension];
		
		dimensions[0] = maxes.getValue0() + 1; // UPPER CONTEXT
		dimensions[1] = maxes.getValue1() + 1 + this.insertionLimit;

		dimensions[0] = 2; // UPPER CONTEXT
		dimensions[1] = 2 + this.insertionLimit;
		
		for (int c3c4 = 0; c3c4 < this.c3 + this.c4; ++c3c4) {
			dimensions[c3c4 + 2] = this.alphabet.size();
		}
		
		System.out.println("DIMENSIONS");
		System.out.println(Arrays.toString(dimensions));
		this.alphasExpected = NDArray.newArray(dimensions);
		this.betasExpected = NDArray.newArray(dimensions);
		
		this.expectedCounts(gradientVector, 0);
		System.exit(0);
		
		System.out.println(this.betasExpected.get(new int[] {0, 0, 0, 0 } ));
		System.out.println(Arrays.toString(this.betasExpected.getShape()));
		System.exit(0);
		this.gradient(gradientVector,5);
		
		return new LemmatizerWFST();
		
		
	}

	@Override
	public LemmaOptions getOptions() {
		return null;
	}

}
