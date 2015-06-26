package chipmunk.segmenter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marmot.util.LineIterator;

public class SegmentationDataReader implements Iterable<Word> {

	private List<Word> words_;
	private int tag_level_;
	private StringNormalizer normalizer_;
	private Map<String, Word> vocab_;
	private String filepath_;

	public SegmentationDataReader(String filepath, String lang, int tag_level) {
		normalizer_ = StringNormalizer.labeledCreate(lang);
		tag_level_ = tag_level;
		filepath_ = filepath;
		words_ = null;
		vocab_ = null;
	}

	public List<Word> map(List<Word> words) {
		getData();
		
		List<Word> new_words = new LinkedList<>();
		for (Word word : words) {
			Word new_word = vocab_.get(word.getWord());
			assert new_word != null;
			new_words.add(new_word);
		}
		return new_words;
	}

	public List<Word> getData() {
		if (words_ == null) {
			vocab_ = new HashMap<>();
			words_ = new LinkedList<Word>();
			for (Word word : this) {
				words_.add(word);
			}
		}
		return words_;
	}

	@Override
	public Iterator<Word> iterator() {

		final LineIterator iterator = new LineIterator(filepath_, "\t");

		return new Iterator<Word>() {

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public Word next() {

				List<String> line = iterator.next();
				assert line.size() == 2;

				String word_string = line.get(0);

				word_string = normalizer_.normalize(word_string);

				Word word = null;
				if (vocab_ != null)
					word = vocab_.get(word_string);

				if (word == null) {
					word = new Word(word_string);

					if (vocab_ != null)
						vocab_.put(word_string, word);
				}

				if (line.size() > 1) {
					addReading(word, line.get(1));
				}

				return word;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	private void addReading(Word word, String reading_string) {

		String[] word_readings = reading_string.split(", ");

		for (String word_reading : word_readings) {

			String[] readings = word_reading.split(" ");
			List<String> segments = new LinkedList<>();
			List<String> tags = new LinkedList<>();

			for (String reading : readings) {

				int index = -1;
				for (int i = 0; i < reading.length(); i++) {

					char c = reading.charAt(i);

					if (c == ':') {

						index = i;
						if (i + 1 < reading.length()
								&& reading.charAt(i + 1) == ':') {
							index = i + 1;
						}

						break;

					}

				}

				String segment;
				String tag;

				if (index < 0 || index == reading.length() - 1) {
					segment = reading;
					tag = null;
				} else {
					segment = reading.substring(0, index);
					tag = reading.substring(index + 1);
				}

				segment = normalizer_.normalize(segment);
				tag = TagSet.getTag(tag, tag_level_);

				segments.add(segment);
				tags.add(tag);
			}

			StringBuilder rejoint_word = new StringBuilder(word.getLength());
			for (String segment : segments) {
				rejoint_word.append(segment);
			}

			assert rejoint_word.toString().equals(word.getWord()) : reading_string
					+ " " + segments;

			word.add(new SegmentationReading(segments, tags));
		}
	}

}
