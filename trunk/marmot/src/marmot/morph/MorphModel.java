// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import marmot.core.Model;
import marmot.core.Sequence;
import marmot.core.State;
import marmot.core.Tagger;
import marmot.core.Token;
import marmot.core.Trainer;
import marmot.core.TrainerFactory;
import marmot.core.WeightVector;
import marmot.morph.analyzer.Analyzer;
import marmot.morph.signature.Trie;
import marmot.util.Counter;
import marmot.util.FileUtils;
import marmot.util.StringUtils;
import marmot.util.StringUtils.Mode;
import marmot.util.SymbolTable;

public class MorphModel extends Model {
	private static final long serialVersionUID = 1L;

	private SymbolTable<String> word_table_;
	private SymbolTable<String> shape_table_;
	private SymbolTable<Character> char_table_;
	private SymbolTable<String> token_feature_table_;
	private SymbolTable<String> weighted_token_feature_table_;

	private List<SymbolTable<String>> subtag_tables_;
	private transient Map<String, Integer> signature_cache;

	private int[] vocab_;
	private int[][] tag_classes_;
	private int[][] transitions_;
	private int[][][] tag_to_subtag_;
	private List<Set<Integer>> observed_sets_;

	private Trie trie_;
	private boolean verbose_;
	private boolean shape_;
	private boolean tag_morph_;
	private int morph_index_;
	private int num_folds_;
	private int pos_index_;
	private int rare_word_max_freq_;

	private boolean split_morphs_;
	private boolean split_pos_;
	private String subtag_seperator_;

	private Mode normalize_forms_;
	
	private Analyzer analyzer_;

	public void init(MorphOptions options, Collection<Sequence> sentences) {
		verbose_ = options.getVerbose();
		rare_word_max_freq_ = options.getRareWordMaxFreq();
		pos_index_ = 0;
		morph_index_ = 1;
		num_folds_ = 10;
		shape_ = options.getShape();
		tag_morph_ = options.getTagMorph();
		split_pos_ = options.getSplitPos();
		split_morphs_ = options.getSplitMorphs();
		subtag_seperator_ = options.getSubTagSeperator();
		normalize_forms_ = options.getNormalizeForms();
		special_signature_ = options.getSpecialSignature();

		init(options, extractCategories(sentences));

		List<SymbolTable<String>> tag_tables_ = getTagTables();

		subtag_tables_ = new ArrayList<SymbolTable<String>>();
		subtag_tables_.add(null);
		subtag_tables_.add(null);

		if (split_pos_) {
			subtag_tables_.set(pos_index_, new SymbolTable<String>());
		}

		if (tag_morph_ && split_morphs_) {
			subtag_tables_.set(morph_index_, new SymbolTable<String>());
		}

		word_table_ = new SymbolTable<String>(true);
		char_table_ = new SymbolTable<Character>();
		if (shape_) {
			shape_table_ = new SymbolTable<String>();
		}
		signature_cache = new HashMap<String, Integer>();

		token_feature_table_ = new SymbolTable<String>();
		weighted_token_feature_table_ = new SymbolTable<String>();

		String internal_analyzer = options.getInternalAnalyzer();
		if (internal_analyzer != null) {
			analyzer_ = Analyzer.create(internal_analyzer); 
		}
		
		if ((shape_)) {

			File file = null;
			if (!options.getShapeTriePath().isEmpty()) {
				file = new File(options.getShapeTriePath());
			}
			if (file == null || !file.exists()) {
				if (verbose_) {
					System.err.println("Inducing shape trie.");
				}
				trie_ = Trie.train(sentences, options.getVeryVerbose());
				if (file != null) {
					if (verbose_) {
						System.err.format("Writing shape trie to: %s.\n",
								options.getShapeTriePath());
					}
					FileUtils.saveToFile(trie_, options.getShapeTriePath());
				}
			} else {
				System.err.format("Loading shape trie from: %s.\n",
						options.getShapeTriePath());
				trie_ = FileUtils.loadFromFile(options.getShapeTriePath());
			}
		}

		if (trie_ == null) {
			shape_ = false;
		}

		for (Sequence sentence : sentences) {
			for (Token token : sentence) {
				Word word = (Word) token;
				addIndexes(word, true);
			}
		}

		vocab_ = extractVocabulary(options, sentences);
		transitions_ = extractPossibleTransitions(options, sentences);
		observed_sets_ = extractObservedSets(sentences);
		tag_classes_ = extractTagClasses(tag_tables_);
		extractSubTags();

		for (Sequence sentence : sentences) {
			for (Token token : sentence) {
				Word word = (Word) token;
				addShape(word, word.getWordForm(), true);
			}
		}
	}

