package marmot.ising;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.javatuples.Pair;

public class DataReader {
	
	
	protected int numVariables;
	protected Map<String,Integer> atoms2Integer;
	protected Map<Integer,String> integer2Atoms;

	protected List<Datum> data;
	protected Set<Pair<Integer, Integer>> pairs;
	protected List<String> tagNames;
	
	protected List<Pair<Integer, Integer>> pairsLst;
	
	
	
	public DataReader() {
		this.numVariables = 0;
		this.atoms2Integer = new HashMap<String,Integer>();
		this.integer2Atoms = new HashMap<Integer,String>();

		this.data = new ArrayList<Datum>();
		this.pairs = new HashSet<Pair<Integer, Integer>>();
		this.tagNames = new ArrayList<String>();
		
	}
}
