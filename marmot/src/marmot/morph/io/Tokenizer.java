// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marmot.core.Sequence;
import marmot.core.Token;
import marmot.morph.Sentence;
import marmot.morph.Word;
import marmot.util.Mutable;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

public class Tokenizer {

	private boolean split_between_alphanum_;
	private boolean treat_underscore_as_whitespace_;

	public Tokenizer() {
		this(true, false);
	}

	public Tokenizer(boolean split_between_alphanum,
			boolean treat_underscore_as_whitespace) {
		split_between_alphanum_ = split_between_alphanum;
		treat_underscore_as_whitespace_ = treat_underscore_as_whitespace;
	}

	private static final Map<String, String> token_map_ = new HashMap<String, String>();
	static {
		token_map_.put("-LCB-", "{");
		token_map_.put("-RCB-", "}");
		token_map_.put("-LRB-", "(");
		token_map_.put("-RRB-", ")");
		token_map_.put("-LSB-", "[");
		token_map_.put("-RSB-", "]");
	}

	public Sentence overTokenize(Sentence sentence, boolean add_bio_tag) {
		List<Word> words = new LinkedList<Word>();

		for (Token token : sentence) {
			Word word = (Word) token;
			overTokenize(word, words, add_bio_tag);
		}

		if (words.size() == 0) {
			System.err.println(sentence);
		}
		
		assert words.size() >= sentence.size();
		return new Sentence(words);
	}

	private boolean isWhiteSpace(char c) {
		return Character.isWhitespace(c)
				|| (c == '_' && treat_underscore_as_whitespace_);
	}

	public void overTokenize(Word word, List<Word> words, boolean add_bio_tag) {
		word = replaceWord(word);

		String word_form = word.getWordForm();

		if (word_form.length() == 1) {
			if (!isWhiteSpace(word_form.charAt(0))) {
				words.add(createSingleton(word, add_bio_tag));
			}
			return;
		}

		StringBuilder sb = new StringBuilder(word.getWordForm().length());
		Mutable<Boolean> first = new Mutable<>(true);

		for (int i = 0; i < word_form.length(); i++) {
			char c = word_form.charAt(i);

			if (isWhiteSpace(c)) {

				if (sb.length() > 0) {
					words.add(createWord(word, sb, first, add_bio_tag));
				}

			} else if ((Character.isDigit(c) || Character.isLetter(c))
					|| betweenLetters(i, word_form)) {
				sb.append(c);
			} else {

				if (sb.length() > 0) {
					words.add(createWord(word, sb, first, add_bio_tag));
				}

				sb.append(c);
				words.add(createWord(word, sb, first, add_bio_tag));
			}
		}

		if (sb.length() > 0) {
			if (sb.length() == word_form.length()) {
				words.add(createSingleton(word, add_bio_tag));
			} else {
				words.add(createWord(word, sb, first, add_bio_tag));
			}
		}
	}

	private boolean betweenLetters(int index, String word_form) {
		char c;

		if (!split_between_alphanum_) {
			return false;
		}

		if (index == 0) {
			return false;
		}

		c = word_form.charAt(index - 1);

		if (!(Character.isLetter(c) || Character.isDigit(c))) {
			return false;
		}

		if (index + 1 >= word_form.length()) {
			return false;
		}

		c = word_form.charAt(index + 1);

		if (!(Character.isLetter(c) || Character.isDigit(c))) {
			return false;
		}

		return true;
	}

	private Word replaceWord(Word word) {
		String replacement = token_map_.get(word.getWordForm());
		if (replacement != null) {
			word = word.shallowCopy();
			word.setWordForm(replacement);
		}
		return word;
	}

	private Word createSingleton(Word word, boolean add_bio_tag) {
		Word new_word = word.shallowCopy();
		if (new_word.getPosTag() != null)
			if (add_bio_tag) {
				new_word.setPosTag("B" + "|" + new_word.getPosTag());
			} else {
				new_word.setPosTag(new_word.getPosTag());
			}
		return new_word;
	}

	private Word createWord(Word word, StringBuilder sb,
			Mutable<Boolean> first, boolean add_bio_tag) {
		Word new_word = word.shallowCopy();

		new_word.setWordForm(sb.toString());
		sb.setLength(0);

		if (new_word.getPosTag() != null) {

			if (add_bio_tag) {
				String prefix = "I";
				if (first.get()) {
					prefix = "B";
					first.set(false);
				}
				new_word.setPosTag(prefix + "|" + new_word.getPosTag());
			} else {
				new_word.setPosTag(new_word.getPosTag());
			}

		}

		return new_word;
	}

