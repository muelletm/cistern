package marmot.ising;

import java.util.List;

public abstract class Factor {

	// neighbors 
		protected List<Variable> neighbors;
		protected List<Integer> messageIds;
		
		//messages 
		protected List<Message> messages;

		
		public abstract void passMessage();
		public abstract void renormalize();
		
		public List<Message> getMessages() {
			return messages;
		}

		public void setMessages(List<Message> messages) {
			this.messages = messages;
		}

		public List<Integer> getMessageIds() {
			return messageIds;
		}

		public void setMessageIds(List<Integer> messageIds) {
			this.messageIds = messageIds;
		}

		public List<Variable> getNeighbors() {
			return neighbors;
		}

		public void setNeighbors(List<Variable> neighbors) {
			this.neighbors = neighbors;
		}
}
