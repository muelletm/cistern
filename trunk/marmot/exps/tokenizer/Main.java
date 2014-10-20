package tokenizer;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		WikiSelector selector = new WikiSelector("data/en/sbd_full.txt.bz2",
				"data/en/tok_full.txt.bz2", 1000); //max number of sentences we want
		selector.selectSentence();
		
		//the train() function will lead to an error, possibly related to the deprecation
		//of a function belonging to TokenizerME.
		//TODO: extend for multiple languages
		WikiTrainer trainer = new WikiTrainer();
		
		//comment in to start training, expect errors
//		try {
//			trainer.train();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

}
