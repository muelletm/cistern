package marmot.lemma.toutanova;

import java.util.Iterator;
import java.util.List;

import marmot.util.HashableIntArray;

public class Result implements Comparable<Result> {

	private List<Integer> outputs_;
	private List<Integer> inputs_;
	private Model model_;
	private double score_;
	private String form_;
	private HashableIntArray signature_;

	public Result(Model model, List<Integer> outputs, List<Integer> inputs, String form, double score) {
		model_ = model;
		outputs_ = outputs;
		inputs_ = inputs;
		score_ = score;
		form_ = form;
	}
	
	public Result(Model model, List<Integer> outputs, List<Integer> inputs, String form) {
		this(model, outputs, inputs, form, Double.NEGATIVE_INFINITY);
	}

	public String getOutput() {
		StringBuilder sb = new StringBuilder();
		
		Iterator<Integer> output_iterator = outputs_.iterator();
		Iterator<Integer> input_iterator = inputs_.iterator();
				
		Integer input_start_index = 0;
		
		while (output_iterator.hasNext()) {
			assert input_iterator.hasNext();
			Integer output_index = output_iterator.next();
			Integer input_end_index = input_iterator.next();
		
			String output_segment;
			if (output_index == 0) {
				output_segment = form_.substring(input_start_index, input_end_index);
			} else {
				output_segment = model_.getOutput(output_index);
			}
			
			sb.append(output_segment);
			input_start_index = input_end_index;
		}
		
		assert !input_iterator.hasNext();
		return sb.toString();
	}

	public List<Integer> getOutputs() {
		return outputs_;
	}

	public List<Integer> getInputs() {
		return inputs_;
	}
	
	public double getScore() {
		return score_;
	}

	@Override
	public int compareTo(Result result) {
		return - Double.compare(score_, result.score_);
	}
	
	public Result setSignature(HashableIntArray signature) {
		signature_ = signature;
		return this;
	}

	public HashableIntArray getSignature() {
		return signature_;
	}

}
