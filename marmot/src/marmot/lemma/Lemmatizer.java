package marmot.lemma;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import marmot.morph.io.SentenceReader;

public interface Lemmatizer extends Serializable {

	public String lemmatize(Instance instance);

}
