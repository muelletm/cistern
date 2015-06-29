package chipmunk.segmenter;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import marmot.util.FileUtils;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

public class SegmenterExperiment {

	private final static String STEM_DICT = "stem-dict";

	public static void main(String[] args) throws JSAPException, IOException {

		FlaggedOption opt;
		JSAP jsap = new JSAP();

		opt = new FlaggedOption("dir").setRequired(true).setLongFlag("dir");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("out").setRequired(true).setLongFlag("out").setDefault("");
		jsap.registerParameter(opt);
		
		opt = new FlaggedOption(STEM_DICT).setRequired(true).setLongFlag(STEM_DICT).setDefault("_");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("num-chunks")
				.setStringParser(JSAP.INTEGER_PARSER).setLongFlag("num-chunks")
				.setDefault("10");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("use-dict")
				.setStringParser(JSAP.BOOLEAN_PARSER).setLongFlag("use-dict")
				.setDefault("true");
		jsap.registerParameter(opt);

		SegmenterOptions options = new SegmenterOptions();
		options.registerOptions(jsap);

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

		options.setOptions(config);
		String dir = config.getString("dir");
		String out = config.getString("out");
		String lang = options.getString(SegmenterOptions.LANG);
		int num_chunks = config.getInt("num-chunks");
		boolean use_dict = config.getBoolean("use-dict");
		String stem_dicts = config.getString(STEM_DICT);
		String dict_path = "_";

		if (use_dict) {
			dict_path = String.format(
					"%s/%s/wiktionary.txt %s/%s/aspell.txt %s/%s/wordlist.txt",
					dir, lang, dir, lang, dir, lang);
			if (!stem_dicts.equals("_")) {
				dict_path = dict_path + " " + stem_dicts;
			}
			
		}
		
		options.setOption(SegmenterOptions.VERBOSE, true);
		
		options.setOption(SegmenterOptions.DICTIONARY_PATHS, dict_path);

		Logger logger = Logger.getLogger(SegmenterTrainer.class.getName());

		String global_trainfile = String.format("%s/%s/trn", dir, lang);

		SegmentationDataReader global_reader = new SegmentationDataReader(
				global_trainfile, lang,
				options.getInt(SegmenterOptions.TAG_LEVEL));

		double score_sum = 0.0;

		int start_chunk = 0;
		int end_chunk = num_chunks;

		if (num_chunks < 10) {
			start_chunk = num_chunks;
			end_chunk = num_chunks + 1;
			num_chunks = 1;
		}

		for (int i = start_chunk; i < end_chunk; i++) {
			System.err.format("chunk: %d\n", i);

			String trainfile = String.format("%s/%s/%d.trn", dir, lang, i);
			String testfile = String.format("%s/%s/%d.tst", dir, lang, i);

			List<Word> train = new SegmentationDataReader(trainfile, lang, 0)
					.getData();
			train = global_reader.map(train);

			List<Word> test = new SegmentationDataReader(testfile, lang, 0)
					.getData();
			test = global_reader.map(test);

			SegmenterTrainer trainer = new SegmenterTrainer(options);

			Segmenter segmenter = trainer.train(train);
			Scorer scorer = new Scorer();
			scorer.eval(test, segmenter);
			logger.info(String.format("%s F1 of chunk %d: %s\n", lang, i,
					scorer.report()));
			score_sum += scorer.getFscore();

			if (!out.isEmpty()) {
				FileUtils.mkDir(String.format("%s/%s", out, lang));
				String outfile = String.format("%s/%s/%d.tst", out, lang, i);
				segmenter.segmentToFile(outfile, test);
			}

		}

		logger.info(String.format("%s Average F1: %g\n", lang, score_sum
				/ num_chunks));

	}

}
