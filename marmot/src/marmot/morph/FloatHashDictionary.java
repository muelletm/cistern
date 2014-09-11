// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marmot.core.ArrayFloatFeatureVector;
import marmot.core.DenseArrayFloatFeatureVector;
import marmot.core.FloatFeatureVector;
import marmot.util.Converter;
import marmot.util.LineIterator;
import marmot.util.SymbolTable;

public class FloatHashDictionary implements Serializable {
	private static final long serialVersionUID = 1L;
	private Map<String, FloatFeatureVector> index_map_;
	private SymbolTable<String> table_;
	private MorphDictionaryOptions options_;

	protected void readSparseVector(LineIterator iterator) {

		while (iterator.hasNext()) {
			List<String> line = iterator.next();

			if (!line.isEmpty()) {
				String form = line.get(0);
				List<Integer> indexes = new LinkedList<>();
				List<Double> values = new LinkedList<>();

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
						int index = table_.toIndex(key, true);

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

		if (!iterator.hasNext()) {
			throw new RuntimeException("File is empty!");
		}

		List<String> first_line = iterator.next();

		if (first_line.size() != 2) {
			throw new RuntimeException("File is empty!");
		}

		int num_forms = Integer.parseInt(first_line.get(0));
		int dim = Integer.parseInt(first_line.get(1));

		System.err.format("Num words: %d, Dim: %d\n", num_forms, dim);
		
		for (int i=0; i< dim; i++) {
			table_.toIndex(Integer.toString(i), true);
		}

		while (iterator.hasNext()) {
			List<String> line = iterator.next();

			if (!line.isEmpty()) {
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
		LineIterator iterator = new LineIterator(options.getFilename());
		table_ = new SymbolTable<>();
		index_map_ = new HashMap<>();

		if (options_.getDense()) {
			readDenseVector(iterator);
		} else {
			readSparseVector(iterator);

			int dense = 0;

			for (Map.Entry<String, FloatFeatureVector> entry : index_map_
					.entrySet()) {

				if (entry.getValue() instanceof ArrayFloatFeatureVector) {

					ArrayFloatFeatureVector vec = (ArrayFloatFeatureVector) entry
							.getValue();

					if (table_.size() == vec.getWeights().length) {
						entry.setValue(new DenseArrayFloatFeatureVector(vec
								.getWeights()));
						dense++;
					} else {

						double[] weights = new double[table_.size()];
						for (int index = 0; index < vec.getWeights().length; index++) {
							weights[vec.getFeatures()[index]] = vec
									.getWeights()[index];
						}

						// vec.setDim(table_.size());
					}

				}

			}

			System.err.format("Dense rate: %d / %d = %g\n", dense,
					index_map_.size(), dense * 100. / index_map_.size());
		}

		System.err.println(table_);
	}

	public FloatFeatureVector getVector(String form) {
		FloatFeatureVector v = index_map_.get(form);
		return v;
	}

	public int size() {
		return table_.size();
	}

	public int[] getOffsets() {
		return options_.getIndexes();
	}

}
