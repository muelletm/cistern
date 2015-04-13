package marmot.lemma;

import java.util.List;

import marmot.lemma.toutanova.Aligner;
import marmot.util.SymbolTable;

public class LemmaCandidate {

	private double score_;
	private int[] feature_indexes_;
	private int[] lemma_chars_;
	private List<Integer> alignment_;

	public LemmaCandidate() {
	}

	public void addFeature(Object generator, Object feature) {
	}

	public void setScore(double score) {
		score_ = score;
	}

	public double getScore() {
		return score_;
	}

	public int[] getFeatureIndexes() {
		return feature_indexes_;
	}

	public void setFeatureIndexes(int[] feature_indexes) {
		feature_indexes_ = feature_indexes;
	}
	
	public int[] getLemmaChars(SymbolTable<Character> char_table_, String lemma, boolean insert) {
		if (lemma_chars_ == null) {
			lemma_chars_ = new int[lemma.length()];
			for (int i = 0; i < lemma.length(); i++) {
				int c = char_table_.toIndex(lemma.charAt(i), -1, insert);
				lemma_chars_[i] = c;
			}
		}
		return lemma_chars_;
	}

	public List<Integer> getAlignment(Aligner aligner, String form, String lemma) {
		if (alignment_ == null) {
			alignment_ = aligner.align(form, lemma);
		}
		return alignment_;
	}
	
}
