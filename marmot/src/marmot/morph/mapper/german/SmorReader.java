// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.mapper.german;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import marmot.morph.mapper.Node;
import marmot.morph.mapper.SyntaxTree;
import marmot.morph.mapper.SyntaxTreeIterator;
import marmot.util.Counter;
import marmot.util.LineIterator;

public class SmorReader {

	public static void main(String[] args) throws IOException {
		SmorReader reader = new SmorReader();

		Map<String, Set<SttsTag>> dict = reader.readFile(args[0]);
		Map<String, Set<SttsTag>> pos_dict = reader.readPosFile(args[1]);

		for (int i = 1; i<args.length; i++) {
			System.err.println(args[i]);
			reader.test(dict, pos_dict, args[i]);
		}

	}

	private void test(Map<String, Set<SttsTag>> dict,
			Map<String, Set<SttsTag>> pos_dict, String string)
			throws IOException {

		Writer writer = new BufferedWriter(new FileWriter(string + ".lattice"));

		SyntaxTreeIterator iterator = new SyntaxTreeIterator(string, 1, 2, 4,
				6, 8, 10, false);

		int pos_candidates = 0;
		int pos_correct = 0;
		int correct = 0;
		int total = 0;
		int candidates = 0;
		int covered = 0;

		Counter<String> candidate_counter = new Counter<>();
		Counter<String> total_counter = new Counter<>();

		while (iterator.hasNext()) {
			SyntaxTree tree = iterator.next();

			for (Node node : tree.getNodes()) {

				String pos = node.getPos();

				// if (!pos.equals("NN")) {
				// continue;
				// }

				Set<SttsTag> pos_set = pos_dict.get(pos);

				if (check(node.getFeats(), pos_set)) {
					pos_correct += 1;
				}

				pos_candidates += pos_set.size();

				if (pos_set.size() > 1) {

					Set<SttsTag> form_set = dict.get(normalize(node.getForm(),
							pos));

					if (form_set != null) {

						covered++;

						Set<SttsTag> set = mergeSets(pos_set, form_set);

						if (!set.isEmpty()) {
							pos_set = set;
						}

					}
				}

				if (check(node.getFeats(), pos_set)) {
					correct += 1;
				}

				candidates += pos_set.size();
				total += 1;

				// candidate_counter.increment(node.getPos(), (double
				// )pos_set.size());
				// total_counter.increment(node.getPos(), (double ) 1);

				writer.write(node.getForm());

				if (pos_set.size() < 5) {
					for (SttsTag tag : pos_set) {
						writer.write(' ');
						writer.write(tag.feat_string_);
					}
				} else {
					writer.write(' ');
					writer.write('*');
				}

				writer.write('\n');

			}

			writer.write('\n');
		}

		System.err.println("pos correct: " + pos_correct * 100. / total);
		System.err
				.println("pos candidates: " + pos_candidates / (double) total);

		System.err.println("correct: " + correct * 100. / total);
		System.err.println("candidates: " + candidates / (double) total);
		System.err.println("coverage: " + covered / (double) total);

		for (Map.Entry<String, Double> entry : candidate_counter.entrySet()) {
			System.err.println(entry.getKey() + ":" + entry.getValue()
					/ total_counter.count(entry.getKey()));
		}

		writer.close();

	}

	private String normalize(String form, String pos) {
		if (pos.equals("NE")) {
			return form;
		}

		StringBuilder sb = new StringBuilder(form.toLowerCase());

		if (pos.equals("NN")) {
			sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		}

		return sb.toString();
	}

	boolean check(String tag_string, Set<SttsTag> set) {
		for (SttsTag tag : set) {
			if (tag.feat_string_.equals(tag_string)) {
				return true;
			}
		}

		return false;
	}

	private static Set<SttsTag> mergeSets(Set<SttsTag> pos_set,
			Set<SttsTag> form_set) {
		Set<SttsTag> set = new HashSet<>();

		for (SttsTag tag : pos_set) {

			for (SttsTag form_tag : form_set) {

				if (match(tag, form_tag)) {
					set.add(tag);
					break;
				}

			}

		}

		return set;
	}

	static boolean match(SttsTag tag, SttsTag form_tag) {
		Matcher m = new Matcher();

		m.add(tag.case_.toString(), form_tag.case_.toString());
		m.add(tag.gender_.toString(), form_tag.gender_.toString());
		m.add(tag.number_.toString(), form_tag.number_.toString());
		m.add(tag.person_.toString(), form_tag.person_.toString());
		m.add(tag.tense_.toString(), form_tag.tense_.toString());
		m.add(tag.mood_.toString(), form_tag.mood_.toString());
		m.add(tag.degree_.toString(), form_tag.degree_.toString());

		return m.matches();
	}

