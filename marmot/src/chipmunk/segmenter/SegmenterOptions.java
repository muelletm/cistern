package chipmunk.segmenter;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.StringParser;

public class SegmenterOptions implements Serializable {

	private static final long serialVersionUID = 1L;
	public final static String CRF_MODE = "crf-mode";
	public static final String NUM_ITERATIONS = "num-iterations";
	public static final String AVERAGING = "averaging";
	public static final String PENALTY = "penalty";
	public static final String SEED = "seed";
	public static final String MAX_CHARACTER_WINDOW = "max-character-window";
	public static final String USE_SEGMENT_CONTEXT = "use-segment-context";
	public static final String USE_CHARACTER_FEATURE = "use-character-feature";
	public static final String DICTIONARY_PATHS = "dictionary-paths";
	public static final String LANG = "lang";
	public static final String VERBOSE = "verbose";
	public static final String TAG_LEVEL = "tag-level";

	private Map<String, Option> map_;
	private Random random_;

	private static class Option implements Serializable {
		private static final long serialVersionUID = 1L;
		String comment_;
		Object value_;

		Option(String comment, Object value) {
			comment_ = comment;
			value_ = value;
		}
	}

	public SegmenterOptions() {
		map_ = new HashMap<>();

		setDefaults();
	}

	private void setDefaults() {

		addOption(NUM_ITERATIONS, 15, "Num iterations (perceptron)");
		addOption(AVERAGING, true, "Whether to use averaging (perceptron)");
		addOption(CRF_MODE, false, "Train CRF instead of perceptron");
		addOption(PENALTY, 0.0, "Quadratic penalty coefficient (CRF)");
		addOption(SEED, 42l, "RNG seed");
		addOption(MAX_CHARACTER_WINDOW, 3,
				"Maximum character window around segment");
		addOption(USE_SEGMENT_CONTEXT, true,
				"Whether to join character window and segment feature");
		addOption(USE_CHARACTER_FEATURE, true,
				"Use Ruokolinen-style character features.");
		addOption(DICTIONARY_PATHS, "_",
				"Space separated list of dictionary files or '_'");
		addOption(LANG, "_",
				"Iso3 language code. Only used to canonicalize forms");
		addOption(TAG_LEVEL, 0, "The tag level to use");

		addOption(VERBOSE, false, "Verbosity");

	}

	private void addOption(String name, Object value, String comment) {
		map_.put(name, new Option(comment, value));
	}

	private Option getOption(String name) {
		Option opt = map_.get(name);

		if (opt == null) {
			throw new RuntimeException("No such option: " + name);
		}

		return opt;
	}

	private Object getObject(String name) {
		return getOption(name).value_;
	}

	public Integer getInt(String name) {
		return (Integer) getObject(name);
	}

	public String getString(String name) {
		return (String) getObject(name);
	}

	public Boolean getBoolean(String name) {
		return (Boolean) getObject(name);
	}

	public Double getDouble(String name) {
		return (Double) getObject(name);
	}

	public Random getRandom() {
		if (random_ == null) {
			random_ = new Random(getLong(SEED));
		}
		return random_;
	}

	private long getLong(String name) {
		return (Long) getObject(name);
	}

	public void registerOptions(JSAP jsap) throws JSAPException {

		for (Map.Entry<String, Option> entry : map_.entrySet()) {

			String name = entry.getKey();
			String comment = entry.getValue().comment_;
			Object value = entry.getValue().value_;

			StringParser parser;
			if (value.getClass() == String.class) {
				parser = JSAP.STRING_PARSER;
			} else if (value.getClass() == Boolean.class) {
				parser = JSAP.BOOLEAN_PARSER;
			} else if (value.getClass() == Integer.class) {
				parser = JSAP.INTEGER_PARSER;
			} else if (value.getClass() == Long.class) {
				parser = JSAP.LONG_PARSER;
			} else if (value.getClass() == Double.class) {
				parser = JSAP.DOUBLE_PARSER;
			} else {
				throw new RuntimeException(String.format(
						"Unknown type: %s %s\n", name, value.getClass()));
			}

			FlaggedOption opt = new FlaggedOption(name).setStringParser(parser)
					.setLongFlag(name).setDefault(value.toString())
					.setRequired(false).setUsageName(comment);

			jsap.registerParameter(opt);

		}
	}

	public void setOptions(JSAPResult config) {
		for (Map.Entry<String, Option> entry : map_.entrySet()) {
			String name = entry.getKey();
			setOption(name, config.getObject(name));
		}
	}

	public void setOption(String name, Object new_value) {
		Option opt = getOption(name);

		if (opt.value_.getClass() != new_value.getClass()) {
			throw new RuntimeException(String.format(
					"Value is of wrong type, provided: %s, needed: %s",
					new_value.getClass(), opt.value_.getClass()));
		}

		opt.value_ = new_value;
	}

	public Collection<String> getDictionaries() {
		List<String> dicts = new LinkedList<>();
		
		String dict_string = getString(SegmenterOptions.DICTIONARY_PATHS);
		
		String[] dict_strings = dict_string.split("\\s+");
		for (String dict : dict_strings) {
			if (!(dict.isEmpty() || dict.equals("_"))) {
				dicts.add(dict);
			}
		}
		
		return dicts;
	}

}
