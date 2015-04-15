package marmot.lemma.reranker;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import marmot.core.Feature;
import marmot.lemma.LemmaCandidate;
import marmot.lemma.LemmaCandidateSet;
import marmot.lemma.reranker.RerankerTrainer.RerankerTrainerOptions;
import marmot.lemma.toutanova.EditTreeAligner;
import marmot.util.Converter;
import marmot.util.Encoder;
import marmot.util.Lexicon;
import marmot.util.LineIterator;
import marmot.util.StringUtils.Mode;
import marmot.util.SymbolTable;
import marmot.util.edit.EditTree;
import marmot.util.Runtime;

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
	private static final int lexicon_feature_ = 7;
	private static final int feature_bits_ = Encoder.bitsNeeded(7);
	private static final int unigram_count_bin_bits_ = Encoder.bitsNeeded(4);
	private static final int max_weights_length_ = 100_000_000;
	
	private static final int encoder_size_ = 11;
	
	private int lemma_bits_;
	private int form_bits_;
	private int pos_bits_;
	private int char_bits_;
	private int tree_bits_;
	private Lexicon unigram_lexicon_;
	private RerankerTrainerOptions options_;
	private SymbolTable<String> morph_table_;
	private int morph_bits_;
	
	

	private static class Context {
		public int pos_index;
		public List<Integer> morph_indexes;
		public List<Integer> list;
		public boolean insert;
	}
	
	private static final int length_bits_ = Encoder
			.bitsNeeded(2 * max_window + 10);

	public void init(RerankerTrainerOptions options,
			List<RerankerInstance> instances, EditTreeAligner aligner) {
		Logger logger = Logger.getLogger(getClass().getName());
		options_ = options;
		aligner_ = aligner;

		form_table_ = new SymbolTable<>();
		lemma_table_ = new SymbolTable<>();
		char_table_ = new SymbolTable<>();
		tree_table_ = new SymbolTable<>();
		
		if (options_.getUsePos()) {
			pos_table_ = new SymbolTable<>();
		}
		
		if (options_.getUseMorph()) {
			morph_table_ = new SymbolTable<>();
		}
		
		for (RerankerInstance instance : instances) {
			fillTables(instance, instance.getCandidateSet());
		}

		form_bits_ = Encoder.bitsNeeded(form_table_.size() - 1);
		lemma_bits_ = Encoder.bitsNeeded(lemma_table_.size() - 1);
		char_bits_ = Encoder.bitsNeeded(char_table_.size());
		tree_bits_ = Encoder.bitsNeeded(tree_table_.size() - 1);
		
		if (pos_table_ != null) {
			pos_bits_ = Encoder.bitsNeeded(pos_table_.size());
			logger.info(String.format("Number of POS features: %d", pos_table_.size()));
		}
		
		if (morph_table_ != null) {
			morph_bits_ = Encoder.bitsNeeded(morph_table_.size());
			logger.info(String.format("Number of morph features: %d", morph_table_.size()));
		}
		
		prepareUnigramFeature(options.getUnigramFile());

		feature_table_ = new SymbolTable<>();
		
		int index = 0;
		for (RerankerInstance instance : instances) {
			addIndexes(index++, instances.size(), instance, instance.getCandidateSet(), true);
		}

		int length = Math.min(feature_table_.size(), max_weights_length_ );
		weights_ = new double[length];
		logger.info(String.format("Number of features: %d", feature_table_.size()));
	}

	private void prepareUnigramFeature(String unigram_file) {
		Logger logger = Logger.getLogger(getClass().getName());

		if (unigram_file == null) {
			return;
		}

		String filename = null;
		int min_count = 0;
		for (String argument : unigram_file.split(",")) {

			int index = argument.indexOf('=');

			if (index >= 0) {

				String argname = argument.substring(0, index);
				String value = argument.substring(index + 1);

				if (argname.equalsIgnoreCase("min-count")) {
					min_count = Integer.valueOf(value);
				} else {
					throw new RuntimeException(String.format(
							"Unknown option: %s", argname));
				}

			} else {
				filename = argument;
			}

		}

		if (filename == null) {
			throw new RuntimeException(String.format(
					"No filename specified: %s", unigram_file));
		}

		logger.info(String
				.format("Creating unigram lexicon from file: %s and with min-count %d.",
						filename, min_count));

		LineIterator iterator = new LineIterator(filename);

		unigram_lexicon_ = new Lexicon(Mode.lower);

		while (iterator.hasNext()) {

			List<String> line = iterator.next();

			if (line.isEmpty())
				continue;

			checkUnigramLine(line.size() == 2, unigram_file, line);

			String word = line.get(0);

			int count = 0;
			try {
				String count_string = line.get(1);
				count = Integer.valueOf(count_string);
			} catch (NumberFormatException e) {
				checkUnigramLine(false, unigram_file, line);
			}

			if (count >= min_count)
				unigram_lexicon_.addEntry(word, count);
		}

		logger.info(String.format("Created unigram lexicon with %7d entries.",
				unigram_lexicon_.size()));
	}

	private void checkUnigramLine(boolean condition, String unigram_file,
			List<String> line) {
		if (!condition) {
			throw new RuntimeException(
					String.format(
							"Line in file %s should be of format <WORD> <COUNT>, but is \"%s\"",
							unigram_file, line));
		}
	}

	private void fillTables(RerankerInstance instance, LemmaCandidateSet set) {
		String form = instance.getInstance().getForm();
		form_table_.insert(form);

		instance.getFormChars(char_table_, true);

		String postag = instance.getInstance().getPosTag();
		if (postag != null && pos_table_ != null )
			pos_table_.insert(postag);

		String morphtag = instance.getInstance().getMorphTag();
		if (morphtag != null && morph_table_ != null) {			
			getMorphFeatures(morphtag, true);
		}
		
		for (Map.Entry<String, LemmaCandidate> candidate_pair : set) {
			String lemma = candidate_pair.getKey();
			LemmaCandidate candidate = candidate_pair.getValue();

			candidate.getLemmaChars(char_table_, lemma, true);
			candidate.getAlignment(aligner_, form, lemma);
			candidate.getTreeIndex(aligner_.getBuilder(), form, lemma,
					tree_table_, true);

			lemma_table_.insert(lemma);
		}
	}

	private List<Integer> getMorphFeatures(String morphtag, boolean insert) {
		if (morphtag == null || morph_table_ == null || morphtag.equals('_')) {
			return Collections.emptyList();
		}
		
		List<Integer> list = new LinkedList<>();
		for (String feat : morphtag.split("|") ) {
			int index = morph_table_.toIndex(feat, -1, insert);
			if (index >= 0) {
				list.add(index);
			}
		}
		return list;
	}

	public void addIndexes(int index, int num_instances, RerankerInstance instance, LemmaCandidateSet set,
			boolean insert) {
		
		if (index % 1000 == 0) {
			Logger logger = Logger.getLogger(getClass().getName());
			logger.info(String.format("%5d / %5d :: Memory usage: %g / %g MB", index, num_instances, Runtime.getUsedMemoryInMegaBytes() , Runtime.getMaxHeapSizeInMegaBytes()));
		}
		
		String form = instance.getInstance().getForm();
		int form_index = form_table_.toIndex(form, -1);
		String postag = instance.getInstance().getPosTag();
		String morphtag = instance.getInstance().getMorphTag();
		
		Context c = new Context();
		c.insert = insert;
		
		c.pos_index = -1;
		if (pos_table_ != null && postag != null)
			c.pos_index = pos_table_.toIndex(postag, -1, false);
		
		c.morph_indexes = Collections.emptyList();
		if (morph_table_ != null && morphtag != null)
			c.morph_indexes = getMorphFeatures(morphtag, false);
		

		int[] form_chars = instance.getFormChars(char_table_, false);

		Encoder encoder = new Encoder(encoder_size_);

		for (Map.Entry<String, LemmaCandidate> candidate_pair : set) {
			c.list = new LinkedList<>();
			
			String lemma = candidate_pair.getKey();
			int lemma_index = lemma_table_.toIndex(lemma, -1, false);

			LemmaCandidate candidate = candidate_pair.getValue();

			int[] lemma_chars = candidate.getLemmaChars(char_table_, lemma, false);

			if (lemma_index >= 0) {
				encoder.append(lemma_feature_, feature_bits_);
				encoder.append(lemma_index, lemma_bits_);
				addFeature(encoder, c);
			}

			if (lemma_index >= 0 && form_index >= 0) {
				encoder.append(lemma_form_feature_, feature_bits_);
				encoder.append(lemma_index, lemma_bits_);
				encoder.append(form_index, form_bits_);
				addFeature(encoder, c);
			}

			List<Integer> alignment = candidate.getAlignment(aligner_, form, lemma);

			addAlignmentIndexes(c, form_chars, lemma_chars, alignment, encoder);

			int tree_index = candidate.getTreeIndex(aligner_.getBuilder(), form, lemma, tree_table_, false);

			if (tree_index >= 0) {
				encoder.append(tree_feature_, feature_bits_);
				encoder.append(tree_index, tree_bits_);
				addFeature(encoder, c);

				encoder.append(tree_feature_, feature_bits_);
				encoder.append(tree_index, tree_bits_);
				addPrefixFeatures(c, form_chars, encoder);
				encoder.reset();

				encoder.append(tree_feature_, feature_bits_);
				encoder.append(tree_index, tree_bits_);
				addSuffixFeatures(c, form_chars, encoder);
				encoder.reset();
			}

			addAffixIndexes(c, lemma_chars, encoder);

			if (unigram_lexicon_ != null) {
				int count = unigram_lexicon_.getCount(lemma);

				int bin = 0;
				if (count == 0) {
					bin = 0;
				} else if (count < 5) {
					bin = 1;
				} else {
					assert count >= 5;
					bin = 2;
				}

				encoder.append(lexicon_feature_, feature_bits_);
				encoder.append(bin, unigram_count_bin_bits_);
				addFeature(encoder, c);
			}

			candidate.setFeatureIndexes(Converter.toIntArray(c.list));
			c.list = null;
		}
	}

	private void addPrefixFeatures(Context context, int[] chars, Encoder encoder) {
		encoder.append(false);
		for (int i = 0; i < Math.min(chars.length, max_affix_length_); i++) {
			int c = chars[i];
			if (c < 0)
				return;
			encoder.append(c, char_bits_);
			addFeature(encoder, context, false);
		}
	}

	private void addSuffixFeatures(Context context, int[] chars, Encoder encoder) {
		encoder.append(true);
		for (int i = chars.length - 1; i >= Math.max(0, chars.length
				- max_affix_length_); i--) {
			int c = chars[i];
			if (c < 0)
				return;
			encoder.append(c, char_bits_);
			addFeature(encoder, context, false);
		}
	}

	private void addAffixIndexes(Context c, int[] lemma_chars, Encoder encoder) {
		encoder.append(affix_feature_, feature_bits_);
		addPrefixFeatures(c, lemma_chars, encoder);
		encoder.reset();

		encoder.append(affix_feature_, feature_bits_);
		addSuffixFeatures(c, lemma_chars, encoder);
		encoder.reset();

	}

	private void addAlignmentIndexes(Context c, int[] form_chars, int[] lemma_chars,
			List<Integer> alignment, Encoder encoder) {

		Iterator<Integer> iterator = alignment.iterator();

		int input_start = 0;
		int output_start = 0;
		while (iterator.hasNext()) {
			int input_length = iterator.next();
			int output_length = iterator.next();

			int input_end = input_start + input_length;
			int output_end = output_start + output_length;

			addAlignmentSegmentIndexes(c, form_chars, lemma_chars, encoder,
					input_start, input_end, output_start, output_end);

			input_start = input_end;
			output_start = output_end;
		}
	}

	private void addAlignmentSegmentIndexes(Context c, int[] form_chars, int[] lemma_chars,
			Encoder encoder, int input_start, int input_end,
			int output_start, int output_end) {

		if (isCopySegment(form_chars, lemma_chars, input_start, input_end,
				output_start, output_end)) {
			encoder.append(align_copy_feature_, feature_bits_);
			addFeature(encoder, c);
		} else {

			encoder.append(align_feature_, feature_bits_);
			addSegment(form_chars, input_start, input_end, encoder);
			addSegment(form_chars, output_start, output_end, encoder);
			addFeature(encoder, c);

			for (int window = 1; window <= max_window; window++) {

				encoder.append(align_window_feature_, feature_bits_);
				encoder.append(window, window_bits_);
				encoder.append(true);
				addSegment(form_chars, input_start - window,
						input_end + window, encoder);
				addSegment(form_chars, output_start, output_end, encoder);
				addFeature(encoder, c);

				encoder.append(align_window_feature_, feature_bits_);
				encoder.append(window, window_bits_);
				encoder.append(false);
				addSegment(form_chars, input_start, input_end, encoder);
				addSegment(form_chars, output_start - window, output_end
						+ window, encoder);
				addFeature(encoder, c);

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
	
	private int getFeatureIndex(Context c, Encoder encoder) {
		if (feature_table_ != null) {
			return feature_table_.toIndex(encoder.getFeature(), -1, c.insert);	 
		}		
		return encoder.hashCode();
	}

	private void addFeature(Encoder encoder, Context c, boolean reset) {
		int index = getFeatureIndex(c, encoder);
		if (index >= 0) {
			c.list.add(index);

			if (c.pos_index >= 0) {
				encoder.storeState();
				encoder.append(c.pos_index, pos_bits_);
				index = getFeatureIndex(c, encoder);
				encoder.restoreState();
				
				if (index >= 0) {
					c.list.add(index);
				}
				
				if (morph_table_ != null && !c.morph_indexes.isEmpty()) {
					for (int morph_index : c.morph_indexes) {
						encoder.append(c.pos_index, pos_bits_);
						encoder.append(morph_index, morph_bits_);
						index = getFeatureIndex(c, encoder);
						encoder.restoreState();
						
						if (index >= 0) {
							c.list.add(index);
						}
					}
				}
			}
			
		}
		if (reset)
			encoder.reset();
	}

	private void addFeature(Encoder encoder, Context c) {
		addFeature(encoder, c, true);
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
			score += weights_[index % weights_.length];
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
			weights_[index % weights_.length] += update;
		}
	}

	public double[] getWeights() {
		return weights_;
	}

	public void setWeights(double[] weights) {
		weights_ = weights;
	}

}
