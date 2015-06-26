// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package experimental.morfessor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import marmot.util.FileUtils;
import marmot.util.LineIterator;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

public class CorpusSegmenter {

	public static void main(String[] args) throws JSAPException, IOException {

		FlaggedOption opt;
		JSAP jsap = new JSAP();

		opt = new FlaggedOption("text-file").setRequired(true).setLongFlag(
				"text-file");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("morfessor").setRequired(false).setLongFlag(
				"morfessor");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("encoder").setRequired(false).setLongFlag(
				"encoder");
		jsap.registerParameter(opt);
		
		
		opt = new FlaggedOption("splitter").setRequired(false).setLongFlag(
				"splitter");
		jsap.registerParameter(opt);
		

		opt = new FlaggedOption("out-file").setRequired(true).setLongFlag(
				"out-file");
		jsap.registerParameter(opt);

		JSAPResult config = jsap.parse(args);
		
        if (!config.success()) {
        	for (Iterator<?> errs = config.getErrorMessageIterator();
                    errs.hasNext();) {
                System.err.println("Error: " + errs.next());
            }
            System.err.println("Usage: ");
            System.err.println(jsap.getUsage());
            System.err.println(jsap.getHelp());
            System.err.println();
            System.exit(1);
        }

		Splitter splitter = null;
			
		if (config.getString("splitter") != null) {
			splitter = FileUtils.loadFromFile(config.getString("splitter"));
		} else if (config.getString("morfessor") != null) {
			CharEncoder encoder = null;
			if (config.getString("encoder") != null) {
				try {
				encoder = FileUtils.<CharEncoder>loadFromFile(config.getString("encoder"));
				} catch (RuntimeException e) {
					System.err.println("Caught :" + e);
					System.err.println("Trying text model ..." );
					encoder = CharEncoder.loadFromFile(config.getString("encoder"));
				}
			}
			splitter = new Morfessor(config.getString("morfessor"), encoder);
		} else {
			System.err.println("Error: Either splitter or morfessor must be specified!");
			System.exit(1);
		}
		
		Writer writer = new BufferedWriter(new FileWriter(
				config.getString("out-file")));

		LineIterator iterator = new LineIterator(config.getString("text-file"));
		int count = 0;
		long time = System.currentTimeMillis();
		
		while (iterator.hasNext() /*&& count < 1000000*/) {
			List<String> line = iterator.next();

			boolean first = true;
			for (String word : line) {
				List<String> morphs = splitter.split(word);

				for (String morph : morphs) {
					if (!first)
						writer.write(' ');
					writer.write(morph);
					first = false;
				}

			}
			writer.write('\n');
			
			count++;
			if (count % 100000 == 0) {
				double delta = (System.currentTimeMillis() - time) / 1000.;
				System.err.format("Processing at %g lines/s.\n", count / delta);
			} 

			
		}

		writer.close();
	}

}
