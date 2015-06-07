package marmot.ising;

public class Datum {

	private int [] features;
	private String word;
	private String lemma;
	
	public Datum(String word, String lemma, int[] features) {
		setWord(word);
		setLemma(lemma);
		setFeatures(features);
	}

	public int [] getFeatures() {
		return features;
	}

	public void setFeatures(int [] features) {
		this.features = features;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getLemma() {
		return lemma;
	}

	public void setLemma(String lemma) {
		this.lemma = lemma;
	}
}
