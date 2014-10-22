// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.mapper.latin;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import marmot.morph.mapper.latin.ItMorphTag.CaseNumber;
import marmot.morph.mapper.latin.ItMorphTag.FlexionalCategory;
import marmot.morph.mapper.latin.ItMorphTag.NominalsDegree;
import marmot.morph.mapper.latin.ItMorphTag.ParticipialsDegree;
import marmot.morph.mapper.latin.LdtMorphTag.Pos;

public class BrandoliniRules {

	static Set<String> f11_nouns;
	static Set<String> f11_pronouns;
	static Set<String> f11_adjectives;
	static Set<String> f11_numbers;
	static Set<String> f11_adverbs;
	static Set<String> o4_conj;
	static Set<String> o4_adverb;
	static Set<String> o4_adverb_deprels;
	static Set<String> o4_conj_deprels;
	
	static Set<String> pronoun_lemmas;
	
	
	static Set<String> pronouns;
	static Set<String> nouns;
	
	{
		nouns = new HashSet<String>();
		nouns.add("aristoteles");
		nouns.add("boetius");
		nouns.add("plato");
		nouns.add("augustinus");
		nouns.add("dionysius");
		nouns.add("avicenna");

		pronouns = new HashSet<String>();
		pronouns.add("alterius");
		pronouns.add("unumquodque");
		pronouns.add("quicquid");
		pronouns.add("cuiuslibet");
		pronouns.add("quaelibet");
		pronouns.add("qualibet");
		pronouns.add("uniuscuiusque");
		pronouns.add("quolibet");
		pronouns.add("quodlibet");
		pronouns.add("unicuique");
		pronouns.add("cuilibet");
		pronouns.add("unaquaeque");

		f11_nouns = new HashSet<String>();
		f11_nouns.add("anima");
		f11_nouns.add("animabus");
		f11_nouns.add("animae");
		f11_nouns.add("animam");
		f11_nouns.add("animarum");
		f11_nouns.add("animas");
		f11_nouns.add("bovis");
		f11_nouns.add("dei");
		f11_nouns.add("deo");
		f11_nouns.add("deum");
		f11_nouns.add("deus");
		f11_nouns.add("domibus");
		f11_nouns.add("domo");
		f11_nouns.add("domui");
		f11_nouns.add("domum");
		f11_nouns.add("domus");
		f11_nouns.add("vi");
		f11_nouns.add("vim");
		f11_nouns.add("vires");
		f11_nouns.add("viribus");
		f11_nouns.add("vis");

		f11_pronouns = new HashSet<String>();
		f11_pronouns.add("aliqua");
		f11_pronouns.add("aliquid");
		f11_pronouns.add("aliquis");
		f11_pronouns.add("aliud");
		f11_pronouns.add("ego");
		f11_pronouns.add("me");
		f11_pronouns.add("mihi");
		f11_pronouns.add("nobis");
		f11_pronouns.add("nobiscum");
		f11_pronouns.add("nos");
		f11_pronouns.add("se");
		f11_pronouns.add("secum");
		f11_pronouns.add("seipsa");
		f11_pronouns.add("seipsam");
		f11_pronouns.add("seipsas");
		f11_pronouns.add("seipsis");
		f11_pronouns.add("seipso");
		f11_pronouns.add("seipsum");
		f11_pronouns.add("semetipsum");
		f11_pronouns.add("sese");
		f11_pronouns.add("sibi");
		f11_pronouns.add("sui");
		f11_pronouns.add("te");
		f11_pronouns.add("tu");
		f11_pronouns.add("vestrum");
		f11_pronouns.add("vobis");
		f11_pronouns.add("vos");

		// Alle zu Pronomen
		f11_adjectives = new HashSet<String>();
		f11_adjectives.add("aliqui");
		f11_adjectives.add("aliquod");
		f11_adjectives.add("ambo");
		f11_adjectives.add("amborum");
		f11_adjectives.add("mei");
		f11_adjectives.add("meum");
		f11_adjectives.add("nulla");
		f11_adjectives.add("nullam");
		f11_adjectives.add("nullas");
		f11_adjectives.add("nulli");
		f11_adjectives.add("nullius");
		f11_adjectives.add("nullo");
		f11_adjectives.add("nullum");
		f11_adjectives.add("nullus");
		f11_adjectives.add("sola");
		f11_adjectives.add("solae");
		f11_adjectives.add("solam");
		f11_adjectives.add("solius");
		f11_adjectives.add("solo");
		f11_adjectives.add("solus");
		f11_adjectives.add("tota");
		f11_adjectives.add("totam");
		f11_adjectives.add("toti");
		f11_adjectives.add("totius");
		f11_adjectives.add("toto");
		f11_adjectives.add("totum");

		f11_numbers = new HashSet<String>();
		f11_numbers.add("duabus");
		f11_numbers.add("duae");
		f11_numbers.add("duas");
		f11_numbers.add("duo");
		f11_numbers.add("duobus");
		f11_numbers.add("duorum");
		f11_numbers.add("duos");
		f11_numbers.add("una");
		f11_numbers.add("unam");
		f11_numbers.add("uni");
		f11_numbers.add("unius");
		f11_numbers.add("uno");
		f11_numbers.add("unum");
		f11_numbers.add("unus");
		f11_numbers.add("una");

		f11_adverbs = new HashSet<String>();
		f11_adverbs.add("aliter");
		f11_adverbs.add("hinc");

		o4_conj = new HashSet<String>();
		o4_conj.add("ac");
		o4_conj.add("aut");
		o4_conj.add("autem");
		o4_conj.add("enim");
		o4_conj.add("et");
		o4_conj.add("etiam");
		o4_conj.add("igitur");
		o4_conj.add("immo");
		o4_conj.add("nam");
		o4_conj.add("nec");
		o4_conj.add("neque");
		o4_conj.add("quasi");
		o4_conj.add("quidem");
		o4_conj.add("quod");
		o4_conj.add("seu");
		o4_conj.add("sic");
		o4_conj.add("sicut");
		o4_conj.add("sive");
		o4_conj.add("tam");
		o4_conj.add("tamquam");
		o4_conj.add("ut");
		o4_conj.add("utrum");
		o4_conj.add("vel");
		o4_conj.add("quando");
		o4_conj.add("vero");
		
		o4_adverb = new HashSet<String>();
		o4_adverb.add("adhuc");
		//o4_adverb.add("alias");
		o4_adverb.add("deinde");
		o4_adverb.add("dumtaxat");
		o4_adverb.add("ergo");
		o4_adverb.add("idcirco");
		o4_adverb.add("ideo");
		o4_adverb.add("inde");
		o4_adverb.add("ita");
		o4_adverb.add("item");
		o4_adverb.add("nihilominus");
		o4_adverb.add("postea");
		o4_adverb.add("praeterea");
		o4_adverb.add("quomodo");
		o4_adverb.add("scilicet");
		o4_adverb.add("simul");
		o4_adverb.add("statim");
		o4_adverb.add("tamen");
		o4_adverb.add("tum");
		o4_adverb.add("propterea");
		o4_adverb.add("tunc");
		o4_adverb.add("unde");
		o4_adverb.add("usque");
		o4_adverb.add("utpote");
		
		o4_conj_deprels = new HashSet<String>();
		o4_conj_deprels.add("auxc");
		o4_conj_deprels.add("coord");
		o4_conj_deprels.add("xseg");
		o4_conj_deprels.add("apos");
		o4_conj_deprels.add("auxy");
		
		o4_adverb_deprels = new HashSet<String>();
		o4_adverb_deprels.add("auxz");
		o4_adverb_deprels.add("adv");
		o4_adverb_deprels.add("atr");
		o4_adverb_deprels.add("pred");
		o4_adverb_deprels.add("sb");
		o4_adverb_deprels.add("exd");
		o4_adverb_deprels.add("obj");
		o4_adverb_deprels.add("pnom");
		
		
		o4_conj.add("nisi");
		o4_adverb.add("quam");
		o4_adverb.add("quamvis");
		o4_adverb.add("tanto");
		
		pronoun_lemmas = new HashSet<String>();
		pronoun_lemmas.add("nullus");
		pronoun_lemmas.add("ullus");
		pronoun_lemmas.add("totus");
		pronoun_lemmas.add("unus");
		pronoun_lemmas.add("uter");
		pronoun_lemmas.add("neuter");
		pronoun_lemmas.add("alter");
		pronoun_lemmas.add("alius");
		pronoun_lemmas.add("solus");
		
	}

