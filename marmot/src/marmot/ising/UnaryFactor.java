package marmot.ising;

import java.util.ArrayList;

public class UnaryFactor extends Factor {

	// variable id
	private int i;
	private int size;
	private double[] potential;
	
	
	
	public UnaryFactor(int size, int i) {
		this.setSize(size);
		this.setPotential(new double[this.size]);
		this.setI(i);
		
		this.setNeigbors(new ArrayList<Variable>());
		this.setMessageIds(new ArrayList<Integer>());
		
		this.setMessages(new ArrayList<Message>());
	}

	@Override
	public void passMessage() {
		for (int i = 0; i < this.size; ++i) {
			this.messages.get(0).measure[i] = this.potential[i];
		}
		
		// renormalize (optional)
		this.messages.get(0).renormalize();
		
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

	

	
	
}
