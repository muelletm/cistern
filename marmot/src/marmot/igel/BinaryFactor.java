package marmot.igel;

import java.util.ArrayList;
import java.util.List;

public class BinaryFactor extends Factor {
	
	private List<SegmentVariable> neighbors;
	private List<Integer> messageIds;
	private List<Message> messages;

	public BinaryFactor() {
		this.setMessages(new ArrayList<Message>());
		this.setNeighbors(new ArrayList<SegmentVariable>());
		this.setMessageIds(new ArrayList<Integer>());
	}

	public void passMessages() {
		
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
}
