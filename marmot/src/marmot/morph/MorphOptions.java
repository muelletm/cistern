// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph;

import java.util.HashMap;
import java.util.Map;

import marmot.core.Options;
import marmot.util.StringUtils.Mode;

public class MorphOptions extends Options {
	private static final long serialVersionUID = 1L;
	public static final String TRAIN_FILE = "train-file";
	public static final String TEST_FILE = "test-file";
	public static final String RARE_WORD_MAX_FREQ = "rare-word-max-freq";
	public static final String SHAPE_TRIE_PATH = "shape-trie-path";
	public static final String MODEL_FILE = "model-file";
	public static final String RESTRICT_TRANSITIONS = "restrict-transitions";
	//public static final String SUBPOS_TO_POS = "subpos-to-pos";
	public static final String SHAPE = "shape";
	public static final String TAG_MORPH = "tag-morph";
	public static final String PRED_FILE = "pred-file";
	public static final String OBSERVED_FEATURE = "observed-feature";
	public static final String SPLIT_MORPH = "split-morph";
	public static final String SUBTAG_SEPARATOR = "subtag-separator";
	public static final String TYPE_DICT = "type-dict";
	public static final String SPLIT_POS = "split-pos";
	public static final String FLOAT_TYPE_DICT = "type-embeddings";
	public static final String FORM_NORMALIZATION = "form-normalization";
	public static final String NUM_CHUNKS = "num-chunks";
	public static final String SPECIAL_SIGNATURE = "special-signature";
	public static final String INTERNAL_ANALYZER = "internal-analyzer";
	public static final String NUM_FOLDS = "num-folds";
	public static final String USE_DEFAULT_FEATURES = "use-default-features";
	public static final String USE_HASH_VECTOR = "use-hash-vector";
	public static final String FEATURE_TEMPLATES = "feature-templates";
	public static final String MAX_AFFIX_LENGTH = "max-affix-length";
	public static final String LEMMATIZE = "lemmatize";
	public static final String LEMMA_UNIGRAM_FILE = "lemma-unigram-file";
	public static final String GOLD_LEMMA = "gold-lemma";
	public static final String LEMMA_PRETRAINING = "lemma-pretraining";
	public static final String MARGINALIZE_LEMMAS = "marginalize-lemmas";
	public static final String LEMMA_ASPELL_LANG = "lemma-aspell-lang";
	public static final String LEMMA_ASPELL_PATH = "lemma-aspell-path";
	public static final String LEMMA_USE_SHAPE_LEXICON= "lemma-use-shape-lexicon";
	public static final String LEMMA_USE_MORPH = "lemma-use-morph";
	public static final String LEMMATIZER_FILE = "lemmatizer-file";
	public static final String LEMMAS_IGNORE_FEATURES = "lemma-ignore-features";
	public static final String LEMMA_PREPRUNING_EXTRACTION_ = "lemma-prepruning-extraction";
	public static final String USE_HASH_FEATURE_TABLE = "use-hash-feature-table";
	public static final String LEMMA_CLUSTER_FILE = "lemma-cluster-file";
	public static final String LEMMA_TAG_DEPENDENT = "lemma-tag-dependent";
	public static final String LEMMA_LEMMING_GENERATOR = "lemma-use-lemming-generator";
	public static final String RESTRICT_POS_TAGS_TO_SEEN_COMBINATIONS = "restrict-pos-tags-to-seen-combinations";
	public static final String OUTPUT_FORMAT_CONLLU = "conllu-format" ; 

	private static final Map<String, String> DEFALUT_VALUES_ = new HashMap<String, String>();
	private static final Map<String, String> COMMENTS_ = new HashMap<String, String>();

