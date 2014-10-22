// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.tokenize.openlp;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import marmot.tokenize.Tokenizer;
import opennlp.model.TrainUtil;
import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.tokenize.TokenSample;
import opennlp.tools.tokenize.TokenSampleStream;
import opennlp.tools.tokenize.TokenizerFactory;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

public class OpenNlpTokenizerTrainer {
	
	public final static int CUTOFF = 1;
	
	public Tokenizer train(String path) throws IOException {
		TokenizerModel model;

		ObjectStream<String> line_stream = new PlainTextByLineStream(
				new FileInputStream(path), Charset.forName("UTF-8"));
		ObjectStream<TokenSample> samples = new TokenSampleStream(line_stream);

		String lang_code = "";
		Dictionary dict = null;
		Pattern alpha_numeric_pattern = null;
		
		TokenizerFactory factory = new TokenizerFactory(lang_code, dict, true, alpha_numeric_pattern);
		
		TrainingParameters params = TrainingParameters.defaultParams();
		params.put(TrainUtil.CUTOFF_PARAM, Integer.toString(CUTOFF));
				
		try {
			model = TokenizerME.train(samples, factory, params);
		}
		finally {
		  samples.close();
		}
		
		return new OpenNlpTokenizer(model);		
	}
	
}
