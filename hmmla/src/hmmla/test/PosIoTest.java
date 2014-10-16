// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.test;

import static org.junit.Assert.*;
import hmmla.io.PosFileOptions;
import hmmla.io.PosReader;
import hmmla.io.PosWriter;
import hmmla.io.Sentence;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class PosIoTest {

	private static String writeSentencesToFile(Iterable<Sentence> sentences) {

		File file;
		try {
			file = File.createTempFile("sentences", ".txt");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		PosWriter writer = new PosWriter(file.getAbsolutePath());

		writer.write(sentences);
		writer.close();

		return file.getAbsolutePath();
	}

	private List<Sentence> readSentencesFromFile(String option_string) {
		List<Sentence> sentences = new LinkedList<Sentence>();
		PosReader reader = new PosReader(option_string);

		for (Sentence sentence : reader) {
			sentences.add(sentence);
		}

		return sentences;
	}

	@Test
	public void testReaderBoundaryChecks() {
		List<Sentence> expected = new LinkedList<Sentence>();
		expected.add(new Sentence("Das_d ist_v mein_d Testsatz_n ._p"));
		String file_name = writeSentencesToFile(expected);

		List<Sentence> actual = readSentencesFromFile("form-index=1,tag-index=2," + file_name);
		assertEquals(expected, actual);
		
		try {
			actual = readSentencesFromFile("form-index=5,tag-index=2," + file_name);
			fail();
		}catch (RuntimeException e) {
			assertEquals(e.getCause().getMessage(), "form_index");
		}
		
		try {
			actual = readSentencesFromFile("form-index=-1,tag-index=2," + file_name);
			fail();
		}catch (RuntimeException e) {
			assertEquals(e.getCause().getMessage(), "form_index");
		}
			
		try {
			actual = readSentencesFromFile("form-index=1,tag-index=10," + file_name);
			fail();
		}catch (RuntimeException e) {
			assertEquals(e.getCause().getMessage(), "tag_index");
		}
		
		expected.clear();
		expected.add(new Sentence("0 1 2 3 4"));
		actual = readSentencesFromFile("form-index=0,tag-index=-1," + file_name);
		assertEquals(expected, actual);
		
		expected.clear();
		expected.add(new Sentence("Das ist mein Testsatz ."));
		actual = readSentencesFromFile("form-index=1,tag-index=-1," + file_name);
		assertEquals(expected, actual);
	}

	@Test
	public void testOptions() {

		PosFileOptions options;

		options = new PosFileOptions("form-index=1,filename");
		assertEquals(options.getFormIndex(), 1);
		assertEquals(options.getTagIndex(), -1);
		assertEquals(options.getLimit(), -1);
		assertEquals(options.getFilename(), "filename");

		try {
			options = new PosFileOptions("form-index=1,form-index,filename");
			fail();
		} catch (RuntimeException e) {
			assertEquals(e.getCause().getMessage(), "filename");
		}

		try {
			options = new PosFileOptions("form-index=1,form-index=2,filename");
			fail();
		} catch (RuntimeException e) {
			assertEquals(e.getCause().getMessage(), "form-index");
		}

		try {
			options = new PosFileOptions("");
			fail();
		} catch (RuntimeException e) {
			assertEquals(e.getCause().getMessage(), "no filename");
		}

		try {
			options = new PosFileOptions("file=name");
			fail();
		} catch (RuntimeException e) {
			assertEquals(e.getCause().getMessage(), "option");
		}

	}
}
