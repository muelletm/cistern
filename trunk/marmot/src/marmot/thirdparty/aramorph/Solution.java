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

import java.util.Arrays;
import java.util.LinkedList;

/** A solution for a word.
 * @author Pierrick Brihaye, 2003*/
public class Solution {

	/** Whether or not the dictionnaries inconsistencies should be output */
	protected boolean debug = false;
	
	/* The order in solutions' sequence.*/
	protected int cnt;
	/* The dictionary entry of the prefix.*/
	protected DictionaryEntry prefix;
	/* The dictionary entry of the stem.*/
	protected DictionaryEntry stem;
	/* The dictionary entry of the suffix.*/
	protected DictionaryEntry suffix;
	/* The prefixes POS.*/
	protected LinkedList<String> prefixesPOS;
	/* The stems POS.*/
	protected LinkedList<String> stemsPOS;
	/* The suffixes POS.*/
	protected LinkedList<String> suffixesPOS;
	/* The prefixes glosses.*/
	protected LinkedList<String> prefixesGlosses;
	/* The stems glosses.*/
	protected LinkedList<String> stemsGlosses;
	/* The suffixes glosses.*/
	protected LinkedList<String> suffixesGlosses;
	
	/** Constructs a solution for a word. Note that the prefix, stem and suffix combination is <STRONG>recomputed</STRONG> 
	 * and may not necessarily match with the information provided by the dictionaries.	 
	 * @param cnt Order in sequence ; not very useful actually
	 * @param prefix The prefix as provided by the prefixes dictionnary
	 * @param stem The stem as provided by the stems dictionnary
	 * @param suffix The suffix as provided by the suffixes dictionnary
	 */
	protected Solution(int cnt, DictionaryEntry prefix, DictionaryEntry stem, DictionaryEntry suffix) {
		this(false, cnt, prefix, stem, suffix);
	}

