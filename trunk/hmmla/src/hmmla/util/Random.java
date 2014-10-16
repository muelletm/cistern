// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.util;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class Random {
	// cf. Random Numbers In Scientific Computing: An Introduction
	public static long getRandomSeed() {
		RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        String jvmName = bean.getName();
        long pid = Long.valueOf(jvmName.split("@")[0]);
        long time = System.currentTimeMillis();
        return Math.abs(((time * 181) * ((pid - 83) * 359)) % 104729);
	}
}
