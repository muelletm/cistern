// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.mapper.latin;

import marmot.morph.mapper.MorphTag;

public class ItMorphTag implements MorphTag {

	public static boolean VERBOSE = true;
	
	public FlexionalType flexional_type_;
	public FlexionalCategory flexional_category_;
	public Mood mood_;
	public Tense tense_;
	public ParticipialsDegree participials_degree_;
	public CaseNumber case_number;
	public Composition composition_;
	public FormalVariation formal_variation_;
	public GraphicalVariation graphical_variation_;
	public GenderNumberPerson gender_number_person_;
	public NominalsDegree nominals_degree_;

	public enum FlexionalType {
		One, // Nominal (only degrees and cases) 1
		Two, // Participial 2
		Three, // Verbal 3
		Four, // Invariable 4
		Five, // Pseudo-lemma 5
		Undef, // None -
		Punc,
	}

	public enum NominalsDegree {
		One, // Positive 1
		Two, // Comparative 2
		Three, // Superlative 3
		Four, // Not stable composition 4
		Undef, // None -
	}

	public enum FlexionalCategory {
		A, // I decl A
		B, // II decl B
		C, // III decl C
		D, // IV decl D
		E, // V decl E
		F, // Regularly irregular decl F
		G, // Uninflected nominal G
		J, // I conjug J
		K, // II conjug K
		L, // III conjug L
		M, // IV conjug M
		N, // Regularly irregular conjug N
		O, // Invariable O
		S, // Prepositional (always or not) particle S
		Punc, //
		Undef, // None -
	}

	public enum Mood {
		A, // Active indicative A
		J, // Pass/Dep indicative J
		B, // Active subjunctive B
		K, // Pass/Dep subjunctive K
		C, // Active imperative C
		L, // Pass/Dep imperative L
		D, // Active participle D
		M, // Pass/Dep Participle M
		E, // Active gerund E
		N, // Passive Gerund N
		O, // Pass/Dep gerundive O
		G, // Active supine G
		P, // Pass/Dep supine P
		H, // Active infinitive H
		Q, // Pass/Dep infinitive Q
		Undef, // None -
	}

	public enum Tense {
		One, // Present 1
		Two, // Imperfect 2
		Three, // Future 3
		Four, // Perfect 4
		Five, // Plusperfect 5
		Six, // Future perfect 6
		Undef, // None -
	}

	public enum ParticipialsDegree {
		One, // Positive 1
		Two, // Comparative 2
		Three, // Superlative 3
		Undef, // None -
	}

	public enum CaseNumber {
		A, // Singular Nominative A
		J, // Plural Nominative J
		B, // Singular Genitive B
		K, // Plural Genitive K
		C, // Singular Dative C
		L, // Plural Dative L
		D, // Singular Accusative D
		M, // Plural Accusative M
		E, // Singular Vocative E
		N, // Plural Vocative N
		F, // Singular Ablative F
		O, // Plural Ablative O
		G, // Adverbial G
		H, // Casus “plurimus” H
		Undef, // None -
	}

	public enum GenderNumberPerson {
		One, // Masculine 1
		Two, // Feminine 2
		Three, // Neuter 3
		Four, // I singular 4
		Five, // II singular 5
		Six, // III singular 6
		Seven, // I plural 7
		Eight, // II plural 8
		Nine, // III plural 9
		Undef, // None -
	}

	public enum Composition {
		A, // Enclytic -ce A
		C, // Enclytic -cum C
		M, // Enclytic -met M
		N, // Enclytic -ne N
		Q, // Enclytic -que Q
		T, // Enclytic -tenus T
		V, // Enclytic -ve V
		H, // Ending homographic with enclytic H
		Z, // Composed with other form Z
		W, // As lemma W
		Undef, // None -
	}

	public enum FormalVariation {
		A, // I variation of wordform A
		B, // II variation of wordform B
		C, // III variation of wordform C
		X, // Author mistake, or bad reading? X
		Undef, // None -
	}

	enum GraphicalVariation {
		One, // Baseform 1
		Two, // Graphical variations of “1”
		Three, //
		Undef, Four, // None -
	}

	public ItMorphTag() {
		flexional_type_ = FlexionalType.Undef;
		nominals_degree_ = NominalsDegree.Undef;
		flexional_category_ = FlexionalCategory.Undef;
		mood_ = Mood.Undef;
		tense_ = Tense.Undef;
		participials_degree_ = ParticipialsDegree.Undef;
		case_number = CaseNumber.Undef;
		composition_ = Composition.Undef;
		formal_variation_ = FormalVariation.Undef;
		gender_number_person_ = GenderNumberPerson.Undef;
		graphical_variation_ = GraphicalVariation.Undef;
	}