	private int getBiIndex(int word, int level, int tag) {
		int length = 1;
		for (int clevel = 0; clevel <= level; clevel++) {
			length *= getTagTables().get(clevel).size();
		}

		assert tag < length;

		return word * length + tag;
	}

	public boolean hasBeenObserved(int form_index, int level, int tag_index) {
		if (isRare(form_index)) {
			form_index = word_table_.size();
		}
		Set<Integer> set = observed_sets_.get(level);
		int index = getBiIndex(form_index, level, tag_index);
		return set.contains(index);
	}

	private void extractSubTags() {
		tag_to_subtag_ = new int[subtag_tables_.size()][][];

		int offset = 0;
		for (int level = 0; level < subtag_tables_.size(); level++) {

			if (level >= getTagTables().size())
				break;

			SymbolTable<String> table = getTagTables().get(level);

			if (table != null && subtag_tables_.get(level) != null) {
				tag_to_subtag_[level] = new int[table.size()][];
				for (Map.Entry<String, Integer> entry : table.entrySet()) {
					tag_to_subtag_[level][entry.getValue()] = getSubTags(
							entry.getKey(), level, true, offset);
				}

				offset += subtag_tables_.get(level).size();
			}
		}

	}

	private int[][] extractTagClasses(List<SymbolTable<String>> tag_tables) {
		int[][] tag_classes = new int[tag_tables.size()][];
		for (int level = 0; level < tag_tables.size(); level++) {
			int num_tags = tag_tables.get(level).size();
			tag_classes[level] = new int[num_tags - 1];

			int index = 0;
			for (int tag_index = 0; tag_index < num_tags; tag_index++) {
				if (tag_index == getBoundaryIndex())
					continue;
				tag_classes[level][index] = tag_index;

				index++;
			}
		}
		return tag_classes;
	}

	private List<Set<Integer>> extractObservedSets(
			Collection<Sequence> sentences) {
		List<SymbolTable<String>> tag_tables = getTagTables();
		List<Set<Integer>> observed_sets = new ArrayList<Set<Integer>>(
				tag_tables.size());

		List<Map<Integer, Set<Integer>>> wordform_to_candidates = new ArrayList<Map<Integer, Set<Integer>>>();

		for (int level = 0; level < tag_tables.size(); level++) {
			wordform_to_candidates.add(new HashMap<Integer, Set<Integer>>());
		}

		for (Sequence sentence : sentences) {
			for (Token xtoken : sentence) {
				Word token = (Word) xtoken;

				int word_index = token.getWordFormIndex();

				int tag_index = 0;
				for (int level = 0; level < tag_tables.size(); level++) {
					tag_index *= tag_tables.get(level).size();
					tag_index += token.getTagIndexes()[level];

					Set<Integer> tags = wordform_to_candidates.get(level).get(
							word_index);
					if (tags == null) {
						tags = new HashSet<Integer>();
						wordform_to_candidates.get(level).put(word_index, tags);
					}
					tags.add(tag_index);
				}

			}
		}

		List<List<Integer>> open_tag_classes_per_level = getOpenPosTagClassesCrossValidation(
				sentences, num_folds_, tag_tables);

		for (int level = 0; level < tag_tables.size(); level++) {
			Set<Integer> observed_set = new HashSet<Integer>();
			observed_sets.add(observed_set);

			List<Integer> open_tag_classes = open_tag_classes_per_level
					.get(level);

			for (int tag : open_tag_classes) {
				int biindex = getBiIndex(word_table_.size(), level, tag);
				observed_set.add(biindex);
			}

			for (Entry<Integer, Set<Integer>> entry : wordform_to_candidates
					.get(level).entrySet()) {
				int word_index = entry.getKey();
				Set<Integer> set = entry.getValue();
				if (!isRare(word_index)) {
					int[] tags = new int[set.size()];
					int index = 0;
					for (int tag : set) {
						tags[index++] = tag;
					}
					for (int tag : tags) {
						int biindex = getBiIndex(word_index, level, tag);
						observed_set.add(biindex);
					}
				}
			}
		}

		return observed_sets;
	}

