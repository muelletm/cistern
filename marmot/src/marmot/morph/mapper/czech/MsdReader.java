// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.mapper.czech;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import marmot.morph.mapper.czech.MsdTag.Pos;
import marmot.util.Counter;
import marmot.util.LineIterator;

// Based on http://nl.ijs.si/ME/V4/msd/html/msd-cs.html

public class MsdReader {

	public static Map<String, Counter<String>> getDict(String filename) {
		Map<String, Counter<String>> map = new HashMap<String, Counter<String>>();

		LineIterator iterator = new LineIterator(filename);

		MsdReader reader = new MsdReader();

		while (iterator.hasNext()) {
			List<String> line = iterator.next();

			if (!line.isEmpty()) {
				String msd_tag = line.get(2);
				MsdTag tag = reader.parse(msd_tag);

				Counter<String> counter = map.get(line.get(1));

				if (counter == null) {
					counter = new Counter<String>();
					map.put(line.get(1), counter);
				}

				counter.increment(tag.toHumanString(), 1.);
			}
		}

		return map;
	}

	static public void main(String[] args) throws IOException {

		LineIterator iterator = new LineIterator(args[0]);
		Writer writer = new BufferedWriter(new FileWriter(args[1]));

		int num_words = 0;
		int token_index = 0;

		MsdReader reader = new MsdReader();

		while (iterator.hasNext()) {
			List<String> line = iterator.next();

			if (!line.isEmpty()) {

				String msd_tag = line.get(2);

				MsdTag tag = reader.parse(msd_tag);

				writer.write(Integer.toString(token_index));
				writer.write('\t');
				writer.write(line.get(0));
				writer.write('\t');
				writer.write(line.get(1));
				writer.write('\t');
				writer.write(tag.toPosString());
				writer.write('\t');
				writer.write(tag.toPosString());
				writer.write('\t');
				writer.write(tag.toHumanMorphString());
				writer.write('\t');
				writer.write('0');
				writer.write('\t');
				writer.write('_');

				token_index++;
				num_words++;

			} else {

				token_index = 0;

				if (num_words > 50000) {
					writer.write('\n');
					break;
				}
			}

			writer.write('\n');

		}

		writer.close();
	}

	public MsdTag parse(String msd_tag) {
		msd_tag = msd_tag.replace('-', '_').toLowerCase();
		MsdTag tag = new MsdTag();

		if (!msd_tag.startsWith("#")) {
			assert msd_tag.equals("c");
			tag.pos_ = MsdTag.Pos.z;
			return tag;
		}

		msd_tag = msd_tag.substring(1);

		char pos_char = msd_tag.charAt(0);

		switch (pos_char) {

		case 'v':
			parseVerb(tag, msd_tag);
			break;

		case 'a':
			parseAdj(tag, msd_tag);
			break;

		case 'c':
			parseConjunction(tag, msd_tag);
			break;

		case 'n':
			parseNoun(tag, msd_tag);
			break;

		case 'm':
			parseNumeral(tag, msd_tag);
			break;

		case 's':
			parseAdposition(tag, msd_tag);
			break;

		case 'r':
			parseAdverb(tag, msd_tag);
			break;

		case 'q':
			tag.pos_ = MsdTag.Pos.q;
			parseGeneric(tag, msd_tag);
			break;

		case 'p':
			parsePronoun(tag, msd_tag);
			break;

		case 'x':
			tag.pos_ = MsdTag.Pos.x;
			parseGeneric(tag, msd_tag);
			break;

		case 'y':
			tag.pos_ = MsdTag.Pos.y;
			parseGeneric(tag, msd_tag);
			break;

		case 'i':
			tag.pos_ = MsdTag.Pos.i;
			parseGeneric(tag, msd_tag);
			break;

		default:

			throw new RuntimeException("Unknown POS: " + pos_char);

		}

		return tag;
	}