	public static void main(String[] args) throws JSAPException, IOException {

		FlaggedOption opt;
		JSAP jsap = new JSAP();

		opt = new FlaggedOption("text-file").setRequired(true).setLongFlag(
				"text-file");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("input-format").setRequired(false)
				.setLongFlag("input-format").setDefault("text");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("treat-underscore-as-whitespace")
				.setRequired(false).setStringParser(JSAP.BOOLEAN_PARSER)
				.setLongFlag("treat-underscore-as-whitespace")
				.setDefault("false");

		jsap.registerParameter(opt);

		opt = new FlaggedOption("add-bio-tag").setRequired(false)
				.setStringParser(JSAP.BOOLEAN_PARSER)
				.setLongFlag("add-bio-tag").setDefault("false");

		jsap.registerParameter(opt);

		opt = new FlaggedOption("strip-symbol").setRequired(false).setLongFlag(
				"strip-symbol");

		jsap.registerParameter(opt);

		opt = new FlaggedOption("sentence-split").setRequired(false)
				.setStringParser(JSAP.BOOLEAN_PARSER)
				.setLongFlag("sentence-split").setDefault("false");

		jsap.registerParameter(opt);

		opt = new FlaggedOption("out-file").setRequired(true).setLongFlag(
				"out-file");
		jsap.registerParameter(opt);

		opt = new FlaggedOption("token-limit").setRequired(false)
				.setStringParser(JSAP.INTEGER_PARSER)
				.setLongFlag("token-limit").setDefault("-1");

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

		boolean sentence_tokenize = config.getBoolean("sentence-split");

		Iterable<Sequence> iterable = null;

		String input_format = config.getString("input-format").toLowerCase();
		String strip_symbol = config.getString("strip-symbol");

		switch (input_format) {
		case "text":
			iterable = new SentenceTextReader(config.getString("text-file"));
			break;
		case "conll":
			iterable = new SentenceReader(config.getString("text-file"));
			break;
		default:
			System.err
					.println("Error: input format must be text or conll! Was: "
							+ input_format);
			System.exit(1);
		}

		Tokenizer tokenizer = new Tokenizer(true,
				config.getBoolean("treat-underscore-as-whitespace"));
		Writer writer = new BufferedWriter(new FileWriter(
				config.getString("out-file")));

		tokenizer.tokenize(iterable, writer, sentence_tokenize, strip_symbol,
				config.getBoolean("add-bio-tag"), config.getInt("token-limit"));
		writer.close();
	}

	public int tokenize(Sequence sequence, Writer writer,
			boolean sentence_tokenize, String strip_symbol,
			boolean add_bio_tag, int token_index, int token_limit)
			throws IOException {

		boolean open_bracket = false;

		List<Word> words = new LinkedList<>();

		for (Token token : sequence) {
			Word word = (Word) token;
			overTokenize(word, words, add_bio_tag);
		}

		if (!words.isEmpty()) {

			boolean write_newline = false;

			for (Word word : words) {

				String form = word.getWordForm();

				if (strip_symbol != null && form.equals(strip_symbol)) {
					continue;
				}

				if (word.getPosTag() == null) {
					writer.write(String.format("%s\n", form));
				} else {
					String pos = word.getPosTag();
					String morph = "_";
					if (word.getMorphTag() != null) {
						morph = word.getMorphTag();
					}
					writer.write(String
							.format("%s\t%s\t%s\n", form, pos, morph));
				}

				write_newline = true;

				if (sentence_tokenize) {

					if (form.equals("(")) {
						open_bracket = true;
					}

					if (form.equals(")")) {
						open_bracket = false;
					}

					if (form.equals(".") || form.equals("!")
							|| form.equals("?")) {

						if (!open_bracket) {
							writer.write("\n");
							write_newline = false;
						}
					}
				} else {

					if (token_limit >= 0) {
						write_newline = false;

						token_index += 1;

						if (token_index == token_limit) {

							writer.write("\n");
							token_index = 0;

						}

					}

				}

			}

			if (write_newline) {
				writer.write("\n");
			}
		}

		return token_index;
	}

	public void tokenize(Iterable<Sequence> iterable, Writer writer,
			boolean sentence_tokenize, String strip_symbol,
			boolean add_bio_tag, int token_limit) throws IOException {

		int token_index = 0;

		for (Sequence sequence : iterable) {

			token_index = tokenize(sequence, writer, sentence_tokenize,
					strip_symbol, add_bio_tag, token_index, token_limit);

		}
	}

	public void tokenize(String string, List<Word> words) {
		Word word = new Word(string, null);
		overTokenize(word, words, false);
	}

	public Sentence tokenize(String sentence) {
		List<Word> words = new LinkedList<Word>();
		tokenize(sentence, words);
		return new Sentence(words);
	}

}
