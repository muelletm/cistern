// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla;

import hmmla.hmm.Model;
import hmmla.io.PosFileOptions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Properties extends java.util.Properties {
	public static final String SEED = "seed";
	public static final String NUM_THREADS = "num-threads";
	public static final String RANDOMNESS = "randomness";
	public static final String MERGE_FACTOR = "merge-factor";
	public static final String EM_STEPS = "em-steps";
	public static final String NUM_TAGS = "num-tags";
	public static final String EXACT_LOSS = "exact-loss";
	public static final String MERGE = "merge";
	public static final String SAMPLE = "sample";
	public static final String SAMPLING_FRACTION = "sampling-fraction";
	public static final String TRAIN_FILE = "train-file";
	public static final String TEST = "test-file";
	public static final String TEST_FILE = "test-file";
	public static final String HMM_TRAINER = "hmm-trainer";
	public static final String IS_RARE_THRESHOLD = "is-rare-threshold";
	public static final String UNIVERSAL_POS_FILE = "universal-pos-file";
	public static final String LANGUAGE = "language";
	public static final String UNIVERSAL_POS = "universal-pos";
	public static final String MODEL_NAME = "model-name";
	public static final String SMOOTHER = "smoother";
	public static final String REFINE = "refine";
	public static final String COARSE_DECODER = "coarse-decoder";
	public static final String DUMP_INTERMEDIATE_MODEL = "dump-intermediate-model";
	public static final String PRED_FILE = "pred-file";

	


	private static final long serialVersionUID = 1L;

	private static HashMap<String, String> defaultValues = new HashMap<>();
	private static Map<String, String> comments = new HashMap<>();
	
	static {
		defaultValues.put(NUM_THREADS, "1");
		comments.put(NUM_THREADS, "Number of threas");
		defaultValues.put(RANDOMNESS, "0.1");
		comments.put(RANDOMNESS, "Randomness (cf. to the paper)");
		defaultValues.put(MERGE_FACTOR, "0.75");
		comments.put(MERGE_FACTOR, "Merge factor (cf. to the paper)");
		defaultValues.put(EM_STEPS, "10");
		comments.put(EM_STEPS, "Number of EM steps");
		defaultValues.put(EXACT_LOSS, "false");
		comments.put(EXACT_LOSS, "Use exact loss. This is a testing option.");
		defaultValues.put(MERGE, "true");
		comments.put(MERGE, "Whether to merge.");
		defaultValues.put(SAMPLE, "true");
		comments.put(SAMPLE, "Whether to sample. (Uses different parts of the training set at every EM step)");
		defaultValues.put(SAMPLING_FRACTION, "0.1");
		comments.put(SAMPLING_FRACTION, "Sampling fraction. See option " + SAMPLE);
		defaultValues.put(HMM_TRAINER, "signaturehmmtrainer");
		comments.put(HMM_TRAINER, "Which trainer to use: signaturehmmtrainer or simplehmmtrainer");
		defaultValues.put(IS_RARE_THRESHOLD, "5");
		comments.put(IS_RARE_THRESHOLD, "Word form rareness threshold.");
		defaultValues.put(LANGUAGE, "none");
		comments.put(LANGUAGE, "To unable language specific behavior.");
		defaultValues.put(UNIVERSAL_POS, "false");
		comments.put(UNIVERSAL_POS, "Use universal POS.");
		defaultValues.put(SMOOTHER, "wb");
		comments.put(SMOOTHER, "Smoother to use: Can be none, linear(x) or wb. Where x is a real number with 0 < x < 1");
		defaultValues.put(REFINE, "false");
		comments.put(REFINE, "Refine. (cf. to the paper)");
		defaultValues.put(COARSE_DECODER, "false");
		comments.put(COARSE_DECODER, "Use a coarse to fine decoder.");
		defaultValues.put(TEST, "true");
		comments.put(TEST, "Run test. Needs option: " + TEST_FILE);
		defaultValues.put(DUMP_INTERMEDIATE_MODEL, "false");
		comments.put(DUMP_INTERMEDIATE_MODEL, "Write a model after each iteration");
		defaultValues.put(SEED, "");
		comments.put(SEED, "Random seed to use. Empty for random");
		defaultValues.put(TRAIN_FILE, "");
		comments.put(TRAIN_FILE, "Train file");
		defaultValues.put(TEST_FILE, "");
		comments.put(TEST_FILE, "Test file");
		defaultValues.put(PRED_FILE, "");
		comments.put(PRED_FILE, "Pred file");
		defaultValues.put(NUM_TAGS, "");
		comments.put(NUM_TAGS, "Number of tags to induce. Define the number of iterations");
		defaultValues.put(UNIVERSAL_POS_FILE, "");
		comments.put(UNIVERSAL_POS_FILE, "File containing mapping of treebank POS to universal POS");
		defaultValues.put(MODEL_NAME, "");
		comments.put(MODEL_NAME, "Model file to store the model to");
	}

	public Properties() {
		super();
		putAll(defaultValues);
	}

	public long getSeed() {
		return Long.parseLong(getProperty(SEED));
	}

	public int getNumThreads() {
		return Integer.parseInt(getProperty(NUM_THREADS));
	}

	public double getRandomness() {
		return Double.parseDouble(getProperty(RANDOMNESS));
	}

	public double getMergeFactor() {
		return Double.parseDouble(getProperty(MERGE_FACTOR));
	}

	public int getEmSteps() {
		return Integer.parseInt(getProperty(EM_STEPS));
	}

	public int getNumTags() {
		return Integer.parseInt(getProperty(NUM_TAGS));
	}

	public boolean getExactLoss() {
		return Boolean.parseBoolean(getProperty(EXACT_LOSS));
	}

	public boolean getMerge() {
		return Boolean.parseBoolean(getProperty(MERGE));
	}

	public boolean getSample() {
		return Boolean.parseBoolean(getProperty(SAMPLE));
	}

	public double getSamplingFraction() {
		return Double.parseDouble(getProperty(SAMPLING_FRACTION));
	}

	public String getTrainFile() {
		return getProperty(TRAIN_FILE);
	}

	public String getTestFile() {
		return getProperty(TEST_FILE);
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

	public String getHmmTrainer() {
		return getProperty(HMM_TRAINER);
	}

	public int getIsRareThreshold() {
		return Integer.parseInt(getProperty(IS_RARE_THRESHOLD));
	}

	public String getUniversalPosFile() {
		return getProperty(UNIVERSAL_POS_FILE);
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

	public boolean getUniversalPos() {
		return Boolean.parseBoolean(getProperty(UNIVERSAL_POS));
	}

	public String getLanguage() {
		return getProperty(LANGUAGE);
	}

	public String getModelFile() {
		return getProperty(MODEL_NAME);
	}

	public String getSmoother() {
		return getProperty(SMOOTHER);
	}
	
	public boolean getRefine() {
		return Boolean.parseBoolean(getProperty(REFINE));
	}

	public boolean getCoarseDecoder() {
		return Boolean.parseBoolean(getProperty(COARSE_DECODER));
	}

	private static final Pattern OPTION_PATTERN = Pattern.compile("-*(.*)");
	
	public void setPropertiesFromStrings(String[] args) {
		int index = 0;	
		while (index < args.length) {
			String option = args[index++];
			Matcher m = OPTION_PATTERN.matcher(option);
			
			if (!m.matches()) {
				throw new RuntimeException("Unexpected argument: " + option + ". Missing '-'?");
			}
			
			option = normalizeOption(m.group(1));
			if (option.equalsIgnoreCase("props")) {
				checkBoundaries(index, args);
				setPropertiesFromFile(args[index++]);
			} else if (this.containsKey(option)) {
				checkBoundaries(index, args);
				this.setProperty(option, args[index++]);
			} else {
				throw new RuntimeException(String.format("Unknown property: %s\n", option));
			}
		}	
	}

	private void checkBoundaries(int index, String[] args) {
		if (index >= args.length) {
			throw new RuntimeException("Missing argument");
		}
	}

	public void check(String class_name) {
		
		if (class_name.equals(Tagger.class.getSimpleName()) || getTest()) {
			checkNotEmpty(TEST_FILE);
			
			PosFileOptions options = new PosFileOptions(getProperty(TEST_FILE));
			checkFileExists(options.getFile());
		
			if (getTest() || getRefine()) {
				
				if (options.getTagIndex() < 0) {
					throw new RuntimeException("No tag index specified in %s"+ getProperty(TEST_FILE) +"!");
				}
			}
			
		}

		checkNotEmpty(MODEL_NAME);
		
		if (getProperty(Properties.SEED).isEmpty()) {
			long seed = hmmla.util.Random.getRandomSeed();
			setProperty(Properties.SEED, Long.toString(seed));
		}
		
		if (class_name.equals(Trainer.class.getSimpleName())) {
			checkNotEmpty(NUM_TAGS);
			
			checkNotEmpty(TRAIN_FILE);
			checkFileExists(new PosFileOptions(getProperty(TRAIN_FILE)).getFile());
			
		}
		
		if (getUniversalPos()) {
			checkNotEmpty(UNIVERSAL_POS_FILE);
			checkFileExists(new File(getProperty(UNIVERSAL_POS_FILE)));
		}
	}

	private void checkNotEmpty(String option) {
		if (getProperty(option).isEmpty()) {
			throw new RuntimeException("Property: \"" + option + "\" has to be set!");
		}
	}
	
	private void checkFileExists(File file) {
		if (!file.canRead()) {
			throw new RuntimeException("Can't read from: " + file.getAbsolutePath());
		}
	}
	
	public String getIntermediateModelName(Model model) {
		StringBuffer sb = new StringBuffer();
		String model_name = getModelFile();

		int index = model_name.lastIndexOf('.');

		String extension;
		if (index >= 0) {
			extension = model_name.substring(index + 1, model_name.length());
			sb.append(model_name.substring(0, index));
		} else {
			sb.append(model_name);
			extension = "tagger";
		}

		sb.append('_');
		
		int needed_digits = (int) Math.ceil(Math.log10(model.getProperties().getNumTags()));
		int current_digits = (int) Math.ceil(Math.log10(model.getNumTags()));	
		for (int i=current_digits; i<=needed_digits; i++) {
			sb.append('0');	
		}
		
		sb.append(model.getNumTags());

		if (!extension.isEmpty()) {
			sb.append('.');
			sb.append(extension);
		}

		return sb.toString();
	}

	public boolean getTest() {
		return Boolean.parseBoolean(getProperty(TEST));
	}

	public boolean getDumpIntermediateModels() {
		return Boolean.parseBoolean(getProperty(DUMP_INTERMEDIATE_MODEL));
	}

	public String getPredFile() {
		return getProperty(PRED_FILE);
	}

	public void usage() {
		System.err.println("General Options:");
		usage(defaultValues, comments);
		System.err.println();		
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

}
