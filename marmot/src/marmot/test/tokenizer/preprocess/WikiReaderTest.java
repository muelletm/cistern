// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.test.tokenizer.preprocess;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import marmot.tokenize.preprocess.Pair;
import marmot.tokenize.preprocess.InternalReader;
import marmot.tokenize.preprocess.WikiReader;

import org.junit.Test;

public class WikiReaderTest {

	class FakeReader implements InternalReader {

		int current_pos_;
		int current_mark_;
		List<String> content_;

		FakeReader() {
			content_ = new ArrayList<String>();
		}

		@Override
		public void mark() {
			current_mark_ = current_pos_;
		}

		@Override
		public void reset() {
			current_pos_ = current_mark_;
		}

		@Override
		public String readLine() {

			if (current_pos_ >= content_.size()) {
				return null;
			}

			String line = content_.get(current_pos_);
			current_pos_++;
			return line;
		}

		public void add(String line) {
			content_.add(line);
		}

		public void fullReset() {
			current_pos_ = 0;
			current_mark_ = 0;
		}

	}

	@Test
	public void simpleTest() {

		FakeReader a = new FakeReader();
		a.add("abc");
		FakeReader b = new FakeReader();
		b.add("ab");
		b.add("c");

		List<Pair> expected = Arrays.asList(new Pair("abc", "abc"));
		bothWayTest(a, b, expected);
	}

	@Test
	public void test() {

		FakeReader a = new FakeReader();
		a.add("a");
		a.add("");
		a.add("bcd");
		FakeReader b = new FakeReader();
		b.add("abc");
		a.add("");
		a.add("");
		b.add("d");
		a.add("");

		List<Pair> expected = Arrays.asList(new Pair("abcd", "abcd"));
		bothWayTest(a, b, expected);
	}

	private void bothWayTest(FakeReader a, FakeReader b, List<Pair> expected) {
		WikiReader reader = new WikiReader(a, b, true);
		List<Pair> actual = reader.readAll();
		
//		for (Pair p : actual)
//			System.err.println(p);
		
		assertEquals(expected, actual);
		
		a.fullReset();
		b.fullReset();
		reader = new WikiReader(b, a, true);
		actual = reader.readAll();
		assertEquals(expected, actual);
	}

}
