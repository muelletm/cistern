package marmot.tokenize.cmd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import marmot.tokenize.RuleBasedTokenizer;
import marmot.tokenize.Tokenizer;
import marmot.tokenize.openlp.OpenNlpConverter;
import marmot.tokenize.openlp.OpenNlpTokenizerTrainer;
import marmot.tokenize.preprocess.Pair;
import marmot.tokenize.preprocess.WikiSelector;
import marmot.tokenize.rules.RuleProvider;
import marmot.util.GeneralLevenshteinLattice;
import marmot.util.LevenshteinLattice;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

public class Experimenter {

	public static void main(String[] args) throws IOException, JSAPException {
	
		FlaggedOption opt;
		JSAP jsap = new JSAP();

		opt = new FlaggedOption("tokenized-file").setRequired(true).setLongFlag(
				"tokenized-file");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("untokenized-file").setRequired(true).setLongFlag(
				"untokenized-file");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("lang").setRequired(true).setLongFlag(
				"lang");
		jsap.registerParameter(opt);
		
		opt = new FlaggedOption("num-sentences").setLongFlag(
				"num-sentences").setStringParser(JSAP.INTEGER_PARSER).setDefault("1000");
		jsap.registerParameter(opt);
		
		opt = new FlaggedOption("random-seed").setLongFlag(
				"random-seed").setStringParser(JSAP.INTEGER_PARSER).setDefault("42");
		jsap.registerParameter(opt);

		
		opt = new FlaggedOption("verbosity").setLongFlag(
				"verbosity").setStringParser(JSAP.INTEGER_PARSER).setDefault("0");
		jsap.registerParameter(opt);
		
		JSAPResult config = jsap.parse(args);

		if (!config.success()) {
			for (Iterator<?> errs = config.getErrorMessageIterator(); errs
					.hasNext();) {
				System.err.println("Error: " + errs.next());
			}
			System.err.println("Usage: ");
			System.err.println(jsap.getUsage());
			System.err.println(jsap.getHelp());
			System.err.println();
			System.exit(1);
		}

		String lang = config.getString("lang");
		String tok_file = config.getString("tokenized-file");
		String untok_file = config.getString("untokenized-file");
		int num_sentences = config.getInt("num-sentences");
		int verbosity = config.getInt("verbosity");
		
		Random random = new Random(config.getInt("random-seed"));
		// verbose: 0 no output
		//			1 only success
		//			2 all messages
		//			3 only failure
		
		boolean expand = lang.equalsIgnoreCase("de") || lang.equalsIgnoreCase("es");
		
		List<Pair> pairs = new LinkedList<>(); 
		for (Pair pair : new WikiSelector(untok_file, tok_file,
				expand, num_sentences)) {
			pairs.add(pair);
		}
		Collections.shuffle(pairs, random);
		
		List<Pair> trnset = new LinkedList<>();
		List<Pair> devset = new LinkedList<>();
		List<Pair> tstset = new LinkedList<>();
		
		int index = 0;
		for (Pair pair : pairs) {
			
			if (index == 0) {
				devset.add(pair);		
			} else if (index == 1) {
				tstset.add(pair);
			} else {
				trnset.add(pair);
			}
			
			index = (index + 1) % 10;
		}

		runExperiment(trnset, devset, tstset, 1., verbosity, lang);
		runExperiment(trnset, devset, tstset, 10., verbosity, lang);
		runExperiment(trnset, devset, tstset, 100., verbosity, lang);
	}

	public static void runExperiment(List<Pair> trnset, List<Pair> devset,
			List<Pair> tstset, double percentage, int verbosity, String lang) throws IOException {
		
		
		int trnset_size = (int) ((percentage * trnset.size()) / 100.);
		System.err.format("Trnsize: %d\n", trnset_size);
		trnset = trnset.subList(0, trnset_size);

		RuleProvider provider = RuleProvider.createRuleProvider(lang);
		OpenNlpConverter converter = new OpenNlpConverter(provider);
		
		File opennlp_file = File.createTempFile("openlp_file", ".txt");
		opennlp_file.deleteOnExit();
		BufferedWriter writer = new BufferedWriter(new FileWriter(opennlp_file));
		converter.convert(trnset, writer, verbosity);		
		writer.close();
		
		OpenNlpTokenizerTrainer trainer = new OpenNlpTokenizerTrainer();
		Tokenizer tokenizer = trainer.train(opennlp_file.getAbsolutePath());
		tokenizer = new RuleBasedTokenizer(tokenizer, provider);

		runEvaluation(lang, tokenizer, devset);
	}

	public static void runEvaluation(String lang, Tokenizer tokenizer, List<Pair> devset) {
		
		int sentence_errors = 0;
		int character_errors = 0;
		int word_errors = 0;
		
		int sentence_total = 0;
		int character_total = 0;
		int word_total = 0;
		
		for (Pair pair : devset) {
			
			List<String> predicted_tokens = tokenizer.tokenize(pair.untokenized);
			String actual_string = pair.tokenized;
			if (lang.equals("es")) {
				actual_string = actual_string.replace('_', ' ');
			}
			String[] array = actual_string.split("\\s+");
			List<String> actual_tokens = Arrays.asList(array);
			
			StringBuilder sb = new StringBuilder();
			for (String token : predicted_tokens) {
				if (sb.length() > 0) {
					sb.append(' ');
				}
				sb.append(token);
			}
			String predicted_string = sb.toString();
			
			// Sentence Accuracy
			if (!predicted_tokens.equals(actual_tokens)) {
				sentence_errors += 1;
			}
			sentence_total += 1;
			
			// Word Accuracy			
			LevenshteinLattice lattice = new LevenshteinLattice(actual_string, predicted_string);
			character_errors += lattice.getDistance();
			character_total +=	actual_string.length();

			// Character Accuracy
			GeneralLevenshteinLattice<String> glattice = new GeneralLevenshteinLattice<String>(actual_tokens, predicted_tokens);
			word_errors += glattice.getDistance();
			word_total += actual_tokens.size();
		}
		
		System.err.format("Sent Err: %d / %d = %g\n", sentence_errors, sentence_total, sentence_errors * 100. / sentence_total);
		System.err.format("Word Err: %d / %d = %g\n", word_errors, word_total, word_errors * 100. / word_total);
		System.err.format("Char Err: %d / %d = %g\n", character_errors, character_total, character_errors * 100. / character_total);
	}

}
