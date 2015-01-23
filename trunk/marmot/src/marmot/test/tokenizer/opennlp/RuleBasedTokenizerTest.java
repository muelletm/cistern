package marmot.test.tokenizer.opennlp;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import marmot.tokenize.RuleBasedTokenizer;
import marmot.tokenize.Tokenizer;
import marmot.tokenize.openlp.OpenNlpTokenizerTrainer;
import marmot.tokenize.rules.RuleProvider;

import org.junit.Test;

public class RuleBasedTokenizerTest {
	
	private Tokenizer tokenizer_;
	private BufferedReader examples_;
	
	private String getResourceFile(String name) {
		String source = "src"+File.separatorChar;
		Package pack = getClass().getPackage();		
		String path = pack.getName().replace('.', File.separatorChar)+File.separatorChar;
		return String.format("%s%s%s", source, path, name);
	}
	
	public RuleBasedTokenizerTest(){
		
		OpenNlpTokenizerTrainer trainer = new OpenNlpTokenizerTrainer();
		
		Tokenizer tokenizer;
		try {
			System.out.println(getResourceFile(""));
			examples_ = new BufferedReader(new FileReader(getResourceFile("RBTT_examples.txt")));
			tokenizer = trainer.train("data/es/open_nlp_style.txt");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		RuleProvider provider = RuleProvider.createRuleProvider("es");
		tokenizer_ = new RuleBasedTokenizer(tokenizer, provider);
	}
	
	@Test
	public void testTokenizer(){		
		try {
			String untok_1 = examples_.readLine();
			List<String> result_1 = new ArrayList<String>();
			for(String token : examples_.readLine().split(" ")) {
				result_1.add(token);
			}
			List<String> prediction_1 = tokenizer_.tokenize(untok_1);
			assertEquals(prediction_1, result_1);
			
			String untok_2 = examples_.readLine();
			List<String> result_2 = new ArrayList<String>();
			for(String token : examples_.readLine().split(" ")) {
				result_2.add(token);
			}
			List<String> prediction_2 = tokenizer_.tokenize(untok_2);
			assertEquals(prediction_2, result_2);
			
			String untok_3 = examples_.readLine();
			List<String> result_3 = new ArrayList<String>();
			for(String token : examples_.readLine().split(" ")) {
				result_3.add(token);
			}
			List<String> prediction_3 = tokenizer_.tokenize(untok_3);
			assertEquals(prediction_3, result_3);
			
			String untok_4 = examples_.readLine();
			List<String> result_4 = new ArrayList<String>();
			for(String token : examples_.readLine().split(" ")) {
				result_4.add(token);
			}
			List<String> prediction_4 = tokenizer_.tokenize(untok_4);
			assertEquals(prediction_4, result_4);
			
			examples_.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
