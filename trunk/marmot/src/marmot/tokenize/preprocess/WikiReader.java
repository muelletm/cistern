// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.tokenize.preprocess;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.compress.compressors.bzip2.*;

public class WikiReader implements Iterator<Pair> {

	private Pair pair_;
	private InternalReader untokenized_;
	private InternalReader tokenized_;
	private boolean expand_;

	public WikiReader(InternalReader untokenized, InternalReader tokenized, boolean expand) {
		untokenized_ = untokenized;
		tokenized_ = tokenized;
		expand_ = expand;
	}

	public WikiReader(String untokenized_file, String tokenized_file, boolean expand) {
		this(openFile(untokenized_file), openFile(tokenized_file), expand);
	}

	public static InternalReader openFile(String file) {
		try {
			return new BufferedReaderWrapper(new BufferedReader(
					new InputStreamReader(new BZip2CompressorInputStream(
							new FileInputStream(file)), "UTF-8")));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean hasNext() {
		readNext();
		return pair_ != null;
	}

	protected String readNonEmptyLine(InternalReader reader) {
		String line = reader.readLine();

		if (line == null) {
			throw new NoSuchElementException();
		}
		
		line = line.trim();

		while (line.isEmpty()) {
			line = reader.readLine();

			if (line == null) {
				throw new NoSuchElementException();
			}
			
			line = line.trim();

		}
		return line;
	}

	public void readNext() {
		if (pair_ != null) {
			return;
		}

		try {
		
		String tokenized = readNonEmptyLine(tokenized_);
		String untokenized = readNonEmptyLine(untokenized_);

		pair_ = new Pair(tokenized, untokenized);

		if (expand_)
			expandPair();
		
		} catch (NoSuchElementException e) {
			
		}
		

	}

	protected void expandPair() {
		Pair pair;
		boolean expanded = false;

		// expand left:
		
		try {

		tokenized_.mark();
		pair = new Pair(pair_.tokenized + readNonEmptyLine(tokenized_),
				pair_.untokenized);

		if (pair.score < pair_.score) {
			pair_ = pair;
			expanded = true;
		} else {
			tokenized_.reset(); // not supported in BufferedReaderWrapper!
		}
		
		
		} catch (NoSuchElementException e) {
			
		}

		// expand right:

		untokenized_.mark();
		pair = new Pair(pair_.tokenized, pair_.untokenized
				+ readNonEmptyLine(untokenized_));

		if (pair.score < pair_.score) {
			pair_ = pair;
			expanded = true;
		} else {
			untokenized_.reset(); // not supported in BufferedReaderWrapper!
		}

		if (expanded) {
			expandPair();
		}
	}

	@Override
	public Pair next() {
		readNext();

		if (pair_ == null) {
			throw new NoSuchElementException();
		}

		Pair pair = pair_;
		pair_ = null;
		return pair;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	public List<Pair> readAll() {
		List<Pair> pairs = new LinkedList<Pair>();
		while (hasNext()) {
			pairs.add(next());
		}
		return pairs;
	}
}
