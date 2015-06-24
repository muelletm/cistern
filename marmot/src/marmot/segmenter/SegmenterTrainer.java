package marmot.segmenter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

import cc.mallet.optimize.LimitedMemoryBFGS;
import cc.mallet.optimize.OptimizationException;
import cc.mallet.optimize.Optimizer;

import marmot.util.DynamicWeights;
import marmot.util.FileUtils;
import marmot.util.Numerics;

public class SegmenterTrainer {

	private int num_iterations_ = 15;
	private boolean averaging_ = true;
	private boolean crf_mode_ = false;
	private double penalty_ = 0.0;
	private Random random_ = new Random(42);
	private int max_character_window_ = 3;
	private boolean use_segment_context_ = true;
	private boolean use_character_feature_ = true;
	private List<String> dictionary_paths_;
	private String lang_;
	
	public SegmenterTrainer(String lang) {
		lang_ = lang;
		dictionary_paths_ = new LinkedList<>();
	}

	public Segmenter train(Collection<Word> words) {
		SegmenterModel model = new SegmenterModel();

		model.init(lang_, words, max_character_window_, use_segment_context_, use_character_feature_, dictionary_paths_);

		if (crf_mode_) {
			System.err.println("Training CRF");
			run_crf(model, words);
		}
		else
			System.err.println("Training Perceptron");
			run_perceptron(model, words);

		model.setFinal();

		Segmenter segmenter = new Segmenter(model);
		return segmenter;
	}

	public SegmenterTrainer addDictionary(String path) {
		dictionary_paths_.add(path);
		return this;
	}	
	
	private void run_crf(SegmenterModel model, Collection<Word> words) {
		SemiCrfObjective objective = new SemiCrfObjective(model, words,
				penalty_);
		objective.init();

		Optimizer optimizer = new LimitedMemoryBFGS(objective);
		Logger.getLogger(optimizer.getClass().getName()).setLevel(Level.OFF);

		try {
			optimizer.optimize(1);

			for (int i = 0; i < 200 && !optimizer.isConverged(); i++) {
				optimizer.optimize(1);
			}

		} catch (IllegalArgumentException e) {
		} catch (OptimizationException e) {
		}
	}

	private void run_perceptron(SegmenterModel model, Collection<Word> words) {
		DynamicWeights weights = new DynamicWeights(null);
		DynamicWeights sum_weights = null;
		if (averaging_) {
			sum_weights = new DynamicWeights(null);
		}

		model.setWeights(weights);

		SegmentationDecoder decoder = new SegmentationDecoder(model);

		int number;

		List<Word> word_array = new ArrayList<>(words);
		for (int iter = 0; iter < num_iterations_; iter++) {

			number = 0;

			Collections.shuffle(word_array, random_);
			for (Word word : word_array) {
				SegmentationInstance instance = model.getInstance(word);
				SegmentationResult result = decoder.decode(instance);

				double score = result.getScore();
				double exact_score = model.getScore(instance, result);
				assert Numerics.approximatelyEqual(score, exact_score) : String
						.format("%d %d", score, exact_score);

				if (!result.isCorrect(instance)) {

					SegmentationResult closest_result = Scorer.closest(result, instance.getResults(), instance.getLength());
					
					model.update(instance, result, -1.);
					model.update(instance, closest_result, +1.);

					if (averaging_) {
						double amount = word_array.size() - number;
						assert amount > 0;
						model.setWeights(sum_weights);
						model.update(instance, result, -amount);
						model.update(instance, closest_result,
								+amount);
						model.setWeights(weights);
					}

				}

				number++;
			}

			if (averaging_) {
				double weights_scaling = 1. / ((iter + 1.) * word_array.size());
				double sum_weights_scaling = (iter + 2.) / (iter + 1.);
				for (int i = 0; i < weights.getLength(); i++) {
					weights.set(i, sum_weights.get(i) * weights_scaling);
					sum_weights
							.set(i, sum_weights.get(i) * sum_weights_scaling);
				}
			}
		}
	}
	
