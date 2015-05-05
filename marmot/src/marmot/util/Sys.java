// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class Sys {
	
	private static final long ONE_MEGA_BYTES_IN_BYTES = 1024 * 1024;

	public static long getUsedMemoryInBytes() {
		java.lang.Runtime r = java.lang.Runtime.getRuntime();
		r.gc();
		return r.totalMemory() - r.freeMemory();
	}
	
	public static double getUsedMemoryInMegaBytes() {
		return getUsedMemoryInBytes() / (double) ONE_MEGA_BYTES_IN_BYTES;
	}
	
	public static long getUsedMemoryInBytes(Serializable object, boolean compress) {
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

	public static long getMaxHeapSizeInBytes() {
		java.lang.Runtime r = java.lang.Runtime.getRuntime();
		return r.maxMemory();
	}
	
	public static double getMaxHeapSizeInMegaBytes() {
		return getMaxHeapSizeInBytes() / (double) ONE_MEGA_BYTES_IN_BYTES;
	}

	public static double getUsedMemoryInMegaBytes(Serializable object, boolean compress) {
		return getUsedMemoryInBytes(object, compress) / (double) ONE_MEGA_BYTES_IN_BYTES;
	}

}
