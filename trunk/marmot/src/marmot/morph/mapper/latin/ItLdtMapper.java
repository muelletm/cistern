// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.mapper.latin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import marmot.morph.mapper.MorphTag;
import marmot.morph.mapper.latin.ItMorphTag.CaseNumber;
import marmot.morph.mapper.latin.ItMorphTag.FlexionalCategory;
import marmot.morph.mapper.latin.ItMorphTag.NominalsDegree;
import marmot.morph.mapper.latin.LdtMorphTag.Case;
import marmot.morph.mapper.latin.LdtMorphTag.Degree;
import marmot.morph.mapper.latin.LdtMorphTag.Gender;
import marmot.morph.mapper.latin.LdtMorphTag.Mood;
import marmot.morph.mapper.latin.LdtMorphTag.Person;
import marmot.morph.mapper.latin.LdtMorphTag.Pos;
import marmot.morph.mapper.latin.LdtMorphTag.Tense;
import marmot.morph.mapper.latin.LdtMorphTag.Voice;

public class ItLdtMapper {

	public MorphTag convert(ItMorphTag it_tag) {
		LdtMorphTag tag = new LdtMorphTag();

		if (it_tag.case_number == CaseNumber.G) {
			return tag;
		}
		
		setPerson(tag, it_tag);
		setNumber(tag, it_tag);
		setTense(tag, it_tag);
		setMoodAndVoice(tag, it_tag);
		setGender(tag, it_tag);
		setCase(tag, it_tag);
		setDegree(tag, it_tag);

		return tag;
	}

	private void setDegree(LdtMorphTag ldt_tag, ItMorphTag it_tag) {

		switch (it_tag.participials_degree_) {
		case Two:
			ldt_tag.degree_ = Degree.c;
			break; // Comparative 2
		case Three:
			ldt_tag.degree_ = Degree.s;
			break; // Superlative 3
		case Undef:
		case One:
			ldt_tag.degree_ = Degree.Undef;
			break; // 6 Participials-Degree Positive 1
		}

	}

	private void setCase(LdtMorphTag ldt_tag, ItMorphTag it_tag) {
		switch (it_tag.case_number) {
		case A: // Singular Nominative A
		case J: // Plural Nominative J
			ldt_tag.case_ = Case.n;
			break;
		case B: // Singular Genitive B
		case K: // Plural Genitive K
			ldt_tag.case_ = Case.g;
			break;
		case C: // Singular Dative C
		case L: // Plural Dative L
			ldt_tag.case_ = Case.d;
			break;
		case D: // Singular Accusative D
		case M: // Plural Accusative M
			ldt_tag.case_ = Case.a;
			break;
		case E:
		case N:
			ldt_tag.case_ = Case.v;
			break;
		case F: // Singular Ablative F
		case O: // Plural Ablative O
			ldt_tag.case_ = Case.b;
			break;
		case G: // Adverbial G
		case H: // Casus “plurimus” H
		case Undef: // None -
		default:
			ldt_tag.case_ = Case.Undef;
		}

	}

	public Set<Pos> getPosCandidates(LdtMorphTag ldt_tag, ItMorphTag it_tag) {
		Set<Pos> candidates = new HashSet<>();

		if (it_tag.case_number == CaseNumber.G) {
			candidates.add(Pos.d);
			return candidates;
		}
		
		switch (it_tag.flexional_type_) {

		case One:

			if (it_tag.nominals_degree_ == NominalsDegree.One
					|| it_tag.nominals_degree_ == NominalsDegree.Undef) {
				candidates.add(Pos.n);
				candidates.add(Pos.a);
				candidates.add(Pos.m);
				candidates.add(Pos.p);
			} else {
				candidates.add(Pos.a);
			}
			break;

		case Two:
			candidates.add(Pos.t);
			break;

		case Three:
			candidates.add(Pos.v);
			break;

		case Four:
			if (it_tag.flexional_category_ == FlexionalCategory.O) {
				candidates.add(Pos.c);
				candidates.add(Pos.d);
			} else {
				candidates.add(Pos.r);
			}
			break;

		case Five:
			if (it_tag.flexional_category_ == FlexionalCategory.G) {
				candidates.add(Pos.m);
			}
			break;

		default:
			break;

		}

		return candidates;
	}

	private void setGender(LdtMorphTag ldt_tag, ItMorphTag it_tag) {
		switch (it_tag.gender_number_person_) {
		case One:
			ldt_tag.gender_ = Gender.m;
			break;
		case Two:
			ldt_tag.gender_ = Gender.f;
			break;
		case Three:
			ldt_tag.gender_ = Gender.n;
			break;
		default:
			ldt_tag.gender_ = Gender.Undef;
			break;
		}
	}

