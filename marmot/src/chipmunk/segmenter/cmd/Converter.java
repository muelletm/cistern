package chipmunk.segmenter.cmd;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import marmot.util.FileUtils;

import chipmunk.segmenter.SegmentationDataReader;
import chipmunk.segmenter.SegmentationReading;
import chipmunk.segmenter.SegmenterOptions;
import chipmunk.segmenter.Word;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

public class Converter {

	private static String CHUNK_FILE = "chunk-file";
	private static String TAGS_FILE = "tags-file";
	private static String OUTPUT_FILE = "output-file";
	
	public static void main(String[] args) throws JSAPException, IOException {
		FlaggedOption opt;
		JSAP jsap = new JSAP();

		opt = new FlaggedOption(CHUNK_FILE).setRequired(true).setLongFlag(CHUNK_FILE);
		jsap.registerParameter(opt);

		opt = new FlaggedOption(TAGS_FILE).setRequired(true).setLongFlag(TAGS_FILE);
		jsap.registerParameter(opt);
		
		opt = new FlaggedOption(OUTPUT_FILE).setRequired(true).setLongFlag(OUTPUT_FILE);
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

		SegmentationDataReader tag_reader = new SegmentationDataReader(config.getString(TAGS_FILE), options.getString(SegmenterOptions.LANG), 4);
		List<Word> words = new SegmentationDataReader(config.getString(CHUNK_FILE), options.getString(SegmenterOptions.LANG), 4).getData();
		
		words = tag_reader.map(words);
		
		Writer writer = FileUtils.openFileWriter(config.getString(OUTPUT_FILE));
		for (Word word : words) {
			for (SegmentationReading reading : word.getReadings()) {
				writer.write(word.getWord());
				writer.write('\t');
				writer.write(reading.toString());
				writer.write('\n');
			}
		}
		writer.close();
				
	}
	
}
