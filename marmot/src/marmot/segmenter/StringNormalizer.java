package marmot.segmenter;

import java.io.Serializable;

public abstract class StringNormalizer implements Serializable {

	private static final long serialVersionUID = 1L;

	abstract public String normalize(String string);
	
	static public StringNormalizer labeledCreate(String lang) {
		
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
