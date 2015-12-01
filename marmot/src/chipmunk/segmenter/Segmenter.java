package chipmunk.segmenter;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

import marmot.util.FileUtils;

import chipmunk.segmenter.Word;


public abstract class Segmenter implements Serializable {

	private static final long serialVersionUID = 1L;

	public abstract SegmentationReading segment(Word word);

	public void segmentToFile(String outfile, Iterable<Word> words) throws IOException {
		Writer writer = FileUtils.openFileWriter(outfile);
		for (Word word : words) {
			SegmentationReading reading = segment(word);
			writer.write(word.getWord());
			writer.write('\t');
			writer.write(reading.toString());
			writer.write('\n');
		}
		writer.close();
	}

}
