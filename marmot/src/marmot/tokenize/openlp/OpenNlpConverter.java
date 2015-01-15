package marmot.tokenize.openlp;

import java.io.IOException;
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
	
	public void convert(Iterable<Pair> pairs, Writer writer) throws IOException {
		Aligner a = new LevenshteinAligner();
		
		for (Pair pair : pairs) {
			String tokenized = pair.tokenized;
	    	String untokenized = pair.untokenized;
	    	
	    	if (tok_transformator_ != null) {
	    		tokenized = tok_transformator_.applyRules(tokenized);
	    	}
	    	
	    	if (untok_transformator_ != null) {
	    		untokenized = untok_transformator_.applyRules(untokenized);
	    	}
	    	
			List<Aligner.Pair> alignment = a.align(tokenized, untokenized).pairs;
			if(alignment != null) {
	    		writer.write(insertSplit(untokenized, alignment));
	    		writer.write("\n");
	    	}
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