	static {
		DEFALUT_VALUES_.put(TRAIN_FILE, "");
		COMMENTS_.put(TRAIN_FILE, "Input training file");
		DEFALUT_VALUES_.put(TEST_FILE, "");
		COMMENTS_.put(TEST_FILE, "Input test file. (optional for training)");
		DEFALUT_VALUES_.put(PRED_FILE, "");
		COMMENTS_.put(PRED_FILE, "Output prediction file in CoNLL09. (optional for training)");
		DEFALUT_VALUES_.put(RARE_WORD_MAX_FREQ, "10");
		COMMENTS_.put(RARE_WORD_MAX_FREQ, "Maximal frequency of a rare word.");
		DEFALUT_VALUES_.put(SHAPE_TRIE_PATH, "");
		COMMENTS_.put(SHAPE_TRIE_PATH, "Path to the shape trie. Will be created if non-existent.");
		DEFALUT_VALUES_.put(MODEL_FILE, "");
		COMMENTS_.put(MODEL_FILE, "Output model file.");
		DEFALUT_VALUES_.put(RESTRICT_TRANSITIONS, "true");
		COMMENTS_.put(RESTRICT_TRANSITIONS, "Whether to only allow POS -> MORPH transitions that have been seen during training.");
		DEFALUT_VALUES_.put(SHAPE, "false");
		COMMENTS_.put(SHAPE, "Whether to use shape features.");
		DEFALUT_VALUES_.put(TAG_MORPH, "true");
		COMMENTS_.put(TAG_MORPH, "Whether to train a morphological tagger or a POS tagger.");
		DEFALUT_VALUES_.put(OBSERVED_FEATURE, "true");
		COMMENTS_.put(OBSERVED_FEATURE, "Whether to use the observed feature. Have a look at the paper!");
		DEFALUT_VALUES_.put(SPLIT_POS, "false");
		COMMENTS_.put(SPLIT_POS, "Whether to split POS tags. See subtag-separator. Have a look at the paper!");
		DEFALUT_VALUES_.put(SPLIT_MORPH, "true");
		COMMENTS_.put(SPLIT_MORPH, "Whether to split MORPH tags. See subtag-separator. Have a look at the paper!");
		DEFALUT_VALUES_.put(SUBTAG_SEPARATOR, "\\|");
		COMMENTS_.put(SUBTAG_SEPARATOR, "Regular expression to use for splitting tags. (Has to work with Java's String.split)");
		DEFALUT_VALUES_.put(TYPE_DICT, "");
		COMMENTS_.put(TYPE_DICT, "Word type dictionary file (optional)");
		DEFALUT_VALUES_.put(FLOAT_TYPE_DICT, "");
		COMMENTS_.put(FLOAT_TYPE_DICT, "Word type embeddings file (optional)");
		DEFALUT_VALUES_.put(FORM_NORMALIZATION, "none");
		COMMENTS_.put(FORM_NORMALIZATION, "Whether to normalize word forms before tagging.");
		DEFALUT_VALUES_.put(NUM_CHUNKS, "5");
		COMMENTS_.put(NUM_CHUNKS, "Number of chunks. CrossAnnotator only.");
		DEFALUT_VALUES_.put(SPECIAL_SIGNATURE, "false");
		COMMENTS_.put(SPECIAL_SIGNATURE, "Whether to mark if a word contains a special character in the word signature.");
		DEFALUT_VALUES_.put(INTERNAL_ANALYZER, "");
		COMMENTS_.put(INTERNAL_ANALYZER, "Use an internal morphological analyzer. Currently supported: 'ar' for AraMorph (Arabic)");
		DEFALUT_VALUES_.put(NUM_FOLDS, "10");
		COMMENTS_.put(NUM_FOLDS, "Number of folds used for estimation of open word classes.");
		DEFALUT_VALUES_.put(USE_DEFAULT_FEATURES, "true");
		COMMENTS_.put(USE_DEFAULT_FEATURES, "Whether to extract default features such as prefixes, suffixes, word forms.");
		DEFALUT_VALUES_.put(USE_HASH_VECTOR, "true");
		COMMENTS_.put(USE_HASH_VECTOR, "Whether to use a hashed feature vector. Saves memory decreases accuracy.");
		DEFALUT_VALUES_.put(FEATURE_TEMPLATES, "form,rare,affix,context,sig,bigrams");
		COMMENTS_.put(FEATURE_TEMPLATES, "Comma separated list, activates individual templates.");
		DEFALUT_VALUES_.put(MAX_AFFIX_LENGTH, "10");
		COMMENTS_.put(MAX_AFFIX_LENGTH, "Max affix length to use in feature extraction.");
		DEFALUT_VALUES_.put(LEMMATIZE, "false");
		COMMENTS_.put(LEMMATIZE, "Train joint tagger + lemmatizer.");
		DEFALUT_VALUES_.put(LEMMA_UNIGRAM_FILE, "");
		COMMENTS_.put(LEMMA_UNIGRAM_FILE, "Is passed to lemma ranker model.");
		DEFALUT_VALUES_.put(GOLD_LEMMA, "false");
		COMMENTS_.put(GOLD_LEMMA, "Use only gold lemma.");
		DEFALUT_VALUES_.put(LEMMA_PRETRAINING, "false");
		COMMENTS_.put(LEMMA_PRETRAINING, "Pretrain tagger without lemma features.");
		DEFALUT_VALUES_.put(MARGINALIZE_LEMMAS, "false");
		COMMENTS_.put(MARGINALIZE_LEMMAS, "Marginalize over lemmas during viterbi decoding.");
		DEFALUT_VALUES_.put(LEMMAS_IGNORE_FEATURES, "");
		COMMENTS_.put(LEMMAS_IGNORE_FEATURES, "Features to ignore in the lemma model.");
		DEFALUT_VALUES_.put(LEMMA_ASPELL_LANG, "");
		COMMENTS_.put(LEMMA_ASPELL_LANG, "Passed to lemma model.");
		DEFALUT_VALUES_.put(LEMMA_ASPELL_PATH, "");
		COMMENTS_.put(LEMMA_ASPELL_PATH, "Passed to lemma model.");
		DEFALUT_VALUES_.put(LEMMA_USE_SHAPE_LEXICON, "false");
		COMMENTS_.put(LEMMA_USE_SHAPE_LEXICON, "Passed to lemma model.");
		DEFALUT_VALUES_.put(LEMMA_USE_MORPH, "true");
		COMMENTS_.put(LEMMA_USE_MORPH, "Passed to lemma model.");
		DEFALUT_VALUES_.put(LEMMATIZER_FILE, "");
		COMMENTS_.put(LEMMATIZER_FILE, "Use this pipeline lemmatizer to lemmatizer after tagging.");
		DEFALUT_VALUES_.put(LEMMA_PREPRUNING_EXTRACTION_, "true");
		COMMENTS_.put(LEMMA_PREPRUNING_EXTRACTION_, "Add lemmas before or after pruning.");
		DEFALUT_VALUES_.put(USE_HASH_FEATURE_TABLE, "false");
		COMMENTS_.put(USE_HASH_FEATURE_TABLE, "Less memory usage sligtly less accurate.");
		DEFALUT_VALUES_.put(LEMMA_CLUSTER_FILE, "");
		COMMENTS_.put(LEMMA_CLUSTER_FILE, "Passed to lemma model.");
		DEFALUT_VALUES_.put(LEMMA_TAG_DEPENDENT, "false");
		COMMENTS_.put(LEMMA_TAG_DEPENDENT, "Passed to lemma model.");
		DEFALUT_VALUES_.put(LEMMA_LEMMING_GENERATOR, "0");
		COMMENTS_.put(LEMMA_LEMMING_GENERATOR, "Passed to lemma model.");
		DEFALUT_VALUES_.put(RESTRICT_POS_TAGS_TO_SEEN_COMBINATIONS, "false");
		COMMENTS_.put(RESTRICT_POS_TAGS_TO_SEEN_COMBINATIONS, "Restrict the possible pos tags of a word to the combinations seen in the training set.");
		DEFALUT_VALUES_.put(OUTPUT_FORMAT_CONLLU, "false");
		COMMENTS_.put(OUTPUT_FORMAT_CONLLU, "Use CoNLL-U output format.");

	}

