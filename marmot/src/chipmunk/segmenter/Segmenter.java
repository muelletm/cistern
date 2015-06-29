package chipmunk.segmenter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

import chipmunk.segmenter.Word;


public abstract class Segmenter implements Serializable {

	private static final long serialVersionUID = 1L;

	public abstract SegmentationReading segment(Word word);

	public void segmentToFile(String outfile, Iterable<Word> words) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
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
