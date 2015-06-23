package marmot.test.segmenter;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import marmot.segmenter.Scorer;
import marmot.segmenter.SegmenterTrainer;
import marmot.segmenter.SegmentationDataReader;
import marmot.segmenter.Segmenter;
import marmot.util.Copy;
import marmot.util.Numerics;

import org.junit.Test;

public class SegmenterTest {

	@Test
	public void test() {
		String trainfile = "res:///marmot/test/segmenter/en.trn";
		SegmentationDataReader reader = new SegmentationDataReader(trainfile, false);
		SegmenterTrainer trainer = new SegmenterTrainer();
		Segmenter segmenter = trainer.train(reader.getData());

		segmenter = Copy.clone(segmenter);		
		
		Logger logger = Logger.getLogger(getClass().getName());
		Scorer scorer = new Scorer();
		scorer.eval(reader.getData(), segmenter);
		logger.info(scorer.report());
		
		double fscore = scorer.getFscore();
		
		assertTrue(Numerics.approximatelyGreaterEqual(fscore, 99.));
		
	}

}
