package marmot.igel;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;

public class FactorGraph {
	
	private List<SegmentVariable> variables;
	private List<UnaryFactor> unaryFactors;
	private List<BinaryFactor> binaryFactors;
	private SemiMarkovFactor globalFactor;
	
	public FactorGraph(Word word) {
		
		variables = new ArrayList<SegmentVariable>();
		unaryFactors = new ArrayList<UnaryFactor>();
		binaryFactors = new ArrayList<BinaryFactor>();
		
		// create the factor graph
		for (Pair<Integer,Integer> key : word.getPos2String().keySet()){
			int startPos = key.getValue0();
			int endPos = key.getValue1();
			String segment = word.getPos2String().get(key);
			
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
		
	}
}
