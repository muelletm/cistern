// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.mapper.latin;

import java.util.List;

import marmot.util.LineIterator;

public class LatMorNormalizer {

	public static void main(String[] args) {
		
		LineIterator iterator = new LineIterator(args[0]);
		
		while (iterator.hasNext()) {
			List<String> line = iterator.next();
			if (!line.isEmpty()) {
				String form = normalize(line.get(0));			
				System.out.println(form);
				StringBuilder sb = new StringBuilder(form);
				sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
				if (!sb.toString().equals(form))
					System.out.println(sb.toString());
			}
		}
		
	}

	public static String normalize(String form) {
		form = form.toLowerCase();
		form = form.replace("j", "i");
		form = form.replace("iic", "ic");
		form = form.replace("intelli", "intelle");
		return form;
	}
	
}
