package marmot.segmenter;

import java.util.Arrays;
import java.util.Map;

import sun.tools.tree.ThisExpression;
import marmot.util.Numerics;

public class Segmenter {
	
	private double[][] alphas;
	private double[][] alphasTrans;
	private double[][] betas;
	private int numTags;
	private int[][] tagtag2int;
	private int[][] tagseg2int;
	private Map<String,Integer> seg2int;

	public Segmenter(int maxLength, int numTags, int[][] tagtag2int, int[][] tagseg2int, Map<String,Integer> seg2int) {
		this.numTags = 2; //numTags;
		this.alphas = new double[maxLength+2][this.numTags];
		this.alphasTrans = new double[maxLength+2][this.numTags];

		this.betas = new double[maxLength+2][this.numTags];
		
		this.tagtag2int = tagtag2int;
		this.tagseg2int = tagseg2int;
		
		this.seg2int = seg2int;
	}
	
	public void backward(Word word, double[] weights) {
		for (int index = 0; index < betas.length; ++index) {
			Arrays.fill(betas[index], Double.NEGATIVE_INFINITY);
		}
		int[][] segment2Id = word.getSegment2Id();
		double transitionScore = 0.0;
		double emissionScore = 0.0;
		double emissionScore2 = 0.0;


		// END
		betas[word.getLength()+1][0] = 0.0;

		//MIDDLE
		//weights[8] = Math.log(2.0);
		// compute transition features
		for (int i = word.getLength() + 1; i > 0; --i) {
			for (int j = i - 1; j > 0; --j) {
				for (int q1 = 0; q1 < this.numTags; ++q1) {
					for (int q2 = 0; q2 < this.numTags; ++q2) {
						emissionScore = weights[tagseg2int[q2][segment2Id[j][i]]];
						//System.out.println(word.getPaddedWord().substring(j,i));
						
						
						//System.out.println("CHAR:\t" + word.getPaddedWord().substring(j,i));
						/*
						if (emissionScore > 0) {
							System.out.println(segment2Id[j][i]);
						}
							*/
/*
						if (tagseg2int[q2][segment2Id[j-1][i-1]] == 5) {
							System.out.println("HERE!" + "\t" + q2 + "\t" + segment2Id[j-1][i-1]);
							System.out.println(i + k)
							System.out.println(emissionScore);
						}
						*/
						
						transitionScore = weights[tagtag2int[q2][q1]];
						betas[j][q2] = Numerics.sumLogProb(betas[j][q2], betas[i][q1] + transitionScore + emissionScore);
					}
				}
			}
		}
		// START
		for (int q = 0; q < this.numTags; ++q) {
			transitionScore = weights[tagtag2int[0][q]];
			betas[0][0] = Numerics.sumLogProb(betas[0][0], betas[1][q] + transitionScore);
		}

		for (int i = 0; i < betas.length; ++i) {
			for (int j = 0; j < betas[0].length; ++j) {
				//betas[i][j] = Math.exp(betas[i][j]);
			}
		}
		//System.out.println(Arrays.deepToString(betas));
		//System.exit(0);
	}
	
	public void expectedCounts(double[] gradient, Word word, double[] weights) {
		this.backward(word, weights);
		for (int index = 0; index < alphas.length; ++index) {
			Arrays.fill(alphas[index], Double.NEGATIVE_INFINITY);
			Arrays.fill(alphasTrans[index], Double.NEGATIVE_INFINITY);
		}
		
		alphas[0][0] = 0.0;
		alphasTrans[0][0] = 0.0;
		
		int [][]segment2Id = word.getSegment2Id();
		
		double transitionScore = 0.0;
		double transitionScore2 = 0.0;
		double emissionScore = 0.0;
		double emissionScore2 = 0.0;

		double marginalTransitionProbability = 0.0;
		double marginalEmissionProbability = 0.0;

		System.out.println(tagseg2int[1][segment2Id[1][4]]);
		System.out.println(segment2Id[1][4]);
		//System.exit(0);
		double Z = betas[0][0];
		// 	MIDDLE
		for (int i = 0; i < word.getLength() + 1; ++i) {
			for (int j = i + 1; j < word.getLength() + 2; ++j) {
				for (int q1 = 0; q1 < this.numTags; ++q1) {
					for (int q2 = 0; q2 < this.numTags; ++q2) {
						
						transitionScore = weights[tagtag2int[q2][q1]];
						transitionScore2 = weights[tagtag2int[q1][q2]];
						emissionScore = weights[tagseg2int[q1][segment2Id[i+1][j+1]]];	
						emissionScore2 = weights[tagseg2int[q1][segment2Id[i][j]]];	
						
						alphas[j][q1] = Numerics.sumLogProb(alphas[j][q1], alphas[i][q2] + transitionScore + emissionScore);
						if (j == i + 1) {

							this.alphasTrans[j][q1] = Numerics.sumLogProb(alphasTrans[j][q1], alphas[i][q2] + transitionScore);			
							marginalTransitionProbability = Math.exp(alphas[i][q2] + betas[j][q1] + transitionScore - Z);
							gradient[tagtag2int[q2][q1]] += marginalTransitionProbability;
						}
						if (i > 0)  {
							marginalEmissionProbability = Math.exp(alphasTrans[i][q1] + betas[j][q2] + emissionScore2 + transitionScore2 -  Z);
							gradient[tagseg2int[q1][segment2Id[i][j]]] += marginalEmissionProbability;
							
								
						}
					}
				}
			}
		}
		// END


	
		for (int i = 0; i < betas.length; ++i) {
			for (int j = 0; j < betas[0].length; ++j) {
				alphas[i][j] = Math.exp(alphas[i][j]);
				alphasTrans[i][j] = Math.exp(alphasTrans[i][j]);
				betas[i][j] = Math.exp(betas[i][j]);
			}
		}
		System.out.println(Arrays.deepToString(alphas));
		System.out.println(Arrays.deepToString(alphasTrans));

		System.out.println(Arrays.deepToString(betas));
		System.out.println(gradient.length);

		//System.exit(0);
		
		
		
	}
	
	public void gradient(Word word) {
		Arrays.fill(alphas,0.0);	
		alphas[0][0] = 1.0;
	}
	
	public double partitionFunction() {
		double paritionFunction = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < this.betas[0].length; ++i) {
			paritionFunction = Numerics.sumLogProb(paritionFunction, this.betas[0][i]);
		}
		return paritionFunction;
	}
	
	public double logLikelihood(Word word, double[] weights) {
		this.backward(word, weights);
		return this.partitionFunction();
	}
}
