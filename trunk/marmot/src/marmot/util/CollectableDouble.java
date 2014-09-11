// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

import java.security.InvalidParameterException;


public class CollectableDouble implements Collectable {

	private static final long serialVersionUID = 1L;
	double d_;
	
	public CollectableDouble(double d) {
		d_ = d;
	}
	
	@Override
	public void add(Collectable other) {
		if (! (other instanceof CollectableDouble)) {
			throw new InvalidParameterException();
		}
		
		CollectableDouble other_integer = (CollectableDouble) other;
		
		d_ += other_integer.getValue();
	}
	
	public double getValue() {
		return d_;
	}

	@Override
	public int sum() {
		return new Double(d_).intValue();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(d_);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		CollectableDouble other = (CollectableDouble) obj;
		if (Double.doubleToLongBits(d_) != Double.doubleToLongBits(other.d_))
			return false;
		return true;
	}

	@Override
	public Collectable copy() {
		return new CollectableDouble(d_);
	}

}
