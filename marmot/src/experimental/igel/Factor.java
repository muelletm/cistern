package experimental.igel;

import java.util.List;

public abstract class Factor {

	protected List<SegmentVariable> neighbors;
	protected List<Integer> messageIds;
	protected List<Message> messages;
	
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
