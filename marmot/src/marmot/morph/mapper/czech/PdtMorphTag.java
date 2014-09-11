// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.mapper.czech;

import marmot.morph.mapper.MorphTag;


public class PdtMorphTag implements MorphTag {

	enum Pos {
		a, // Adjective
		c, // Numeral
		d, // Adverb
		i, // Interjection
		j, // Conjunction
		n, // Noun
		p, // Pronoun
		v, // Verb
		r, // Preposition
		t, // Particle
		z, //Punctuation (also used for the Sentence Boundary token)
		x,
		_,
	};

	enum Type {
		Dash, // Sentence boundary Z - punctuation
		Percent, // Author's signature, e.g. haš-99_:B_;S N - noun
		Asterisk, // Word krát (lit.: times) C - numeral
		Comma, // Conjunction subordinate (incl. aby, kdyby in all forms) J -
				// conjuction
		Bracket, // Numeral, written using Roman numerals (XIV) C - numeral
		Colon, // Punctuation (except for the virtual sentence boundary word
				// ###, which uses the the section called
				// "2 - Detailed part of speech" #) Z - punctuation
		Equals, // Number written using digits C - numeral
		Questionmark, // Numeral kolik (lit. how many/how much) C - numeral
		At, // Unrecognized word form X - unknown
		Zircumflex, // ^ Conjunction (connecting main clauses, not subordinate)
					// J - conjunction
		One, //
		Two, //
		Three, //
		Four, // Relative/interrogative pronoun with adjectival declension of
				// both types (soft and hard) (jaký, který, čí, ..., lit. what,
				// which, whose, ...) P - pronoun
		Five, // The pronoun he in forms requested after any preposition (with
				// prefix n-: něj, něho, ..., lit. him in various cases) P -
				// pronoun
		Six, // Reflexive pronoun se in long forms (sebe, sobě, sebou, lit.
				// myself / yourself / herself / himself in various cases; se is
				// personless) P - pronoun
		Seven, // Reflexive pronouns se (the section called "5 - Case" = 4), si
				// (the section called "5 - Case" = 3), plus the same two forms
				// with contracted -s: ses, sis (distinguished by the section
				// called "8 - Person" = 2; also number is singular only) This
				// should be done somehow more consistently, virtually any word
				// can have this contracted -s (cos, polívkus, ...)
		// P,// - pronoun
		Eight, // Possessive reflexive pronoun svůj (lit. my/your/her/his when
				// the possessor is the subject of the sentence) P - pronoun
		Nine, // Relative pronoun jenž, již, ... after a preposition (n-: něhož,
				// niž, ..., lit. who) P - pronoun
		A, // Adjective, general A - adjective
		B, // Verb, present or future form V - verb
		C, // Adjective, nominal (short, participial) form rád, schopen, ... A -
			// adjective
		D, // Pronoun, demonstrative (ten, onen, ..., lit. this, that, that ...
			// over there, ... ) P - pronoun
		E, // Relative pronoun což (corresponding to English which in
			// subordinate clauses referring to a part of the preceding text) P
			// - pronoun
		F, // Preposition, part of; never appears isolated, always in a phrase
			// (nehledě (na), vzhledem (k), ..., lit. regardless, because of) R
			// - preposition
		G, // Adjective derived from present transgressive form of a verb A -
			// adjective
		H, // Personal pronoun, clitical (short) form (mě, mi, ti, mu, ...);
			// these forms are used in the second position in a clause (lit. me,
			// you, her, him), even though some of them (mě) might be regularly
			// used anywhere as well P - pronoun
		I, // Interjections I - interjection
		J, // Relative pronoun jenž, již, ... not after a preposition (lit. who,
			// whom) P - pronoun
		K, // Relative/interrogative pronoun kdo (lit. who), incl. forms with
			// affixes -ž and -s (affixes are distinguished by the category
			// Table 2.16, "VAR" (for -ž) and the section called "8 - Person"
			// (for -s)) P - pronoun
		L, // Pronoun, indefinite všechnen, sám (lit. all, alone) P - pronoun
		M, // Adjective derived from verbal past transgressive form A -
			// adjective
		N, // Noun (general) N - noun
		O, // Pronoun svůj, nesvůj, tentam alone (lit. own self, not-in-mood,
			// gone) P - pronoun
		P, // Personal pronoun já, ty, on (lit. I, you, he ) (incl. forms with
			// the enclitic -s, e.g. tys, lit. you're); gender position is used
			// for third person to distinguish on/ona/ono (lit. he/she/it), and
			// number for all three persons P - pronoun
		Q, // Pronoun relative/interrogative co, copak, cožpak (lit. what,
			// isn't-it-true-that) P - pronoun
		R, // Preposition (general, without vocalization) R - preposition
		S, // Pronoun possessive můj, tvůj, jeho (lit. my, your, his); gender
			// position used for third person to distinguish jeho, její, jeho
			// (lit. his, her, its), and number for all three pronouns P -
			// pronoun
		T, // Particle T - particle
		U, // Adjective possessive (with the masculine ending -ův as well as
			// feminine -in) A - adjective
		V, // Preposition (with vocalization -e or -u): (ve, pode, ku, ..., lit.
			// in, under, to) R - preposition
		W, // Pronoun negative (nic, nikdo, nijaký, žádný, ..., lit. nothing,
			// nobody, not-worth-mentioning, no/none) P - pronoun
		X, // (temporary) Word form recognized, but tag is missing in dictionary
			// due to delays in (asynchronous) dictionary creation
		Y, // Pronoun relative/interrogative co as an enclitic (after a
			// preposition) (oč, nač, zač, lit. about what, on/onto what,
			// after/for what) P - pronoun
		Z, // Pronoun indefinite (nějaký, některý, číkoli, cosi, ..., lit. some,
			// some, anybody's, something) P - pronoun
		a, // Numeral, indefinite (mnoho, málo, tolik, několik, kdovíkolik, ...,
			// lit. much/many, little/few, that much/many, some (number of),
			// who-knows-how-much/many) C - numeral
		b, // Adverb (without a possibility to form negation and degrees of
			// comparison, e.g. pozadu, naplocho, ..., lit. behind, flatly);
			// i.e. both the the section called "11 - Negation" as well as the
			// Table 2.13, "GRADE" attributes in the same tag are marked by -
			// (Not applicable) D - adverb
		c, // Conditional (of the verb být (lit. to be) only) (by, bych, bys,
			// bychom, byste, lit. would) V - verb
		d, // Numeral, generic with adjectival declension (dvojí, desaterý, ...,
			// lit. two-kinds/..., ten-...) C - numeral
		e, // Verb, transgressive present (endings -e/-ě, -íc, -íce) V - verb
		f, // Verb, infinitive V - verb
		g, // Adverb (forming negation (??? set to A/N) and degrees of
			// comparison Table 2.13, "GRADE" set to 1/2/3
			// (comparative/superlative), e.g. velký, za\-jí\-ma\-vý, ..., lit.
			// big, interesting
		h, // Numeral, generic; only jedny and nejedny (lit. one-kind/sort-of,
			// not-only-one-kind/sort-of) C - numeral
		i, // Verb, imperative form V - verb
		j, // Numeral, generic greater than or equal to 4 used as a syntactic
			// noun (čtvero, desatero, ..., lit. four-kinds/sorts-of, ten-...) C
			// - numeral
		k, // Numeral, generic greater than or equal to 4 used as a syntactic
			// adjective, short form (čtvery, ..., lit. four-kinds/sorts-of) C -
			// numeral
		l, // Numeral, cardinal jeden, dva, tři, čtyři, půl, ... (lit. one, two,
			// three, four); also sto and tisíc (lit. hundred, thousand) if noun
			// declension is not used C - numeral
		m, // Verb, past transgressive; also archaic present transgressive of
			// perfective verbs (ex.: udělav, lit. (he-)having-done; arch. also
			// udělaje (Table 2.16, "VAR" = 4), lit. (he-)having-done) V - verb
		n, // Numeral, cardinal greater than or equal to 5 C - numeral
		o, // Numeral, multiplicative indefinite (-krát, lit. (times):
			// mnohokrát, tolikrát, ..., lit. many times, that many times) C -
			// numeral
		p, // Verb, past participle, active (including forms with the enclitic -
			// s, lit. 're (are)) V - verb
		q, // Verb, past participle, active, with the enclitic -ť, lit.
			// (perhaps) - could-you-imagine-that? or but-because- (both
			// archaic) V - verb
		r, // Numeral, ordinal (adjective declension without degrees of
			// comparison) C - numeral
		s, // Verb, past participle, passive (including forms with the enclitic
			// -s, lit. 're (are)) V - verb
		t, // Verb, present or future tense, with the enclitic -ť, lit.
			// (perhaps) -could-you-imagine-that? or but-because- (both archaic)
			// V - verb
		u, // Numeral, interrogative kolikrát, lit. how many times? C - numeral
		v, // Numeral, multiplicative, definite (-krát, lit. times: pětkrát,
			// ..., lit. five times) C - numeral
		w, // Numeral, indefinite, adjectival declension (nejeden, tolikátý,
			// ..., lit. not-only-one, so-many-times-repeated) C - numeral
		y, // Numeral, fraction ending at -ina; used as a noun (pětina, lit.
			// one-fifth) C - numeral
		z, // Numeral, interrogative kolikátý, lit. what (at-what-position-
			// place-in-a-sequence) C - numeral
		x,
	}

