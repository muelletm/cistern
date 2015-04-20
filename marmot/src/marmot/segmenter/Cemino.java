package marmot.segmenter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Cemino {
	
	private SegmentationDataReader sdr;
	private double[] weights;
	
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
	
		tagtag2int = new int[this.numTags][this.numTags];
		tagseg2int = new int[this.numTags][this.numSegs];
		
		
		
		
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
		
		Segmenter segmenter = new Segmenter(this.maxLength,this.numTags);
		segmenter.backward(trainingData.get(0), weights, tagtag2int, tagseg2int);
	}
	
	public static void main(String[] args) {
		SegmentationDataReader sdr = new SegmentationDataReader(args[0],args[1],args[2],1);
		Cemino cemino = new Cemino(sdr);
	}

}
