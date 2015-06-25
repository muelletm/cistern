package marmot.experimental.igel;

import java.util.ArrayList;
import java.util.List;

public class SegmentVariable extends Measure {
	
	private String segment;
	private int startPos;
	private int endPos;
	
	private List<Factor> neighbors;
	private List<Integer> messageIds;
	private List<Message> messages;
	
	private Belief belief;
	
	public SegmentVariable(String segment, int startPos, int endPos) {
		super(2);
		this.setSegment(segment);
		this.setStartPos(startPos);
		this.setEndPos(endPos);
		
		this.setNeighbors(new ArrayList<Factor>());
		this.setMessageIds(new ArrayList<Integer>());
		this.setMessages(new ArrayList<Message>());
		
		this.setBelief(new Belief(2));
	}

	
	public void computeBelief() {
		this.belief.toOnes();
		
		for (int index = 0; index < this.messageIds.size(); ++index) {
			Factor f = neighbors.get(index);
			int messageId = this.messageIds.get(index);
			
			Message m = f.getMessages().get(messageId);
			
			for (int i = 0; i < 2; ++i) {
				this.belief.measure[i] *= m.measure[i];
			}
		}
	}
	
	public void passMessages() {
		this.computeBelief();
		
		for (int index = 0; index < this.messageIds.size(); ++index) {
			Factor f = neighbors.get(index);
			int messageId = this.messageIds.get(index);
			
			Message m = f.getMessages().get(messageId);
			
			messages.get(index).toZeros();
			for (int i = 0; i < 2; ++i) {
				messages.get(index).measure[i] = this.belief.measure[i] / m.measure[i];
			}
		}
	}
	
	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

	public int getStartPos() {
		return startPos;
	}

	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}

	public int getEndPos() {
		return endPos;
	}

	public void setEndPos(int endPos) {
		this.endPos = endPos;
	}

	public List<Factor> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(List<Factor> neighbors) {
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


	public Belief getBelief() {
		return belief;
	}


	public void setBelief(Belief belief) {
		this.belief = belief;
	}
	
	

}
