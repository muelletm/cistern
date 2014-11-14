// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import marmot.util.CapStats;
import marmot.util.CapStats.CapType;
import marmot.util.StringUtils.Mode;
import marmot.morph.EvalToken;
import marmot.util.LineIterator;
import marmot.util.StringUtils;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

public class Transformator {

	private final static Set<String> puncts = new HashSet<String>();
	private static final List<String> cats_ = new LinkedList<String>();
	static {
		puncts.add(",");

		cats_.add("postype");
		cats_.add("SubPOS");
		cats_.add("Type");
	}

	public static boolean isPunct(String form) {
		form = form.trim();
		return puncts.contains(form);
	}

	public static void main(String[] args) throws JSAPException, IOException {

		FlaggedOption opt;
		JSAP jsap = new JSAP();

		opt = new FlaggedOption("infile").setRequired(true).setLongFlag(
				"infile");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("outfile").setRequired(true).setLongFlag(
				"outfile");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("upper").setRequired(false)
				.setLongFlag("upper").setStringParser(JSAP.BOOLEAN_PARSER)
				.setDefault("false");

		jsap.registerParameter(opt);

		opt = new FlaggedOption("lower").setRequired(false)
				.setLongFlag("lower").setStringParser(JSAP.BOOLEAN_PARSER)
				.setDefault("false");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("normalize").setRequired(false)
				.setLongFlag("normalize").setStringParser(JSAP.STRING_PARSER)
				.setDefault("none");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("add-cap-tags").setRequired(false)
				.setLongFlag("add-cap-tags")
				.setStringParser(JSAP.BOOLEAN_PARSER).setDefault("false");

		jsap.registerParameter(opt);

		opt = new FlaggedOption("move-subpos").setRequired(false)
				.setLongFlag("move-subpos")
				.setStringParser(JSAP.BOOLEAN_PARSER).setDefault("false");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("punct").setRequired(false)
				.setLongFlag("punct").setStringParser(JSAP.BOOLEAN_PARSER)
				.setDefault("false");
		jsap.registerParameter(opt);

		JSAPResult config = jsap.parse(args);

		if (!config.success()) {
			for (Iterator<?> errs = config.getErrorMessageIterator(); errs
					.hasNext();) {
				System.err.println("Error: " + errs.next());
			}
			System.err.println();
			System.err.println(jsap.getHelp());
			System.err.println();
			System.exit(1);
		}

		FileOptions opts = new FileOptions(config.getString("infile"));

		LineIterator iterator = new LineIterator(opts.getFilename());
		BufferedWriter writer = new BufferedWriter(new FileWriter(
				config.getString("outfile")));

		while (iterator.hasNext()) {
			List<String> tokens = iterator.next();

			if (!tokens.isEmpty()) {

				String form = tokens.get(opts.getFormIndex());

				if (config.getBoolean("upper")) {
					tokens.set(opts.getFormIndex(), form.toUpperCase());
				}

				if (config.getBoolean("lower")) {
					tokens.set(opts.getFormIndex(), form.toLowerCase());
				}

				tokens.set(opts.getFormIndex(), StringUtils.normalize(form, Mode.valueOf(config.getString("normalize"))));

				if (config.getBoolean("move-subpos") && opts.getTagIndex() > -1
						&& opts.getMorphIndex() > -1) {

					String tag = tokens.get(opts.getTagIndex());

					String morph = tokens.get(opts.getMorphIndex());

					if (morph.contains("=")) {

						Map<String, String> dict = EvalToken.splitFeats(morph,
								null);

						for (String cat : cats_) {
							String value = dict.get(cat);
							if (value != null) {
								tag += '|' + cat + "=" + value;
							}
							dict.remove(cat);
						}

						StringBuilder sb = new StringBuilder();
						for (Map.Entry<String, String> entry : dict.entrySet()) {
							if (sb.length() > 0) {
								sb.append('|');
							}
							sb.append(String.format("%s=%s", entry.getKey(),
									entry.getValue()));
						}

						if (sb.length() == 0) {
							morph = "_";
						} else {
							morph = sb.toString();
						}

						tokens.set(opts.getTagIndex(), tag);
						tokens.set(opts.getMorphIndex(), morph);
					}
				}

				if (config.getBoolean("add-cap-tags")) {

					String tag = tokens.get(opts.getTagIndex());
					tokens.set(opts.getMorphIndex(), "pos=" + tag);

					CapType ct = CapStats.getCapType(form);
					String cap_string;
					if (ct == null) {
						cap_string = "Cnone";
					} else {
						cap_string = "C" + ct.toString().toLowerCase();
					}

					tokens.set(opts.getTagIndex(), cap_string);
				}

				if (!(config.getBoolean("punct") && isPunct(form))) {

					boolean first = true;
					for (String field : tokens) {
						if (!first)
							writer.write('\t');
						writer.write(field);
						first = false;
					}
					writer.write('\n');

				}
			} else {
				writer.write('\n');
			}
		}

		writer.close();

	}

}
