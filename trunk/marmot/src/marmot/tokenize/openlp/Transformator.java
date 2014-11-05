package marmot.tokenize.openlp;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import marmot.util.LevenshteinLattice;
import marmot.util.StringUtils;

public class Transformator {

	private static Pattern mark = Pattern.compile("\\p{Punct}",
			Pattern.CASE_INSENSITIVE);

	public static String transform(String tok, String unTok) {
		StringBuilder nlpFormat = new StringBuilder();
		int tokIdx = 0;
		for (int i = 0; i < unTok.length(); i++) {
			try {
				if (unTok.charAt(i) != tok.charAt(tokIdx)) {
					if (tok.charAt(tokIdx) == ' ') {
						nlpFormat.append(SPLIT);
						if (mark.matcher(
								Character.toString(tok.charAt(++tokIdx)))
								.find()) {
							while (tok.charAt(tokIdx + 1) != ' '
									&& (tok.charAt(tokIdx + 1) != unTok
											.charAt(i + 1)))
								tokIdx++;
						}
					} else {
						while (tok.charAt(tokIdx + 1) != ' '
								&& (tok.charAt(tokIdx + 1) != unTok
										.charAt(i + 1)))
							tokIdx++;
					}
				}
				nlpFormat.append(unTok.charAt(i));
				tokIdx++;
			} catch (StringIndexOutOfBoundsException e) {
				nlpFormat.append(".");
			}
		}
		return nlpFormat.toString();
	}

	public static final String SPLIT = "<SPLIT>";

	public static String transformLevenshtein(String tok, String untok) {

		// Set the cost for Replace higher then Insert + Delete so we don't have
		// to deal with it
		LevenshteinLattice lattice = new LevenshteinLattice(untok, tok, 1, 1, 3);
		List<List<Character>> operations_list = lattice
				.searchOperationSequences();

		System.err.println(operations_list.size());
		
		for (List<Character> operations : operations_list) {

			List<String> segments = new LinkedList<String>();

			boolean invalid = false;
			int tok_index = 0;
			int untok_index = 0;

			for (int i = 0; i < operations.size(); i++) {
				char op = operations.get(i);
				// assert op != 'R';

				// Basic idea we only apply insert operations where
				// the inserted character is a space.
				// Replace operations are treated as insert

				if (op == 'I') {
					if (tok.charAt(tok_index) == ' ') {
						if (!can_add_split(segments)) {
							invalid = true;
							break;
						}
						segments.add(SPLIT);
					}

					tok_index += 1;
				}

				if (op == 'D') {
					segments.add(Character.toString(untok.charAt(untok_index)));
					untok_index += 1;
				} else if (op == 'C') {
					segments.add(Character.toString(untok.charAt(untok_index)));
					tok_index += 1;
					untok_index += 1;
				}

			}

			if (lastIsSpace(segments)) {
				invalid = true;
			}
			
			if (invalid) {
				continue;
			}
			
			assert (!invalid(segments));

			return StringUtils.join(segments);

		}

		throw new RuntimeException("Couldn't find a solution.");

	}
	
	private static boolean lastIsSpace(List<String> segments) {
		return segments.size() > 0 && is_space(segments.get(segments.size() - 1));
	}

	private static boolean can_add_split(List<String> segments) {
		if (segments.isEmpty()) {
			return false;
		}
		
		return !lastIsSpace(segments);
	}

	private static boolean is_space(String segment) {
		return segment == null || segment.equals(" ") || segment.equals(SPLIT);
	}

	private static boolean invalid(List<String> segments) {
		String last_segment = null;
		for (String segment : segments) {
			if (is_space(segment)) {

				if (is_space(last_segment)) {
					return true;
				}

			}

			last_segment = segment;
		}

		return is_space(last_segment);
	}
}