	private void setMoodAndVoice(LdtMorphTag ldt_tag, ItMorphTag it_tag) {
		switch (it_tag.mood_) {
		case A:
			ldt_tag.voice_ = Voice.a;
			ldt_tag.mood_ = Mood.i;
			break; // Active indicative A
		case J:
			ldt_tag.voice_ = Voice.p;
			ldt_tag.mood_ = Mood.i;
			break; // Pass/Dep indicative J
		case B:
			ldt_tag.voice_ = Voice.a;
			ldt_tag.mood_ = Mood.s;
			break; // Active subjunctive B
		case K:
			ldt_tag.voice_ = Voice.p;
			ldt_tag.mood_ = Mood.s;
			break; // Pass/Dep subjunctive K
		case C:
			ldt_tag.voice_ = Voice.a;
			ldt_tag.mood_ = Mood.m;
			break; // Active imperative C
		case L:
			ldt_tag.voice_ = Voice.p;
			ldt_tag.mood_ = Mood.m;
			break; // Pass/Dep imperative L
		case D:
			ldt_tag.voice_ = Voice.a;
			ldt_tag.mood_ = Mood.p;
			break; // Active participle D
		case M:
			ldt_tag.voice_ = Voice.p;
			ldt_tag.mood_ = Mood.p;
			break; // Pass/Dep Participle M

		case E:
			ldt_tag.voice_ = Voice.a;
			ldt_tag.mood_ = Mood.d;
			break; // Active gerund E
		case N:
			ldt_tag.voice_ = Voice.p;
			ldt_tag.mood_ = Mood.d;
			break; // Passive Gerund N
		case O:
			ldt_tag.voice_ = Voice.p;
			ldt_tag.mood_ = Mood.g;

			break; // Pass/Dep gerundive O
		case G:
			ldt_tag.voice_ = Voice.a;
			ldt_tag.mood_ = Mood.u;
			break; // Active supine G
		case P:
			ldt_tag.voice_ = Voice.p;
			ldt_tag.mood_ = Mood.u;
			break; // Pass/Dep supine P
		case H:
			ldt_tag.voice_ = Voice.a;
			ldt_tag.mood_ = Mood.n;
			break; // Active infinitive H
		case Q:
			ldt_tag.voice_ = Voice.p;
			ldt_tag.mood_ = Mood.n;
			break; // Pass/Dep infinitive Q
		case Undef:
			ldt_tag.voice_ = Voice.Undef;
			ldt_tag.mood_ = Mood.Undef;
			break; // None -
		}

	}

	private void setTense(LdtMorphTag ldt_tag, ItMorphTag it_tag) {
		switch (it_tag.tense_) {

		case One:
			ldt_tag.tense_ = Tense.p;
			break;
		case Two:
			ldt_tag.tense_ = Tense.i;
			break;
		case Three:
			ldt_tag.tense_ = Tense.f;
			break;
		case Four:
			ldt_tag.tense_ = Tense.r;
			break;
		case Five:
			ldt_tag.tense_ = Tense.l;
			break;
		case Six:
			ldt_tag.tense_ = Tense.t;
			break;
		default:
			ldt_tag.tense_ = Tense.Undef;
			break;
		}

	}

	private void setNumber(LdtMorphTag ldt_tag, ItMorphTag it_tag) {
		switch (it_tag.gender_number_person_) {
		case Four:
		case Five:
		case Six:
			ldt_tag.number_ = LdtMorphTag.Number.s;
			break;
		case Seven:
		case Eight:
		case Nine:
			ldt_tag.number_ = LdtMorphTag.Number.p;
			break;
		default:
			switch (it_tag.case_number) {
			case A:
			case B:
			case C:
			case D:
			case E:
			case F:
				ldt_tag.number_ = LdtMorphTag.Number.s;
				break;
			case O:
			case J:
			case K:
			case M:
			case L:
			case N:
				ldt_tag.number_ = LdtMorphTag.Number.p;
				break;
			default:
				ldt_tag.number_ = LdtMorphTag.Number.Undef;
			}

			break;
		}
	}

