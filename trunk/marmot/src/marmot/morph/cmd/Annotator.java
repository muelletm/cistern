// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.cmd;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import marmot.core.Sequence;
import marmot.core.Tagger;
import marmot.morph.MorphDictionary;
import marmot.morph.MorphOptions;
import marmot.morph.MorphWeightVector;
import marmot.morph.Sentence;
import marmot.morph.Word;
import marmot.morph.io.SentenceReader;
import marmot.util.FileUtils;

public class Annotator {
	
	private static final char SEPERATOR_ = '\t';
	private static final String EMPTY_ = "_";
	
	public static void main(String[] args) {
		MorphOptions options = new MorphOptions();
		options.setPropertiesFromStrings(args);
		
		options.dieIfPropertyIsEmpty(MorphOptions.MODEL_FILE);
		options.dieIfPropertyIsEmpty(MorphOptions.PRED_FILE);
		options.dieIfPropertyIsEmpty(MorphOptions.TEST_FILE);
		
		Tagger tagger = FileUtils.loadFromFile(options.getModelFile());
		
		if (!options.getMorphDict().isEmpty()) {
			MorphWeightVector vector = (MorphWeightVector) tagger.getWeightVector();
			MorphDictionary dict = vector.getMorphDict();
			if (dict != null) {
				dict.addWordsFromFile(options.getMorphDict());
			} else {
				System.err.format("Warning: Can't add words from morph. dictionary, because morph. dictionary is null!\n");
			}
		}
		
		
		try {
			String pred_file = options.getPredFile();
			Writer writer;
			if (pred_file.isEmpty()) {
				writer = new OutputStreamWriter(System.out);	
			} else {
				writer = new FileWriter(pred_file);
			}
			
			writer = new BufferedWriter(writer);
			
			annotate(tagger, options.getTestFile(), writer);
			
			writer.close();
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void annotate(Tagger tagger, String text_file, Writer writer) throws IOException {	
		SentenceReader reader = new SentenceReader(text_file);
		
		for (Sequence sequence : reader) {
			Sentence sentence = (Sentence) sequence;
			
			if (sentence.isEmpty()) {
				System.err.println("Warning: Skipping empty sentence!");
				continue;
			}
			
			List<List<String>> tags;
			
			try {
			
			tags = tagger.tag(sentence);
			
			} catch (OutOfMemoryError e) {
				
				tags = new ArrayList<List<String>>(sentence.size());
				
				List<String> tag = Collections.singletonList("_");
				
				for (int index = 0; index < sentence.size(); index ++) {
					tags.add(tag);
				}
				
				System.err.format("Warning: Can't tag sentence of length: %d (Not enough memory)!\n", sentence.size());
				
			}
			
			for (int i = 0; i < sentence.size(); i ++) {
				Word word = sentence.getWord(i);
				
				writer.append(Integer.toString(i + 1));
				writer.append(SEPERATOR_);
				writer.append(word.getWordForm());
				
				// Lemma
				writer.append(SEPERATOR_);
				writer.append(EMPTY_);
				writer.append(SEPERATOR_);
				writer.append(EMPTY_);
				
				// Pos
				writer.append(SEPERATOR_);
				writer.append((word.getPosTag() != null ) ? word.getPosTag() : EMPTY_ );
				writer.append(SEPERATOR_);
				writer.append(tags.get(i).get(0));
				
				// Feat
				writer.append(SEPERATOR_);
				writer.append((word.getMorphTag() != null ) ? word.getMorphTag() : EMPTY_);
				writer.append(SEPERATOR_);
				writer.append((tags.get(i).size() > 1) ? tags.get(i).get(1) : EMPTY_);

//				// Head
//				writer.append(SEPERATOR_);
//				writer.append(EMPTY_);
//				writer.append(SEPERATOR_);
//				writer.append(EMPTY_);
//				
//				// Deprel
//				writer.append(SEPERATOR_);
//				writer.append(EMPTY_);
//				writer.append(SEPERATOR_);
//				writer.append(EMPTY_);
//
//				// Predicate
//				writer.append(SEPERATOR_);
//				writer.append(EMPTY_);
//				
//				// Yield
//				writer.append(SEPERATOR_);
//				writer.append(EMPTY_);
					
				writer.append('\n');
			}
			writer.append('\n');
		}
	}
}
