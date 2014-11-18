package marmot.tokenize.preprocess;

import java.util.LinkedList;
import java.util.List;

import marmot.tokenize.openlp.Aligner.Pair;
import marmot.tokenize.openlp.Aligner.Result;

public class Viewer {
	private Result result_;
	private String tokenized_;
	private String untokenized_;
	
	public Viewer(String tokenized, String untokenized, Result result) {
		result_ = result;
		tokenized_ = tokenized;
		untokenized_ = untokenized;		
	}
	
	public List<String> getView() {
		List<Pair> pairs = result_.pairs;
		
		if(pairs == null) return null;
		
		String tokenized = "|" + tokenized_.charAt(0);
		String untokenized = pairs.get(0).b == 0 ? "|" + untokenized_.charAt(0) : "|\\";
		
		String tok_nums = "|0";
		String untok_nums = "|"+pairs.get(0).b;
		
		int prev = pairs.get(0).b;
		
		for(int i=1; i<pairs.size(); i++) {
			int a = pairs.get(i).a;
			int b = pairs.get(i).b;
			
			String num_spaces_a = spaceDiff(b, a);
			String num_spaces_b = spaceDiff(a, b);
			
			String diff_a = spaceDiff(a, 1);
			String diff_b = spaceDiff(b, 1);
			
			String num_spaces = diff_a.length() < diff_b.length() ? diff_b : diff_a;
			
			if(b == -1) {
				tok_nums += "|" + i + num_spaces_a;
				untok_nums += "|" +-1 + num_spaces_b;
				
				tokenized += "|" + tokenized_.charAt(a) + num_spaces;
				untokenized += "|\\" + num_spaces;
				
				continue;
			}
			if(b == prev) {
				tok_nums += " " + i + num_spaces_a;
				untok_nums += " " + prev + num_spaces_b;
				
				tokenized += " " + tokenized_.charAt(a) + num_spaces;
				untokenized += "  " + num_spaces;
				
				continue;
			}
			
			tok_nums += "|" + a + num_spaces_a;
			untok_nums += "|" + b + num_spaces_b;
			
			tokenized += "|" + tokenized_.charAt(a) + num_spaces;
			untokenized += "|" + untokenized_.charAt(b) + num_spaces;
			
			prev = b;
		}
		
		List<String> ret = new LinkedList<String>();
		ret.add(tokenized);
		ret.add(untokenized);
		
		ret.add(tok_nums);
		ret.add(untok_nums);

		return ret;
	}
	
	/**
	 * If the spaces for a are fewer than for b, the difference is returned
	 * @param a
	 * @param b
	 * @return difference, given that a is 'smaller' than b
	 */
	private String spaceDiff(int a, int b) {
		int a_count = a < 0 ? 1 : 0;
		a_count += (int)(Math.log10(Math.abs(a)));
		
		int b_count = b < 0 ? 1 : 0;
		b_count += (int)(Math.log10(Math.abs(b)));
		
		String ret = "";
		for(int i=0; i<(a_count-b_count); i++) {
			ret += " ";
		}		
		return ret;
	}
}
