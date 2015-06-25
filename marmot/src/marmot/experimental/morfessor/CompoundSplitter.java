// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.experimental.morfessor;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

import marmot.util.CollectableDouble;
import marmot.util.FileUtils;
import marmot.util.LineIterator;
import marmot.util.StringUtils;
import marmot.util.StringUtils.Mode;
import marmot.util.Trie;

public class CompoundSplitter implements Serializable, Splitter {

	private static final long serialVersionUID = 1L;

	public CompoundSplitter(boolean normalize) {
		trie_ = new Trie<CollectableDouble>();
		normalize_ = normalize;
	}
	
	private Trie<CollectableDouble> trie_;
	private boolean normalize_;
	
	public CompoundSplitter() {
		this(false);
	}
	
	public void addWord(String word, double count) {
		if (normalize_) {
			word = StringUtils.normalize(word, Mode.lower);
		}
		trie_.addWord(word, new CollectableDouble(count));
	}
	
	public PriorityQueue<Segment> segment(Segment segment, String compound, int start_index, Scorer scorer) {
		PriorityQueue<Segment> queue = new PriorityQueue<>(); 
		
		Trie<CollectableDouble> trie = trie_;
		for (int i=start_index; i < compound.length(); i++) {			
			char c = compound.charAt(i);
			
			trie = trie.getChild(c);
			
			if (trie == null) {
				
				break;
			}
			
			if (trie.isTerminal()) {
				Segment seg = new Segment(segment, i + 1, trie.getContent().getValue(), scorer);			
				
				if (i + 1 == compound.length()) {
					queue.add(seg);
				} else {
					queue.addAll(segment(seg, compound, i + 1, scorer));
				}
			}
			
		}
		
		return queue;
	}

	public PriorityQueue<Segment> segment(String compound, Scorer scorer) {
		if (normalize_) {
			compound = StringUtils.normalize(compound, Mode.lower);
		}
		
		return segment(null, compound, 0, scorer);
	}

	public Trie<CollectableDouble> getTrie() {
		return trie_;
	}
	
	public static void main(String[] args) throws JSAPException {
		FlaggedOption opt;
		JSAP jsap = new JSAP();

		opt = new FlaggedOption("vocab-file").setRequired(true).setLongFlag(
				"vocab-file");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("splitter-file").setRequired(true).setLongFlag(
				"splitter-file");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("min-freq").setRequired(false).setStringParser(JSAP.INTEGER_PARSER).setLongFlag(
				"min-freq").setDefault("10");
		jsap.registerParameter(opt);
		
		opt = new FlaggedOption("min-length").setRequired(false).setStringParser(JSAP.INTEGER_PARSER).setLongFlag(
				"min-length").setDefault("3");
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
		
        int min_freq = config.getInt("min-freq"); 
        int min_length = config.getInt("min-length");
	
		CompoundSplitter splitter = new CompoundSplitter(true);
		
		splitter.loadFile(config.getString("vocab-file"), min_freq, min_length);
		FileUtils.saveToFile(splitter, config.getString("splitter-file"));
	}

	public void loadFile(String filename, int min_freq, int min_length) {
		LineIterator iterator = new LineIterator(filename);
		while (iterator.hasNext()) {
			List<String> list = iterator.next();
			
			String form = list.get(0);
			int freq = Integer.parseInt(list.get(1));
			
			if (freq < min_freq) {
				break;
			}
			
			if (form.length() >= min_length) {
				addWord(form, freq);
			}
			
		}
	}

	public List<String> split(String word, Scorer scorer) {
		PriorityQueue<Segment> queue = segment(word, scorer);
		
		if (queue.isEmpty()) {
			return Collections.singletonList(word);
		}
		
		List<String> list = new LinkedList<>();
		
		Segment segment = queue.poll();
		
		Segment run = segment.getPreviousSegment();
		
		int from_index = word.length();
		
		while (run != null) {
			int to_index = run.getIndex();
			String morph = word.substring(to_index, from_index);
			list.add(0, morph);
			run = run.getPreviousSegment();
			from_index = to_index;
		}
		
		String morph = word.substring(0, from_index);
		list.add(0, morph);
		
		return list;
	}

	@Override
	public List<String> split(String form) {
		return split(form, new MeanScorer());
	}
}
