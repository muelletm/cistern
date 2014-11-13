package marmot.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerialUtils {

	public static void writeArray(ObjectOutputStream oos,
			double[] weights) throws IOException {
		
		int nonzero = 0;
		for (double w : weights) {
			if (Math.abs(w) > 1e-10) {
				nonzero += 1;
			}
		}
		
		System.err.println(nonzero + " / " + weights.length);
		
		oos.writeObject(weights);
	}
	
	public static void writeSparseArray(ObjectOutputStream oos,
			double[] weights, double eps) throws IOException {
		if (weights == null) {
			oos.writeInt(-1);
			return;
		}

		oos.writeInt(weights.length);

		int nonzero = 0;
		int last_i = -1;
		int max_diff = 0;
		for (int i = 0; i < weights.length; i++) {
			double w = weights[i];
			if (Math.abs(w) > eps) {
				nonzero += 1;

				int diff = i - last_i;
				if (diff > max_diff) {
					max_diff = diff;
				}

				last_i = i;
			}
		}

		if (nonzero > 0 ) {
			assert max_diff > 0;
		}
		
		oos.writeInt(nonzero);

		int num_bytes = 0;
		while (max_diff > 0) {
			max_diff /= 8;
			num_bytes += 1;
		}

		oos.writeInt(num_bytes);
		
		last_i = -1;
		for (int i = 0; i < weights.length; i++) {
			double w = weights[i];
			if (Math.abs(w) > eps) {
				int diff = i - last_i;
				writeInt(oos, diff, num_bytes);
				oos.writeDouble(w);
				last_i = i;
			}
		}
	}

	public static double[] readSparseArray(ObjectInputStream ois)
			throws IOException {
		int length = ois.readInt();

		if (length < 0) {
			return null;
		}

		double[] weights = new double[length];
		int nonzero = ois.readInt();
		int num_bytes = ois.readInt();

		int last_i = -1;
		for (int elem = 0; elem < nonzero; elem++) {
			int diff = readInt(ois, num_bytes);
			int i = last_i + diff;
			double w = ois.readDouble();
			weights[i] = w;
			last_i = i;
		}
		return weights;
	}

	private static void writeInt(ObjectOutputStream oos, int diff, int num_bytes) throws IOException {
		assert diff > 0;
		assert diff < Math.pow(8, num_bytes);
		int bytes_written = 0;
		while (diff > 0) {
			int digit = diff % 8;
			oos.write(digit);
			diff /= 8;
			bytes_written += 1;
		}
		while (bytes_written < num_bytes) {
			oos.write(0);
			bytes_written += 1;
		}
	}

	private static int readInt(ObjectInputStream ois, int num_bytes) throws IOException {
		int diff = 0;
		for (int i=0; i< num_bytes; i++) {
			int digit = ois.read();
			for (int k=0; k<i; k++){
				digit *= 8;
			}
			
			diff += digit;		
		}
		return diff;
	}

}
