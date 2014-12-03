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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** An in-memory dictionary of prefixes, stems, suffixes and combinations fed with
 * resources avalaible in the classpath.
 * TODO : use a Lucene index ;-) or any other fast-access resources.
 *@author Pierrick Brihaye, 2003
 */
class DictionaryHandler {
	
	/** Dictionary of prefixes */
	private Map<String, List<DictionaryEntry>> prefixes;
	/** Dictionary of stems */
	private Map<String, List<DictionaryEntry>> stems;
	/** Dictionary of suffixes */
	private Map<String, List<DictionaryEntry>> suffixes;
	/** Compatibility table for prefixes-stems combinations.
	 * TODO : definitely not the best container
	 */
	private Set<String> hash_AB;
	/** Compatibility table for prefixes-suffixes combinations.
	 * TODO : definitely not the best container
	 */
	private Set<String> hash_AC;
	/** Compatibility table for stems-suffixes combinations.
	 * TODO : definitely not the best container
	 */
	private Set<String> hash_BC;
	
	/** Private constructor to avoid multiple instanciations. */
	public DictionaryHandler() {
		//System.out.println("Initializing in-memory dictionary handler...");
		// load 3 lexicons
		prefixes = loadDictionary("dictPrefixes", this.getClass().getResourceAsStream("dictionaries/dictPrefixes"));
		stems = loadDictionary("dictStems", this.getClass().getResourceAsStream("dictionaries/dictStems"));
		suffixes = loadDictionary("dictSuffixes", this.getClass().getResourceAsStream("dictionaries/dictSuffixes"));
		//load 3 compatibility tables
		hash_AB = loadCompatibilityTable("tableAB", this.getClass().getResourceAsStream("dictionaries/tableAB"));
		hash_AC = loadCompatibilityTable("tableAC", this.getClass().getResourceAsStream("dictionaries/tableAC"));
		hash_BC = loadCompatibilityTable("tableBC", this.getClass().getResourceAsStream("dictionaries/tableBC"));
		//System.out.println("... done.");
	};
		
	/** Returns an iterator on the solutions for the given prefix.
	 * @param translitered The prefix
	 * @return The iterator
	 */
	protected Collection<DictionaryEntry> getPrefixIterator(String translitered) {
		return prefixes.get(translitered);
	}
	
	/** Returns an iterator on the solutions for the given stem.
	 * @param translitered The stem
	 * @return The iterator
	 */
	protected Collection<DictionaryEntry> getStemIterator(String translitered) {
		return stems.get(translitered);
	}
	
	/** Returns an iterator on the solutions for the given suffix.
	 * @param translitered The suffix
	 * @return The iterator
	 */
	protected Collection<DictionaryEntry> getSuffixIterator(String translitered) {
		return suffixes.get(translitered);
	}
	
	/** Whether or not the prefix/stem combination is possible.
	 * @param AB The prefix and stem combination.
	 * @return The result
	 */
	protected boolean hasAB(String A, String B) {
		return hash_AB.contains(A + " " + B);
	}
	
	/** Whether or not the prefix/suffix combination is possible.
	 * @param AC The prefix and suffix combination.
	 * @return The result
	 */
	protected boolean hasAC(String A, String C) {
		return hash_AC.contains(A + " " + C);
	}
	
	/** Whether or not the stem/suffix combination is possible.
	 * @param BC The stem and suffix combination.
	 * @return The result
	 */
	protected boolean hasBC(String B, String C) {
		return hash_BC.contains(B + " " + C);
	}
	
