// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

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
		List<String> tags = new LinkedList<String>();

		List<Pair> pairs = new LinkedList<Pair>();

		while (iterator.hasNext()) {

			List<String> line = iterator.next();

			if (line.size() > 0) {

				String char_code = line.get(0);
				String tag = line.get(1);

				char c = (char) Integer.parseInt(char_code);

				if (tag.equals("S")) {
					// Tag starts a new sentence.
					// Create a new pair and reset untokenized / tags.
					
					if (untokenized.length() > 0) {
						// Create new sentence pair
						pairs.add(Pair.fromTags(untokenized.toString(), tags));
					}
					
					// Reset untokenized and tags
					untokenized.setLength(0);
					tags = new LinkedList<String>();			
					
					// Change tag to beginning of token
					tag = "T";
					
				}

				untokenized.append(c);
				tags.add(tag);
			}

		}

		return pairs;

	}

	public static void main(String[] args) {

		ElephantReader reader = new ElephantReader();

		String path = "/marmot/test/english.small.iob";
		InputStream input_stream = reader.getClass().getResourceAsStream(path);
		
		System.out.println(reader.readFile(input_stream));
	}

}
