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
		// TODO
	}
	
	/**
	 * Efficiently compute all message in O(n) rather than O(n^2) (naive)
	 * Algorithm:
	 * 1) Compute belief 
	 * 2) Divide out message for each message
	 */
	public void passMessage() {
		// TODO
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
