package marmot.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

public class HashFeatureTable implements FeatureTable {

	private static final long serialVersionUID = 1L;
	private Set<Integer> set_;
	
	public HashFeatureTable() {
		set_ = new HashSet<>();
	}
	
	@Override
	public int size() {
		return set_.size();
	}

	@Override
	public int getFeatureIndex(Encoder encoder, boolean insert) {
		int hash_code = encoder.hashCode();
		
		if (set_.contains(hash_code)) {
			return hash_code;
		}
		
		if (insert) {
			set_.add(hash_code);
			return hash_code;
		}
		
		return -1;
	}
	
	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
		oos.writeInt(set_.size());
		for (Integer i : set_) {
			oos.writeInt(i);
		}
	}

	private void readObject(ObjectInputStream ois)
			throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		int size = ois.readInt();
		set_ = new HashSet<>(size);
		for (int number = 0; number < size; number++) {
			Integer i = ois.readInt();
			set_.add(i);
		}
	}
}
