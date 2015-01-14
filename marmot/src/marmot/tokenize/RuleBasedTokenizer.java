package marmot.tokenize;

import java.io.IOException;
import java.util.List;

import marmot.tokenize.Tokenizer;
import marmot.tokenize.openlp.OpenNlpTokenizerTrainer;
import marmot.tokenize.rules.RuleProvider;
import marmot.tokenize.rules.RulebasedTransformator;

public class RuleBasedTokenizer implements Tokenizer {

	private static final long serialVersionUID = 1214140578027691025L;
	private Tokenizer tokenizer_;
	private RulebasedTransformator untok_transformator_;
	
	public RuleBasedTokenizer(String trainData, String lang){ // default: data/[en|de|es]/open_nlp_style.txt
		try {
			tokenizer_ = OpenNlpTokenizerTrainer.train(trainData);
		} catch (IOException e) {
			e.printStackTrace();
		}
		RuleProvider provider = RuleProvider.createRuleProvider(lang);
		if(provider != null){
			untok_transformator_ = provider.getUnTokTransformator();
		} else {
			untok_transformator_ = null;
		}
	}
	
	@Override
	public List<String> tokenize(String untokenized) {
    	if (untok_transformator_ != null) {
    		untokenized = untok_transformator_.applyRules(untokenized);
    	}
    	return tokenizer_.tokenize(untokenized);
   	}

	@Override
	public void saveToFile(String path) {
		tokenizer_.saveToFile(path);
	}

}
