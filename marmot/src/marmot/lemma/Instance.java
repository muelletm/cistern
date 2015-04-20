// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

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
	String ptag_;
	String mtag_;
	
	@Override
	public String toString() {
		String ptag = "_";
		if (ptag_ != null)
			ptag = ptag_;
		
		String mtag = "_";
		if (mtag_ != null)
			mtag = mtag_;
		
		return String.format("%s\t%s\t%s\t%s", form_, ptag, mtag, lemma_);
	}
	
	public Instance(String form, String lemma, String tag, String mtag) {
		count_ = 1;
		form_ = form;
		lemma_ = lemma;
		ptag_ = tag;
		mtag_ = mtag;
	}

	
	public String getForm() {
		return form_;
	}
	
	public String getLemma() {
		return lemma_;
	}
	
	public String getFormPadded() {
		return "123" + form_ + "456";
	}
	
	public String getLemmaPadded() {
		return lemma_;
	}

	
	public String getPosTag() {
		return ptag_;
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
		result = prime * result + ((ptag_ == null) ? 0 : ptag_.hashCode());
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
		if (ptag_ == null) {
			if (other.ptag_ != null)
				return false;
		} else if (!ptag_.equals(other.ptag_))
			return false;
		return true;
	}
	
	public static List<Instance> getInstances(Iterable<Sequence> reader) {
		return getInstances(reader, -1);
	}
	
	public static List<Instance> getInstances(Iterable<Sequence> reader, int limit) {
		return getInstances(reader, limit, true, true);
	}
	
	public static List<Instance> getInstances(Iterable<Sequence> reader, boolean use_ptag, boolean use_mtag) {
		return getInstances(reader, -1, use_ptag, use_mtag);
	}
	
	public static List<Instance> getInstances(Iterable<Sequence> reader, int limit, boolean use_postag, boolean use_mtag) {
		 
		Map<Instance, Mutable<Integer>> map = new HashMap<>();
		
		int number = 0;
		for (Sequence sentence : reader) {
			for (Token token : sentence) {
				
				number ++;
				
				Instance instance = Instance.getInstance((Word) token, use_postag, use_mtag);
				
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

	public String getMorphTag() {
		return mtag_;
	}

	public static Instance getInstance(Word word, boolean use_postag, boolean use_mtag) {
		String form = word.getWordForm();
		
		if (form == null) {
			throw new RuntimeException("Form is null. Did you specify a form-index?");
		}
		
		String lemma = word.getLemma();
		
		if (lemma == null) {
			throw new RuntimeException("Lemma is null. Did you specify a lemma-index?");
		}
		
		form = form.toLowerCase();
		lemma = lemma.toLowerCase();
		return  new Instance(form, lemma, (use_postag)? word.getPosTag() : null, (use_mtag) ? word.getMorphTag() : null);	
	}
	
	public static Instance getInstance(Word word) {
		return getInstance(word, true, true);	
	}

}
