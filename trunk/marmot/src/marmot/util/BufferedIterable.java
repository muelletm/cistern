// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class BufferedIterable<T> implements Iterable<T> {
	List<T> buffer_;
	
	public BufferedIterable(Iterable<T> iterable) {
		buffer_ = new LinkedList<T>();
		for (T item : iterable) {
			buffer_.add(item);
		}
	}
	
	@Override
	public Iterator<T> iterator() {
		return buffer_.iterator();
	}
	
	public List<T> getBuffer() {
		return buffer_;
	}
}
