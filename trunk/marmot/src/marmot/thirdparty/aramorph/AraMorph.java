/*
Copyright (C) 2003-2004 Pierrick Brihaye
pierrick.brihaye@wanadoo.fr
 
Original Perl code :
Portions (c) 2002 QAMUS LLC (www.qamus.org), 
(c) 2002 Trustees of the University of Pennsylvania 
 
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the
Free Software Foundation, Inc.
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
or connect to:
http://www.fsf.org/copyleft/gpl.html
 */

package marmot.thirdparty.aramorph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A java port of Buckwalter Arabic Morphological Analyzer Version 1.0. Original
 * Perl distribution avalaible from : <a href=
 * "http://www.ldc.upenn.edu/Catalog/CatalogEntry.jsp?catalogId=LDC2002L49">LDC
 * Catalog</a>
 * 
 * @author Pierrick Brihaye, 2003
 */
public class AraMorph {

	/**
	 * The dictionary handler. TODO : use more generic interface.
	 */
	private static DictionaryHandler dict = null;

	/**
	 * The solutions handler. TODO : use more generic interface.
	 */

	/** Constructs an Arabic morphological analyzer that will output nothing. */
	public AraMorph() {
		dict = new DictionaryHandler();
	}

	public char romanizeChar(char c) {
		
		switch (c) {
		
		case '\u0621':
			return '\''; // \u0621 : ARABIC LETTER HAMZA
		case '\u0622':
			return '|'; // \u0622 : ARABIC LETTER ALEF WITH MADDA ABOVE
		case '\u0623':
			return '>'; // \u0623 : ARABIC LETTER ALEF WITH HAMZA ABOVE
		case '\u0624':
			return '&'; // \u0624 : ARABIC LETTER WAW WITH HAMZA ABOVE
		case '\u0625':
			return '<'; // \u0625 : ARABIC LETTER 
		case '\u0626':
			return '}'; // \u0626 : ARABIC LETTER YEH WITH HAMZA ABOVE
		case '\u0627':
			return 'A'; // \u0627 : ARABIC LETTER ALEF
		case '\u0628':
			return 'b'; // \u0628 : ARABIC LETTER BEH
		case '\u0629':
			return 'p'; // \u0629 : ARABIC LETTER TEH MARBUTA
		case '\u062A':
			return 't'; // \u062A : ARABIC LETTER TEH
		case '\u062B':
			return 'v'; // \u062B : ARABIC LETTER THEH
		case '\u062C':
			return 'j'; // \u062C : ARABIC LETTER JEEM
		case '\u062D':
			return 'H'; // \u062D : ARABIC LETTER HAH
		case '\u062E':
			return 'x'; // \u062E : ARABIC LETTER KHAH
		case '\u062F':
			return 'd'; // \u062F : ARABIC LETTER DAL
		case '\u0630':
			return '*'; // \u0630 : ARABIC LETTER THAL
		case '\u0631':
			return 'r'; // \u0631 : ARABIC LETTER REH
		case '\u0632':
			return 'z'; // \u0632 : ARABIC LETTER ZAIN
		case '\u0633':
			return 's'; // \u0633 : ARABIC LETTER SEEN
		case '\u0634':
			return '$'; // \u0634 : ARABIC LETTER SHEEN
		case '\u0635':
			return 'S'; // \u0635 : ARABIC LETTER SAD
		case '\u0636':
			return 'D'; // \u0636 : ARABIC LETTER DAD
		case '\u0637':
			return 'T'; // \u0637 : ARABIC LETTER TAH
		case '\u0638':
			return 'Z'; // \u0638 : ARABIC LETTER ZAH
		case '\u0639':
			return 'E'; // \u0639 : ARABIC LETTER AIN
		case '\u063A':
			return 'g'; // \u063A : ARABIC LETTER GHAIN
		case '\u0640':
			return '_'; // \u0640 : ARABIC TATWEEL
		case '\u0641':
			return 'f'; // \u0641 : ARABIC LETTER FEH
		case '\u0642':
			return 'q'; // \u0642 : ARABIC LETTER QAF
		case '\u0643':
			return 'k'; // \u0643 : ARABIC LETTER KAF
		case '\u0644':
			return 'l'; // \u0644 : ARABIC LETTER LAM
		case '\u0645':
			return 'm'; // \u0645 : ARABIC LETTER MEEM
		case '\u0646':
			return 'n'; // \u0646 : ARABIC LETTER NOON
		case '\u0647':
			return 'h'; // \u0647 : ARABIC LETTER HEH
		case '\u0648':
			return 'w'; // \u0648 : ARABIC LETTER WAW
		case '\u0649':
			return 'Y'; // \u0649 : ARABIC LETTER ALEF MAKSURA
		case '\u064A':
			return 'y'; // \u064A : ARABIC LETTER YEH
		case '\u064B':
			return 'F'; // \u064B : ARABIC FATHATAN 
		case '\u064C':
			return 'N'; // \u064C : ARABIC DAMMATAN
		case '\u064D':
			return 'K'; // \u064D : ARABIC KASRATAN
		case '\u064E':
			return 'a'; // \u064E : ARABIC FATHA
		case '\u064F':
			return 'u'; // \u064F : ARABIC DAMMA
		case '\u0650':
			return 'i'; // \u0650 : ARABIC KASRA
		case '\u0651':
			return '~'; // \u0651 : ARABIC SHADDA
		case '\u0652':
			return 'o'; // \u0652 : ARABIC SUKUN
		case '\u0670':
			return '`'; // \u0670 : ARABIC LETTER SUPERSCRIPT ALEF
		case '\u0671':
			return '{'; // \u0671 : ARABIC LETTER ALEF WASLA
		case '\u067E':
			return 'P'; // \u067E : ARABIC LETTER PEH
		case '\u0686':
			return 'J'; // \u0686 : ARABIC LETTER TCHEH
		case '\u06A4':
			return 'V'; // \u06A4 : ARABIC LETTER VEH
		case '\u06AF':
			return 'G'; // \u06AF : ARABIC LETTER GAF
		case '\u0698':
			return 'R'; // \u0698 : ARABIC LETTER JEH (no more in Buckwalter system)
		// Not in Buckwalter system \u0679 : ARABIC LETTER TTEH
		// Not in Buckwalter system \u0688 : ARABIC LETTER DDAL
		// Not in Buckwalter system \u06A9 : ARABIC LETTER KEHEH
		// Not in Buckwalter system \u0691 : ARABIC LETTER RREH
		// Not in Buckwalter system \u06BA : ARABIC LETTER NOON GHUNNA
		// Not in Buckwalter system \u06BE : ARABIC LETTER HEH DOACHASHMEE
		// Not in Buckwalter system \u06C1 : ARABIC LETTER HEH GOAL
		// Not in Buckwalter system \u06D2 : ARABIC LETTER YEH BARREE
		case '\u060C':
			return ','; // \u060C : ARABIC COMMA
		case '\u061B':
			return ';'; // \u061B : ARABIC SEMICOLON
		case '\u061F':
			return '?'; // \u061F : ARABIC QUESTION MARK
		}	
				
		return c;
	}

