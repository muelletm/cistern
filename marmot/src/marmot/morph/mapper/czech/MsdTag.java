// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.mapper.czech;

import marmot.morph.mapper.MorphTag;
import marmot.morph.mapper.Names;

public class MsdTag implements MorphTag {

	enum Pos {
		v, a, c, n, m, s, r, q, p, x, y, i, z, _,
	}

	enum Type {
		m, // main / mutiple
		a, // auxiliary
		o, // modal / ordinal
		c, // copula / common / coord / cardinal
		f, // qualificative
		s, // possessive / special (number) / subord
		p, // proper, prepositon, personal
		g, // general
		d, // demonstrative
		i, // indefinite
		q, // interrogative
		r, // relative
		x, // reflexive
		z, // negative
		_
	}

	enum Mood {
		i, // indicative i
		m, // imperative
		c, // conditional
		n, // infinitive n
		p, // participle
		t, // transgressive t
		_,
	}

	enum Tense {
		p, // Tense present p
		f, // future f
		s, // past s
		_,
	}

	enum Person {
		fst, // first 1
		snd, // second 2
		thd, // third 3
		_,
	}

	enum Number {
		s, // singular s
		p, // plural p
		d, // dual d
		_,
	}

	enum Gender {
		m, // masculine m
		f, // feminine f
		n, // neuter n
		_;
	}

	enum Voice {
		a, // active a
		p, // passive p
		_;
	}

	// enum Negative {
	// n, y, _,
	// }

	// enum Animate {
	// n, y, _,
	// }

	enum Degree {
		p, // positive p
		c, // comparative c
		s, // superlative s
		_,
	}

	enum Case {
		n, // nominative n
		g, // genitive g
		d, // dative d
		a, // accusative a
		v, // vocative v
		l, // locative l
		i, // instrumental i
		_
	}

	// enum Formation {
	// n, //nominal,
	// c, //compound,
	// _
	// }

	public Pos pos_;
	public Type type_;
	public Tense tense_;
	public Person person_;
	public Number number_;
	public Mood mood_;
	public Gender gender_;
	public Voice voice_;
	// public Negative negative_;
	// public Animate animate_;
	public Degree degree_;
	public Case case_;

	// public Formation formation_;

	public MsdTag() {
		reset();
	}

	void reset() {
		pos_ = Pos._;
		type_ = Type._;
		mood_ = Mood._;
		tense_ = Tense._;
		person_ = Person._;
		number_ = Number._;
		gender_ = Gender._;
		voice_ = Voice._;
		// negative_ = Negative._;
		// animate_ = Animate._;
		degree_ = Degree._;
		case_ = Case._;
		// formation_ = Formation._;
	}

	@Override
	public String toHumanMorphString() {
		StringBuilder sb = new StringBuilder();

		addFeature(sb, Names.Tense, tense_ == Tense._, tense_.toString());
		addFeature(sb, Names.Person, person_ == Person._, person_.toString());
		addFeature(sb, Names.Number, number_ == Number._, number_.toString());
		addFeature(sb, Names.Gender, gender_ == Gender._, gender_.toString());
		addFeature(sb, Names.Voice, voice_ == Voice._, voice_.toString());
		addFeature(sb, Names.Degree, degree_ == Degree._, degree_.toString());
		addFeature(sb, Names.Case, case_ == Case._, case_.toString());

		if (sb.length() == 0) {
			return "_";
		}

		return sb.toString();
	}

	private void addFeature(StringBuilder sb, String category, boolean b,
			String value) {
		if (!b) {
			if (sb.length() > 0)
				sb.append(Names.Sep);

			sb.append(category);
			sb.append('=');
			sb.append(value);
		}
	}
	
	public String toHumanString() {
		StringBuilder sb = new StringBuilder();

		sb.append(toPosString());
		String morph = toHumanMorphString();
		if (!morph.equals("_")) {
			sb.append(Names.Sep);
			sb.append(morph);
		}
		
		return sb.toString();
	}

	@Override
	public String toPosString() {
		return pos_.toString();
	}
}
