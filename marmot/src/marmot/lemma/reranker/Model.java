package marmot.lemma.reranker;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import marmot.core.Feature;
import marmot.lemma.LemmaCandidate;
import marmot.lemma.LemmaCandidateSet;
import marmot.lemma.toutanova.EditTreeAligner;
import marmot.util.Converter;
import marmot.util.DynamicWeights;
import marmot.util.Encoder;
import marmot.util.SymbolTable;
import marmot.util.edit.EditTree;

public class Model implements Serializable {

	private double[] weights_;
	private SymbolTable<String> form_table_;
	private SymbolTable<String> lemma_table_;
	private SymbolTable<String> pos_table_;
	private SymbolTable<Feature> feature_table_;
	private SymbolTable<Character> char_table_;
	private SymbolTable<EditTree> tree_table_;
	private EditTreeAligner aligner_;
	
	private static final int max_window = 5;
	private static final int window_bits_ = Encoder.bitsNeeded(max_window);
	private static final int max_affix_length_ = 10;
	
	private static final int lemma_feature_ = 0;
	private static final int lemma_form_feature_ = 1;
	private static final int align_feature_ = 2;
	private static final int align_copy_feature_ = 3;
	private static final int align_window_feature_ = 4;
	private static final int tree_feature_ = 5;
	private static final int affix_feature_ = 6;
	private static final int feature_bits_ = Encoder.bitsNeeded(6);
	
	private int lemma_bits_;
	private int form_bits_;
	private int pos_bits_;
	private int char_bits_;
	private int tree_bits_;
	private static final int length_bits_ = Encoder.bitsNeeded(2 * max_window + 10);


	public void init(List<RerankerInstance> instances, Random random,
			EditTreeAligner aligner) {
		aligner_ = aligner;
		
		

		form_table_ = new SymbolTable<>();
		lemma_table_ = new SymbolTable<>();
		pos_table_ = new SymbolTable<>();
		feature_table_ = new SymbolTable<>();
		char_table_ = new SymbolTable<>();
		tree_table_ = new SymbolTable<>();

		for (RerankerInstance instance : instances) {
			fillTables(instance, instance.getCandidateSet());
		}

		form_bits_ = Encoder.bitsNeeded(form_table_.size() - 1);
		lemma_bits_ = Encoder.bitsNeeded(lemma_table_.size() - 1);
		pos_bits_ = Encoder.bitsNeeded(pos_table_.size() - 1);
		char_bits_ = Encoder.bitsNeeded(char_table_.size());
		tree_bits_ = Encoder.bitsNeeded(tree_table_.size() -1);

		for (RerankerInstance instance : instances) {
			addIndexes(instance, instance.getCandidateSet(), true);
		}
		
		weights_ = new double[feature_table_.size()];
	}

	private void fillTables(RerankerInstance instance, LemmaCandidateSet set) {
		String form = instance.getInstance().getForm();
		form_table_.insert(form);

		instance.getFormChars(char_table_, true);

		String postag = instance.getInstance().getPosTag();
		pos_table_.insert(postag);

		for (Map.Entry<String, LemmaCandidate> candidate_pair : set) {
			String lemma = candidate_pair.getKey();
			LemmaCandidate candidate = candidate_pair.getValue();
			
			candidate.getLemmaChars(char_table_, lemma, true);
			candidate.getAlignment(aligner_, form, lemma);
			candidate.getTreeIndex(aligner_.getBuilder(), form, lemma, tree_table_, true);
			
			
			lemma_table_.insert(lemma);
		}
	}

