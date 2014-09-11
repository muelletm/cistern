// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.mapper.spanish;

import java.util.HashSet;
import java.util.Set;

import marmot.morph.mapper.MorphTag;
import marmot.morph.mapper.Names;
import marmot.morph.mapper.Node;

// Based on http://nlp.lsi.upc.edu/freeling/doc/tagsets/tagset-es.html
// Modifcations:
// based on the data (CoNLL 2009 dataset, IULA treebank) all numbers don't get types
// No adjective degree and noun degree in CoNLL 2009
// Weird type "numeral" in CoNLL:
//  - dos, tres, ... seem to be tagged as z in IULA and as p,d in CoNLL 
//  - other words like "ambos" are of type indefinite in IULA and of type numeral in CoNLL
//  - current treatment: move numeral to indefinite
//  - move certain forms to z (un, uno, dos, tres, ...)
// IULA doesn't use fun=p it uses verbs with mood=p instead:
//  - CoNLL a type=q|num=.|gen=.|fun=p -> c type=m|num=.|gen=.|mood=p 
// IULA annotates all prepositions as SPS00
// In CoNLL proper nouns have common gender and invariable number.
// In CoNLL verbs might have common gender and invariable number
// In ConLL and IULA "se" is not annotated as PP3CN000

public class EaglesTag implements MorphTag {

	Pos pos_;
	Type type_;
	Degree degree_;
	Gender gender_;
	Number number_;
	Function function_;
	Mood mood_;
	Tense tense_;
	Person person_;
	Case case_;
	OwnerNumber owner_;
	Politeness politeness_;
	Form form_;
	Closing closing_;
	NounDegree noun_degree_;

	enum Pos {
		a, // Adjective
		c, // Conjunction
		d, // Determiner
		f, // Punctuation
		i, // Interjection
		n, // Noun
		p, // Pronoun
		r, // Adverbs
		s, // Preposition
		v, // Verb
		w, // Date
		z, // Numeral
		_, // Undef
	}

	enum Type {
		a, // article, auxiliary, exclamationmark
		c, // common, coordinating, comma
		d, // demonstrative, colon
		e, // exclamative, quotation
		g, // general, hyphen
		h, // slash
		i, // indefinite, question mark
		t, // interrogative, percentage
		m, // main, principal, currency
		n, // negative
		o, // ordinal
		p, // possessive (determiner), proper, personal, preposition, period,
			// bracket
		q, // qualificative
		r, // relative, «/»)
		s, // semiauxiliary, subordinating, etc
		x, // possesive (pronoun) , semicolon
		z, // mathsign
		_, // undef,
	}

	enum Degree {
		c, // dimunitive
		s, // superlative
		_, // undef
	}

	enum Gender {
		m, // masculine
		f, // femine
		n, // neuter
		c, // common
		_, // undef
	}

	enum Number {
		s, // singular
		p, // plural
		n, // invariable
		_, // undef
	}

	enum Function {
		p, // participle
		_, // undef
	}

	enum Mood {
		i, // Indicativo
		s, // Subjuntivo
		m, // Imperativo
		n, // Infinitivo
		g, // Gerundio
		p, // Participio
		_, // Undef
	};

	enum Tense {
		p, // Presente
		i, // Imperfecto
		f, // Futuro
		s, // Pasado
		c, // Condicional
		_, // Undef
	}

	enum Person {
		first, second, third, _
	}

	enum Case {
		n, // nominative
		a, // accusative
		d, // dative
		o, // oblicuo
		_, // undef
	}

	enum OwnerNumber {
		s, // Singular
		p, // Plural
		_, // undef
	}

	enum Politeness {
		p, // Polite
		_, // Undef
	}

	enum Form {
		s, // simple
		c, // contracted
		_, // undef
	}

	enum Closing {
		a, // opening
		t, // closing
		_
	}

	enum NounDegree {
		d, // dimunitive
		a, // aumentative
		_, // undef
	}

	public EaglesTag() {
		reset();
	}

	public void reset() {
		pos_ = Pos._;
		type_ = Type._;
		degree_ = Degree._;
		gender_ = Gender._;
		number_ = Number._;
		function_ = Function._;
		mood_ = Mood._;
		tense_ = Tense._;
		person_ = Person._;
		case_ = Case._;
		owner_ = OwnerNumber._;
		politeness_ = Politeness._;
		form_ = Form._;
		closing_ = Closing._;
		noun_degree_ = NounDegree._;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(pos_);
		sb.append(type_);
		sb.append(degree_);
		sb.append(gender_);
		sb.append(number_);
		sb.append(function_);
		sb.append(mood_);
		sb.append(tense_);
		sb.append(person_);
		sb.append(case_);
		sb.append(owner_);
		sb.append(politeness_);
		sb.append(form_);
		sb.append(closing_);
		sb.append(noun_degree_);

		return sb.toString();
	}

