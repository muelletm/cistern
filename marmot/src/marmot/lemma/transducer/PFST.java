package marmot.lemma.transducer;

import java.util.List;
import java.util.Set;

import marmot.lemma.Instance;
import marmot.lemma.Lemmatizer;
import marmot.lemma.transducer.exceptions.LabelBiasException;
import marmot.lemma.transducer.exceptions.NegativeContext;

public class PFST extends Transducer {

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
	protected void gradient(double[] gradinet, String upper, String lower) {
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
				betas[i][j] = betas[i+1][j];
				// del
				betas[i][j] = betas[i][j+1];
				// sub
				betas[i][j] = betas[i+1][j+1];
			}
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
				alphas[i][j] = alphas[i-1][j];
				// del
				alphas[i][j] = alphas[i][j-1];
				// sub
				alphas[i][j] = alphas[i-1][j-1];
			}
		}
		
	}

	@Override
	protected double logLikelihood() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Lemmatizer train(List<Instance> instances,
			List<Instance> dev_instances) {
		
		// get maximum input and output strings sizes
		int max1 = 0;
		int max2 = 0;
		for (Instance instance : instances) {
			max1 = Math.max(max1,instance.getForm().length()+1);
			max2 = Math.max(max2,instance.getLemma().length()+1);	
		}
		for (Instance instance : dev_instances) {
			max1 = Math.max(max1,instance.getForm().length()+1);
			max2 = Math.max(max2,instance.getLemma().length()+1);
		}
		this.alphas = new double[max1][max2];
		this.betas = new double[max1][max2];
		
		zeroOut(alphas);
		zeroOut(betas);
		
		return new LemmatizerPFST();
	}

	

	

}
