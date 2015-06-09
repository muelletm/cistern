package marmot.analyzer;

import java.util.Collection;

public interface Analyzer {

	Collection<AnalyzerReading> analyze(AnalyzerInstance instance);
	
}
