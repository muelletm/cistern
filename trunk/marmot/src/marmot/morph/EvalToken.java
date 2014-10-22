// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EvalToken {

	private String gold_pos_;
	private String pred_pos_;
	private String form_;
	private Set<String> cats_;

	private Map<String, String> gold_feats_;
	private Map<String, String> pred_feats_;
	private Set<String> common_cats_;

	public EvalToken(String form, String gold_pos, String pred_pos,
			String gold_feats, String pred_feats, Set<String> cats) {
		cats_ = cats;
		form_ = form;
		gold_pos_ = gold_pos;
		pred_pos_ = pred_pos;
		gold_feats_ = splitFeats(gold_feats, cats_);
		pred_feats_ = splitFeats(pred_feats, cats_);
		setCommonCats();
	}

	public static Map<String, String> splitFeats(String feats, Set<String> cats) {
		Map<String, String> map = new HashMap<String, String>();

		if (feats.equals("_")) {
			return map;
		}

		for (String feat : feats.split("\\|")) {
			int index = feat.indexOf("=");

			try {
				String key = feat.substring(0, index);
				String value = feat.substring(index + 1);

				if (cats != null)
					cats.add(key);
				map.put(key, value);

			} catch (StringIndexOutOfBoundsException e) {
				throw new RuntimeException("Can't split: " + feat + " " + feats);
			}

		}

		return map;
	}

	private void setCommonCats() {
		common_cats_ = new HashSet<String>(gold_feats_.keySet());
		common_cats_.retainAll(pred_feats_.keySet());
	}

	public String getForm() {
		return form_;
	}

	public boolean posCorrect(EvalResult result) {
		boolean correct = gold_pos_.equals(pred_pos_);
		
		if (!correct) {
			String signature = String.format("pos %s %s", gold_pos_, pred_pos_);
			result.incrementValueCounter(signature);
		}
		
		return correct;
	}

	public void update(EvalResult result) {

		Set<String> not_present = new HashSet<String>(cats_);
		not_present.removeAll(gold_feats_.keySet());
		not_present.removeAll(pred_feats_.keySet());

		for (String cat : not_present) {
			result.incrementCorrectNoPresentCounter(cat);
			result.incrementCorrectCounter(cat);
		}

		for (String cat : common_cats_) {
			String gold_value = gold_feats_.get(cat);
			String pred_value = pred_feats_.get(cat);

			if (gold_value.equals(pred_value)) {
				result.incrementCorrectCounter(cat);
			} else {
				String signature = String.format("%s %s %s", cat, gold_value, pred_value);
				result.incrementValueCounter(signature);
			}
		}

		Set<String> uncalled = new HashSet<String>(pred_feats_.keySet());
		uncalled.removeAll(common_cats_);

		for (String cat : uncalled) {
			result.incrementUncalledCounter(cat);
		}

		Set<String> missed = new HashSet<String>(gold_feats_.keySet());
		missed.removeAll(common_cats_);

		for (String cat : missed) {
			result.incrementMissingCounter(cat);
		}

		assert not_present.size() + uncalled.size() + missed.size()
				+ common_cats_.size() == cats_.size();

		if (gold_feats_.equals(pred_feats_)) {
			result.incrementMorphCorrect();
		} else  if (!(uncalled.isEmpty() && missed.isEmpty())){
			result.incrementMorphMissedUncalled();
		}
	}

}
