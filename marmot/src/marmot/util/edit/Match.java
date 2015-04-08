package marmot.util.edit;

public class Match {
	private int input_start_;
	private int output_start_;
	private int length_;

	public Match(int input_start, int output_start, int length) {
		input_start_ = input_start;
		output_start_ = output_start;
		length_ = length;
	}

	public int getInputStart() {
		return input_start_;
	}

	public int getOutputStart() {
		return output_start_;
	}

	public int getInputEnd() {
		return input_start_ + length_;
	}

	public int getOutputEnd() {
		return output_start_ + length_;
	}

	@Override
	public String toString() {
		return "Match";
	}

	public int getLength() {
		return length_;
	}
}
