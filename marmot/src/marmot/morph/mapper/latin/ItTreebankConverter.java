// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.mapper.latin;

import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import marmot.core.Sequence;
import marmot.core.Tagger;
import marmot.morph.MorphModel;
import marmot.morph.MorphOptions;
import marmot.morph.Sentence;
import marmot.morph.Word;
import marmot.morph.mapper.Node;
import marmot.morph.mapper.SyntaxTree;
import marmot.morph.mapper.SyntaxTreeIterator;
import marmot.morph.mapper.latin.LdtMorphTag.Pos;
import marmot.util.Counter;
import marmot.util.FileUtils;

public class ItTreebankConverter {

	Counter<String> amb_counter;
	Map<String, Counter<String>> amb_map;
	LatMorReader latmor_reader_;

	public ItTreebankConverter(String latmore_file, String missing_file,
			String wrong_file) throws IOException {

		ItMorphTag.VERBOSE = false;

		latmor_reader_ = new LatMorReader();
		try {
			latmor_reader_.readLatMorFile(latmore_file);
			if (missing_file != null) {
				latmor_reader_.readMissingFile(missing_file);
			}
			if (wrong_file != null) {
				latmor_reader_.readWrongFile(wrong_file);
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		amb_counter = new Counter<String>();
		amb_map = new HashMap<String, Counter<String>>();
	}

	public void convert(String in_treebank_file, String out_treebank_file)
			throws IOException {
		List<SyntaxTree> trees = readInitialTrees(in_treebank_file);
		replaceUnkownPosTags(trees);
		
		Calendar c = Calendar.getInstance();
		
		String date_string = String.format("-%d-%d-%d", c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));		
		
		out_treebank_file = out_treebank_file + date_string + ".conll"; 
		
		Writer writer = FileUtils.openFileWriter(out_treebank_file);
		for (SyntaxTree tree : trees) {
			tree.write(writer);
			writer.write('\n');
		}
		writer.close();
	}

	static private final Set<Pos> nominals = new HashSet<Pos>();
	static {
		nominals.add(Pos.p);
		nominals.add(Pos.n);
		nominals.add(Pos.m);
		nominals.add(Pos.a);
	}

	public Word nodeToWord(Node node, boolean delete_x) {
		String form = node.getForm();
		String lemma = node.getLemma();
		Set<Pos> candidates = latmor_reader_.getPosCandidates(form, lemma);
		String[] feats;
		LdtMorphTag tag = (LdtMorphTag) node.getMorphTag();
		Pos pos = tag.pos_;

		form = node.getLemma();

		if (nominals.contains(pos)) {
			feats = new String[nominals.size() + 1];
			int index = 0;
			for (Pos p : nominals) {
				feats[index++] = p.toString();
			}
			feats[index++] = node.getDeprel();
		} else if (pos == Pos.x && !delete_x) {

			candidates.retainAll(nominals);
			if (candidates.isEmpty()) {
				candidates = nominals;
			}

			feats = new String[candidates.size() + 1];

			int index = 0;
			for (Pos p : candidates) {
				feats[index++] = p.toString();
			}
			feats[index++] = node.getDeprel();
		} else {
			form = form.toUpperCase();
			feats = new String[1];
			feats[0] = form.toUpperCase();
		}

		return new Word(form, pos.toString(), null, feats, null, null);
	}

	public void replaceUnkownPosTags(List<SyntaxTree> trees) {
		List<Sequence> sentences = new LinkedList<Sequence>();
		for (SyntaxTree tree : trees) {
			List<Word> words = new LinkedList<Word>();
			for (Node node : tree.getNodes()) {
				words.add(nodeToWord(node, true));
			}
			sentences.add(new Sentence(words));
		}

		MorphOptions opts = new MorphOptions();
		opts.setProperty(MorphOptions.SHAPE, "false");
		opts.setProperty(MorphOptions.TAG_MORPH, "false");
		opts.setProperty(MorphOptions.ORDER, "2");
		opts.setProperty(MorphOptions.VERBOSE, "false");
		opts.setProperty(MorphOptions.NUM_ITERATIONS, "10");

		Tagger tagger = MorphModel.train(opts, sentences, null);

		replaceUnkownPosTagsWithTagger(trees, tagger);
	}

	public void replaceUnkownPosTagsWithTagger(List<SyntaxTree> trees,
			Tagger tagger) {

		Counter<Pos> counter = new Counter<Pos>();

		for (SyntaxTree tree : trees) {
			List<Word> words = new LinkedList<Word>();
			boolean contains_unkown = false;
			for (Node node : tree.getNodes()) {
				words.add(nodeToWord(node, false));

				if (((LdtMorphTag) node.getMorphTag()).pos_ == Pos.x) {
					contains_unkown = true;
				}

			}

			if (contains_unkown) {
				Sentence sentence = new Sentence(words);
				List<List<String>> tags = tagger.tag(sentence);

				assert tags.size() == tree.getNodes().size();

				for (int i = 0; i < tags.size(); i++) {
					Node node = tree.getNodes().get(i);
					LdtMorphTag tag = (LdtMorphTag) node.getMorphTag();
					if (tag.pos_ == Pos.x) {
						switch (tags.get(i).get(0)) {
						case "n":
							tag.pos_ = Pos.n;
							break;
						case "a":
							tag.pos_ = Pos.a;
							break;
						case "m":
							tag.pos_ = Pos.m;
							break;
						case "p":
							tag.pos_ = Pos.p;
							break;
						default:
							System.err.println("Unexpected tag: " + tag);
							break;
						}
						counter.increment(tag.pos_, 1.0);
					}
				}
			}
		}

		System.err.print("Replacement statistics: ");
		System.err.println(counter);

	}

	private List<SyntaxTree> readInitialTrees(String in_treebank_file) {
		List<SyntaxTree> trees = new LinkedList<SyntaxTree>();

		SyntaxTreeIterator iterator = new SyntaxTreeIterator(in_treebank_file,
				1, 2, 4, 5, 6, 7, false);

		while (iterator.hasNext()) {

			SyntaxTree tree = iterator.next();

			for (Node node : tree.getNodes()) {

				String form = LatMorNormalizer.normalize(node.getForm());
				node.setForm(form);
				String lemma = LatMorNormalizer.normalize(node.getLemma());

				int index = lemma.indexOf('^');
				if (index >= 0) {
					lemma = lemma.substring(0, index);
				}

				node.setLemma(lemma);
				String deprel = node.getDeprel().toLowerCase();
				node.setDeprel(deprel);

				String fpos = node.getPos();
				String cpos = fpos;
				if (fpos.length() == 2 && !fpos.equalsIgnoreCase("oq")) {
					cpos = fpos.substring(1);
					assert Character.isDigit(cpos.charAt(0));
				}
				String feats = node.getFeats();
				String tag_string = String
						.format("%s %s %s", cpos, fpos, feats);

				LdtMorphTag tag;
				if (cpos.equals("Punc")) {
					tag = new LdtMorphTag();
					tag.pos_ = Pos.u;
				} else {
					tag = convert(form, lemma, tag_string, deprel);
				}

				node.setMorphTag(tag);

			}
			trees.add(tree);
		}

		System.err.print("Ambiguity stats: ");
		System.err.println(amb_counter);
		Counter<String> counter = amb_map.get("unk");
		if (counter != null) {
			System.err.println("Unknown forms: " + counter.size());
			// for (Map.Entry<String, Double> entry : counter.sortedEntries()) {
			// System.err.format("%s %g\n", entry.getKey(), entry.getValue());
			// }
		}

		return trees;
	}

	public LdtMorphTag convert(String form, String lemma, String tag_string,
			String deprel) {
		BrandoliniRules rules = new BrandoliniRules();
		ItLdtMapper mapper = new ItLdtMapper();
		ItMorphTag it_tag = (ItMorphTag) ItMorphTag.parseString(tag_string);
		LdtMorphTag ldt_tag = (LdtMorphTag) mapper.convert(it_tag);

		Set<Pos> candidates = rules
				.getCandidates(form, lemma, deprel, ldt_tag, it_tag);
		if (candidates.isEmpty()) {
			candidates = mapper.getPosCandidates(ldt_tag, it_tag);
		}

		if (candidates.isEmpty()) {
			ldt_tag.pos_ = Pos.Undef;
		} else if (candidates.size() == 1) {
			ldt_tag.pos_ = candidates.iterator().next();
		} else {
			ldt_tag.pos_ = mergeWithLatMor(form, lemma, candidates, ldt_tag);
		}

		return ldt_tag;
	}

	public Pos mergeWithLatMor(String form, String lemma, Set<Pos> candidates,
			LdtMorphTag ldt_tag) {

		Pos pos = Pos.Undef;

		Set<Pos> tags = latmor_reader_.getPosCandidates(form, lemma);
		if (!tags.isEmpty()) {
			pos = mergeWithLatMor(tags, candidates, ldt_tag, form, false);

			if (pos != Pos.Undef) {
				return pos;
			}
		} else {
			// System.err.println(form + " " + lemma + " " + latmor_reader_.getLemmas(form));
		}

		Set<String> lemmas = latmor_reader_.getLemmas(form);

		if (lemmas != null && lemmas.contains("_")) {
			tags = latmor_reader_.getPosCandidates(form, "_");

			pos = mergeWithLatMor(tags, candidates, ldt_tag, form, false);

			if (pos != Pos.Undef) {
				return pos;
			}
			
		} else {
			addToCounter("unk", form);
		}

		if (pos == Pos.Undef) {
			pos = ambiguous(candidates);
			addToCounter(pos.toString(), form);
		}

		return pos;
	}

	private Pos ambiguous(Set<Pos> merged_set) {

		if (merged_set.size() > 1) {

			if (merged_set.contains(Pos.a) || merged_set.contains(Pos.m)
					|| merged_set.contains(Pos.p) || merged_set.contains(Pos.n)) {
				assert !merged_set.contains(Pos.c)
						|| merged_set.contains(Pos.r);
				return Pos.x;
			}

		}

		return Pos.Undef;
	}

	private Pos mergeWithLatMor(Set<Pos> tags, Set<Pos> candidates,
			LdtMorphTag ldt_tag, String form, boolean found_lemma) {

		Set<Pos> merged_set = new HashSet<Pos>(candidates);
		merged_set.retainAll(tags);

		if (merged_set.size() == 1) {
			return merged_set.iterator().next();
		}

		if (!found_lemma)
			addToAmbCounter(merged_set, form);

		return Pos.Undef;
	}

	private void addToAmbCounter(Set<Pos> merged_set, String form) {
		List<String> list = new LinkedList<String>();
		for (Pos pos : merged_set) {
			list.add(pos.toString());

		}
		Collections.sort(list);
		if (list.isEmpty()) {
			list.add(".");
		}

		addToCounter(list.toString(), form);

	}

	private void addToCounter(String string, String form) {
		amb_counter.increment(string, 1.0);

		Counter<String> forms = amb_map.get(string);
		if (forms == null) {
			forms = new Counter<String>();
			amb_map.put(string, forms);
		}

		forms.increment(form, 1.0);

	}

	public static void main(String[] args) throws IOException {
		ItTreebankConverter conv = new ItTreebankConverter(args[0], args[1],
				args[2]);
		conv.convert(args[3], args[4]);
	}

}