	public static List<List<Integer>> getOpenPosTagClassesCrossValidation(
			Collection<Sequence> sentences, int num_folds,
			List<SymbolTable<String>> tag_tables) {

		int sentences_per_fold = sentences.size() / num_folds;
		if (sentences_per_fold == 0)
			sentences_per_fold = 1;

		Set<Integer> known = new HashSet<Integer>();
		List<Counter<Integer>> counters = new ArrayList<Counter<Integer>>(
				tag_tables.size());

		for (int level = 0; level < tag_tables.size(); level++) {
			counters.add(new Counter<Integer>());
		}

		int start_index = 0;

		while (start_index < sentences.size()) {
			known.clear();

			int end_index = start_index + sentences_per_fold;
			if (end_index + sentences_per_fold >= sentences.size()) {
				end_index = sentences.size();
			}

			int index = 0;
			for (Sequence sentence : sentences) {
				if (index < start_index || index >= end_index) {
					for (Token token : sentence) {
						known.add(((Word) token).getWordFormIndex());
					}
				}

				index++;
			}

			index = 0;
			for (Sequence sentence : sentences) {
				if (index >= start_index && index < end_index) {
					for (Token token : sentence) {
						int form = ((Word) token).getWordFormIndex();
						if (!known.contains(form)) {
							int tag_index = 0;
							for (int level = 0; level < tag_tables.size(); level++) {
								tag_index *= tag_tables.get(level).size();
								tag_index += token.getTagIndexes()[level];
								counters.get(level).increment(tag_index, 1.0);
							}
						}
					}
				}
				index++;
			}

			start_index = end_index;
		}

		List<List<Integer>> list = new ArrayList<List<Integer>>(
				tag_tables.size());
		for (int level = 0; level < tag_tables.size(); level++) {
			Counter<Integer> counter = counters.get(level);
			double total_count = counter.totalCount();
			List<Integer> open_tag_classes = new LinkedList<Integer>();
			for (Map.Entry<Integer, Double> entry : counter.entrySet()) {
				if (entry.getValue() / total_count > 0.0001) {
					open_tag_classes.add(entry.getKey());
				}
			}
			list.add(open_tag_classes);
		}

		return list;
	}

	private int[] extractVocabulary(MorphOptions options,
			Collection<Sequence> sentences) {

		Counter<Integer> vocab_counter = new Counter<Integer>();

		for (Sequence sentence : sentences) {
			for (Token token : sentence) {
				Word word = (Word) token;
				int word_index = word.getWordFormIndex();
				vocab_counter.increment(word_index, 1.);
			}
		}

		int[] vocab_array = new int[vocab_counter.size()];
		for (Map.Entry<Integer, Double> entry : vocab_counter.entrySet()) {
			vocab_array[entry.getKey()] = entry.getValue().intValue();
		}

		return vocab_array;
	}

	private int[][] extractPossibleTransitions(MorphOptions options,
			Collection<Sequence> sentences) {
		if (!(options.getRestricTransitions() && tag_morph_))
			return null;

		Map<Integer, Set<Integer>> tag_to_morph = new HashMap<Integer, Set<Integer>>();

		for (Sequence sentence : sentences) {
			for (Token token : sentence) {
				int from_index = token.getTagIndexes()[pos_index_];
				int to_index = token.getTagIndexes()[morph_index_];
				Set<Integer> tags = tag_to_morph.get(from_index);
				if (tags == null) {
					tags = new HashSet<Integer>();
					tag_to_morph.put(from_index, tags);
				}
				tags.add(to_index);
			}
		}

		int[][] transitions = new int[tag_to_morph.size() + 1][];
		transitions[0] = new int[1];

		for (Map.Entry<Integer, Set<Integer>> entry : tag_to_morph.entrySet()) {
			int from_index = entry.getKey();
			int[] to_indexes = new int[entry.getValue().size()];

			int index = 0;
			for (int to_index : entry.getValue()) {
				to_indexes[index++] = to_index;
			}
			Arrays.sort(to_indexes);

			assert transitions[from_index] == null;
			transitions[from_index] = to_indexes;
		}

		return transitions;
	}

