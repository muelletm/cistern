package experimental.igel;

import java.util.ArrayList;
import java.util.List;

public class BinaryFactor extends Factor {
	
	
	protected double[][] potential;
	
	public BinaryFactor() {
		this.setMessages(new ArrayList<Message>());
		this.setNeighbors(new ArrayList<SegmentVariable>());
		this.setMessageIds(new ArrayList<Integer>());
		
		this.setPotential(new double[2][2]);
	}

	public void passMessages() {
		Message messageIn1 = this.messages.get(messageIds.get(0));
		Message messageIn2 = this.messages.get(messageIds.get(1));

		// message 1
		this.messages.get(0).measure[0] = this.potential[0][0] * messageIn2.measure[0] + this.potential[0][1] * messageIn2.measure[1];
		this.messages.get(0).measure[1] = this.potential[1][0] * messageIn2.measure[0] + this.potential[1][1] * messageIn2.measure[1];

		// message 2
		this.messages.get(1).measure[0] = this.potential[0][0] * messageIn1.measure[0] + this.potential[1][0] * messageIn1.measure[1];
		this.messages.get(1).measure[1] = this.potential[0][1] * messageIn1.measure[0] + this.potential[1][1] * messageIn1.measure[1];

	}
	

	public double[][] getPotential() {
		return potential;
	}

	public void setPotential(double[][] potential) {
		this.potential = potential;
	}
}
