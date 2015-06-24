package marmot.test.segmenter;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import marmot.segmenter.SegmentationInstance;
import marmot.segmenter.SegmentationReading;
import marmot.segmenter.SegmentationResult;
import marmot.segmenter.SegmentationSumLattice;
import marmot.segmenter.SegmenterModel;
import marmot.segmenter.Word;
import marmot.util.DynamicWeights;
import marmot.util.Numerics;

import org.junit.Assert;
import org.junit.Test;

public class SumLatticeTest {

	double explicit_update(SegmentationInstance instance, SegmenterModel model) {
		int max_segment_length = model.getMaxSegmentLength();

		List<SegmentationResult> results = new LinkedList<>();
		addAllResults(instance, model, max_segment_length, results, 0);

		double score_sum = Double.NEGATIVE_INFINITY;
		for (SegmentationResult result : results) {
			double score = model.getScore(instance, result);
			score_sum = Numerics.sumLogProb(score, score_sum);
		}
		
		for (SegmentationResult result : results) {
			double score = model.getScore(instance, result);
			double log_prob = score - score_sum;
			double prob = Math.exp(log_prob);
			model.update(instance, result, -prob);
		}
		
		double score = model.getScore(instance, instance.getFirstResult());
		double log_prob = score - score_sum;
		model.update(instance, instance.getFirstResult(), 1.0);
		
		return log_prob;
	}

	private void addAllResults(SegmentationInstance instance,
			SegmenterModel model, int max_segment_length,
			List<SegmentationResult> results, int start) {

		String word = instance.getWord().getWord();

		for (int end = start + 1; end <= Math.min(start + max_segment_length,
				word.length()); end++) {

			List<SegmentationResult> intermediates = new LinkedList<>();

			if (end == word.length()) {

				for (int tag = 0; tag < model.getNumTags(); tag++) {

					List<Integer> tags = new LinkedList<>();
					tags.add(tag);

					List<Integer> indexes = new LinkedList<>();
					indexes.add(end);

					results.add(new SegmentationResult(tags, indexes));
				}
				
			} else {

				addAllResults(instance, model, max_segment_length,
						intermediates, end);

				for (SegmentationResult intermediate : intermediates) {

					for (int tag = 0; tag < model.getNumTags(); tag++) {

						List<Integer> tags = new LinkedList<>();
						tags.add(tag);
						tags.addAll(intermediate.getTags());

						List<Integer> indexes = new LinkedList<>();
						indexes.add(end);
						indexes.addAll(intermediate.getInputIndexes());

						results.add(new SegmentationResult(tags, indexes));
					}
				}

			}
		}
	}

	@Test
	public void test() {

		List<Word> words = new LinkedList<>();

		words.add(toWord(Arrays.asList("b"), Arrays.asList("B")));
		words.add(toWord(Arrays.asList("aa"), Arrays.asList("A")));
		words.add(toWord(Arrays.asList("a", "bb"), Arrays.asList("A", "B")));
		words.add(toWord(Arrays.asList("aa", "bb"), Arrays.asList("A", "B")));
		words.add(toWord(Arrays.asList("a", "b"), Arrays.asList("A", "B")));
		words.add(toWord(Arrays.asList("aa", "b"), Arrays.asList("A", "B")));
		words.add(toWord(Arrays.asList("aa", "c"), Arrays.asList("A", "C")));

		SegmenterModel model = new SegmenterModel();
		model.init(null, words, 0, false, false, Collections.<String> emptyList());

		SegmentationSumLattice lattice = new SegmentationSumLattice(model);
				
		Random random = new Random(42);
		
		for (int trial = 0; trial < 10; trial ++) {
			
			double[] weights = new double[50];
			for (int i=0; i<weights.length; i++) {
				weights[i] = random.nextGaussian(); 
			}
			
			double[] gradient = new double[weights.length];
			model.setScorerWeights(new DynamicWeights(weights, false, false));
			model.setUpdaterWeights(new DynamicWeights(gradient, false, false));

			for (Word word : words) {
//				System.err.println("\n\n\nNEW WORD:" + word);
				
				SegmentationInstance instance = model.getInstance(word);

//				System.err.println("LATTICE");
				double act_value = lattice.update(instance, true);
				double[] act_gradient = gradient.clone();
				Arrays.fill(gradient, 0.0);
				
//				System.err.println("\n\nEXPLICIT");
				double real_value = explicit_update(instance, model);
				double[] real_gradient = gradient.clone();
				Arrays.fill(gradient, 0.0);

				boolean equal_gradient = Numerics.approximatelyEqual(act_gradient, real_gradient, 1e-5);
				if (!equal_gradient) {
					System.err.println(Arrays.toString(act_gradient) + "\n" + Arrays.toString(real_gradient));
				}
				
				boolean equal_value = Numerics.approximatelyEqual(act_value, real_value);
				if (!equal_value) {
					System.err.println(word + " " + act_value + "\n" + real_value);
				}
			
				Assert.assertTrue(equal_gradient && equal_value);
			}
			
		}
		
		
		
	}

	private Word toWord(List<String> segments, List<String> tags) {
		String form = "";
		for (String segment : segments) {
			form += segment;
		}
		Word w = new Word(form);
		w.add(new SegmentationReading(segments, tags));
		return w;
	}

}
