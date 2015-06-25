package chipmunk.segmenter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import marmot.util.LineIterator;

public class Dictionary implements Serializable {

	private static final long serialVersionUID = 1L;
	private StringNormalizer normalizer_;
	private Set<String> set_;
	private int min_count_ = 5;

	public Dictionary(String path, String lang, int max_length) {
		normalizer_ = StringNormalizer.rawCreate(lang);
		init(path, max_length);
	}

	private void init(String path, int max_length) {
		set_ = new HashSet<>();

		LineIterator iterator = new LineIterator(path);

		while (iterator.hasNext()) {
			List<String> line = iterator.next();

			assert line.size() == 1 || line.size() == 2 : line;

			int count;
			String segment;

			if (line.size() == 2) {
				segment = line.get(1);
				count = Integer.parseInt(line.get(0));
			} else {
				segment = line.get(0);
				count = min_count_;
			}

			if (count >= min_count_) {
				segment = normalizer_.normalize(segment);

				if (segment.length() <= max_length) {
					set_.add(segment);
				}
			}
		}

		System.err.format("Created dictionary with %d entries from %s\n",
				set_.size(), path);
	}

	boolean contains(String segment) {
		return set_.contains(segment);
	}

}
