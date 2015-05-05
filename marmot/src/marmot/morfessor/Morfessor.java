// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morfessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import marmot.util.FileUtils;
import marmot.util.StringUtils;
import marmot.util.StringUtils.Mode;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

public class Morfessor implements Serializable, Splitter {
	private static final long serialVersionUID = 1L;
	private CharEncoder encoder_;
	private ViterbiDecoder viterbi_;
	private Expander expander_;

	public Morfessor(String train_dir) {
		this(train_dir, null);
	}

	public Morfessor(String train_dir, CharEncoder encoder) {
		viterbi_ = new ViterbiDecoder(train_dir
				+ "/viterbitagsplit2.ii.probs.gz");
		expander_ = new Expander(train_dir + "/viterbitagsplit2.ii.tagged.gz");
		encoder_ = encoder;
	}

	private List<Morpheme> split_(String word) {
		return expander_.expand(viterbi_.split(word));
	}

	public void setEncoder(CharEncoder encoder) {
		encoder_ = encoder;
	}

	public List<String> split(String word) {
		return split(word, 200);
	}

	public List<String> split(String word, int length_limit) {
		List<String> list = new LinkedList<String>();

		for (String token : Vocab.tokenize(word)) {
			if (Vocab.isSpecial(token)) {
				list.add(token);
				continue;
			}

			String new_token = StringUtils.normalize(token, Mode.lower);
			
			if (new_token.length() != token.length()) {
				System.err.println(new_token + " ==> " + token);
			}
			

			if (new_token.length() > length_limit) {
				list.add(new_token);
			} else {

				if (encoder_ != null)
					new_token = encoder_.encode(new_token);

				List<Morpheme> morphemes = split_(new_token);

				int last_index = 0;
				for (Morpheme morph : morphemes) {
					int new_index = last_index + morph.getMorpheme().length();
					String string = token.substring(last_index, new_index);
					last_index = new_index;
					list.add(string);
				}

			}

		}
		return list;
	}

	public static void main(String[] args) throws JSAPException, IOException {
		FlaggedOption opt;
		JSAP jsap = new JSAP();

		opt = new FlaggedOption("morfessor").setRequired(true).setLongFlag(
				"morfessor");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("encoder").setRequired(false).setLongFlag(
				"encoder");
		jsap.registerParameter(opt);

		JSAPResult config = jsap.parse(args);

		if (!config.success()) {
			for (Iterator<?> errs = config.getErrorMessageIterator(); errs
					.hasNext();) {
				System.err.println("Error: " + errs.next());
			}
			System.err.println("Usage: ");
			System.err.println(jsap.getUsage());
			System.err.println(jsap.getHelp());
			System.err.println();
			System.exit(1);
		}

		CharEncoder encoder = null;
		if (config.getString("encoder") != null) {
			encoder = FileUtils.loadFromFile(config.getString("encoder")); 
		}
		
		Morfessor m = new Morfessor(config.getString("morfessor"), encoder);

		String commandLine;
		BufferedReader console = new BufferedReader(new InputStreamReader(
				System.in));

		while (true) {

			System.out.print("morfessor> ");

			commandLine = console.readLine();

			if (commandLine == null) {
				break;
			}
			
			if (commandLine.equals("")) {
				continue;
			}
			
			if (commandLine.equals("q") || commandLine.equals("e") || commandLine.equals("exit") || commandLine.equals("quit")) {
				break;
			}
		
			for (String morph : m.split(commandLine)) {
				System.out.print(' ');
				System.out.print(morph);
			}
			System.out.println();
		}

	}

	// private void test(String filename) {
	// int error = 0;
	// int total = 0;
	//
	// try {
	// BufferedReader reader = File.openFile(filename);
	//
	// while (reader.ready()) {
	// String line = reader.readLine();
	//
	// if (line.startsWith("#")) {
	// continue;
	// }
	//
	// int index;
	// int space_index = line.indexOf(' ');
	// int tab_index = line.indexOf('\t');
	//
	// if (space_index == -1) {
	// index = tab_index;
	// } else if (tab_index == -1) {
	// index = space_index;
	// } else {
	// index = Math.min(tab_index, space_index);
	// }
	//
	// line = line.substring(index + 1);
	// List<Morpheme> morphs = Morpheme.split(line.trim());
	// String word = Morpheme.join(morphs, false, false, "");
	// List<Morpheme> morphs2 = split_(word);
	//
	// if (!morphs.equals(morphs2)) {
	// if (error < 100) {
	// System.err.format("%s : %s <-> %s\n", word, morphs,
	// morphs2);
	// }
	// error++;
	// }
	// total++;
	// }
	//
	// } catch (IOException e) {
	// throw new RuntimeException(e);
	// }
	//
	// System.err.println("Error rate: " + error + " / " + total);
	// }

}