	public void addIndexes(RerankerInstance instance, LemmaCandidateSet set,
			boolean insert) {
		String form = instance.getInstance().getForm();
		int form_index = form_table_.toIndex(form, -1);
		String postag = instance.getInstance().getPosTag();
		int pos_index = pos_table_.toIndex(postag, -1);

		int[] form_chars = instance.getFormChars(char_table_, insert);

		Encoder encoder = new Encoder(10);

		for (Map.Entry<String, LemmaCandidate> candidate_pair : set) {
			String lemma = candidate_pair.getKey();
			int lemma_index = lemma_table_.toIndex(lemma, -1);

			LemmaCandidate candidate = candidate_pair.getValue();

			int[] lemma_chars = candidate.getLemmaChars(char_table_, lemma,
					insert);

			List<Integer> list = new LinkedList<>();

			if (lemma_index >= 0) {
				encoder.append(lemma_feature_, feature_bits_);
				encoder.append(lemma_index, lemma_bits_);
				addFeature(encoder, list, insert, pos_index);
			}

			if (lemma_index >= 0 && form_index >= 0) {
				encoder.append(lemma_form_feature_, feature_bits_);
				encoder.append(lemma_index, lemma_bits_);
				encoder.append(form_index, form_bits_);
				addFeature(encoder, list, insert, pos_index);
			}

			List<Integer> alignment = candidate.getAlignment(aligner_, form,
					lemma);

			addAlignmentIndexes(form_chars, lemma_chars, pos_index, alignment,
					encoder, list, insert);
			
			int tree_index = candidate.getTreeIndex(aligner_.getBuilder(), form, lemma, tree_table_, insert);
			
			if (tree_index >= 0) {
				encoder.append(tree_feature_, feature_bits_);
				encoder.append(tree_index, tree_bits_);
				addFeature(encoder, list, insert, pos_index);
				
				encoder.append(tree_feature_, feature_bits_);
				encoder.append(tree_index, tree_bits_);
				addPrefixFeatures(form_chars, encoder, list, insert, pos_index);
				encoder.reset();
				
				encoder.append(tree_feature_, feature_bits_);
				encoder.append(tree_index, tree_bits_);
				addSuffixFeatures(form_chars, encoder, list, insert, pos_index);
				encoder.reset();
			}
			
			addAffixIndexes(lemma_chars, encoder, list, insert, pos_index);

			candidate.setFeatureIndexes(Converter.toIntArray(list));
		}
	}

	private void addPrefixFeatures(int[] chars, Encoder encoder, List<Integer> list, boolean insert, int pos_index) {
		encoder.append(false);
		for (int i=0; i < Math.min(chars.length, max_affix_length_); i++) {
			int c = chars[i];
			if (c < 0)
				return;
			encoder.append(c, char_bits_);
			addFeature(encoder, list, insert, pos_index, false);
		}
	}
	
	private void addSuffixFeatures(int[] chars, Encoder encoder, List<Integer> list, boolean insert, int pos_index) {
		encoder.append(true);
		for (int i=chars.length - 1; i >= Math.max(0, chars.length - max_affix_length_); i--) {
			int c = chars[i];
			if (c < 0)
				return;
			encoder.append(c, char_bits_);
			addFeature(encoder, list, insert, pos_index, false);
		}
	}
		
	private void addAffixIndexes(int[] lemma_chars, Encoder encoder, List<Integer> list,
			boolean insert, int pos_index) {
		encoder.append(affix_feature_, feature_bits_);
		addPrefixFeatures(lemma_chars, encoder, list, insert, pos_index);	
		encoder.reset();
		
		encoder.append(affix_feature_, feature_bits_);
		addSuffixFeatures(lemma_chars, encoder, list, insert, pos_index);
		encoder.reset();
		
	}

	private void addAlignmentIndexes(int[] form_chars, int[] lemma_chars,
			int pos_index, List<Integer> alignment, Encoder encoder,
			List<Integer> list, boolean insert) {

		Iterator<Integer> iterator = alignment.iterator();

		int input_start = 0;
		int output_start = 0;
		while (iterator.hasNext()) {
			int input_length = iterator.next();
			int output_length = iterator.next();

			int input_end = input_start + input_length;
			int output_end = output_start + output_length;

			addAlignmentIndexes(form_chars, lemma_chars, pos_index, encoder,
					input_start, input_end, output_start, output_end, list,
					insert);

			input_start = input_end;
			output_start = output_end;
		}
	}

