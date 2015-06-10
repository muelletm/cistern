package marmot.ising;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import marmot.util.Numerics;

public class Analyzer {
	
	private DataReader drTrain;
	private DataReader drDev;
	private List<Datum> data;
	private List<IsingFactorGraph> trainingFactorGraphs;
	private List<IsingFactorGraph> devFactorGraphs;
	
	private UnaryFeatureExtractor ufe;
	
	private double[] parameters;
	private double[] gradient;
	
	public Analyzer(DataReader drTrain, DataReader drDev) {
		this.drTrain = drTrain;
		this.drDev = drDev;
		
		this.data = new LinkedList<Datum>();
		this.trainingFactorGraphs = new LinkedList<IsingFactorGraph>();
		this.devFactorGraphs = new LinkedList<IsingFactorGraph>();

		this.ufe = new UnaryFeatureExtractor(0,5);
		
		
		System.out.println("...num variables:\t" + drTrain.numVariables);
		System.out.println("...num pairs:\t" + drTrain.pairs.size());

		int counter = 0;
		int multiple = 0;
		ufe.setStartFeature(0);
		ufe.setTotalNumVariables(drTrain.numVariables);
		for (Datum d : drTrain.data) {
			ufe.extract(d.getWord());
		}
		for (Datum d : drDev.data) {
			ufe.extract(d.getWord());
		}
		
	
		
		System.out.println("...num parameters:\t" + ufe.getNumFeatures());
		this.parameters = new double[ufe.getNumFeatures()];
		this.gradient = new double[ufe.getNumFeatures()];
		// dev
		for (Datum d : drDev.data) {
			System.out.println(counter + "\t" + d.getWord() + "\t" + d.getTag().size());
			
			ArrayList<Integer> golden = new ArrayList<Integer>();
			for (int i = 0; i < drTrain.numVariables; ++i) {
				golden.add(0);
			}
			for (Integer t : d.getTag()) {
				golden.set(t, 1);
			}
			
			if (d.getTag().size() > 1) 
				multiple += 1;

			
			
			IsingFactorGraph fg = new IsingFactorGraph(d.getWord(), ufe, 1, drTrain.numVariables, drTrain.pairsLst, golden, drTrain.tagNames);
			this.devFactorGraphs.add(fg);
			
			++counter;
		}
		
		for (Datum d : drTrain.data) {
			System.out.println(counter + "\t" + d.getWord() + "\t" + d.getTag().size());
			
			ArrayList<Integer> golden = new ArrayList<Integer>();
			for (int i = 0; i < drTrain.numVariables; ++i) {
				golden.add(0);
			}
			for (Integer t : d.getTag()) {
				golden.set(t, 1);
			}
			
			if (d.getTag().size() > 1) 
				multiple += 1;

			
			
			IsingFactorGraph fg = new IsingFactorGraph(d.getWord(), ufe, 1, drTrain.numVariables, drTrain.pairsLst, golden, drTrain.tagNames);
			this.trainingFactorGraphs.add(fg);
			
			++counter;
		}
		

		train(50,2.0);
		System.out.println("...train accuracy:\t" + decodeTrain());
		System.out.println("...dev accuracy:\t" + decodeDev());
;
		System.exit(0);
		System.out.println("...feature dump:\t");
		for (IsingFactorGraph fg : this.trainingFactorGraphs) {
			
			for (UnaryFactor uf : fg.getUnaryFactor()) {
				System.out.println(uf.getTag());

				for (Integer feat : uf.getFeaturesPositive()) {
					System.out.println(ufe.getInt2Feature().get(feat) + "\t" + feat);
					System.out.println(ufe.getInt2Feature().get(feat + 1) + "\t" + (feat + 1));
				}
			}
		}
		
		System.out.println("...tag names:\t");
		System.out.println(drTrain.tagNames);
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
				ig.featurizedGradient(gradient, this.trainingFactorGraphs.size());
				for (int i = 0; i < this.gradient.length; ++i) {
					this.parameters[i] += eta * this.gradient[i] ; //- 0.001 * parameters[i];
					this.gradient[i] = 0.0;
				}
				//likelihood += ig.logLikelihood();
			}
			eta *= .9;

			System.out.println("...iteration:\t" + iter);
			System.out.println("...likelihood:\t" + likelihood);
			// descent
			
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
					String tag = this.drTrain.integer2Tag.get(counter);
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
	
	public double decodeDev() {
		double correct = 0.0;
		int total = 0;
		for (IsingFactorGraph ig : this.devFactorGraphs) {
			List<String> decoded = ig.posteriorDecode();
			List<String> golden = new LinkedList<String>();
			
			int counter = 0;
			for (Integer g : ig.golden) {
				
				if (g == 1) {
					String tag = this.drDev.integer2Tag.get(counter);
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
		DataReader drTrain = new ThomasReader(args[0]);
		DataReader drDev = new ThomasReader(args[1]);
		new Analyzer(drTrain, drDev);


	}
}
