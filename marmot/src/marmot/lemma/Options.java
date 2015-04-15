package marmot.lemma;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class Options implements Serializable {

	protected Map<String, Object> map_;

	public final static String SEED = "seed";
	public final static String NUM_ITERATIONS = "num-iterations";
	public static final String VERBOSITY = "verbosity";
	public static final String USE_POS = "use-pos";
	public static final String AVERAGING = "averaging";
	public static final String LIMIT = "limit";
	private static final String USE_MORPH = "use-morph";
	
	private Random random_;

	public Options() {
		map_ = new HashMap<>();

		map_.put(SEED, 42L);
		map_.put(NUM_ITERATIONS, 10);
		map_.put(VERBOSITY, 0);
		map_.put(AVERAGING, true);
		map_.put(USE_POS, true);
		map_.put(USE_MORPH, false);
		map_.put(LIMIT, -1);
		
	}

	public Random getRandom() {
		if (random_ == null) {
			random_ = new Random((Long) getOption(SEED));
		}
		return random_;
	}

	public int getNumIterations() {
		return (Integer) getOption(NUM_ITERATIONS);
	}

	public int getVerbosity() {
		return (Integer) getOption(VERBOSITY);
	}
	
	public boolean getUsePos() {
		return (Boolean) getOption(USE_POS);
	}
	
	public boolean getUseMorph() {
		return (Boolean) getOption(USE_MORPH);
	}

	public boolean getAveraging() {
		return (Boolean) getOption(AVERAGING);
	}
	
	public Object getOption(String name) {
		Object current_value = map_.get(name);
		if (current_value == null) {
			throw new RuntimeException("Unknown option: " + name);
		}
		return current_value;
	}

	private static final String BSLASH_SYM = "%%BSLASH%%";
	private static final String COMMA_SYM = "%%COMMA%%"; 
	private static final String SEMICOL_SYM = "%%SEMICOL%%";
	
	public void readArguments(String options_string) {
		if (options_string.equals("_"))
			return;
		
		options_string = options_string.replace("\\,", COMMA_SYM).replace("\\;", SEMICOL_SYM).replace("\\\\", BSLASH_SYM);
		
		for (String option : options_string.split(",")) {
			option = option.replace(COMMA_SYM, ",").replace(SEMICOL_SYM, ";").replace(BSLASH_SYM, "\\");
			
			int index = option.indexOf('=');
			if (index < 0) {
				throw new RuntimeException(String.format("Not = in " + option));
			}
			
			String name = option.substring(0, index);
			String value = option.substring(index + 1);
			setOption(name, value);
		}
	}

	protected Object getValue(Object current_value, Object value) {
		Object new_value = null;

		if (current_value instanceof List) {
			new_value = getListValue((List<Object>) current_value, value);
		} else if (current_value.getClass() == value.getClass()) {
			new_value = value;
		} else {
			if (value.getClass() != String.class) {
				throw new RuntimeException(String.format(
						"Value is of type %s expected type %s.",
						value.getClass(), current_value.getClass()));
			}

			String value_as_string = (String) value;

			if (current_value.getClass() == Integer.class) {
				new_value = Integer.valueOf(value_as_string);
			} else if (current_value.getClass() == Double.class) {
				new_value = Double.valueOf(value_as_string);
			} else if (current_value.getClass() == Long.class) {
				new_value = Long.valueOf(value_as_string);
			} else if (current_value.getClass() == Boolean.class) {
				new_value = Boolean.valueOf(value_as_string);
			} else if (current_value.getClass() == Class.class){
				try {
					new_value = Class.forName(value_as_string);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			} else {
				throw new RuntimeException("Not implemented: "
						+ current_value.getClass());
			}
		}

		return new_value;
	}
	
	public Options setOption(String name, Object value) {
		Object current_value = getOption(name);
		Object new_value = getValue(current_value, value);
		map_.put(name, new_value);
		return this;
	}

	protected Object getListValue(List<Object> current_value, Object value) {
		if (value instanceof List) {
			return value;
		}		
		
		if (value.getClass() != String.class) {
			throw new RuntimeException("Value should be list or string: " + value);
		}
		
		Object first_elem = current_value.get(0);
		
		List<Object> list = new LinkedList<>();
		String value_as_string = (String) value;
		for (String element : value_as_string.split(";")) {
			list.add(getValue(first_elem, element));
		}
		
		return list;
	}

	public String report() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Object> entry : map_.entrySet()) {
			sb.append(String.format("%s: %s\n", entry.getKey(),
					entry.getValue()));
		}
		return sb.toString();
	}

	public Object toInstance(Class<?> klass) {
		try {
			return klass.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Object getInstance(String name) {
		Class<?> klass = (Class<?>) getOption(name);
		return toInstance(klass);
	}

	public int getLimit() {
		return (Integer) getOption(LIMIT);
	}

}