	/** Loads a dictionary into a <CODE>Set</CODE> where the <PRE>key</PRE> is entry and its <PRE>value</PRE> is a
	 * <CODE>List</CODE> (each entry can have multiple values)
	 * @param set The set
	 * @param name A human-readable name
	 * @param is The stream
	 * @throws RuntimeException If a problem occurs when reading the dictionary
	 */
	private Map<String, List<DictionaryEntry>> loadDictionary(String name, InputStream is) throws RuntimeException { //TODO : should be static
		Map<String, List<DictionaryEntry>> set = new HashMap<String, List<DictionaryEntry>>();
		Set<String> lemmas = new HashSet<String>();
		String lemmaID = "";
		try {
			LineNumberReader IN = new LineNumberReader(new InputStreamReader(is,"ISO8859_1"));
			String line = null;
			while ((line = IN.readLine()) != null) {
				// new lemma
				if (line.startsWith(";; ")) {
					lemmaID = line.substring(3);
					// lemmaID's must be unique
					if (lemmas.contains(lemmaID))
						throw new RuntimeException("Lemma " + lemmaID + "in " + name + " (line " + IN.getLineNumber() + ") isn't unique");
					lemmas.add(lemmaID);
				}
				// comment
				else if (line.startsWith(";")) {}
				else {
					String split[] = line.split("\t",-1); //-1 to avoid triming of trail values
					
					//a little error-checking won't hurt :
					if (split.length != 4) {
						throw new RuntimeException("Entry in " + name + " (line " + IN.getLineNumber() + ") doesn't have 4 fields (3 tabs)");
					}
					String entry = split[0]; // get the entry for use as key
					String vocalization = split[1];
					String morphology = split[2];
					String glossPOS = split[3];
					
					String gloss;
					String POS;
					
					Pattern p;
					Matcher m;
					
					// two ways to get the POS info:
					// (1) explicitly, by extracting it from the gloss field:
					p = Pattern.compile(".*" + "<pos>(.+?)</pos>" + ".*");
					m = p.matcher(glossPOS);
					if (m.matches()) {
						POS = m.group(1); //extract POS from glossPOS
						gloss = glossPOS; //we clean up the gloss later (see below)
					}
					// (2) by deduction: use the morphology (and sometimes the voc and gloss) to deduce the appropriate POS
					else {
						// we need the gloss to guess proper names
						gloss = glossPOS; 
						// null prefix or suffix
						if (morphology.matches("^(Pref-0|Suff-0)$")) { 
							POS = "";
						}
						else if (morphology.matches("^F" + ".*")) {
							POS = vocalization + "/FUNC_WORD";
						}
						else if (morphology.matches("^IV" + ".*")) {
							POS = vocalization + "/VERB_IMPERFECT";
						}
						else if (morphology.matches("^PV" + ".*")) {
							POS = vocalization + "/VERB_PERFECT";
						}
						else if (morphology.matches("^CV" + ".*")) {
							POS = vocalization + "/VERB_IMPERATIVE";
						}						
						else if (morphology.matches("^N" + ".*")) {
							// educated guess (99% correct)
							if (gloss.matches("^[A-Z]" + ".*")) {
								POS = vocalization + "/NOUN_PROP";
							}
							// (was NOUN_ADJ: some of these are really ADJ's and need to be tagged manually)
							else if (vocalization.matches(".*" + "iy~$")) { 
								POS = vocalization + "/NOUN";
							}
							else 
								POS = vocalization + "/NOUN";
						}
						else {
							throw new RuntimeException("No POS can be deduced in " + name + " (line " + IN.getLineNumber() + ")");
						}
					}
					
					// clean up the gloss: remove POS info and extra space, and convert upper-ASCII  to lower (it doesn't convert well to UTF-8)
					gloss = gloss.replaceFirst("<pos>.+?</pos>","");
					gloss = gloss.trim();
					//TODO : we definitely need a translate() method in the java packages !
					gloss = gloss.replaceAll(";","/"); //TODO : is it necessary ?
					gloss = gloss.replaceAll("�","A");
					gloss = gloss.replaceAll("�","A");
					gloss = gloss.replaceAll("�","A");
					gloss = gloss.replaceAll("�","A");
					gloss = gloss.replaceAll("�","A");
					gloss = gloss.replaceAll("�","A");
					gloss = gloss.replaceAll("�","C");
					gloss = gloss.replaceAll("�","E");
					gloss = gloss.replaceAll("�","E");
					gloss = gloss.replaceAll("�","E");
					gloss = gloss.replaceAll("�","E");
					gloss = gloss.replaceAll("�","I");
					gloss = gloss.replaceAll("�","I");
					gloss = gloss.replaceAll("�","I");
					gloss = gloss.replaceAll("�","I");
					gloss = gloss.replaceAll("�","N");
					gloss = gloss.replaceAll("�","O");
					gloss = gloss.replaceAll("�","O");
					gloss = gloss.replaceAll("�","O");
					gloss = gloss.replaceAll("�","O");
					gloss = gloss.replaceAll("�","O");
					gloss = gloss.replaceAll("�","U");
					gloss = gloss.replaceAll("�","U");
					gloss = gloss.replaceAll("�","U");
					gloss = gloss.replaceAll("�","U");
					gloss = gloss.replaceAll("�","a");
					gloss = gloss.replaceAll("�","a");
					gloss = gloss.replaceAll("�","a");
					gloss = gloss.replaceAll("�","a");
					gloss = gloss.replaceAll("�","a");
					gloss = gloss.replaceAll("�","a");
					gloss = gloss.replaceAll("�","c");
					gloss = gloss.replaceAll("�","e");
					gloss = gloss.replaceAll("�","e");
					gloss = gloss.replaceAll("�","e");
					gloss = gloss.replaceAll("�","e");
					gloss = gloss.replaceAll("�","i");
					gloss = gloss.replaceAll("�","i");
					gloss = gloss.replaceAll("�","i");
					gloss = gloss.replaceAll("�","i");
					gloss = gloss.replaceAll("�","n");
					gloss = gloss.replaceAll("�","o");
					gloss = gloss.replaceAll("�","o");
					gloss = gloss.replaceAll("�","o");
					gloss = gloss.replaceAll("�","o");
					gloss = gloss.replaceAll("�","o");
					gloss = gloss.replaceAll("�","u");
					gloss = gloss.replaceAll("�","u");
					gloss = gloss.replaceAll("�","u");
					gloss = gloss.replaceAll("�","u");
					gloss = gloss.replaceAll("�","AE");
					gloss = gloss.replaceAll("�","Sh");
					gloss = gloss.replaceAll("�","Zh");
					gloss = gloss.replaceAll("�","ss");
					gloss = gloss.replaceAll("�","ae");
					gloss = gloss.replaceAll("�","sh");
					gloss = gloss.replaceAll("�","zh");
					// note that although we read 4 fields from the dict we now save 5 fields in the hash table
					// because the info in last field, glossPOS, was split into two: gloss and POS
					DictionaryEntry de = new DictionaryEntry(entry, lemmaID, vocalization, morphology, gloss, POS);
					
					List<DictionaryEntry> list = set.get(entry);
					if (list == null) {
						list = new LinkedList<DictionaryEntry>();
						set.put(entry, list);
					}
					
					list.add(de);
				}
			}
			IN.close();
		}
		catch (IOException e) {
			throw new RuntimeException("Can not open : " + name);
		}
		
		return set;
	}
	
	/** Loads a compatibility table into a <CODE>Set</CODE>.
	 * @param set The set
	 * @param name A human-readable name
	 * @param is The stream
	 * @throws RuntimeException If a problem occurs when reading the compatibility table
	 */
	private Set<String> loadCompatibilityTable(String name, InputStream is) throws RuntimeException {
		Set<String> set = new HashSet<String>();
		try {
			LineNumberReader IN = new LineNumberReader(new InputStreamReader(is,"ISO8859_1"));
			String line = null;
			while ((line = IN.readLine()) != null) {
				if (!line.startsWith(";")) { //Ignore comments
					line = line.trim();
					line = line.replaceAll("\\s+", " ");
					set.add(line);
				}
			}
			IN.close();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		return set;
	}
	
	
}