	private void parsePronoun(MsdTag tag, String msd_tag) {
		tag.pos_ = MsdTag.Pos.p;

		for (int index = 1; index < msd_tag.length(); index++) {
			char c = msd_tag.charAt(index);

			if (c == '_') {
				continue;
			}

			switch (index) {

			case 1:
				setType(tag, c);
				break;

			case 2:
				setPerson(tag, c);
				break;

			case 3:
				setGender(tag, c);
				break;

			case 4:
				setNumber(tag, c);
				break;

			case 5:
				setCase(tag, c);
				break;

			case 6:
				// ignore owner number
				break;

			case 7:
				// ignore owner gender
				break;

			case 8:
				// ignore clitic
				break;

			case 9:
				// ignore referent tpye
				break;

			case 10:
				// ignore syntactic type
				break;

			case 12:
				// setAnimate(tag, c);
				break;

			case 13:
				// ignore clitic
				break;

			default:
				throw new RuntimeException("Unexpected Index: " + index);
			}
		}
	}

	private void parseGeneric(MsdTag tag, String msd_tag) {
		for (int index = 1; index < msd_tag.length(); index++) {
			char c = msd_tag.charAt(index);

			if (c == '_') {
				continue;
			}

			switch (index) {

			case 1:
				setType(tag, c);
				break;

			default:
				throw new RuntimeException("Unexpected Index: " + index);
			}
		}
	}

	private void parseAdverb(MsdTag tag, String msd_tag) {
		tag.pos_ = MsdTag.Pos.r;

		for (int index = 1; index < msd_tag.length(); index++) {
			char c = msd_tag.charAt(index);

			if (c == '_') {
				continue;
			}

			switch (index) {

			case 1:
				setType(tag, c);
				break;

			case 2:
				setDegree(tag, c);
				break;

			default:
				throw new RuntimeException("Unexpected Index: " + index);
			}
		}
	}

	private void parseAdposition(MsdTag tag, String msd_tag) {
		tag.pos_ = MsdTag.Pos.s;

		for (int index = 1; index < msd_tag.length(); index++) {
			char c = msd_tag.charAt(index);

			if (c == '_') {
				continue;
			}

			switch (index) {

			case 1:
				setType(tag, c);
				break;

			case 2:
				// Ignore Formation;
				break;

			case 3:
				setCase(tag, c);
				break;

			default:
				throw new RuntimeException("Unexpected Index: " + index);
			}
		}
	}

	private void parseNumeral(MsdTag tag, String msd_tag) {
		tag.pos_ = MsdTag.Pos.m;

		for (int index = 1; index < msd_tag.length(); index++) {
			char c = msd_tag.charAt(index);

			if (c == '_') {
				continue;
			}

			switch (index) {

			case 1:
				setType(tag, c);
				break;

			case 2:
				setGender(tag, c);
				break;

			case 3:
				setNumber(tag, c);
				break;

			case 4:
				setCase(tag, c);
				break;

			case 5:
				// Ignore Form
				break;

			case 8:
				// Ignore Class
				break;

			case 9:
				// setAnimate(tag, c);
				break;

			default:
				throw new RuntimeException("Unexpected Index: " + index);
			}
		}
	}

	private void parseNoun(MsdTag tag, String msd_tag) {
		tag.pos_ = MsdTag.Pos.n;

		for (int index = 1; index < msd_tag.length(); index++) {
			char c = msd_tag.charAt(index);

			if (c == '_') {
				continue;
			}

			switch (index) {

			case 1:
				setType(tag, c);
				break;

			case 2:
				setGender(tag, c);
				break;

			case 3:
				setNumber(tag, c);
				break;

			case 4:
				setCase(tag, c);
				break;

			case 7:
				// setAnimate(tag, c);
				break;

			default:
				throw new RuntimeException("Unexpected Index: " + index);
			}
		}
	}

	private void parseConjunction(MsdTag tag, String msd_tag) {
		tag.pos_ = MsdTag.Pos.c;

		for (int index = 1; index < msd_tag.length(); index++) {

			char c = msd_tag.charAt(index);

			if (c == '_') {
				continue;
			}

			switch (index) {

			case 1:
				setType(tag, c);
				break;

			case 6:
				setNumber(tag, c);
				break;

			case 7:
				setPerson(tag, c);
				break;

			default:
				throw new RuntimeException("Unexpected Index: " + index);
			}
		}
	}