	public String toHumanMorphString() {
		StringBuilder sb = new StringBuilder();

		addFeature(sb, Names.Type, type_ == Type._,
				type_.toString());
		addFeature(sb, Names.Degree, degree_ == Degree._,
				degree_.toString());
		addFeature(sb, Names.Number, number_ == Number._, number_.toString());
		addFeature(sb, Names.Gender, gender_ == Gender._, gender_.toString());
		addFeature(sb, Names.Function, function_ == Function._, function_.toString());
		addFeature(sb, Names.Mood, mood_ == Mood._,
				mood_.toString());
		addFeature(sb, Names.Tense, tense_ == Tense._,
				tense_.toString());
		addFeature(sb, Names.Person, person_ == Person._, person_.toString());
		addFeature(sb, Names.Case, case_ == Case._, case_.toString());
		addFeature(sb, Names.OwnerNumber, owner_ == OwnerNumber._, owner_.toString());
		addFeature(sb, Names.Politeness, politeness_ == Politeness._, politeness_.toString());
		addFeature(sb, Names.Form, form_ == Form._,
				form_.toString());
		addFeature(sb, Names.Closing, closing_ == Closing._, closing_.toString());
		addFeature(sb, Names.NounDegree, noun_degree_ == NounDegree._, noun_degree_.toString());

		if (sb.length() == 0) {
			return "_";
		}
		
		return sb.toString();
	}

	public String toHumanString() {
		StringBuilder sb = new StringBuilder();

		addFeature(sb, "", false, pos_.toString());
		sb.append("|");
		sb.append(toHumanMorphString());

		return sb.toString();
	}

	private void addFeature(StringBuilder sb, String name, boolean b,
			String value) {
		if (!b) {

			switch (value) {
			case "first":
				value = "1";
				break;
			case "second":
				value = "2";
				break;
			case "third":
				value = "3";
				break;
			default:
				break;
			}

			if (sb.length() > 0)
				sb.append('|');

			if (name.length() > 0) {
				sb.append(name.toLowerCase());
				sb.append('=');
			}
			sb.append(value);
		}
	}

