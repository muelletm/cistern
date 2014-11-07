package marmot.tokenize.preprocess;

import marmot.util.LevenshteinLattice;

public class Pair {

	public Pair(String tokenized, String untokenized) {
		this.tokenized = tokenized;
		this.untokenized = untokenized;
		
		LevenshteinLattice lattice = new LevenshteinLattice(untokenized,
				tokenized);
		
		score = lattice.getDistance() / (double) (untokenized.length() + tokenized.length());
	}

	public String tokenized;
	@Override
	public String toString() {
		return "Pair [tokenized=" + tokenized + ", untokenized=" + untokenized
				+ "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((tokenized == null) ? 0 : tokenized.hashCode());
		result = prime * result
				+ ((untokenized == null) ? 0 : untokenized.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair other = (Pair) obj;
		if (tokenized == null) {
			if (other.tokenized != null)
				return false;
		} else if (!tokenized.equals(other.tokenized))
			return false;
		if (untokenized == null) {
			if (other.untokenized != null)
				return false;
		} else if (!untokenized.equals(other.untokenized))
			return false;
		return true;
	}

	public String untokenized;
	public double score;

}
