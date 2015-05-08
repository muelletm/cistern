package marmot.util;

import java.io.Serializable;

public interface FeatureTable extends Serializable {

	public int size();

	public int getFeatureIndex(Encoder encoder, boolean insert);

	public static class StaticMethods {

		public static FeatureTable create(boolean use_hash_table) {
			if (use_hash_table) {
				return new HashFeatureTable();
			}
			
			return new ExactFeatureTable();
		}

	}
	
}
