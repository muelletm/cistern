package chipmunk.segmenter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marmot.core.Feature;
import marmot.util.DynamicWeights;
import marmot.util.Encoder;
import marmot.util.SymbolTable;

public class SegmenterModel implements Serializable {

	private static final long serialVersionUID = 1L;
	private SymbolTable<String> tag_table_;
	private SymbolTable<Character> char_table_;

	private int max_segment_length_;
	transient private Encoder encoder_;
	transient private Encoder.State encoder_state_;

	private int window_length_bits_;
	private int num_char_bits_;
	private int num_tag_bits_;
	private int max_segment_length_bits_;

	private IndexScorer scorer_;
	private IndexUpdater updater_;
	
	private final static int FEATURE_BITS = Encoder.bitsNeeded(4);
	private final static int TRANS_FEAT = 0;
	private final static int TAG_FEAT = 1;
	private final static int PAIR_FEAT = 2;
	private final static int CHARACTER_FEAT = 3;
	private final static int DICT_FEAT = 4;
	
	private List<Dictionary> dictionaries_;
	private int dictionary_bits_;
	private List<List<Integer>> tags_to_subtags_;
	private SegmenterOptions options_;

	public void init(SegmenterOptions options, Collection<Word> words) {
		options_ = options;
		tag_table_ = new SymbolTable<>(true);
		char_table_ = new SymbolTable<>();

		max_segment_length_ = 0;

		for (Word word : words) {

			for (SegmentationReading reading : word.getReadings()) {
				for (String segment : reading.getSegments()) {
					assert segment.length() > 0;
					if (segment.length() > max_segment_length_) {
						max_segment_length_ = segment.length();
					}
				}

				for (String tag : reading.getTags()) {
					tag_table_.toIndex(tag, true);
				}
			}

			for (int i = 0; i < word.getWord().length(); i++) {
				char c = word.getWord().charAt(i);
				char_table_.toIndex(c, true);
			}
		}
		
		
		tags_to_subtags_ = new ArrayList<>();
		for (int i=0;i<tag_table_.size(); i++) {
			tags_to_subtags_.add(null);
		}
		
		SymbolTable<String> subtag_table = new SymbolTable<>();
		for (Map.Entry<String, Integer> entry : tag_table_.entrySet()) {
			String tag = entry.getKey();
			String[] subtags = TagSet.split(tag);
			
			List<Integer> indexes = new LinkedList<>();
			indexes.add(subtag_table.toIndex(tag, true));
			
			if (subtags.length > 1) {
				for (String subtag : subtags) {
					indexes.add(subtag_table.toIndex(subtag, true));		
				}
			}
			
			tags_to_subtags_.set(entry.getValue(), indexes);
		}

		if (options_.getBoolean(SegmenterOptions.VERBOSE));
		System.err.println("Tag table: " + tag_table_);
		System.err.println("Num tags: " + tag_table_.size());
		
		dictionaries_ = new LinkedList<>();
		Collection<String> dictionary_paths = options_.getDictionaries();
		for (String path : dictionary_paths) {
			Dictionary dictionary = new Dictionary(path, options_.getString(SegmenterOptions.LANG), max_segment_length_);
			
			if (options_.getBoolean(SegmenterOptions.VERBOSE)) {
				System.err.format("Created dictionary with %d entries from %s\n", dictionary.size(), path);
			}
			
			dictionaries_.add(dictionary);
		}

		num_tag_bits_ = Encoder.bitsNeeded(subtag_table.size());
		num_char_bits_ = Encoder.bitsNeeded(char_table_.size());
		max_segment_length_bits_ = Encoder.bitsNeeded(max_segment_length_);
		window_length_bits_ = Encoder.bitsNeeded(options_.getInt(SegmenterOptions.MAX_CHARACTER_WINDOW) + 1);
		
		if (!dictionaries_.isEmpty()) {
			dictionary_bits_ = Encoder.bitsNeeded(dictionaries_.size());
		}

		SymbolTable<Feature> feature_map = new SymbolTable<>();
		scorer_ = new IndexScorer(null, feature_map, num_tag_bits_);
		updater_ = new IndexUpdater(null, feature_map, num_tag_bits_);
	}

	private void prepareEncoder() {
		if (encoder_ == null) {
			encoder_ = new Encoder(10);
			encoder_state_ = new Encoder.State();
		}
		encoder_.reset();
	}

	public int getNumTags() {
		return tag_table_.size();
	}

	public int getMaxSegmentLength() {
		return max_segment_length_;
	}

	public double getPairScore(SegmentationInstance instance, int l_start,
			int l_end, int tag) {
		scorer_.reset();
		consumeTagPair(scorer_, instance, l_start, l_end, tag);
		return scorer_.getScore();
	}

	public double getTransitionScore(SegmentationInstance instance,
			int last_tag, int tag, int l_start, int l_end) {
		scorer_.reset();
		consumeTransition(scorer_, instance, l_start, l_end, last_tag, tag);
		return scorer_.getScore();
	}