	private void addAlignmentIndexes(int[] form_chars, int[] lemma_chars,
			int pos_index, Encoder encoder, int input_start, int input_end,
			int output_start, int output_end, List<Integer> list, boolean insert) {

		if (isCopySegment(form_chars, lemma_chars, input_start, input_end,
				output_start, output_end)) {
			encoder.append(align_copy_feature_, feature_bits_);
			addFeature(encoder, list, insert, pos_index);
		} else {

			encoder.append(align_feature_, feature_bits_);
			addSegment(form_chars, input_start, input_end, encoder);
			addSegment(form_chars, output_start, output_end, encoder);
			addFeature(encoder, list, insert, pos_index);

			for (int window = 1; window <= max_window; window++) {

				encoder.append(align_window_feature_, feature_bits_);
				encoder.append(window, window_bits_);
				encoder.append(true);
				addSegment(form_chars, input_start - window,
						input_end + window, encoder);
				addSegment(form_chars, output_start, output_end, encoder);
				addFeature(encoder, list, insert, pos_index);

				encoder.append(align_window_feature_, feature_bits_);
				encoder.append(window, window_bits_);
				encoder.append(false);
				addSegment(form_chars, input_start, input_end, encoder);
				addSegment(form_chars, output_start - window, output_end
						+ window, encoder);
				addFeature(encoder, list, insert, pos_index);

			}
		}
	}

	private boolean isCopySegment(int[] form_chars, int[] lemma_chars,
			int input_start, int input_end, int output_start, int output_end) {
		if (input_end - input_start != 1)
			return false;

		if (output_end - output_start != 1) {
			return false;
		}

		return form_chars[input_start] == lemma_chars[output_start];
	}

	private void addSegment(int[] chars, int start, int end, Encoder encoder) {
		encoder.append(end - start, length_bits_);

		for (int i = start; i < end; i++) {

			int c;
			if (i >= 0 && i < chars.length) {
				c = chars[i];
			} else {
				c = char_table_.size();
			}

			if (c < 0)
				return;
			
			encoder.append(c, char_bits_);
		}
	}

	private void addFeature(Encoder encoder, List<Integer> list,
			boolean insert, int pos_index, boolean reset) {
		int index = feature_table_.toIndex(encoder.getFeature(), -1, insert);
		if (index >= 0) {
			list.add(index);

			if (pos_index >= 0) {
				encoder.append(pos_index, pos_bits_);
				index = feature_table_
						.toIndex(encoder.getFeature(), -1, insert);
				if (index >= 0) {
					list.add(index);
				}
			}
		}
		if (reset)
			encoder.reset();
	}
	
	private void addFeature(Encoder encoder, List<Integer> list,
			boolean insert, int pos_index) {
		addFeature(encoder, list, insert, pos_index, true);
	}

	public String select(RerankerInstance instance) {
		Map.Entry<String, LemmaCandidate> best_pair = null;
		for (Map.Entry<String, LemmaCandidate> candidate_pair : instance
				.getCandidateSet()) {
			LemmaCandidate candidate = candidate_pair.getValue();
			double score = score(candidate);

			candidate.setScore(score);

			if (best_pair == null || score > best_pair.getValue().getScore()) {
				best_pair = candidate_pair;
			}
		}
		return best_pair.getKey();
	}

	public double score(LemmaCandidate candidate) {
		assert candidate != null;
		double score = 0.0;
		for (int index : candidate.getFeatureIndexes()) {
			score += weights_[index];
		}
		return score;
	}

	public void update(RerankerInstance instance, String lemma, double update) {
		LemmaCandidate candidate = instance.getCandidateSet().getCandidate(
				lemma);
		update(candidate, update);
	}

	private void update(LemmaCandidate candidate, double update) {
		for (int index : candidate.getFeatureIndexes()) {
			weights_[index] += update;
		}
	}

	public double[] getWeights() {
		return weights_;
	}

	public void setWeights(double[] weights) {
		weights_ = weights;
	}

}
