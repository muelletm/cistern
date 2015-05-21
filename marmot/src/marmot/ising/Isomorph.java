package marmot.ising;

public class Isomorph {

	public static void main(String[] args) {
		
		// extract features and read in data
		FeatureExtractor fe = new FeatureExtractor();
		DataReader dr = new DataReader();
		
		// train
		Learner learner = new Learner(dr,fe);
		learner.train();
		
		// evaluate
		Evaluator evaluator = new Evaluator();
		evaluator.evaluate();
		
	}
}
