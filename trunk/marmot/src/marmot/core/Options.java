// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import marmot.util.Mutable;
import marmot.util.StringUtils;

public class Options extends java.util.Properties {

	public static final long serialVersionUID = 1L;
	public static final String BEAM_SIZE = "beam-size";
	public static final String ORDER = "order";
	public static final String PRUNE = "prune";
	public static final String NUM_ITERATIONS = "num-iterations";
	public static final String PENALTY = "penalty";
	public static final String PROB_THRESHOLD = "prob-threshold";
	public static final String SHUFFLE = "shuffle";
	public static final String CANDIDATES_PER_STATE = "candidates-per-state";
	public static final String EFFECTIVE_ORDER = "effective-order";
	public static final String VECTOR_SIZE = "initial-vector-size";
	public static final String VERBOSE = "verbose";
	public static final String QUADRATIC_PENALTY = "quadratic-penalty";
	public static final String ORACLE = "oracle";
	public static final String MAX_TRANSITION_FEATURE_LEVEL = "max-transition-feature-level";
	public static final String VERY_VERBOSE = "very-verbose";
	public static final String TRAINER = "trainer";
	public static final String AVERAGING = "averaging";
	public static final String SEED = "seed";

	private static final Map<String, String> DEFALUT_VALUES_ = new HashMap<String, String>();
	private static final Map<String, String> COMMENTS_ = new HashMap<String, String>();

	static {
		DEFALUT_VALUES_.put(BEAM_SIZE, "1");
		COMMENTS_
				.put(BEAM_SIZE, "Specify the beam size of the n-best decoder.");
		DEFALUT_VALUES_.put(ORDER, "2");
		COMMENTS_.put(ORDER, "Set the model order.");
		DEFALUT_VALUES_.put(PRUNE, "true");
		COMMENTS_.put(PRUNE, "Whether to use pruning.");
		DEFALUT_VALUES_.put(NUM_ITERATIONS, "10");
		COMMENTS_.put(NUM_ITERATIONS, "Number of training iterations.");
		DEFALUT_VALUES_.put(PENALTY, "0.0");
		COMMENTS_.put(PENALTY, "L1 penalty parameter.");
		DEFALUT_VALUES_.put(PROB_THRESHOLD, "0.01");
		COMMENTS_
				.put(PROB_THRESHOLD,
						"Initial pruning threshold. Changing this value should have almost no effect.");
		DEFALUT_VALUES_.put(SHUFFLE, "true");
		COMMENTS_.put(SHUFFLE,
				"Whether to shuffle between training iterations.");
		DEFALUT_VALUES_.put(CANDIDATES_PER_STATE, "[4, 2, 1.5]");
		COMMENTS_
				.put(CANDIDATES_PER_STATE,
						"Average number of states to obtain after pruning at each order. These are the mu values from the paper.");
		DEFALUT_VALUES_.put(EFFECTIVE_ORDER, "1");
		COMMENTS_.put(EFFECTIVE_ORDER,
				"Maximal order to reach before increasing the level.");
		DEFALUT_VALUES_.put(VECTOR_SIZE, "10000000");
		COMMENTS_.put(VECTOR_SIZE, "Size of the weight vector.");
		DEFALUT_VALUES_.put(VERBOSE, "false");
		COMMENTS_.put(VERBOSE, "Whether to print status messages.");
		DEFALUT_VALUES_.put(QUADRATIC_PENALTY, "0.0");
		COMMENTS_.put(QUADRATIC_PENALTY, "L2 penalty parameter.");
		DEFALUT_VALUES_.put(ORACLE, "false");
		COMMENTS_
				.put(ORACLE,
						"Whether to do oracle pruning. Probably not relevant. Have a look at the paper!");
		DEFALUT_VALUES_.put(MAX_TRANSITION_FEATURE_LEVEL, "-1");
		COMMENTS_.put(MAX_TRANSITION_FEATURE_LEVEL,
				"Something for testing the code. Don't change it.");
		DEFALUT_VALUES_.put(VERY_VERBOSE, "false");
		COMMENTS_.put(VERY_VERBOSE,
				"Whether to print a lot of status messages.");
		DEFALUT_VALUES_.put(TRAINER, CrfTrainer.class.getCanonicalName());
		COMMENTS_
				.put(TRAINER,
						"Which trainer to use. (There is also a perceptron trainer but don't use it.)");
		DEFALUT_VALUES_.put(AVERAGING, "true");
		COMMENTS_.put(AVERAGING, "Whether to use averaging. Perceptron only!");
		DEFALUT_VALUES_.put(SEED, "42");
		COMMENTS_.put(SEED, "Random seed to use for shuffling. 0 for nondeterministic seed");


	}

	public Options() {
		super();
		putAll(DEFALUT_VALUES_);
	}

	public Options(Options options) {
		this();
		putAll(options);
	}

	public String toSimpleString() {
		String string = "";

		Set<Object> key_set = keySet();
		List<String> key_list = new ArrayList<String>(key_set.size());
		for (Object key : keySet()) {
			key_list.add((String) (key));
		}
		Collections.sort(key_list);

		for (String key : key_list) {
			String value = getProperty(key);
			string += String.format("%s = %s\n", key, value);
		}
		return string;
	}

