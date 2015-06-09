package marmot.ising;

import java.util.HashMap;
import java.util.Map;

public class UnaryFeatureExtractor extends FeatureExtractor {

	
	
	private int maxPrefix;
	private int maxSuffix;

	private Map<String, Integer> prefix2Integer;
	private Map<String, Integer> suffix2Integer;
	
	public UnaryFeatureExtractor(int maxPrefix, int maxSuffix) {
		this.setMaxPrefix(maxPrefix);
		this.setMaxSuffix(maxSuffix);
		
		this.setPrefix2Integer(new HashMap<String, Integer>());
		this.setSuffix2Integer(new HashMap<String, Integer>());

	}
	
	/** Feature Templates
	 * TEMPLATE(SUB<
	 * @param feature
	 * @param tag
	 */
	public void extract(String word) {
		for (int i = 0; i < this.maxPrefix; ++i) {
			String prefix = word.substring(0, i);
			String suffix = word.substring(word.length() - i, word.length());

			// suffixes
			if (!this.prefix2Integer.containsKey(prefix)) {
				this.prefix2Integer.put(prefix, this.prefix2Integer.size());
			}
			
			//prefixes
			if (!this.suffix2Integer.containsKey(suffix)) {
				this.suffix2Integer.put(suffix, this.suffix2Integer.size());
			}
			
		}
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
}
