package marmot.ising;

public class Datum {

	private int [] features;
	private String word;
	private String lemma;
	private String tag;
	
	public Datum(String word, String lemma, String tag) {
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

	public String getLemma() {
		return lemma;
	}

	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
}
