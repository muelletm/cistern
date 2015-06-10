package marmot.ising;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

public class UnaryFeatureExtractor extends FeatureExtractor {

	private int maxPrefix;
	private int maxSuffix;

	private Map<Pair<Integer,String>, Integer> prefixFeatures;
	private Map<Pair<Integer,String>, Integer> suffixFeatures;

	private Map<Integer, String> int2Features;

	
	private int numFeatures = 0;
	
	private int startFeature;
	private int totalNumVariables;
	
	public UnaryFeatureExtractor(int maxPrefix, int maxSuffix) {
		this.setMaxPrefix(maxPrefix);
		this.setMaxSuffix(maxSuffix);
		
		this.prefixFeatures = new HashMap<Pair<Integer,String>,Integer>();
		this.suffixFeatures = new HashMap<Pair<Integer,String>,Integer>();
		
		this.int2Features = new HashMap<Integer,String>();

		
		setNumFeatures(0);

	}
	
	/** Feature Templates
	 * TEMPLATE(SUB<
	 * @param feature
	 * @param tag
	 */
	public void extract(String word) {
		for (int i = 0; i < this.maxPrefix; ++i) {
			if (i <= word.length()) {
				String prefix = word.substring(0, i);
				
				for (int v = 0; v < this.totalNumVariables; ++v) {
					Pair<Integer,String> key = new Pair<>(v, prefix);
					if (!this.prefixFeatures.containsKey(key)) {
						this.prefixFeatures.put(key,numFeatures);
						this.int2Features.put(numFeatures, "" + key + "\t POS");
						this.int2Features.put(numFeatures + 1, "" + key + "\t NEG");

						numFeatures += 2;
					}	
				}
			}
		}
		for (int i = 0; i < this.maxSuffix; ++i) {
			if (word.length() - i >= 0) {
				String suffix = word.substring(word.length() - i, word.length());
		
				for (int v = 0; v < this.totalNumVariables; ++v) {
					Pair<Integer,String> key = new Pair<>(v, suffix);
					if (!this.suffixFeatures.containsKey(key)) {
						this.suffixFeatures.put(key,numFeatures);
						
						this.int2Features.put(numFeatures, "" + key + "\t POS");
						this.int2Features.put(numFeatures + 1, "" + key + "\t NEG");
						numFeatures += 2;
					}				
				}
				
			}
		}		

	}
	
	public Pair<List<Integer>, List<Integer>> getFeatures(int variableId, String word) {
		List<Integer> featuresPos = new LinkedList<Integer>();
		List<Integer> featuresNeg = new LinkedList<Integer>();
		
		for (int i = 0; i < this.maxPrefix; ++i) {
			if (i <= word.length()) {
				String prefix = word.substring(0, i);
				
				Pair<Integer,String> key = new Pair<>(variableId, prefix);
					
				int feat = this.prefixFeatures.get(key);
				featuresPos.add(feat);
				featuresNeg.add(feat + 1);
				
			}
		}
		for (int i = 0; i < this.maxSuffix; ++i) {
			if (word.length() - i >= 0) {
				String suffix = word.substring(word.length() - i, word.length());
		
				
				Pair<Integer,String> key = new Pair<>(variableId, suffix);
					
				int feat = this.suffixFeatures.get(key);
				featuresPos.add(feat);
				featuresNeg.add(feat + 1);
				
			
			}
		}		
		return new Pair<>(featuresPos, featuresNeg);
	
	}
	
	public int getStartFeature() {
		return startFeature;
	}

	public void setStartFeature(int startFeature) {
		this.startFeature = startFeature;
	}
	

	public int getTotalNumVariables() {
		return totalNumVariables;
	}

	public void setTotalNumVariables(int totalNumVariables) {
		this.totalNumVariables = totalNumVariables;
	}
	
	
	public int getMaxSuffix() {
		return maxSuffix;
	}

	public void setMaxSuffix(int maxSuffix) {
		this.maxSuffix = maxSuffix;
	}

	public int getMaxPrefix() {
		return maxPrefix;
	}

	public void setMaxPrefix(int maxPrefix) {
		this.maxPrefix = maxPrefix;
	}

	public int getNumFeatures() {
		return numFeatures;
	}

	public void setNumFeatures(int numFeatures) {
		this.numFeatures = numFeatures;
	}
	
	public Map<Integer,String> getInt2Feature() {
		return this.int2Features;
	}
}
