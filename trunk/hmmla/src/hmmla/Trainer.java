// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla;

import hmmla.decode.CoarseToFineDecoder;
import hmmla.decode.Decoder;
import hmmla.eval.Eval;
import hmmla.eval.Result;
import hmmla.hmm.HmmTrainer;
import hmmla.hmm.HmmTrainerFactory;
import hmmla.hmm.Model;
import hmmla.hmm.Smoother;
import hmmla.hmm.SmootherFactory;
import hmmla.io.PosReader;
import hmmla.io.Sentence;
import hmmla.splitmerge.ApproximativeLossEstimator;
import hmmla.splitmerge.ConcurrentEmTrainer;
import hmmla.splitmerge.EmTrainer;
import hmmla.splitmerge.ExactLossEstimator;
import hmmla.splitmerge.LossEstimator;
import hmmla.splitmerge.Merger;
import hmmla.splitmerge.SimpleEmTrainer;
import hmmla.splitmerge.Splitter;
import hmmla.util.BufferedIterable;
import hmmla.util.Ling;
import hmmla.util.Mapping;
import hmmla.util.RandomIterable;
import hmmla.util.SuffixTrie;

import java.util.Random;

public class Trainer {
	private Properties props_;
	private Random rng_;
	private Merger merger_;
	private Model model_;
	private Splitter splitter_;
	private EmTrainer em_trainer_;
	private HmmTrainer hmm_trainer_;
	private Iterable<Sentence> train_reader_;
	private Iterable<Sentence> test_reader_;
	private Smoother smoother_;

	public Trainer(Properties props) {
		props_ = props;
		rng_ = new Random(props.getSeed());

		Mapping map = null;

		if (props_.getUniversalPos()) {
			map = new Mapping(props_.getUniversalPosFile());
		}

		train_reader_ = new BufferedIterable<Sentence>(new PosReader(
				props_.getTrainFile(), map));

		if (props_.getTest()) {
			test_reader_ = new BufferedIterable<Sentence>(new PosReader(
					props_.getTestFile(), map));
		}

		model_ = new Model(train_reader_, props_);
		if (!props.getLanguage().equals("en")) {
			SuffixTrie trie = Ling.getSuffixes(new PosReader(props_
					.getTrainFile()));
			model_.setSuffixTrie(trie);
		}

		// Splitter setup.
		splitter_ = new Splitter(props_.getRandomness(), rng_);

		// EM trainer setup.
		if (props_.getNumThreads() == 1) {
			em_trainer_ = new SimpleEmTrainer();
		} else {
			em_trainer_ = new ConcurrentEmTrainer(props_.getNumThreads());
		}

		hmm_trainer_ = HmmTrainerFactory.getTrainer(props_);

		// Merger setup.
		LossEstimator estimator;
		if (props_.getExactLoss()) {
			estimator = new ExactLossEstimator(em_trainer_, hmm_trainer_);
		} else {
			estimator = new ApproximativeLossEstimator(hmm_trainer_);
		}
		merger_ = new Merger(estimator);

		smoother_ = SmootherFactory.getSmoother(props_);
	}

	private void runEm() {
		Iterable<Sentence> em_reader;
		if (props_.getSample()) {
			em_reader = new RandomIterable<Sentence>(train_reader_,
					props_.getSamplingFraction(), rng_);
		} else {
			em_reader = train_reader_;
		}
		int step = 0;
		while (step < props_.getEmSteps()) {
			em_trainer_.estep(hmm_trainer_, model_, em_reader);
			model_.setStatistics(smoother_.smooth(model_));
			step += 1;
		}
	}

	private void split() {
		splitter_.split(model_);
		runEm();
	}

	private void merge() {
		merger_.merge(model_, train_reader_, props_.getMergeFactor());
		runEm();
	}

	private void run() {
		
		eval();

		
		
		while (model_.getNumTags() < props_.getNumTags()) {
			split();

			if (props_.getSample()) {
				em_trainer_.estep(hmm_trainer_, model_, train_reader_);
			}

			if (props_.getMerge()) {
				merge();
			}

			if (props_.getSample()) {
				em_trainer_.estep(hmm_trainer_, model_, train_reader_);
			}

			eval();
			
			if (props_.getDumpIntermediateModels()) {
				model_.saveToFile(props_.getIntermediateModelName(model_));
			}
		}
		
		model_.saveToFile(props_.getModelFile());
	}

	private void eval() {
		Decoder decoder = new CoarseToFineDecoder(model_, hmm_trainer_, true,
				false);
		
		int tagsize = model_.getTagTable().size() - 1;
		System.err.format("Tag size: %d", tagsize);
		
		if (test_reader_ != null) {
			Result result = Eval.eval(decoder, test_reader_, model_);
			
			System.err.format(" Acc: %s", result.toString());
		}
		System.err.format("\n");
	}

	public static void main(String[] args) {
		Properties props = new Properties();
		
		if (args.length == 0) {
			props.usage();
			return;
		}
		
		props.setPropertiesFromStrings(args);

		props.check(Trainer.class.getSimpleName());

		props.writePropertiesToFile(props.getModelFile() + ".props");
		
		Trainer pipeline = new Trainer(props);
		pipeline.run();
	}

}
