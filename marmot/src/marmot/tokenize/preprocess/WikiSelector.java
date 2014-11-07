package marmot.tokenize.preprocess;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class WikiSelector {
	private int num_sentences_;
	private WikiReader reader;

	public WikiSelector(String untokenizedFile, String tokenizedFile,
			String lang, int max_sentences) {
		this.num_sentences_ = max_sentences;

		boolean expand = false;
		if (lang.equalsIgnoreCase("de") || lang.equalsIgnoreCase("es")) {
			expand = true;
		}

		reader = new WikiReader(untokenizedFile, tokenizedFile, expand);
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

	public static void main(String[] args) throws IOException {

		String[] langs = { "de", "en", "es" };

		for (String lang : langs) {

			String path = "/home/muellets/Desktop/tokenizer_data_sml/" + lang;

			String untok_file = path + "/sbd_full.txt.bz2";
			String tok_file = path + "/tok_full.txt.bz2";
			int num_sentences = -1;
			String untok_outfile = path + "/sbd_selected.txt";
			String tok_outfile = path + "/tok_selected.txt";

			WikiSelector selector = new WikiSelector(untok_file, tok_file,
					lang, num_sentences);

			selector.select(untok_outfile, tok_outfile);

		}

		// OpenNlpTokenizerTrainer trainer = new OpenNlpTokenizerTrainer();
		// Tokenizer tokenizer =
		// trainer.train("/mounts/data/proj/marmot/tokenizer/data/en/test.txt");
		// tokenizer.saveToFile("/mounts/data/proj/marmot/tokenizer/data/en/test.txt.tok");
		// System.out.println(tokenizer.tokenize("Shouldn't this work, now?"));

	}

}
