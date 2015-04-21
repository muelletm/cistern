package marmot.util;

import marmot.util.StringUtils.Mode;
import marmot.util.StringUtils.Shape;

public class AspellLexicon implements Lexicon {

	private static final long serialVersionUID = 1L;
	private Aspell aspell_;
	private Mode mode_;

	public AspellLexicon(Mode mode, String aspell_path, String aspell_lang) {
		aspell_ = new Aspell(aspell_path, aspell_lang, "utf-8");
		mode_ = mode;
	}
	
	@Override
	public int[] getCount(String lemma) {
		lemma = StringUtils.normalize(lemma, mode_);
		
		boolean lower = false;
		boolean first_cap = false;
		boolean all_cap = false;
		boolean no_letter = false;
		
		if (StringUtils.containsLetter(lemma)) {
			lower = aspell_.isCorrect(lemma);
			first_cap = aspell_.isCorrect(StringUtils.capitalize(lemma));
			all_cap = aspell_.isCorrect(lemma.toUpperCase());
		} else {
			no_letter = aspell_.isCorrect(lemma);
		}
		
		if (lower || first_cap || all_cap || no_letter) {
			int[] counts = new int[Lexicon.ARRAY_LENGTH];
			if (lower) {
				counts[Shape.Lower.ordinal()] = 1;
			}
			if (first_cap) {
				counts[Shape.FirstCap.ordinal()] = 1;
			}
			if (all_cap) {
				counts[Shape.AllCap.ordinal()] = 1;
			}
			if (no_letter) {
				counts[Shape.NoLetter.ordinal()] = 1;
			}
			counts[Lexicon.ARRAY_LENGTH - 1] = 1;
			return counts;
		}
		
		return null;
	}

}
