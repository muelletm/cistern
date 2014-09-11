// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.mapper.latin;

import marmot.morph.mapper.MorphTag;
import marmot.morph.mapper.Names;

public class LdtMorphTag implements MorphTag {

	static public final int PosIndex = 0;
	public enum Pos {
		Undef, // -
		n, // noun
		v, // verb
		t, // participle
		a, // adjective
		d, // adverb
		c, // conjunction
		r, // preposition
		p, // pronoun
		m, // numeral
		i, // interjection
		e, // exclamation
		u, // punctuation
		x, // nominal
	}

	static public final int PersonIndex = 1;
	public enum Person {
		Undef, // -
		first, // first person
		second, // second person
		third, // third person
	}

	static public final int NumberIndex = 2;
	public enum Number {
		Undef, // -
		s, // singular
		p, // plural
	}

	static public final int TenseIndex = 3;
	public enum Tense {
		Undef, // -
		p, // present
		i, // imperfect
		r, // perfect
		l, // plus perfect
		t, // future perfect
		f, // future
	}
	
	static public final int MoodIndex = 4;
	public enum Mood {
		Undef, // -
		i, // indicative
		s, // subjunctive
		n, // infinitive
		m, // imperative
		p, // participle
		d, // gerund
		g, // gerundive
		u, // supine
	}
	static public final int VoiceIndex = 5;
	public enum Voice {
		Undef, // -
		a, // active
		p, // passive
	}

	static public final int GenderIndex = 6;
	public enum Gender {
		Undef, // -
		m, // masculine
		f, // feminine
		n, // neuter
	}

	static public final int CaseIndex = 7;
	public enum Case {
		Undef, // -
		n, // nominative
		g, // genitive
		d, // dative
		a, // accusative
		b, // ablative
		v, // vocative
		l, // locative
	}

	static public final int DegreeIndex = 8;
	public enum Degree {
		Undef, // -
		c, // comparative
		s, // superlative
	}

	public Person person_;
	public Number number_;
	public Tense tense_;
	public Object voice_;
	public Mood mood_;
	public Gender gender_;
	public Pos pos_;
	public Case case_;
	public Degree degree_;

	public LdtMorphTag() {
		reset();
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder(9);

		sb.append(convert(pos_.toString()));
		sb.append(convert(person_.toString()));
		sb.append(convert(number_.toString()));
		sb.append(convert(tense_.toString()));
		sb.append(convert(mood_.toString()));
		sb.append(convert(voice_.toString()));
		sb.append(convert(gender_.toString()));
		sb.append(convert(case_.toString()));
		sb.append(convert(degree_.toString()));

		assert sb.length() == 9;
		return sb.toString();
	}

	public String toHumanString() {
		StringBuilder sb = new StringBuilder();
		
		if (pos_ != Pos.Undef) {
			sb.append("pos=");
			sb.append(convertHuman(PosIndex, pos_.toString()));
		}
		
		String morph_string = toHumanMorphString();
		
		if (morph_string != "_") {
			sb.append(Names.Sep);
			sb.append(morph_string);
		}
		
		return morph_string;

	}

	public static LdtMorphTag parseString(String string) {
		LdtMorphTag tag = new LdtMorphTag();

		for (int index = 0; index < string.length(); index++) {
			char c = string.charAt(index);

			
			String c_string;
			
			
			switch (c) {
				case '-':
					c_string = "Undef";
					break;
				case '1':
					c_string = "first";
					break;
				case '2':
					c_string = "second";
					break;
				case '3':
					c_string = "third";
					break;
				default:
					c_string = Character.toString(c);
					break;
			}
			
			switch (index) {
			case PosIndex:
				tag.pos_ = Pos.valueOf(c_string);
				break;
			case PersonIndex:
				tag.person_ = Person.valueOf(c_string);
				break;
			case NumberIndex:
				tag.number_ = Number.valueOf(c_string);
				break;
			case TenseIndex:
				tag.tense_ = Tense.valueOf(c_string);
				break;
			case MoodIndex:
				tag.mood_ = Mood.valueOf(c_string);
				break;
			case GenderIndex:
				tag.gender_ = Gender.valueOf(c_string);
				break;
			case VoiceIndex:
				tag.voice_ = Voice.valueOf(c_string);
				break;
			case CaseIndex:
				tag.case_ = Case.valueOf(c_string);
				break;
			case DegreeIndex:
				tag.degree_ = Degree.valueOf(c_string);
				break;
			default:
				throw new RuntimeException("Unknown value: " + c);
			}

		}

		return tag;
	}