	/** Constructs a solution for a word. Note that the prefix, stem and suffix combination is <STRONG>recomputed</STRONG> 
	 * and may not necessarily match with the information provided by the dictionaries.	 
	 * @param debug Whether or not the dictionnaries inconsistencies should be output
	 * @param cnt Order in sequence ; not very useful actually
	 * @param prefix The prefix as provided by the prefixes dictionnary
	 * @param stem The stem as provided by the stems dictionnary
	 * @param suffix The suffix as provided by the suffixes dictionnary
	 */
	protected Solution(boolean debug, int cnt, DictionaryEntry prefix, DictionaryEntry stem, DictionaryEntry suffix) {
		this.debug = debug; 
		this.debug = true; //TODO : suppress when fully tested		
		this.cnt = cnt;
		this.prefix = prefix;
		this.stem = stem;
		this.suffix = suffix;
		prefixesPOS = new LinkedList<String>(Arrays.asList(prefix.getPOS()));
		stemsPOS = new LinkedList<String>(Arrays.asList(stem.getPOS()));
		suffixesPOS = new LinkedList<String>(Arrays.asList(suffix.getPOS()));
		prefixesGlosses = new LinkedList<String>(Arrays.asList(prefix.getGlosses()));
		stemsGlosses = new LinkedList<String>(Arrays.asList(stem.getGlosses()));
		suffixesGlosses = new LinkedList<String>(Arrays.asList(suffix.getGlosses()));
//		if (stemsPOS.size() != stemsGlosses.size()) {
//			if (this.debug) System.err.println("\"" + this.getLemma() + "\" : stem's sizes for POS (" + stemsPOS.size() + ") and GLOSS ("+ stemsGlosses.size() + ") do not match");
//		}
		String stemPOS = null;
		String stemGloss = null;		
		//Normalize stems since some of them can contain prefixes		
		while (stemsPOS.size() > 0) {
			stemPOS = (String)stemsPOS.getFirst();
			if (stemsGlosses.size() >  0) stemGloss = (String)stemsGlosses.getFirst();
			else stemGloss = null;			
			if (stemPOS.endsWith("CONJ")) { //TODO : approve
				stemsPOS.removeFirst();
				if (stemGloss != null) stemsGlosses.removeFirst();
				prefixesPOS.addLast(stemPOS);
				if (stemGloss != null) prefixesGlosses.addLast(stemGloss);
			}	
			else if (stemPOS.endsWith("EMPHATIC_PARTICLE")) { //TODO : approve
				stemsPOS.removeFirst();
				if (stemGloss != null) stemsGlosses.removeFirst();
				prefixesPOS.addLast(stemPOS);
				if (stemGloss != null) prefixesGlosses.addLast(stemGloss);
			}	
			else if (stemPOS.endsWith("FUNC_WORD")) { //TODO : approve
				stemsPOS.removeFirst();
				if (stemGloss != null) stemsGlosses.removeFirst();
				prefixesPOS.addLast(stemPOS);
				if (stemGloss != null) prefixesGlosses.addLast(stemGloss);
			}				
			else if (stemPOS.endsWith("FUT_PART")) { //TODO : approve
				stemsPOS.removeFirst();
				if (stemGloss != null) stemsGlosses.removeFirst();
				prefixesPOS.addLast(stemPOS);
				if (stemGloss != null) prefixesGlosses.addLast(stemGloss);
			}		
			else if (stemPOS.endsWith("INTERJ")) { //TODO : approve
				stemsPOS.removeFirst();
				if (stemGloss != null) stemsGlosses.removeFirst();
				prefixesPOS.addLast(stemPOS);
				if (stemGloss != null) prefixesGlosses.addLast(stemGloss);
			}
			else if (stemPOS.endsWith("INTERROG_PART")) { //TODO : approve
				stemsPOS.removeFirst();
				if (stemGloss != null) stemsGlosses.removeFirst();
				prefixesPOS.addLast(stemPOS);
				if (stemGloss != null) prefixesGlosses.addLast(stemGloss);
			}			
			else if (stemPOS.endsWith("IV1S")) {
				stemsPOS.removeFirst();
				if (stemGloss != null) stemsGlosses.removeFirst();
				prefixesPOS.addLast(stemPOS);
				if (stemGloss != null) prefixesGlosses.addLast(stemGloss);
			}
			else if (stemPOS.endsWith("IV2MS")) {
				stemsPOS.removeFirst();
				if (stemGloss != null) stemsGlosses.removeFirst();
				prefixesPOS.addLast(stemPOS);
				if (stemGloss != null) prefixesGlosses.addLast(stemGloss);
			}
			else if (stemPOS.endsWith("IV2FS")) {
				stemsPOS.removeFirst();
				if (stemGloss != null) stemsGlosses.removeFirst();
				prefixesPOS.addLast(stemPOS);
				if (stemGloss != null) prefixesGlosses.addLast(stemGloss);
			}
			else if (stemPOS.endsWith("IV3MS")) {
				stemsPOS.removeFirst();
				if (stemGloss != null) stemsGlosses.removeFirst();
				prefixesPOS.addLast(stemPOS);
				if (stemGloss != null) prefixesGlosses.addLast(stemGloss);
			}
			else if (stemPOS.endsWith("IV3FS")) {
				stemsPOS.removeFirst();
				if (stemGloss != null) stemsGlosses.removeFirst();
				prefixesPOS.addLast(stemPOS);
				if (stemGloss != null) prefixesGlosses.addLast(stemGloss);
			}			
			else if (stemPOS.endsWith("IV2D")) {
				stemsPOS.removeFirst();
				if (stemGloss != null) stemsGlosses.removeFirst();
				prefixesPOS.addLast(stemPOS);
				if (stemGloss != null) prefixesGlosses.addLast(stemGloss);
			}			
			else if (stemPOS.endsWith("IV2FD")) {
				stemsPOS.removeFirst();
				if (stemGloss != null) stemsGlosses.removeFirst();
				prefixesPOS.addLast(stemPOS);
				if (stemGloss != null) prefixesGlosses.addLast(stemGloss);
			}			
			else if (stemPOS.endsWith("IV3MD")) {
				stemsPOS.removeFirst();
				if (stemGloss != null) stemsGlosses.removeFirst();
				prefixesPOS.addLast(stemPOS);
				if (stemGloss != null) prefixesGlosses.addLast(stemGloss);
			}
			else if (stemPOS.endsWith("IV3FD")) {
				stemsPOS.removeFirst();
				if (stemGloss != null) stemsGlosses.removeFirst();
				prefixesPOS.addLast(stemPOS);
				if (stemGloss != null) prefixesGlosses.addLast(stemGloss);
			}
			else if (stemPOS.endsWith("IV1P")) {
				stemsPOS.removeFirst();
				if (stemGloss != null) stemsGlosses.removeFirst();
				prefixesPOS.addLast(stemPOS);
				if (stemGloss != null) prefixesGlosses.addLast(stemGloss);
			}
			else if (stemPOS.endsWith("IV2MP")) {
				stemsPOS.removeFirst();
				if (stemGloss != null) stemsGlosses.removeFirst();
				prefixesPOS.addLast(stemPOS);
				if (stemGloss != null) prefixesGlosses.addLast(stemGloss);
			}
			else if (stemPOS.endsWith("IV2FP")) {
				stemsPOS.removeFirst();
				if (stemGloss != null) stemsGlosses.removeFirst();
				prefixesPOS.addLast(stemPOS);
				if (stemGloss != null) prefixesGlosses.addLast(stemGloss);
			}
			else if (stemPOS.endsWith("IV3MP")) {
				stemsPOS.removeFirst();
				if (stemGloss != null) stemsGlosses.removeFirst();
				prefixesPOS.addLast(stemPOS);
				if (stemGloss != null) prefixesGlosses.addLast(stemGloss);
			}
			else if (stemPOS.endsWith("IV3FP")) {
				stemsPOS.removeFirst();
				if (stemGloss != null) stemsGlosses.removeFirst();
				prefixesPOS.addLast(stemPOS);
				if (stemGloss != null) prefixesGlosses.addLast(stemGloss);
			}	
			else if (stemPOS.endsWith("NEG_PART")) { //TODO : approve
				stemsPOS.removeFirst();
				if (stemGloss != null) stemsGlosses.removeFirst();
				prefixesPOS.addLast(stemPOS);
				if (stemGloss != null) prefixesGlosses.addLast(stemGloss);
			}
			else if (stemPOS.endsWith("PREP")) { //TODO : approve
				stemsPOS.removeFirst();
				if (stemGloss != null) stemsGlosses.removeFirst();
				prefixesPOS.addLast(stemPOS);
				if (stemGloss != null) prefixesGlosses.addLast(stemGloss);
			}			
			else if (stemPOS.endsWith("RESULT_CLAUSE_PARTICLE")) {
				stemsPOS.removeFirst();
				if (stemGloss != null) stemsGlosses.removeFirst();
				prefixesPOS.addLast(stemPOS);
				if (stemGloss != null) prefixesGlosses.addLast(stemGloss);
			}
			else {
				break;						
			}
		}		
		//Normalize stems since some of them can contain suffixes		
		while (stemsPOS.size() > 0) {
			stemPOS = (String)stemsPOS.getLast();
			if (stemsGlosses.size() >  0) stemGloss = (String)stemsGlosses.getLast();				
			else stemGloss = null;
			if (stemPOS.endsWith("CASE_INDEF_NOM")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}
			else if (stemPOS.endsWith("CASE_INDEF_ACC")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}				
			else if (stemPOS.endsWith("CASE_INDEF_ACCGEN")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}			
			else if (stemPOS.endsWith("CASE_INDEF_GEN")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}			
			else if (stemPOS.endsWith("CASE_DEF_NOM")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}				
			else if (stemPOS.endsWith("CASE_DEF_ACC")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}				
			else if (stemPOS.endsWith("CASE_DEF_ACCGEN")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}				
			else if (stemPOS.endsWith("CASE_DEF_GEN")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("NSUFF_MASC_SG_ACC_INDEF")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}				
			else if (stemPOS.endsWith("NSUFF_FEM_SG")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}				
			else if (stemPOS.endsWith("NSUFF_MASC_DU_NOM")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("NSUFF_MASC_DU_NOM_POSS")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}				
			else if (stemPOS.endsWith("NSUFF_MASC_DU_ACCGEN")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}				
			else if (stemPOS.endsWith("NSUFF_MASC_DU_ACCGEN_POSS")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}
			else if (stemPOS.endsWith("NSUFF_FEM_DU_NOM")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}				
			else if (stemPOS.endsWith("NSUFF_FEM_DU_NOM_POSS")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}				
			else if (stemPOS.endsWith("NSUFF_FEM_DU_ACCGEN")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("NSUFF_FEM_DU_ACCGEN_POSS")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}				
			else if (stemPOS.endsWith("NSUFF_MASC_PL_NOM")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}				
			else if (stemPOS.endsWith("NSUFF_MASC_PL_NOM_POSS")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}				
			else if (stemPOS.endsWith("NSUFF_MASC_PL_ACCGEN")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}				
			else if (stemPOS.endsWith("NSUFF_MASC_PL_ACCGEN_POSS")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}				
			else if (stemPOS.endsWith("NSUFF_FEM_PL")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}				
			else if (stemPOS.endsWith("POSS_PRON_1S")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("POSS_PRON_2MS")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}				
			else if (stemPOS.endsWith("POSS_PRON_2FS")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}				
			else if (stemPOS.endsWith("POSS_PRON_3MS")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}				
			else if (stemPOS.endsWith("POSS_PRON_3FS")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}		
			else if (stemPOS.endsWith("POSS_PRON_2D")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}				
			else if (stemPOS.endsWith("POSS_PRON_3D")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}				
			else if (stemPOS.endsWith("POSS_PRON_1P")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}				
			else if (stemPOS.endsWith("POSS_PRON_2MP")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("POSS_PRON_2FP")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}				
			else if (stemPOS.endsWith("POSS_PRON_3MP")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}							
			else if (stemPOS.endsWith("POSS_PRON_3FP")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("IVSUFF_DO:1S")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("IVSUFF_DO:2MS")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("IVSUFF_DO:2FS")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("IVSUFF_DO:3MS")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("IVSUFF_DO:3FS")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("IVSUFF_DO:2D")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("IVSUFF_DO:3D")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("IVSUFF_DO:1P")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("IVSUFF_DO:2MP")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("IVSUFF_DO:2FP")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("IVSUFF_DO:3MP")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("IVSUFF_DO:3FP")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("IVSUFF_MOOD:I")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("IVSUFF_SUBJ:2FS_MOOD:I")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("IVSUFF_SUBJ:D_MOOD:I")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("IVSUFF_SUBJ:3D_MOOD:I")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("IVSUFF_SUBJ:MP_MOOD:I")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("IVSUFF_MOOD:S")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}				
			else if (stemPOS.endsWith("IVSUFF_SUBJ:2FS_MOOD:SJ")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("IVSUFF_SUBJ:D_MOOD:SJ")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("IVSUFF_SUBJ:MP_MOOD:SJ")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("IVSUFF_SUBJ:3MP_MOOD:SJ")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("IVSUFF_SUBJ:FP")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("PVSUFF_DO:1S")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}				
			else if (stemPOS.endsWith("PVSUFF_DO:2MS")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("PVSUFF_DO:2FS")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("PVSUFF_DO:3MS")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}				
			else if (stemPOS.endsWith("PVSUFF_DO:3FS")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("PVSUFF_DO:2D")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("PVSUFF_DO:3D")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("PVSUFF_DO:1P")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("PVSUFF_DO:2MP")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("PVSUFF_DO:2FP")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("PVSUFF_DO:3MP")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("PVSUFF_DO:3FP")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("PVSUFF_SUBJ:1S")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("PVSUFF_SUBJ:2MS")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("PVSUFF_SUBJ:2FS")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("PVSUFF_SUBJ:3MS")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("PVSUFF_SUBJ:3FS")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("PVSUFF_SUBJ:2MD")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("PVSUFF_SUBJ:2FD")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("PVSUFF_SUBJ:3MD")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("PVSUFF_SUBJ:3FD")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("PVSUFF_SUBJ:1P")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("PVSUFF_SUBJ:2MP")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("PVSUFF_SUBJ:2FP")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("PVSUFF_SUBJ:3MP")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("PVSUFF_SUBJ:3FP")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("CVSUFF_DO:1S")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("CVSUFF_DO:3MS")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("CVSUFF_DO:3FS")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("CVSUFF_DO:3D")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("CVSUFF_DO:1P")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("CVSUFF_DO:3MP")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("CVSUFF_DO:3FP")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("CVSUFF_SUBJ:2MS")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("CVSUFF_SUBJ:2FS")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}	
			else if (stemPOS.endsWith("CVSUFF_SUBJ:2MP")) {
				stemsPOS.removeLast();					
				if (stemGloss != null) stemsGlosses.removeLast();
				suffixesPOS.addFirst(stemPOS);
				if (stemGloss != null) suffixesGlosses.addFirst(stemGloss);
			}		
			else {
				break;						
			}
		}
		
		//Normalization of bayon, bayona, bayoni
		//TODO : revisit		
		if (stemsPOS.size() > 1) {			
			String pos0 = (String)stemsPOS.get(0);
			String pos1 = (String)stemsPOS.get(1);
			String[] array;			
			StringBuffer sb;
			int i = 0;
			if (pos1.equals("bayon")) {				
				if (this.debug) System.err.println("Merging \"bayon\" into first part of stem \"" + pos0 + "\"");
				array = pos0.split("/");				
				sb = new StringBuffer(array[0] + "bayon/");
				for (i = 1 ; i < array.length ; i++) {
					sb.append(array[i]);
				}
				stemsPOS.removeFirst();
				stemsPOS.removeFirst();
				stemsPOS.addFirst(sb.toString());				
			}
			else if (pos1.equals("bayona")) {	
				if (this.debug) System.err.println("Merging \"bayona\" into first part of stem \"" + pos0 + "\"");
				array = pos0.split("/");				
				sb = new StringBuffer(array[0] + "bayona/");
				for (i = 1 ; i < array.length ; i++) {
					sb.append(array[i]);
				}
				stemsPOS.removeFirst();
				stemsPOS.removeFirst();
				stemsPOS.addFirst(sb.toString());				
			}
			else if (pos1.equals("bayoni")) {	
				if (this.debug) System.err.println("Merging \"bayoni\" into first part of stem \"" + pos0 + "\"");
				array = pos0.split("/");				
				sb = new StringBuffer(array[0] + "bayoni/");
				for (i = 1 ; i < array.length ; i++) {
					sb.append(array[i]);
				}				
				stemsPOS.removeFirst();
				stemsPOS.removeFirst();
				stemsPOS.addFirst(sb.toString());				
			}			
		}		 
		
		//Sanity check
//		if (stemsPOS.size() > 1) {
//			if (this.debug) System.err.println("More than one stem for " + stemsPOS.toString());
//		}
	}
	
