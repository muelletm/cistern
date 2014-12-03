package marmot.tokenize.preprocess;

import java.util.HashMap;
import java.util.Map;

public class LanguageNormalizer {
	
	Map<String, String> replaceMap_de_;
	Map<String, String> replaceMap_en_;
	Map<String, String> replaceMap_es_;
	
	public LanguageNormalizer() {
		replaceMap_de_ = new HashMap<String, String>();
		replaceMap_en_ = new HashMap<String, String>();
		replaceMap_es_ = new HashMap<String, String>();
		
		// add strings to be replaced in the tokenized version
		replaceMap_es_.put("del", "de el");

	}
	
	public String normalize(String tok, String lang) {
		String ret = tok;
		if (lang.equals("de")) {
			for (String key : replaceMap_de_.keySet()) {
				ret = ret.replace(" "+key+" ", " "+replaceMap_de_.get(key)+" ");
			}
			return ret;
		}
		if (lang.equals("en")) {
			for (String key : replaceMap_en_.keySet()) {
				ret = ret.replace(" "+key+" ", " "+replaceMap_en_.get(key)+" ");
			}
			return ret;
		}
		if (lang.equals("es")) {
			for (String key : replaceMap_es_.keySet()) {
				ret = ret.replace(" "+key+" ", " "+replaceMap_es_.get(key)+" ");
			}
			return ret;
		}
		System.err.println("Used language not recognized in LanguageNormalizer!");
		return ret;
	}
	
}


