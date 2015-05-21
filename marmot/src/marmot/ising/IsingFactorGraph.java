package marmot.ising;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.javatuples.Pair;

public class IsingFactorGraph {
	
	private int numVariables;

	protected List<Variable> variables;
	protected List<UnaryFactor> unaryFactors;
	protected List<BinaryFactor> binaryFactors;

	

	public IsingFactorGraph(int numVariables, List<Pair<Integer,Integer>> pairwise, List<String> tagNames) {
		this.numVariables = numVariables;
		
		this.variables = new ArrayList<Variable>();
		this.unaryFactors = new ArrayList<UnaryFactor>();
		this.binaryFactors = new ArrayList<BinaryFactor>();
		
		// ADD VARIABLES AND UNARY FACTORS
		for (int i = 0; i < this.numVariables; ++i) {
			Variable v = new Variable(2,i,tagNames.get(i));
			UnaryFactor uf = new UnaryFactor(2,i);
			
			// add neighbors to variable
			v.getNeighbors().add(uf);
			v.getMessageIds().add(0);
			v.getMessages().add(new Message(2));
			
			// add neighbors to unary
			uf.getNeigbors().add(v);
			uf.getMessageIds().add(v.getMessages().size());
			uf.getMessages().add(new Message(2));
			
			// add to graph
			this.variables.add(v);
			this.unaryFactors.add(uf);
		}
		
		for (Pair<Integer,Integer> p : pairwise) {
			int i = p.getValue0();
			int j = p.getValue1();
			BinaryFactor bf = new BinaryFactor(2,2,i,j);
			
			// add neighbors to variable
			Variable v1 = this.variables.get(i);
			Variable v2 = this.variables.get(j);
			
			v1.getNeighbors().add(bf);
			v1.getMessageIds().add(0);
			v1.getMessages().add(new Message(2));
			
			v2.getNeighbors().add(bf);
			v2.getMessageIds().add(1);
			v2.getMessages().add(new Message(2));
			
			// add neighbors to factor
			bf.getNeigbors().add(v1);
			bf.getMessageIds().add(v1.getMessages().size());
			bf.getMessages().add(new Message(2));
			
			bf.getNeigbors().add(v2);
			bf.getMessageIds().add(v2.getMessages().size());
			bf.getMessages().add(new Message(2));
			
			// add to graph
			this.binaryFactors.add(bf);
		}
	}
	
	/**
	 * Brute force inference for the Ising factor graph
	 */
	public void inferenceBruteForce() {
		double Z = 0.0;
		double[][] marginals = new double[this.numVariables][2];
		for(int i = 0; i < Math.pow(2,this.numVariables); i++) {    
		    
			double configurationScore = 0.0;
			
			 String format="%0"+this.numVariables+"d";
			 String newString = String.format(format,Integer.valueOf(Integer.toBinaryString(i)));
			 List<Integer> configuration = new ArrayList<Integer>();
			 for (int n = 0; n < this.numVariables; ++n) {
				 configuration.add(Character.getNumericValue(newString.charAt(n)));
			 }
			 
			 // sum over unary factors
			 for (UnaryFactor uf : this.unaryFactors) {
				 int value = configuration.get(uf.getI());
				 configurationScore += uf.potential[value];
				 
			 }
			 
			 // sum over binary factors
			 for (BinaryFactor bf : this.binaryFactors) {
				 int value1 = configuration.get(bf.getI());
				 int value2 = configuration.get(bf.getJ());
				 configurationScore += bf.potential[value1][value2];
			 }
			 
			 Z += configurationScore;
			 System.out.println(configuration);
			 System.out.println(configurationScore);
			 System.out.println();
			 //add configuration score
			 for (int n = 0; n < this.numVariables; ++n) {
				 int value = configuration.get(n);
				 marginals[n][value] += configurationScore;
			 }
		}
		
		for (int n = 0; n < this.numVariables; ++n) {
			 double Z_local = marginals[n][0] + marginals[n][1];
			 marginals[n][0] /= Z_local;
			 marginals[n][1] /= Z_local;
		 }
		
		System.out.println(Arrays.deepToString(marginals));
	}
	
	/**
	 * Performs inference by belief propagation
	 * @param maxIterNum
	 * @param convergence
	 */
	public void inference(int maxIterNum, double convergence) {
		for (int iterNum = 0; iterNum < maxIterNum; ++iterNum) {
		
			// update unary factors
			for (UnaryFactor ur : this.unaryFactors) {
				ur.passMessage();
			}
			
			// update binary factors
			for (BinaryFactor bf : this.binaryFactors)
			{
				bf.passMessage();
			}
			
			// update variables
			for (Variable v : this.variables) {
				v.passMessage();
			}
		}
	}
}
