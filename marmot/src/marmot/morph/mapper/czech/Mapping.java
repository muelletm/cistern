// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.mapper.czech;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import marmot.morph.mapper.czech.PdtMorphTag.Gender;
import marmot.util.LineIterator;

public class Mapping {

	Map<String, Map<String, Set<PdtMorphTag>>> map_ = new HashMap<String, Map<String, Set<PdtMorphTag>>>();
	
	public void init_fst_map(String filename) {
		LineIterator iterator = new LineIterator(filename);
		
		map_ = new HashMap<String, Map<String, Set<PdtMorphTag>>>();
		
		while (iterator.hasNext()) {
			
			List<String> line = iterator.next();
			
			if (!line.isEmpty()) {
				
					String form = line.get(0);
					
					Map<String, Set<PdtMorphTag>> lemmas = map_.get(form);
					
					if (lemmas == null) {
						lemmas = new HashMap<String, Set<PdtMorphTag>>();
						map_.put(form, lemmas);
					}
				
					String lemma = line.get(1);
					
					Set<PdtMorphTag> tags = lemmas.get(lemma);
					
					if (tags == null) {
						tags = new HashSet<PdtMorphTag>();
						lemmas.put(lemma, tags);
					}
					
					
					String tag = line.get(2);
					String feat = line.get(3);
					
					PdtMorphTagReader reader = new PdtMorphTagReader();
					PdtMorphTag pdt_tag = reader.parse_positional(tag + feat);
					
					// System.err.println(pdt_tag.toHumanMorphString().toUpperCase() + " " + tag + feat);
					
					tags.add(pdt_tag);
					
			}
			
			
		}
		
	}
	
	public void create_mapping(String mte_file) {

		LineIterator iter = new LineIterator(mte_file);
		
		MsdReader reader = new MsdReader();
		
		PdTMsdMapper mapper = new PdTMsdMapper();
		
		while (iter.hasNext()) {
			List<String> line = iter.next();
					
			if (line.isEmpty()) {
				continue;
			}
			
			String form = line.get(0);
			
			Map<String, Set<PdtMorphTag>> map = map_.get(form);
			
			if (map != null) {
			
				String lemma = line.get(1);
				Set<PdtMorphTag> set = getLemmaSet(map, lemma);
				
				
				String msd_tag_string = line.get(2);
				MsdTag msd_tag = reader.parse(msd_tag_string);
				
				if (set.size() == 1) {
					
					PdtMorphTag pdt_tag = set.iterator().next(); 
					
					MsdTag msd_tag_mapped = mapper.map(pdt_tag);
					
					
					
					//if (!msd_tag_mapped.toHumanString().equals(msd_tag.toHumanString())) {
						
					if (pdt_tag.gender_ == Gender.q) {
						//if (msd_tag.gender_ == MsdTag.Gender.f && msd_tag.tense_ == MsdTag.Tense.s && msd_tag.number_ == MsdTag.Number.s) {
						
						//if (msd_tag.toHumanString().equals(msd_tag_mapped.toHumanString())) {
						
						System.err.println(form + " " + pdt_tag.toHumanMorphString() + " " + msd_tag_mapped.toHumanString() + " " + msd_tag.toHumanString());
						}
						//}
					
				
					//}
					
					
				}
				
				
			
			}
			
			
		}		

		
	}
	
	private Set<PdtMorphTag> getLemmaSet(Map<String, Set<PdtMorphTag>> map,
			String lemma) {
		
		Set<PdtMorphTag> set = map.get(lemma);
		
		if (set != null) {
			return set;
		}
		
		set = new HashSet<PdtMorphTag>();
		
		for (Set<PdtMorphTag> current_set : map.values()) {
			set.addAll(current_set);
		}
		
		return set;
		
	}

	public static void main(String[] args) {
		Mapping m = new Mapping();
		m.init_fst_map(args[1]);
		m.create_mapping(args[0]);
		
	}	
	
	
}
