package marmot.ising;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Analyzer {
	
	private DataReader dr;
	private List<Datum> data;
	private List<IsingFactorGraph> factorGraphs;
	
	public Analyzer(DataReader dr) {
		this.dr = dr;
		this.data = new LinkedList<Datum>();
		this.factorGraphs = new LinkedList<IsingFactorGraph>();

		
		System.out.println("...num variables:\t" + dr.numVariables);
		System.out.println("...num pairs:\t" + dr.pairs.size());

		int counter = 0;
		int multiple = 0;
		for (Datum d : dr.data) {
			
			//System.out.println(counter);
			
			ArrayList<Integer> golden = new ArrayList<Integer>();
			for (int i = 0; i < dr.numVariables; ++i) {
				golden.add(0);
			}
			for (Integer t : d.getTag()) {
				golden.set(t, 1);
			}
			
			if (d.getTag().size() > 1) 
				multiple += 1;
			//System.out.println("START MAKING GRAPH");
			IsingFactorGraph fg = new IsingFactorGraph(dr.numVariables, dr.pairsLst, golden, dr.tagNames);
			//System.out.println("STOP MAKING GRAPH");
			this.factorGraphs.add(fg);
			
			++counter;
		}
		System.out.println("DONE");
		System.out.println("MULTIPLE:\t" + multiple);
	}


	public double logLikelihood() {
		return 0.0;
	}
	
	public void gradient() {
		
	}
	
	public static void main(String[] args) {
		DataReader dr = new MorphItReader(args[0]);
		new Analyzer(dr);

	}
}
