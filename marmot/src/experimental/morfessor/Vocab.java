// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package experimental.morfessor;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import marmot.util.Counter;
import marmot.util.FileUtils;
import marmot.util.LineIterator;
import marmot.util.StringUtils;
import marmot.util.StringUtils.Mode;

public class Vocab {

	private Counter<String> counter_;

	public Vocab(String filename) {

		counter_ = new Counter<String>();

		init(filename, -1);

		System.err.println("|Vocab| = " + counter_.size());

	}

	public static List<String> tokenize(String word) {
		List<String> list = new LinkedList<String>();
		StringBuilder sb = new StringBuilder(word.length());
		for (int index = 0; index < word.length(); index++) {

			char c = word.charAt(index);

			if (Character.isDigit(c) || Character.isLetter(c)) {
				sb.append(c);
			} else {
				if (sb.length() > 0) {
					list.add(sb.toString());
					sb.setLength(0);
				}

				if (!Character.isWhitespace(c)) {
					list.add(Character.toString(c));
				}
			}
		}

		if (sb.length() > 0) {
			list.add(sb.toString());
		}

		return list;
	}

	private void init(String filename, int limit) {
		int lines = 0;
		LineIterator iterator = new LineIterator(filename);

		while (iterator.hasNext() && (limit < 0 || lines < limit)) {
			List<String> line = iterator.next();
			double count = Double.parseDouble(line.get(0));
			for (String word : tokenize(line.get(1))) {
				if (isSpecial(word)) {
					continue;
				}
				word = StringUtils.normalize(word, Mode.lower);
				counter_.increment(word, count);
			}
			lines++;
		}
	}

	public void saveToAsciiFile(String filename) {
		CharEncoder encoder = CharEncoder.fromVocab(this);
		FileUtils.saveToFile(encoder, filename + ".map");

		Counter<String> counter = new Counter<>();
		for (Entry<String, Double> entry : counter_.entrySet()) {

			String form = encoder.encode(entry.getKey());
			Double count = entry.getValue();

			counter.increment(form, count);
		}

		try {
			Writer writer = FileUtils.openFileWriter(filename + ".ascii");

			for (Entry<String, Double> entry : counter.entrySet()) {
				writer.write(String.format("%d %s\n", entry.getValue()
						.intValue(), entry.getKey()));
			}
			writer.close();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Set<Map.Entry<String, Double>> entrySet() {
		return counter_.entrySet();
	}

	public static void main(String[] args) {
		Vocab vocab = new Vocab(args[0]);
		vocab.saveToAsciiFile(args[1]);
	}

	public static boolean isSpecial(String word) {
		return word.length() == 1
				&& !(Character.isDigit(word.charAt(0)) || Character
						.isLetter(word.charAt(0)));
	}

}
