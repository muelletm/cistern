package marmot.experimental.analyzer;

import java.io.Serializable;
import java.util.Collection;

public interface Analyzer extends Serializable {

	Collection<AnalyzerReading> analyze(AnalyzerInstance instance);

	String represent(AnalyzerInstance instance);

	int getNumTags();
	
}
