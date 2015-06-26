package chipmunk.test.segmenter;

import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import marmot.util.Copy;
import marmot.util.Numerics;

import org.junit.Test;

import chipmunk.segmenter.Scorer;
import chipmunk.segmenter.SegmentationDataReader;
import chipmunk.segmenter.Segmenter;
import chipmunk.segmenter.SegmenterOptions;
import chipmunk.segmenter.SegmenterTrainer;


public class SegmenterTest {

	@Test
	public void trainAccuracyTest() {
		String trainfile = "res:///chipmunk/test/segmenter/data/eng/trn";
		SegmentationDataReader reader = new SegmentationDataReader(trainfile, "eng", 0);
		
		SegmenterOptions options = new SegmenterOptions();
		options.setOption(SegmenterOptions.LANG, "eng");
		SegmenterTrainer trainer = new SegmenterTrainer(options);
		Segmenter segmenter = trainer.train(reader.getData());

		segmenter = Copy.clone(segmenter);

		Logger logger = Logger.getLogger(getClass().getName());
		Scorer scorer = new Scorer();
		scorer.eval(reader.getData(), segmenter);
		logger.info(scorer.report());

		double fscore = scorer.getFscore();

		assertTrue(Numerics.approximatelyGreaterEqual(fscore, 99.));
	}
	
	@Test
	public void crfTrainAccuracyTest() {
		String trainfile = "res:///chipmunk/test/segmenter/data/eng/trn";
		SegmentationDataReader reader = new SegmentationDataReader(trainfile, "eng", 0);
		
		SegmenterOptions options = new SegmenterOptions();
		options.setOption(SegmenterOptions.LANG, "eng");
		options.setOption(SegmenterOptions.CRF_MODE, true);
		SegmenterTrainer trainer = new SegmenterTrainer(options);
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
