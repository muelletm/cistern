package marmot.ising;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;

public class IsingFactorGraph {
	
	private int numVariables;

	protected List<Variable> variables;
	protected List<UnaryFactor> unaryFactors;
	protected List<BinaryFactor> binaryFactors;

	protected List<Integer> golden;
	
	protected int numParameters;
	

	public IsingFactorGraph(int numVariables, List<Pair<Integer,Integer>> pairwise, List<Integer> golden, List<String> tagNames) {
		this.numVariables = numVariables;
		
		this.variables = new ArrayList<Variable>();
		this.unaryFactors = new ArrayList<UnaryFactor>();
		this.binaryFactors = new ArrayList<BinaryFactor>();
		
		this.golden = golden;
		
		// ADD VARIABLES AND UNARY FACTORS
		for (int i = 0; i < this.numVariables; ++i) {
			Variable v = new Variable(2,i,tagNames.get(i));
			UnaryFactor uf = new UnaryFactor(2,i);
			
			// add neighbors to variable
			v.getNeighbors().add(uf);
			v.getMessageIds().add(0);
			v.getMessages().add(new Message(2));
			
			// add neighbors to unary
			uf.getNeighbors().add(v);
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
			bf.getMessages().add(new Message(2));

			
			v2.getNeighbors().add(bf);
			v2.getMessageIds().add(1);
			bf.getMessages().add(new Message(2));

			
			// add neighbors to factor
			bf.getNeighbors().add(v1);
			bf.getMessageIds().add(v1.getMessages().size());
			v1.getMessages().add(new Message(2));

			bf.getNeighbors().add(v2);
			bf.getMessageIds().add(v2.getMessages().size());
			v2.getMessages().add(new Message(2));

			// add to graph
			this.binaryFactors.add(bf);
		}
		
		this.numParameters = 2 * this.unaryFactors.size() + 4 * this.binaryFactors.size();

	}
	
	/**
	 * Brute force inference for the Ising factor graph
	 */
	public double[][] inferenceBruteForce() {
		double[][] marginals = new double[this.numVariables][2];
		for(int i = 0; i < Math.pow(2,this.numVariables); i++) {    
		    
			double configurationScore = 1.0;
			
			 String format="%0"+this.numVariables+"d";
			 String newString = String.format(format,Integer.valueOf(Integer.toBinaryString(i)));
			 List<Integer> configuration = new ArrayList<Integer>();
			 for (int n = 0; n < this.numVariables; ++n) {
				 configuration.add(Character.getNumericValue(newString.charAt(n)));
			 }
			 
			 // sum over unary factors
			 for (UnaryFactor uf : this.unaryFactors) {
				 int value = configuration.get(uf.getI());
				 configurationScore *= uf.potential[value];
				 
			 }
			 
			 // sum over binary factors
			 for (BinaryFactor bf : this.binaryFactors) {
				 int value1 = configuration.get(bf.getI());
				 int value2 = configuration.get(bf.getJ());
				 configurationScore *= bf.potential[value1][value2];
			 }
			 
			 
			 //System.out.println(configuration);
			 //System.out.println(configurationScore);
			 //System.out.println();
			 
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
		
		return marginals;
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
	
	/**
	 * 
	 */
	public double logLikelihood() {
		// partition function
		
		int index = 0;
		double logLikelihood = 0.0;
		
		for (int golden : this.golden) {
			logLikelihood += Math.log(this.variables.get(index).getBelief().measure[golden]);
			++index;
		}
		return logLikelihood;
	}
	
	/**
	 * Finite Difference Results
	 * @return
	 */
	public double[] finiteDifference(double[] parameters, double epsilon) {
		double[] gradient = new double[parameters.length];
		
		for (int i = 0; i < parameters.length; ++i) {
			parameters[i] += epsilon;
			this.updatePotentials(parameters);
			this.inference(10, 1.0);
			
			double val1 = this.logLikelihood();

			parameters[i] -= 2 * epsilon;
			this.updatePotentials(parameters);
			this.inference(10, 1.0);

			double val2 = this.logLikelihood();

			gradient[i]  = (val1 - val2) / (2 * epsilon);
			
			parameters[i] += epsilon;
		}
		
		return gradient;
	}
	
	public void updatePotentials(double[] parameters) {
		int counter = 0;
		for (UnaryFactor uf : this.unaryFactors) {
			uf.setPotential(0, parameters[counter]);
			++counter;

			uf.setPotential(1, parameters[counter]);
			++counter;
		
			uf.renormalize();
		}
	
		// random binary potentials
		for (BinaryFactor bf : this.binaryFactors) {
			//bf.setPotential(0, 0, parameters[counter]);
			++counter;

			//bf.setPotential(0, 1, parameters[counter]);
			++counter;

			//bf.setPotential(1, 0, parameters[counter]);
			++counter;

			bf.setPotential(1, 1, parameters[counter]);
			++counter;
			
			bf.renormalize();

		}
	}
	
	/**
	 * 
	 * @return
	 */
	public double[] unfeaturizedGradient() {
		double[] gradient = new double[this.numParameters];
		
		int counter = 0;
		for (UnaryFactor uf : this.unaryFactors) {
			
			if (this.golden.get(uf.getI()) == 0) {
				gradient[counter] += 1.0;
			}
			gradient[counter] -= this.variables.get(this.golden.get(uf.getI())).getBelief().measure[0];
			
			++counter;

			if (this.golden.get(uf.getI()) == 1) {
				gradient[counter] += 1.0;
			}
			
			gradient[counter] -= this.variables.get(this.golden.get(uf.getI())).getBelief().measure[1];

			
			++counter;
			
		}
	
		// random binary potentials
		for (BinaryFactor bf : this.binaryFactors) {
			
			if (this.golden.get(bf.getI()) == 0 && bf.getJ() == 0) {
				gradient[counter] += 1.0;
			}
			gradient[counter] -= this.variables.get(this.golden.get(bf.getI())).getBelief().measure[0] * this.variables.get(this.golden.get(bf.getJ())).getBelief().measure[0];

			++counter;

			if (this.golden.get(bf.getI()) == 0 && bf.getJ() == 1) {
				gradient[counter] += 1.0;
			}
			gradient[counter] -= this.variables.get(this.golden.get(bf.getI())).getBelief().measure[0] * this.variables.get(this.golden.get(bf.getJ())).getBelief().measure[1];

			
			++counter;

			if (this.golden.get(bf.getI()) == 1 && bf.getJ() == 0) {
				gradient[counter] += 1.0;
			}
			
			gradient[counter] -= this.variables.get(this.golden.get(bf.getI())).getBelief().measure[1] * this.variables.get(this.golden.get(bf.getJ())).getBelief().measure[0];

			++counter;

			if (this.golden.get(bf.getI()) == 1 && bf.getJ() == 1) {
				gradient[counter] += 1.0;
			}
			
			gradient[counter] -= this.variables.get(this.golden.get(bf.getI())).getBelief().measure[1] * this.variables.get(this.golden.get(bf.getJ())).getBelief().measure[1];

			++counter;

		}
		
		return gradient;
	}
}
