// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.util;

import hmmla.hmm.Model;
import hmmla.io.Sentence;
import hmmla.io.Token;

import java.util.HashSet;
import java.util.Set;

public class Ling {

	// Code taken from Berkley Parser v1.1 SophisticatedLexicon.java
	public static String signature(String word, Model model) {
		
		int wlen = word.length();
		int numCaps = 0;
		boolean hasDigit = false;
		boolean hasDash = false;
		boolean hasLower = false;

		for (int i = 0; i < wlen; i++) {
			char ch = word.charAt(i);
			if (Character.isDigit(ch)) {
				hasDigit = true;
			} else if (ch == '-') {
				hasDash = true;
			} else if (Character.isLetter(ch)) {
				if (Character.isLowerCase(ch)) {
					hasLower = true;
				} else if (Character.isTitleCase(ch)) {
					hasLower = true;
					numCaps++;
				} else {
					numCaps++;
				}
			}
		}

		StringBuilder sb = new StringBuilder();

		char ch0 = word.charAt(0);
		String lowered = word.toLowerCase();
		if (Character.isUpperCase(ch0) || Character.isTitleCase(ch0)) {
			if (numCaps == 1) {
				sb.append("-INITC");
				if (model.isKnown(lowered)) {
					sb.append("-KNOWNLC");
				}
			} else {
				sb.append("-CAPS");
			}
		} else if (!Character.isLetter(ch0) && numCaps > 0) {
			sb.append("-CAPS");
		} else if (hasLower) { // (Character.isLowerCase(ch0)) {
			sb.append("-LC");
		}
		if (hasDigit) {
			sb.append("-NUM");
		}
		if (hasDash) {
			sb.append("-DASH");
		}

		SuffixTrie trie = model.getSuffixTrie();
		String lang = model.getProperties().getLanguage();
		
		if (trie != null) {

			if (word.length() >= 5 && !hasDash && !hasDigit && numCaps == 0) {
				String suffix = trie.getSuffix(word);
				if (suffix != null) {
					sb.append("-");
					sb.append(suffix);
				}
			}

		} else if (lang.equals("en")) {
			if (lowered.endsWith("s") && wlen >= 3) {
				char ch2 = lowered.charAt(wlen - 2);
				if (ch2 != 's' && ch2 != 'i' && ch2 != 'u') {
					sb.append("-s");
				}
			} else if (word.length() >= 5 && !hasDash
					&& !(hasDigit && numCaps > 0)) {
				if (lowered.endsWith("ed")) {
					sb.append("-ed");
				} else if (lowered.endsWith("ing")) {
					sb.append("-ing");
				} else if (lowered.endsWith("ion")) {
					sb.append("-ion");
				} else if (lowered.endsWith("er")) {
					sb.append("-er");
				} else if (lowered.endsWith("est")) {
					sb.append("-est");
				} else if (lowered.endsWith("ly")) {
					sb.append("-ly");
				} else if (lowered.endsWith("ity")) {
					sb.append("-ity");
				} else if (lowered.endsWith("y")) {
					sb.append("-y");
				} else if (lowered.endsWith("al")) {
					sb.append("-al");
				}
			}
		}

		return sb.toString();
	}

	public static SuffixTrie getSuffixes(Iterable<Sentence> sentences) {
		SuffixTrie trie = new SuffixTrie(null);

		Set<String> vocab = new HashSet<String>();

		for (Sentence sentence : sentences) {
			for (Token token : sentence) {
				String form = token.getWordForm();

				if (form.matches(".*[0-9].*")) {
					continue;
				}

				if (form.matches(".*[A-ZÄÖÜ].*")) {
					continue;
				}

				if (form.matches(".*-.*")) {
					continue;
				}

				if (form.length() < 5) {
					continue;
				}

				if (vocab.contains(form)) {
					continue;
				}

				vocab.add(form);
				trie.add(form, Math.max(form.length() - 5, form.length() / 2),
						token.getTag());
			}
		}

		trie.prune(0.5, 50);
		trie.clean();
		return trie;
	}

}