	private final static Set<String> numbers = new HashSet<>();
	static {
		numbers.add("catorce");
		numbers.add("cero");
		numbers.add("cien");
		numbers.add("cien_mil");
		numbers.add("cien_por_cien");
		numbers.add("cien_por_ciento");
		numbers.add("ciento_ochenta");
		numbers.add("ciento_sesenta_mil_millones");
		numbers.add("ciento_setenta_y_ocho");
		numbers.add("cinco");
		numbers.add("cinco_mil");
		numbers.add("cinco_mil_millones");
		numbers.add("cinco_por_ciento");
		numbers.add("cincuenta");
		numbers.add("cincuenta_por_ciento");
		numbers.add("cincuenta_y_dos");
		numbers.add("cincuenta_y_uno");
		numbers.add("cuarenta");
		numbers.add("cuarenta_y_cinco");
		numbers.add("cuarenta_y_dos");
		numbers.add("cuarenta_y_ocho");
		numbers.add("cuatro");
		numbers.add("cuatro_de_cada_diez");
		numbers.add("cuatro_millones");
		numbers.add("cuatro_mil_millones");
		numbers.add("cuatro_por_ciento");
		numbers.add("cuatro_por_mil");
		numbers.add("diecinueve");
		numbers.add("dieciocho");
		numbers.add("dieciséis");
		numbers.add("diecisiete");
		numbers.add("diez");
		numbers.add("diez_mil");
		numbers.add("diez_millones");
		numbers.add("diez_por_ciento");
		numbers.add("doce");
		numbers.add("dos");
		numbers.add("doscientas_cincuenta");
		numbers.add("dos_millones");
		numbers.add("dos_mil_millones");
		numbers.add("dos_mil_quinientas");
		numbers.add("dos_por_ciento");
		numbers.add("dos_por_mil");
		numbers.add("media_docena");
		numbers.add("mil");
		numbers.add("mil_millones");
		numbers.add("mil_seiscientas");
		numbers.add("noventa");
		numbers.add("noventa_por_ciento");
		numbers.add("nueve");
		numbers.add("nueve_de_cada_diez");
		numbers.add("nueve_mil");
		numbers.add("ochenta");
		numbers.add("ocho");
		numbers.add("ocho_de_cada_diez");
		numbers.add("ocho_por_ciento");
		numbers.add("once");
		numbers.add("quince");
		numbers.add("quince_por_ciento");
		numbers.add("quinientas");
		numbers.add("quinientos_mil");
		numbers.add("quinientos_un");
		numbers.add("seis");
		numbers.add("seis_millones");
		numbers.add("seis_por_ciento");
		numbers.add("sesenta");
		numbers.add("sesenta_y_cinco");
		numbers.add("sesenta_y_nueve");
		numbers.add("sesenta_y_ocho");
		numbers.add("sesenta_y_seis");
		numbers.add("sesenta_y_siete");
		numbers.add("sesenta_y_un");
		numbers.add("setenta");
		numbers.add("siete");
		numbers.add("siete_mil");
		numbers.add("siete_por_ciento");
		numbers.add("tanto_por_ciento");
		numbers.add("treinta");
		numbers.add("treinta_mil_millones");
		numbers.add("tres");
		numbers.add("trescientas_sesenta_y_cinco");
		numbers.add("trescientos_doce");
		numbers.add("tres_de_cada_cuatro");
		numbers.add("tres_mil");
		numbers.add("tres_millones");
		numbers.add("tres_mil_millones");
		numbers.add("tres_por_ciento");
		numbers.add("un");
		numbers.add("una");
		numbers.add("una_de_cada_cuatro");
		numbers.add("una_docena");
		numbers.add("un_centenar");
		numbers.add("un_millar");
		numbers.add("un_millón");
		numbers.add("uno");
		numbers.add("uno_de_cada_diez");
		numbers.add("uno_por_ciento");
		numbers.add("veinte");
		numbers.add("veinte_mil");
		numbers.add("veinte_mil_millones");
		numbers.add("veinte_por_ciento");
		numbers.add("veinticinco");
		numbers.add("veinticinco_mil");
		numbers.add("veinticinco_mil_millones");
		numbers.add("veinticuatro");
		numbers.add("veintitrés");
		numbers.add("veintiuno");
	}

	public void normalize(Node node, boolean iula) {
		String form = node.getForm();

		degree_ = EaglesTag.Degree._;
		noun_degree_ = EaglesTag.NounDegree._;

		if (form.equals("se")) {
			reset();
			pos_ = Pos.p;
			type_ = Type.r;
			number_ = Number.n;
			gender_ = Gender.c;
			person_ = Person.third;
			return;
		}

		switch (pos_) {
		case a:
			if (function_ == Function.p) {
				pos_ = Pos.v;
				function_ = Function._;
				mood_ = Mood.p;
				type_ = Type.m;
			}
			break;
		case d:
		case p:
			if (numbers.contains(form)) {
				reset();
				pos_ = Pos.z;
			}
			break;
		case s:
			reset();
			pos_ = Pos.s;
			type_ = Type.p;
			form_ = Form.s;
			break;
		case n:
			if (type_ == Type.p) {
				reset();
				pos_ = Pos.n;
				type_ = Type.p;
			}
			break;
		case v:
			if (gender_ == Gender.c) {
				gender_ = Gender._;
			}
			if (number_ == Number.n) {
				number_ = Number._;
			}

			if (node.getLemma().equals("ser")) {
				type_ = Type.s;
			} else {

				if (node.getLemma().equals("estar")) {

					boolean found_gerund = false;


					if (!iula) {

						Node head = node.getHead();
						if (head != null) {
							EaglesTag tag = (EaglesTag) head.getMorphTag();
							if (tag.pos_ == EaglesTag.Pos.v
									&& tag.mood_ == Mood.g) {
								found_gerund = true;
							}
						}

					} else {

						for (Node child : node.getChildren()) {
							EaglesTag tag = (EaglesTag) child.getMorphTag();
							if (tag.pos_ == EaglesTag.Pos.v
									&& tag.mood_ == Mood.g) {
								found_gerund = true;
								break;
							}
						}

					}

					if (found_gerund) {
						type_ = Type.a;
					} else {
						type_ = Type.m;
					}

				}

			}

			break;
		case r:
			if (type_ == Type.n) {
				reset();
				pos_ = Pos.r;
				type_ = Type.n;
			} else {
				pos_ = Pos.r;
				type_ = Type.g;
			}
			break;
		default:
			break;
		}

	}

	@Override
	public String toPosString() {
		return pos_.toString();
	}

}
