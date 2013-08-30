// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

public class Runtime {

	public static long getUsedMemory() {
		java.lang.Runtime r = java.lang.Runtime.getRuntime();
		r.gc();
		return r.totalMemory() - r.freeMemory();
	}

}
