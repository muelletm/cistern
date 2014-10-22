package marmot.tokenize.preprocess;

import java.io.IOException;

import marmot.tokenize.Tokenizer;
import marmot.tokenize.openlp.OpenNlpTokenizerTrainer;

public class Main {

	public static void main(String[] args) throws IOException {
		WikiSelector selector = new WikiSelector("data/en/sbd_full.txt.bz2",
				"data/en/tok_full.txt.bz2", 1000); //max number of sentences we want
		selector.selectSentence();

		
		OpenNlpTokenizerTrainer trainer = new OpenNlpTokenizerTrainer();
		Tokenizer tokenizer = trainer.train("/mounts/data/proj/marmot/tokenizer/data/en/test.txt");
		tokenizer.saveToFile("/mounts/data/proj/marmot/tokenizer/data/en/test.txt.tok");
		
		System.out.println(tokenizer.tokenize("Shouldn't this work, now?"));
		
		
		//the train() function will lead to an error, possibly related to the deprecation
		//of a function belonging to TokenizerME.
		//TODO: extend for multiple languages
		//WikiTrainer trainer = new WikiTrainer();
		
		//comment in to start training, expect errors
//		try {
//			trainer.train();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

}
