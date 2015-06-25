package chipmunk.segmenter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import chipmunk.segmenter.Word;


public abstract class Segmenter implements Serializable {

	private static final long serialVersionUID = 1L;

	public abstract SegmentationReading segment(Word word);

	public void segmentToFile(String outfile, List<Word> test) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
		for (Word word : test) {
			
			SegmentationReading result = segment(word);
			
			writer.write(word.getWord());
			writer.write('\t');
			
			Iterator<String> segment_iterator = result.getSegments().iterator();
			Iterator<String> tag_iterator = result.getTags().iterator();
			
			boolean first = true;
			while (segment_iterator.hasNext()) {
				
				String segment = segment_iterator.next();
				String tag = tag_iterator.next();
				
				if (!first) {
					writer.write(' ');
				}
				
				writer.write(segment);
				writer.write(':');
				writer.write(tag);
				
				first = false;
			}
			
			writer.write('\n');
		}
		writer.close();

	}

}
