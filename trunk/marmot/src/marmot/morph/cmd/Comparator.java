// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.cmd;

import java.util.List;

import marmot.core.Options;
import marmot.morph.MorphEvaluator;
import marmot.morph.MorphOptions;
import marmot.morph.MorphResult;
import marmot.util.LineIterator;


public class Comparator {

	public static final int num_trials_ = 5;
	public static final int seed_ = 1000;

	private MorphOptions getDefaultOptions(String trainfile, String devfile) {
		MorphOptions opts = new MorphOptions();
		opts.setProperty(Options.PENALTY, "0.0");
		opts.setProperty(MorphOptions.TRAIN_FILE, trainfile);
		opts.setProperty(MorphOptions.TEST_FILE, devfile);
		opts.setProperty(MorphOptions.SHAPE, "false");
		return opts;
	}

	public static void main(String[] args) {

		if (args.length != 1) {
			System.err.format("Usage: Comparator propfile");
			System.exit(1);
		}
		
		String propfile = args[0];
		LineIterator iter = new LineIterator(propfile);
		
		while (iter.hasNext()) {
			
			List<String> line = iter.next();
			
			if (line.size() == 3) {
				
				String name = line.get(0);
				String trainfile = line.get(1);
				String devfile = line.get(2);

				System.err.format("%s (%s %s)\n", name, trainfile, devfile);

				Comparator c = new Comparator();

				c.run_baseline(trainfile, devfile);
				c.run_penalty(trainfile, devfile);
				c.run_shape(trainfile, devfile);
				c.run_iters(trainfile, devfile);
			}			
		}
	}

	private void run_iters(String trainfile, String devfile) {
		MorphOptions opts = getDefaultOptions(trainfile, devfile);
		opts.setProperty(Options.NUM_ITERATIONS, Integer.toString(opts.getNumIterations() * 2));
		run("Iters", opts);		
	}

	private void run_penalty(String trainfile, String devfile) {
		MorphOptions opts = getDefaultOptions(trainfile, devfile);
		opts.setProperty(MorphOptions.PENALTY, "0.1");
		run("Penalty", opts);

	}

	private void run_shape(String trainfile, String devfile) {
		MorphOptions opts = getDefaultOptions(trainfile, devfile);
		opts.setProperty(MorphOptions.SHAPE, "true");
		run("Shape", opts);
	}

	private void run(String name, MorphOptions opts) {
		try {
		MorphResult result = MorphEvaluator.eval(opts, num_trials_, seed_);
		System.err.format("%s: %g %g %ds\n", name, result.getTokenAccuracy(), result.getOovTokenAccuracy(), result.time / 1000);
		} catch (Exception e) {
			String s = e.toString().replace('\n', ' ');
			System.err.format("%s: %s\n", name, s);	
		}
	}
	
	private void run_baseline(String trainfile, String devfile) {
		MorphOptions opts = getDefaultOptions(trainfile, devfile);
		run("Baseline", opts);
	}

}
