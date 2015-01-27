// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

import java.util.List;

public class GeneralLevenshteinLattice<T> {
	protected int[][] cost_lattice_;
	protected short[][] op_lattice_;

	protected final static short START = 1;
	protected final static short INSERT = 2;
	protected final static short DELETE = 4;
	protected final static short COPY = 8;
	protected final static short REPLACE = 16;

	protected List<T> input_;
	protected List<T> output_;

	protected int replace_cost_;
	protected int insert_cost_;
	protected int delete_cost_;
	
	private boolean initialized_;

	public GeneralLevenshteinLattice(List<T> input, List<T> output) {
		this(input, output, 1, 1, 2);
	}

	public GeneralLevenshteinLattice(List<T> input, List<T> output, int insert_cost,
			int delete_cost, int replace_cost) {
		input_ = input;
		output_ = output;
		replace_cost_ = replace_cost;
		insert_cost_ = insert_cost;
		delete_cost_ = delete_cost;
		initialized_ = false;		
	}
	
	protected void init() {
		if (! initialized_) {
			fillLattice();
		}
		initialized_ = true;
	}

	protected int min(int a, int b, int c) {
		return Math.min(a, Math.min(b, c));
	}

	protected void fillLattice() {
		int input_length = input_.size();
		int output_length = output_.size();
		cost_lattice_ = new int[input_length + 1][output_length + 1];
		op_lattice_ = new short[input_length + 1][output_length + 1];

		op_lattice_[0][0] = START;

		for (int input_index = 1; input_index <= input_length; input_index++) {
			cost_lattice_[input_index][0] = delete_cost_ * input_index;
			op_lattice_[input_index][0] = DELETE;
		}

		for (int output_index = 1; output_index <= output_length; output_index++) {
			cost_lattice_[0][output_index] = insert_cost_ * output_index;
			op_lattice_[0][output_index] = INSERT;
		}

		for (int input_index = 1; input_index <= input_length; input_index++) {
			T current_input = input_.get(input_index - 1);
			for (int output_index = 1; output_index <= output_length; output_index++) {
				T current_output = output_.get(output_index - 1);

				short diag_op;
				int diag_cost;
				if (current_input.equals(current_output)) {
					diag_op = COPY;
					diag_cost = getCopyCost(input_index);
				} else {
					diag_op = REPLACE;
					diag_cost = getReplaceCost(current_input, current_output);	
				}

				int minimal_diag_cost = cost_lattice_[(input_index - 1)][(output_index - 1)]
						+ diag_cost;
				int minimal_delete_cost = cost_lattice_[(input_index - 1)][output_index]
						+ delete_cost_;
				int minimal_insert_cost = cost_lattice_[input_index][(output_index - 1)]
						+ insert_cost_;

				int minimal_cost = min(minimal_delete_cost,
						minimal_insert_cost, minimal_diag_cost);

				cost_lattice_[input_index][output_index] = minimal_cost;

				short minimal_cost_op = 0;

				if (minimal_cost == minimal_diag_cost) {
					minimal_cost_op |= diag_op;
				}

				if (minimal_cost == minimal_delete_cost) {
					minimal_cost_op |= DELETE;
				}

				if (minimal_cost == minimal_insert_cost) {
					minimal_cost_op |= INSERT;
				}

				op_lattice_[input_index][output_index] = minimal_cost_op;
			}
		}
	}

	protected int getCopyCost(int input_index) {
		return 0;
	}
	
	protected int getReplaceCost(T input, T output) {
		return replace_cost_;
	}

	public int getDistance() {
		init();
		return cost_lattice_[input_.size()][output_.size()];
	}

}