	private SymbolTable<String> extractCategories(Collection<Sequence> sentences) {
		SymbolTable<String> catgegory_table = new SymbolTable<String>(true);
		catgegory_table.toIndex("pos", true);
		if (tag_morph_) {
			catgegory_table.toIndex("morph", true);
		}
		return catgegory_table;
	}

	private transient Set<Character> unseen_char_set_;

	private boolean special_signature_;

	private void addCharIndexes(Word word, String form, boolean insert) {
		short[] char_indexes = new short[form.length()];
		for (int index = 0; index < form.length(); index++) {
			char c = form.charAt(index);

			char_indexes[index] = (short) char_table_.toIndex(c, -1, insert);

			if (char_indexes[index] < 0) {
				if (verbose_) {

					if (unseen_char_set_ == null) {
						unseen_char_set_ = new HashSet<Character>();
					}

					if (!unseen_char_set_.contains(c)) {
						System.err
								.format("Warning: Unknown character: %c\n", c);
						unseen_char_set_.add(c);
					}
				}
			}
		}
		word.setCharIndexes(char_indexes);
	}

	private void addSignature(Word word, String form, boolean insert) {
		if (signature_cache == null) {
			signature_cache = new HashMap<String, Integer>();
		}
		
		Integer signature = signature_cache.get(form);
		if (signature == null) {
			signature = 0;
			
			if (special_signature_) {
				if (StringUtils.containsSpecial(form)) {
					signature += 1;
				}
				signature *= 2;
			}
			
			if (StringUtils.containsDigit(form)) {
				signature += 1;
			}
			signature *= 2;
			if (StringUtils.containsHyphon(form)) {
				signature += 1;
			}
			signature *= 2;
			if (StringUtils.containsUpperCase(form)) {
				signature += 1;
			}
			signature *= 2;
			if (StringUtils.containsLowerCase(form)) {
				signature += 1;
			}

			signature_cache.put(form, signature);
		}

		word.setWordSignature(signature);
	}

	private void addTokenFeatures(Word word, Word in_word, boolean insert) {
		String[] token_features = in_word.getTokenFeatures();
		
		List<String> readings = null;
		if (analyzer_ != null) {
			readings = analyzer_.analyze(in_word.getWordForm());
		}
		
		int indexes_length = 0;
		
		if (token_features != null) {
			indexes_length += token_features.length;
		}
		
		if (readings != null) {
			indexes_length += readings.size();
		}
		
		if (indexes_length > 0) {
			int[] indexes = new int[indexes_length];
			int index = 0;
			
			if (token_features != null) {
				for (String feature : token_features) {
					indexes[index] = token_feature_table_.toIndex(feature, -1,
							insert);
					index++;
				}	
			}
			
			if (readings != null) {
				for (String feature : readings) {
					indexes[index] = token_feature_table_.toIndex(feature, -1,
							insert);
					index++;
				}	
			}
			
			word.setTokenFeatureIndexes(indexes);
		}
		

		token_features = word.getWeightedTokenFeatures();
		if (token_features != null && weighted_token_feature_table_ != null) {
			int[] indexes = new int[token_features.length];
			int index = 0;
			for (String feature : token_features) {
				indexes[index] = weighted_token_feature_table_.toIndex(feature,
						-1, insert);
				index++;
			}
			word.setWeightedTokenFeatureIndexes(indexes);
		}

	}

	public void addIndexes(Word word, boolean insert) {
		String word_form = word.getWordForm();

		addTagIndexes(word, -1, insert);

		addSignature(word, word_form, insert);
		addTokenFeatures(word, word, insert);
		addShape(word, word_form, insert);

		String normalized_form = StringUtils.normalize(word_form,
				normalize_forms_);
		int word_index = word_table_.toIndex(normalized_form, -1, insert);
		word.setWordIndex(word_index);
		addCharIndexes(word, normalized_form, insert);
	}

