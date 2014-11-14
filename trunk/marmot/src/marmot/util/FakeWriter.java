package marmot.util;

import java.io.IOException;
import java.io.Writer;

public class FakeWriter extends Writer {

	@Override
	public void close() throws IOException {
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void write(char[] arg0, int arg1, int arg2) throws IOException {
	}

}
