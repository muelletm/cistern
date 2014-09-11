// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.mapper.czech;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import marmot.morph.mapper.Node;
import marmot.morph.mapper.SyntaxTree;
import marmot.morph.mapper.SyntaxTreeIterator;
import marmot.util.Counter;

public class ConllReader {

	static public void main(String[] args) throws IOException {

		for (String filename : args) {

			SyntaxTreeIterator iterator = new SyntaxTreeIterator(filename, 1, 2,
					4, 6, 8, 10, false);
			
			File file = new File(filename);
			String outfile = file.getName() + ".converted";
			file = new File(outfile);
			if (file.exists()) {
				System.err.println("Error: Outfile already exists: " + file.getAbsolutePath());
				System.exit(1);
			}

			Writer writer = new BufferedWriter(new FileWriter(outfile));

			PdTMsdMapper mapper = new PdTMsdMapper();

			while (iterator.hasNext()) {
				SyntaxTree tree = iterator.next();

				for (Node node : tree.getNodes()) {

					String pos = node.getPos();
					String feats = node.getFeats();

					try {
						PdtMorphTag tag = parse(pos, feats);

						MsdTag new_tag = mapper.map(tag);

						node.setMorphTag(new_tag);

					} catch (Exception e) {
						System.err.println(pos + " " + feats);
						throw e;
					}

				}

				tree.write(writer);
				writer.write('\n');
			}

			writer.close();
		}
	}

	static private PdtMorphTag parse(String pos, String feats) {
		PdtMorphTagReader reader = new PdtMorphTagReader();
		return reader.parse_keyvalue(pos, feats);
	}

	public static Map<String, Counter<String>> getDict(String ptb_file) {
		Map<String, Counter<String>> map = new HashMap<>();

		SyntaxTreeIterator iterator = new SyntaxTreeIterator(ptb_file, 1, 2, 4,
				6, 8, 10, false);

		PdTMsdMapper mapper = new PdTMsdMapper();

		while (iterator.hasNext()) {
			SyntaxTree tree = iterator.next();

			for (Node node : tree.getNodes()) {

				String pos = node.getPos();
				String feats = node.getFeats();

				PdtMorphTag tag = parse(pos, feats);
				MsdTag new_tag = mapper.map(tag);

				Counter<String> counter = map.get(node.getForm());

				if (counter == null) {
					counter = new Counter<>();
					map.put(node.getForm(), counter);
				}

				counter.increment(new_tag.toHumanString(), 1.);
			}
		}

		return map;
	}
}
