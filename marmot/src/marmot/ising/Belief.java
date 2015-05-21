package marmot.ising;

public class Belief extends Measure {

	public Belief(int size) {
		super(size);
	}
	
	public void toOnes() {
		for (int i = 0; i < this.size; ++i) {
			this.measure[i] = 1.0;
		}
	}

}
