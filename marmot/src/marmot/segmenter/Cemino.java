package marmot.segmenter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Cemino {
	
	private SegmentationDataReader sdr;
	private double[] weights;
	private double[] gradient;
	
	private Map<String,Integer> tag2int;
	private Map<String,Integer> seg2int;
	private Map<String,Integer> seg2count;
	
	private List<Word> trainingData;
	private List<Word> devData;
	private List<Word> testData;
	private int[][] tagtag2int;
	private int[][] tagseg2int;
	private int numTags;
	private int numSegs;
	private int maxLength;

	public Cemino(SegmentationDataReader sdr) {
		
		this.sdr = sdr;
		this.tag2int = sdr.getTag2int();
		this.seg2int = sdr.getSeg2int();
		this.seg2count = sdr.getSeg2count();
		
		this.trainingData = sdr.getTrainingData();
		this.devData = sdr.getDevData();
		this.testData = sdr.getTestData();
	
		this.maxLength = sdr.getMaxLength();
		this.numSegs = sdr.getNumSegs();
		this.numTags = sdr.getNumTags();
				
		this.tagtag2int = new int[this.numTags][this.numTags];
		this.tagseg2int = new int[this.numTags][this.numSegs];
		
		
		this.gradient = new double[this.numTags * this.numSegs + this.numTags * this.numTags];
		this.weights = new double[this.numTags * this.numSegs + this.numTags * this.numTags];
		int counter = 0;
		
		for (int i = 0; i < this.numTags; ++i) {
			for (int j = 0; j < this.numTags; ++j) {
				tagtag2int[i][j] = counter;
				++counter;
			}
		}
		for (int i = 0; i < this.numTags; ++i) {
			for (int j = 0; j < this.numSegs; ++j) {
				tagseg2int[i][j] = counter;
				++counter;
			}
		}
		
		Segmenter segmenter = new Segmenter(this.maxLength,this.numTags, tagtag2int, tagseg2int);
		double likelihood = segmenter.logLikelihood(trainingData.get(0), weights);
		
		segmenter.expectedCounts(gradient,trainingData.get(0), weights);
		System.out.println(Math.exp(likelihood));
		
		// finite difference
		double eps = 0.01;
		double[] gradientTest = new double[this.gradient.length];
		for (int i = 0; i < 8; ++i) {
			this.weights[i] += eps;
			double value1 = segmenter.logLikelihood(trainingData.get(0), weights);
			this.weights[i] -= 2 * eps;
			double value2 = segmenter.logLikelihood(trainingData.get(0), weights);
			this.weights[i] += eps;
			gradientTest[i] = (value1 - value2) / (2 * eps);
		}
		
		
		System.out.println(Arrays.toString(this.gradient));
		System.out.println(Arrays.toString(gradientTest));

	}
	
	public static void main(String[] args) {
		SegmentationDataReader sdr = new SegmentationDataReader(args[0],args[1],args[2],1);
		Cemino cemino = new Cemino(sdr);
	}

}