	private void consumeTagPair(IndexConsumer consumer,
			SegmentationInstance instance, int l_start, int l_end, int tag) {
		assert l_start >= 0 && l_end <= instance.getLength();
		
		
		consumePairFeature(consumer, instance, l_start, l_end, tag);
		consumeCharacterFeature(consumer, instance, l_start, l_end, tag);
		consumeTagFeature(consumer, instance, l_start, l_end, tag);
		
		if (!dictionaries_.isEmpty()) {
			String segment = instance.getWord().getWord().substring(l_start, l_end);
			int length = l_end - l_start;
			assert segment.length() == length;
			assert length <= max_segment_length_;
			
			int dict_index = 0;
			for (Dictionary dictionary : dictionaries_) {
				
				boolean value = dictionary.contains(segment);
				prepareEncoder();
				encoder_.append(DICT_FEAT, FEATURE_BITS);
				encoder_.append(dict_index, dictionary_bits_);
				encoder_.append(0, max_segment_length_bits_);
				encoder_.append(value);
				consumer.consume(encoder_, tags_to_subtags_.get(tag));
				
				prepareEncoder();
				encoder_.append(DICT_FEAT, FEATURE_BITS);
				encoder_.append(dict_index, dictionary_bits_);
				encoder_.append(length, max_segment_length_bits_);
				encoder_.append(value);
				consumer.consume(encoder_, tags_to_subtags_.get(tag));
				
				dict_index ++;
			}
			
		}
		
	}

	private void consumeCharacterFeature(IndexConsumer consumer,
			SegmentationInstance instance, int l_start, int l_end, int tag) {
		
		if (options_.getBoolean(SegmenterOptions.USE_CHARACTER_FEATURE)) {
			short[] chars = instance.getFormCharIndexes(char_table_);
			
			prepareEncoder();
			encoder_.append(CHARACTER_FEAT, FEATURE_BITS);
			encoder_.append(0, 2);
			encoder_.storeState(encoder_state_);
			for (int window = 1; window <= options_.getInt(SegmenterOptions.MAX_CHARACTER_WINDOW); window++) {
				encoder_.restoreState(encoder_state_);
				addSegment(chars, l_start, l_start + window);
				consumer.consume(encoder_, tags_to_subtags_.get(tag));
			}

			prepareEncoder();
			encoder_.append(CHARACTER_FEAT, FEATURE_BITS);
			encoder_.append(2, 2);
			encoder_.storeState(encoder_state_);
			for (int window = 1; window <= options_.getInt(SegmenterOptions.MAX_CHARACTER_WINDOW); window++) {
				encoder_.restoreState(encoder_state_);
				addSegment(chars, l_end - window, l_end);
				consumer.consume(encoder_, tags_to_subtags_.get(tag));
			}
		}
	}

	private void consumeTransition(IndexConsumer consumer,
			SegmentationInstance instance, int l_start, int l_end,
			int last_tag, int tag) {
		consumeTransitionFeature(consumer, instance, l_start, l_end, last_tag,
				tag);
	}

	public void consumeTagFeature(IndexConsumer consumer,
			SegmentationInstance instance, int l_start, int l_end, int tag) {
		prepareEncoder();
		encoder_.append(TAG_FEAT, FEATURE_BITS);
		consumer.consume(encoder_, tags_to_subtags_.get(tag));
	}

	public void consumePairFeature(IndexConsumer consumer,
			SegmentationInstance instance, int l_start, int l_end, int tag) {
		assert l_start >= 0 && l_end <= instance.getLength();
		
		prepareEncoder();
		short[] chars = instance.getFormCharIndexes(char_table_);
		assert chars.length == instance.getLength();
		
		encoder_.append(PAIR_FEAT, FEATURE_BITS);
		encoder_.append(l_end - l_start, max_segment_length_bits_);
		for (int l = l_start; l < l_end; l++) {
			int c = chars[l];
			if (c < 0) {
				return;
			}
			encoder_.append(c, num_char_bits_);
		}
		consumer.consume(encoder_, tags_to_subtags_.get(tag));
		addCharacterContext(instance, consumer, l_start, l_end, tag);
	}

	public void consumeTransitionFeature(IndexConsumer consumer,
			SegmentationInstance instance, int l_start, int l_end,
			int last_tag, int tag) {
		if (last_tag < 0) {
			return;
		}
		
		for (int last_subtag : tags_to_subtags_.get(last_tag)) {
			prepareEncoder();
			encoder_.append(TRANS_FEAT, FEATURE_BITS);
			encoder_.append(last_subtag, num_tag_bits_);
			consumer.consume(encoder_, tags_to_subtags_.get(tag));
		}
	}

	public void update(SegmentationInstance instance,
			SegmentationResult result, double update) {
		updater_.setUpdate(update);
		Iterator<Integer> tag_iterator = result.getTags().iterator();
		Iterator<Integer> input_iterator = result.getInputIndexes().iterator();

		int last_tag = -1;
		int l_start = 0;

		while (tag_iterator.hasNext()) {
			int tag = tag_iterator.next();
			int l_end = input_iterator.next();
			assert l_end <= instance.getLength();

			if (last_tag >= 0) {
				consumeTransition(updater_, instance, l_start, l_end, last_tag,
						tag);
			}

			assert l_start >= 0 && l_end <= instance.getLength();
			consumeTagPair(updater_, instance, l_start, l_end, tag);

			last_tag = tag;
			l_start = l_end;
		}

	}

