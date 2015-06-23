package marmot.segmenter;

import java.util.List;

public class SegmentationResult {

	@Override
	public String toString() {
		return "[" + tags_ + " " + input_indexes_ + "]";
	}

	private List<Integer> tags_;
	private List<Integer> input_indexes_;
	private double score_;

	public SegmentationResult(List<Integer> tags,
			List<Integer> inputs, double score) {
		input_indexes_ = inputs;
		tags_ = tags;
		score_ = score;
	}
	
	public SegmentationResult(List<Integer> tags,
			List<Integer> inputs) {
		this(tags, inputs, Double.NEGATIVE_INFINITY);
	}

	public List<Integer> getTags() {
		return tags_;
	}
	
	public List<Integer> getInputIndexes() {
		return input_indexes_;
	}

	public boolean isCorrect(SegmentationInstance instance) {
		for (SegmentationResult result : instance.getResults()) {
			if (tags_.equals(result.getTags()) && input_indexes_.equals(result.getInputIndexes())) {
				return true;
			}
		}
		return false;
	}

	public boolean isSegmentationCorrect(SegmentationInstance instance) {
		for (SegmentationResult result : instance.getResults()) {
			if (input_indexes_.equals(result.getInputIndexes())) {
				return true;
			}
		}
		return false;
	}

	public double getScore() {
		return score_;
	}

}