	/** Returns the dictionary entry for the word's prefix as provided by the prefixes dictionnary, i.e. <STRONG>before</STRONG> recomputation.
	 * @return The prefix
	 */
	public DictionaryEntry getPrefix() { return this.prefix; }
	
	/** Returns the dictionary entry for the word's stem as provided by the stems dictionnary, i.e. <STRONG>before</STRONG> recomputation..
	 * @return The stem
	 */
	public DictionaryEntry getStem() { return this.stem; }
	
	/** Returns the dictionary entry for the word's suffix as provided by the suffixes dictionnary, i.e. <STRONG>before</STRONG> recomputation..
	 * @return The suffix
	 */
	public DictionaryEntry getSuffix() { return this.suffix; }

	/** Returns the order in solutions' sequence.
	 * @return The order in sequence
	 */
	public int getCnt() { return this.cnt; }
	
	/** Returns the lemma id in the stems dictionary.
	 * @return The lemma ID
	 */
	public String getLemma() {
		return stem.getLemmaID().replaceFirst("(_|-).*$",""); //inconsistent formatting of lemma IDs
	}
	
	/** Returns the vocalizations of the <STRONG>recomputed</STRONG> prefixes in the Buckwalter transliteration system or  <CODE>null</CODE> if there are no prefixes for the word.
	 * @return The vocalizations
	 */
	public String[] getPrefixesVocalizations() {
		if (prefixesPOS.isEmpty()) return null;
		String[] vocalizations = new String[prefixesPOS.size()];
		int i;
		String pos = null;
		String[] array;			
		for (i = 0 ; i < prefixesPOS.size() ; i++) {
			pos = (String)prefixesPOS.get(i);
			array = pos.split("/");				
			vocalizations[i] = array[0];
		}
		return vocalizations;
	}


