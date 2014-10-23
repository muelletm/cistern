// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.mapper.spanish;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import marmot.morph.mapper.Node;
import marmot.morph.mapper.SyntaxTree;
import marmot.morph.mapper.SyntaxTreeIterator;
import marmot.morph.mapper.spanish.EaglesTag.Person;

public class IulaConverter {

	public static void main(String[] args) throws IOException {

		SyntaxTreeIterator iterator = new SyntaxTreeIterator(args[0], 1, 2, 4,
				5, 6, 7, false);
		BufferedWriter writer = new BufferedWriter(new FileWriter(args[1]));
		boolean normalize = true;
		if (args.length > 2) {
			normalize = Boolean.parseBoolean(args[2]);
		}
		
		IulaConverter c = new IulaConverter();

		while (iterator.hasNext()) {

			SyntaxTree tree = iterator.next();

			for (Node node : tree.getNodes()) {
				EaglesTag tag = c.convert(node.getPos());
				node.setMorphTag(tag);
			}

			if (normalize) {
				for (Node node : tree.getNodes()) {
					((EaglesTag) node.getMorphTag()).normalize(node, true);
				}
			}

			tree.write(writer);
			writer.write('\n');
			
			// Skip some sentence
			if (iterator.hasNext())
				iterator.next();
			if (iterator.hasNext())
				iterator.next();
			if (iterator.hasNext())
				iterator.next();
		}

		writer.close();

	}

	public EaglesTag convert(String freeling_pos) {
		freeling_pos = freeling_pos.toLowerCase();

		EaglesTag tag = new EaglesTag();

		setPos(freeling_pos, tag);

		switch (tag.pos_) {
		case a:
			setAdjectiveFeatures(freeling_pos, tag);
			break;
		case d:
			setDeterminerFeatures(freeling_pos, tag);
			break;
		case n:
			setNounFeatures(freeling_pos, tag);
			break;
		case v:
			setVerbFeatures(freeling_pos, tag);
			break;
		case p:
			setPronounFeatures(freeling_pos, tag);
			break;
		case r:
		case c:
		case i:
		case z:
		case w:
			setDefaultFeatures(freeling_pos, tag);
			break;
		case s:
			setPrepositionFeatures(freeling_pos, tag);
			break;
		case f:
			setPunctFeatures(freeling_pos, tag);
			break;
		case _:
			break;

		}

		return tag;

	}

	public void setNounFeatures(String freeling_pos, EaglesTag tag) {
		for (int pos = 1; pos <= freeling_pos.length(); pos++) {
			char c = freeling_pos.charAt(pos - 1);

			if (c == '0')
				continue;

			switch (pos) {
			case 1:
				break;
			case 2:
				setType(c, tag);
				break;
			case 3:
				setGender(c, tag);
				break;
			case 4:
				setNumber(c, tag);
				break;
			case 5:
			case 6:
				// ignore the entity type
				break;
			case 7:
				setNounDegree(c, tag);
				break;
			default:
				throw new RuntimeException("Invalid position : " + pos + " "
						+ freeling_pos);
			}
		}
	}

	public void setNounDegree(char c, EaglesTag tag) {

		// IULA bug
		if (c == 'x')
			return;

		tag.noun_degree_ = EaglesTag.NounDegree.valueOf(Character.toString(c));
	}

	public void setPunctFeatures(String freeling_pos, EaglesTag tag) {
		for (int pos = 1; pos <= freeling_pos.length(); pos++) {
			char c = freeling_pos.charAt(pos - 1);

			if (c == '0')
				continue;

			switch (pos) {
			case 1:
				break;
			case 2:
				setType(c, tag);
				break;
			case 3:
				setClosing(c, tag);
				break;
			default:
				throw new RuntimeException("Invalid position : " + pos + " "
						+ freeling_pos);
			}
		}
	}

	public void setClosing(char c, EaglesTag tag) {

		// IULA bug
		if (c == 'c')
			c = 't';

		tag.closing_ = EaglesTag.Closing.valueOf(Character.toString(c));
	}

	public void setDeterminerFeatures(String freeling_pos, EaglesTag tag) {
		for (int pos = 1; pos <= freeling_pos.length(); pos++) {
			char c = freeling_pos.charAt(pos - 1);

			if (c == '0')
				continue;

			switch (pos) {
			case 1:
				break;
			case 2:
				setType(c, tag);
				break;
			case 3:
				setPerson(c, tag);
				break;
			case 4:
				setGender(c, tag);
				break;
			case 5:
				setNumber(c, tag);
				break;
			case 6:
				setOwnerNumber(c, tag);
				break;
			default:
				throw new RuntimeException("Invalid position : " + pos + " "
						+ freeling_pos);
			}
		}
	}

	public void setPrepositionFeatures(String freeling_pos, EaglesTag tag) {
		for (int pos = 1; pos <= freeling_pos.length(); pos++) {
			char c = freeling_pos.charAt(pos - 1);

			if (c == '0')
				continue;

			switch (pos) {
			case 1:
				break;
			case 2:
				setType(c, tag);
				break;
			case 3:
				setForm(c, tag);
				break;
			case 4:
				setGender(c, tag);
				break;
			case 5:
				setNumber(c, tag);
				break;
			default:
				throw new RuntimeException("Invalid position : " + pos + " "
						+ freeling_pos);
			}
		}
	}

