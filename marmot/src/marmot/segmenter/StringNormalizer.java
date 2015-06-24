package marmot.segmenter;

import java.io.Serializable;

public abstract class StringNormalizer implements Serializable {

	abstract public String normalize(String string);
	
	public static StringNormalizer labeledCreate(String lang) {
		
		if (lang.equalsIgnoreCase("tur")) {
			return new TurkishStringNormalizer();
		}
		
		return new DefaultStringNormalizer();
		
	}
	
	public static StringNormalizer rawCreate(String lang) {
		
		if (lang.equalsIgnoreCase("ger")) {
			return new GermanStringNormalizer();
		}
		
		return new LowerStringNormalizer();
		
	}
	
}
