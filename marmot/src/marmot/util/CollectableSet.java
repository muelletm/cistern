// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.Set;


public class CollectableSet implements Collectable {

	private static final long serialVersionUID = 1L;
	private Set<Object> set_;
	
	public CollectableSet(Set<?> set) {
		set_ = new HashSet<>(set);
	}
	
	public CollectableSet(Object object) {
		set_ = new HashSet<>();
		set_.add(object);
	}
	
	@Override
	public void add(Collectable other) {
		if (! (other instanceof CollectableSet)) {
			throw new InvalidParameterException();
		}
		
		CollectableSet other_set = (CollectableSet) other;
		
		set_.addAll(other_set.set_);
	}
	
	public Set<Object> getValue() {
		return set_;
	}

	@Override
	public int sum() {
		return set_.size();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((set_ == null) ? 0 : set_.hashCode());
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
		CollectableSet other = (CollectableSet) obj;
		if (set_ == null) {
			if (other.set_ != null)
				return false;
		} else if (!set_.equals(other.set_))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return set_.toString();
	}

	@Override
	public Collectable copy() {
		return new CollectableSet(set_);
	}
	
	

}
