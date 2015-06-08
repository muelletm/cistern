package marmot.ising;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.javatuples.Pair;



public class MorphItReader extends DataReader {
	
	public MorphItReader(String fileName) {
		super();
		System.out.println("...reading in:\t" + fileName);
		try {
			
	        BufferedReader reader = new BufferedReader(new FileReader(fileName));
	        
	        try {
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	            	String[] splitted = line.split("\t");
	            	String word = splitted[0];
	            	String lemma = splitted[1];
	            	String tag = splitted[2];
	            	        
	            	// add word
	            	if (!this.word2Tags.containsKey(word)) {
	            		this.word2Tags.put(word, new HashSet<String>());
	            	}
	            	this.word2Tags.get(word).add(tag);
	            	
	            	// cache features
            		if (!this.tag2Integer.keySet().contains(tag)) {
            			this.tag2Integer.put(tag, this.tag2Integer.size());
            			this.integer2Tag.put(this.tag2Integer.size() - 1, tag);
            		}
            		
            		this.numVariables = this.tag2Integer.size();
            		
	            	this.data.add(new Datum(word,lemma, tag));
		            this.tagNames.add(tag);

	            }
	        }
	        catch (IOException ioe) {
	        	System.out.println("Badness");
	        	System.exit(0);
	        }
            
		}
		catch (IOException ioe) {
	         System.out.println("Badness");
	         System.exit(0);
        }
		
		// add pairs
		for (String word : this.word2Tags.keySet()) {
			Set<String> set = this.word2Tags.get(word);
			for (String tag1 : set) {
				for (String tag2 : set) {
					int int1 = this.tag2Integer.get(tag1);
					int int2 = this.tag2Integer.get(tag2);
					
					if (int1 != int2) {
						this.pairs.add(new Pair<>(int1,int2));
					}
				}
			}
		}
		for (Pair<Integer, Integer> p : this.pairs) {
			String tag1 = this.integer2Tag.get(p.getValue0());
			String tag2 = this.integer2Tag.get(p.getValue1());
			
			System.out.println(tag1 + "\t" + tag2);
		}
		
		this.pairsLst = new ArrayList<>(this.pairs);
	}

}
