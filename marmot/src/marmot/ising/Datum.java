package marmot.ising;

import java.util.ArrayList;

public class Datum {

	// TODO
	private int [] features;
	private String word;
	private ArrayList<String> lemma;
	private ArrayList<Integer> tag;
	
	public Datum(String word, ArrayList<String> lemma, ArrayList<Integer> tag) {
		setWord(word);
		setLemma(lemma);
		setTag(tag);
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public ArrayList<String> getLemma() {
		return lemma;
	}

	public void setLemma(ArrayList<String> lemma) {
		this.lemma = lemma;
	}

	public ArrayList<Integer> getTag() {
		return tag;
	}

	public void setTag(ArrayList<Integer> tag) {
		this.tag = tag;
	}
}
