// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.tokenize.preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import marmot.tokenize.openlp.Aligner;
import marmot.tokenize.openlp.LevenshteinAligner;
import marmot.tokenize.rules.RuleProvider;
import marmot.tokenize.rules.RulebasedTransformator;

public class WikiSelector {
	private int num_sentences_;
	private WikiReader reader_;
	private RulebasedTransformator tok_transformator_;
	private RulebasedTransformator untok_transformator_;

	public WikiSelector(String untokenizedFile, String tokenizedFile,
			String lang, int max_sentences) {
		this.num_sentences_ = max_sentences;

		boolean expand = false;
		if (lang.equalsIgnoreCase("de") || lang.equalsIgnoreCase("es")) {
			expand = true;
		}

		reader_ = new WikiReader(untokenizedFile, tokenizedFile, expand);
		
		RuleProvider provider = RuleProvider.createRuleProvider(lang);
		if (provider == null) {
			tok_transformator_ = null;
			untok_transformator_ = null;
		} else {
			tok_transformator_ = provider.getTokTransformator();
			untok_transformator_ = provider.getUnTokTransformator();
		}		
	}

	public void select(String untok, String tok) throws IOException {

		BufferedWriter untok_writer = new BufferedWriter(new FileWriter(untok));
		BufferedWriter tok_writer = new BufferedWriter(new FileWriter(tok));

		int num_selected_sentences = 0;

		while ((num_sentences_ < 0 || num_selected_sentences < num_sentences_)
				&& reader_.hasNext()) {
			Pair pair = reader_.next();

			int num_tokens = pair.tokenized.split("\\s+").length;

			if (num_tokens > 5 && pair.score > 0.01) {

				tok_writer.write(pair.tokenized);
				tok_writer.write('\n');

				untok_writer.write(pair.untokenized);
				untok_writer.write('\n');

				num_selected_sentences += 1;

				// nlp_format.push(Transformator.transform(tok, unTok));
			}

		}

		tok_writer.close();
		untok_writer.close();

		System.err.format("Selected %d sentences.\n", num_selected_sentences);
	}

	//Method to read selected files and print them together with the aligner results
	public void testAligner(String tokfile, String untokfile, int num_sentences, String lang) throws IOException {
		
		// reading data into string arrays
		String[] tokenized = new String[num_sentences];
		String[] untokenized = new String[num_sentences];
		
	    BufferedReader br_tok = new BufferedReader(new FileReader(tokfile));
	    BufferedReader br_untok = new BufferedReader(new FileReader(untokfile));
	    
	    for(int i=0; i<num_sentences; i++) {
	    	tokenized[i] = br_tok.readLine();
	    	untokenized[i] = br_untok.readLine();
	    	
	    	// the actual split is hidden, but here all exceptions are split off	    	
	    	if (tok_transformator_ != null) {
	    		tokenized[i] = tok_transformator_.applyRules(tokenized[i]);
	    	}
	    	
	    	if (untok_transformator_ != null) {
	    		untokenized[i] = untok_transformator_.applyRules(untokenized[i]);
	    	}
	    }	    

	    br_tok.close();
	    br_untok.close();
	    
	    // feeding pairs of a sentence to an aligner
		Aligner a = new LevenshteinAligner();
		int counter_e = 0;
	    for(int i=0; i<num_sentences; i++) {
	    	Viewer v = new Viewer(tokenized[i], untokenized[i], a.align(tokenized[i], untokenized[i]));
	    	List<String> view = null;
			try {
				view = v.getView();
			} catch (java.lang.StringIndexOutOfBoundsException e) {
				System.err.println("Out of bounds!");
				continue;
			}
	    	if(view == null) {
	    		System.err.println("No alignment done!");
	    		System.err.println(tokenized[i]);
	    		System.err.println(untokenized[i]+"\n");
	    		counter_e++;
	    		continue;	
	    	}
	    	for(String str : view) {
	    		System.out.println(str);
	    	}
	    	System.out.println();
	    	System.out.flush();
	    	System.err.flush();
	    }
	    System.out.println("Success\n\n\n");
	    System.out.println(counter_e);
	}
	
	public void generateOpenNlp(String tokfile, String untokfile, int num_sentences, String lang) throws IOException{
        BufferedWriter openNlpFile = new BufferedWriter(new FileWriter("data/"+lang+"/open_nlp_style.txt"));		
		Aligner a = new LevenshteinAligner();
	    BufferedReader br_tok = new BufferedReader(new FileReader(tokfile));
	    BufferedReader br_untok = new BufferedReader(new FileReader(untokfile));	    
	    
	    // some repeated code, but this step needs to be done separately
	    for(int i=0; i<num_sentences; i++) {
	    	String tokenized = br_tok.readLine();
	    	String untokenized = br_untok.readLine();
	    	
	    	if (tok_transformator_ != null) {
	    		tokenized = tok_transformator_.applyRules(tokenized);
	    	}
	    	
	    	if (untok_transformator_ != null) {
	    		untokenized = untok_transformator_.applyRules(untokenized);
	    	}
			List<Aligner.Pair> pairs = a.align(tokenized, untokenized).pairs;
			if(pairs != null) {
	    		openNlpFile.write(insertSplit(untokenized, pairs));
	    		openNlpFile.newLine();
	    	}
	    }	 
	    openNlpFile.close();
	    br_tok.close();
	    br_untok.close();
	}
	
	private String insertSplit(String untokenized, List<Aligner.Pair> pairs){
		String result = "";
		int count = 0;
		for(Aligner.Pair p : pairs) {
			if(p.b == -1){
				result += "<SPLIT>";
			} else {
				result += untokenized.charAt(count);
				count ++;
			}
		}
		return result;
	}
	
	public static void main(String[] args) throws IOException {

		String[] langs = { "es" };
		//String[] langs = { "de", "en", "es" };

		for (String lang : langs) {
			String path;
			//path = "/mounts/data/proj/marmot/tokenizer/data/sml/";
			//path = "/home/muellets/Desktop/tokenizer_data_sml/";
			path = "data/";
			
			path = path + lang;
			
			String untok_file = path + "/sbd_full.txt.bz2";
			String tok_file = path + "/tok_full.txt.bz2";
			int num_sentences = 1000;
			String untok_outfile = path + "/sbd_selected.txt";
			String tok_outfile = path + "/tok_selected.txt";

			WikiSelector selector = new WikiSelector(untok_file, tok_file,
					lang, num_sentences);

			selector.select(untok_outfile, tok_outfile);
			
			selector.testAligner(tok_outfile, untok_outfile, 1000, lang);
			
		}

		// OpenNlpTokenizerTrainer trainer = new OpenNlpTokenizerTrainer();
		// Tokenizer tokenizer =
		// trainer.train("/mounts/data/proj/marmot/tokenizer/data/en/test.txt");
		// tokenizer.saveToFile("/mounts/data/proj/marmot/tokenizer/data/en/test.txt.tok");
		// System.out.println(tokenizer.tokenize("Shouldn't this work, now?"));

	}

}