	/** Returns the vocalizations of the <STRONG>recomputed</STRONG> prefixes in arabic or <CODE>null</CODE> if there are no prefixes for the word.
	 * @return The vocalizations
	 */
	public String[] getPrefixesArabicVocalizations() {
		if (prefixesPOS.isEmpty()) return null;
		String[] vocalizations = new String[prefixesPOS.size()];
		int i;
		String pos = null;
		String[] array;			
		for (i = 0 ; i < prefixesPOS.size() ; i++) {
			pos = (String)prefixesPOS.get(i);
			array = pos.split("/");				
			vocalizations[i] = AraMorph.arabizeWord(array[0]);
		}
		return vocalizations;
	}

	/** Returns the vocalization of the <STRONG>recomputed</STRONG> stem in the Buckwalter transliteration system or <CODE>null</CODE> if there is no stem for the word.
	 * @return The vocalization
	 */
	public String getStemVocalization() {
		if (stemsPOS.isEmpty()) return null;
		String[] vocalizations = new String[stemsPOS.size()];
		int i;
		String pos = null;
		String[] array;					
		for (i = 0 ; i < stemsPOS.size() ; i++) {
			pos = (String)stemsPOS.get(i);
			array = pos.split("/");				
			vocalizations[i] = array[0];
		}
		//if ((vocalizations.length > 1) && this.debug) System.err.println("More than one stem for " + vocalizations.toString());
		//return the first anyway :-(
		return vocalizations[0];		
	}

