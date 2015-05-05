// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morfessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import marmot.util.FileUtils;

/* This is a java port of the perl code in bin/expandmorphsegmentations.pl
 * and two lines of test/Makefile of morfessor_catmap0.9.2
 */

public class Expander implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Pattern LINE_PATTERN_ = Pattern
			.compile("^1 (\\*[1-4]?) (.+)$");
	private Map<String, List<Morpheme>> sub_morphs_;

	public Expander(String segmentation_file) {
		init(segmentation_file);
	}

	private void init(String segmentation_file) {
		sub_morphs_ = new HashMap<String, List<Morpheme>>();
		try {
			BufferedReader reader = FileUtils.openFile(segmentation_file);
			while (reader.ready()) {
				String line = reader.readLine();
				Matcher m = LINE_PATTERN_.matcher(line);
				if (m.matches()) {
					String type = m.group(1);
					List<Morpheme> morphs = Morpheme.split(m.group(2));
					sub_morphs_.put(Morpheme.join(morphs, false, false, "") + type, morphs);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}

	public List<Morpheme> expand(List<Morpheme> morphs) {
		return expand(morphs, false);
	}
	
	public List<Morpheme> expand(List<Morpheme> morphs, boolean split_all ) {
		List<Morpheme> morph_list = new LinkedList<Morpheme>();
		for (Morpheme morph : morphs) {
			morph_list.addAll(expand(morph, split_all));
		}
		
		ListIterator<Morpheme> iterator = morph_list.listIterator();
		
		// Merge consecutive non-morphemes
		while (iterator.hasNext()) {
			Morpheme morph = iterator.next();			
			if (morph.isNonMorpheme() && iterator.hasNext()) {
				Morpheme next_morph = iterator.next();
				
				if (next_morph.isNonMorpheme()) {
					morph.setMorpheme(morph.getMorpheme() + next_morph.getMorpheme());
					morph.setAsterisk(null);
					iterator.remove();
					iterator.previous();
				}
			}
		}
		
		for (Morpheme morph : morph_list) {
			if (morph.isNonMorpheme()) {
				morph.setTag(Morpheme.STEM);
			}
		}
		
		return morph_list;
	}
	
	public List<Morpheme> expand(Morpheme morph, boolean split_all) {
		if (morph.isNonMorpheme()) {
			return Collections.singletonList(morph);
		}

		List<Morpheme> sub_morphs = sub_morphs_.get(morph.getMorphAsterisk());

		if (sub_morphs == null) {
			return Collections.singletonList(morph);
		}

		List<Morpheme> morph_list = new LinkedList<Morpheme>();
		for (Morpheme sub_morph : sub_morphs) {
			if (sub_morph.isNonMorpheme() && (!split_all)) {
				// Don't expand to non-morphemes!
				return Collections.singletonList(morph);
			}
			morph_list.addAll(expand(sub_morph, split_all));
		}
		return morph_list;
	}
	
}
