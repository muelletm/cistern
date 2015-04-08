package marmot.util.edit;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marmot.lemma.toutanova.Aligner;
import marmot.lemma.toutanova.EditTreeAlginer;
import marmot.util.Counter;

public class EditTreeBuilder {

	private Counter<String> counter_;
	private Map<String, EditTree> cache_;
	private StringBuilder sb_;
	private final static char SEPARATOR = ' ';
	
	public EditTreeBuilder() {
		counter_ = new Counter<>();
		cache_ = new HashMap<>();
		sb_ = new StringBuilder();
	}
	
	public EditTree build(String input, String output) {
		clearCache();
		return build(input, 0, input.length(), output, 0, output.length());
	}

	private void clearCache() {
		cache_.clear();
	}

	public Counter<String> getCounter() {
		return counter_;
	}

	public EditTree build(String input, int input_start, int input_end,
			String output, int output_start, int output_end) {
		
		EditTree best_tree = retrieveFromCache(input_start, input_end, output_start, output_end);

		if (best_tree != null) {
			return best_tree;
		}
		
		for (Match match : longestMatches(input, input_start, input_end, output, output_start, output_end)) {

			EditTree left = null;
			if (input_start < match.getInputStart()
					|| output_start < match.getOutputStart()) {
				left = build(input, input_start, match.getInputStart(), output,
						output_start, match.getOutputStart());
			}

			EditTree right = null;
			if (match.getInputEnd() < input_end || match.getOutputEnd() < output_end) {
				right = build(input, match.getInputEnd(), input_end, output,
						match.getOutputEnd(), output_end);
			}

			EditTree tree = new MatchNode(match, left, right);
			
			if (best_tree == null || tree.getCost(this) < best_tree.getCost(this)) {
				best_tree = tree;
			}
		}
		
		if (best_tree == null) {
			best_tree = new ReplaceNode(input.substring(input_start, input_end), output.substring(output_start, output_end));
		}
		
		addToCache(input_start, input_end, output_start, output_end, best_tree);
		return best_tree;
	}

	private void addToCache(int input_start, int input_end, int output_start,
			int output_end, EditTree tree) {
		String key = getCacheKey(input_start, input_end, output_start, output_end);
		cache_.put(key, tree);
	}

	private EditTree retrieveFromCache(int input_start, int input_end,
			int output_start, int output_end) {
		String key = getCacheKey(input_start, input_end, output_start, output_end);
		return cache_.get(key);
	}

	private String getCacheKey(int input_start, int input_end,
			int output_start, int output_end) {
		sb_.setLength(0);
		sb_.append(Integer.toHexString(input_start));
		sb_.append(SEPARATOR);
		sb_.append(Integer.toHexString(input_end));
		sb_.append(SEPARATOR);
		sb_.append(Integer.toHexString(output_start));
		sb_.append(SEPARATOR);
		sb_.append(Integer.toHexString(output_end));
		return sb_.toString();
	}

	private List<Match> longestMatches(String input, int input_start, int input_end,
			String output, int output_start, int output_end) {

		LinkedList<Match> longest_matches = new LinkedList<>();

		for (int m_input_start = input_start; m_input_start < input_end; m_input_start++) {
			for (int m_output_start = output_start; m_output_start < output_end; m_output_start++) {

				int length = 0;

				while (true) {

					int i = m_input_start + length;
					if (i >= input_end) {
						break;
					}

					int o = m_output_start + length;
					if (o >= output_end) {
						break;
					}

					if (input.charAt(i) != output.charAt(o)) {
						break;
					}

					length++;
				}

				if (length > 0) {

					if (longest_matches.isEmpty() || longest_matches.getFirst().getLength() <= length) {
						
						
						
						if (!longest_matches.isEmpty() && longest_matches.getFirst().getLength() < length) {
							longest_matches.clear();
						}
						
						longest_matches.add(new Match(m_input_start, m_output_start,
								length));
						
					}
				}

			}
		}

		return longest_matches;
	}

	public static void main(String[] args) {
		Counter<String> counter = new Counter<>();
		test("look", "look", counter);
		test("looks", "look", counter);
		test("gelaufen", "laufen", counter);
		test("gezogen", "ziehen", counter);
		test("umgezogen", "umziehen", counter);
	}
	
	private static void test(String input, String output, Counter<String> counter) {
		EditTreeBuilder b = new EditTreeBuilder();
		
		b.setCounter(counter);
		
		EditTree tree = b.build(input, output);
		tree.increment(counter);
		System.err.format("%s - %s -> %s\n", input, output, tree);
		
		EditTreeAlginer aligner = new EditTreeAlginer(b);
		
		List<Integer> list = aligner.align(input, output);
		List<Aligner.Pair> pairs = Aligner.Pair.toPairs(input, output, list);
		
		System.err.format("%s - %s -> %s %s\n", input, output, pairs, list);
	}

	private void setCounter(Counter<String> counter) {
		counter_ = counter;
	}
	
}
