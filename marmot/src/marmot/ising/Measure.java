package marmot.ising;

import java.util.Map;

public abstract class Measure {
	private Map<Integer,String> int2String;
	protected double[] measure;
	protected int size;
	
	public Measure(int size) {
		this.setSize(size);
		this.measure = new double[this.size];
	}
	
	/**
	 * Renormalizes the measure
	 */
	public void renormalize() {
		double Z = 0.0;
		
		for (int i = 0; i < this.measure.length; ++i) {
			Z += this.measure[i];
		}
		for (int i = 0; i < this.measure.length; ++i) {
			this.measure[i] /= Z;
		}
	}
	
	public void toZeros() {
		for (int i = 0; i < this.size; ++i) {
			this.measure[i] = 1.0;
		}
	}
	
	public void toOnes() {
		for (int i = 0; i < this.size; ++i) {
			this.measure[i] = 1.0;
		}
	}

	public Map<Integer,String> getInt2String() {
		return int2String;
	}

	public void setInt2String(Map<Integer,String> int2String) {
		this.int2String = int2String;
	}

	public double[] getMeasure() {
		return measure;
	}

	public void setMeasure(double[] measure) {
		this.measure = measure;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
}
