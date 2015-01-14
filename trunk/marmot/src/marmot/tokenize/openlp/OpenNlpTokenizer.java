// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.tokenize.openlp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import marmot.tokenize.AbstractTokenizer;

public class OpenNlpTokenizer extends AbstractTokenizer {

	transient TokenizerModel model_;

	private static final long serialVersionUID = 1L;

	public OpenNlpTokenizer(TokenizerModel model) {
		model_ = model;
	}

	@Override
	public List<String> tokenize(String untokenized) {
		Tokenizer tokenizer = (Tokenizer) new TokenizerME(model_);
		return Arrays.asList(tokenizer.tokenize(untokenized));
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
		model_.serialize(oos);
	}

	private void readObject(ObjectInputStream ois)
			throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		model_ = new TokenizerModel(ois);
	}

	public static void main(String[] args) throws IOException {
		String[] langs = {/*"de", "en", */"es"};
		for(String lang : langs) {
			String path = "data/"+lang;
			marmot.tokenize.Tokenizer tok = OpenNlpTokenizerTrainer.train(path+"/open_nlp_style.txt");
			tok.saveToFile(path+"/tokenizer.model");
			System.out.println("Success!");
		}
	}
}
