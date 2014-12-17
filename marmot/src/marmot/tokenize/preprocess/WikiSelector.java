package marmot.tokenize.preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import marmot.tokenize.openlp.Aligner;
import marmot.tokenize.openlp.LevenshteinAligner;
import marmot.tokenize.openlp.SplitRules;

public class WikiSelector {
	private int num_sentences_;
	private WikiReader reader;
	private SplitRules split_rules;

	public WikiSelector(String untokenizedFile, String tokenizedFile,
			String lang, int max_sentences) {
		this.num_sentences_ = max_sentences;

		boolean expand = false;
		if (lang.equalsIgnoreCase("de") || lang.equalsIgnoreCase("es")) {
			expand = true;
		}

		reader = new WikiReader(untokenizedFile, tokenizedFile, expand);
		split_rules = new SplitRules();
	}

	public void select(String untok, String tok) throws IOException {

		BufferedWriter untok_writer = new BufferedWriter(new FileWriter(untok));
		BufferedWriter tok_writer = new BufferedWriter(new FileWriter(tok));

		int num_selected_sentences = 0;

		while ((num_sentences_ < 0 || num_selected_sentences < num_sentences_)
				&& reader.hasNext()) {
			Pair pair = reader.next();

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
	public void testAligner(int num_sentences, String lang) throws IOException {
		
		// reading data into string arrays
		String[] tokenized = new String[num_sentences];
		String[] untokenized = new String[num_sentences];
		
	    BufferedReader br_tok = new BufferedReader(new FileReader(
	    		//"/home/muellets/Desktop/tokenizer_data_sml/"+lang+"/tok_selected.txt"));
	    		"data/"+lang+"/tok_selected.txt"));
	    BufferedReader br_untok = new BufferedReader(new FileReader(
	    		//"/home/muellets/Desktop/tokenizer_data_sml/"+lang+"/sbd_selected.txt"));
	    		"data/"+lang+"/sbd_selected.txt"));
	    
	    for(int i=0; i<num_sentences; i++) {
	    	String tmp = br_tok.readLine(); 
	    	if(lang.equals("es") && tmp.contains("_")) {
	    		tmp = tmp.replace("_", " ");
	    	}
	    	
	    	// the actual split is hidden, but here all exceptions are split off
	    	String[] line = split_rules.applyRules(br_untok.readLine(), tmp);
	    	untokenized[i] = line[0];
	    	tokenized[i] = line[1];
	    }	    

	    br_tok.close();
	    br_untok.close();
	    
	    // feeding pairs of a sentence to an aligner
		Aligner a = new LevenshteinAligner();
	    for(int i=0; i<num_sentences; i++) {	    	
	    	Viewer v = new Viewer(tokenized[i], untokenized[i], a.align(tokenized[i], untokenized[i]));
	    	List<String> view = v.getView();
	    	if(view == null) {
	    		System.err.println("No alignment done!");
	    		System.err.println(tokenized[i]);
	    		System.err.println(untokenized[i]+"\n");
	    		continue;
	    	}
	    	for(String str : view) {
	    		System.out.println(str);
	    	}
	    	System.out.println();
	    }
	    System.out.println("Success\n\n\n");
	    
	}
	
	public static void main(String[] args) throws IOException {

		String[] langs = { "de", "en", "es" };

		for (String lang : langs) {
			//String path = "/home/muellets/Desktop/tokenizer_data_sml/" + lang;
			String path = "data/" + lang;
			
			String untok_file = path + "/sbd_full.txt.bz2";
			String tok_file = path + "/tok_full.txt.bz2";
			int num_sentences = 1000;
			String untok_outfile = path + "/sbd_selected.txt";
			String tok_outfile = path + "/tok_selected.txt";

			WikiSelector selector = new WikiSelector(untok_file, tok_file,
					lang, num_sentences);

			selector.select(untok_outfile, tok_outfile);
			
			selector.testAligner(20, lang);

		}

		// OpenNlpTokenizerTrainer trainer = new OpenNlpTokenizerTrainer();
		// Tokenizer tokenizer =
		// trainer.train("/mounts/data/proj/marmot/tokenizer/data/en/test.txt");
		// tokenizer.saveToFile("/mounts/data/proj/marmot/tokenizer/data/en/test.txt.tok");
		// System.out.println(tokenizer.tokenize("Shouldn't this work, now?"));

	}

}