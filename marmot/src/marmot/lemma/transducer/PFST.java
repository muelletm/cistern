package marmot.lemma.transducer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import marmot.lemma.Instance;
import marmot.lemma.Lemmatizer;
import marmot.lemma.transducer.exceptions.LabelBiasException;
import marmot.lemma.transducer.exceptions.NegativeContext;
import marmot.util.Numerics;



public class PFST extends Transducer {

    private static final Logger LOGGER = Logger.getLogger(PFST.class.getName());

	/**
	 * Don't ever call
	 * @throws NegativeContext
	 * @throws LabelBiasException 
	 */
	public PFST() throws NegativeContext, LabelBiasException {
		this(null,0,1,0,0);
	}
	public PFST(Set<Character> alphabet, int c1, int c2, int c3, int c4) throws LabelBiasException, NegativeContext {
		super(alphabet, c1, c2, c3, c4);
		if (c4 != 0) {
			throw new LabelBiasException();
		}
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void gradient(double gradient[] ) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void gradient(double[] gradient, String upper, String lower) {
		
		LOGGER.info("Starting gradient computation for pair (" + upper + "," + lower + ")...");
		//zero out the relevant positions in the log-semiring
		zeroOut(alphas,upper.length()+1, lower.length()+1);
		zeroOut(betas,upper.length()+1, lower.length()+1);
		
		//make the start position unity in the log-semiring
		alphas[0][0] = 0.0;
		betas[upper.length()][lower.length()] = 0.0;
		
		//backward
		for (int i = upper.length() - 1 ; i >= 0; --i) {
			for (int j = lower.length() - 1; j >= 0; --j) {
	
				// TODO EXTRACT CONTEXT
				
				// ins 
				betas[i][j] = Numerics.sumLogProb(betas[i][j], betas[i+1][j] + 1.0);
				// del
				betas[i][j] = Numerics.sumLogProb(betas[i][j], betas[i][j+1] + 1.0);
				// sub
				betas[i][j] = Numerics.sumLogProb(betas[i][j], betas[i+1][j+1] + 1.0);
			}
		}
		

		//PRINT
		//backward
		for (int i = 0; i < upper.length() + 1; ++i) {
			for (int j = 0; j < lower.length() + 1; ++j ) {
				System.out.print(Double.toString(betas[i][j]) + "\t");
			}
			System.out.println();
		}
		
		//forward 
		for (int i = 1; i < upper.length() + 1; ++i) {
			for (int j = 1; j < lower.length() + 1; ++j ) {
				
				//TODO EXTRACT CONTEXT
				
				// compute gradient
				
				// ins
				double tmp = alphas[i-1][j] * betas[i][j];
				// del
				tmp = alphas[i][j-1] * betas[i][j];
				
				// sub
				tmp = alphas[i-1][j-1] * betas[i][j];
				
				// ins 
				alphas[i][j] = Numerics.sumLogProb(alphas[i][j],alphas[i-1][j] + 1);
				// del
				alphas[i][j] = Numerics.sumLogProb(alphas[i][j],alphas[i][j-1] + 1);
				// sub
				alphas[i][j] = Numerics.sumLogProb(alphas[i][j],alphas[i-1][j-1] + 1);
			}
		}
		// PRINT
		// forward
		for (int i = 0; i < upper.length() + 1; ++i) {
			for (int j = 0; j < lower.length() + 1; ++j ) {
				System.out.print(Double.toString(alphas[i][j]) + "\t");
			}
			System.out.println();
		}
	}

	@Override
	protected double logLikelihood(String upper, String lower) {
		zeroOut(betas,upper.length()+1, lower.length()+1);
		betas[upper.length()][lower.length()] = 0.0;
		
		//backward
		for (int i = upper.length() - 1 ; i >= 0; --i) {
			for (int j = lower.length() - 1; j >= 0; --j) {
	
				// TODO EXTRACT CONTEXT
				
				// ins 
				betas[i][j] = Numerics.sumLogProb(betas[i][j], betas[i+1][j] + 1.0);
				// del
				betas[i][j] = Numerics.sumLogProb(betas[i][j], betas[i][j+1] + 1.0);
				// sub
				betas[i][j] = Numerics.sumLogProb(betas[i][j], betas[i+1][j+1] + 1.0);
			}
		}
		
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Lemmatizer train(List<Instance> instances,
			List<Instance> dev_instances) {
		
	
		// get maximum input and output strings sizes
		this.alphabet = new HashSet<Character>();
		int max1 = 0;
		int max2 = 0;
		for (Instance instance : instances) {
			max1 = Math.max(max1,instance.getForm().length()+1);
			max2 = Math.max(max2,instance.getLemma().length()+1);	
					//extract alphabet
			for (Character c : instance.getForm().toCharArray()) {
				this.alphabet.add(c);
			}
			for (Character c : instance.getLemma().toCharArray()) {
				this.alphabet.add(c);
			}
		}
		for (Instance instance : dev_instances) {
			max1 = Math.max(max1,instance.getForm().length()+1);
			max2 = Math.max(max2,instance.getLemma().length()+1);
			
			//extract alphabet
			for (Character c : instance.getForm().toCharArray()) {
				this.alphabet.add(c);
			}
			for (Character c : instance.getLemma().toCharArray()) {
				this.alphabet.add(c);
			}
		}
		this.alphas = new double[max1][max2];
		this.betas = new double[max1][max2];
		
		zeroOut(alphas);
		zeroOut(betas);
	
		double[] gradient_vector = new double[5];
		this.gradient(gradient_vector,instances.get(0).getForm(), instances.get(0).getLemma());
		
		return new LemmatizerPFST();
	}

	

	

}
