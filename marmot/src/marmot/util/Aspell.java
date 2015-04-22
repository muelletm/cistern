package marmot.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.lang.Runtime;

public class Aspell implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public final static String ASPELL_PATH = "/mounts/Users/cisintern/muellets/cistern/marmot/cmd/marmot_aspell";

	private transient Process process_;
	private transient BufferedReader out_;
	private transient BufferedWriter in_;
	private transient Map<String, Boolean> cache_;
	
	private String command_line_;
	private String encoding_;

	public Aspell(String marmot_aspell_path, String lang, String encoding) {
		encoding_ = encoding;
		command_line_ = String.format("%s %s %s", marmot_aspell_path, lang,
				encoding);
		init();
	}

	private void readObject(ObjectInputStream ois)
			throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		init();
	}

	private void init() {
		cache_ = new HashMap<>();
		try {
			process_ = Runtime.getRuntime().exec(command_line_);
			out_ = new BufferedReader(new InputStreamReader(
					process_.getInputStream()));
			in_ = new BufferedWriter(new OutputStreamWriter(
					process_.getOutputStream(), encoding_));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized boolean isCorrect(String word) {
		Boolean correct = cache_.get(word);
		if (correct != null)
			return correct;

		try {
			in_.write(word);
			in_.newLine();
			in_.flush();

			while (true) {
				if (out_.ready()) {
					String line = out_.readLine();
					assert line != null;
					assert line.length() == 1;
					char c = line.charAt(0);
					assert c == '0' || c == '1';
					boolean is_correct = c == '0' ? false : true;
					cache_.put(word, is_correct);
					return is_correct;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized void shutdown() {
		try {
			in_.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