	public static void main(String[] args) throws JSAPException, IOException {
		
		FlaggedOption opt;
		JSAP jsap = new JSAP();

		opt = new FlaggedOption("dir").setRequired(true).setLongFlag("dir");
		jsap.registerParameter(opt);
		
		opt = new FlaggedOption("out").setRequired(true).setLongFlag("out");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("lang").setRequired(true).setLongFlag("lang");
		jsap.registerParameter(opt);
		
		opt = new FlaggedOption("crf-mode").setStringParser(JSAP.BOOLEAN_PARSER).setLongFlag("crf-mode").setDefault("false");
		jsap.registerParameter(opt);
		
		opt = new FlaggedOption("tag-level").setStringParser(JSAP.INTEGER_PARSER).setLongFlag("tag-level").setDefault("0");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("num-chunks").setStringParser(JSAP.INTEGER_PARSER).setLongFlag("num-chunks").setDefault("10");
		jsap.registerParameter(opt);

		JSAPResult config = jsap.parse(args);
		
        if (!config.success()) {
        	for (Iterator<?> errs = config.getErrorMessageIterator();
                    errs.hasNext();) {
                System.err.println("Error: " + errs.next());
            }
            System.err.println("Usage: ");
            System.err.println(jsap.getUsage());
            System.err.println(jsap.getHelp());
            System.err.println();
            System.exit(1);
        }
        
        String dir = config.getString("dir");
        String out = config.getString("out");
        String lang = config.getString("lang");
		int tag_level = config.getInt("tag-level");
		int num_chunks = config.getInt("num-chunks");
		boolean crf_mode = config.getBoolean("crf-mode");
		Logger logger = Logger.getLogger(SegmenterTrainer.class.getName());
		
		String global_trainfile = String.format(
				"%s/%s/trn", dir, lang);
		
		SegmentationDataReader global_reader = new SegmentationDataReader(global_trainfile, lang, tag_level);

		double score_sum = 0.0;
		
		int start_chunk = 0;
		int end_chunk = num_chunks;
		
		if (num_chunks < 10) {
			start_chunk = num_chunks;
			end_chunk = num_chunks + 1;
			num_chunks = 1;
		}
		
		for (int i = start_chunk; i < end_chunk; i++) {
			System.err.format("chunk: %d\n", i);
			
			String trainfile = String.format(
					"%s/%s/%d.trn", dir, lang, i);
			String testfile = String.format(
					"%s/%s/%d.tst", dir, lang, i);
			
			List<Word> train = new SegmentationDataReader(trainfile, lang, 0)
					.getData();
			train = global_reader.map(train);
			
			List<Word> test = new SegmentationDataReader(testfile, lang, 0)
					.getData();
			test = global_reader.map(test);

			SegmenterTrainer trainer = new SegmenterTrainer(lang);
			
			trainer.addDictionary(String.format(
					"%s/%s/wiktionary.txt", dir, lang));
			trainer.addDictionary(String.format(
					"%s/%s/aspell.txt", dir, lang));
			trainer.addDictionary(String.format(
					"%s/%s/wordlist.txt", dir, lang));
			trainer.setCrfMode(crf_mode);
			
			Segmenter segmenter = trainer.train(train);
			Scorer scorer = new Scorer();
			scorer.eval(test, segmenter);
			logger.info(String.format("%s F1 of chunk %d: %s\n", lang, i,
					scorer.report()));
			score_sum += scorer.getFscore();

			FileUtils.mkDir(String.format("%s/%s", out, lang));
			
			String outfile = String.format("%s/%s/%d.tst", out, lang, i);
			segmenter.segmentToFile(outfile, test);
			
		}
		
		logger.info(String.format("%s Average F1: %g\n", lang, score_sum
				/ num_chunks));
		
		
	}

	public void setCrfMode(boolean crf_mode) {
		crf_mode_ = crf_mode;
	}

}