	/**
	 * Returns a word in the Buckwalter transliteration system from a word in
	 * arabic. Vowels and diacritics are <strong>discarded</strong>.
	 * 
	 * @param word
	 *            The word in arabic
	 * @return The romanized word
	 */
	public String romanizeWord(String word) {
		StringBuilder sb = new StringBuilder(word.length());
		
		for (int index = 0; index < word.length(); index++) {
			
			char c = word.charAt(index);
			
			char new_c = romanizeChar(c);
			
			if (c == new_c) {
				// Delete
				
				System.err.println(word);
				
				continue;
			}
			
			switch (new_c) {
					// Not significant for morphological analysis (ARABIC TATWEEL)
				case '_':					
					// Not suitable for morphological analysis : remove all
					// vowels/diacritics, i.e. undo the job !
				case 'F':
				case 'N':
				case 'K':
				case 'a':
				case 'u':
				case 'i':
				case '~':
				case 'o':
					// TODO : how to handle ARABIC LETTER SUPERSCRIPT ALEF and ARABIC LETTER
					// ALEF WASLA ? 
					// Strip them for now.
				case '`':
				case '\\':
				case '{':
					// Delete Character
					continue;
			}
			
			sb.append(new_c);
			
		}
		return sb.toString();
	}

	static private final Pattern arabic_word_pattern_ = Pattern.compile("([\u067E\u0686\u0698\u06AF\u0621-\u063A\u0641-\u0652])+");
	
	
	/**
	 * Analyzes a token. For performance issues, the analyzer keeps track of the
	 * results.
	 * 
	 * @return Whether or not the word has a solution in arabic
	 * @param outputBuckwalter
	 *            Whether or not the Buckwalter transliteration system should be
	 *            used. If not, outputs will be in arabic wherever possible
	 * @param token
	 *            The token to be analyzed
	 */
	public Set<Solution> analyzeToken(String token) {
		if (!arabic_word_pattern_.matcher(token).matches()) {
			return null;
		}

		String translitered = romanizeWord(token);
		Set<Solution> solutions = null;

		solutions = feedWordSolutions(translitered);
		if (solutions != null) {
			return solutions;
		}

//		Set<String> alternative_spellings = feedAlternativeSpellings(translitered);
//		solutions = new HashSet<Solution>();
//		if (alternative_spellings != null) {
//
//			for (String alternative : alternative_spellings) {
//				// feed solutions with alternative spellings' ones
//
//				Set<Solution> alternative_solutions = feedWordSolutions(alternative);
//
//				if (alternative_solutions != null)
//					solutions.addAll(alternative_solutions);
//			}
//
//		}
//		if (solutions.isEmpty()) {
//			return null;
//		}

		return solutions;
	}

