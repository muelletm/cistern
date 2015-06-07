package marmot.ising;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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
	            	
	            	String POS = splitted[2].split(":")[0];
	            	String[] attributes = {};
	            	
	            	if (splitted[2].split(":").length > 1) {
	            		attributes = splitted[2].split(":")[1].split("\\+");
	            	}
	            
	            	// cache features
            		if (!this.atoms2Integer.keySet().contains(POS)) {
            			this.atoms2Integer.put(POS, this.atoms2Integer.size());
            			this.integer2Atoms.put(this.atoms2Integer.size()-1, POS);
            		}
            		
	            	for (String attribute : attributes) {
	            		if (!this.atoms2Integer.keySet().contains(attribute)) {
		            		this.atoms2Integer.put(attribute, this.atoms2Integer.size());
		            		this.integer2Atoms.put(this.atoms2Integer.size()-1, attribute);
	            		}

	            	}
            		this.numVariables = this.atoms2Integer.size();

	            	
	            	// extract features
	            	int[] features = new int[attributes.length + 1];
	            	features[0] = this.atoms2Integer.get(POS);
	            	
	            	int counter = 1;
	            	for (String attribute : attributes) {
	            		features[counter] = this.atoms2Integer.get(attribute);
	            		++counter;
	            	}
	            	// get edges in the graph
	            	// double counts but whatever
	            	for (int f1 : features) {
	            		for (int f2 : features)  {
	            			this.pairs.add(new Pair<>(f1, f2));
	            		}
	            	}
	            	
	            	this.data.add(new Datum(word,lemma, features));
	            
	            }
	            
	            for (int varId = 0; varId < this.numVariables; ++varId) {
	            	this.tagNames.add(this.integer2Atoms.get(varId));
	            }
	           
	            
	            this.pairsLst = new ArrayList<Pair<Integer, Integer>>(this.pairs);
	   
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
	}

}
