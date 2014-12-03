/*
Copyright (C) 2003  Pierrick Brihaye
pierrick.brihaye@wanadoo.fr
 
Original Perl code :
Portions (c) 2002 QAMUS LLC (www.qamus.org), 
(c) 2002 Trustees of the University of Pennsylvania 
 
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the
Free Software Foundation, Inc.
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
or connect to:
http://www.fsf.org/copyleft/gpl.html
*/

package marmot.thirdparty.aramorph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/** A solution for a word.
 * @author Pierrick Brihaye, 2003*/
public class Solution {


	private List<String> features_;
	
	public List<String> getFeatures() {
		return features_;
	}
	
	private final static List<String> prefix_features_ = new ArrayList<String>();
	private final static List<String> suffix_features_ = new ArrayList<String>();
	static {
		prefix_features_.add("CONJ"); //TODO : approve
		prefix_features_.add("EMPHATIC_PARTICLE"); //TODO : approve
		prefix_features_.add("FUNC_WORD"); //TODO : approve
		prefix_features_.add("FUT_PART"); //TODO : approve
		prefix_features_.add("INTERJ"); //TODO : approve
		prefix_features_.add("INTERROG_PART"); //TODO : approve
		prefix_features_.add("IV1S");
		prefix_features_.add("IV2MS");
		prefix_features_.add("IV2FS");
		prefix_features_.add("IV3MS");
		prefix_features_.add("IV3FS");
		prefix_features_.add("IV2D");
		prefix_features_.add("IV2FD");
		prefix_features_.add("IV3MD");
		prefix_features_.add("IV3FD");
		prefix_features_.add("IV1P");
		prefix_features_.add("IV2MP");
		prefix_features_.add("IV2FP");
		prefix_features_.add("IV3MP");
		prefix_features_.add("IV3FP");
		prefix_features_.add("NEG_PART"); //TODO : approve
		prefix_features_.add("PREP"); //TODO : approve
		prefix_features_.add("RESULT_CLAUSE_PARTICLE");
		suffix_features_.add("CASE_INDEF_NOM");
		suffix_features_.add("CASE_INDEF_ACC");
		suffix_features_.add("CASE_INDEF_ACCGEN");
		suffix_features_.add("CASE_INDEF_GEN");
		suffix_features_.add("CASE_DEF_NOM");
		suffix_features_.add("CASE_DEF_ACC");
		suffix_features_.add("CASE_DEF_ACCGEN");
		suffix_features_.add("CASE_DEF_GEN");
		suffix_features_.add("NSUFF_MASC_SG_ACC_INDEF");
		suffix_features_.add("NSUFF_FEM_SG");
		suffix_features_.add("NSUFF_MASC_DU_NOM");
		suffix_features_.add("NSUFF_MASC_DU_NOM_POSS");
		suffix_features_.add("NSUFF_MASC_DU_ACCGEN");
		suffix_features_.add("NSUFF_MASC_DU_ACCGEN_POSS");
		suffix_features_.add("NSUFF_FEM_DU_NOM");
		suffix_features_.add("NSUFF_FEM_DU_NOM_POSS");
		suffix_features_.add("NSUFF_FEM_DU_ACCGEN");
		suffix_features_.add("NSUFF_FEM_DU_ACCGEN_POSS");
		suffix_features_.add("NSUFF_MASC_PL_NOM");
		suffix_features_.add("NSUFF_MASC_PL_NOM_POSS");
		suffix_features_.add("NSUFF_MASC_PL_ACCGEN");				
		suffix_features_.add("NSUFF_MASC_PL_ACCGEN_POSS");
		suffix_features_.add("NSUFF_FEM_PL");
		suffix_features_.add("POSS_PRON_1S");
		suffix_features_.add("POSS_PRON_2MS");
		suffix_features_.add("POSS_PRON_2FS");
		suffix_features_.add("POSS_PRON_3MS");
		suffix_features_.add("POSS_PRON_3FS");
		suffix_features_.add("POSS_PRON_2D");
		suffix_features_.add("POSS_PRON_3D");
		suffix_features_.add("POSS_PRON_1P");
		suffix_features_.add("POSS_PRON_2MP");
		suffix_features_.add("POSS_PRON_2FP");
		suffix_features_.add("POSS_PRON_3MP");
		suffix_features_.add("POSS_PRON_3FP");
		suffix_features_.add("IVSUFF_DO:1S");
		suffix_features_.add("IVSUFF_DO:2MS");
		suffix_features_.add("IVSUFF_DO:2FS");
		suffix_features_.add("IVSUFF_DO:3MS");
		suffix_features_.add("IVSUFF_DO:3FS");
		suffix_features_.add("IVSUFF_DO:2D");
		suffix_features_.add("IVSUFF_DO:3D");
		suffix_features_.add("IVSUFF_DO:1P");
		suffix_features_.add("IVSUFF_DO:2MP");
		suffix_features_.add("IVSUFF_DO:2FP");
		suffix_features_.add("IVSUFF_DO:3MP");
		suffix_features_.add("IVSUFF_DO:3FP");
		suffix_features_.add("IVSUFF_MOOD:I");
		suffix_features_.add("IVSUFF_SUBJ:2FS_MOOD:I");
		suffix_features_.add("IVSUFF_SUBJ:D_MOOD:I");
		suffix_features_.add("IVSUFF_SUBJ:3D_MOOD:I");
		suffix_features_.add("IVSUFF_SUBJ:MP_MOOD:I");
		suffix_features_.add("IVSUFF_MOOD:S");
		suffix_features_.add("IVSUFF_SUBJ:2FS_MOOD:SJ");
		suffix_features_.add("IVSUFF_SUBJ:D_MOOD:SJ");
		suffix_features_.add("IVSUFF_SUBJ:MP_MOOD:SJ");
		suffix_features_.add("IVSUFF_SUBJ:3MP_MOOD:SJ");
		suffix_features_.add("IVSUFF_SUBJ:FP");
		suffix_features_.add("PVSUFF_DO:1S");
		suffix_features_.add("PVSUFF_DO:2MS");
		suffix_features_.add("PVSUFF_DO:2FS");
		suffix_features_.add("PVSUFF_DO:3MS");
		suffix_features_.add("PVSUFF_DO:3FS");
		suffix_features_.add("PVSUFF_DO:2D");
		suffix_features_.add("PVSUFF_DO:3D");
		suffix_features_.add("PVSUFF_DO:1P");
		suffix_features_.add("PVSUFF_DO:2MP");
		suffix_features_.add("PVSUFF_DO:2FP");
		suffix_features_.add("PVSUFF_DO:3MP");
		suffix_features_.add("PVSUFF_DO:3FP");
		suffix_features_.add("PVSUFF_SUBJ:1S");
		suffix_features_.add("PVSUFF_SUBJ:2MS");
		suffix_features_.add("PVSUFF_SUBJ:2FS");
		suffix_features_.add("PVSUFF_SUBJ:3MS");
		suffix_features_.add("PVSUFF_SUBJ:3FS");
		suffix_features_.add("PVSUFF_SUBJ:2MD");
		suffix_features_.add("PVSUFF_SUBJ:2FD");
		suffix_features_.add("PVSUFF_SUBJ:3MD");
		suffix_features_.add("PVSUFF_SUBJ:3FD");
		suffix_features_.add("PVSUFF_SUBJ:1P");
		suffix_features_.add("PVSUFF_SUBJ:2MP");
		suffix_features_.add("PVSUFF_SUBJ:2FP");
		suffix_features_.add("PVSUFF_SUBJ:3MP");
		suffix_features_.add("PVSUFF_SUBJ:3FP");
		suffix_features_.add("CVSUFF_DO:1S");
		suffix_features_.add("CVSUFF_DO:3MS");
		suffix_features_.add("CVSUFF_DO:3FS");
		suffix_features_.add("CVSUFF_DO:3D");
		suffix_features_.add("CVSUFF_DO:1P");
		suffix_features_.add("CVSUFF_DO:3MP");
		suffix_features_.add("CVSUFF_DO:3FP");
		suffix_features_.add("CVSUFF_SUBJ:2MS");
		suffix_features_.add("CVSUFF_SUBJ:2FS");
		suffix_features_.add("CVSUFF_SUBJ:2MP");
	}
	