	enum Gender {
		f, // Feminine
		h, // {F, N} - Feminine or Neuter
		i, // Masculine inanimate
		m, // Masculine animate
		n, // Neuter
		q, // Feminine (with singular only) or Neuter (with plural only); used
			// only with participles and nominal forms of adjectives
		t, // Masculine inanimate or Feminine (plural only); used only with
			// participles and nominal forms of adjectives
		y, // {M, I} - Masculine (either animate or inanimate)
		z, // {M, I, N} - Not fenimine (i.e., Masculine animate/inanimate or
			// Neuter); only for (some) pronoun forms and certain numerals
		_,
	}

	enum Number {
		d, // Dual , e.g. nohama
		p, // Plural, e.g. nohami
		s, // Singular, e.g. noha
		w, // Singular for feminine gender, plural with neuter; can only appear
			// in participle or nominal adjective form with gender value Q
		_,
	}

	enum Case {
		nom, // Nominative, e.g. žena
		gen, // Genitive, e.g. ženy
		dat, // Dative, e.g. ženě
		acc, // Accusative, e.g. ženu
		voc, // Vocative, e.g. ženo
		loc, // Locative, e.g. ženě
		ins, // Instrumental, e.g. ženou
		_
	}

//	enum PossGender {
//		f, // Feminine, e.g. matčin, její
//		m, // Masculine animate (adjectives only), e.g. otců
//		z, // {M, I, N} - Not feminine, e.g. jeho
//	}
//	
//	enum PossNumber {
//		p,//	Plural, e.g. náš
//		s,//	Singular, e.g. můj
//	}
	
