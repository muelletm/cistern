package marmot.ising;

public class Analyzer {

	public static void main(String[] args) {
		DataReader dr = new MorphItReader(args[0]);
		System.out.println("...num variables:\t" + dr.numVariables);
		System.out.println("...num pairs:\t" + dr.pairs.size());

		IsingFactorGraph fg = new IsingFactorGraph(dr.numVariables, dr.pairsLst, dr.tagNames);
		fg.inference(5, 0.001);

	}
}