	Set<Pos> getCandidates(String form, String lemma, String simple_deprel, LdtMorphTag ldt_tag, ItMorphTag it_tag) {
		int index = simple_deprel.indexOf('_');
		if (index >= 0) {
			simple_deprel = simple_deprel.substring(0, index);	
		}
		
		Set<Pos> candidates = new HashSet<Pos>();

		switch (it_tag.flexional_type_) {

		case One:
			
			if (form.equals("artificis") && lemma.equals("artifex")) {
				return Collections.singleton(Pos.n);
			}
			
			if (pronoun_lemmas.contains(lemma)) {
				return Collections.singleton(Pos.p);
			}

			switch (it_tag.flexional_category_) {
			case F:
				if (it_tag.nominals_degree_ == NominalsDegree.One) {

					if (f11_nouns.contains(form)) {
						return Collections.singleton(Pos.n);
					}

					if (f11_pronouns.contains(form)) {
						return Collections.singleton(Pos.p);
					}

					if (f11_adjectives.contains(form)) {
						return Collections.singleton(Pos.a);
					}

					if (f11_numbers.contains(form)) {
						return Collections.singleton(Pos.m);
					}

					if (f11_adverbs.contains(form)) {
						ldt_tag.reset();
						return Collections.singleton(Pos.d);
					}

				}
				break;
			case G:
				if (form.equals("esse")) {
					return Collections.singleton(Pos.n);
				}
				break;
			default:
				break;
			}

			if (it_tag.participials_degree_ == ParticipialsDegree.Undef) {

				if (it_tag.case_number == CaseNumber.G) {
					ldt_tag.reset();
					
					return Collections.singleton(Pos.d);
				}

				//candidates.add(Pos.n);
				//candidates.add(Pos.a);
				//return candidates;
			}
			
			if (nouns.contains(form)) {
				return Collections.singleton(Pos.n);
			}
			
			if (pronouns.contains(form)) {
				return Collections.singleton(Pos.p);
			}
									
			break;

		case Four:
			if (it_tag.flexional_category_ == FlexionalCategory.O) {

				if (o4_conj.contains(form)) {
					return Collections.singleton(Pos.c);
				}
				
				if (o4_adverb.contains(form)) {
					return Collections.singleton(Pos.d);
				}
				
				if (o4_conj_deprels.contains(simple_deprel)) {
					return Collections.singleton(Pos.c);
				}
				
				if (o4_adverb_deprels.contains(simple_deprel)) {
					return Collections.singleton(Pos.d);
				}
				
			}
			
			//if (it_tag.flexional_category_ == FlexionalCategory.S) {
			//	return Collections.singleton(Pos.r);
			//}
			
			break;

		default:
			break;

		}

		return candidates;

	}

}
