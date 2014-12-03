package marmot.tokenize.preprocess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LanguageNormalizer {
	
	Map<String, String> replace_map_de_;
	Map<String, String> replace_map_en_;
	Map<String, String> replace_map_es_;
	
	HashMap<String, Integer> cost_map_de_;
	HashMap<String, Integer> cost_map_en_;
	HashMap<String, Integer> cost_map_es_; 
	
	int current_cost_;
	
	public LanguageNormalizer() {
		current_cost_ = 0;
		
		// list of exceptions
		replace_map_de_ = new HashMap<String, String>();
		replace_map_en_ = new HashMap<String, String>();
		replace_map_es_ = new HashMap<String, String>();
		
		cost_map_de_ = new HashMap<String, Integer>();
		cost_map_en_ = new HashMap<String, Integer>();
		cost_map_es_ = new HashMap<String, Integer>();
			
		// add strings to be replaced in the tokenized version
		replace_map_es_.put("del", "de el"); //
		//replace_map_es_.put("Fz", "");
		
		// and their associated cost
		cost_map_es_.put("del", 2);
		//cost_map_es_.put("Fz", 2);
	}
	
	public String normalize(String tok, String lang) {
		current_cost_ = 0;
		
		String ret = tok;
		if (lang.equals("de")) {
			for (String key : replace_map_de_.keySet()) {
				List<Integer> occurences = getOccurences(ret, key);
				ret = ret.replace(" "+key+" ", " "+replace_map_en_.get(key)+" ");
				current_cost_ += cost_map_de_.get(key) * occurences.size();
			}
			return ret;
		}
		if (lang.equals("en")) {
			for (String key : replace_map_en_.keySet()) {
				List<Integer> occurences = getOccurences(ret, key);
				ret = ret.replace(" "+key+" ", " "+replace_map_en_.get(key)+" ");
				current_cost_ += cost_map_en_.get(key) * occurences.size();
			}
			return ret;
		}
		if (lang.equals("es")) {
			for (String key : replace_map_es_.keySet()) {
				List<Integer> occurences = getOccurences(ret, key);
				ret = ret.replace(" "+key+" ", " "+replace_map_es_.get(key)+" ");
				current_cost_ += cost_map_es_.get(key) * occurences.size();
			}
			return ret;
		}
		System.err.println("Used language not recognized in LanguageNormalizer!");
		return ret;
	}
	
	private List<Integer> getOccurences(String t, String p) {
		List<Integer> ret = new ArrayList<Integer>();
		
		for (int i=0; i<t.length(); i++) {
			int j = 0;
			for (; j<p.length() && ((i+j) < t.length()); j++) {
				if (t.charAt(i+j) != p.charAt(j)) break;
			}
			if (j == p.length()) ret.add(i);
		}

		return ret;
	}
	
	public int getCurrentCost() {
		return current_cost_;
	}
	
}


