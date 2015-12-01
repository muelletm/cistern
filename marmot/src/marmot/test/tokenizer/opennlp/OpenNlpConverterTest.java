// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.test.tokenizer.opennlp;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import marmot.tokenize.openlp.OpenNlpConverter;
import marmot.tokenize.preprocess.Pair;
import marmot.tokenize.rules.RuleProvider;
import marmot.util.FileUtils;

import org.junit.Test;

public class OpenNlpConverterTest {
	
	private OpenNlpConverter es_converter_;
	private Writer writer_;
	private BufferedReader reader_;
	private File tmp_;
	
	private String getResourceFile(String name) {
		String source = "src"+File.separatorChar;
		Package pack = getClass().getPackage();		
		String path = pack.getName().replace('.', File.separatorChar)+File.separatorChar;
		return String.format("%s%s%s", source, path, name);
	}	
	
	public OpenNlpConverterTest(){
		
		RuleProvider es_provider = RuleProvider.createRuleProvider("es");
		es_converter_ = new OpenNlpConverter(es_provider);
		// TODO: other languages
			
		tmp_ = new File(getResourceFile("tmp.txt"));	
	}
	
	@Test
	public void testConvert() throws IOException {		
		List<Pair> pairs = new ArrayList<Pair>();
		String dummy = "";
		
		writer_ = FileUtils.openFileWriter(tmp_.getAbsolutePath());
		pairs.add(new Pair(
				"Eso , es : un ! ejemplo .",
				"Eso, es: un! ejemplo."
				));
		es_converter_.convert(pairs, writer_, 0);
		writer_.close();
		reader_ = new BufferedReader(new FileReader(tmp_));
		reader_ = new BufferedReader(new FileReader(tmp_));
		String prediction_1 = "";
		while((dummy = reader_.readLine()) != null) {
			prediction_1 = dummy;
		}		
		reader_.close();
		String result_1 = "Eso<SPLIT>, es<SPLIT>: un<SPLIT>! ejemplo<SPLIT>.";
		assertEquals(prediction_1, result_1);
		
		writer_ = FileUtils.openFileWriter(tmp_.getAbsolutePath());
		pairs.add(new Pair(
				"Demonstration , de el poder de : \" una regla sobre todo \" !",
				"Demonstration, del poder de: \"una regla sobre todo\"!"
				));
		es_converter_.convert(pairs, writer_, 0);
		writer_.close();
		reader_ = new BufferedReader(new FileReader(tmp_));
		String prediction_2 = "";
		while((dummy = reader_.readLine()) != null) {
			prediction_2 = dummy;
		}	
		reader_.close();
		String result_2 = "Demonstration<SPLIT>, de el poder de<SPLIT>: \"<SPLIT>una regla sobre todo<SPLIT>\"<SPLIT>!";
		assertEquals(prediction_2, result_2);
		
		writer_ = FileUtils.openFileWriter(tmp_.getAbsolutePath());
		pairs.add(new Pair(
				"Es en este contexto , tal como lo señala el libro de profeta Daniel , cuando el rey Nabucodonosor tiene el sueño de esta colosal estatua .",
				"Es en este contexto, tal como lo señala el libro de profeta Daniel, cuando el rey Nabucodonosor tiene el sueño de esta colosal estatua."
				));
		es_converter_.convert(pairs, writer_, 0);
		writer_.close();
		reader_ = new BufferedReader(new FileReader(tmp_));
		String prediction_3 = "";
		while((dummy = reader_.readLine()) != null) {
			prediction_3 = dummy;
		}	
		reader_.close();
		String result_3 = "Es en este contexto<SPLIT>, tal como lo señala el libro de profeta Daniel<SPLIT>, cuando el rey Nabucodonosor tiene el sueño de esta colosal estatua<SPLIT>.";
		assertEquals(prediction_3, result_3);
	
	}
		
}
