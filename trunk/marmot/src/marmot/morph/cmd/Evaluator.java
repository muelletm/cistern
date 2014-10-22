// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.cmd;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

import marmot.core.Sequence;
import marmot.core.Token;
import marmot.morph.EvalResult;
import marmot.morph.EvalToken;
import marmot.morph.HashDictionary;
import marmot.morph.MorphDictionary;
import marmot.morph.MorphTagger;
import marmot.morph.Word;
import marmot.morph.io.SentenceReader;
import marmot.util.Counter;
import marmot.util.LineIterator;
import marmot.util.StringUtils;

public class Evaluator {

	private Counter<String> vocab_;
	private HashDictionary mdict_;
	private boolean normalize_;

	public Evaluator(String trainfile, boolean normalize) {
		this(trainfile, normalize , null);
	}

	
	public Evaluator(String trainfile, boolean normalize, String mdict) {
		normalize_ = normalize;
		
		if (trainfile != null) {
			readVocab(trainfile);
		}
		
		if (mdict != null) {
			readMdict(mdict);
		}
	}

	private void readMdict(String mdict_file) {
		mdict_ = (HashDictionary) MorphDictionary.create(mdict_file);
	}


	public static void main(String args[]) throws JSAPException {

		FlaggedOption opt;
		JSAP jsap = new JSAP();

		opt = new FlaggedOption("train-file").setRequired(true).setLongFlag(
				"train-file");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("test-file").setRequired(true).setLongFlag(
				"test-file");
		jsap.registerParameter(opt);
		
		opt = new FlaggedOption("mdict").setRequired(false).setLongFlag(
				"mdict");
		
		jsap.registerParameter(opt);

		opt = new FlaggedOption("rareness").setRequired(false)
				.setLongFlag("rareness").setStringParser(JSAP.INTEGER_PARSER)
				.setDefault("-1");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("punct").setRequired(false)
				.setLongFlag("punct").setStringParser(JSAP.BOOLEAN_PARSER)
				.setDefault("false");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("normalize").setRequired(false)
				.setLongFlag("normalize").setStringParser(JSAP.BOOLEAN_PARSER)
				.setDefault("false");
		jsap.registerParameter(opt);
		
		opt = new FlaggedOption("verbose").setRequired(false)
				.setLongFlag("verbose").setStringParser(JSAP.BOOLEAN_PARSER)
				.setDefault("false");
		jsap.registerParameter(opt);

		JSAPResult config = jsap.parse(args);

		if (!config.success()) {
			for (Iterator<?> errs = config.getErrorMessageIterator(); errs
					.hasNext();) {
				System.err.println("Error: " + errs.next());
			}
			System.err.println("Usage: ");
			System.err.println(jsap.getUsage());
			System.err.println(jsap.getHelp());
			System.err.println();
			System.exit(1);
		}

		Evaluator eval = new Evaluator(config.getString("train-file"), config.getBoolean("normalize"), config.getString("mdict"));

		EvalResult result = eval.eval(config.getString("test-file"), config.getInt("rareness"),
				config.getBoolean("punct"));
		
		result.report(config.getBoolean("verbose"));
	}

	public EvalResult eval(String testfile, int rareness, boolean punct) {

		LineIterator iterator = new LineIterator(testfile);

		List<EvalToken> tokens = new LinkedList<EvalToken>();

		Set<String> cats = new HashSet<String>();

		while (iterator.hasNext()) {
			List<String> line = iterator.next();

			if (!line.isEmpty()) {

				String form = line.get(1);
				String gpos = line.get(4);
				String ppos = line.get(5);

				String gfeats = line.get(6);
				String pfeats = line.get(7);

				if (normalize_) {
					form = StringUtils.normalize(form, false);
				}
				
				EvalToken token = new EvalToken(form, gpos, ppos, gfeats,
						pfeats, cats);
				tokens.add(token);
			}
		}

		return eval(tokens, rareness, punct);
	}

	private EvalResult eval(List<EvalToken> tokens, int rareness, boolean punct) {
		EvalResult result = new EvalResult(punct, vocab_, mdict_);

		for (EvalToken token : tokens) {
			result.update(token, rareness);
		}

		return result;
	}

	private void readVocab(String trainfile) {
		vocab_ = new Counter<String>();

		SentenceReader reader = new SentenceReader(trainfile);
		for (Sequence sequence : reader) {
			for (Token token : sequence) {
				Word word = (Word) token;
				
				String form = word.getWordForm();
				if (normalize_) {
					form = StringUtils.normalize(form, false);
				}
				
				vocab_.increment(form, 1.0);
			}
		}

	}


	public EvalResult eval(MorphTagger tagger, String filename, int rareness,
			boolean punct) {
		List<EvalToken> tokens = new LinkedList<EvalToken>();
		Set<String> cats = new HashSet<String>();
		
		for (Sequence sequence : new SentenceReader(filename)) {
			List<List<String>> tags = tagger.tag(sequence);
			
			for (int i=0; i<tags.size(); i++) {
				List<String> tag_list = tags.get(i);
				Word word = (Word) sequence.get(i);
				
				String ppos = tag_list.get(0);
				
				String pfeats = "_";
				if (tag_list.size() > 1) {
					pfeats = tag_list.get(1);
				}
				
				String form = word.getWordForm();
				if (normalize_) {
					form = StringUtils.normalize(form, false);
				}
				
				EvalToken eval_token = new EvalToken(form, word.getPosTag(), ppos, word.getMorphTag(),
						pfeats, cats);
				
				tokens.add(eval_token);	
			}
		}
	
		return eval(tokens, rareness, punct);
	}

}
 