	private Map<String, Set<SttsTag>> readPosFile(String string) {
		Map<String, Set<SttsTag>> map = new HashMap<>();

		SyntaxTreeIterator iterator = new SyntaxTreeIterator(string, 1, 2, 4,
				6, 8, 10, false);

		while (iterator.hasNext()) {
			SyntaxTree tree = iterator.next();

			for (Node node : tree.getNodes()) {

				Set<SttsTag> set = map.get(node.getPos());

				if (set == null) {
					set = new HashSet<>();
					map.put(node.getPos(), set);
				}

				set.add(parseSeekerTag(node.getFeats()));

			}

		}

		return map;
	}

	private SttsTag parseSeekerTag(String feats) {
		SttsTag tag = new SttsTag();

		// setPos(tag, pos);

		String[] features = feats.split("\\|");

		for (String feature : features) {
			if (!feature.isEmpty()) {
				setSeekerFeature(tag, feature);
			}
		}

		tag.setFeatString(feats);

		return tag;
	}

	private void setSeekerFeature(SttsTag tag, String feature) {

		if (feature.equals("_"))
			return;

		String[] key_value = feature.toLowerCase().split("=");

		String key = key_value[0];
		String value = key_value[1];

		switch (key) {
		case "case":
			if (value.equals("*")) {
				tag.case_ = SttsTag.Case.amb;
			} else {
				tag.case_ = SttsTag.Case.valueOf(value);
			}
			break;

		case "number":
			if (value.equals("*")) {
				tag.number_ = SttsTag.Number.amb;
			} else {
				tag.number_ = SttsTag.Number.valueOf(value);
			}
			break;

		case "gender":

			if (value.equals("*")) {
				tag.gender_ = SttsTag.Gender.amb;
			} else {
				tag.gender_ = SttsTag.Gender.valueOf(value);
			}

			break;

		case "person":
			switch (value) {
			case "1":
				tag.person_ = SttsTag.Person.fst;
				break;
			case "2":
				tag.person_ = SttsTag.Person.snd;
				break;
			case "3":
				tag.person_ = SttsTag.Person.thd;
				break;

			}
			break;

		case "tense":
			tag.tense_ = SttsTag.Tense.valueOf(value);
			break;

		case "mood":
			tag.mood_ = SttsTag.Mood.valueOf(value);
			break;

		case "degree":
			if (value.equals("*")) {
				tag.degree_ = SttsTag.Degree.amb;
			} else {
				tag.degree_ = SttsTag.Degree.valueOf(value);
			}
			break;

		default:
			throw new RuntimeException("Unknown key: " + key);
		}

	}

	public Map<String, Set<SttsTag>> readFile(String filename) {
		Map<String, Set<SttsTag>> dict = new HashMap<>();

		LineIterator iterator = new LineIterator(filename);

		while (iterator.hasNext()) {
			List<String> line = iterator.next();

			if (!line.isEmpty()) {

				String form = line.get(0);

				Set<SttsTag> readings = dict.get(form);
				if (readings == null) {
					readings = new HashSet<SttsTag>();
					dict.put(form, readings);
				}

				SttsTag tag = parseMorphTagString(line.get(2), line.get(3));
				readings.add(tag);

			}

		}

		return dict;
	}

	public SttsTag parseMorphTagString(String pos, String morph_tag_string) {
		SttsTag tag = new SttsTag();

		// setPos(tag, pos);

		String[] features = morph_tag_string.split("[<>]");

		for (String feature : features) {
			if (!feature.isEmpty()) {
				setFeature(tag, feature);
			}
		}

		return tag;
	}

	// private void setPos(SttsTag tag, String pos) {
	// assert pos.startsWith("<+");
	// assert pos.endsWith(">");
	//
	// tag.pos_ = SttsTag.Pos.valueOf(pos.substring(2, pos.length() - 1));
	// }

