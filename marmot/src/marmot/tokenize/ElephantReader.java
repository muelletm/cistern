package marmot.tokenize;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import marmot.util.LineIterator;

// Reads tokenizer training files in the format of elephant : http://gmb.let.rug.nl/elephant/about.php

public class ElephantReader {

	public List<Pair> readFile(InputStream istream) {

		LineIterator iterator = new LineIterator(istream);

		StringBuilder untokenized = new StringBuilder();
		StringBuilder tokenized = new StringBuilder();

		List<Pair> pairs = new LinkedList<>();

		while (iterator.hasNext()) {

			List<String> line = iterator.next();

			if (line.size() > 0) {

				String char_code = line.get(0);
				String tag = line.get(1);

				char c = (char) Integer.parseInt(char_code);

				if (tag.equals("S")) {
					if (untokenized.length() > 0) {
						pairs.add(new Pair(untokenized.toString(), tokenized
								.toString().split(" ")));
					}
					
					untokenized.setLength(0);
					tokenized.setLength(0);
					
					tag = "T";
				}

				if (tag.equals("T")) {
					if (tokenized.length() > 0 && !Character.isWhitespace(tokenized.charAt(tokenized.length() - 1))) {
						tokenized.append(' ');
					}
				}

				untokenized.append(c);
				tokenized.append(c);

			}

		}

		return pairs;

	}
	
	public List<Pair> readFile2(InputStream istream) {

		LineIterator iterator = new LineIterator(istream);

		StringBuilder untokenized = new StringBuilder();
		List<String> tokenized = new LinkedList<>();
		StringBuilder token = new StringBuilder();

		List<Pair> pairs = new LinkedList<>();

		while (iterator.hasNext()) {

			List<String> line = iterator.next();

			if (line.size() > 0) {

				String char_code = line.get(0);
				String tag = line.get(1);

				char c = (char) Integer.parseInt(char_code);

				if (tag.equals("S") || tag.equals("T") || tag.equals("O")) {
					if (token.length() > 0) {
						tokenized.add(token.toString());
					}
					token.setLength(0);
				}				
				
				if (tag.equals("S")) {
					if (untokenized.length() > 0) {
						pairs.add(new Pair(untokenized.toString(), tokenized));
					}
					
					untokenized.setLength(0);
					tokenized = new LinkedList<>();			
					
				}

				untokenized.append(c);
				if (!tag.equals("O")) {
					token.append(c);
				}
				
			}

		}

		return pairs;

	}

	public static void main(String[] args) {

		ElephantReader reader = new ElephantReader();

		String path = "/marmot/test/english.small.iob";

		InputStream input_stream = reader.getClass().getResourceAsStream(path);

		System.out.println(reader.readFile2(input_stream));
	}

}