	public void setForm(char c, EaglesTag tag) {
		tag.form_ = EaglesTag.Form.valueOf(Character.toString(c));
	}

	public void setPronounFeatures(String freeling_pos, EaglesTag tag) {
		for (int pos = 1; pos <= freeling_pos.length(); pos++) {
			char c = freeling_pos.charAt(pos - 1);

			if (c == '0')
				continue;

			switch (pos) {
			case 1:
				break;
			case 2:
				setType(c, tag);
				break;
			case 3:
				setPerson(c, tag);
				break;
			case 4:
				setGender(c, tag);
				break;
			case 5:

				// IULA bug
				if (c == 'c')
					continue;

				setNumber(c, tag);
				break;
			case 6:
				setCase(c, tag);
				break;
			case 7:

				// IULA bug
				if (c == 'c') {
					continue;
				}

				setOwnerNumber(c, tag);
				break;
			case 8:
				setPoliteness(c, tag);
				break;
			default:
				throw new RuntimeException("Invalid position : " + pos + " "
						+ freeling_pos);
			}
		}
	}

	public void setPoliteness(char c, EaglesTag tag) {
		tag.politeness_ = EaglesTag.Politeness.valueOf(Character.toString(c));
	}

	public void setOwnerNumber(char c, EaglesTag tag) {
		tag.owner_ = EaglesTag.OwnerNumber.valueOf(Character.toString(c));
	}

	public void setCase(char c, EaglesTag tag) {
		tag.case_ = EaglesTag.Case.valueOf(Character.toString(c));
	}

	public void setVerbFeatures(String freeling_pos, EaglesTag tag) {
		for (int pos = 1; pos <= freeling_pos.length(); pos++) {
			char c = freeling_pos.charAt(pos - 1);

			if (c == '0')
				continue;

			switch (pos) {
			case 1:
				break;
			case 2:
				setType(c, tag);
				break;
			case 3:
				setMood(c, tag);
				break;
			case 4:
				setTense(c, tag);
				break;
			case 5:
				setPerson(c, tag);
				break;
			case 6:
				setNumber(c, tag);
				break;
			case 7:
				setGender(c, tag);
				break;
			default:
				throw new RuntimeException("Invalid position : " + pos + " "
						+ freeling_pos);
			}
		}

	}

	public void setPerson(char c, EaglesTag tag) {
		switch (c) {
		case '1':
			tag.person_ = Person.first;
			break;
		case '2':
			tag.person_ = Person.second;
			break;
		case '3':
			tag.person_ = Person.third;
			break;
		}
	}

	public void setTense(char c, EaglesTag tag) {

		// Error in IULA
		if (c == 'b') {
			return;
		}

		tag.tense_ = EaglesTag.Tense.valueOf(Character.toString(c));
	}

	public void setMood(char c, EaglesTag tag) {
		tag.mood_ = EaglesTag.Mood.valueOf(Character.toString(c));
	}

	public void setDefaultFeatures(String freeling_pos, EaglesTag tag) {
		for (int pos = 1; pos <= freeling_pos.length(); pos++) {
			char c = freeling_pos.charAt(pos - 1);

			if (c == '0')
				continue;

			switch (pos) {
			case 1:
				break;
			case 2:
				setType(c, tag);
				break;
			default:
				throw new RuntimeException("Invalid position : " + pos + " "
						+ freeling_pos);
			}
		}
	}

	public void setAdjectiveFeatures(String freeling_pos, EaglesTag tag) {
		for (int pos = 1; pos <= freeling_pos.length(); pos++) {
			char c = freeling_pos.charAt(pos - 1);

			if (c == '0')
				continue;

			switch (pos) {
			case 1:
				break;
			case 2:
				setType(c, tag);
				break;
			case 3:
				setDegree(c, tag);
				break;
			case 4:
				setGender(c, tag);
				break;
			case 5:
				setNumber(c, tag);
				break;
			case 6:
				setFunction(c, tag);
				break;
			default:
				throw new RuntimeException("Invalid position : " + pos + " "
						+ freeling_pos);
			}
		}

	}

	public void setFunction(char c, EaglesTag tag) {
		tag.function_ = EaglesTag.Function.valueOf(Character.toString(c));
	}

	public void setNumber(char c, EaglesTag tag) {
		tag.number_ = EaglesTag.Number.valueOf(Character.toString(c));
	}

	public void setGender(char c, EaglesTag tag) {
		tag.gender_ = EaglesTag.Gender.valueOf(Character.toString(c));
	}

	public void setDegree(char c, EaglesTag tag) {

		// Following the reference diminutive should be c but in IULA it is d
		if (c == 'd') {
			c = 'c';
		}

		try {

			tag.degree_ = EaglesTag.Degree.valueOf(Character.toString(c));

		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Invalid degree: " + c);
		}
	}

	public void setType(char c, EaglesTag tag) {
		tag.type_ = EaglesTag.Type.valueOf(Character.toString(c));
	}

	public void setPos(String freeling_pos, EaglesTag tag) {
		char c = freeling_pos.charAt(0);
		tag.pos_ = EaglesTag.Pos.valueOf(Character.toString(c));
	}

}
