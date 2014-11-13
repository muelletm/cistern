// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class FileUtils {

	public static BufferedReader openFile(String filename) throws IOException {
		return openFileReader(filename);
	}

	public static void saveToFile(Serializable object, String filepath) {
		saveToFile(object, new File(filepath), true);
	}

	public static <E extends Serializable> E loadFromFile(String filename) {
		return loadFromFile(new File(filename));
	}
	
	@SuppressWarnings("unchecked")
	public static <E extends Serializable> E loadFromFile(File file) {
		try {
			ObjectInputStream stream = new ObjectInputStream(
					new GZIPInputStream(new FileInputStream(file)));

			Object object = stream.readObject();
			stream.close();

			if (object == null) {
				throw new RuntimeException("Object couldn't be deserialized: "
						+ file.getAbsolutePath());
			}

			E new_object;

			try {
				new_object = (E) object;
			} catch (ClassCastException e) {
				throw new RuntimeException(
						"Does not seem to be of right type a: " + file.getAbsolutePath());
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

	public static void saveToFile(Serializable object, File file, boolean compress) {
		try {
			
			OutputStream ostream = new FileOutputStream(file);
			if (compress) {
				ostream = new GZIPOutputStream(ostream);
			}
			
			ObjectOutputStream stream = new ObjectOutputStream(ostream);
			stream.writeObject(object);
			stream.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void saveToFile(Serializable object, File file) {
		saveToFile(object, file, true);
	}

}
