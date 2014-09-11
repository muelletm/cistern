package marmot.morph.mapper.czech;

import marmot.morph.mapper.czech.PdtMorphTag.Gender;

public class PdtMorphTagReader {

	public PdtMorphTag parse_keyvalue(String pos, String feats) {
		PdtMorphTag tag = new PdtMorphTag();
		setPos(pos, tag);
		setFeatures(feats, tag);

		return tag;
	}

	private void setFeatures(String feats, PdtMorphTag tag) {
		feats = feats.toLowerCase();

		if (!feats.equals("_")) {

			for (String feat : feats.split("\\|")) {
				int index = feat.indexOf("=");

				String key = feat.substring(0, index);
				String value = feat.substring(index + 1);

				setGenricFeature(key, value, tag);
			}
		}
	}

	private void setGenricFeature(String key, String value, PdtMorphTag tag) {

		if (!key.equals("subpos")) {
			value = value.replace("x", "_");
		}

		switch (key) {

		case "subpos":
			setSubPos(value, tag);
			break;

		case "gen":
			setGender(value, tag);
			break;

		case "num":
			setNumber(value, tag);
			break;

		case "cas":
			setCase(value, tag);
			break;

		case "gra":
			setDegree(value, tag);
			break;

		case "neg":
			setNegation(value, tag);
			break;

		case "per":
			setPerson(value, tag);
			break;

		case "ten":
			setTense(value, tag);
			break;

		case "voi":
			setVoice(value, tag);
			break;

		case "var":
		case "sem":
		case "pge":
		case "pnu":
			break;

		default:
			throw new RuntimeException("Unknown category: " + key);
		}
	}

	private void setVoice(String value, PdtMorphTag tag) {
		tag.voice_ = PdtMorphTag.Voice.valueOf(value);
	}

	private void setTense(String value, PdtMorphTag tag) {
		tag.tense_ = PdtMorphTag.Tense.valueOf(value);
	}

	private void setPerson(String value, PdtMorphTag tag) {
		switch (value) {

		case "1":
			tag.person_ = PdtMorphTag.Person.fst;
			break;

		case "2":
			tag.person_ = PdtMorphTag.Person.snd;
			break;

		case "3":
			tag.person_ = PdtMorphTag.Person.thd;
			break;

		case "_":
			break;

		default:
			throw new RuntimeException("Unknown value: " + value);
		}
	}

	private void setNegation(String value, PdtMorphTag tag) {
		tag.negation_ = PdtMorphTag.Negation.valueOf(value);
	}

	private void setDegree(String value, PdtMorphTag tag) {
		switch (value) {
		case "1":
			tag.degree_ = PdtMorphTag.Degree.pos;
			break;

		case "2":
			tag.degree_ = PdtMorphTag.Degree.comp;
			break;

		case "3":
			tag.degree_ = PdtMorphTag.Degree.sup;
			break;

		default:
			throw new RuntimeException("Unknown value: " + value);
		}
	}

	private void setCase(String value, PdtMorphTag tag) {
		switch (value) {

		case "1":
			tag.case_ = PdtMorphTag.Case.nom;
			break;

		case "2":
			tag.case_ = PdtMorphTag.Case.gen;
			break;

		case "3":
			tag.case_ = PdtMorphTag.Case.dat;
			break;

		case "4":
			tag.case_ = PdtMorphTag.Case.acc;
			break;

		case "5":
			tag.case_ = PdtMorphTag.Case.voc;
			break;

		case "6":
			tag.case_ = PdtMorphTag.Case.loc;
			break;

		case "7":
			tag.case_ = PdtMorphTag.Case.ins;
			break;

		case "_":
			break;

		default:
			throw new RuntimeException("Unknown value: " + value);
		}
	}

	private void setNumber(String value, PdtMorphTag tag) {
		tag.number_ = PdtMorphTag.Number.valueOf(value);
	}

	private void setGender(String value, PdtMorphTag tag) {
		
		if (value.equals("x")) {
			tag.gender_ = Gender._;
			return;
		}
		
		tag.gender_ = PdtMorphTag.Gender.valueOf(value);
	}

	private void setSubPos(String value, PdtMorphTag tag) {
		try {
			tag.type_ = PdtMorphTag.Type.valueOf(value);
		} catch (IllegalArgumentException e) {
			switch (value) {
			case ":":
				tag.type_ = PdtMorphTag.Type.Colon;
				break;

			case "^":
				tag.type_ = PdtMorphTag.Type.Zircumflex;
				break;

			case ",":
				tag.type_ = PdtMorphTag.Type.Comma;
				break;

			case "=":
				tag.type_ = PdtMorphTag.Type.Equals;
				break;

			case "?":
				tag.type_ = PdtMorphTag.Type.Questionmark;
				break;

			case "}":
				tag.type_ = PdtMorphTag.Type.Bracket;
				break;

			case "*":
				tag.type_ = PdtMorphTag.Type.Asterisk;
				break;

			case "@":
				tag.type_ = PdtMorphTag.Type.At;
				break;

			case "1":
				tag.type_ = PdtMorphTag.Type.One;

			case "2":
				tag.type_ = PdtMorphTag.Type.Two;

			case "3":
				tag.type_ = PdtMorphTag.Type.Three;

			case "4":
				tag.type_ = PdtMorphTag.Type.Four;
				break;

			case "5":
				tag.type_ = PdtMorphTag.Type.Five;
				break;

			case "6":
				tag.type_ = PdtMorphTag.Type.Six;
				break;

			case "7":
				tag.type_ = PdtMorphTag.Type.Seven;
				break;

			case "8":
				tag.type_ = PdtMorphTag.Type.Eight;
				break;

			case "9":
				tag.type_ = PdtMorphTag.Type.Nine;
				break;

			default:
				throw e;
			}
		}
	}

	private void setPos(String pos, PdtMorphTag tag) {
		pos = pos.toLowerCase();

		tag.pos_ = PdtMorphTag.Pos.valueOf(pos);
	}

	public PdtMorphTag parse_positional(String string) {
		string = string.toLowerCase();
		
		PdtMorphTag tag = new PdtMorphTag();
		for (int i = 0; i < string.length(); i++) {

			char c = string.charAt(i);
			String current_char = Character.toString(c);

			if 	(c == '-'  || c == 'x') {
				continue;
			}
			
			switch (i) {

			case 0:
				setPos(current_char, tag);
				break;
			case 1:
				setSubPos(current_char, tag);
				break;
			case 2:
				setGender(current_char, tag);
				break;
			case 3:
				setNumber(current_char, tag);
				break;
			case 4:
				setCase(current_char, tag);
				break;
			case 5:
				// Poss Gender
				break;
			case 6:
				// Poss Number
				break;
			case 7:
				setPerson(current_char, tag);
				break;
			case 8:
				setTense(current_char, tag);
				break;
			case 9:
				setDegree(current_char, tag);
				break;
			case 10:
				setNegation(current_char, tag);
				break;
			case 11:
				setVoice(current_char, tag);
				break;
			case 12:
			case 13:
			case 14:
				// Unused
				break;
			case 15:
				// Variant
				break;

			}

		}
		return tag;
	}

}
