package marmot.ising;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.javatuples.Pair;

public class DataReader {
	
	
	protected Map<String,Integer> atoms2Integer;
	protected List<Datum> data;
	protected Set<Pair<Integer, Integer>> pairs;
	
	
	public DataReader() {
		
		this.atoms2Integer = new HashMap<String,Integer>();
		this.data = new ArrayList<Datum>();
		this.pairs = new HashSet<Pair<Integer, Integer>>();
		
	}
}
