package marmot.analyzer;

import java.util.Collection;

public interface AnalyzerTrainer {

	Analyzer train(Collection<AnalyzerInstance> instances);
	
}