	/**
	 * Splits a word in prefix + stem + suffix combinations.
	 * 
	 * @return The list of combinations
	 * @param translitered
	 *            The word. It is assumed that {@link #romanizeWord(String word)
	 *            romanizeWord} has been called before
	 */
	private Set<SegmentedWord> segmentWord(String translitered) {
		Set<SegmentedWord> segmented = new HashSet<SegmentedWord>();
		int prefix_len = 0;
		int suffix_len = 0;
		// TODO : why 4 ? The info could certainly be grabbed from
		// dictionnaries...
		while ((prefix_len) <= 4 && (prefix_len <= translitered.length())) {
			String prefix = translitered.substring(0, prefix_len);
			int stem_len = (translitered.length() - prefix_len);
			suffix_len = 0;
			// TODO : why 6 ? The info could certainly be grabbed from
			// dictionnaries...
			while ((stem_len >= 1) && (suffix_len <= 6)) {
				String stem = translitered.substring(prefix_len, prefix_len
						+ stem_len);
				String suffix = translitered.substring(prefix_len + stem_len,
						prefix_len + stem_len + suffix_len);
				segmented.add(new SegmentedWord(prefix, stem, suffix));
				stem_len--;
				suffix_len++;
			}
			prefix_len++;
		}
		return segmented;
	}

	/**
	 * Feed an internal list of solutions for the given word
	 * 
	 * @param translitered
	 *            The word. It is assumed that {@link #romanizeWord(String word)
	 *            romanizeWord} has been called before
	 * @return Whether or not there are solutions for this word
	 */
	private Set<Solution> feedWordSolutions(String translitered) {
		Set<Solution> wordSolutions = new HashSet<Solution>();
		
		// get a list of valid segmentations
		Set<SegmentedWord> segments = segmentWord(translitered);
		// Brute force algorithm

		for (SegmentedWord segmentedWord : segments) {

			Collection<DictionaryEntry> prefixes = dict
					.getPrefixIterator(segmentedWord.getPrefix());

			if (prefixes == null) {
				continue;
			}

			Collection<DictionaryEntry> stems = dict
					.getStemIterator(segmentedWord.getStem());

			if (stems == null) {
				continue;
			}

			Collection<DictionaryEntry> suffixes = dict
					.getSuffixIterator(segmentedWord.getSuffix());

			if (suffixes == null) {
				continue;
			}

			for (DictionaryEntry prefix : prefixes) {

				for (DictionaryEntry stem : stems) {

					// Prefix/Stem compatiblity
					if (dict.hasAB(prefix.getMorphology(), stem.getMorphology())) {

						for (DictionaryEntry suffix : suffixes) {

							// Prefix/Suffix compatiblity
							if (dict.hasAC(prefix.getMorphology(),
									suffix.getMorphology())) {
								// Stem/Suffix compatibility
								if (dict.hasBC(stem.getMorphology(),
										suffix.getMorphology())) {
									// All tests passed : it is a solution
									wordSolutions.add(new Solution(prefix, stem, suffix));
								}
							}
						}
					}
				}
			}
		}

		return wordSolutions;
	}