	/** Returns the vocalization of the <STRONG>recomputed</STRONG> stem in arabic or <CODE>null</CODE> if there is no stem for the word.
	 * @return The vocalization
	 */
	public String getStemArabicVocalization() {
		if (stemsPOS.isEmpty()) return null;
		String[] vocalizations = new String[stemsPOS.size()];
		int i;
		String pos = null;
		String[] array;					
		for (i = 0 ; i < stemsPOS.size() ; i++) {
			pos = (String)stemsPOS.get(i);
			array = pos.split("/");				
			vocalizations[i] = AraMorph.arabizeWord(array[0]);
		}
		//if ((vocalizations.length > 1) && this.debug) System.err.println("More than one stem for " + vocalizations.toString());
		//return the first anyway :-(
		return vocalizations[0];		
	}
	
	/** Returns the vocalizations of the <STRONG>recomputed</STRONG> suffixes in the Buckwalter transliteration system or <CODE>null</CODE> if there ares no suffixes for the word.
	 * @return The vocalizations
	 */
	public String[] getSuffixesVocalizations() {
		if (suffixesPOS.isEmpty()) return null;
		String[] vocalizations = new String[suffixesPOS.size()];
		int i;
		String pos = null;
		String[] array;					
		for (i = 0 ; i < suffixesPOS.size() ; i++) {
			pos = (String)suffixesPOS.get(i);
			array = pos.split("/");				
			vocalizations[i] = array[0];
		}
		return vocalizations;
	}	
	
	/** Returns the vocalizations of the <STRONG>recomputed</STRONG> suffixes in arabic or <CODE>null</CODE> if there ares no suffixes for the word.
	 * @return The vocalizations
	 */
	public String[] getSuffixesArabicVocalizations() {
		if (suffixesPOS.isEmpty()) return null;
		String[] vocalizations = new String[suffixesPOS.size()];
		int i;
		String pos = null;
		String[] array;			
		for (i = 0 ; i < suffixesPOS.size() ; i++) {
			pos = (String)suffixesPOS.get(i);
			array = pos.split("/");				
			vocalizations[i] = AraMorph.arabizeWord(array[0]);
		}
		return vocalizations;
	}	

	
	/** Returns the vocalization of the word in the Buckwalter transliteration system.
	 * @return The vocalization
	 */
	public String getWordVocalization() {
		StringBuffer sb = new StringBuffer();		
		int i;
		String vocalisations[];
		vocalisations = this.getPrefixesVocalizations();
		if (vocalisations != null) {
			for (i = 0 ; i < vocalisations.length ; i++) {
				if (!"".equals(vocalisations[i])) sb.append(vocalisations[i]);
			}
		}
		if (this.getStemVocalization() != null) sb.append(this.getStemVocalization());		
		vocalisations = this.getSuffixesVocalizations();
		if (vocalisations != null) {			
			for (i = 0 ; i < vocalisations.length ; i++) {
				if (!"".equals(vocalisations[i])) sb.append(vocalisations[i]);
			}		
		}
		return sb.toString();
	}

