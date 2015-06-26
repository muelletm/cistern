package experimental.igel;

import java.util.List;

public class BinaryVariable {
	
	String segment;
	int segmentId;
	
	List<Factor> neighbors;
	List<Integer> messageIds;
	List<Message> messages;
	
	SemiMarkovFactor globalFactor;
	
	Belief belief;
	
	public BinaryVariable() {
		
	}
	
	

}