	private int[] getSubTags(String morph, int level, boolean insert, int offset) {
		if (morph.equals(BORDER_SYMBOL_)) {
			return null;
		}

		if (morph.equals("_")) {
			return null;
		}

		if (level >= subtag_tables_.size()) {
			return null;
		}

		SymbolTable<String> subtag_table = subtag_tables_.get(level);

		if (subtag_table == null) {
			return null;
		}

		String[] sub_tags = morph.split(subtag_seperator_);

		if (sub_tags.length == 1) {
			return null;
		}

		List<Integer> indexes = new LinkedList<Integer>();
		for (String sub_tag : sub_tags) {

			if (sub_tag.length() > 0) {

				int value = subtag_table.toIndex(sub_tag, -1, insert);
				if (value >= 0) {
					indexes.add(value);
				}

			}

		}

		int[] array = new int[indexes.size()];
		int i = 0;
		for (int index : indexes) {
			array[i++] = index + offset;
		}

		return array;
	}

	private void addTagIndexes(Word word, int head, boolean insert) {
		List<SymbolTable<String>> tag_tables = getTagTables();
		String pos_tag = word.getPosTag();
		String morph = word.getMorphTag();

		int[] tag_indexes = new int[tag_tables.size()];

		tag_indexes[0] = tag_tables.get(0).toIndex(pos_tag, -1, insert);
		if (tag_morph_) {
			tag_indexes[1] = tag_tables.get(1).toIndex(morph, -1, insert);
		}

		word.setTagIndexes(tag_indexes);
	}

	private void addShape(Word word, String form, boolean insert) {
		if (shape_) {
			int word_index = word.getWordFormIndex();

			if (vocab_ == null) {
				return;
			}

			if (isRare(word_index)) {
				int shape_index = -1;
				if (trie_ != null) {
					String shape = Integer.toString(trie_.classify(form));
					shape_index = shape_table_.toIndex(shape, -1, insert);
				}
				word.setWordShapeIndex(shape_index);
			}
		}
	}

	public boolean isRare(int word) {
		if (word < 0 || word >= vocab_.length) {
			return true;
		}
		return vocab_[word] < rare_word_max_freq_;
	}

	public SymbolTable<String> getWordTable() {
		return word_table_;
	}

	public static Tagger train(MorphOptions options,
			Collection<Sequence> train_sentences,
			Collection<Sequence> test_sentences) {

		if (!options.getVerbose()) {
			test_sentences = null;
		}

		MorphModel model = new MorphModel();
		model.init(options, train_sentences);

		if (test_sentences != null) {
			for (Sequence sentence : test_sentences) {
				for (Token token : sentence) {
					Word word = (Word) token;
					model.addIndexes(word, false);
				}
			}
		}

		WeightVector weights = new MorphWeightVector(options);
		weights.init(model, train_sentences);
		Tagger tagger = new MorphTagger(model, model.getOrder(), weights);

		Trainer trainer = TrainerFactory.create(options);

		MorphEvaluator evaluator = null;
		if (test_sentences != null) {
			evaluator = new MorphEvaluator(test_sentences);
		}

		trainer.train(tagger, train_sentences, evaluator);
		return tagger;
	}

	public SymbolTable<Character> getCharTable() {
		return char_table_;
	}

	public int getNumShapes() {
		if (trie_ == null) {
			return shape_table_.size();
		} else {
			return trie_.getIndex();
		}
	}

	public SymbolTable<String> getShapeTable() {
		return shape_table_;
	}

	public boolean isOOV(int form_index) {
		return form_index < 0 || vocab_[form_index] == 0;
	}

	public int getNumSubTags() {
		int total = 0;
		if (subtag_tables_ != null) {
			for (SymbolTable<String> table : subtag_tables_) {
				if (table != null) {
					total += table.size();
				}
			}
		}
		return total;
	}

	public SymbolTable<String> getTokenFeatureTable() {
		return token_feature_table_;
	}

	public SymbolTable<String> getWeightedTokenFeatureTable() {
		return weighted_token_feature_table_;
	}

	@Override
	public int[] getTagCandidates(Sequence sequence, int index, State state) {
		int level = (state == null) ? 0 : state.getLevel() + 1;

		if (transitions_ != null && level == morph_index_) {
			return transitions_[state.getIndex()];
		}

		return tag_classes_[level];
	}

	public int[][][] getTagToSubTags() {
		return tag_to_subtag_;
	}

	public void setVerbose(boolean verbose) {
		verbose_ = verbose;
	}

	public int getMaxSignature() {
		return (special_signature_) ? 64 : 32; 
	}

}
