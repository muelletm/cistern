package marmot.lemma.cmd;

import marmot.lemma.Model;

public class Trainer {
	
	public static void main(String[] args) {
		
		String train_file = args[0];
		String test_file = args[1];
		
		Trainer trainer = new Trainer();
	
		trainer.trainAndTest(train_file, test_file);
		

		
		
	}

	public void trainAndTest(String train_file, String test_file) {
		Model model = new Model();
		
		model.init(train_file, test_file);
	}	
	
}
