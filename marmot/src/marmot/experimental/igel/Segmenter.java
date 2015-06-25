package marmot.experimental.igel;

public class Segmenter {

	public Segmenter() {
		// toy data
		for (int iter = 0; iter < 1; ++iter) {
			Word word = new Word("ab");
			FactorGraph fg = new FactorGraph(word);
			fg.inferenceBruteForce();
		}
	}
	
	
	public static void main(String[] args) {
		Segmenter segmenter = new Segmenter();
	}
}
