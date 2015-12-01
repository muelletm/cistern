// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.mapper.spanish;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import marmot.morph.mapper.Node;
import marmot.morph.mapper.SyntaxTree;
import marmot.morph.mapper.SyntaxTreeIterator;
import marmot.util.FileUtils;

public class ConllConverter {

	public static void main(String[] args) throws IOException {

		for (String filename : args) {

			SyntaxTreeIterator iterator = new SyntaxTreeIterator(filename, 1, 2,
					4, 6, 8, 10, false);
			
			File file = new File(filename);
			String outfile = file.getName() + ".converted";
			file = new File(outfile);
			if (file.exists()) {
				System.err.println("Error: Outfile already exists: " + file.getAbsolutePath());
				System.exit(1);
			}
			
			Writer writer = FileUtils.openFileWriter(outfile);
			ConllConverter c = new ConllConverter();

			while (iterator.hasNext()) {

				SyntaxTree tree = iterator.next();

				for (Node node : tree.getNodes()) {
					EaglesTag tag = c.convert(node.getPos(), node.getFeats());
					node.setMorphTag(tag);
				}

				for (Node node : tree.getNodes()) {
					((EaglesTag) node.getMorphTag()).normalize(node, false);
				}

				tree.write(writer);
				writer.write('\n');
			}

			writer.close();
		}

	}

	private EaglesTag convert(String pos, String features) {
		IulaConverter converter = new IulaConverter();

		EaglesTag tag = new EaglesTag();

		converter.setPos(pos, tag);

		for (String feature : features.split("\\|")) {

			if (feature.equals("_")) {
				continue;
			}

			String[] key_value = feature.split("=");
			String key = key_value[0].toLowerCase();
			String value = key_value[1].toLowerCase();

			setKeyValueFeature(key, value, tag, converter);

		}

		return tag;
	}

	private void setKeyValueFeature(String key, String value, EaglesTag tag,
			IulaConverter c) {
		switch (key) {
		case "postype":
		case "punct":
			setType(value, tag);
			break;
		case "gen":
			assert value.length() == 1;
			c.setGender(value.charAt(0), tag);
			break;
		case "num":
			assert value.length() == 1;

			// CoNLL 2009 bug
			if (value.equals("c")) {
				value = "n";
			}

			c.setNumber(value.charAt(0), tag);
			break;
		case "person":
			assert value.length() == 1;
			c.setPerson(value.charAt(0), tag);
			break;
		case "mood":
			setMood(value, tag);
			break;
		case "tense":
			setTense(value, tag);
			break;
		case "punctenclose":
			setClosing(value, tag);
			break;
		case "case":
			setCase(value, tag);
			break;
		case "contracted":
			assert value.equals("yes");
			tag.form_ = EaglesTag.Form.c;
			break;
		case "possessornum":
			assert value.length() == 1;

			// CoNLL bug
			if (!value.equals("c")) {
				c.setOwnerNumber(value.charAt(0), tag);
			}

			break;
		case "polite":
			assert value.equals("yes");
			tag.politeness_ = EaglesTag.Politeness.p;
			break;
		case "posfunction":
			assert value.equals("participle");
			tag.function_ = EaglesTag.Function.p;
			break;
		default:
			System.err.println("Unknown key: " + key);
		}

	}

	private void setCase(String value, EaglesTag tag) {
		switch (value) {
		case "accusative":
			tag.case_ = EaglesTag.Case.a;
			break;
		case "dative":
			tag.case_ = EaglesTag.Case.d;
			break;
		case "nominative":
			tag.case_ = EaglesTag.Case.n;
			break;
		case "oblique":
			tag.case_ = EaglesTag.Case.o;
			break;
		default:
			throw new RuntimeException("Unknown case value: " + value);
		}
	}

	private void setClosing(String value, EaglesTag tag) {
		switch (value) {
		case "close":
			tag.closing_ = EaglesTag.Closing.t;
			break;
		case "open":
			tag.closing_ = EaglesTag.Closing.a;
			break;
		default:
			throw new RuntimeException("Unknown closing value: " + value);
		}
	}

	private void setTense(String value, EaglesTag tag) {
		switch (value) {
		case "conditional":
			tag.tense_ = EaglesTag.Tense.c;
			break;
		case "future":
			tag.tense_ = EaglesTag.Tense.f;
			break;
		case "imperfect":
			tag.tense_ = EaglesTag.Tense.i;
			break;
		case "past":
			tag.tense_ = EaglesTag.Tense.s;
			break;
		case "present":
			tag.tense_ = EaglesTag.Tense.p;
			break;

		default:
			throw new RuntimeException("Unknown tense value: " + value);
		}
	}

	private void setMood(String value, EaglesTag tag) {
		switch (value) {
		case "gerund":
			tag.mood_ = EaglesTag.Mood.g;
			break;
		case "imperative":
			tag.mood_ = EaglesTag.Mood.m;
			break;
		case "subjunctive":
			tag.mood_ = EaglesTag.Mood.s;
			break;
		case "infinitive":
			tag.mood_ = EaglesTag.Mood.n;
			break;
		case "pastparticiple":
			tag.mood_ = EaglesTag.Mood.p;
			break;
		case "indicative":
			tag.mood_ = EaglesTag.Mood.i;
			break;
		default:
			throw new RuntimeException("Unknown mood value: " + value);
		}
	}

	private void setType(String value, EaglesTag tag) {
		switch (value) {
		case "article":
		case "auxiliary":
		case "exclamationmark":
			tag.type_ = EaglesTag.Type.a;
			break;
		case "common":
		case "coordinating":
		case "comma":
			tag.type_ = EaglesTag.Type.c;
			break;
		case "demonstrative":
		case "colon":
			tag.type_ = EaglesTag.Type.d;
			break;
		case "exclamative":
		case "quotation":
			tag.type_ = EaglesTag.Type.e;
			break;
		case "general":
		case "hyphen":
			tag.type_ = EaglesTag.Type.g;
			break;
		case "indefinite":
		case "questionmark":
		case "numeral":
			tag.type_ = EaglesTag.Type.i;
			break;
		case "interrogative":
		case "percentage":
			tag.type_ = EaglesTag.Type.t;
			break;
		case "main":
		case "principal":
		case "currency":
			tag.type_ = EaglesTag.Type.m;
			break;
		case "negative":
			tag.type_ = EaglesTag.Type.n;
			break;
		case "ordinal":
			tag.type_ = EaglesTag.Type.o;
			break;
		case "proper":
		case "personal":
		case "preposition":
		case "bracket":
		case "period":
			tag.type_ = EaglesTag.Type.p;
			break;
		case "qualificative":
			tag.type_ = EaglesTag.Type.q;
			break;
		case "relative":
			tag.type_ = EaglesTag.Type.r;
			break;
		case "semiauxiliary":
		case "subordinating":
		case "etc":
			tag.type_ = EaglesTag.Type.s;
			break;
		case "possessive":
			if (tag.pos_ == EaglesTag.Pos.d) {
				tag.type_ = EaglesTag.Type.p;
			} else if (tag.pos_ == EaglesTag.Pos.p) {
				tag.type_ = EaglesTag.Type.x;
			} else {
				assert false;
			}
			break;
		case "semicolon":
			tag.type_ = EaglesTag.Type.x;
			break;
		case "mathsign":
			tag.type_ = EaglesTag.Type.z;
			break;
		case "slash":
			tag.type_ = EaglesTag.Type.h;
			break;
		default:
			throw new RuntimeException("Unknown type/punct value: " + value);
		}

	}

}
