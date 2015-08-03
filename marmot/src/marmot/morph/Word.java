// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph;

import java.util.Arrays;
import java.util.List;

import lemming.lemma.ranker.RankerInstance;
import marmot.core.FeatureVector;
import marmot.core.Token;


public class Word implements Token {
	private static final long serialVersionUID = 1L;
	private String word_;
	private String tag_;
	private String morph_;
	private String[] token_features_;
	private String lemma_;
	
	private int word_index_;
	private short[] char_indexes_;
	private int word_shape_index_;
	private int[] tag_indexes_;
	private int signature_;
	private int[] token_feature_indexes_;
	private FeatureVector vector_;
	private String[] weighted_token_features_;
	private double[] weighted_token_feature_weights_;
	private int[] weighted_token_feature_indexes_;
	//private RankerInstance instance_;
	private List<RankerInstance> instances_;
	
	public Word(String word, String lemma, String tag, String morph, String[] token_features, String[] weighted_token_features, double[] weighted_token_feature_weights) {
		word_ = word;
		lemma_ = lemma;
		tag_ = tag;
		morph_ = morph;
		token_features_ = token_features;
		weighted_token_features_ = weighted_token_features;
		weighted_token_feature_weights_ = weighted_token_feature_weights;
		word_index_ = -1;
		word_shape_index_ = -1;
		signature_ = -1;
	}
	
	public Word(String word, String tag, String morph, String[] token_features, String[] weighted_token_features, double[] weighted_token_feature_weights) {
		this(word, null, tag, morph, token_features, weighted_token_features, weighted_token_feature_weights);
	}
	
	public Word(String word, String tag, String morph) {
		this(word, tag, morph, null, null, null);
	}
	
	public Word(String word, String tag) {
		this(word, tag, null);
	}
	
	public Word(String word) {
		this(word, null);
	}

	public String getWordForm() {
		return word_;
	}
	
	public String getPosTag() {
		return tag_;
	}

	public void setPosTag(String tag) {
		tag_ = tag;
	}
	
	@Override
	public String toString() {
		return word_ + " " + tag_ + " " + morph_ + " " + Arrays.toString(token_features_);
	}

	public void setWordIndex(int word_index) {
		word_index_ = word_index;
	}

	public int[] getTagIndexes() {
		return tag_indexes_;
	}

	public int getWordFormIndex() {
		return word_index_;
	}

	public void setCharIndexes(short[] char_indexes) {
		char_indexes_ = char_indexes;
	}

	public short[] getCharIndexes() {
		return char_indexes_;
	}

	public void setWordShapeIndex(int shape_index) {
		word_shape_index_ = shape_index;
	}

	public int getWordShapeIndex() {
		return word_shape_index_;
	}

	public String getMorphTag() {
		return morph_;
	}

	public void setTagIndexes(int[] tag_indexes) {
		tag_indexes_ = tag_indexes;
	}

	public int getWordSignature() {
		return signature_;
	}

	public void setWordSignature(int signature) {
		signature_ = signature;
	}

	public String[] getTokenFeatures() {
		return token_features_;
	}

	public void setTokenFeatureIndexes(int[] token_feature_indexes) {
		token_feature_indexes_ = token_feature_indexes;
	}

	public int[] getTokenFeatureIndexes() {
		return token_feature_indexes_;
	}

	public void setMorphTag(String morph) {
		morph_ = morph;
	}

	@Override
	public void setVector(FeatureVector vector) {
		vector_ = vector;
	}

	@Override
	public FeatureVector getVector() {
		return vector_;
	}

	public Word shallowCopy() {
		Word word = new Word(word_, tag_, morph_, token_features_, weighted_token_features_, weighted_token_feature_weights_);		
		word.word_index_ = word_index_; 
		word.char_indexes_ = char_indexes_;
		word.word_shape_index_ = word_shape_index_;
		word.tag_indexes_ = tag_indexes_;
		word.signature_ = signature_;
		word.token_feature_indexes_ = token_feature_indexes_;
		word.vector_ = vector_;
		word.weighted_token_feature_indexes_ = token_feature_indexes_;
		return word;
	}

	public void setWordForm(String word) {
		word_ = word;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(char_indexes_);
		result = prime * result + ((morph_ == null) ? 0 : morph_.hashCode());
		result = prime * result + signature_;
		result = prime * result + ((tag_ == null) ? 0 : tag_.hashCode());
		result = prime * result + Arrays.hashCode(tag_indexes_);
		result = prime * result + Arrays.hashCode(token_feature_indexes_);
		result = prime * result + Arrays.hashCode(token_features_);
		result = prime * result + ((vector_ == null) ? 0 : vector_.hashCode());
		result = prime * result + ((word_ == null) ? 0 : word_.hashCode());
		result = prime * result + word_index_;
		result = prime * result + word_shape_index_;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Word other = (Word) obj;
		if (!Arrays.equals(char_indexes_, other.char_indexes_))
			return false;
		if (morph_ == null) {
			if (other.morph_ != null)
				return false;
		} else if (!morph_.equals(other.morph_))
			return false;
		if (signature_ != other.signature_)
			return false;
		if (tag_ == null) {
			if (other.tag_ != null)
				return false;
		} else if (!tag_.equals(other.tag_))
			return false;
		if (!Arrays.equals(tag_indexes_, other.tag_indexes_))
			return false;
		if (!Arrays
				.equals(token_feature_indexes_, other.token_feature_indexes_))
			return false;
		if (!Arrays.equals(token_features_, other.token_features_))
			return false;
		if (vector_ == null) {
			if (other.vector_ != null)
				return false;
		} else if (!vector_.equals(other.vector_))
			return false;
		if (word_ == null) {
			if (other.word_ != null)
				return false;
		} else if (!word_.equals(other.word_))
			return false;
		if (word_index_ != other.word_index_)
			return false;
		if (word_shape_index_ != other.word_shape_index_)
			return false;
		return true;
	}

	public void setWeightedTokenFeatureIndexes(int[] indexes) {
		weighted_token_feature_indexes_ = indexes;
	}

	public String[] getWeightedTokenFeatures() {
		return weighted_token_features_;
	}
	
	public int[] getWeightedTokenFeatureIndexes() {
		return weighted_token_feature_indexes_;
	}

	public double[] getWeightedTokenFeatureWeights() {
		return weighted_token_feature_weights_;
	}

	public void setTokenFeatures(String[] token_features) {
		token_features_ = token_features;
	}

	public String getLemma() {
		return lemma_;
	}

	public List<RankerInstance> getRankerIstances() {
		return instances_;
	}
	
	public void setRankerIstances(List<RankerInstance> instances) {
		instances_ = instances;
	}

}
