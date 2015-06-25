package marmot.experimental.ising;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

public class UnaryFactor extends Factor {

	// variable id
	private int i;
	private int size;
	protected double[] potential;
	protected double[] factorBelief;
	
	private UnaryFeatureExtractor ufe;
	
	private List<Integer> featuresPositive;
	private List<Integer> featuresNegative;
	
	private String word;
	private String tag;
	
	
	public UnaryFactor(String word, String tag, int size, int i, UnaryFeatureExtractor ufe) {
		this.word = word;
		this.tag = tag;
		this.setSize(size);
		this.setPotential(new double[this.size]);
		
		this.setFeaturesPositive(new LinkedList<Integer>());
		this.setFeaturesNegative(new LinkedList<Integer>());

		for (int n = 0; n < this.size; ++n) {
			this.potential[n] = 1.0;
		}
		
		this.setI(i);
		this.setNeighbors(new ArrayList<Variable>());
		this.setMessageIds(new ArrayList<Integer>());
		this.setMessages(new ArrayList<Message>());
		
		this.ufe = ufe;
		Pair<List<Integer>, List<Integer>> p = this.ufe.getFeatures(i, word);
		
		this.featuresPositive = p.getValue0();
		this.featuresNegative = p.getValue1();
		
	}
	
	@Override
	public void computeFactorBelief() {

	}
	
	@Override
	public void passMessage() {
		for (int i = 0; i < this.size; ++i) {
			this.messages.get(0).measure[i] = this.potential[i];
		}
		
		// renormalize (optional)
		this.messages.get(0).renormalize();
		//System.out.println(Arrays.toString(this.messages.get(0).measure));
		
	}
	
	
	public void updatePotential(double[] parameters) {
		
		this.potential[0] = 0.0;
		for (Integer feat : this.featuresNegative) {
			this.potential[0] += parameters[feat];
		}
		
		this.potential[1] = 0.0;
		for (Integer feat : this.featuresPositive) {
			this.potential[1] += parameters[feat];
		}
		
		this.potential[0] = Math.exp(this.potential[0]);
		this.potential[1] = Math.exp(this.potential[1]);

	}
	
	@Override
	public void renormalize() {
		double Z = 0.0;
		for (int i = 0; i < this.size; ++i) {
			Z += this.potential[i];
		}

		for (int i = 0; i < this.size; ++i) {
			this.potential[i] /= Z;
		}
	}
	
	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public double[] getPotential() {
		return potential;
	}

	public void setPotential(double[] potential) {
		this.potential = potential;
	}
	
	public void setPotential(int n, double value) {
		this.potential[n] = value;
	}

	public List<Integer> getFeaturesNegative() {
		return featuresNegative;
	}

	public void setFeaturesNegative(List<Integer> featuresNegative) {
		this.featuresNegative = featuresNegative;
	}
	
	public List<Integer> getFeaturesPositive() {
		return featuresPositive;
	}

	public void setFeaturesPositive(List<Integer> featuresPositive) {
		this.featuresPositive = featuresPositive;
	}
	
	public String getTag() {
		return this.tag;
	}

}
