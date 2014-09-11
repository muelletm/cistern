// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.mapper.latin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import marmot.morph.mapper.latin.LdtMorphTag.Case;
import marmot.morph.mapper.latin.LdtMorphTag.Degree;
import marmot.morph.mapper.latin.LdtMorphTag.Gender;
import marmot.morph.mapper.latin.LdtMorphTag.Mood;
import marmot.morph.mapper.latin.LdtMorphTag.Person;
import marmot.morph.mapper.latin.LdtMorphTag.Pos;
import marmot.morph.mapper.latin.LdtMorphTag.Tense;
import marmot.morph.mapper.latin.LdtMorphTag.Voice;
import marmot.util.LineIterator;

public class LatMorReader {

	Map<String, Map<String, Set<LdtMorphTag>>> dict_;

	public LatMorReader() {
		dict_ = new HashMap<String, Map<String, Set<LdtMorphTag>>>();
	}

	public LatMorReader(Map<String, Map<String, Set<LdtMorphTag>>> dict) {
		dict_ = dict;
	}

	public void readLatMorFile(String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filename));

		String line = null;

		while (reader.ready()) {
			if (line == null)
				line = reader.readLine().trim();

			if (!line.isEmpty()) {
				assert line.startsWith("> ");

				String form = LatMorNormalizer.normalize(line.substring(2));

				Map<String, Set<LdtMorphTag>> readings = dict_.get(form);
				// assert readings == null;

				boolean insert = false;
				if (readings == null) {
					insert = true;
					readings = new HashMap<String, Set<LdtMorphTag>>();

				}

				line = readReadings(reader, readings);

				if (insert && !readings.isEmpty())
					dict_.put(form, readings);

			}

		}

		reader.close();
	}

	public void readMissingFile(String filename) throws IOException {
		LineIterator iterator = new LineIterator(filename);

		while (iterator.hasNext()) {

			List<String> line = iterator.next();
			
			if (line.size() > 2) {

				String form = LatMorNormalizer.normalize(line.get(0));
				
				Set<LdtMorphTag> tags = new HashSet<>();
				
				for (String tag_string : line.subList(2, line.size())) {
					LdtMorphTag tag = new LdtMorphTag();
					switch (tag_string) {
					case "aj":
						tag.pos_ = Pos.a;
						break;
					case "su":
						tag.pos_ = Pos.n;
						break;
					case "np":
						tag.pos_ = Pos.n;
						break;
					case "pn":
						tag.pos_ = Pos.p;
						break;
					case "nu":
						tag.pos_ = Pos.m;
						break;
					default:
						System.err.println("Unknown tag: " + tag_string);
					}
					if (tag.pos_ != Pos.Undef) {
						tags.add(tag);
					}
				}
				
				if (tags.size() > 0) {
					Map<String, Set<LdtMorphTag>> readings = dict_.get(form);
					if (readings == null) {
						readings = new HashMap<String, Set<LdtMorphTag>>();
						dict_.put(form, readings);
					}					
					readings.put("_", tags);
				} 
			}

		}
	}

	public String readReadings(BufferedReader reader,
			Map<String, Set<LdtMorphTag>> readings) throws IOException {
		String line = null;

		while (reader.ready()) {

			line = reader.readLine().trim();

			if (line.startsWith(">"))
				break;

			if (line.startsWith("no result for ")) {
				continue;
			}

			parseReading(line, readings);

		}

		return line;
	}

	public void parseReading(String line, Map<String, Set<LdtMorphTag>> readings) {

		int morph_start = line.indexOf('<');

		if (morph_start < 0) {
			throw new RuntimeException("Invalid reading: " + line);
		}

		String lemma = LatMorNormalizer.normalize(line
				.substring(0, morph_start).replace("-", ""));
				
		String morph_tag_string = line.substring(morph_start);

		Set<LdtMorphTag> set = readings.get(lemma);
		if (set == null) {
			set = new HashSet<LdtMorphTag>();
			readings.put(lemma, set);
		}

		set.add(parseMorpTagString(morph_tag_string));

	}

	public LdtMorphTag parseMorpTagString(String morph_tag_string) {
		LdtMorphTag tag = new LdtMorphTag();

		String[] features = morph_tag_string.split("[<>]");

		for (String feature : features) {
			if (!feature.isEmpty()) {
				setFeature(tag, feature);
			}
		}

		postProcess(tag);

		return tag;
	}

	public void postProcess(LdtMorphTag tag) {
		if (tag.pos_ == Pos.p) {
			tag.person_ = Person.Undef;
		}
		if (tag.pos_ == Pos.r) {
			tag.case_ = Case.Undef;
		}

	}

	public static void setFeature(LdtMorphTag tag, String feature) {
		switch (feature) {

		// POS

		case "N":
		case "PN":
			tag.pos_ = Pos.n;
			break;

		case "V":
			tag.pos_ = Pos.v;

			if (tag.mood_ == Mood.p)
				tag.pos_ = Pos.t;

			break;

		case "ADJ":
			tag.pos_ = Pos.a;
			break;

		case "PREP":
			tag.pos_ = Pos.r;
			break;

		case "ADV":
			tag.pos_ = Pos.d;
			break;

		case "CONJ":
			tag.pos_ = Pos.c;
			break;

		case "PRO":
			tag.pos_ = Pos.p;
			break;

		case "NUM":
			tag.pos_ = Pos.m;
			break;

		// Person

		case "1":
			tag.person_ = Person.first;
			break;

		case "2":
			tag.person_ = Person.second;
			break;

		case "3":
			tag.person_ = Person.third;
			break;

		// Number

		case "sg":
			tag.number_ = LdtMorphTag.Number.s;
			break;

		case "pl":
			tag.number_ = LdtMorphTag.Number.p;
			break;

		// Tense

		case "pres":
			tag.tense_ = Tense.p;
			break;

		case "imperf":
			tag.tense_ = Tense.i;
			break;

		case "perf":
			tag.tense_ = Tense.r;
			break;

		case "pqperf":
			tag.tense_ = Tense.l;
			break;

		case "futureII":
			tag.tense_ = Tense.t;
			break;

		case "future":
		case "futureI":
			tag.tense_ = Tense.f;
			break;

		// Mood

		case "ind":
			tag.mood_ = Mood.i;
			break;

		case "cond":
			tag.mood_ = Mood.s;

		case "inf":
			tag.mood_ = Mood.n;
			break;

		case "imp":
			tag.mood_ = Mood.m;
			break;

		case "part":
			tag.mood_ = Mood.p;
			if (tag.pos_ == Pos.v)
				tag.pos_ = Pos.t;
			break;

		case "gerund":
			tag.mood_ = Mood.d;
			break;

		case "gerundivum":
			tag.mood_ = Mood.g;
			break;

		case "supinI":
		case "supinII":
			tag.mood_ = Mood.u;
			break;

		// Voice

		case "active":
			tag.voice_ = Voice.a;
			break;

		case "passive":
		case "deponens":
			tag.voice_ = Voice.p;
			break;

		// Gender

		case "masc":
			tag.gender_ = Gender.m;
			break;

		case "fem":
			tag.gender_ = Gender.f;
			break;

		case "neut":
			tag.gender_ = Gender.n;
			break;

		// Case

		case "nom":
			tag.case_ = Case.n;
			break;

		case "gen":
			tag.case_ = Case.g;
			break;

		case "dat":
			tag.case_ = Case.d;
			break;

		case "acc":
			tag.case_ = Case.a;
			break;

		case "abl":
			tag.case_ = Case.b;
			break;

		case "voc":
			tag.case_ = Case.v;
			break;

		// LOCATIVE

		// Degree

		case "superlative":
			tag.degree_ = Degree.s;
			break;

		case "comparative":
			tag.degree_ = Degree.c;
			break;

		case "INTJ":
			tag.pos_ = LdtMorphTag.Pos.i;

		case "subj":
		case "positive":
		case "conj":
		case "alt":
		case "coord":
		case "dem":
		case "indef":
		case "subord":
		case "dist":
		case "card":
		case "rel":
		case "quest":
		case "ord":
		case "poss":
		case "refl":
		case "pers":
		case "dig":
		case "adj":
			break;

		default:
			throw new RuntimeException("Unknown feature: " + feature);
		}

	}

