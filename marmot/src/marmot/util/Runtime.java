// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class Runtime {

	public static long getUsedMemory() {
		java.lang.Runtime r = java.lang.Runtime.getRuntime();
		r.gc();
		return r.totalMemory() - r.freeMemory();
	}
	
	public static long getUsedMemory(Serializable object, boolean compress) {
		try {
			File file = File.createTempFile("memory","ser");
			FileUtils.saveToFile(object, file, compress);			
			long length = file.length();
			file.deleteOnExit();
			return length;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
