package marmot.segmenter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import marmot.segmenter.Word;

public class Segmenter implements Serializable {

	private static final long serialVersionUID = 1L;
	private SegmenterModel model_;

	public Segmenter(SegmenterModel model) {
		model_ = model;
	}
	
	public SegmentationReading segment(Word word) {
		SegmentationInstance instance = model_.getInstance(word);
		SegmentationDecoder decoder = new SegmentationDecoder(model_);
		SegmentationResult result = decoder.decode(instance);
		return model_.toWord(word.getWord(), result).getReadings().iterator().next();
	}

	public SegmenterModel getModel() {
		return model_;
	}

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