	/** Constructs a solution for a word. Note that the prefix, stem and suffix combination is <STRONG>recomputed</STRONG> 
	 * and may not necessarily match with the information provided by the dictionaries.	 
	 * @param cnt Order in sequence ; not very useful actually
	 * @param prefix The prefix as provided by the prefixes dictionnary
	 * @param stem The stem as provided by the stems dictionnary
	 * @param suffix The suffix as provided by the suffixes dictionnary
	 */
	
	protected Solution(DictionaryEntry prefix, DictionaryEntry stem, DictionaryEntry suffix) {
		LinkedList<String> prefixes = new LinkedList<String>(Arrays.asList(prefix.getPOS()));
		LinkedList<String> stems = new LinkedList<String>(Arrays.asList(stem.getPOS()));
		LinkedList<String> suffixes = new LinkedList<String>(Arrays.asList(suffix.getPOS()));
		
			
		//Normalize stems since some of them can contain prefixes		
		while (stems.size() > 0) {
			String stem_feat = (String)stems.getFirst();
			
			boolean found_prefix = false;
			for (String prefix_feat : prefix_features_) {
				if (stem_feat.endsWith(prefix_feat)) { //TODO : approve
					stems.removeFirst();
					prefixes.addLast(stem_feat);					
					found_prefix = true;
					break;
				}
			}
			
			if (!found_prefix)
				break;
		}		
		
		//Normalize stems since some of them can contain suffixes		
		while (stems.size() > 0) {
			String stem_feat = (String)stems.getLast();
			
			boolean found_suffix = false;
			for (String suffix_feat : suffix_features_) {
				if (stem_feat.endsWith(suffix_feat)) {
					stems.removeLast();					
					suffixes.addFirst(stem_feat);
					found_suffix = true;
					break;
				}
			}
			
			if (!found_suffix)
				break;
		}
		
		features_ = new ArrayList<String>();
		features_.addAll(prefixes);
		if (stems.isEmpty()) {
			features_.add("NOSTEM");
		} else {
			features_.add(stems.getFirst());
		}
		features_.addAll(suffixes);
		
		for (int i=0; i<features_.size(); i++) {
			features_.set(i, simplify(features_.get(i)));
		}
	}
	
	public String simplify(String string) {
		int index = string.indexOf('/');
		
		if (index >= 0) {
			string = string.substring(index + 1);
		}
		return string;
	}
}



