package experimental.analyzer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import marmot.core.Sequence;
import marmot.core.Token;
import marmot.morph.Word;
import marmot.morph.io.SentenceReader;
import marmot.util.Mutable;

public class AnalyzerInstance implements Serializable {

	private static final long serialVersionUID = 1L;
	private String form_;
	private Collection<AnalyzerReading> readings_;
	
	public AnalyzerInstance(Collection<Token> sequence) {
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
	
	public AnalyzerInstance(String form, Collection<AnalyzerReading> readings) {
		form_ = form;
		readings_ = readings;
	}

	public static Collection<AnalyzerInstance> getInstances(String filename) {
		List<AnalyzerInstance> list = new LinkedList<>();
		for (Sequence sequence : new SentenceReader(filename)) {
			list.add(new AnalyzerInstance(sequence));
		}
		return list;
	}
	
	public static Collection<AnalyzerInstance> getTreebankInstances(String filename) {
		Map<String, Map<AnalyzerReading, Mutable<Integer>>> map = new HashMap<>();
				
		for (Sequence sequence : new SentenceReader(filename)) {
			for (Token token : sequence) {
				Word word = (Word) token;
				Map<AnalyzerReading, Mutable<Integer>> instance = map.get(word.getWordForm());
				if (instance == null) {
					instance = new HashMap<>();
					map.put(word.getWordForm(), instance);
				}
				AnalyzerReading reading = new AnalyzerReading(word);
				Mutable<Integer> i = instance.get(reading);
				if (i == null) {
					i = new Mutable<Integer>(0);
					instance.put(reading, i);
				}
				i.set(i.get() + 1);
			}	
		}

		List<AnalyzerInstance> list = new LinkedList<>();
		for (Map.Entry<String, Map<AnalyzerReading, Mutable<Integer>>> entry : map.entrySet()) {
			String word = entry.getKey();
			Map<AnalyzerReading, Mutable<Integer>> m = entry.getValue();
			
			Collection<AnalyzerReading> readings = new LinkedList<>();
			for (Map.Entry<AnalyzerReading, Mutable<Integer>> m_entry : m.entrySet()) {
				AnalyzerReading reading = m_entry.getKey();
				Mutable<Integer> i = m_entry.getValue();
				reading.setCount(i.get());
				readings.add(reading);		
			}
			
			list.add(new AnalyzerInstance(word, readings));
			
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