	/** Returns the vocalization of the word in arabic.
	 * @return The vocalization
	 */
	public String getWordArabicVocalization() {
		StringBuffer sb = new StringBuffer();		
		int i;
		String vocalisations[];
		vocalisations = this.getPrefixesArabicVocalizations();
		if (vocalisations != null) {
			for (i = 0 ; i < vocalisations.length ; i++) {
				if (!"".equals(vocalisations[i])) sb.append(vocalisations[i]);
			}
		}
		if (this.getStemArabicVocalization() != null) sb.append(this.getStemArabicVocalization());		
		vocalisations = this.getSuffixesArabicVocalizations();
		if (vocalisations != null) {
			for (i = 0 ; i < vocalisations.length ; i++) {
				if (!"".equals(vocalisations[i])) sb.append(vocalisations[i]);
			}		
		}
		return sb.toString();
	}	
	
	/** Returns the morphology of the prefix.
	 * @return The morphology
	 */
	public String getPrefixMorphology() {
		return prefix.getMorphology();
	}

	/** Returns the morphology of the stem.
	 * @return The morphology
	 */
	public String getStemMorphology() {
		return stem.getMorphology();
	}

	/** Returns the morphology of the suffix.
	 * @return The morphology
	 */
	public String getSuffixMorphology() {
		return suffix.getMorphology();
	}
	
	/** Returns the morphology of the word.
	 * @return The morphology
	 */
	public String getWordMorphology() {
		StringBuffer sb = new StringBuffer();
		if (!"".equals(prefix.getMorphology()))
			sb.append("\t" + "prefix : " + prefix.getMorphology() + "\n");		
		if (!"".equals(stem.getMorphology()))
			sb.append("\t" + "stem : " + stem.getMorphology() + "\n");
		if (!"".equals(suffix.getMorphology()))
			sb.append("\t" + "suffix : " + suffix.getMorphology() + "\n");
		return sb.toString();
	}
	
	/** Returns the grammatical categories of the <STRONG>recomputed</STRONG> prefixes or <CODE>null</CODE> if there are no prefixes for the word.
	 * @return The grammatical categories
	 */
	public String[] getPrefixesPOS() {		
		if (prefixesPOS.isEmpty()) return null;
		String[] POS = new String[prefixesPOS.size()];
		int i, j;
		String pos = null;
		String[] array;			
		StringBuffer sb;
		for (i = 0 ; i < prefixesPOS.size() ; i++) {
			pos = (String)prefixesPOS.get(i);
			array = pos.split("/");	
			sb = new StringBuffer();
			for (j = 1 ; j < array.length ; j++) {
				if (j > 1) sb.append(" / ");
				sb.append(array[j]);
			}			
			POS[i] = sb.toString();
		}
		return POS;
	}

	/** Returns The vocalizations using the Buckwalter transliteration system of the <STRONG>recomputed</STRONG> prefixes and their grammatical categories or <CODE>null</CODE> if there are no prefixes for the word.
	 * @return The vocalizations and the grammatical categories
	 */
	public String[] getPrefixesLongPOS() {		
		if (prefixesPOS.isEmpty()) return null;
		String[] POS = new String[prefixesPOS.size()];
		int i, j;
		String pos = null;
		String[] array;			
		StringBuffer sb;
		for (i = 0 ; i < prefixesPOS.size() ; i++) {
			pos = (String)prefixesPOS.get(i);
			array = pos.split("/");	
			sb = new StringBuffer(array[0] + "\t");
			for (j = 1 ; j < array.length ; j++) {
				if (j > 1) sb.append(" / ");
				sb.append(array[j]);
			}			
			POS[i] = sb.toString();
		}
		return POS;
	}

	/** Returns The vocalizations in arabic of the <STRONG>recomputed</STRONG> prefixes and their grammatical categories or <CODE>null</CODE> if there is no stem for the word.
	 * @return The vocalizations and the grammatical categories.
	 */
	public String[] getPrefixesArabicLongPOS() {		
		if (prefixesPOS.isEmpty()) return null;
		String[] POS = new String[prefixesPOS.size()];
		int i, j;
		String pos = null;
		String[] array;			
		StringBuffer sb;
		for (i = 0 ; i < prefixesPOS.size() ; i++) {
			pos = (String)prefixesPOS.get(i);
			array = pos.split("/");	
			sb = new StringBuffer(AraMorph.arabizeWord(array[0]) + "\t");
			for (j = 1 ; j < array.length ; j++) {
				if (j > 1) sb.append(" / ");
				sb.append(array[j]);
			}			
			POS[i] = sb.toString();
		}
		return POS;
	}
	
	/** Returns the grammatical category of the <STRONG>recomputed</STRONG> stem.
	 * @return The grammatical category
	 */
	public String getStemPOS() {		
		if (stemsPOS.isEmpty()) return "NO_STEM";
		String[] POS = new String[stemsPOS.size()];
		int i, j;
		String pos = null;
		String[] array;			
		StringBuffer sb;
		for (i = 0 ; i < stemsPOS.size() ; i++) {
			pos = (String)stemsPOS.get(i);
			array = pos.split("/");	
			sb = new StringBuffer();
			for (j = 1 ; j < array.length ; j++) {
				if (j > 1) sb.append(" / ");
				sb.append(array[j]);
			}			
			POS[i] = sb.toString();
		}
//		if ((POS.length > 1) && this.debug) System.err.println("More than one stem for " + POS.toString());
//		if ("".equals(POS[0])) System.err.println("Empty POS for stem " + getStemLongPOS());
		//return the first anyway :-(
		return POS[0];				
	}

