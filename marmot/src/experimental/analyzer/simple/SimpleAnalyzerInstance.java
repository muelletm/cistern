package experimental.analyzer.simple;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import experimental.analyzer.AnalyzerInstance;
import experimental.analyzer.AnalyzerTag;

import marmot.core.FloatFeatureVector;

public class SimpleAnalyzerInstance implements Serializable {

	private AnalyzerInstance instance_;
	private Collection<AnalyzerTag> tags_;
	private List<Integer> tag_indexes_;
	private int signature_;
	private short[] form_chars_;
	private int[] feat_indexes_;
	private FloatFeatureVector vector_;
	private int[] float_feat_indexes_;
	private double[] float_values_;
	
	public SimpleAnalyzerInstance(AnalyzerInstance instance, Collection<AnalyzerTag> tags) {
		instance_ = instance;
		tags_ = tags;
	}

	public Collection<AnalyzerTag> getTags() {
		return tags_;
	}

	public void setTagIndexes(List<Integer> tag_indexes) {
		tag_indexes_ = tag_indexes;
	}

	public AnalyzerInstance getInstance() {
		return instance_;
	}

	public void setSignature(int signature) {
		signature_ = signature;
	}

	public void setFormChars(short[] form_chars) {
		form_chars_ = form_chars;
	}

	public short[] getFormChars() {
		return form_chars_;
	}

	public int getSignature() {
		return signature_;
	}

	public void setFeatureIndexes(int[] feat_indexes) {
		feat_indexes_ = feat_indexes;
	}

	public Collection<Integer> getTagIndexes() {
		return tag_indexes_;
	}

	public int[] getFeatIndexes() {
		return feat_indexes_;
	}

	public void setVector(FloatFeatureVector vector) {
		vector_ = vector;
	}

	public FloatFeatureVector getVector() {
		return vector_;
	}

	public void setFloatFeatIndexes(int[] indexes) {
		float_feat_indexes_ = indexes;
	}

	public void setFloatValues(double[] values) {
		float_values_ = values;
	}

	public int[] getFloatFeatIndexes() {
		return float_feat_indexes_;
	}

	public double[] getFloatValues() {
		return float_values_;
	}
	
}
