package marmot.test.segmenter;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.logging.Logger;

import marmot.segmenter.Scorer;
import marmot.segmenter.SegmentationDataReader;
import marmot.segmenter.Segmenter;
import marmot.segmenter.SegmenterTrainer;
import marmot.segmenter.Word;
import marmot.util.Copy;
import marmot.util.ListUtils;
import marmot.util.Numerics;

import org.junit.Test;

public class SegmenterTest {

	@Test
	public void trainAccuracyTest() {
		String trainfile = "res:///marmot/test/segmenter/en.trn";
		SegmentationDataReader reader = new SegmentationDataReader(trainfile,
				false);
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

	@Test
	public void trainCrossfoldTest() {
		Logger logger = Logger.getLogger(getClass().getName());
		String trainfile = "res:///marmot/test/segmenter/en.trn";
		SegmentationDataReader reader = new SegmentationDataReader(trainfile,
				false);

		List<List<Word>> chunks = ListUtils.chunk(reader.getData(), 10);

		double score_sum = 0.0;

		for (int i = 0; i < chunks.size(); i++) {
			List<Word> train = ListUtils.complement(chunks, i);
			List<Word> test = chunks.get(i);

			SegmenterTrainer trainer = new SegmenterTrainer();
			Segmenter segmenter = trainer.train(train);

			Scorer scorer = new Scorer();
			scorer.eval(test, segmenter);
			logger.info(String.format("F1 of chunk %d: %s\n", i,
					scorer.report()));
			score_sum += scorer.getFscore();
		}

		logger.info(String.format("Average F1: %g\n", score_sum / chunks.size()));
	}

	@Test
	public void trainCrossfoldTest2() {
		Logger logger = Logger.getLogger(getClass().getName());

		

		int num_chunks = 10;

		String[] langs = { "ger", "tur" };

		for (String lang : langs) {

			double score_sum = 0.0;
			
			for (int i = 0; i < num_chunks; i++) {
				String trainfile = String.format(
						"res:///marmot/test/segmenter/data/%s/%d.trn", lang, i);
				String testfile = String.format(
						"res:///marmot/test/segmenter/data/%s/%d.tst", lang, i);

				List<Word> train = new SegmentationDataReader(trainfile, false)
						.getData();
				List<Word> test = new SegmentationDataReader(testfile, false)
						.getData();

				SegmenterTrainer trainer = new SegmenterTrainer();
				Segmenter segmenter = trainer.train(train);
				Scorer scorer = new Scorer();
				scorer.eval(test, segmenter);
				logger.info(String.format("%s F1 of chunk %d: %s\n", lang, i,
						scorer.report()));
				score_sum += scorer.getFscore();

			}
			logger.info(String.format("%s Average F1: %g\n", lang, score_sum
					/ num_chunks));
		}
	}

}
