// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import marmot.core.ArrayFloatFeatureVector;
import marmot.core.DenseArrayFloatFeatureVector;
import marmot.core.FloatFeatureVector;
import marmot.util.Converter;
import marmot.util.LineIterator;
import marmot.util.SymbolTable;

public class FloatHashDictionary implements Serializable {
	private static final long serialVersionUID = 1L;
	private Map<String, FloatFeatureVector> index_map_;
	private SymbolTable<String> column_table_;
	private MorphDictionaryOptions options_;

	protected void readSparseVector(LineIterator iterator) {

		while (iterator.hasNext()) {
			List<String> line = iterator.next();

			if (!line.isEmpty()) {
				String form = line.get(0);
				List<Integer> indexes = new LinkedList<Integer>();
				List<Double> values = new LinkedList<Double>();

				for (int i = 1; i < line.size(); i++) {

					String pair_string = line.get(i);
					String[] key_value = pair_string.split(":");
					Double value = null;

					if (key_value.length == 2) {

						try {

							value = Double.parseDouble(key_value[1]);

						} catch (NumberFormatException e) {

						}

					}

					if (value == null) {
						System.err.println("Skipping pair string: "
								+ pair_string);
					} else {
						String key = key_value[0];
						int index = column_table_.toIndex(key, true);

						assert (index >= 0);

						indexes.add(index);
						values.add(value);
					}

				}

				FloatFeatureVector vector = new ArrayFloatFeatureVector(
						Converter.toIntArray(indexes),
						Converter.toDoubleArray(values), 0);
				index_map_.put(form, vector);
			}
		}

	}

	public void readDenseVector(LineIterator iterator) {

		int dim = -1;

		while (iterator.hasNext()) {
			List<String> line = iterator.next();

			if (dim == -1 && line.size() == 2) {
				// Data is in word2vec text format.
				Logger logger = Logger.getLogger(getClass().getName());
				logger.info(String.format("Skipping possible file header: %s", line));
				continue;
			}		
			
			if (!line.isEmpty()) {
				
				if (dim < 0) {
					dim = line.size() - 1;
					for (int i=0; i< dim; i++) {
						column_table_.toIndex(Integer.toString(i), true);
					}			
				}
				
				String form = line.get(0);

				if (dim != line.size() - 1) {
					System.err
							.format("Dimension was expected to be %d, but is %d\n Line : %s\n",
									dim, line.size() - 1, line.toString());

					System.exit(1);
				}

				double[] weights = new double[dim];

				for (int i = 1; i < dim + 1; i++) {

					Double value = Double.parseDouble(line.get(i));
					weights[i - 1] = value;
				}

				DenseArrayFloatFeatureVector vector = new DenseArrayFloatFeatureVector(
						weights);
				index_map_.put(form, vector);
			}
		}
	}

	public void init(MorphDictionaryOptions options) {
		options_ = options;
		
		LineIterator iterator = new LineIterator(options_.getFilename());
		column_table_ = new SymbolTable<String>();
		index_map_ = new HashMap<String, FloatFeatureVector>();

		if (options_.getDense()) {
			readDenseVector(iterator);
		} else {
			readSparseVector(iterator);

			for (Map.Entry<String, FloatFeatureVector> entry : index_map_
					.entrySet()) {

				if (entry.getValue() instanceof ArrayFloatFeatureVector) {

					ArrayFloatFeatureVector vec = (ArrayFloatFeatureVector) entry
							.getValue();

					if (column_table_.size() == vec.getWeights().length) {
						entry.setValue(new DenseArrayFloatFeatureVector(vec
								.getWeights()));
					} else {
						double[] weights = new double[column_table_.size()];
						for (int index = 0; index < vec.getWeights().length; index++) {
							weights[vec.getFeatures()[index]] = vec
									.getWeights()[index];
						}
					}

				}
			}
		}
	}

	public FloatFeatureVector getVector(String form) {
		FloatFeatureVector v = index_map_.get(form);
		return v;
	}

	public int getDimension() {
		return column_table_.size();
	}

	public int numEntries() {
		return index_map_.size();
	}

}
