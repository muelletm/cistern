package marmot.igel;

import java.util.ArrayList;
import java.util.List;

public class UnaryFactor extends Factor {
	
	private List<SegmentVariable> neighbors;
	private List<Integer> messageIds;
	private List<Message> messages;
	
	private double[] potential;
	
	public UnaryFactor() {
		this.setNeighbors(new ArrayList<SegmentVariable>());
		this.setMessageIds(new ArrayList<Integer>());
		this.setMessages(new ArrayList<Message>());
		
		this.setPotential(new double[2]);
		
		// init
		this.potential[0] = 1.0;
		this.potential[1] = 1.0;
	}

	public void passMessages() {
		this.messages.get(0).measure[0] = this.potential[0];
		this.messages.get(0).measure[1] = this.potential[1];

	}
	
	public List<SegmentVariable> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(List<SegmentVariable> neighbors) {
		this.neighbors = neighbors;
	}

	public List<Integer> getMessageIds() {
		return messageIds;
	}

	public void setMessageIds(List<Integer> messageIds) {
		this.messageIds = messageIds;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public double[] getPotential() {
		return potential;
	}

	public void setPotential(double[] potential) {
		this.potential = potential;
	}
	

}
