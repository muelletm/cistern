// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.hmm;

import hmmla.Properties;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmootherFactory {

	private static final Pattern linear_pattern = Pattern
			.compile("linear\\s*\\(\\s*([0-9]*\\.[0-9.]+)\\s*\\)");

	public static Smoother getSmoother(Properties props) {
		String smoother = props.getSmoother().toLowerCase();
		
		if (smoother.equals("none")) {
			return new IdentitySmoother();
		}

		if (smoother.equals("wb")) {
			return new WbSmoother();
		}

		Matcher m = linear_pattern.matcher(smoother);
		if (m.matches()) {
			double param = Double.parseDouble(m.group(1));
			if (param < 0. || param > 1.0) {
				throw new RuntimeException("Param out of range!: " + param);
			}
			return new LinearSmoother(param);
		}
		
		throw new RuntimeException("Unknown smoother: " + props.getSmoother());
	}

}
