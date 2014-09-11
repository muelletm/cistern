// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.mapper.czech;

public class PdTMsdMapper {

	public MsdTag map(PdtMorphTag tag) {
		MsdTag msd_tag = new MsdTag();

		setPos(msd_tag, tag);
		setTense(msd_tag, tag);
		setPerson(msd_tag, tag);
		setNumber(msd_tag, tag);
		setGender(msd_tag, tag);

		// There are some cyclic dependencies ...
		setNumber(msd_tag, tag);
		setGender(msd_tag, tag);

		setVoice(msd_tag, tag);
		setDegree(msd_tag, tag);
		setCase(msd_tag, tag);

		return msd_tag;
	}

	private void setCase(MsdTag msd_tag, PdtMorphTag tag) {
		switch (tag.case_) {
		case nom:
			msd_tag.case_ = MsdTag.Case.n;
			break;
		case gen:
			msd_tag.case_ = MsdTag.Case.g;
			break;
		case dat:
			msd_tag.case_ = MsdTag.Case.d;
			break;
		case acc:
			msd_tag.case_ = MsdTag.Case.a;
			break;
		case loc:
			msd_tag.case_ = MsdTag.Case.l;
			break;
		case voc:
			msd_tag.case_ = MsdTag.Case.v;
			break;
		case ins:
			msd_tag.case_ = MsdTag.Case.i;
			break;
		case _:
			break;
		}
	}

	private void setDegree(MsdTag msd_tag, PdtMorphTag tag) {
		switch (tag.degree_) {
		case pos:
			msd_tag.degree_ = MsdTag.Degree.p;
			break;
		case comp:
			msd_tag.degree_ = MsdTag.Degree.c;
			break;
		case sup:
			msd_tag.degree_ = MsdTag.Degree.s;
			break;
		case _:
			break;
		}
	}

	private void setVoice(MsdTag msd_tag, PdtMorphTag tag) {
		switch (tag.voice_) {
		case a:
			msd_tag.voice_ = MsdTag.Voice.a;
			break;
		case p:
			msd_tag.voice_ = MsdTag.Voice.p;
			break;
		case _:
			break;
		}
	}

	private void setGender(MsdTag msd_tag, PdtMorphTag tag) {

		switch (tag.gender_) {
		case f:
			msd_tag.gender_ = MsdTag.Gender.f;
			break;

		case i: // Masculine inanimate
		case m: // Masculine animate
		case y: // Masculine (either animate or inanimate)
			msd_tag.gender_ = MsdTag.Gender.m;
			break;

		case n:
			msd_tag.gender_ = MsdTag.Gender.n;
			break;
		case q:
			// Feminine (with singular only) or Neuter (with plural only)

			if (msd_tag.number_ == MsdTag.Number.s) {
				msd_tag.gender_ = MsdTag.Gender.f;
			}

			if (msd_tag.number_ == MsdTag.Number.p) {
				msd_tag.gender_ = MsdTag.Gender.n;
			}

			break;

		case t:
			// Masculine inanimate or Feminine (plural only)

			if (msd_tag.number_ == MsdTag.Number.s) {
				msd_tag.gender_ = MsdTag.Gender.m;
			}

			break;

		case z:
		case h:
		case _:
			break;
		}
	}

	private void setNumber(MsdTag msd_tag, PdtMorphTag tag) {

		switch (tag.number_) {
		case s:
			msd_tag.number_ = MsdTag.Number.s;
			break;
		case p:
			msd_tag.number_ = MsdTag.Number.p;
			break;
		case d:
			msd_tag.number_ = MsdTag.Number.d;
			break;
		case w:

			// Singular for feminine gender, plural with neuter;

			if (msd_tag.gender_ == MsdTag.Gender.f) {
				msd_tag.number_ = MsdTag.Number.s;
			}

			if (msd_tag.gender_ == MsdTag.Gender.n) {
				msd_tag.number_ = MsdTag.Number.p;
			}

			break;
		case _:
			break;
		}
	}

	private void setPerson(MsdTag msd_tag, PdtMorphTag tag) {
		switch (tag.person_) {
		case fst:
			msd_tag.person_ = MsdTag.Person.fst;
			break;
		case snd:
			msd_tag.person_ = MsdTag.Person.snd;
			break;
		case thd:
			msd_tag.person_ = MsdTag.Person.thd;
			break;
		case _:
			break;
		}
	}

	private void setTense(MsdTag msd_tag, PdtMorphTag tag) {
		switch (tag.tense_) {
		case f:
			msd_tag.tense_ = MsdTag.Tense.f;
			break;
		case h:
			break;
		case p:
			msd_tag.tense_ = MsdTag.Tense.p;
			break;
		case r:
			msd_tag.tense_ = MsdTag.Tense.s;
			break;
		case _:
			break;
		}
	}

	private void setPos(MsdTag msd_tag, PdtMorphTag tag) {
		switch (tag.pos_) {
		case a: // Adjective
			msd_tag.pos_ = MsdTag.Pos.a;
			break;
		case c: // Numeral
			msd_tag.pos_ = MsdTag.Pos.m;
			break;
		case d: // Adverb
			msd_tag.pos_ = MsdTag.Pos.r;
			break;
		case i: // Interjection
			msd_tag.pos_ = MsdTag.Pos.i;
			break;
		case j: // Conjunction
			msd_tag.pos_ = MsdTag.Pos.c;
			break;
		case n: // Noun
			msd_tag.pos_ = MsdTag.Pos.n;
			break;
		case p: // Pronoun
			msd_tag.pos_ = MsdTag.Pos.p;
			break;
		case v: // Verb
			msd_tag.pos_ = MsdTag.Pos.v;
			break;
		case r: // Preposition
			msd_tag.pos_ = MsdTag.Pos.s;
			break;
		case t: // Particle
			msd_tag.pos_ = MsdTag.Pos.q;
			break;
		case z: // Punctuation (also used for the Sentence Boundary token)
			msd_tag.pos_ = MsdTag.Pos.z;
			break;
		case _:
			break;
		}
	}

}
