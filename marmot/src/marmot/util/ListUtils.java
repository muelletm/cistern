package marmot.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ListUtils {

	public static <T> List<T> complement(List<List<T>> items, int i) {
		List<T> complement = new LinkedList<T>();
		for (int j = 0; j < items.size(); j++) {
			if (i != j) {
				complement.addAll(items.get(j));
			}
		}
		return complement;
	}

	public static <T> List<List<T>> chunk(List<T> items, int num_chunks) {

		List<List<T>> chunks = new ArrayList<List<T>>(num_chunks);

		int items_per_chunk = items.size() / num_chunks;
		if (items_per_chunk == 0)
			items_per_chunk = 1;
		
		List<T> chunk = new ArrayList<T>(items_per_chunk);
		for (T item : items) {
			chunk.add(item);
			
			if (chunk.size() == items_per_chunk) {
				chunks.add(chunk);
				if (chunks.size() < num_chunks)
					chunk = new LinkedList<T>();
			}
		}

		assert chunks.size() == num_chunks;
		return chunks;
	}

}