	/** Returns The vocalization using the Buckwalter transliteration system of the <STRONG>recomputed</STRONG> stem and its grammatical category  or <CODE>null</CODE> if there is no stem for the word.
	 * @return The vocalization and the grammatical category
	 */
	public String getStemLongPOS() {
		if (stemsPOS.isEmpty()) return null;
		String[] POS = new String[stemsPOS.size()];
		int i, j;
		String pos = null;
		String[] array;			
		StringBuffer sb;
		for (i = 0 ; i < stemsPOS.size() ; i++) {
			pos = (String)stemsPOS.get(i);
			array = pos.split("/");	
			sb = new StringBuffer(array[0] + "\t");
			for (j = 1 ; j < array.length ; j++) {
				if (j > 1) sb.append(" / ");
				sb.append(array[j]);
			}			
			POS[i] = sb.toString();
		}
		//if ((POS.length > 1) && this.debug) System.err.println("More than one stem for " + POS.toString());
		//return the first anyway :-(
		return POS[0];				
	}
	
	/** Returns The vocalization in arabic of the <STRONG>recomputed</STRONG> stem and its grammatical category or <CODE>null</CODE> if there is no stem for the word.
	 * @return The vocalization and the grammatical category
	 */
	public String getStemArabicLongPOS() {		
		if (stemsPOS.isEmpty()) return null;
		String[] POS = new String[stemsPOS.size()];
		int i, j;
		String pos = null;
		String[] array;			
		StringBuffer sb;
		for (i = 0 ; i < stemsPOS.size() ; i++) {
			pos = (String)stemsPOS.get(i);
			array = pos.split("/");	
			sb = new StringBuffer(AraMorph.arabizeWord(array[0]) + "\t");
			for (j = 1 ; j < array.length ; j++) {
				if (j > 1) sb.append(" / ");
				sb.append(array[j]);
			}			
			POS[i] = sb.toString();
		}
		//if ((POS.length > 1) && this.debug) System.err.println("More than one stem for " + POS.toString());
		//return the first anyway :-(
		return POS[0];				
	}	
	
	/** Returns The grammatical categories of the <STRONG>recomputed</STRONG> suffixes or  <CODE>null</CODE> if there are no suffixes for the word..
	 * @return The grammatical categories
	 */
	public String[] getSuffixesPOS() {
		//replaceFirst("^.*/","");		
		if (suffixesPOS.isEmpty()) return null;
		String[] POS = new String[suffixesPOS.size()];
		int i, j;
		String pos = null;
		String[] array;			
		StringBuffer sb;
		for (i = 0 ; i < suffixesPOS.size() ; i++) {
			pos = (String)suffixesPOS.get(i);
			array = pos.split("/");	
			sb = new StringBuffer();
			for (j = 1 ; j < array.length ; j++) {
				if (j > 1) sb.append(" / ");
				sb.append(array[j]);
			}			
			POS[i] = sb.toString();
		}
		return POS;
	}	

	/** Returns The vocalizations using the Buckwalter transliteration system of the <STRONG>recomputed</STRONG> suffixes and their grammatical categories or <CODE>null</CODE> if there is are no suffixes for the word.
	 * @return The vocalizations and the grammatical categories
	 */
	public String[] getSuffixesLongPOS() {
		//replaceFirst("^.*/","");		
		if (suffixesPOS.isEmpty()) return null;
		String[] POS = new String[suffixesPOS.size()];
		int i, j;
		String pos = null;
		String[] array;			
		StringBuffer sb;
		for (i = 0 ; i < suffixesPOS.size() ; i++) {
			pos = (String)suffixesPOS.get(i);
			array = pos.split("/");	
			sb = new StringBuffer(array[0] + "\t");
			for (j = 1 ; j < array.length ; j++) {
				if (j > 1) sb.append(" / ");
				sb.append(array[j]);
			}			
			POS[i] = sb.toString();
		}
		return POS;
	}	

	/** Returns The vocalization in arabic of the <STRONG>recomputed</STRONG> suffixes and their grammatical categories or <CODE>null</CODE> if there is are no suffixes for the word.
	 * @return The vocalizations and the grammatical categories
	 */
	public String[] getSuffixesArabicLongPOS() {
		//replaceFirst("^.*/","");		
		if (suffixesPOS.isEmpty()) return null;
		String[] POS = new String[suffixesPOS.size()];
		int i, j;
		String pos = null;
		String[] array;			
		StringBuffer sb;
		for (i = 0 ; i < suffixesPOS.size() ; i++) {
			pos = (String)suffixesPOS.get(i);
			array = pos.split("/");	
			sb = new StringBuffer(AraMorph.arabizeWord(array[0]) + "\t");
			for (j = 1 ; j < array.length ; j++) {
				if (j > 1) sb.append(" / ");
				sb.append(array[j]);
			}			
			POS[i] = sb.toString();
		}
		return POS;
	}	
	
