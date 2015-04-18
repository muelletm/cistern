package marmot.lemma;

import java.io.Serializable;

public interface Lemmatizer extends Serializable {

	public String lemmatize(Instance instance);

}