	public static MorphTag parseString(String tag_string) {
		ItMorphTag tag = new ItMorphTag();

		String[] fields = tag_string.split("\\s+");
		assert fields.length == 3;

		tag.readFlexionalType(fields[0]);
		tag.readFlexionalCategory(fields[1]);
		tag.readFeatures(fields[2]);

		return tag;
	}

	private void readFeatures(String string) {
		String[] features = string.split("\\|");
		for (String feature : features) {
			readFeature(feature);
		}
	}

	private void readFeature(String feature) {

		String lower_feature = feature.toLowerCase();

		if (lower_feature.startsWith("cas")) {
			case_number = caseFeatureToString(feature.substring(3));
		} else if (lower_feature.startsWith("gen")) {
			gender_number_person_ = genderFeatureToString(feature.substring(3));
		} else if (lower_feature.startsWith("vgr")) {
			graphical_variation_ = graphicalVariationToString(feature
					.substring(3));
		} else if (lower_feature.equals("_")) {
			// Ignore
		} else if (lower_feature.startsWith("grn")) {
			nominals_degree_ = nominalsDegreeToString(feature.substring(3));
		} else if (lower_feature.startsWith("com")) {
			composition_ = compositionToString(feature.substring(3));
		} else if (lower_feature.startsWith("var")) {
			formal_variation_ = formalVariationToString(feature.substring(3));
		} else if (lower_feature.startsWith("tem")) {
			tense_ = tenseToString(feature.substring(3));
		} else if (lower_feature.startsWith("mod")) {
			mood_ = moodToString(feature.substring(3));
		} else if (lower_feature.startsWith("grp")) {
			participials_degree_ = participialsDegreeToString(feature
					.substring(3));
		} else {
			throw new RuntimeException("Unknown feature: " + feature);
		}

	}

	private ParticipialsDegree participialsDegreeToString(String string) {
		if (string.equals("1")) {
			return ParticipialsDegree.One;
		} else if (string.equals("2")) {
			return ParticipialsDegree.Two;
		} else if (string.equals("3")) {
			return ParticipialsDegree.Three;
		} else if (string.equals(".")) {
			return ParticipialsDegree.Undef;
		}

		if (VERBOSE) System.err.println("Unknown participals degree value: " + string);
		return ParticipialsDegree.Undef;
	}

	private Mood moodToString(String string) {
		try {
			return Mood.valueOf(string.toUpperCase());
		} catch (RuntimeException e) {
			if (string.equalsIgnoreCase(".")) {
				return Mood.Undef;
			}
			if (VERBOSE) System.err.println("Unknown mood value: " + string);
			return Mood.Undef;
		}
	}

	private Tense tenseToString(String string) {
		if (string.equals("1")) {
			return Tense.One;
		} else if (string.equals("2")) {
			return Tense.Two;
		} else if (string.equals("3")) {
			return Tense.Three;
		} else if (string.equals("4")) {
			return Tense.Four;
		} else if (string.equals("5")) {
			return Tense.Five;
		} else if (string.equals("6")) {
			return Tense.Six;
		} else if (string.equals(".")) {
			return Tense.Undef;
		}

		if (VERBOSE) System.err.println("Unknown tense value: " + string);
		return Tense.Undef;
	}

	private FormalVariation formalVariationToString(String string) {
		try {
			return FormalVariation.valueOf(string.toUpperCase());
		} catch (RuntimeException e) {
			if (string.equalsIgnoreCase(".")) {
				return FormalVariation.Undef;
			}
			if (VERBOSE)  System.err.println("Unknown formal variation value: " + string);
			return FormalVariation.Undef;
		}
	}

	private Composition compositionToString(String string) {
		try {
			return Composition.valueOf(string.toUpperCase());
		} catch (RuntimeException e) {
			if (string.equalsIgnoreCase(".")) {
				return Composition.Undef;
			}
			if (VERBOSE) System.err.println("Unknown composition value: " + string);
			return Composition.Undef;
		}
	}

	private NominalsDegree nominalsDegreeToString(String string) {
		if (string.equals("1")) {
			return NominalsDegree.One;
		} else if (string.equals("2")) {
			return NominalsDegree.Two;
		} else if (string.equals("3")) {
			return NominalsDegree.Three;
		} else if (string.equals("4")) {
			return NominalsDegree.Four;
		} else if (string.equals(".")) {
			return NominalsDegree.Undef;
		}

		if (VERBOSE) System.err.println("Unknown nominals degree: " + string);
		return NominalsDegree.Undef;
	}

