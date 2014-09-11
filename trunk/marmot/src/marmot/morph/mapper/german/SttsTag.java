// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.mapper.german;

import marmot.morph.mapper.MorphTag;

public class SttsTag implements MorphTag {

//	enum Pos {
//		_,
//		ADJ,
//		ADV,
//		ART,
//		CARD,
//		CIRCP,
//		CONJ,
//		DEM,
//		INDEF,
//		INTJ,
//		NN,
//		NPROP,
//		ORD,
//		POSS,
//		POSTP,
//		PPRO,
//		PREP,
//		PREPART,
//		PROADV,
//		PTCL,
//		PUNCT,
//		REL,
//		SYMBOL,
//		TRUNC,
//		V,
//		VPART,
//		WADV,
//		WPRO,
//	}
	
//	enum Type {
//		_,
//		Comma, // $,
//		Period, // $.
//		Bracket, // $(
//		ADJA,
//		ADJD,
//		ADV,
//		APPO,
//		APPR,
//		APPRART,
//		APZR,
//		ART,
//		CARD,
//		FM,
//		ITJ,
//		KOKOM,
//		KON,
//		KOUI,
//		KOUS,
//		NE,
//		NN,
//		NNE,
//		PDAT,
//		PDS,
//		PIAT,
//		PIS,
//		PPER,
//		PPOSAT,
//		PPOSS,
//		PRELAT,
//		PRELS,
//		PRF,
//		PROAV,
//		PTKA,
//		PTKANT,
//		PTKNEG,
//		PTKVZ,
//		PTKZU,
//		PWAT,
//		PWAV,
//		PWS,
//		TRUNC,
//		VAFIN,
//		VAIMP,
//		VAINF,
//		VAPP,
//		VMFIN,
//		VMINF,
//		VMPP,
//		VVFIN,
//		VVIMP,
//		VVINF,
//		VVIZU,
//		VVPP,
//		XY,
//	}
	
	enum Case {
		_,
		amb,
		acc,
		dat,
		gen,
		nom
	}
	
	enum Degree {
		_,
		amb,
		comp,
		pos,
		sup,
	}
	
	enum Gender {
		_,
		amb,
		fem,
		masc,
		neut
	}
	
	enum Mood {
		_,
		imp,
		ind,
		subj,		
	}
	
	enum Number {
		_,
		amb,
		pl,
		sg,
	}
	
	enum Person {
		_,
		fst,
		snd,
		thd,
	}
	
	enum Tense {
		_,
		past,
		pres,
	}
	
	//Pos pos_;
	//Type type_;
	Case case_;
	Number number_;
	Gender gender_;
	Degree degree_;
	Person person_;
	Tense tense_;
	Mood mood_;
	String feat_string_;
	
	public SttsTag() {
		reset();
	}
	
	public void setFeatString(String feat_string) {
		feat_string_ = feat_string;
	}
	
	void reset() {
		//pos_ = Pos._;
		//type_ = Type._;
		case_ = Case._;
		number_ = Number._;
		gender_ = Gender._;
		degree_ = Degree._;
		person_ = Person._;
		tense_ = Tense._;
		mood_ = Mood._;
	}
	
	@Override 
	public String toString() {
		return toHumanMorphString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((case_ == null) ? 0 : case_.hashCode());
		result = prime * result + ((degree_ == null) ? 0 : degree_.hashCode());
		result = prime * result
				+ ((feat_string_ == null) ? 0 : feat_string_.hashCode());
		result = prime * result + ((gender_ == null) ? 0 : gender_.hashCode());
		result = prime * result + ((mood_ == null) ? 0 : mood_.hashCode());
		result = prime * result + ((number_ == null) ? 0 : number_.hashCode());
		result = prime * result + ((person_ == null) ? 0 : person_.hashCode());
		result = prime * result + ((tense_ == null) ? 0 : tense_.hashCode());
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
		SttsTag other = (SttsTag) obj;
		if (case_ != other.case_)
			return false;
		if (degree_ != other.degree_)
			return false;
		if (feat_string_ == null) {
			if (other.feat_string_ != null)
				return false;
		} else if (!feat_string_.equals(other.feat_string_))
			return false;
		if (gender_ != other.gender_)
			return false;
		if (mood_ != other.mood_)
			return false;
		if (number_ != other.number_)
			return false;
		if (person_ != other.person_)
			return false;
		if (tense_ != other.tense_)
			return false;
		return true;
	}

	@Override
	public String toHumanMorphString() {
		if (feat_string_ != null) {
			return feat_string_;
		}
		
		StringBuilder sb = new StringBuilder();
		
		if (case_ != Case._) {
			sb.append(case_);
		}

		if (gender_ != Gender._) {
			sb.append(gender_);
		}
		
		if (number_ != Number._) { 
			sb.append(number_);
		}
		
		if (person_ != Person._) { 
			sb.append(person_);
		}
		
		if (tense_ != Tense._) { 
			sb.append(tense_);
		}

		if (mood_ != Mood._) { 
			sb.append(mood_);
		}

		if (degree_ != Degree._) {
			sb.append(degree_);
		}

		if (sb.length() == 0) {
			sb.append('_');
		}
		
		return sb.toString();
	}

	@Override
	public String toPosString() {
		throw new UnsupportedOperationException();
	}
	
	




}
