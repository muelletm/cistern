package marmot.experimental.igel;

public abstract class Measure {
	
	private int size;
	protected double[] measure;

	public Measure(int size) {
		this.setSize(size);
		this.setMeasure(new double[size]);
		
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public double[] getMeasure() {
		return measure;
	}

	public void setMeasure(double[] measure) {
		this.measure = measure;
	}
	
	
	public void toZeros() {
		for (int i = 0; i < this.size; ++i) {
			this.measure[i]  = 0.0;
		}
	}
	
	
	public void toOnes() {
		for (int i = 0; i < this.size; ++i) {
			this.measure[i]  = 0.0;
		}
	}
}
