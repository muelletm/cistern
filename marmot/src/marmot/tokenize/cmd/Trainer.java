package marmot.tokenize.cmd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import marmot.tokenize.RuleBasedTokenizer;
import marmot.tokenize.Tokenizer;
import marmot.tokenize.openlp.OpenNlpConverter;
import marmot.tokenize.openlp.OpenNlpTokenizerTrainer;
import marmot.tokenize.preprocess.BufferedReaderWrapper;
import marmot.tokenize.preprocess.Pair;
import marmot.tokenize.preprocess.WikiSelector;
import marmot.tokenize.rules.RuleProvider;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

public class Trainer {

	
	public static void main(String[] args) throws IOException, JSAPException {
		
		FlaggedOption opt;
		JSAP jsap = new JSAP();

		opt = new FlaggedOption("tokenized-file").setRequired(true).setLongFlag(
				"tokenized-file");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("untokenized-file").setRequired(true).setLongFlag(
				"untokenized-file");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("model-file").setRequired(true).setLongFlag(
				"model-file");
		jsap.registerParameter(opt);
		
		opt = new FlaggedOption("lang").setRequired(true).setLongFlag(
				"lang");
		jsap.registerParameter(opt);
		
		opt = new FlaggedOption("num-sentences").setRequired(true).setLongFlag(
				"num-sentences").setStringParser(JSAP.INTEGER_PARSER).setDefault("1000");
		jsap.registerParameter(opt);
		
		opt = new FlaggedOption("verbose").setRequired(true).setLongFlag(
				"verbose").setStringParser(JSAP.INTEGER_PARSER).setDefault("0");
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
		String model_file = config.getString("model-file");
		int num_sentences = config.getInt("num-sentences");
		int verbose = config.getInt("verbose");
		// verbose: 0 no output
		//			1 only success
		//			2 all messages
		//			3 only failure
	
		boolean expand = lang.equalsIgnoreCase("de") || lang.equalsIgnoreCase("es");
		
		Iterable<Pair> pairs = new WikiSelector(untok_file, tok_file,
				expand, num_sentences);
			
		RuleProvider provider = RuleProvider.createRuleProvider(lang);
		OpenNlpConverter converter = new OpenNlpConverter(provider);
		
		System.out.println("Starting alignment for '"+lang+"' textset");
		File opennlp_file = File.createTempFile("openlp_file", ".txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(opennlp_file));
		converter.convert(pairs, writer, verbose);		
		writer.close();
			
		OpenNlpTokenizerTrainer trainer = new OpenNlpTokenizerTrainer();
		Tokenizer tokenizer = trainer.train(opennlp_file.getAbsolutePath());
		tokenizer = new RuleBasedTokenizer(tokenizer, provider);
		tokenizer.saveToFile(model_file);
		
	}	
	
}
