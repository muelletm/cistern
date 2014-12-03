package marmot.morph.analyzer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import marmot.thirdparty.aramorph.AraMorph;
import marmot.thirdparty.aramorph.Solution;

public class ArabicAnalyzer extends Analyzer {

	private static final long serialVersionUID = 1L;
	transient private AraMorph aramorph_;
	private boolean subfeatures_;
	
	public ArabicAnalyzer(boolean subfeatures) {
		aramorph_ = null;
		subfeatures_ = subfeatures;
	}
	
	@Override
	public List<String> analyze(String form) {
		if (aramorph_ == null) {
			aramorph_ = new AraMorph();
		}
		
		Set<Solution> solutions = aramorph_.analyzeToken(form);

		if (solutions == null || solutions.isEmpty()) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		Set<String> set = new HashSet<String>();
		
		for (Solution solution : solutions) {
			sb.setLength(0);

			List<String> feats = solution.getFeatures();
			
			for (String feat : feats) {
				
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
