// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph;

import java.util.HashMap;
import java.util.Map;

import marmot.morph.io.Transformator;
import marmot.util.Counter;

public class EvalResult {

	private int total_;
	private int pos_correct_;
	
	private int morph_correct_;
	private int morph_missed_uncalled;

	private Counter<String> correct_counter_;
	private Counter<String> missing_counter_;
	private Counter<String> uncalled_counter_;
	private Counter<String> value_counter_;
	private Counter<String> correct_not_present_counter_;
	private boolean punct_;
	private Counter<String> vocab_;
	private HashDictionary mdict_;
	private Map<String, EvalResult> map_;
	private Counter<String> form_counter_;
	
	public EvalResult(boolean punct, Counter<String> vocab) {
		this(punct, vocab, null);
	}

	public EvalResult(boolean punct, Counter<String> vocab, HashDictionary mdict) {		
		total_ = 0;
		pos_correct_ = 0;
		morph_correct_ = 0;
		morph_missed_uncalled = 0;
		correct_counter_ = new Counter<String>();
		missing_counter_ = new Counter<String>();
		uncalled_counter_ = new Counter<String>();
		value_counter_ = new Counter<String>();
		correct_not_present_counter_ = new Counter<String>();
		punct_ = punct;
		vocab_ = vocab;
		mdict_ = mdict;
		if (mdict != null) {
			map_ = new HashMap<String, EvalResult>();
		}
		form_counter_ = new Counter<String>();
	}
	
	public void incrementCorrectNoPresentCounter(String cat) {
		correct_not_present_counter_.increment(cat, 1.0);
	}
	
	public void incrementMorphMissedUncalled() {
		morph_missed_uncalled ++;
	}

	
	public void incrementCorrectCounter(String cat) {
		correct_counter_.increment(cat, 1.0);
	}
	
	public void incrementMissingCounter(String cat) {
		missing_counter_.increment(cat, 1.0);
	}
	
	public void incrementUncalledCounter(String cat ) {
		uncalled_counter_.increment(cat, 1.0);
	}
	
	public void incrementValueCounter(String cat ) {
		value_counter_.increment(cat, 1.0);
	}
	
	
	public void incrementMorphCorrect() {
		morph_correct_ ++;
	}
	
	public void update(EvalToken token, int rareness) {
		if (punct_ && Transformator.isPunct(token.getForm())) {
			return;
		}
		
		form_counter_.increment(token.getForm(), 1.0);
		
		if (rareness < 0 || vocab_.count(token.getForm()) - 0.5 < rareness) {
			total_ += 1;
			if (token.posCorrect(this)) {
				pos_correct_++;
			}
			token.update(this);
		}
		
		if (mdict_ != null) {
			String form = token.getForm();
			int[] indexes = mdict_.getIndexes(form);
			if (indexes == null) {
				update("<UNK>", token, rareness);
			} else {
				for (int i : indexes) {
					String name = mdict_.getName(i);
					update(name, token, rareness);
				}
			}
		}
		
		
	}

	private void update(String name, EvalToken token, int rareness) {
		EvalResult result = map_.get(name);
		if (result == null) {
			result = new EvalResult(punct_, vocab_);
			map_.put(name, result);
			result.update(token, rareness);
		}
		
	}

	public void report(boolean verbose) {
		System.out.format("PoS: %d / %d = %g\n", pos_correct_, total_,
				pos_correct_ * 100. / total_);
		System.out.format("Morph: %d / %d = %g\n", morph_correct_, total_,
				morph_correct_ * 100. / total_);
		
		if (verbose) {
		
		System.out.format("Morph (-m/uc): %d / %d = %g\n", morph_correct_, total_ - morph_missed_uncalled,
				morph_correct_ * 100. / (total_ - morph_missed_uncalled));
		System.out.format("Morph (-pos): %d / %d = %g\n", morph_correct_, pos_correct_,
				morph_correct_ * 100. / pos_correct_);
		
		System.out.format("Correct\n");
		
		for (Map.Entry<String, Double> entry : correct_counter_.sortedEntries()) {
			String cat = entry.getKey();
			
			
			
			System.out.format("%s: %g / %d = %g", cat, entry.getValue(), total_,
					entry.getValue() * 100. / total_);
			
			double value = entry.getValue() - correct_not_present_counter_.count(cat);
			
			System.out.format(" (%g)\n", value * 100. / (pos_correct_ - correct_not_present_counter_.count(cat)));
			
			
		}
		System.out.format("\n");
		
		System.out.format("Uncalled\n");
		for (Map.Entry<String, Double> entry : uncalled_counter_.sortedEntries()) {
			System.out.format("%s: %g / %d = %g\n", entry.getKey(), entry.getValue(), total_,
					entry.getValue() * 100. / total_);
		}
		System.out.format("\n");

		System.out.format("Missing\n");
		for (Map.Entry<String, Double> entry : missing_counter_.sortedEntries()) {
			System.out.format("%s: %g / %d = %g\n", entry.getKey(), entry.getValue(), total_,
					entry.getValue() * 100. / total_);
		}
		System.out.format("\n");

		System.out.format("Most frequent errors\n");
		for (Map.Entry<String, Double> entry : value_counter_.sortedEntries(10)) {
			System.out.format("%s: %g\n", entry.getKey(), entry.getValue());
		}
		
		}
		
		if (mdict_ != null) {
			
			System.out.println("MDICT: ");
			
			for (Map.Entry<String, EvalResult> entry : map_.entrySet()) {
				System.out.println(entry.getKey());
				System.out.println(entry.getValue().form_counter_.sortedEntries(20));
				entry.getValue().report(verbose);
				System.out.println();
			}
		}
		
	}


	

}
