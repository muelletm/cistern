// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Mapping extends AbstractMap<String, String> {

	Map<String, String> map_;
	
	public Mapping(String filename) {
		init(filename);
	}
	
	private void init(String filename) {
		map_ = new HashMap<String, String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			
			while (reader.ready()) {
				
				String line = reader.readLine();
				
							
				line = line.trim();
				
				if (line.length() == 0) {
					continue;
				}
				
				String[] tokens = line.split("\\s");
				
				if (tokens.length != 2) {
					reader.close();
					throw new RuntimeException(String.format("Invalid line: %s\n", line));
				}
				
				String fine = tokens[0];
				String coarse = tokens[1];
							
				map_.put(fine, coarse);				
			}
			
			reader.close();
			
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String get(String key) {
		String value = map_.get(key);
		if (value == null) {
				throw new RuntimeException(String.format("Unknown key: %s", key));	
		}
		return value;
	}
	
	@Override
	public Set<java.util.Map.Entry<String, String>> entrySet() {
		return map_.entrySet();
	}
	
}
