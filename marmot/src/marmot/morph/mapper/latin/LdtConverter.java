// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.mapper.latin;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import marmot.morph.mapper.Node;
import marmot.morph.mapper.SyntaxTree;
import marmot.morph.mapper.SyntaxTreeIterator;

public class LdtConverter {

	public static void main(String[] args) throws IOException {	
		LdtConverter conv = new LdtConverter();
		conv.convert(args[0], args[1]);
	}

	public void convert(String input_file, String output_file) throws IOException {
		SyntaxTreeIterator iterator = new SyntaxTreeIterator(input_file, 1, 2, 3, 5, 6, 7, false);
		BufferedWriter writer = new BufferedWriter(new FileWriter(
				output_file));
		
		LdtTagReader reader = new LdtTagReader();
		
		while (iterator.hasNext()) {
		
			SyntaxTree tree = iterator.next();
		
			for (Node node : tree.getNodes()) {
				node.setForm(LatMorNormalizer.normalize(node.getForm()));
				node.setLemma(LatMorNormalizer.normalize(node.getLemma()));	
				node.setMorphTag(reader.read(node.getPos(), node.getFeats()));
			}
		
			tree.write(writer);
			writer.write('\n');
		}
		writer.close();
	}
	
}
