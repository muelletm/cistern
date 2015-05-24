package marmot.igel;

import java.util.ArrayList;
import java.util.List;



import org.javatuples.Pair;

public class FactorGraph {
	
	private List<SegmentVariable> variables;
	private List<UnaryFactor> unaryFactors;
	private List<BinaryFactor> binaryFactors;
	private SemiMarkovFactor globalFactor;
	private int numVariables;
	
	public FactorGraph(Word word) {
		
		this.numVariables = 0;
		
		variables = new ArrayList<SegmentVariable>();
		unaryFactors = new ArrayList<UnaryFactor>();
		binaryFactors = new ArrayList<BinaryFactor>();
		
		// create the factor graph
		for (Pair<Integer,Integer> key : word.getPos2String().keySet()){
			int startPos = key.getValue0();
			int endPos = key.getValue1();
			String segment = word.getPos2String().get(key);
			
			this.numVariables += 1;
			variables.add(new SegmentVariable(segment,startPos,endPos));
		}
		
		// create unary factors
		for (SegmentVariable sv : variables) {
			UnaryFactor uf = new UnaryFactor();
			
			uf.getNeighbors().add(sv);
			uf.getMessageIds().add(sv.getMessages().size());
			sv.getMessages().add(new Message(2));
			
			unaryFactors.add(uf);
		}
		
		// create binary factors
		for (SegmentVariable sv1 : variables) {
			for (SegmentVariable sv2 : variables) {
				if (sv1.getEndPos() == sv2.getStartPos()) {
					System.out.println(sv1.getSegment() + "\t" + sv2.getSegment());
					
					BinaryFactor bf = new BinaryFactor();
					
					// add messages
					bf.getNeighbors().add(sv1);
					bf.getMessageIds().add(sv1.getMessages().size());
					sv1.getMessages().add(new Message(2));
					
					// add message
					bf.getNeighbors().add(sv2);
					bf.getMessageIds().add(sv2.getMessages().size());
					sv2.getMessages().add(new Message(2));
					
					binaryFactors.add(bf);
				}
			}
		}
	}
	
	public void inferenceBP(int maxIterations, double convergence) {
		for (int iterNum = 0; iterNum < maxIterations; ++iterNum) {
			// unary factors
			for (UnaryFactor uf : this.unaryFactors){
				uf.passMessages();
			}
			
			// binary factors
			for (BinaryFactor bf : this.binaryFactors) {
				bf.passMessages();
			}
			
			// global factor
			// TODO
			
			// variables
			for (SegmentVariable sv : this.variables) {
				sv.passMessages();
			}
		}
	}
	
	public void inferenceBruteForce() {
		for(int i = 0; i < Math.pow(2,this.numVariables); i++) {    
		    
			double configurationScore = 1.0;
			
				 String format="%0"+this.numVariables+"d";
				 String newString = String.format(format,Integer.valueOf(Integer.toBinaryString(i)));
				 System.out.println(newString);
			 /*
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
			 */
		}
	}
}