	public MorphOptions() {
		super();
		putAll(DEFALUT_VALUES_);
	}
	
	public String getTrainFile() {
		return getProperty(TRAIN_FILE);
	}

	public String getTestFile() {
		return getProperty(TEST_FILE);
	}
	
	public int getRareWordMaxFreq() {
		return Integer.parseInt(getProperty(RARE_WORD_MAX_FREQ));
	}
	
	public boolean getRestricTransitions() {
		return Boolean.parseBoolean(getProperty(RESTRICT_TRANSITIONS));
	}
	
	public boolean getTagMorph() {
		return Boolean.valueOf(getProperty(TAG_MORPH));
	}
	
	public String getPredFile() {
		return getProperty(PRED_FILE);
	}
	
	public boolean getShape() {
		return Boolean.parseBoolean(getProperty(SHAPE));
	}
	
	public String getShapeTriePath() {
		return getProperty(SHAPE_TRIE_PATH);
	}

	public String getModelFile() {
		return getProperty(MODEL_FILE);
	}

	public boolean getObservedFeature() {
		return Boolean.parseBoolean(getProperty(OBSERVED_FEATURE));
	}

	public boolean getSplitMorphs() {
		return Boolean.parseBoolean(getProperty(SPLIT_MORPH));
	}
	
	public String getSubTagSeparator() {
		return getProperty(SUBTAG_SEPARATOR);
	}

