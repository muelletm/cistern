package marmot.test.segmenter;

import static org.junit.Assert.*;

import marmot.segmenter.SegmenterTrainer;
import marmot.segmenter.SegmentationDataReader;
import marmot.segmenter.Segmenter;

import org.junit.Test;

public class SegmenterTest {

	@Test
	public void test() {
		String trainfile = "res:///marmot/test/segmenter/en.trn";
		SegmentationDataReader reader = new SegmentationDataReader(trainfile);
		SegmenterTrainer trainer = new SegmenterTrainer();
		Segmenter segmenter = trainer.train(reader.getData());
	}

}
