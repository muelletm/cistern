package marmot.morph.analyzer;

import java.io.Serializable;
import java.util.List;

public abstract class Analyzer implements Serializable {

	private static final long serialVersionUID = 1L;

	abstract public List<String> analyze(String form);
	
	public static Analyzer create(String lang) {
		if (lang.equals("ar")) {
			return new ArabicAnalyzer(true, true);
		}
		throw new RuntimeException("Unknown language: " + lang);
	}
}
