package chipmunk.segmenter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class Stemmer {

	public enum Mode {
		ROOT_DETECTION, STEMMING
	};

	private Segmenter segmenter_;
	private Mode mode_;

	public Stemmer(Segmenter segmenter, Mode mode) {
		segmenter_ = segmenter;
		mode_ = mode;
	}

	String stem(Word word) {

		SegmentationReading reading = segmenter_.segment(word);

		StringBuilder sb = new StringBuilder();

		Iterator<String> segments = reading.getSegments().iterator();
		Iterator<String> tags = reading.getTags().iterator();

		while (segments.hasNext()) {

			String segment = segments.next();
			String tag = tags.next();

			if (tag.startsWith("ROOT")
					|| (tag.startsWith("DERI") && mode_ == Mode.STEMMING)
					|| tag.startsWith(TagSet.NUMBER)
					|| tag.startsWith(TagSet.SPECIAL)) {
				sb.append(segment);
			}

		}

		if (sb.length() == 0) {
			return "EMPTY";
		}
		return sb.toString();
	}

	public void stemToFile(String outfile, SegmentationDataReader words)
			throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
		for (Word word : words) {
			String stem = stem(word);
			writer.write(word.getWord());
			writer.write('\t');
			writer.write(stem);
			writer.write('\n');
		}
		writer.close();
	}

}
