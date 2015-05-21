package marmot.ising;

import java.util.ArrayList;
import java.util.List;

public class Variable {
	
	private int i;
	private String tagName;
	
	//
	private Belief belief;
	private int size;
	
	// neighbors 
	private List<Factor> neighbors;
	private List<Integer> messageIds;
	
	//messages 
	private List<Message> messages;

	public Variable(int size, int i, String tagName) {
		this.setSize(size);
		this.setBelief(new Belief(size));
		
		this.setI(i);
		this.setTagName(tagName);
		
		this.setNeighbors(new ArrayList<Factor>());
		this.setMessageIds(new ArrayList<Integer>());
		
		this.setMessages(new ArrayList<Message>());
	}
	
	public void computeBelief() {
		this.belief.toOnes();
		for (int neighborId = 0; neighborId < this.neighbors.size(); ++neighborId) {
			Factor f = this.neighbors.get(neighborId);
			int messageId = this.messageIds.get(neighborId);
			Message m = f.getMessages().get(messageId);
			
			for (int n = 0; n < this.size; ++n) {
				this.belief.measure[n] *= m.measure[n];
			}
		}
		this.belief.renormalize();
		
		//System.exit(0);
	}
	
	/**
	 * Efficiently compute all message in O(n) rather than O(n^2) (naive)
	 * Algorithm:
	 * 1) Compute belief 
	 * 2) Divide out message for each message
	 */
	public void passMessage() {
		this.computeBelief();
		
		int neighborId = 0;
		for (Message m1 : this.messages) {
			Factor f = this.neighbors.get(neighborId);
			int messageId = this.messageIds.get(neighborId);
			
			Message m2 = f.messages.get(messageId);
			
			for (int n = 0; n < this.size; ++n) {
				m1.measure[n] = this.belief.measure[n] / m2.measure[n];
			}
			// renormalize (optional)
			m1.renormalize();
			
			++neighborId;
		}
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
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

	public List<Factor> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(List<Factor> neighbors) {
		this.neighbors = neighbors;
	}

	public Belief getBelief() {
		return belief;
	}

	public void setBelief(Belief belief) {
		this.belief = belief;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}