	enum Person {
		fst,//	1st person, e.g. píšu, píšeme
		snd,//	2nd person, e.g. píšeš, píšete
		thd,//	3rd person, e.g. píše, píšou
		_,
	}
	
	enum Tense {
		f,//	Future
		h,//	{R, P} - Past or Present
		p,//	Present
		r,//	Past
		_,
	}
	
	enum Degree {
		pos, //	Positive, e.g. velký
		comp, //	Comparative, e.g. větší
		sup,//	Superlative, e.g. největší
		_,
	}
	
	enum Negation {
		a,//	Affirmative (not negated), e.g. možný
		n,//	Negated, e.g. nemožný
		_,
	}
	
	enum Voice {
		a,//	Active, e.g. píšící
		p,//	Passive, e.g. psaný
		_,
	}
	
	public Pos pos_;
	public Tense tense_;
	public Person person_;
	public Number number_;
	public Type type_;
	public Gender gender_;
	public Case case_;
	public Degree degree_;
	public Negation negation_;
	public Voice voice_;
	
	public PdtMorphTag() {
		reset();
	}
	
	void reset() {
		pos_ = Pos._;
		type_ = Type.X;
		tense_ = Tense._;
		person_ = Person._;
		number_ = Number._;
		gender_ = Gender._;
		case_ = Case._;
		degree_ = Degree._;
		negation_ = Negation._;
		voice_ = Voice._;
	}

	@Override
	public String toHumanMorphString() {
		return pos_.toString() + type_ + gender_ + number_ + case_ + person_ + tense_ + degree_ + negation_ + voice_;
	}

	@Override
	public String toPosString() {
		return pos_.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((case_ == null) ? 0 : case_.hashCode());
		result = prime * result + ((degree_ == null) ? 0 : degree_.hashCode());
		result = prime * result + ((gender_ == null) ? 0 : gender_.hashCode());
		result = prime * result
				+ ((negation_ == null) ? 0 : negation_.hashCode());
		result = prime * result + ((number_ == null) ? 0 : number_.hashCode());
		result = prime * result + ((person_ == null) ? 0 : person_.hashCode());
		result = prime * result + ((pos_ == null) ? 0 : pos_.hashCode());
		result = prime * result + ((tense_ == null) ? 0 : tense_.hashCode());
		result = prime * result + ((type_ == null) ? 0 : type_.hashCode());
		result = prime * result + ((voice_ == null) ? 0 : voice_.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PdtMorphTag other = (PdtMorphTag) obj;
		if (case_ != other.case_)
			return false;
		if (degree_ != other.degree_)
			return false;
		if (gender_ != other.gender_)
			return false;
		if (negation_ != other.negation_)
			return false;
		if (number_ != other.number_)
			return false;
		if (person_ != other.person_)
			return false;
		if (pos_ != other.pos_)
			return false;
		if (tense_ != other.tense_)
			return false;
		if (type_ != other.type_)
			return false;
		if (voice_ != other.voice_)
			return false;
		return true;
	}
	
}
