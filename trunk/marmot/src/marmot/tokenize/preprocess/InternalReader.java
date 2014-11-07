package marmot.tokenize.preprocess;

public interface InternalReader {

	void mark();

	void reset();

	String readLine();

}
