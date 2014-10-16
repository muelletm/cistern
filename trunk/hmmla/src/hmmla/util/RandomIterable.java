// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

public class RandomIterable<T> implements Iterable<T> {
	Iterable<T> iterable_;
	double prob_;
	Random rng_;
	
	public RandomIterable(Iterable<T> iterable, double prob, Random rng) {
		iterable_ = iterable;
		prob_ = prob;
		rng_ = rng;
	}
	
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			Iterator<T> iterator_ = iterable_.iterator();
			T next_ = null;
			boolean has_next_ = false;
			
			private boolean test() {
				return rng_.nextDouble() <= prob_;
			}
			
			private void SetNext() {
				assert (!has_next_);
				while (iterator_.hasNext()) {
					T current = iterator_.next();
					if (test()) {
						next_ = current;
						has_next_ = true;
						return;
					}
				}
				throw new NoSuchElementException();
			}
			
			@Override
			public boolean hasNext() {
				if (has_next_) {
					return true;
				}
				try {
					SetNext();
				} catch (NoSuchElementException e) {
					return false;
				}
				return true;
			}

			@Override
			public T next() {
				if (!has_next_) {
					SetNext();
				}
				has_next_ = false;
				return next_;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}
