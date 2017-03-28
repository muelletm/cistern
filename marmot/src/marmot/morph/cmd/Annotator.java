// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.cmd;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Hashtable;

import lemming.lemma.Lemmatizer;
import marmot.core.Sequence;
import marmot.morph.MorphDictionary;
import marmot.morph.MorphOptions;
import marmot.morph.MorphTagger;
import marmot.morph.MorphWeightVector;
import marmot.morph.Sentence;
import marmot.morph.Word;
import marmot.morph.io.SentenceReader;
import marmot.util.FileUtils;
import marmot.util.Sys;

public class Annotator {
	
	private static final char SEPARATOR_ = '\t';
	private static final String EMPTY_ = "_";
	
	public static void main(String[] args) {
		MorphOptions options = new MorphOptions();
		options.setPropertiesFromStrings(args);

		options.dieIfPropertyIsEmpty(MorphOptions.MODEL_FILE);
		options.dieIfPropertyIsEmpty(MorphOptions.PRED_FILE);
		options.dieIfPropertyIsEmpty(MorphOptions.TEST_FILE);
		
		MorphTagger tagger = FileUtils.loadFromFile(options.getModelFile());

		if(options.getFormatIsCoNLLU()) {
			tagger.setFormat("conllu");
		}
		
		String lemmatizer_file = options.getLemmatizerFile();
		if (!lemmatizer_file.isEmpty()) {
			Lemmatizer lemmatizer = FileUtils.loadFromFile(lemmatizer_file);
			tagger.setPipeLineLemmatizer(lemmatizer);
		}
		
		if (options.getVerbose()) {
			System.err.format("Loaded model, currently using %g MB of RAM\n", Sys.getUsedMemoryInMegaBytes());
		}
		
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
				writer = new BufferedWriter(new OutputStreamWriter(System.out));	
			} else {
				writer = FileUtils.openFileWriter(pred_file);
			}
			annotate(tagger, options.getTestFile(), writer);
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void annotate(MorphTagger tagger, String text_file, Writer writer) throws IOException {	
		SentenceReader reader = new SentenceReader(text_file);
		
		for (Sequence sequence : reader) {
			annotate(tagger, sequence, writer);
		}
	}

	public static void annotate(MorphTagger tagger, Sequence sequence, Writer writer) throws IOException {
		Sentence sentence = (Sentence) sequence;
		
		if (sentence.isEmpty()) {
			System.err.println("Warning: Skipping empty sentence!");
			return;
		}
		
		List<List<String>> lemma_tags;

		List<String> comments = sentence.getComments(); 

		for (int i = 0; i < comments.size(); i ++) {
			writer.append(comments.get(i) + '\n');
		}
		
		try {
		
		lemma_tags = tagger.tagWithLemma(sentence);
		
		} catch (OutOfMemoryError e) {
			
			lemma_tags = new ArrayList<List<String>>(sentence.size());
			
			List<String> lemma_tag = Arrays.asList(EMPTY_, EMPTY_);
			
			for (int index = 0; index < sentence.size(); index ++) {
				lemma_tags.add(lemma_tag);
			}
			
			System.err.format("Warning: Can't tag sentence of length: %d (Not enough memory)!\n", sentence.size());
			
		}

		Hashtable<Integer, String> segments = sentence.getSegments(); 
		Hashtable<Integer, String> empty_nodes = sentence.getEmptyNodes(); 
		
		for (int i = 0; i < sentence.size(); i ++) {
			Word word = sentence.getWord(i);
			
			List<String> token_lemma_tags = lemma_tags.get(i);

			// Segments go before

			if(segments.containsKey(i+1)) {
				writer.append(segments.get(i+1));
				for(int j = 0; j < 8; j++) {
					writer.append(SEPARATOR_);
					writer.append(EMPTY_);
				}
				writer.append('\n');
			}

			if(tagger.getFormat() == 1) {
				// 0	1		2		3	4	5			6	7	8	9
				// 1	Komissio	komissio	NOUN	N	Case=Nom|Number=Sing	3	nsubj	5:nsubj	_

				writer.append(Integer.toString(i + 1));		// 0
				writer.append(SEPARATOR_);
				writer.append(word.getWordForm());		// 1
				writer.append(SEPARATOR_);
				String lemma = token_lemma_tags.get(0);
				if(word.getLemma() != null) {			// 2
					writer.append(word.getLemma());
				} else if(lemma != null) {
					writer.append(lemma);
				} else {
					writer.append(EMPTY_);
				}
				writer.append(SEPARATOR_);
				String pos = token_lemma_tags.get(1);
				if(word.getPosTag() != null) {			// 3
					writer.append(word.getPosTag());
				} else if(pos != null) {
					writer.append(pos);
				} else {
					writer.append(EMPTY_);
				}
				writer.append(SEPARATOR_);
				writer.append(EMPTY_);				// 4
				writer.append(SEPARATOR_);
				String morph = EMPTY_;
				if(word.getMorphTag() != null) {		// 5 
					writer.append(word.getMorphTag());
				} else if (2 < token_lemma_tags.size()) {
					morph = token_lemma_tags.get(2);
					writer.append(morph);
				} else {
					writer.append(EMPTY_);
				}
				writer.append(SEPARATOR_);
				writer.append(EMPTY_);				// 6
				writer.append(SEPARATOR_);
				writer.append(EMPTY_);				// 7
				writer.append(SEPARATOR_);
				writer.append(EMPTY_);				// 8
				writer.append(SEPARATOR_);
				writer.append(EMPTY_);				// 9

			} else {
				
				writer.append(Integer.toString(i + 1)); 
				writer.append(SEPARATOR_);
				writer.append(word.getWordForm());
				
				// Lemma
				writer.append(SEPARATOR_);
				writer.append(word.getLemma() != null ? word.getLemma() : EMPTY_);
				writer.append(SEPARATOR_);
				
				String lemma = token_lemma_tags.get(0);
				writer.append(lemma != null ? lemma : EMPTY_ );
				
				// Pos
				writer.append(SEPARATOR_);
				writer.append(word.getPosTag() != null ? word.getPosTag() : EMPTY_ );
				writer.append(SEPARATOR_);
				
				String pos = token_lemma_tags.get(1);
				writer.append(pos);
				
				// Feat
				writer.append(SEPARATOR_);
				writer.append(word.getMorphTag() != null ? word.getMorphTag() : EMPTY_);
				writer.append(SEPARATOR_);
				String morph = EMPTY_;
				if (2 < token_lemma_tags.size()) {
					morph = token_lemma_tags.get(2);
				}
				writer.append(morph);
			}

			writer.append('\n');

			// Empty nodes go after 

			if(empty_nodes.containsKey(i+1)) {
				writer.append(empty_nodes.get(i+1));
				writer.append('\n');
			}

		}
		writer.append('\n');
	
	}
}
