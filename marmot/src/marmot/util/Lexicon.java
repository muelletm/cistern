package marmot.util;

import java.io.Serializable;

import marmot.util.StringUtils.Shape;

public interface Lexicon extends Serializable {

	public static final int ARRAY_LENGTH = Shape.values().length + 1;
	int[] getCount(String lemma);

}
