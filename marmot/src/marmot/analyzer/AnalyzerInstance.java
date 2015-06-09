package marmot.analyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import marmot.core.Sequence;
import marmot.core.Token;
import marmot.morph.Word;
import marmot.morph.io.SentenceReader;

public class AnalyzerInstance {

	private String form_;
	private Collection<AnalyzerReading> readings_;
	
	public AnalyzerInstance(Sequence sequence) {
		form_ = null;
		Set<AnalyzerReading> tags = new HashSet<>();
		for (Token token : sequence) {
			Word word = (Word) token;
			if (form_ == null) {
				form_ = word.getWordForm();
			}
			assert form_.equals(word.getWordForm());
			AnalyzerReading tag = new AnalyzerReading(word);
			tags.add(tag);
		}
		readings_ = new ArrayList<>(tags);
	}
	
	public static Collection<AnalyzerInstance> getInstances(String filename) {
		List<AnalyzerInstance> list = new LinkedList<>();
		for (Sequence sequence : new SentenceReader(filename)) {
			list.add(new AnalyzerInstance(sequence));
		}
		return list;
	}

	public String getForm() {
		return form_;
	}

	public Collection<AnalyzerReading> getReadings() {
		return readings_;
	}
	
}
