package marmot.tokenize.preprocess;

import java.io.BufferedReader;
import java.io.IOException;

public class BufferedReaderWrapper implements InternalReader {


	private BufferedReader reader_;

	public BufferedReaderWrapper(BufferedReader reader) {
		reader_ = reader;
	}

	@Override
	public void mark() {
		try {
			reader_.mark(1000);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void reset() {
		try {
			reader_.reset();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String readLine() {
		try {
			return reader_.readLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
