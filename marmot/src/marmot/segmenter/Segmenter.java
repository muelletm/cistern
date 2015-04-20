package marmot.segmenter;

import java.util.Arrays;

import marmot.util.Numerics;

public class Segmenter {
	
	private double[][] alphas;
	private double[][] alphasTrans;
	private double[][] betas;
	private int numTags;
	private int[][] tagtag2int;
	private int[][] tagseg2int;
	
	public Segmenter(int maxLength, int numTags, int[][] tagtag2int, int[][] tagseg2int) {
		this.numTags = 2; //numTags;
		this.alphas = new double[maxLength+2][this.numTags];
		this.alphasTrans = new double[maxLength+2][this.numTags];

		this.betas = new double[maxLength+2][this.numTags];
		
		this.tagtag2int = tagtag2int;
		this.tagseg2int = tagseg2int;
	}
	
	public void backward(Word word, double[] weights) {
		for (int index = 0; index < betas.length; ++index) {
			Arrays.fill(betas[index], Double.NEGATIVE_INFINITY);
		}
		int[][] segment2Id = word.getSegment2Id();
		double transitionScore = 0.0;
		double emissionScore = 0.0;

		// END
		betas[word.getLength()+1][0] = 0.0;

		//MIDDLE
		for (int q1 = 0; q1 < this.numTags; ++q1) {
			for (int q2 = 0; q2 < this.numTags; ++q2) {
				// compute transition features
				transitionScore = weights[tagtag2int[q2][q1]];
				for (int i = word.getLength() + 1; i > 0; --i) {
					for (int j = i - 1; j > 0; --j) {
						//emissionScore = weights[tagseg2int[q1][segment2Id[j][i]]];
						
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
		double emissionScore = 0.0;
		double marginalTransitionProbability = 0.0;
		double marginalEmissionProbability = 0.0;

		double Z = betas[0][0];
		// 	MIDDLE
		for (int i = 0; i < word.getLength() + 1; ++i) {
			for (int j = i + 1; j < word.getLength() + 2; ++j) {
				for (int q1 = 0; q1 < this.numTags; ++q1) {
					for (int q2 = 0; q2 < this.numTags; ++q2) {
						transitionScore = weights[tagtag2int[q1][q2]];
						
						//emissionScore = weights[tagseg2int[q2][segment2Id[i][j]]];
						
						alphas[j][q2] = Numerics.sumLogProb(alphas[j][q2], alphas[i][q1] + transitionScore + emissionScore);
						// transition features
						if (j == i + 1) {
							this.alphasTrans[j][q2] = Numerics.sumLogProb(alphasTrans[j][q1], alphas[i][q1] + transitionScore);
							
							marginalTransitionProbability = Math.exp(alphas[i][q1] + betas[j][q2] + transitionScore - Z);
							gradient[tagtag2int[q1][q2]] += marginalTransitionProbability;
						}
						else if (i > 0) {
							marginalEmissionProbability = Math.exp(alphasTrans[i][q1] + betas[j][q2] + transitionScore + emissionScore - Z);
							//gradient[tagseg2int[q1][q2]] += marginalTransitionProbability;
						}
					}
				}
			}
		}
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