	private void setPerson(LdtMorphTag ldt_tag, ItMorphTag it_tag) {
		switch (it_tag.gender_number_person_) {
		case Four:
		case Seven:
			ldt_tag.person_ = Person.first;
			break;
		case Five:
		case Eight:
			ldt_tag.person_ = Person.second;
			break;
		case Six:
		case Nine:
			ldt_tag.person_ = Person.third;
			break;
		default:
			ldt_tag.person_ = Person.Undef;
		}
	}

	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(
				new FileReader(
						"/mount/projekte/sfb-732/d4/users/muellets/treebanks/latin/ldt-1.5/tagwords.txt"));

		Map<String, Set<String>> ldt_word_map = new HashMap<String, Set<String>>();
		Map<String, Integer> ldt_tag_vocab = new HashMap<String, Integer>();
		Map<String, Set<String>> ldt_morph_pos_map = new HashMap<String, Set<String>>();

		while (reader.ready()) {
			String line = reader.readLine().trim();
			if (!line.isEmpty()) {
				String[] tokens = line.split("\\s+");
				int count = Integer.parseInt(tokens[0]);
				String form = tokens[1];
				String pos = tokens[2];
				String morph = tokens[3];

				Set<String> tags = ldt_morph_pos_map.get(morph);
				if (tags == null) {
					tags = new HashSet<String>();
					ldt_morph_pos_map.put(morph, tags);
				}
				tags.add(pos);

				tags = ldt_word_map.get(morph);
				if (tags == null) {
					tags = new HashSet<String>();
					ldt_word_map.put(morph, tags);
				}
				tags.add(form);

				Integer word_count = ldt_tag_vocab.get(morph);
				if (word_count == null)
					word_count = 0;
				word_count += count;
				ldt_tag_vocab.put(morph, word_count);
			}
		}
		reader.close();

		reader = new BufferedReader(
				new FileReader(
						"/mount/projekte/sfb-732/d4/users/muellets/treebanks/latin/ittb/tagwords.txt"));

		ItLdtMapper mapper = new ItLdtMapper();

		ItMorphTag.VERBOSE = false;

		Map<String, Set<String>> it_word_map = new HashMap<String, Set<String>>();
		Set<String> set = new HashSet<String>();

		while (reader.ready()) {
			String line = reader.readLine().trim();
			if (!line.isEmpty()) {

				String[] tokens = line.split("\\s+");

				String form = tokens[1];
				String it_tag_string = tokens[2] + " " + tokens[3] + " "
						+ tokens[4];

				ItMorphTag it_tag = (ItMorphTag) ItMorphTag
						.parseString(it_tag_string);

				LdtMorphTag ldt_tag = (LdtMorphTag) mapper.convert(it_tag);

				String tag = ldt_tag.toString().substring(1);

				Set<String> tags = it_word_map.get(form);
				if (tags == null) {
					tags = new HashSet<String>();
					it_word_map.put(form, tags);
				}
				tags.add(tag + "#" + tokens[4]);

				set.add(tag);

			}
		}

		// System.out.println(it_word_map);

		int total = 0;
		int unseen = 0;

		Map<String, Integer> confusion_map = new HashMap<String, Integer>();

		for (Map.Entry<String, Set<String>> entry : ldt_word_map.entrySet()) {
			String tag = entry.getKey();

			int count = ldt_tag_vocab.get(tag);

			total += count;

			if (!set.contains(tag)) {

				Set<String> tag_set = new HashSet<String>();
				for (String form : entry.getValue()) {
					Set<String> tags = it_word_map.get(form);
					if (tags != null) {
						tag_set.addAll(tags);
					}
				}

				for (String compound_tag : tag_set) {
					String[] tags = compound_tag.split("#");

					if (distance(tag, tags[0], confusion_map) < 2) {
						// System.out.format("%d %s | %s <- %s\n", count, tag,
						// tags[0], tags[1]);
					}

				}

				// if (!tag_set.isEmpty())
				// System.out.println(count + " " + tag + " " + tag_set);

				unseen += count;
			}
		}

		for (Map.Entry<String, Integer> entry : confusion_map.entrySet()) {
			System.err.format("%s %d\n", entry.getKey(), entry.getValue());
		}

		System.err.format("%d / %d = %g", unseen, total, unseen * 100. / total);

		reader.close();
	}

	private static int distance(String tag, String string,
			Map<String, Integer> map) {
		assert tag.length() == string.length();

		int dist = 0;
		for (int index = 0; index < tag.length(); index++) {
			if (tag.charAt(index) != string.charAt(index)) {

				String siganture = String.format("%d %c %c", index + 2,
						tag.charAt(index), string.charAt(index));
				Integer count = map.get(siganture);
				if (count == null) {
					count = 0;
				}
				map.put(siganture, count + 1);

				dist++;
			}
		}

		return dist;
	}

}
