package marmot.morph.analyzer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import marmot.thirdparty.aramorph.AraMorph;
import marmot.thirdparty.aramorph.Solution;

public class ArabicAnalyzer extends Analyzer {

	private static final long serialVersionUID = 1L;
	transient private AraMorph aramorph_;
	private boolean romanize_;
	private boolean subfeatures_;
	
	public ArabicAnalyzer(boolean romanize, boolean subfeatures) {
		romanize_ = romanize;
		aramorph_ = null;
		subfeatures_ = subfeatures;
	}
	
	@Override
	public List<String> analyze(String form) {
		if (aramorph_ == null) {
			aramorph_ = new AraMorph();
		}
		
		if (!romanize_) {
			form = AraMorph.arabizeWord(form);
		}

		Set<Solution> solutions = aramorph_.analyzeToken(form);

		if (solutions == null || solutions.isEmpty()) {
			return null;
		}

		List<String> feats = new LinkedList<String>();
		StringBuilder sb = new StringBuilder();

		Set<String> set = new HashSet<String>();
		
		for (Solution solution : solutions) {
			feats.clear();
			sb.setLength(0);

			if (solution.getPrefixesLongPOS() != null) {
				for (String feat : solution.getPrefixesLongPOS())
					feats.add(feat);
			} else {
				feats.add("NoPrefixLongPOS");
			}

			if (solution.getStemLongPOS() != null) {
				feats.add(solution.getStemLongPOS());
			} else {
				feats.add("NoStemLongPos");
			}
			

			if (solution.getSuffixesLongPOS() != null) {
				for (String feat : solution.getSuffixesLongPOS())
					feats.add(feat);
			}else {
				feats.add("NoSuffixLongPOS");
			}

			for (String feat : feats) {
				
				int index;
				
				// Process features such as mutamar~id NOUN.
				index = feat.lastIndexOf('\t');
				if (index >= 0) {
					feat = feat.substring(index + 1);
				}

				// Process features such as NSuff-iyna.			
				index = feat.indexOf("-");
				if (index >= 0) {
					feat = feat.substring(0, index);
				}

				if (!feat.isEmpty()) {
					if (sb.length() > 0) {
						sb.append('|');
					}
					sb.append(feat);
					if (subfeatures_) {
						set.add(feat);
						String [] parts = feat.split("_:");
						if (parts.length > 1) {
							for (String part : parts) {
								if (!part.isEmpty()) {
									set.add(part);
								}
							}
						}
					}
				}
			}

			set.add(sb.toString());
		}

		if (set.isEmpty()) {
			return null;
		}
		
		return new ArrayList<String>(set);
	}
}