	public double getScore(SegmentationInstance instance,
			SegmentationResult result) {
		scorer_.reset();

		Iterator<Integer> tag_iterator = result.getTags().iterator();
		Iterator<Integer> input_iterator = result.getInputIndexes().iterator();

		int last_tag = -1;
		int l_start = 0;

		while (tag_iterator.hasNext()) {
			int tag = tag_iterator.next();
			int l_end = input_iterator.next();

			if (last_tag >= 0) {
				consumeTransition(scorer_, instance, l_start, l_end, last_tag,
						tag);
			}

			consumeTagPair(scorer_, instance, l_start, l_end, tag);
			last_tag = tag;
			l_start = l_end;
		}

		return scorer_.getScore();
	}

	public SegmentationInstance getInstance(Word word) {

		List<SegmentationResult> results = new LinkedList<>();

		for (SegmentationReading reading : word.getReadings()) {

			List<Integer> tags = new ArrayList<>();
			for (String tag : reading.getTags()) {
				int tag_index = tag_table_.toIndex(tag);
				tags.add(tag_index);
			}

			List<Integer> input_indexes = new ArrayList<>();
			int index = 0;
			for (String segment : reading.getSegments()) {
				index += segment.length();
				
				assert index <= word.getLength() : word + " " + reading;
				
				input_indexes.add(index);
			}

			results.add(new SegmentationResult(tags, input_indexes));

		}

		SegmentationInstance instance = new SegmentationInstance(word, results);
		return instance;
	}

	public void setWeights(DynamicWeights weights) {
		setScorerWeights(weights);
		setUpdaterWeights(weights);
	}

	public void setScorerWeights(DynamicWeights weights) {
		scorer_.setWeights(weights);
	}

	public void setUpdaterWeights(DynamicWeights weights) {
		updater_.setWeights(weights);
	}

	public Word toWord(String form, SegmentationResult result) {
		List<String> tags = new ArrayList<>();
		for (int tag_index : result.getTags()) {
			tags.add(tag_table_.toSymbol(tag_index));
		}

		List<String> segments = new ArrayList<>();
		int start_index = 0;
		for (int end_index : result.getInputIndexes()) {
			segments.add(form.substring(start_index, end_index));
			start_index = end_index;
		}

		Word word = new Word(form);
		word.add(new SegmentationReading(segments, tags));
		return word;
	}

	public void update(SegmentationInstance instance, int l_start, int l_end,
			int tag, double update) {
		updater_.setUpdate(update);
		consumeTagPair(updater_, instance, l_start, l_end, tag);
	}

	public void update(SegmentationInstance instance, int l_start, int l_end,
			int last_tag, int tag, double update) {
		updater_.setUpdate(update);
		consumeTransition(updater_, instance, l_start, l_end, last_tag, tag);
	}

	public void printWeights() {
		System.err.println(Arrays.toString(scorer_.getWeights().getWeights()));
		System.err.println(Arrays.toString(updater_.getWeights().getWeights()));
	}

	public void setFinal() {
		updater_ = null;
		scorer_.setInsert(false);
		scorer_.getWeights().setExapnd(false);
		encoder_ = null;
	}

	public IndexScorer getScorer() {
		return scorer_;
	}

	public IndexUpdater getUpdater() {
		return updater_;
	}

	private void addCharacterContext(SegmentationInstance instance,
			IndexConsumer consumer, int l_start, int l_end, int tag_index) {
		if (options_.getBoolean(SegmenterOptions.USE_SEGMENT_CONTEXT)) {

			encoder_.storeState(encoder_state_);
			
			for (int window = 1; window <= options_.getInt(SegmenterOptions.MAX_CHARACTER_WINDOW); window++) {
				encoder_.restoreState(encoder_state_);
				encoder_.append(0, 1);
				addSegment(instance.getFormCharIndexes(char_table_), l_start- window, l_start);
				consumer.consume(encoder_, tags_to_subtags_.get(tag_index));
			}

			for (int window = 1; window <= options_.getInt(SegmenterOptions.MAX_CHARACTER_WINDOW); window++) {
				encoder_.restoreState(encoder_state_);
				encoder_.append(1, 1);
				addSegment(instance.getFormCharIndexes(char_table_), l_end, l_end + window);
				consumer.consume(encoder_, tags_to_subtags_.get(tag_index));
			}
		}
	}

	private void addSegment(short[] chars, int start, int end) {
		encoder_.append(end - start, window_length_bits_);
		for (int i = start; i < end; i++) {

			int c;
			if (i >= 0 && i < chars.length) {
				c = chars[i];
			} else {
				c = char_table_.size();
			}
			if (c < 0)
				return;
			encoder_.append(c, num_char_bits_);
		}
	}

}
