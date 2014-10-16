// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.io;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class PosWriter {

	Writer writer_;
	String subtoken_delimiter_ = "\t";
	String token_delimiter_ = "\n";
	String sentence_delimiter_ = "\n";

	public PosWriter(String filename) {

		try {

			if (filename.endsWith(".gz")) {
				writer_ = new BufferedWriter(new OutputStreamWriter(
						new GZIPOutputStream(new FileOutputStream(filename))));
			} else {
				writer_ = new BufferedWriter(new FileWriter(filename));
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public PosWriter(Writer writer) {
		writer_ = writer;
	}

	public PosWriter(PrintStream printStream) {
		writer_ = new OutputStreamWriter(printStream);
	}

	public void write(Sentence sentence) {
		try {
			for (int index = 0; index < sentence.size(); index += 1) {
				Token token = sentence.get(index);
				writer_.write(Integer.toString(index));
				writer_.write(subtoken_delimiter_);
				writer_.write(token.getWordForm());
				if (token.getTag() != null) {
					writer_.write(subtoken_delimiter_);
					writer_.write(token.getTag());
				}
				writer_.write(token_delimiter_);
			}
			writer_.write(sentence_delimiter_);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void write(Iterable<Sentence> sentences) {
		for (Sentence sentence : sentences) {
			write(sentence);
		}
	}
	
	public void close() {
		try {
			writer_.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void write(Sentence sentence, List<String> tags) {
		sentence = new Sentence(sentence);
		sentence.setTags(tags);
		write(sentence);
	}

}
