package marmot.test.tokenizer.opennlp;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import marmot.tokenize.RuleBasedTokenizer;
import marmot.tokenize.Tokenizer;
import marmot.tokenize.openlp.OpenNlpTokenizerTrainer;
import marmot.tokenize.rules.RuleProvider;

import org.junit.Test;

public class RuleBasedTokenizerTest {
	
	private Tokenizer tokenizer_;
	
	public RuleBasedTokenizerTest(){
		
		OpenNlpTokenizerTrainer trainer = new OpenNlpTokenizerTrainer();
		
		Tokenizer tokenizer;
		try {
			tokenizer = trainer.train("data/es/open_nlp_style.txt");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		RuleProvider provider = RuleProvider.createRuleProvider("es");
		tokenizer_ = new RuleBasedTokenizer(tokenizer, provider);
	}
	
	@Test
	public void testTokenizer(){
		String untok_1 = "Y que dicen cuando se van?";
		List<String> result_1 = Arrays.asList(
				  new String("Y"),
				  new String("que"),
				  new String("dicen"),
				  new String("cuando"),
				  new String("se"),
				  new String("van"),
				  new String("?")
				);
		List<String> prediction_1 = tokenizer_.tokenize(untok_1);
		assertEquals(prediction_1, result_1);
	}
}
