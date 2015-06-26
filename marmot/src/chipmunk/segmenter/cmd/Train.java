package chipmunk.segmenter.cmd;

import java.util.Iterator;
import java.util.List;

import marmot.util.FileUtils;

import chipmunk.segmenter.SegmentationDataReader;
import chipmunk.segmenter.Segmenter;
import chipmunk.segmenter.SegmenterOptions;
import chipmunk.segmenter.SegmenterTrainer;
import chipmunk.segmenter.Word;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

public class Train {

	private static String TRAIN_FILE = "train-file";
	private static String MODEL_FILE = "model-file";
	
	public static void main(String[] args) throws JSAPException {
		FlaggedOption opt;
		JSAP jsap = new JSAP();

		opt = new FlaggedOption(TRAIN_FILE).setRequired(true).setLongFlag(TRAIN_FILE);
		jsap.registerParameter(opt);

		opt = new FlaggedOption(MODEL_FILE).setRequired(true).setLongFlag(MODEL_FILE);
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
		SegmenterTrainer trainer = new SegmenterTrainer(options);
		List<Word> words = new SegmentationDataReader(config.getString(TRAIN_FILE), options.getString(SegmenterOptions.LANG), options.getInt(SegmenterOptions.TAG_LEVEL)).getData();
		Segmenter segmenter = trainer.train(words);
		FileUtils.saveToFile(segmenter, config.getString(MODEL_FILE));		
	}
	
}
