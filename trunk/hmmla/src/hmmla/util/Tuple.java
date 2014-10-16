// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.util;

public class Tuple<T, D extends Comparable<D>> implements
		Comparable<Tuple<T, D>> {

	public T x;
	public D y;

	public Tuple(T x, D y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int compareTo(Tuple<T, D> t) {
		return -y.compareTo(t.y);
	}

	@Override
	public String toString() {
		return String.valueOf(x) + ":" + String.valueOf(y);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (o instanceof Tuple<?, ?>) {
			Tuple<?, ?> tuple = (Tuple<?, ?>) o;
			return tuple.x.equals(this.x) && tuple.y.equals(this.y);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 3 * (x.hashCode() + y.hashCode());
	}
}
