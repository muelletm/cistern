// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package lemming.lemma.toutanova;

import java.util.LinkedList;
import java.util.List;

public class SimpleAligner implements Aligner {

	private static final long serialVersionUID = 1L;

	@Override
	public List<Integer> align(String input, String output) {
	
		List<Integer> positions = new LinkedList<>();
		
		int length = Math.min(input.length(), output.length());
		
		int index = 0;
		
		while (index < length - 1) {
			
			if (input.charAt(index) == output.charAt(index)) {
				
				positions.add(1);
				positions.add(1);
				
			} else {
				
				break;
				
			}
			
			index ++;
			
		}
		
		positions.add(input.length() - index);
		positions.add(output.length() - index);
		
		return positions;
	}

}
