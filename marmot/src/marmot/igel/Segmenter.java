package marmot.igel;

public class Segmenter {

	public Segmenter() {
		// toy data
		for (int iter = 0; iter < 1; ++iter) {
			Word word = new Word("aufschreiben");
			FactorGraph fg = new FactorGraph(word);
		}
	}
	
	
	public static void main(String[] args) {
		Segmenter segmenter = new Segmenter();
	}
}
