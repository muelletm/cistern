// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class FileUtils {

	public static BufferedReader openFile(String filename) throws IOException {
		return openFileReader(filename);
	}

	public static void saveToFile(Serializable object, String filepath) {
		try {
			ObjectOutputStream stream = new ObjectOutputStream(
					new GZIPOutputStream(new FileOutputStream(filepath)));
			stream.writeObject(object);
			stream.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	@SuppressWarnings("unchecked")
	public static <E extends Serializable> E loadFromFile(String filepath) {
		try {
			ObjectInputStream stream = new ObjectInputStream(
					new GZIPInputStream(new FileInputStream(filepath)));

			Object object = stream.readObject();
			stream.close();

			if (object == null) {
				throw new RuntimeException("Object couldn't be deserialized: "
						+ filepath);
			}

			E new_object;

			try {
				new_object = (E) object;
			} catch (ClassCastException e) {
				throw new RuntimeException(
						"Does not seem to be of right type a: " + filepath);
			}

			return new_object;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static BufferedReader openFileReader(String filename) throws IOException {
		InputStream stream = openFileInputStream(filename);
		if (filename.endsWith(".gz")) {
			stream = new GZIPInputStream(stream);
		}		
		
		return new BufferedReader(new InputStreamReader(stream, "UTF-8"));
	}

	private static InputStream openFileInputStream(String filename) throws FileNotFoundException {
		return new FileInputStream(filename);
	}

	public static BufferedReader openStream(InputStream in) throws IOException {
		return new BufferedReader(new InputStreamReader(in, "UTF-8"));
	}

}
