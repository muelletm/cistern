// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.cmd;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import marmot.core.SimpleTagger;
import marmot.core.Token;
import marmot.morph.MorphModel;
import marmot.morph.MorphOptions;
import marmot.morph.Sentence;
import marmot.morph.Word;
import marmot.util.FileUtils;


public class Eyeballer {

	public static void main(String[] args) throws IOException {
		MorphOptions options = new MorphOptions();
		options.setPropertiesFromStrings(args);		
		options.dieIfPropertyIsEmpty(MorphOptions.MODEL_FILE);
		
		SimpleTagger tagger = FileUtils.loadFromFile(args[0]); 
		MorphModel model = (MorphModel) tagger.getModel();
	
		BufferedReader console = new BufferedReader(new InputStreamReader(
				System.in));
		
		while (true) {
			System.out.print("> ");

			String command_line = console.readLine();
			if (command_line == null) {
				break;
			}
			if (!command_line.equals("")) {
				
				List<Word> words = new LinkedList<Word>();
				
				for (String form : command_line.split("\\s")) {
					words.add(new Word(form));
				}

				Sentence sentence = new Sentence(words);
				
				for (Token token : sentence) {
					Word word = (Word) token;
					model.addIndexes(word, false);
				}
				
				tagger.activateCandiateBuffer(true);		
				
				List<List<String>> tags = tagger.tag(sentence);
				
				for (List<String> tag : tags) {
					System.err.println(tag);
				}
				
				tagger.printCandidateBuffer(10);
				tagger.activateCandiateBuffer(false);
				
				//System.err.println();
				
			}
		}
		
	}
	
}
