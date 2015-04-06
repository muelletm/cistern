package marmot.lemma.toutanova;

import java.util.List;

public class Result {

	private List<Integer> outputs_;
	private List<Integer> inputs_;
	private Model model_;
	private double score_;

	public Result(Model model, List<Integer> outputs, List<Integer> inputs, double score) {
		model_ = model;
		outputs_ = outputs;
		inputs_ = inputs;
		score_ = score;
	}
	
	public Result(Model model, List<Integer> outputs, List<Integer> inputs) {
		this(model, outputs, inputs, Double.NEGATIVE_INFINITY);
	}

	public String getOutput() {
		StringBuilder sb = new StringBuilder();
		for (Integer output : outputs_) {
			sb.append(model_.getOutput(output));
		}
		return sb.toString();
	}

	public List<Integer> getOutputs() {
		return outputs_;
	}

	public List<Integer> getInputs() {
		return inputs_;
	}
	
	double getScore() {
		return score_;
	}

}
