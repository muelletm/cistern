// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.mapper.latin;

import marmot.morph.mapper.latin.LdtMorphTag.Degree;
import marmot.morph.mapper.latin.LdtMorphTag.Mood;
import marmot.morph.mapper.latin.LdtMorphTag.Pos;
import marmot.morph.mapper.latin.LdtMorphTag.Tense;
import marmot.morph.mapper.latin.LdtMorphTag.Voice;

public class ProielLdtMorphTagMapper {

	public LdtMorphTag convert(String pos, String feats) {
		LdtMorphTag tag = new LdtMorphTag();
		setPos(tag, pos);
		setFeats(tag, feats);
		return tag;
	}

	public void setFeats(LdtMorphTag tag, String feats_string) {
		String[] feats = feats_string.split("\\|");
		for (String feat : feats) {
			setFeat(tag, feat);
		}
	}

	private void setFeat(LdtMorphTag tag, String feat) {
		feat = feat.toLowerCase();
		switch (feat) {
		case "numbs":
			tag.number_ = LdtMorphTag.Number.s;
			break;

		case "numbp":
			tag.number_ = LdtMorphTag.Number.p;
			break;
			
		case "gendf":
			tag.gender_ = LdtMorphTag.Gender.f;
			break;
			
		case "gendn":
			tag.gender_ = LdtMorphTag.Gender.n;
			break;
			
		case "gendm":
			tag.gender_ = LdtMorphTag.Gender.m;
			break;
			
		case "gendp":
		case "gendq":
		case "gendo":
		case "gendr":
			break;
			
			
//			n, // nominative
//			g, // genitive
//			d, // dative
//			a, // accusative
//			b, // ablative
//			v, // vocative
//			l, // locative
			
		case "casea":
			tag.case_ = LdtMorphTag.Case.a;
			break;
			
		case "casen":
			tag.case_ = LdtMorphTag.Case.n;
			break;
			
		case "caseg":
			tag.case_ = LdtMorphTag.Case.g;
			break;
			
		case "cased":
			tag.case_ = LdtMorphTag.Case.d;
			break;
			
		case "caseb":
			tag.case_ = LdtMorphTag.Case.b;
			break;
			
		case "casel":
			tag.case_ = LdtMorphTag.Case.l;
			break;

		case "casev":
			tag.case_ = LdtMorphTag.Case.v;
			break;

		case "infln":
		case "infli":
			break;
			
		case "pers1":
			tag.person_ = LdtMorphTag.Person.first;
			break;
			
		case "pers2":
			tag.person_ = LdtMorphTag.Person.second;
			break;

		case "pers3":
			tag.person_ = LdtMorphTag.Person.third;
			break;

		case "tensr":
			tag.tense_ = Tense.r;
			break;
			
		case "tensl":
			tag.tense_ = Tense.l;
			break;
			
		case "tenst":
			tag.tense_ = Tense.t;
			break;
			
		case "tensp":
			tag.tense_ = Tense.p;
			break;
			
		case "tensi":
			tag.tense_ = Tense.i;
			break;
			
		case "tensf":
			tag.tense_ = Tense.f;
			break;
		
		case "moodi":
			tag.mood_ = Mood.i;
			break;
			
		case "moods":
			tag.mood_ = Mood.s;
			break;
			
		case "moodn":
			tag.mood_ = Mood.n;
			break;
			
		case "moodm":
			tag.mood_ = Mood.m;
			break;
			
		case "moodp":
			tag.pos_ = Pos.t;
			tag.mood_ = Mood.p;
			break;

		case "moodd":
			tag.mood_ = Mood.d;
			break;
			
		case "moodg":
			tag.mood_ = Mood.g;
			break;

		case "moodu":
			tag.mood_ = Mood.u;
			break;

		case "voica":
			tag.voice_ = Voice.a;
			break;

		case "voicp":
			tag.voice_ = Voice.p;
			break;
			
		case "degrp":
			break;
			
		case "degrs":
			tag.degree_ = Degree.s;
			break;

		case "degrc":
			tag.degree_ = Degree.c;
			break;

		default:
			throw new RuntimeException("Unknown feat: " + feat);
			
		}
	}

	public void setPos(LdtMorphTag tag, String pos) {

		assert pos.length() == 2;

		char c = Character.toLowerCase(pos.charAt(0));

		switch (c) {

		case 'n':
			tag.pos_ = Pos.n;
			break;

		case 'v':
			tag.pos_ = Pos.v;
			break;

		case 'd':
			tag.pos_ = Pos.d;
			break;

		case 'c':
		case 'g':
			tag.pos_ = Pos.c;
			break;

		case 'p':
			tag.pos_ = Pos.p;
			break;

		case 'r':
			tag.pos_ = Pos.r;
			break;

		case 'm':
			tag.pos_ = Pos.m;
			break;

		case 'a':
			tag.pos_ = Pos.a;
			break;

		case 'i':
			tag.pos_ = Pos.i;
			break;

		case 'f':
			tag.pos_ = Pos.Undef;
			break;

		default:
			throw new RuntimeException("Unknown tag character: " + c);
		}

	}

}