	/**
	 * Feed an internal list of alternative spellings for the given word
	 * 
	 * @param translitered
	 *            The word. It is assumed that {@link #romanizeWord(String word)
	 *            romanizeWord} has been called before
	 * @return Whether or not there are alternative spellings for this word
	 */
//	private Set<String> feedAlternativeSpellings(String translitered) {
//		// No need to reprocess
//		HashSet<String> wordAlternativeSpellings = new HashSet<String>();
//		String temp = translitered;
//		String temp2;
//		// final 'alif maqSuura + hamza-on-the-line
//		if (temp.matches(".*" + "Y'$")) { // Y_w'_Y'
//			// -> yaa' + hamza-on-the-line
//			temp = temp.replaceAll("Y", "y"); // y_w'_y'
//
//			wordAlternativeSpellings.add(temp); // y_w'_y' -- pushed
//			// medial waaw + hamza-on-the-line -> hamza-on-waaw
//			temp2 = temp.replaceFirst("w'", "&"); // y_&__y'
//			if (!temp.equals(temp2)) {
//				temp = temp2; // y_&__y'
//				wordAlternativeSpellings.add(temp); // y_&__y' -- pushed
//			}
//			temp = translitered; // Y_w'_Y'
//			// -> yaa' + hamza-on-the-line
//			temp = temp.replaceAll("Y", "y"); // y_w'_y'
//			// final yaa' + hamza-on-the-line -> hamza-on-yaa'
//			temp = temp.replaceFirst("y'$", "}"); // y_w'_}
//
//			wordAlternativeSpellings.add(temp); // y_w'_} -- pushed
//			// medial waaw + hamza-on-the-line -> hamza-on-waaw
//			temp2 = temp.replaceFirst("w'", "&"); // y_&__}
//			if (!temp.equals(temp2)) {
//				temp = temp2; // y_&__}
//				wordAlternativeSpellings.add(temp); // y_&__} -- pushed
//			}
//		}
//		// final yaa' + hamza-on-the-line
//		else if (temp.matches(".*" + "y'$")) { // Y_w'_y'
//			// 'alif maqSuura -> yaa'
//			temp2 = temp.replaceAll("Y", "y"); // y_w'_y'
//			if (!temp.equals(temp2)) {
//				temp = temp2; // y_w'_y'
//				wordAlternativeSpellings.add(temp); // y_w'_y' -- pushed
//			}
//			// medial waaw + hamza-on-the-line -> hamza-on-waaw
//			temp2 = temp.replaceFirst("w'", "&"); // y_&__y'
//			if (!temp.equals(temp2)) {
//				temp = temp2; // y_&__y'
//				wordAlternativeSpellings.add(temp); // y_&__y' -- pushed
//			}
//			temp = translitered; // Y_w'_y'
//			// 'alif maqSuura -> yaa'
//			temp = temp.replaceAll("Y", "y"); // y_w'_y'
//			// final yaa' + hamza-on-the-line -> 'alif maqSuura
//			temp = temp.replaceFirst("y'$", "}"); // y_w'_}
//			wordAlternativeSpellings.add(temp); // y_w'_} -- pushed
//			// medial waaw + hamza-on-the-line -> hamza-on-waaw
//			temp2 = temp.replaceFirst("w'", "&"); // y_&__}
//			if (!temp.equals(temp2)) {
//				temp = temp2; // y_&__}
//				wordAlternativeSpellings.add(temp); // y_&__} -- pushed
//			}
//		}
//		// final yaa'
//		else if (temp.matches(".*" + "y$")) { // Y_w'_y
//			// 'alif maqSuura -> yaa'
//			temp = temp.replaceAll("Y", "y"); // y_w'_y
//			// medial waaw + hamza-on-the-line -> hamza-on-waaw
//			temp2 = temp.replaceFirst("w'", "&"); // y_&__y
//			if (!temp.equals(temp2)) {
//				temp = temp2; // y_&__y
//				wordAlternativeSpellings.add(temp); // y_&__y -- pushed
//			}
//			temp = translitered; // Y_w'_y
//			// 'alif maqSuura -> yaa'
//			temp = temp.replaceAll("Y", "y"); // y_w'_y
//			// final yaa' -> 'alif maqSuura
//			temp = temp.replaceAll("y$", "Y"); // y_w'_Y
//			wordAlternativeSpellings.add(temp); // y_w'_Y -- pushed
//			// medial waaw + hamza-on-the-line -> hamza-on-waaw
//			temp2 = temp.replaceFirst("w'", "&"); // y_&__Y
//			if (!temp.equals(temp2)) {
//				temp = temp2; // y_&__Y
//				wordAlternativeSpellings.add(temp); // y_&__Y -- pushed
//			}
//		}
//		// final haa'
//		else if (temp.matches(".*" + "h$")) { // Y_w'_h
//			// 'alif maqSuura -> yaa'
//			temp2 = temp.replaceAll("Y", "y"); // y_w'_h
//			if (!temp.equals(temp2)) {
//				temp = temp2; // y_w'_h
//				wordAlternativeSpellings.add(temp); // y_w'_h -- pushed
//			}
//			// medial waaw + hamza-on-the-line -> hamza-on-waaw
//			temp2 = temp.replaceFirst("w'", "&"); // y_&__h
//			if (!temp.equals(temp2)) {
//				temp = temp2; // y_&__h
//				wordAlternativeSpellings.add(temp); // y_&__h -- pushed
//			}
//			// final haa' -> taa' marbuuTa
//			temp = temp.replaceFirst("h$", "p"); // y_w'_p
//			wordAlternativeSpellings.add(temp); // y_w'_p -- pushed
//		}
//		// final taa' marbuuTa
//		else if (temp.matches(".*" + "p$")) { // Y_w'_p
//			// 'alif maqSuura -> yaa'
//			temp2 = temp.replaceAll("Y", "y"); // y_w'_p
//			if (!temp.equals(temp2)) {
//				temp = temp2; // y_w'_p
//				wordAlternativeSpellings.add(temp); // y_w'_p -- pushed
//			}
//			// medial waaw + hamza-on-the-line -> hamza-on-waaw
//			temp2 = temp.replaceFirst("w'", "&"); // y_&__p
//			if (!temp.equals(temp2)) {
//				temp = temp2; // y_&__p
//				wordAlternativeSpellings.add(temp); // y_&__p -- pushed
//			}
//			// final taa' marbuuTa -> haa'
//			temp = temp.replaceFirst("p$", "h"); // y_w'_h
//			wordAlternativeSpellings.add(temp); // //y_w'_h -- pushed
//		}
//		// Substitutions before matching
//		else {
//			// final 'alif maqSuura -> yaa'
//			temp2 = temp.replaceFirst("Y$", "y"); // Y_w'_y
//			if (!temp.equals(temp2)) {
//				temp = temp2; // Y_w'_y
//				// 'alif maqSuura -> yaa'
//				temp = temp.replaceAll("Y", "y"); // y_w'_y
//				wordAlternativeSpellings.add(temp); // y_w'_y -- pushed
//				// medial waaw + hamza-on-the-line -> hamza-on-waaw
//				temp2 = temp.replaceFirst("w'", "&"); // y_&__y
//				if (!temp.equals(temp2)) {
//					temp = temp2; // y_&__y
//					wordAlternativeSpellings.add(temp); // y_&__y -- pushed
//				}
//			} else {
//				// 'alif maqSuura -> yaa'
//				temp2 = temp.replaceAll("Y", "y"); // y_w'__
//				if (!temp.equals(temp2)) {
//					temp = temp2; // y_w'__
//					wordAlternativeSpellings.add(temp); // y_w'__ -- pushed
//					// medial waaw + hamza-on-the-line -> hamza-on-waaw
//					temp2 = temp.replaceFirst("w'", "&"); // y_&___
//					if (!temp.equals(temp2)) {
//						temp = temp2; // y_&___
//						wordAlternativeSpellings.add(temp); // y_&___ -- pushed
//					}
//				} else {
//					// medial waaw + hamza-on-the-line -> hamza-on-waaw
//					temp2 = temp.replaceFirst("w'", "&"); // y_&___
//					if (!temp.equals(temp2)) {
//						temp = temp2; // y_&___
//						wordAlternativeSpellings.add(temp); // y_&___ -- pushed
//					} else {
//					} // nothing
//				}
//			}
//		}
//
//		if (wordAlternativeSpellings.isEmpty()) {
//			return null;
//		}
//
//		return wordAlternativeSpellings;
//	}

	// Inner class
	private class SegmentedWord {

		private String prefix;
		private String stem;
		private String suffix;

		protected SegmentedWord(String prefix, String stem, String suffix) {
			this.prefix = prefix;
			this.stem = stem;
			this.suffix = suffix;
		}

		protected String getPrefix() {
			return this.prefix;
		}

		protected String getStem() {
			return this.stem;
		}

		protected String getSuffix() {
			return this.suffix;
		}
	}
	
}