	/** Returns The vocalization of the word in the Buckwalter transliteration system and its grammatical categories.
	 * @return The vocalization and the grammatical categories
	 */
	public String getWordLongPOS() {		
		String[] POS;
		int i = 0;
		StringBuffer sb = new StringBuffer();
		POS = this.getPrefixesLongPOS();	
		if (POS != null) {
			for (i = 0 ; i < POS.length ; i++) {				
				if (!"".equals(POS[i])) sb.append("\t" + "prefix : " + POS[i] + "\n");
			}		
		}
		if (this.getStemLongPOS() != null) sb.append("\t" + "stem : " + this.getStemLongPOS() + "\n");
		POS = this.getSuffixesLongPOS();	
		if (POS != null) {		
			for (i = 0 ; i < POS.length ; i++) {				
				if (!"".equals(POS[i])) sb.append("\t" + "suffix : " + POS[i] + "\n");
			}		
		}
		return sb.toString();		
	}
	
	/** Returns The vocalization of the word in atabic and its grammatical categories.
	 * @return The vocalization and the grammatical categories
	 */
	public String getWordArabicLongPOS() {		
		String[] POS;
		int i = 0;
		StringBuffer sb = new StringBuffer();
		POS = this.getPrefixesArabicLongPOS();	
		if (POS != null) {
			for (i = 0 ; i < POS.length ; i++) {				
				if (!"".equals(POS[i])) sb.append("\t" + "prefix : " + POS[i] + "\n");
			}		
		}
		if (this.getStemArabicLongPOS() == null) sb.append("\t" + "stem : " + this.getStemArabicLongPOS() + "\n");
		POS = this.getSuffixesArabicLongPOS();	
		if (POS != null) {		
			for (i = 0 ; i < POS.length ; i++) {				
				if (!"".equals(POS[i])) sb.append("\t" + "prefix : " + POS[i] + "\n");
			}		
		}
		return sb.toString();		
	}	
	
	/** Returns the english glosses of the prefixes.
	 * @return The glosses.
	 */	
	public String[] getPrefixesGlosses() { 
		if (prefixesGlosses.isEmpty()) return null;
		String[] glosses = new String[prefixesGlosses.size()];
		for (int i = 0 ; i < prefixesGlosses.size() ; i++) {
			glosses[i] = (String)prefixesGlosses.get(i);
		}
		return glosses; 
	}	

	/** Returns the english gloss of the stem.
	 * @return The gloss.
	 */
	public String getStemGloss() { 
		if (stemsGlosses.isEmpty()) return null;
		//if ((this.stemsGlosses.size() > 1) && this.debug) System.err.println("More than one gloss for " + stemsGlosses.toString());		
		//return the first anyway :-(
		return (String)stemsGlosses.get(0); 		
	}			
	
	/** Returns the english glosses of the suffixes.
	 * @return The glosses.
	 */
	public String[] getSuffixesGlosses() { 
		if (suffixesGlosses.isEmpty()) return null;
		String[] glosses = new String[suffixesGlosses.size()];
		for (int i = 0 ; i < suffixesGlosses.size() ; i++) {
			glosses[i] = (String)suffixesGlosses.get(i);
		}
		return glosses; 
	}	
	
	/** Returns the English glosses of the word.
	 * @return The glosses.
	 */
	public String getWordGlosses() {		
		int i = 0;
		String[] glosses = null;
		StringBuffer sb = new StringBuffer();		
		glosses = this.getPrefixesGlosses();
		if (glosses != null) {
			for (i = 0 ; i < glosses.length ; i++) {				
				if (!"".equals(glosses[i])) sb.append("\t" + "prefix : " + glosses[i] + "\n");		
			}
		}
		if (this.getStemGloss() != null) sb.append("\t" + "stem : " + this.getStemGloss() + "\n");		
		glosses = this.getSuffixesGlosses();
		if (glosses != null) {
			for (i = 0 ; i < glosses.length ; i++) {				
				if (!"".equals(glosses[i])) sb.append("\t" + "suffix : " + glosses[i] + "\n");		
			}
		}
		return sb.toString();	
	}
	
	/** Returns a string representation of how the word can be analyzed using the Buckwalter transliteration system for the vocalizations.
	 * @return The representation
	 */
	public String toString() {
		return new String(
		"\n" + "SOLUTION #" + cnt + "\n"
		+ "Lemma  : " + "\t" + getLemma() + "\n"
		+ "Vocalized as : " + "\t" + this.getWordVocalization() + "\n"
		+ "Morphology : " + "\n"
		+ this.getWordMorphology()
		+ "Grammatical category : " + "\n"
		+ this.getWordLongPOS()
		+ "Glossed as : " + "\n"
		+ this.getWordGlosses()
		);
	}
	
	/** Returns a string representation of how the word can be analyzed using arabic for the vocalizations..
	 * @return The representation
	 */
	public String toArabizedString() {
		return new String(
		"\n" + "SOLUTION #" + cnt + "\n"
		+ "Lemma  : " + "\t" + getLemma() + "\n"
		+ "Vocalized as : " + "\t" + this.getWordArabicVocalization() + "\n"
		+ "Morphology : " + "\n"
		+ this.getWordMorphology()
		+ "Grammatical category : " + "\n"
		+ this.getWordArabicLongPOS()
		+ "Glossed as : " + "\n"
		+ this.getWordGlosses()
		);
	}	
}