	private String convertHuman(int position, String string) {
		
		switch (string) {
		case "first":
			return "1";
		case "second":
			return "2";
		case "third":
			return "3";
		}
		
		return string;

//		switch (string) {
//		case "-":
//			return "-";
//		case "n":
//
//			if (position == MoodIndex) {
//				return "inf";
//			}
//
//			if (position == GenderIndex) {
//				return "neut";
//			}
//
//			if (position == CaseIndex)
//				return "nom";
//
//			return "noun";
//		case "v":
//
//			if (position == CaseIndex)
//				return "voc";
//
//			return "verb";
//		case "t":
//
//			if (position == TenseIndex)
//				return "futureII";
//
//			return "part";
//		case "a":
//
//			if (position == VoiceIndex)
//				return "active";
//
//			if (position == CaseIndex)
//				return "acc";
//
//			return "adj";
//		case "d":
//
//			if (position == MoodIndex)
//				return "gerund";
//
//			if (position == CaseIndex)
//				return "dat";
//
//			return "adv";
//		case "c":
//
//			if (position == DegreeIndex) {
//				return "comp";
//			}
//
//			return "conj";
//		case "r":
//
//			if (position == TenseIndex) {
//				return "perf";
//			}
//
//			return "prep";
//		case "p":
//
//			if (position == NumberIndex)
//				return "pl";
//
//			if (position == TenseIndex) {
//				return "pres";
//			}
//
//			if (position == MoodIndex) {
//				return "part";
//			}
//
//			if (position == VoiceIndex) {
//				return "passive";
//			}
//
//			return "pron";
//		case "m":
//
//			if (position == TenseIndex)
//				return "imp";
//
//			if (position == GenderIndex)
//				return "masc";
//
//			return "num";
//		case "i":
//			
//			if (position == TenseIndex) {
//				return "imp";
//			}
//
//			if (position == MoodIndex) {
//				return "ind";
//			}
//
//			return "int";
//		case "e":
//			return "exc";
//		case "u":
//
//			if (position == TenseIndex)
//				return "supine";
//
//			return "punc";
//		case "first":
//			return "1";
//		case "second":
//			return "2";
//		case "third":
//			return "3";
//		case "s":
//
//			if (position == MoodIndex) {
//				return "subj";
//			}
//
//			if (position == DegreeIndex) {
//				return "super";
//			}
//
//			return "sg";
//		case "l":
//
//			if (position == CaseIndex) {
//				return "loc";
//			}
//
//			return "plus";
//		case "f":
//
//			if (position == GenderIndex)
//				return "fem";
//
//			return "future";
//		case "g":
//
//			if (position == CaseIndex)
//				return "gen";
//
//			return "gerundive";
//
//		case "b":
//			return "abl";
//			
//		case "x":
//			return "nominal";
//			
//		case "y":
//			return "conj/prep";
//
//		}
//
//		return "-";
	}

	private char convert(String string) {
		if (string.length() == 1) {
			return string.charAt(0);
		}

		if (string.equalsIgnoreCase("first")) {
			return '1';
		}

		if (string.equalsIgnoreCase("second")) {
			return '2';
		}

		if (string.equalsIgnoreCase("third")) {
			return '3';
		}

		return '-';
	}
	
	public LdtMorphTag merge(LdtMorphTag other) {
		StringBuilder sb = new StringBuilder(9);
		
		String this_string = toString();
		String other_string = other.toString();
		
		for (int index = 0; index < this_string.length(); index ++) {
			char this_c = this_string.charAt(index);
			char other_c = other_string.charAt(index);
			
			if (this_c == '-') {
				sb.append(other_c);
			} else if (other_c == '-') {
				sb.append(this_c);
			} else if (this_c == other_c) {
				sb.append(this_c);
			} else {
				return null;
			}
		}		
		
		return parseString(sb.toString());
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LdtMorphTag other = (LdtMorphTag) obj;
		return toString().equals(other.toString());
	}

	public void reset() {
		person_ = Person.Undef;
		number_ = Number.Undef;
		tense_ = Tense.Undef;
		voice_ = Voice.Undef;
		mood_ = Mood.Undef;
		gender_ = Gender.Undef;
		pos_ = Pos.Undef;
		case_ = Case.Undef;
		degree_ = Degree.Undef;
	}

	@Override
	public String toHumanMorphString() {
		StringBuilder sb = new StringBuilder(9);

		if (person_ != Person.Undef) {
			
			if (sb.length() > 0) {
				sb.append('|');
			}
			
			sb.append(Names.Person);
			sb.append('=');
			sb.append(convertHuman(PersonIndex, person_.toString()));
		}

		if (number_ != Number.Undef) {
			
			if (sb.length() > 0) {
				sb.append('|');
			}
					
			sb.append(Names.Number);
			sb.append('=');
			sb.append(convertHuman(NumberIndex, number_.toString()));
		}

		if (tense_ != Tense.Undef) {
			
			if (sb.length() > 0) {
				sb.append('|');
			}
			
			sb.append(Names.Tense);
			sb.append('=');
			sb.append(convertHuman(TenseIndex, tense_.toString()));
		}

		if (mood_ != Mood.Undef) {
			
			if (sb.length() > 0) {
				sb.append('|');
			}
					
			sb.append(Names.Mood);
			sb.append('=');
			sb.append(convertHuman(MoodIndex, mood_.toString()));
		}

		if (voice_ != Voice.Undef) {
			
			if (sb.length() > 0) {
				sb.append('|');
			}
						
			sb.append(Names.Voice);
			sb.append('=');
			sb.append(convertHuman(VoiceIndex, voice_.toString()));
		}

		if (gender_ != Gender.Undef) {
			
			if (sb.length() > 0) {
				sb.append('|');
			}
					
			sb.append(Names.Gender);
			sb.append('=');
			sb.append(convertHuman(GenderIndex, gender_.toString()));
		}

		if (case_ != Case.Undef) {
			
			if (sb.length() > 0) {
				sb.append('|');
			}
						
			sb.append(Names.Case);
			sb.append('=');
			sb.append(convertHuman(CaseIndex, case_.toString()));
		}

		if (degree_ != Degree.Undef) {
			
			if (sb.length() > 0) {
				sb.append('|');
			}
						
			sb.append(Names.Degree);
			sb.append('=');
			sb.append(convertHuman(DegreeIndex, degree_.toString()));
		}

		if (sb.length() == 0) {
			return "_";
		}		
		
		return sb.toString();
	}

	@Override
	public String toPosString() {
		if (pos_ == Pos.Undef) {
			return "_";
		}
		return pos_.toString();
	}

}