	private void parseAdj(MsdTag tag, String msd_tag) {
		tag.pos_ = MsdTag.Pos.a;

		for (int index = 1; index < msd_tag.length(); index++) {

			char c = msd_tag.charAt(index);

			if (c == '_') {
				continue;
			}

			switch (index) {

			case 1:
				setType(tag, c);
				break;

			case 2:
				setDegree(tag, c);
				break;

			case 3:
				setGender(tag, c);
				break;

			case 4:
				setNumber(tag, c);
				break;

			case 5:
				setCase(tag, c);
				break;

			case 8:
				// setAnimate(tag, c);
				break;

			case 9:
				// setFormation(tag, c);
				break;

			default:
				throw new RuntimeException("Unexpected Index: " + index);
			}
		}
	}

	// private void setFormation(MsdTag tag, char c) {
	// tag.formation_ = MsdTag.Formation.valueOf(Character.toString(c));
	// }

	private void setCase(MsdTag tag, char c) {
		tag.case_ = MsdTag.Case.valueOf(Character.toString(c));
	}

	private void setDegree(MsdTag tag, char c) {
		
		if (tag.pos_ == Pos.r) {
			return;
		}
		
		
		tag.degree_ = MsdTag.Degree.valueOf(Character.toString(c));
	}

	private void parseVerb(MsdTag tag, String msd_tag) {
		tag.pos_ = MsdTag.Pos.v;

		for (int index = 1; index < msd_tag.length(); index++) {

			char c = msd_tag.charAt(index);
			if (c == '_') {
				continue;
			}

			switch (index) {

			case 1:
				setType(tag, c);
				break;

			case 2:
				setMood(tag, c);
				break;

			case 3:
				setTense(tag, c);
				break;

			case 4:
				setPerson(tag, c);
				break;

			case 5:
				setNumber(tag, c);
				break;

			case 6:
				setGender(tag, c);
				break;

			case 7:
				setVoice(tag, c);
				break;

			case 8:
				// setNegative(tag, c);
				break;

			case 12:
				// setAnimate(tag, c);
				break;

			case 13:
				// Ignore clitics
				break;

			default:
				throw new RuntimeException("Unexpected Index: " + index);
			}
		}
	}

	private void setType(MsdTag tag, char c) {
		tag.type_ = MsdTag.Type.valueOf(Character.toString(c));
	}

	// private void setAnimate(MsdTag tag, char c) {
	// tag.animate_ = MsdTag.Animate.valueOf(Character.toString(c));
	// }

	// private void setNegative(MsdTag tag, char c) {
	// tag.negative_ = MsdTag.Negative.valueOf(Character.toString(c));
	// }

	private void setVoice(MsdTag tag, char c) {
		tag.voice_ = MsdTag.Voice.valueOf(Character.toString(c));
	}

	private void setGender(MsdTag tag, char c) {
		tag.gender_ = MsdTag.Gender.valueOf(Character.toString(c));
	}

	private void setMood(MsdTag tag, char c) {
		tag.mood_ = MsdTag.Mood.valueOf(Character.toString(c));
	}

	private void setNumber(MsdTag tag, char c) {
		tag.number_ = MsdTag.Number.valueOf(Character.toString(c));
	}

	private void setPerson(MsdTag tag, char c) {
		switch (c) {
		case '1':
			tag.person_ = MsdTag.Person.fst;
			break;
		case '2':
			tag.person_ = MsdTag.Person.snd;
			break;
		case '3':
			tag.person_ = MsdTag.Person.thd;
			break;
		case '_':
			tag.person_ = MsdTag.Person._;
			break;
		default:
			throw new RuntimeException("Unknown value: " + c);
		}
	}

	private void setTense(MsdTag tag, char c) {
		tag.tense_ = MsdTag.Tense.valueOf(Character.toString(c));
	}

}