	private GraphicalVariation graphicalVariationToString(String string) {
		if (string.equals("1")) {
			return GraphicalVariation.One;
		} else if (string.equals("2")) {
			return GraphicalVariation.Two;
		} else if (string.equals("3")) {
			return GraphicalVariation.Three;
		} else if (string.equals("4")) {
			return GraphicalVariation.Four;
		} else if (string.equals(".")) {
			return GraphicalVariation.Undef;
		}

		if (VERBOSE) System.err.println("Unknown graphical variation value: " + string);
		return GraphicalVariation.Undef;

	}

	private GenderNumberPerson genderFeatureToString(String string) {
		if (string.equals("1")) {
			return GenderNumberPerson.One;
		} else if (string.equals("2")) {
			return GenderNumberPerson.Two;
		} else if (string.equals("3")) {
			return GenderNumberPerson.Three;
		} else if (string.equals("4")) {
			return GenderNumberPerson.Four;
		} else if (string.equals("5")) {
			return GenderNumberPerson.Five;
		} else if (string.equals("6")) {
			return GenderNumberPerson.Six;
		} else if (string.equals("7")) {
			return GenderNumberPerson.Seven;
		} else if (string.equals("8")) {
			return GenderNumberPerson.Eight;
		} else if (string.equals("9")) {
			return GenderNumberPerson.Nine;
		} else if (string.equals(".")) {
			return GenderNumberPerson.Undef;
		}

		if (VERBOSE) System.err.println("Unknown gender value: " + string);
		return GenderNumberPerson.Undef;
	}

	private CaseNumber caseFeatureToString(String case_feature) {
		try {
			return CaseNumber.valueOf(case_feature.toUpperCase());
		} catch (RuntimeException e) {
			if (case_feature.equalsIgnoreCase(".")) {
				return CaseNumber.Undef;
			}
			if (VERBOSE) System.err.println("Unknown case value: " + case_feature);
			return CaseNumber.Undef;
		}
	}

	private void readFlexionalCategory(String string) {
		if (string.equals("-")) {
			assert flexional_type_ == FlexionalType.Undef;
			flexional_category_ = FlexionalCategory.Undef;
		} else if (string.equalsIgnoreCase("Punc")) {
			assert flexional_type_ == FlexionalType.Punc;
			flexional_category_ = FlexionalCategory.Punc;
		} else if (string.length() == 1) {
			FlexionalType type = stringToFlexionalType(string);
			assert type == flexional_type_;
			flexional_category_ = FlexionalCategory.Undef;
		} else if (string.length() == 2) {
			assert stringToFlexionalType(Character.toString(string.charAt(1))) == flexional_type_;
			flexional_category_ = charToFlexionalCategory(string.charAt(0));
		} else {
			if (VERBOSE) System.err.println("Unknown flexional category value: " + string);
			flexional_category_ = FlexionalCategory.Undef;
		}
	}

	private FlexionalCategory charToFlexionalCategory(char c) {
		try {
			return FlexionalCategory.valueOf(Character.toString(c)
					.toUpperCase());
		} catch (RuntimeException e) {
			throw new RuntimeException("Unkown value: " + c);
		}
	}

	public static FlexionalType stringToFlexionalType(String string) {
		FlexionalType flexional_type;
		if (string.equals("1")) {
			flexional_type = FlexionalType.One;
		} else if (string.equals("2")) {
			flexional_type = FlexionalType.Two;
		} else if (string.equals("3")) {
			flexional_type = FlexionalType.Three;
		} else if (string.equals("4")) {
			flexional_type = FlexionalType.Four;
		} else if (string.equals("5")) {
			flexional_type = FlexionalType.Five;
		} else if (string.equals("-")) {
			flexional_type = FlexionalType.Undef;
		} else if (string.equalsIgnoreCase("Punc")) {
			flexional_type = FlexionalType.Punc;
		} else {
			if (VERBOSE) System.err.println("Unkown flexional type value: " + string);
			flexional_type = FlexionalType.Undef;
		}
		return flexional_type;
	}

	private void readFlexionalType(String string) {
		flexional_type_ = stringToFlexionalType(string);
	}

	@Override
	public String toHumanMorphString() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toPosString() {
		throw new UnsupportedOperationException();
	}

}