	public void writePropertiesToFile(String filename) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			writer.write(toSimpleString());
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void setPropertiesFromFile(String filename) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			setPropertiesFromReader(reader);
			reader.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String normalizeOption(String option) {
		return option.trim().replace("_", "-").toLowerCase();
	}

	public void setPropertiesFromReader(BufferedReader reader) {

		Pattern p = Pattern.compile("([^:=]*)[:=](.*)");

		try {
			while (reader.ready()) {
				String line = reader.readLine();

				line = line.trim();

				if (line.length() == 0) {
					continue;
				}

				Matcher m = p.matcher(line);

				if (!m.matches()) {
					throw new RuntimeException(String.format(
							"Invalid line: %s\n", line));
				}

				String key = normalizeOption(m.group(1));

				if (!this.containsKey(key)) {
					throw new RuntimeException(String.format(
							"Unknown property: %s\n", key));
				}

				String value = m.group(2).trim();

				if (value.endsWith(";")) {
					value = value.substring(0, value.length() - 1);
				}
				if (value.endsWith("\"") && value.startsWith("\"")) {
					value = value.substring(1, value.length() - 1);
				}
				value = new String(value);

				this.setProperty(key, value);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static final Pattern OPTION_PATTERN = Pattern.compile("-*(.*)");

	public void setPropertiesFromStrings(String[] args) {
		int index = 0;
		while (index < args.length) {
			String option = args[index++];
			Matcher m = OPTION_PATTERN.matcher(option);

			if (!m.matches()) {
				throw new RuntimeException("Unexpected argument: " + option
						+ ". Missing '-'?");
			}

			option = normalizeOption(m.group(1));
			if (option.equalsIgnoreCase("props")) {
				checkBoundaries(index, args);
				setPropertiesFromFile(args[index++]);
			} else if (this.containsKey(option)) {
				checkBoundaries(index, args);
				this.setProperty(option, args[index++]);
			} else {

				usage();

				throw new RuntimeException(String.format(
						"Unknown property: %s\n", option));
			}
		}

		if (getVerbose()) {
			for (Map.Entry<Object, Object> prop : this.entrySet()) {
				System.err.println(prop.getKey() + ": " + prop.getValue());
			}
		}
	}

	private void checkBoundaries(int index, String[] args) {
		if (index >= args.length) {
			throw new RuntimeException("Missing argument");
		}
	}

	public void dieIfPropertyIsEmpty(String property) {
		if (getProperty(property).isEmpty()) {
			usage();
			System.err.format("Error: Property '%s' needs to be set!\n",
					property);
			System.exit(1);
		}
	}

	protected void usage(Map<String, String> defaults,
			Map<String, String> comments) {
		for (Map.Entry<String, String> entry : defaults.entrySet()) {
			System.err.format("\t%s:\n", entry.getKey());
			String comment = comments.get(entry.getKey());
			assert comment != null;
			System.err.format("\t\t%s\n", comment);
			System.err.format("\t\tDefault value: \"%s\"\n", entry.getValue()
					.replaceAll("\\\\", "\\\\\\\\"));
		}
	}

	protected void usage() {
		System.err.println("General Options:");
		usage(DEFALUT_VALUES_, COMMENTS_);
		System.err.println();
	}

	public boolean getPrune() {
		return Boolean.parseBoolean(getProperty(PRUNE));
	}

	public int getBeamSize() {
		return Integer.parseInt(getProperty(BEAM_SIZE));
	}

	public int getOrder() {
		return Integer.parseInt(getProperty(ORDER));
	}

	public int getNumIterations() {
		return Integer.parseInt(getProperty(NUM_ITERATIONS));
	}

	public double getPenalty() {
		return Double.parseDouble(getProperty(PENALTY));
	}

	public double getProbThreshold() {
		return Double.parseDouble(getProperty(PROB_THRESHOLD));
	}

	public boolean getShuffle() {
		return Boolean.parseBoolean(getProperty(SHUFFLE));
	}

	public double[] getCandidatesPerState() {
		double[] array = StringUtils
				.parseDoubleArray(getProperty(CANDIDATES_PER_STATE), new Mutable<Integer>(0));

		for (double element : array) {

			if (element < 1.0) {
				throw new InvalidParameterException("Candidates per state must be >= 1.0: " + getProperty(CANDIDATES_PER_STATE));
			}
		}

		return array;
	}

	public int getEffectiveOrder() {
		return Integer.parseInt(getProperty(EFFECTIVE_ORDER));
	}

	public int getInitialVectorSize() {
		return (int) Double.parseDouble(getProperty(VECTOR_SIZE));
	}

	public boolean getVerbose() {
		return Boolean.parseBoolean(getProperty(VERBOSE));
	}

	public double getQuadraticPenalty() {
		return Double.parseDouble(getProperty(QUADRATIC_PENALTY));
	}

	public boolean getOracle() {
		return Boolean.parseBoolean(getProperty(ORACLE));
	}

	public int getMaxTransitionFeatureLevel() {
		return Integer.parseInt(getProperty(MAX_TRANSITION_FEATURE_LEVEL));
	}

	public boolean getVeryVerbose() {
		return Boolean.parseBoolean(getProperty(VERY_VERBOSE));
	}

	public String getTrainer() {
		return getProperty(TRAINER);
	}

	public boolean getAveraging() {
		return Boolean.parseBoolean(getProperty(AVERAGING));
	}

	public long getSeed() {
		return Long.parseLong(getProperty(SEED));
	}

}
