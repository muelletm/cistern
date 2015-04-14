package marmot.lemma;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marmot.core.Sequence;
import marmot.core.Token;
import marmot.morph.Word;
import marmot.morph.io.SentenceReader;
import marmot.util.Mutable;

public class Instance {

	double count_;
	String form_;
	String lemma_;
	String tag_;
	String mtag_;
	
	@Override
	public String toString() {
		return String.format("%s %s : %s", form_, tag_, lemma_);
	}
	
	public Instance(String form, String lemma, String tag, String mtag) {
		count_ = 1;
		form_ = form;
		lemma_ = lemma;
		tag_ = tag;
		mtag_ = mtag;
	}

	public String getForm() {
		return form_;
	}
	
	public String getLemma() {
		return lemma_;
	}

	public String getPosTag() {
		return tag_;
	}
	
	public void setCount(double count) {
		count_ = count;
	}
	
	public double getCount() {
		return count_;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((form_ == null) ? 0 : form_.hashCode());
		result = prime * result + ((lemma_ == null) ? 0 : lemma_.hashCode());
		result = prime * result + ((mtag_ == null) ? 0 : mtag_.hashCode());
		result = prime * result + ((tag_ == null) ? 0 : tag_.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Instance other = (Instance) obj;
		if (form_ == null) {
			if (other.form_ != null)
				return false;
		} else if (!form_.equals(other.form_))
			return false;
		if (lemma_ == null) {
			if (other.lemma_ != null)
				return false;
		} else if (!lemma_.equals(other.lemma_))
			return false;
		if (mtag_ == null) {
			if (other.mtag_ != null)
				return false;
		} else if (!mtag_.equals(other.mtag_))
			return false;
		if (tag_ == null) {
			if (other.tag_ != null)
				return false;
		} else if (!tag_.equals(other.tag_))
			return false;
		return true;
	}
	
	public static List<Instance> getInstances(SentenceReader reader) {
		return getInstances(reader, -1);
	}
	
	public static List<Instance> getInstances(SentenceReader reader, int limit) {
		 
		Map<Instance, Mutable<Integer>> map = new HashMap<>();
		
		int number = 0;
		for (Sequence sentence : reader) {
			for (Token token : sentence) {
				
				number ++;
				
				Word word = (Word) token;
				String form = word.getWordForm().toLowerCase();
				String lemma = word.getLemma().toLowerCase();			
				Instance instance = new Instance(form, lemma, word.getPosTag(), word.getMorphTag());
				
				Mutable<Integer> mi = map.get(instance);
				if (mi == null) {
					mi = new Mutable<Integer>(0);
					map.put(instance, mi);
				}
				
				mi.set(mi.get() + 1);
			}
			
			if (limit >= 0 && number > limit)
				break;
			
		}
		
		List<Instance> instances = new LinkedList<Instance>();
		for (Map.Entry<Instance, Mutable<Integer>> entry : map.entrySet()) {

			Instance instance = entry.getKey();
			double count = entry.getValue().get();
			
			instance.setCount(count);
			instances.add(instance);
		}

		return instances;
	}

	public static List<Instance> getInstances(String file) {
		return getInstances(new SentenceReader(file));
	}

}
