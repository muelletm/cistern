package marmot.ising;

import java.util.ArrayList;

public class BinaryFactor extends Factor {

	private int size1;
	private int size2;
	protected double[][] potential;
	
	// first variable id
	private int i;
	// second variable id
	private int j;
	
	public BinaryFactor(int size1, int size2, int i, int j) {
		this.setSize1(size1);
		this.setSize2(size2);
		
		this.setPotential(new double[this.size1][this.size2]);
		
		for (int n = 0; n < this.size1; ++n) {
			for (int m = 0; m < this.size2; ++m) {
				this.potential[n][m] = 1.0;
			}
		}
		
		
		this.setI(i);
		this.setJ(j);
		
		this.setNeigbors(new ArrayList<Variable>());
		
		this.setMessageIds(new ArrayList<Integer>());
		this.setMessages(new ArrayList<Message>());
	}
	

	@Override
	public void passMessage() {
		// zero out
		this.messages.get(0).toZeros();
		this.messages.get(1).toZeros();
		
		// TOOD
		
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public int getJ() {
		return j;
	}

	public void setJ(int j) {
		this.j = j;
	}


	public int getSize1() {
		return size1;
	}


	public void setSize1(int size1) {
		this.size1 = size1;
	}


	public int getSize2() {
		return size2;
	}


	public void setSize2(int size2) {
		this.size2 = size2;
	}


	public double[][] getPotential() {
		return potential;
	}


	public void setPotential(double[][] potential) {
		this.potential = potential;
	}

}
