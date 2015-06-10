package marmot.ising;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import marmot.util.Numerics;

public class Analyzer {
	
	private DataReader dr;
	private List<Datum> data;
	private List<IsingFactorGraph> trainingFactorGraphs;
	
	private UnaryFeatureExtractor ufe;
	
	private double[] parameters;
	private double[] gradient;
	
	public Analyzer(DataReader dr) {
		this.dr = dr;
		this.data = new LinkedList<Datum>();
		this.trainingFactorGraphs = new LinkedList<IsingFactorGraph>();

		this.ufe = new UnaryFeatureExtractor(5,5);
		
		
		System.out.println("...num variables:\t" + dr.numVariables);
		System.out.println("...num pairs:\t" + dr.pairs.size());

		int counter = 0;
		int multiple = 0;
		ufe.setStartFeature(0);
		ufe.setTotalNumVariables(dr.numVariables);
		for (Datum d : dr.data) {
			ufe.extract(d.getWord());
		}
		
	
	
		
		System.out.println("...num parameters:\t" + ufe.getNumFeatures());
		this.parameters = new double[ufe.getNumFeatures()];
		this.gradient = new double[ufe.getNumFeatures()];
		
		for (Datum d : dr.data) {
			System.out.println(counter + "\t" + d.getWord() + "\t" + d.getTag().size());
			
			ArrayList<Integer> golden = new ArrayList<Integer>();
			for (int i = 0; i < dr.numVariables; ++i) {
				golden.add(0);
			}
			for (Integer t : d.getTag()) {
				golden.set(t, 1);
			}
			
			if (d.getTag().size() > 1) 
				multiple += 1;

			
			
			IsingFactorGraph fg = new IsingFactorGraph(d.getWord(), ufe, 10, dr.numVariables, dr.pairsLst, golden, dr.tagNames);
			this.trainingFactorGraphs.add(fg);
			
			++counter;
		}

		train(1000,0.01);
		
	}

	public void train(int numIterations, double eta) {
		// train
		for (int i = 0; i < this.gradient.length; ++i) {
			this.gradient[i] = 0.0;
		}
		
		for (int iter = 0; iter < numIterations; ++iter) {
			double likelihood = 0.0;
			for (IsingFactorGraph ig : this.trainingFactorGraphs) {
				ig.updatePotentials(parameters);
				ig.featurizedGradient(gradient);
				likelihood += ig.logLikelihood();
			}
			System.out.println("...likelihood:\t" + likelihood);
			// descent
			for (int i = 0; i < this.gradient.length; ++i) {
				this.parameters[i] += eta * this.gradient[i];
				this.gradient[i] = 0.0;
			}
		}
		
	}

	
	public double decodeTrain() {
		double accuracy = 0.0;
		for (IsingFactorGraph ig : this.trainingFactorGraphs) {
			
		}
		
		return accuracy;
	}
	
	public static void main(String[] args) {
		DataReader dr = new MorphItReader(args[0]);
		new Analyzer(dr);


	}
}
