package marmot.ising;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;

public class FactorGraph {
	
	private int numVariables;

	private List<Variable> variables;
	private List<UnaryFactor> unaryFactors;
	private List<BinaryFactor> binaryFactors;

	

	public FactorGraph(int numVariables, List<Pair<Integer,Integer>> pairwise, List<String> tagNames) {
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
	 * Performs inferene by belif propagation
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
