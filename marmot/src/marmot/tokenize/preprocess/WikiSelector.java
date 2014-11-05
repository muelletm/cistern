package marmot.tokenize.preprocess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;

import marmot.tokenize.openlp.Transformator;
import marmot.util.LevenshteinLattice;

public class WikiSelector {
	private LinkedList<String> untokenized;
	private LinkedList<String> tokenized;
	private LinkedList<String> nlpFormat;
	private String lang;
	private int maxSentence;
	private WikiReader reader;
	
	WikiSelector(String untokenizedFile, String tokenizedFile, String lang, int maxSentence) {
		untokenized = new LinkedList<String>();
		tokenized = new LinkedList<String>();
		nlpFormat = new LinkedList<String>();
		this.lang = lang;
		this.maxSentence = maxSentence;
		reader = new WikiReader(untokenizedFile, tokenizedFile, 10000);
	}
	
	public void selectSentence() {
		ListIterator<String> unTokIt = reader.getUntokenized().listIterator();
		ListIterator<String> tokIt = reader.getTokenized().listIterator();
//		int sum = 0;
//		int[] scores = new int[maxSentence];
		String unTok;
		String tok;
		
		//score that is used to decide if a sentence is usable
		for(int i=0; i<maxSentence; i++) {
			unTok = unTokIt.next();
			tok = tokIt.next();
			//TODO: check for tok unTok inconsistencies
			int score = new LevenshteinLattice(unTok, tok).getDistance();
			
			if(score > 12) {
				untokenized.push(unTok);
				tokenized.push(tok);
				nlpFormat.push(Transformator.transform(tok, unTok)); 
			}
//			sum += score;
//			scores[i] = score;
		}

		// Calculation of statistical data
//		float mean = ((float)sum) / maxSentence;
//		float variance = 0;
//		for(int score : scores) 
//			variance += (score - mean) * (score - mean);
//		variance /= maxSentence;
//		float stdDevi = (float) Math.sqrt(variance);
//		
//		System.out.println("Sum of all scores: "+sum);
//		System.out.println("Mean value: "+mean);
//		System.out.println("Variance: "+variance);
//		System.out.println("Standard deviation: "+stdDevi);
		
		try {
			writeFile("./data/text/" + lang + "_unTokCorpus.train", untokenized);
			writeFile("./data/text/" + lang + "_tokCorpus.train", tokenized);
			writeFile("./data/text/" + lang + "_nlpFormatCorpus.train", nlpFormat);
//			writeFiles("./data/text/unTokCorpus.ser", untokenized);
//			writeFiles("./data/text/tokCorpus.ser", tokenized);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(untokenized.size()+" sentences were collected.");
	}
	
	//old write method
//	private void writeFiles(String filename, LinkedList<String> data) throws IOException {
//		FileOutputStream fs = new FileOutputStream(filename);
//		ObjectOutputStream out = new ObjectOutputStream(fs);
//		out.writeObject(data);
//		out.close();
//		fs.close();
//		System.out.println("Data is saved in "+filename+".");
//	}

	private void writeFile(String filename, LinkedList<String> data) throws IOException {
		File file = new File(filename);
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		
		for(String s : data) {
			bw.write(s + "\n");
		}
		bw.close();
		System.out.println("Data is saved in "+filename+".");
	}
	
	public LinkedList<String> getUntokenized() {
		return untokenized;
	}

	public LinkedList<String> getTokenized() {
		return tokenized;
	}
}
