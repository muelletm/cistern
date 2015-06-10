package marmot.analyzer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AnalyzerTrainer {

	public static final String FLOAT_DICT_ = "float-dict";
	
	protected Map<String, String> options_;
	
	public abstract Analyzer train(Collection<AnalyzerInstance> instances);

	private static final String BSLASH_SYM = "%%BSLASH%%";
	private static final String COMMA_SYM = "%%COMMA%%"; 
	private static final String SEMICOL_SYM = "%%SEMICOL%%";
	
	public void setOptions(String options_string) {
		if (options_ == null) {
			options_= new HashMap<>();
		}
		
		if (options_string.equals("_"))
			return;
		
		options_string = options_string.replace("\\,", COMMA_SYM).replace("\\;", SEMICOL_SYM).replace("\\\\", BSLASH_SYM);
		
		for (String option : options_string.split(",")) {
			option = option.replace(COMMA_SYM, ",").replace(SEMICOL_SYM, ";").replace(BSLASH_SYM, "\\");
			
			int index = option.indexOf('=');
			if (index < 0) {
				throw new RuntimeException(String.format("Not = in " + option));
			}
			
			String name = option.substring(0, index);
			String value = option.substring(index + 1);
			options_.put(name, value);
		}
		
	}
	
}
