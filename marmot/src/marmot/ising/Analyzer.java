package marmot.ising;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
		double accuracy = decodeTrain();
		System.out.println("...train accuracy:\t" + accuracy);
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
		double correct = 0.0;
		int total = 0;
		for (IsingFactorGraph ig : this.trainingFactorGraphs) {
			List<String> decoded = ig.posteriorDecode();
			List<String> golden = new LinkedList<String>();
			
			int counter = 0;
			for (Integer g : ig.golden) {
				
				if (g == 1) {
					String tag = this.dr.integer2Tag.get(counter);
					golden.add(tag);
				}
				++counter;
			}
			
			Collections.sort(decoded);
			Collections.sort(golden);
			
			if (decoded.equals(golden)) {
				correct += 1;
			} else {
				System.out.println("...word:\t" + ig.getWord());
				System.out.println("...predicted:\t" + decoded);
				System.out.println("...golden:\t" + golden);
			}
			total += 1;
		}
		
		return correct / total;
	}
	
	public static void main(String[] args) {
		DataReader dr = new MorphItReader(args[0]);
		new Analyzer(dr);


	}
}
