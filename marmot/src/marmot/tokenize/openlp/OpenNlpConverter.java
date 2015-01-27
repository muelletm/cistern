package marmot.tokenize.openlp;

import java.io.Writer;
import java.util.List;

import marmot.tokenize.preprocess.Pair;
import marmot.tokenize.rules.RuleProvider;
import marmot.tokenize.rules.RulebasedTransformator;

public class OpenNlpConverter {

	private RulebasedTransformator untok_transformator_;
	private RulebasedTransformator tok_transformator_;

	public OpenNlpConverter(RuleProvider provider) {
		if (provider == null) {
			tok_transformator_ = null;
			untok_transformator_ = null;
		} else {
			tok_transformator_ = provider.getTokTransformator();
			untok_transformator_ = provider.getUnTokTransformator();
		}	
	}
	
	public void convert(Iterable<Pair> pairs, Writer writer, int verbose) {
		Aligner a = new LevenshteinAligner();
		
		int total = 0;
		int error = 0;
		
		for (Pair pair : pairs) {
			total ++;
			
			String tokenized = pair.tokenized;
	    	String untokenized = pair.untokenized;
	    	
	    	if (tok_transformator_ != null) {
	    		tokenized = tok_transformator_.applyRules(tokenized);
	    	}
	    	
	    	if (untok_transformator_ != null) {
	    		untokenized = untok_transformator_.applyRules(untokenized);
	    	}

	    	try {
	    		List<Aligner.Pair> alignment = a.align(tokenized, untokenized).pairs;
	    		if(alignment == null) {
	    			throw new RuntimeException(); // java style goto
	    		}
	    		
	    		writer.write(insertSplit(untokenized, alignment));
	    		writer.write("\n");
//	    		if(verbose == 1 || verbose == 2) {
//					System.err.println(tokenized);
//					System.err.println(untokenized);	
//				}
	    		
			} catch (Exception e) { // catches unforeseen alignment errors as well
				error++;
				if(verbose == 2 || verbose == 3) {
					if(!e.getClass().toString().split("\\.")[2].equals("RuntimeException")) {
						System.err.println("GRAVE ERROR!");
					}
					
					System.err.println("<Tok> " + tokenized + "</Tok>");
					System.err.println("<UnT> " + untokenized + "</UnT>" );	
				}
			} 
		}
		
		if(verbose > 0) {
			System.err.format("Conversion Error rate: %d / %d = %g\n", error, total, error * 100. / total);
		}
	}
	
	private String insertSplit(String untokenized, List<Aligner.Pair> alignment){
		StringBuilder sb = new StringBuilder();
		int index = 0;
		for(Aligner.Pair p : alignment) {
			if(p.b == -1){
				sb.append("<SPLIT>");
			} else {
				sb.append(untokenized.charAt(index));
				index ++;
			}
		}
		return sb.toString();
	}

}
