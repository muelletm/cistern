// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util.eval;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomizationTest implements SignificanceTest {
	Random random_;

	public RandomizationTest() {
		random_ = new Random();
	}
	
	public RandomizationTest(long seed) {
		random_ = new Random(seed);
	}

	public double getSum(List<Double> scores) {
		double sum = 0.0;
		for (double score : scores) {
			sum += score;
		}
		return sum;
	}
	
	public double test(Scorer scorer, String gold, String pred1, String pred2) {
		List<Double> scores1 = scorer.getScores(gold, pred1);
		List<Double> scores2 = scorer.getScores(gold, pred2);
		List<Double> diffs = getDifferences(scores1, scores2, true);	
		double diff = getAbsoluteDifference(diffs, false);
			
		int total = 1048576;
		int error = 0;
		
		for (int index = 0; index < total; index++) {
			double random_diff = getAbsoluteDifference(diffs, true);
					
			if (diff - random_diff < 1.e-10) {
				error += 1;
			}
		}

		error++;
		total++;
		return error / (double) total;
	}

	public static List<Double> getDifferences(List<Double> scores1,
			List<Double> scores2, boolean remove_zeroes) {	
		List<Double> list = new ArrayList<Double>(scores1.size());
		for (int index = 0; index < scores1.size(); index++) {
			double diff = scores1.get(index) - scores2.get(index);
			
			if (remove_zeroes && Math.abs(diff) < 1e-99) {
				continue;
			}
			
			list.add(diff);
		}
		return list;
	}

	private double getAbsoluteDifference(List<Double> differences, boolean random) {
		double diff = 0.;
		for (double current_diff : differences) {
			if (random && random_.nextBoolean()) {
				diff -= current_diff;
			} else {
				diff += current_diff;
			}
		}
		return Math.abs(diff);
	}
	
	public static void main(String[] args) {
		
		String scorer_string = args[0];
		Scorer scorer;
		try {
			scorer = (Scorer)(Class.forName(scorer_string).newInstance());
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		
		String actual = args[1];
		String prediction1 = args[2];
		String prediction2 = args[3];
		
		if (args.length > 4 ) {
			String[] key_value = args[4].split("=");
			scorer.setOption(key_value[0], key_value[1]);
		}
		
		SignificanceTest test = new RandomizationTest();
		
		DecimalFormat df = new DecimalFormat("0.#######################");
		
		System.out.println(df.format(test.test(scorer, actual, prediction1, prediction2)));
		
	}
	
}
