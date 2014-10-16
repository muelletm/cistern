// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.hmm;

import hmmla.Properties;
import hmmla.io.Sentence;
import hmmla.io.Token;
import hmmla.util.SuffixTrie;
import hmmla.util.SymbolTable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Model implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int BorderIndex = 0;
	public static final String BorderSymbol = "<BREAK>";

	protected SymbolTable<String> word_table_;
	protected SymbolTable<String> tag_table_;
	protected Map<String, Tree> top_level_;
	protected Map<String, Tree> clustering_;
	protected Statistics statistics_;

	private List<String> open_tag_classes_;
	private Map<String, List<String>> wordform_to_candidates_;
	private Properties props_;
	private Set<String> rare_;
	private Set<String> vocab_;
	private SuffixTrie suffix_trie_;

	public Model(Model model) {
		word_table_ = model.getWordTable();
		tag_table_ = model.getTagTable();
		top_level_ = model.getTopLevel();
		clustering_ = model.getClustering();
		statistics_ = model.getStatistics();

		rare_ = model.rare_;
		vocab_ = model.vocab_;
		open_tag_classes_ = model.getOpenTagClasses();
		wordform_to_candidates_ = model.getWordformToCandidates();
		props_ = model.getProperties();
	}

	public Model(Iterable<Sentence> reader, Properties props) {
		init(reader, props);
	}

	private void init(Iterable<Sentence> reader, Properties props) {
		props_ = props;
		word_table_ = new SymbolTable<String>();
		tag_table_ = new SymbolTable<String>();
		tag_table_.toIndex(BorderSymbol, true);
		vocab_ = new HashSet<String>();
		
		for (Sentence sentence : reader){
			for (Token token : sentence){			
				tag_table_.toIndex(token.getTag(), true);
				word_table_.toIndex(token.getWordForm(), true);
				vocab_.add(token.getWordForm());
			}
		}
		
		statistics_ = new Statistics(tag_table_, word_table_);
		for (Sentence sentence : reader){
			int fromIndex = BorderIndex;
			for (Token token : sentence){
				String tag = token.getTag();
				int toIndex = tag_table_.toIndex(tag);
				int output = word_table_.toIndex(token.getWordForm());
				statistics_.addEmissions(toIndex, output, 1.0);
				statistics_.addTransitions(fromIndex, toIndex, 1.0);
				fromIndex = toIndex;
			}
			statistics_.addTransitions(fromIndex, BorderIndex, 1.0);		
		}
		
		rare_ = new HashSet<String>();
		for (Entry<String, Integer> entry : word_table_.entrySet()) {
			double total = 0.0;
			for (int index = 0; index < statistics_.getNumTags(); index++) {
				total += statistics_.getEmissions(index, entry.getValue());
			}
			if (total + 0.5 < props_.getIsRareThreshold()) {
				rare_.add(entry.getKey());
			}
		}

		clustering_ = new HashMap<String, Tree>();
		for (Entry<String, Integer> entry : tag_table_.entrySet()) {
			Tree tree = new Tree(entry.getKey(), 0);
			clustering_.put(entry.getKey(), tree);
		}

		top_level_ = new HashMap<String, Tree>(clustering_);

		wordform_to_candidates_ = new HashMap<String, List<String>>();
		Map<String, Set<Integer>> tag_to_words = new HashMap<String, Set<Integer>>();

		for (Entry<String, Integer> entry : word_table_.entrySet()) {
			String word_form = entry.getKey();
			List<String> tags = new LinkedList<String>();

			for (Entry<String, Integer> tag : tag_table_.entrySet()) {
				if (statistics_.getEmissions(tag.getValue(), entry.getValue()) < 0.5) {
					continue;
				}
				
				tags.add(tag.getKey());
				Set<Integer> words = tag_to_words.get(tag.getKey());
				
				if (words == null) {
					words = new HashSet<Integer>();
					tag_to_words.put(tag.getKey(), words);
				}
				words.add(entry.getValue());
			}

			if (tags.size() > 0) {
				wordform_to_candidates_.put(word_form, tags);
			}
		}

		open_tag_classes_ = new LinkedList<String>();
		for (Entry<String, Set<Integer>> entry : tag_to_words.entrySet()) {
			if (entry.getValue().size() > 40) {
				open_tag_classes_.add(entry.getKey());
			}
		}

	}

	public Map<String, List<String>> getWordformToCandidates() {
		return wordform_to_candidates_;
	}
	
	public List<Iterable<Integer>> getSentenceCandidates(Sentence sentence) {
		List<Iterable<Integer>> candidates = new ArrayList<Iterable<Integer>>(sentence.size());
		List<Tree> leaves = new LinkedList<Tree>();
		for (Token token : sentence) {
			
			List<String> candidate_parents = getCandidates(token.getWordForm());
			List<Integer> candidate_list = new LinkedList<Integer>();
			
			for (String parent_string : candidate_parents) {
				leaves.clear();
				Tree parent = top_level_.get(parent_string);
				parent.getLeaves(leaves);
				for (Tree leaf : leaves) {
					candidate_list.add(tag_table_.toIndex(leaf.getName()));
				}
			}
			candidates.add(candidate_list);
		}
		return candidates;
	}

	public List<String> getOpenTagClasses() {
		return open_tag_classes_;
	}

	public List<String> getCandidates(String word_form) {
		List<String> candidates = wordform_to_candidates_.get(word_form);
		if (candidates != null) {
			return candidates;
		}
		return open_tag_classes_;
	}

	public Properties getProperties() {
		return props_;
	}

	public SymbolTable<String> getWordTable() {
		return word_table_;
	}

	public SymbolTable<String> getTagTable() {
		return tag_table_;
	}

	public void setTagTable(SymbolTable<String> tag_table) {
		this.tag_table_ = tag_table;
	}

	public Map<String, Tree> getTopLevel() {
		return top_level_;
	}

	public Map<String, Tree> getClustering() {
		return clustering_;
	}

	public void setClustering(Map<String, Tree> clustering_) {
		this.clustering_ = clustering_;
	}

	public Statistics getStatistics() {
		return statistics_;
	}

	public void setStatistics(Statistics stats) {
		this.statistics_ = stats;
	}

	public void saveToFile(String file_path) {
		try {
			FileOutputStream stream = new FileOutputStream(file_path);
			GZIPOutputStream gzip = new GZIPOutputStream(stream);
			ObjectOutputStream oos = new ObjectOutputStream(gzip);
			oos.writeObject(this);
			oos.close();
			gzip.close();
			stream.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static Model loadFromFile(String file_path) {
		try {
			FileInputStream stream = new FileInputStream(file_path);
			GZIPInputStream gzip = new GZIPInputStream(stream);
			ObjectInputStream ois = new ObjectInputStream(gzip);
			Object object = ois.readObject();
			ois.close();
			gzip.close();
			stream.close();
			boolean is_model = object instanceof Model;
			if (!is_model) {
				throw new RuntimeException("Object at " + file_path
						+ "is not of class Model");
			}
			Model model = (Model) object;
			if (model == null) {
				throw new NullPointerException();
			}
			return model;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public int getLevel() {
		for (Tree tree : getClustering().values()) {
			return tree.getLevel();
		}
		throw new RuntimeException("Clustering is empty!");
	}

	public boolean isRare(String word) {
		return rare_.contains(word);
	}

	public boolean isKnown(String word) {
		return vocab_.contains(word);
	}

	public void setVocab(Set<String> vocab) {
		vocab_ = vocab;
	}

	public void setWordTable(SymbolTable<String> word_table) {
		word_table_ = word_table;
	}

	public void setTopLevel(Map<String, Tree> top_level) {
		top_level_ = top_level;
	}

	public void setSuffixTrie(SuffixTrie trie) {
		suffix_trie_ = trie;
	}

	public SuffixTrie getSuffixTrie() {
		return suffix_trie_;
	}

	public void setProperties(Properties props) {
		props_ = props;
	}

	public int getNumTags() {
		// We don't count the border symbol.
		return tag_table_.size() - 1;
	}
	
}
