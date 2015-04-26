package marmot.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class KeyValueOptions {

	private Map<String, String> map_ = new HashMap<>();
	private String default_option_;

	public KeyValueOptions(String format) {
		parse(format);
	}

	private void parse(String format) {
		String[] args = format.split(",");
		for (String arg : args) {

			if (arg.length() == 0) {
				continue;
			}

			int index = arg.indexOf('=');

			if (index < 0) {
				if (default_option_ != null)
					throw new RuntimeException("Default option already set: %s"
							+ args);
				default_option_ = arg;
			} else {

				String key = arg.substring(0, index);
				String value = arg.substring(index + 1, arg.length());

				if (map_.containsKey(key)) {
					throw new RuntimeException("Key already definded: %s"
							+ args);
				}

				map_.put(key, value);
			}
		}
	}

	public Integer getValueAsInteger(String option) {
		String value = map_.get(option);
		if (value == null) {
			throw new NoSuchElementException();
		}
		return Integer.valueOf(value);
	}

	public Collection<String> getKeys() {
		return map_.keySet();
	}

	public Collection<String> getSortedKeys() {

		List<String> keys = new LinkedList<>();
		for (String key : getKeys()) {
			keys.add(key);
		}

		Collections.sort(keys);
		return keys;
	}

	public String getDefaultOption() {
		return default_option_;
	}
}