	public static void setFeature(SttsTag tag, String feature) {

		feature = feature.toLowerCase();

		switch (feature) {

		// SubPoS

		// case "adv":
		// tag.type_ = SttsTag.Type.ADJA;
		// break;
		//
		// case "pred":
		// if (tag.pos_ == SttsTag.Pos.ADJ) {
		// tag.type_ = SttsTag.Type.ADJA;
		// }
		// break;
		//
		// case "comma":
		// tag.type_ = SttsTag.Type.Comma;
		// break;
		//
		// case "norm":
		// tag.type_ = SttsTag.Type.Period;
		// break;
		//
		// case "left":
		// case "right":
		// tag.type_ = SttsTag.Type.Bracket;
		// break;
		//
		// case "coord":
		// tag.type_ = SttsTag.Type.KON;
		// break;
		//
		// case "compar":
		// tag.type_ = SttsTag.Type.KOKOM;
		// break;
		//
		// case "sub":
		// tag.type_ = SttsTag.Type.KOUS; // KOUI
		// break;
		//
		// case "ppres":
		// tag.type_ = SttsTag.Type.ADJD;
		// tag.pos_ = SttsTag.Pos.ADJ;
		// break;
		//
		// case "imp":
		// tag.type_ = SttsTag.Type.VVIMP;
		// break;
		//
		// case "inf":
		// tag.type_ = Type.VVINF;
		// break;
		//
		// case "zu":
		// tag.type_ = Type.VVIZU;
		// break;
		//
		// case "ppast":
		// tag.type_ = Type.VVPP;
		// break;
		//
		// case "ans":
		// tag.type_ = Type.PTKANT;
		// break;

		// Case

		case "acc":
			tag.case_ = SttsTag.Case.acc;
			break;

		case "dat":
			tag.case_ = SttsTag.Case.dat;
			break;

		case "gen":
			tag.case_ = SttsTag.Case.gen;
			break;

		case "nom":
			tag.case_ = SttsTag.Case.nom;
			break;

		// Gender

		case "fem":
			tag.gender_ = SttsTag.Gender.fem;
			break;

		case "masc":
			tag.gender_ = SttsTag.Gender.masc;
			break;

		case "neut":
			tag.gender_ = SttsTag.Gender.neut;
			break;

		// Number

		case "sg":
			tag.number_ = SttsTag.Number.sg;
			break;

		case "pl":
			tag.number_ = SttsTag.Number.pl;
			break;

		// Degree

		case "pos":
			tag.degree_ = SttsTag.Degree.pos;
			break;

		case "comp":
			tag.degree_ = SttsTag.Degree.comp;
			break;

		case "sup":
			tag.degree_ = SttsTag.Degree.sup;
			break;

		// Person

		case "1":
			tag.person_ = SttsTag.Person.fst;
			break;

		case "2":
			tag.person_ = SttsTag.Person.snd;
			break;

		case "3":
			tag.person_ = SttsTag.Person.thd;
			break;

		// Tense

		case "pres":
			tag.tense_ = SttsTag.Tense.pres;
			break;

		case "past":
			tag.tense_ = SttsTag.Tense.past;
			break;

		// Mood

		case "ind":
			tag.mood_ = SttsTag.Mood.ind;
			break;

		case "subj":
			tag.mood_ = SttsTag.Mood.subj;
			break;

		// case "attr":
		//
		// switch (tag.pos_) {
		//
		// case DEM:
		// tag.type_ = Type.PDAT;
		// break;
		// case INDEF:
		// tag.type_ = Type.PIAT; // PIDAT
		// break;
		// case POSS:
		// tag.type_ = Type.PPOSAT;
		// break;
		// case REL:
		// tag.type_ = Type.PRELAT;
		// break;
		// case WPRO:
		// tag.type_ = Type.PWAT;
		// break;
		//
		// default:
		// System.err.println("attr" + tag.pos_);
		// break;
		// }
		//
		//
		// break;

		// case "subst":
		//
		// switch (tag.pos_) {
		//
		// case DEM:
		// tag.type_ = Type.PDS;
		// break;
		// case INDEF:
		// tag.type_ = Type.PIS;
		// break;
		// case POSS:
		// tag.type_ = Type.PPOSS;
		// break;
		// case REL:
		// tag.type_ = Type.PRELS;
		// break;
		// case WPRO:
		// tag.type_ = Type.PWS;
		// break;
		//
		// default:
		// System.err.println("subst " + tag.pos_);
		// break;
		// }
		//
		//
		// break;

		case "_":
		case "pro":
		case "nogend":
		case "wk": // weak
		case "st": // strong
		case "old": // old dative
		case "invar":
		case "simp":
		case "adj":
		case "def":
		case "pers":
		case "refl":
		case "indef":
		case "rec":
		case "neg":
		case "comma":
		case "norm":
		case "left":
		case "right":
		case "adv":
		case "pred":
		case "inf":
		case "ppast":
		case "coord":
		case "ppres":
		case "imp":
		case "zu":
		case "compar":
		case "sub":
		case "ans":
		case "attr":
		case "subst":

			break;

		default:

			throw new RuntimeException("Unknown feature: " + feature);

		}

	}

}