	public String getMorphDict() {
		return getProperty(TYPE_DICT);
	}
	
	public String getFloatTypeDict() {
		return getProperty(FLOAT_TYPE_DICT);
	}

	public boolean getSplitPos() {
		return Boolean.parseBoolean(getProperty(SPLIT_POS));
	}
	
	protected void usage() {
		super.usage();
		System.err.println("Morph Options:");
		usage(DEFALUT_VALUES_, COMMENTS_);
		System.err.println();
	}

	public Mode getNormalizeForms() {
		return Mode.valueOf(getProperty(FORM_NORMALIZATION));
	}

	public int getNumChunks() {
		return Integer.parseInt(getProperty(NUM_CHUNKS));
	}

	public boolean getSpecialSignature() {
		return Boolean.valueOf(getProperty(SPECIAL_SIGNATURE));
	}
	
	public String getInternalAnalyzer() {
		String prop = getProperty(INTERNAL_ANALYZER);
		
		if (prop.isEmpty()) {
			return null;
		}
		
		return prop;
	}

	public boolean getFormatIsCoNLLU() {
		return Boolean.parseBoolean(getProperty(OUTPUT_FORMAT_CONLLU));
	}

	public int getNumFolds() {
		return Integer.parseInt(getProperty(NUM_FOLDS));
	}

	public boolean getUseDefaultFeatures() {
		return Boolean.parseBoolean(getProperty(USE_DEFAULT_FEATURES));
	}

	public boolean getUseHashVector() {
		return Boolean.parseBoolean(getProperty(USE_HASH_VECTOR));
	}

	public String getFeatureTemplates() {
		return getProperty(FEATURE_TEMPLATES);
	}

	public int getMaxAffixLength() {
		return Integer.valueOf(getProperty(MAX_AFFIX_LENGTH));
	}
	
	public boolean getLemmatizer() {
		return Boolean.valueOf(getProperty(LEMMATIZE));
	}
	
	public String getLemmaUnigramFile() {
		return getProperty(LEMMA_UNIGRAM_FILE);
	}
	
	public boolean getGoldLemma() {
		return Boolean.valueOf(getProperty(GOLD_LEMMA));
	}

	public boolean getLemmaPretraining() {
		return Boolean.valueOf(getProperty(LEMMA_PRETRAINING));
	}

	public boolean getMarginalizeLemmas() {
		return Boolean.valueOf(getProperty(MARGINALIZE_LEMMAS));
	}

	public String getLemmaIgnoreFeatures() {
		return getProperty(LEMMAS_IGNORE_FEATURES);
	}
	
	public String getLemmaAspellPath() {
		return getProperty(LEMMA_ASPELL_PATH);
	}
	
	public String getLemmaAspellLang() {
		return getProperty(LEMMA_ASPELL_LANG);
	}
	
	public boolean getLemmaUseShapeLexicon() {
		return Boolean.parseBoolean(getProperty(LEMMA_USE_SHAPE_LEXICON));
	}
	
	public boolean getLemmaUseMorph() {
		return Boolean.parseBoolean(getProperty(LEMMA_USE_MORPH));
	}

	public String getLemmatizerFile() {
		return getProperty(LEMMATIZER_FILE);
	}

	public boolean getUseHashFeatureTable() {
		return Boolean.valueOf(getProperty(USE_HASH_FEATURE_TABLE));
	}

	public String getLemmaClusterFile() {
		return getProperty(LEMMA_CLUSTER_FILE);
	}

	public boolean getLemmaPrePruningExtraction() {
		return Boolean.valueOf(getProperty(LEMMA_PREPRUNING_EXTRACTION_));
	}

	public boolean getLemmaTagDependent() {
		return Boolean.valueOf(getProperty(LEMMA_TAG_DEPENDENT));
	}

	public int getLemmaUseLemmingGenerator() {
		return Integer.valueOf(getProperty(LEMMA_LEMMING_GENERATOR ));
	}

	public boolean getRestrictPosTagsToSeenCombinations() {
		return Boolean.valueOf(getProperty(RESTRICT_POS_TAGS_TO_SEEN_COMBINATIONS));
	}

}
