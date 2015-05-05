// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.cmd;

import marmot.morph.MorphTagger;
import marmot.morph.MorphWeightVector;
import marmot.util.FileUtils;
import marmot.util.Sys;

public class MemoryProfiler {
	
	static final boolean compress = true;

	public static void main(String[] args) {
		MorphTagger tagger = FileUtils.loadFromFile(args[0]);
		long tagger_size = Sys.getUsedMemoryInBytes(tagger, compress);
		System.out.format("Tagger: %d bits\n", tagger_size);
		MorphWeightVector vector = (MorphWeightVector) tagger.getWeightVector();
		long weights_size = Sys.getUsedMemoryInBytes(vector.getWeights(), compress);
		System.out.format("Weights : %d bits = %g%%\n", weights_size, weights_size * 100. / tagger_size);
	}
}
