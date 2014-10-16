// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla;

import hmmla.decode.CoarseToFineDecoder;
import hmmla.decode.Decoder;
import hmmla.decode.SupervisedDecoder;
import hmmla.eval.Eval;
import hmmla.eval.Result;
import hmmla.hmm.HmmTrainer;
import hmmla.hmm.HmmTrainerFactory;
import hmmla.hmm.Model;
import hmmla.io.PosReader;
import hmmla.io.PosWriter;
import hmmla.io.Sentence;
import hmmla.util.Mapping;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Tagger {

	public static void main(String[] args) throws IOException {	
		Properties props = new Properties();
		props.setPropertiesFromStrings(args);

		props.check(Tagger.class.getSimpleName());
		
		Model model = Model.loadFromFile(props.getModelFile());
		model.setProperties(props);
		
		boolean refine = props.getRefine();
		boolean toplevel = props.getCoarseDecoder();

		Mapping upos = null;
		if (props.getUniversalPos()) {
			upos = new Mapping(props.getUniversalPosFile());
		}

		Iterable<Sentence> reader = new PosReader(props.getTestFile(),
				upos);

		HmmTrainer trainer = HmmTrainerFactory.getTrainer(props);

		Decoder decoder;
		if (refine) {
			decoder = new SupervisedDecoder(model, trainer.train(model));
		} else {
			decoder = new CoarseToFineDecoder(model, trainer, toplevel, false);
		}

		Result result = new Result();

		PosWriter writer = new PosWriter(new FileWriter(props.getPredFile()));
		
		for (Sentence sentence : reader) {
			List<String> tags = decoder.bestPath(sentence);
			
			if (toplevel && ! refine && props.getTest())
				result.increment(Eval.eval(tags, sentence, model));
			writer.write(sentence, tags);
		}

		if (toplevel && !refine && props.getTest())
			System.err.println(result);
		
		writer.close();
	}
	
}
