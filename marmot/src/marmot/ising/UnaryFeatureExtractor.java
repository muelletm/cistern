package marmot.ising;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

public class UnaryFeatureExtractor extends FeatureExtractor {

	
	
	private int maxPrefix;
	private int maxSuffix;

	private Map<String, Integer> prefix2Integer;
	private Map<String, Integer> suffix2Integer;
	
	private int numFeatures = 0;
	
	private int startFeature;
	private int totalNumVariables;
	
	public UnaryFeatureExtractor(int maxPrefix, int maxSuffix) {
		this.setMaxPrefix(maxPrefix);
		this.setMaxSuffix(maxSuffix);
		
		this.setPrefix2Integer(new HashMap<String, Integer>());
		this.setSuffix2Integer(new HashMap<String, Integer>());
		
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
				
				// suffixes
				if (!this.prefix2Integer.containsKey(prefix)) {
					this.prefix2Integer.put(prefix, this.prefix2Integer.size());
				}
				
			}
		}
		for (int i = 0; i < this.maxSuffix; ++i) {
			if (word.length() - i >= 0) {
				String suffix = word.substring(word.length() - i, word.length());
				//prefixes
				if (!this.suffix2Integer.containsKey(suffix)) {
					this.suffix2Integer.put(suffix, this.suffix2Integer.size());
				}
				
			}
		}		

		int numPrefixes = this.prefix2Integer.size();
		int numSuffixes = this.suffix2Integer.size();
		this.numFeatures = this.startFeature + numSuffixes * this.totalNumVariables * 4;
	}
	
	public Pair<List<Integer>, List<Integer>> getFeatures(int variableId, String word) {
		int numSuffixes = this.suffix2Integer.size();
		int numPrefixes = this.prefix2Integer.size();
		
		int posPrefixOffset = this.startFeature;
		int negPrefixOffset = this.startFeature + numPrefixes * this.totalNumVariables;
		int posSuffixOffset = this.startFeature + numSuffixes * this.totalNumVariables * 2;
		int negSuffixOffset = this.startFeature + numSuffixes * this.totalNumVariables * 3;
		
		
		List<Integer> listPos = new ArrayList<Integer>();
		List<Integer> listNeg = new ArrayList<Integer>();

		
		for (int i = 0; i < this.maxPrefix; ++i) {
			if (i <= word.length()) {
				String prefix = word.substring(0, i);
				int prefixId = this.prefix2Integer.get(prefix) + variableId;
				listPos.add(posPrefixOffset + prefixId);
				listNeg.add(negPrefixOffset + prefixId);

			}
		}
		for (int i = 0; i < this.maxSuffix; ++i) {
			if (word.length() - i >= 0) {
				String suffix = word.substring(word.length() - i, word.length());
				int suffixId = this.suffix2Integer.get(suffix) + variableId;
		
				listPos.add(posSuffixOffset + suffixId);
				listNeg.add(negSuffixOffset + suffixId);

			}
		}
		return new Pair<>(listPos, listNeg);
	
	}

	public int numAtomicFeatures() {
		return this.prefix2Integer.size() + this.suffix2Integer.size();
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

	public Map<String, Integer> getSuffix2Integer() {
		return suffix2Integer;
	}

	public void setSuffix2Integer(Map<String, Integer> suffix2Integer) {
		this.suffix2Integer = suffix2Integer;
	}

	public Map<String, Integer> getPrefix2Integer() {
		return prefix2Integer;
	}

	public void setPrefix2Integer(Map<String, Integer> prefix2Integer) {
		this.prefix2Integer = prefix2Integer;
	}

	public int getNumFeatures() {
		return numFeatures;
	}

	public void setNumFeatures(int numFeatures) {
		this.numFeatures = numFeatures;
	}
}
