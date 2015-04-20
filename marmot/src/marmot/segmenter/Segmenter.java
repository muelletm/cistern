package marmot.segmenter;

import java.util.Arrays;

public class Segmenter {
	
	private double[][] alphas;
	private double[][] betas;
	private int numTags;
	
	public Segmenter(int maxI, int numTags) {
		this.alphas = new double[maxI][this.numTags];
		this.betas = new double[maxI][this.numTags];
		this.numTags = numTags;
	}
	
	public void backward(Word word, double[] weights, int[][] tagtag2int, int[][] tagseg2int) {
		Arrays.fill(betas, 0.0);
		betas[word.getLength() + 1][word.getLength()+1] = 1.0;
		
		double transitionScore = 0.0;
		double emissionScore = 0.0;
		for (int q1 = 0; q1 < this.numTags; ++q1) {
			for (int q2 = 0; q2 < this.numTags; ++q2) {
				// compute transition features
				transitionScore = weights[tagtag2int[q1][q2]];
				
				
				for (int i = word.getLength() + 1; i >=0; --i) {
					for (int j = i - 1; j >=0; --j) {
						// extract scores
						emissionScore = tagseg2int[q1][i];
					}
				}
			}
		}
	}
	
	public void gradient(Word word) {
		Arrays.fill(alphas,0.0);	
		alphas[0][0] = 1.0;
	}
	
	public void logLikelhood(Word word) {
		
	}
}
