// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.cmd;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import marmot.core.Sequence;
import marmot.morph.Word;
import marmot.morph.io.SentenceReader;

public class SoftEvaluator {

	enum Mode {
		Jaccard,
		Acc,
		Cosine,
		Fscore
	};
	
	class Result {

		double sim = 0;
		private Mode _mode;

		public Result(Mode mode) {
			_mode = mode;
		}

		protected Map<String, Double> toVector(String pos, String morph) {
			Map<String, Double> map = new HashMap<String, Double>();

			map.put(pos, 1.0);
			if (morph != null && !morph.equals("_")) {
				for (String morpheme : morph.split("\\|")) {
					map.put(morpheme, 1.0);
				}
			}
			
			// Normalize
			double norm = 0;
			for (double d : map.values()) {
				norm += d*d;
			}
			norm = Math.sqrt(norm);
			
			for (Map.Entry<String, Double> entry : map.entrySet()) {
				entry.setValue(entry.getValue() / norm);
			}

			return map;
		}
		
		protected double calc_jaccard(String gold_pos, String gold_morph,
				String pred_pos, String pred_morph) {
			return jaccard(toSet(gold_pos, gold_morph), toSet(pred_pos, pred_morph));
		}
		
		protected double calc_fscore(String gold_pos, String gold_morph,
				String pred_pos, String pred_morph) {
			return fscore(toSet(gold_pos, gold_morph), toSet(pred_pos, pred_morph));
		}
		
		private double fscore(Set<String> set, Set<String> set2) {
			Set<String> intersection = new HashSet<String>(set);
			intersection.retainAll(set2);
			
			
			
			assert intersection.size() <= set2.size();
			
			double p = intersection.size() / (double) set2.size();
			double r = intersection.size() / (double) set.size();
			
			assert (p <= 1.0);
			assert (r <= 1.0);
			
			if (p < 1e-10)
				return 0;
			
			if (r < 1e-10)
				return 0;
			
			
			double f = p*r*2 / (p + r);
						
			assert (f <= 1.0 + 1e-5);
					
			return 100 * 1.0 ;
		}
		
		private double jaccard(Set<String> set, Set<String> set2) {
			Set<String> intersection = new HashSet<String>(set);
			intersection.retainAll(set2);
			
			Set<String> union = new HashSet<String>(set);
			union.addAll(set2);
			
			double score = intersection.size() / (double) union.size();
			
//			if (score < 0.99) {
//						System.err.println(set + " " + set2 + " " + intersection + " " + union + " "  + score);
//			}
			
			return score;
		}

		private Set<String> toSet(String pos, String morph) {
			Set<String> set = new HashSet<String>();

			set.add("POS=" + pos);
			if (morph != null && !morph.equals("_")) {
				for (String morpheme : morph.split("\\|")) {
					set.add(morpheme);
				}
			}
		
			return set;
		}

		protected double cosineSim(Map<String, Double> vec, Map<String, Double> vec2) {
			double sim = 0;
			
			for (Map.Entry<String, Double> entry : vec.entrySet()) {
				Double d2 = vec2.get(entry.getKey());
				
				if (d2 != null) {
					sim += entry.getValue() * d2;
				}
			}
			
			return sim * 100.;
		}

		public void eval(Word gold_token, Word pred_token, double factor) {
			String gold_pos = gold_token.getPosTag();
			String pred_pos = pred_token.getPosTag();

			String gold_morph = gold_token.getMorphTag();
			String pred_morph = pred_token.getMorphTag();
		
			double pair_sim = 0.0;
			switch (_mode) {
			case Acc:
				pair_sim = calc_acc(gold_pos, gold_morph, pred_pos, pred_morph);
				break;
			case Jaccard:
				pair_sim = calc_jaccard(gold_pos, gold_morph, pred_pos, pred_morph);
				break;
			case Cosine:
				pair_sim = calc_cosineSim(gold_pos, gold_morph, pred_pos, pred_morph);
				break;
			case Fscore:
				pair_sim = calc_fscore(gold_pos, gold_morph, pred_pos, pred_morph);
				break;

			default:
				System.err.println("What?");
			}
			
			sim += pair_sim * factor;
		}

		private double calc_acc(String gold_pos, String gold_morph, String pred_pos,
				String pred_morph) {
			if (gold_pos.equals(pred_pos) && (gold_morph == pred_morph || gold_morph.equals(pred_morph))) {
				return 100.0;
			}
			return 0.0;
		}

		private double calc_cosineSim(String gold_pos, String gold_morph,
				String pred_pos, String pred_morph) {
			return cosineSim(toVector(gold_pos, gold_morph), toVector(pred_pos, pred_morph));
		}

		public String report() {
			return String.format("%s: %g", _mode.toString(), sim);
		}

	}

	void eval(Sequence gold_sentence, Sequence pred_sentence, Result result, int num_tokens) {
		assert gold_sentence.size() == pred_sentence.size();

		for (int i = 0; i < gold_sentence.size(); i++) {
			Word gold_token = (Word) gold_sentence.get(i);
			Word pred_token = (Word) pred_sentence.get(i);
			result.eval(gold_token, pred_token, 1. / num_tokens);
		}
	}
	
	void eval(String pred_file, Result result) {
		Iterable<Sequence> gold_sentences = new SentenceReader("form-index=1,tag-index=4,morph-index=6," + pred_file);
		Iterable<Sequence> pred_sentences = new SentenceReader("form-index=1,tag-index=5,morph-index=7," + pred_file);

		eval(gold_sentences, pred_sentences, result);
	}

	void eval(Iterable<Sequence> gold_sentences,
			Iterable<Sequence> pred_sentences, Result result) {	
		int num_tokens = 0;
		
		for (Sequence seq : gold_sentences) {
			num_tokens += seq.size();
		}
		
		Iterator<Sequence> gold_iter = gold_sentences.iterator();
		Iterator<Sequence> pred_iter = pred_sentences.iterator();
		
		while (gold_iter.hasNext()) {
			eval(gold_iter.next(), pred_iter.next(), result, num_tokens);
		}
		
		assert !pred_iter.hasNext();
	}
	
	public static void main(String[] args) {
		SoftEvaluator evaluator = new SoftEvaluator();
		
		Result result = evaluator.new Result(Mode.Acc);
		evaluator.eval(args, result);
		System.out.print(result.report());
		
		result = evaluator.new Result(Mode.Jaccard);
		evaluator.eval(args, result);
		System.out.println(" " + result.report());
				
//		result = evaluator.new Result(Mode.Cosine);
//		evaluator.eval(args, result);
//		System.out.println(result.report());
		
	}

	public void eval(String[] pred_files, Result result) {
		for (String pred_file : pred_files) {
			eval(pred_file, result);
		}
		
		result.sim = result.sim / pred_files.length;
	}

}
