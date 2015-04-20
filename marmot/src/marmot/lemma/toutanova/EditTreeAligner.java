// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.lemma.toutanova;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import marmot.util.edit.EditTree;
import marmot.util.edit.EditTreeBuilder;
import marmot.util.edit.MatchNode;
import marmot.util.edit.ReplaceNode;

public class EditTreeAligner implements Aligner {

	private static final long serialVersionUID = 1L;
	private EditTreeBuilder builder_;
	private boolean merge_empty_input_segments_;

	public EditTreeAligner(EditTreeBuilder builder,
			boolean merge_empty_input_segments) {
		builder_ = builder;
		merge_empty_input_segments_ = merge_empty_input_segments;
	}

	@Override
	public List<Integer> align(String input, String output) {
		EditTree tree = builder_.build(input, output);
		List<Integer> list = new LinkedList<>();
		readAlignment(tree, list, 0, input.length(), 0, output.length());

		if (merge_empty_input_segments_) {
			List<Integer> merged_list = Aligner.StaticMethods
					.mergeEmptyInputSegments(list);

			if (!merged_list.equals(list)) {
				Logger.getLogger(getClass().getName()).info(
						String.format("Merging: %s %s %s -> %s", input, output,
								list, merged_list));
			}
			
			list = merged_list;
		}

		return list;
	}

	private void readAlignment(EditTree tree, List<Integer> list,
			int input_start, int input_end, int output_start, int output_end) {
		if (tree.getClass() == ReplaceNode.class) {
			list.add(input_end - input_start);
			list.add(output_end - output_start);
			return;
		}

		MatchNode match_node = (MatchNode) tree;

		int input_length = 0;
		int output_length = 0;

		EditTree left = match_node.getLeft();

		int match_length = input_end - input_start
				- match_node.getLeftInputLength()
				- match_node.getRightnputLength();

		if (left != null) {
			int left_input_length = match_node.getLeftInputLength();
			int left_output_length = match_node.getLeftInputLength()
					- left.getFixedInputLength() + left.getFixedOutputLength();

			input_length += left_input_length;
			output_length += left_output_length;
			readAlignment(left, list, input_start, input_start + input_length,
					output_start, output_start + output_length);
		}

		input_length += match_length;
		output_length += match_length;

		for (int i = 0; i < match_length; i++) {
			list.add(1);
			list.add(1);
		}

		EditTree right = match_node.getRight();
		if (right != null) {
			readAlignment(right, list, input_start + input_length, input_end,
					output_start + output_length, output_end);
		}
	}

	public EditTreeBuilder getBuilder() {
		return builder_;
	}

}
