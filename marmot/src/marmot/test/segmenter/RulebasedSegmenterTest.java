package marmot.test.segmenter;

import java.util.Arrays;
import java.util.List;

import marmot.segmenter.Word;
import marmot.segmenter.RulebasedSegmenter;
import marmot.segmenter.SegmentationReading;
import marmot.segmenter.Segmenter;
import marmot.segmenter.TagSet;

import org.junit.Assert;
import org.junit.Test;

public class RulebasedSegmenterTest {

	@Test
	public void test() {
		Segmenter segmenter = new RulebasedSegmenter();
		test(segmenter, "123abc", Arrays.asList("123", "abc"), Arrays.asList(TagSet.NUMBER, TagSet.ALPHA));
		test(segmenter, "123--abc", Arrays.asList("123", "-", "-", "abc"), Arrays.asList(TagSet.NUMBER,TagSet.SPECIAL, TagSet.SPECIAL, TagSet.ALPHA));
		test(segmenter, "12c-", Arrays.asList("12", "c", "-"), Arrays.asList(TagSet.NUMBER, TagSet.ALPHA, TagSet.SPECIAL));
	}

	private void test(Segmenter segmenter, String string, List<String> segments,
			List<String> tags) {
		
		SegmentationReading reading = segmenter.segment(new Word(string));
		
		Assert.assertEquals(reading.getSegments(), segments);
		Assert.assertEquals(reading.getTags(), tags);
		
	}

}