//	private Set<Pos> getPosCandidates(String form) {
//		return getPosCandidates(form, null);
//	}

	Set<Pos> getPosCandidates(String form, String lemma) {
		Map<String, Set<LdtMorphTag>> lemmas = dict_.get(form);
		Set<Pos> tags = new HashSet<>();

		if (lemmas != null) {
			Set<LdtMorphTag> ldt_tags;
			if (lemma != null) {
				ldt_tags = lemmas.get(lemma);
			} else {
				ldt_tags = new HashSet<>();
				for (Set<LdtMorphTag> ldt_tag_set : lemmas.values()) {
					ldt_tags.addAll(ldt_tag_set);
				}
			}

			if (ldt_tags != null) {
				for (LdtMorphTag ldt_tag : ldt_tags) {
					tags.add(ldt_tag.pos_);
				}
			}
		}

		return tags;
	}

	public void readWrongFile(String filename) {
		LineIterator iterator = new LineIterator(filename);

		while (iterator.hasNext()) {

			List<String> line = iterator.next();
			
			if (line.size() > 3) {

				String form = LatMorNormalizer.normalize(line.get(1));
				
				Set<LdtMorphTag> tags = new HashSet<>();
				
				for (String tag_string : line.subList(3, line.size())) {
					LdtMorphTag tag = new LdtMorphTag();
					switch (tag_string) {
					case "a":
						tag.pos_ = Pos.a;
						break;
					case "n":
						tag.pos_ = Pos.n;
						break;
					case "p":
						tag.pos_ = Pos.p;
						break;
					default:
						System.err.println("Unknown tag: " + tag_string);
					}
					if (tag.pos_ != Pos.Undef) {
						tags.add(tag);
					}
				}
				
				if (tags.size() > 0) {
					Map<String, Set<LdtMorphTag>> readings = dict_.get(form);
					if (readings == null) {
						readings = new HashMap<String, Set<LdtMorphTag>>();
						dict_.put(form, readings);
					}					
					readings.put(line.get(2), tags);
				} 
			}

		}
	}

	public Set<String> getLemmas(String form) {
		Map<String, Set<LdtMorphTag>> lemmas = dict_.get(form);
		if (lemmas == null) {
			return null;
		}
		return lemmas.keySet();
	}

}
