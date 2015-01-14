package marmot.test.tokenizer.opennlp;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import marmot.tokenize.RuleBasedTokenizer;

import org.junit.Test;



public class RuleBasedTokenizerTest {
	
	private RuleBasedTokenizer tokenizer_;
	
	public RuleBasedTokenizerTest(){
		tokenizer_ = new RuleBasedTokenizer("data/es/open_nlp_style.txt", "es");
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
