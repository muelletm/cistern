package marmot.tokenize.openlp;

import java.util.regex.Pattern;

public class OpenNlpScript {

	private static Pattern mark = Pattern.compile("\\p{Punct}", Pattern.CASE_INSENSITIVE);
	
	public static String transform(String tok, String unTok) {
		String nlpFormat = "";
		int tokIdx = 0;
		for(int i=0; i<unTok.length(); i++) {
			try {
				if(unTok.charAt(i) != tok.charAt(tokIdx)) {
					if(tok.charAt(tokIdx) == ' ') {
						nlpFormat += "<SPLIT>";
						if(mark.matcher(Character.toString(tok.charAt(++tokIdx))).find()) {
							while( tok.charAt(tokIdx+1) != ' '	&&
								  (tok.charAt(tokIdx+1) != unTok.charAt(i+1))) tokIdx++;							
						}
					} else {
						while(tok.charAt(tokIdx+1) != ' ' && 
							 (tok.charAt(tokIdx+1) != unTok.charAt(i+1))) tokIdx++;
					}
				}			
				nlpFormat += unTok.charAt(i);
				tokIdx++;
			} catch (StringIndexOutOfBoundsException e) {
				nlpFormat += ".";
			}
		}
		return nlpFormat;	
	}

	/* failed levenshtein v2
	LevenshteinLattice lattice = new LevenshteinLattice(unTok, tok);
	String diffs = lattice.searchOperationSequence();
	System.out.println("       diffs: "+diffs);
	int idx = 0;
	String out = "";
	for(int i=0; i<tok.length(); i++) {
		if(diffs.charAt(i) == 'I' || diffs.charAt(i) == 'R') {
			if(tok.charAt(i) == ' ') {
				out += "<SPLIT>";
			}
			if(diffs.charAt(i) == 'R') out += unTok.charAt(idx++);
		} else out += unTok.charAt(idx++);
	}
	nlpFormat = out;
	*/
	
	/* failed levensthein v1
	for(int i=0; i<unTok.length(); i++) {
		if(diffs.charAt(idx) == 'I') {
			if(diffs.charAt(idx+1) == 'C') {
				nlpFormat += "<SPLIT>";
				idx++;
			} else {
				while(diffs.charAt(++idx+1) != 'R') continue;
				nlpFormat += "<SPLIT>";
				idx++;
			}
		}
		nlpFormat += unTok.charAt(i);
		if(++idx >= diffs.length()) break;
	}
	*/

	
//	public static String transform_old(String tok, String unTok) {
//		String[] toks = tok.split(" ");
//		String[] unToks = unTok.split(" ");
//		//Pattern insertLeft = Pattern.compile("[.\\D&&\\W&&\\S\b]");
//		Pattern insertLeft = Pattern.compile("[.\\p{Punct}\b]");
//		//Pattern insertRight = Pattern.compile("[\b&&\\D&&\\W&&\\S.]");
//		Pattern insertRight = Pattern.compile("[\b\\p{Punct}.]");
//		// cases where <SPLIT> is inserted:
//		// ( ) " ' , . ; : ! ? [ ] { }
//		// maybe just [.&&^\s&&^\w&&^\s&&^\]  ?
//		int tokCount = 0;
//		for(int i=0; i<unToks.length; i++) {
//			String onlpString = unToks[i];
//			System.out.println("before: "+onlpString);
//			if(unToks[i].length() > toks[tokCount++].length()) {
//				System.out.println("Difference in length between "+toks[tokCount]+" and "+unToks[i]);
//				if(insertLeft.matcher(unToks[i]).matches()) {
//					onlpString = onlpString.substring(0,1)+"<SPLIT>"+onlpString.substring(1);
//					System.out.println("left insert");
//					tokCount++;
//				}
//				if(insertRight.matcher(unToks[i]).matches()) {
//					onlpString = onlpString.substring(0, onlpString.length()-2)+"<SPLIT>"+onlpString.substring(onlpString.length()-2);
//					System.out.println("right insert");
//					tokCount++;
//				}
//					
//			}
//			System.out.println("after: "+onlpString);
//		}
//		
//		
//		return "";
//	}
}
